package cn.waynezw.config;

import cn.waynezw.common.DataSourceKey;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;

//@EnableTransactionManagement
//@Configuration
public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {

    @Autowired
    @Qualifier(value = "writeDataSource")
    private DataSource dataSource;

    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManagers() {
        logger.info("-------------------- transactionManager init ---------------------");
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 只读事务到读库，读写事务到写库
     * @param transaction
     * @param definition
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {

        //设置数据源
        boolean readOnly = definition.isReadOnly();
        if(readOnly) {
            DynamicDataSourceHolder.setDataSourceKey(DataSourceKey.READ);
        } else {
            DynamicDataSourceHolder.setDataSourceKey(DataSourceKey.WRITE);
        }
        super.doBegin(transaction, definition);
    }

    /**
     * 清理本地线程的数据源
     * @param transaction
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DynamicDataSourceHolder.clearDataSourceKey();
    }

}