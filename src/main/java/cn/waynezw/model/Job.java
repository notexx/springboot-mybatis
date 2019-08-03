package cn.waynezw.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
public class Job implements Serializable {
    /**
     * 连续自增主键
     */
    private Long id;
    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务类型
     */
    private String jobType;
    /**
     * 任务数据
     */
    private String jobData;
    /**
     * 状态：0 新建 1执行成功 2-执行失败
     */
    private Integer status;
    /**
     * 执行时间
     */
    private String jobTime;

    /**
     * 执行任务消耗时间
     */
    private String consumerTime;

    /**
     * 执行通道
     */
    private String executeChannel;

}