package cn.waynezw.config;

import cn.waynezw.common.DataSourceKey;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源实现读写分离
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceKey dataSourceKey = DynamicDataSourceHolder.getDataSourceKey();
        if (dataSourceKey == null || dataSourceKey == DataSourceKey.WRITE) {
            return DataSourceKey.WRITE.name();
        }
        return DataSourceKey.READ.name();
    }

}
