package cn.waynezw.mapper;

import cn.waynezw.common.DataSource;
import cn.waynezw.common.DataSourceKey;
import cn.waynezw.model.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobMapper {
    void save(Job job);
    @DataSource(value = DataSourceKey.WRITE)
    Job findById(Long id);

    List<Job> findAll();

    List<Job> findAliveJobs();

    void update(Job job);

    void delete(Long id);

    @DataSource(value = DataSourceKey.WRITE)
    void updateStatusById(@Param("id") Long id, @Param("status") int status);
}
