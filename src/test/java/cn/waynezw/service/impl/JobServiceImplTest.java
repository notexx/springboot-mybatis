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

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
//@Import(JobServiceImpl.class)
public class JobServiceImplTest {
    @Autowired
    private JobService jobService;

    @Test
    public void testFindById() {
        Job job = jobService.findById(Long.valueOf(5));
        System.out.println(job);
        Assert.assertNotNull(job.getId());
    }
    @Test
    public void testFindAll() {
        List<Job> all = jobService.findAll();
        all.stream().forEach((job) -> System.out.println(job));
    }
    @Test
    public void testUpdateStatusById() {
        Job job = jobService.updateStatusById(Long.valueOf(1), 2);
        Assert.assertNotNull(job.getId());
    }
    @Test
    public void testSave() {
        Job job = new Job();
        job.setJobName("job-name-3");
        Job save = jobService.save(job);
        Assert.assertTrue(!StringUtils.isEmpty(save.getId()));

    }
    @Test
    public void testMutilThread() {
        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(100);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 15, 1,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executor.execute(() -> {
                try {
                    if (finalI % 2 == 0) {
                        Job save = jobService.updateStatusById(Long.valueOf(1), finalI);
                        Assert.assertTrue(!StringUtils.isEmpty(save.getId()));
                    }  else {
                        Job save = jobService.updateStatusById(Long.valueOf(1), finalI);
                        Assert.assertTrue(!StringUtils.isEmpty(save.getId()));
                    }
                } catch (RuntimeException e) {
                    System.out.println("RuntimeException...." + finalI);
                }
                latch.countDown();
            });
        }
        try {
            latch.await(2, TimeUnit.SECONDS);
            System.out.println("执行完毕, 耗时：" + (System.currentTimeMillis() - start)/ 1000.0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}