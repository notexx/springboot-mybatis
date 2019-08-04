package cn.waynezw.config;

import cn.waynezw.common.DataSourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
//@Configuration
public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceTransactionManager.class);


    public DynamicDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 只读事务到读库，读写事务到写库
     *
     * @param transaction
     * @param definition
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        //设置数据源
        boolean readOnly = definition.isReadOnly();
        logger.info("doBegin : [{}]", readOnly);
        if (readOnly) {
            DynamicDataSourceHolder.setDataSourceKey(DataSourceKey.READ);
        } else {
            DynamicDataSourceHolder.setDataSourceKey(DataSourceKey.WRITE);
        }
        super.doBegin(transaction, definition);
    }

    /**
     * 清理本地线程的数据源
     *
     * @param transaction
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DynamicDataSourceHolder.clearDataSourceKey();
    }
}