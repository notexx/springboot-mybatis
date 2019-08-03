# springboot-mybatis
SpringBoot+MyBatis实现动态数据源切换


# master分支
通过Spring AOP在业务层实现读写分离，在DAO层调用前定义切面，利用Spring的AbstractRoutingDataSource解决多数据源的问题，实现动态选择数据源

# planA分支
通过spring的AbstractRoutingDataSource和mybatis Plugin拦截器实现
