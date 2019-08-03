package cn.waynezw.config;

import cn.waynezw.common.DataSourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DynamicDataSourceHolder {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceHolder.class);

    /**
     * 用于在切换数据源时保证不会被其他线程修改
     */
    private static Lock lock = new ReentrantLock();

    /**
     * 用于轮循的计数器
     */
    private static int counter = 0;

    /**
     * Maintain variable for every thread, to avoid effect other thread
     */
    private static final ThreadLocal<DataSourceKey> holder = new ThreadLocal<>();


    /**
     * All DataSource List
     */
    public static List<Object> dataSourceKeys = new ArrayList<>();

    /**
     * The constant readDataSourceKeys.
     */
    public static List<Object> readDataSourceKeys = new ArrayList<>();
    static {
        readDataSourceKeys.add("READ");
    }

    /**
     * To switch DataSource
     *
     * @param key the key
     */
    public static void setDataSourceKey(DataSourceKey key) {
        holder.set(key);
    }

    /**
     * 使用主库 也就是 WRITE
     */
    public static void useWriteDataSource() {
        holder.set(DataSourceKey.WRITE);
    }

    /**
     * 当使用只读数据源时通过轮循方式选择要使用的数据源
     */
    public static void useReadDataSource() {
        lock.lock();
        try {
            int datasourceKeyIndex = counter % readDataSourceKeys.size();
            Object o = readDataSourceKeys.get(datasourceKeyIndex);
            holder.set(DataSourceKey.valueOf(o.toString()));
            counter++;
        } catch (Exception e) {
            logger.error("切换数据源错误： {}", e.getMessage());
            useWriteDataSource();
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get current DataSource
     * @return data source key
     */
    public static DataSourceKey getDataSourceKey() {
        return holder.get();
    }

    /**
     * To set DataSource as default
     */
    public static void clearDataSourceKey() {
        holder.remove();
    }

    /**
     * Check if give DataSource is in current DataSource list
     *
     * @param key the key
     * @return boolean boolean
     */
    public static boolean containDataSourceKey(String key) {
        return dataSourceKeys.contains(key);
    }
}
