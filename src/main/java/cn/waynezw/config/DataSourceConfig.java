package cn.waynezw.config;

import cn.waynezw.common.DataSourceKey;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.write")
    public DataSource writeDataSource() {
        DruidDataSource writeDataSource = new DruidDataSource();
        return writeDataSource;
    }

    @Bean
    @ConfigurationProperties("spring.datasource.read")
    public DataSource readDataSource() {
        DruidDataSource readDataSource = new DruidDataSource();
        return readDataSource;
    }

    @Bean
    public DataSource myRoutingDataSource(@Qualifier("writeDataSource") DataSource writeDataSource,
                                          @Qualifier("readDataSource") DataSource readDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceKey.WRITE, writeDataSource);
        targetDataSources.put(DataSourceKey.READ, readDataSource);
        DynamicRoutingDataSource myRoutingDataSource = new DynamicRoutingDataSource();
        myRoutingDataSource.setWriteDataSource(writeDataSource);
        myRoutingDataSource.setTargetDataSources(targetDataSources);
        return myRoutingDataSource;
    }

    @Bean(name = "sqlSessionFactory")
    @Autowired
    public SqlSessionFactory sqlSessionFactory(DataSource myRoutingDataSource) throws IOException {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(myRoutingDataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        bean.setTypeAliasesPackage("cn.waynezw.mapper");
        bean.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));
        try {

            SqlSessionFactory session = bean.getObject();
            return session;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean(name = "sqlSessionTemplate")
    @Autowired
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }



    @Bean
    public MapperScannerConfigurer scannerConfigurer(){
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        configurer.setSqlSessionTemplateBeanName("sqlSessionTemplate");
        configurer.setBasePackage("cn.waynezw.mapper");
        configurer.setMarkerInterface(Mapper.class);
        return configurer;
    }

}