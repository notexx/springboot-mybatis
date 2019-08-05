package cn.waynezw.config;

import cn.waynezw.common.DataSource;
import cn.waynezw.common.DataSourceKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {
                MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {
                MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class})})
@Configuration
@Component
public class DynamicPlugin implements Interceptor {

    protected static final Logger logger = LoggerFactory.getLogger(DynamicPlugin.class);

    private static final String REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";

    private static final Map<String, DataSourceKey> cacheMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (!synchronizationActive) {
            Object[] objects = invocation.getArgs();
            MappedStatement ms = (MappedStatement) objects[0];
            String id = ms.getId();
            int i = id.lastIndexOf(".");
            String substring = id.substring(0, i);
            Class<?> aClass = Class.forName(substring);
            Method[] methods = aClass.getMethods();
            DataSourceKey dataSourceKey = null;

            for (int j = 0; j < methods.length; j++) {
                if (Objects.equals(methods[j].getName(), id.substring(i+1))) {
                    DataSource annotation = methods[j].getAnnotation(DataSource.class);
                    if (annotation != null) {
                        dataSourceKey = annotation.value();
                        DynamicDataSourceHolder.setDataSourceKey(dataSourceKey);
                        logger.info("设置方法[{}] 使用 [{}] 数据源, SqlCommandType [{}]..", ms.getId(), dataSourceKey.name(), ms.getSqlCommandType().name());
                        return invocation.proceed();
                    }
                    break;
                }
            }

            if ((dataSourceKey = cacheMap.get(ms.getId())) == null) {
                //读方法
                if (ms.getSqlCommandType().equals(SqlCommandType.SELECT)) {
                    //!selectKey 为自增id查询主键(SELECT LAST_INSERT_ID() )方法，使用主库
                    if (ms.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
                        dataSourceKey = DataSourceKey.WRITE;
                    } else {
                        BoundSql boundSql = ms.getSqlSource().getBoundSql(objects[1]);
                        String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("[\\t\\n\\r]", " ");
                        if (sql.matches(REGEX)) {
                            dataSourceKey = DataSourceKey.WRITE;
                        } else {
                            dataSourceKey = DataSourceKey.READ;
                        }
                    }
                } else {
                    dataSourceKey = DataSourceKey.WRITE;
                }
                logger.info("设置方法[{}] 使用 [{}] 数据源, SqlCommandType [{}]..", ms.getId(), dataSourceKey.name(), ms.getSqlCommandType().name());
                cacheMap.put(ms.getId(), dataSourceKey);
            }
            DynamicDataSourceHolder.setDataSourceKey(dataSourceKey);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        //
    }
}