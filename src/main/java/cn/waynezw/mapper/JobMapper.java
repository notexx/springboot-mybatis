package cn.waynezw.mapper;

import cn.waynezw.model.Job;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JobMapper {
    void save(Job job);

    Job findById(Long id);

    List<Job> findAll();

    List<Job> findAliveJobs();

    void update(Job job);

    void delete(Long id);

    void updateStatusById(Long id, int status);
}
