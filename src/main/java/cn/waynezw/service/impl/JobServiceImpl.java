package cn.waynezw.service.impl;

import cn.waynezw.mapper.JobMapper;
import cn.waynezw.model.Job;
import cn.waynezw.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobMapper jobMapper;

    @Override
    public Job save(Job job) {
        job.setStatus(0);
        jobMapper.save(job);
        return job;
    }

    @Override
    public void delete(Long id) {
        jobMapper.delete(id);
    }

    @Override
    public Job update(Job job) {
        jobMapper.update(job);
        return jobMapper.findById(job.getId());
    }

    @Override
    public List<Job> findAll() {
        return jobMapper.findAll();
    }

    @Override
    public List<Job> findAliveJobs() {
        return jobMapper.findAliveJobs();
    }

    @Override
    public Job findById(Long id) {
        return jobMapper.findById(id);
    }

    @Override
    public Job updateStatusById(Long id, int status) {
        jobMapper.updateStatusById(id, status);
        return jobMapper.findById(id);
    }
}
