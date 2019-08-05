package cn.waynezw.config;

import cn.waynezw.common.DataSource;
import cn.waynezw.common.DataSourceKey;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class DynamicDataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    private final String[] QUERY_PREFIX = {"select", "get", "find"};
    private static AtomicInteger countRead = new AtomicInteger(0);
    private static AtomicInteger countWrite = new AtomicInteger(0);

    @Pointcut("execution( * cn.waynezw.mapper.*.*(..))")
    public void daoAspect() {
    }


    @Before("daoAspect()")
    public void switchDataSource(JoinPoint point) {
        Boolean isQueryMethod = isQueryMethod(point);
        if (isQueryMethod) {
            DynamicDataSourceHolder.useReadDataSource();
            logger.info("拦截器[切换]数据源[{}] 方法 [{}], READ count[{}]",
                    DynamicDataSourceHolder.getDataSourceKey(), point.getSignature(), countRead.incrementAndGet());
        } else {
            DynamicDataSourceHolder.useWriteDataSource();
            logger.info("拦截器[切换]数据源 [{}] 方法 [{}], WRITE count[{}]",
                    DynamicDataSourceHolder.getDataSourceKey(), point.getSignature(), countWrite.incrementAndGet());
        }
    }

    @After("daoAspect()")
    public void restoreDataSource(JoinPoint point) {
        DynamicDataSourceHolder.clearDataSourceKey();
//        logger.info("拦截器[恢复]数据源 [{}] 方法 [{}]", DynamicDataSourceHolder.getDataSourceKey(), point.getSignature());
    }

    /**
     * 配置了注解，优先走注解，没有注解走特定的字符拦截 "select", "get", "find"
     *
     * @param point
     * @return
     */
    private Boolean isQueryMethod(JoinPoint point) {
        MethodSignature sign = (MethodSignature) point.getSignature();
        Method method = sign.getMethod();
        //获取方法上的注解
        DataSource annotation = method.getAnnotation(DataSource.class);
        if (annotation != null && Objects.equals(annotation.value(), DataSourceKey.READ)) {
            return true;
        }
        if (annotation != null && Objects.equals(annotation.value(), DataSourceKey.WRITE)) {
            return false;
        }
        String methodName = point.getSignature().getName();
        for (String prefix : QUERY_PREFIX) {
            if (methodName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}