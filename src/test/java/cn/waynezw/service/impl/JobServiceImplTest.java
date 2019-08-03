package cn.waynezw.service.impl;

import cn.waynezw.model.Job;
import cn.waynezw.service.JobService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
//@Import(JobServiceImpl.class)
public class JobServiceImplTest {
    @Autowired
    private JobService jobService;

    @Test
    public void testFindById() {
        Job job = jobService.findById(Long.valueOf(1));
        Assert.assertNotNull(job.getId());
    }

    @Test
    public void testSave() {
        Job job = new Job();
        job.setJobName("job-name-3");
        Job save = jobService.save(job);
        Assert.assertTrue(!StringUtils.isEmpty(save.getId()));

    }

}