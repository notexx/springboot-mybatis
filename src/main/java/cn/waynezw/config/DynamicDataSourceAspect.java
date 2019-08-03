package cn.waynezw.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicDataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    private final String[] QUERY_PREFIX = {"select", "get", "find"};

    @Pointcut("execution( * cn.waynezw.mapper.*.*(..))")
    public void daoAspect() {
    }

    @Before("daoAspect()")
    public void switchDataSource(JoinPoint point) {
        Boolean isQueryMethod = isQueryMethod(point.getSignature().getName());
        if (isQueryMethod) {
            DynamicDataSourceHolder.useReadDataSource();
            logger.info("拦截器切换数据源[{}] 方法 [{}]",
                    DynamicDataSourceHolder.getDataSourceKey(), point.getSignature());
        } else {
            DynamicDataSourceHolder.useWriteDataSource();
            logger.info("拦截器切换数据源 [{}] 方法 [{}]",
                    DynamicDataSourceHolder.getDataSourceKey(), point.getSignature());
        }
    }

    /*@After("daoAspect()")
    public void restoreDataSource(JoinPoint point) {
        DynamicDataSourceHolder.clearDataSourceKey();
        logger.info("拦截器恢复数据源 [{}] 方法 [{}]",
                DynamicDataSourceHolder.getDataSourceKey(), point.getSignature());
    }*/

    private Boolean isQueryMethod(String methodName) {
        for (String prefix : QUERY_PREFIX) {
            if (methodName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}