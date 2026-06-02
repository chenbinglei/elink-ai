package com.sunmax.configure.task;

import com.sunmax.configure.runner.SubstationRunner;
import com.sunmax.configure.service.InterflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 互联互通数据定时任务类
 */
@Configuration
public class InterflowTask {

    @Autowired
    private InterflowService interflowService;

    /**
     * 每天晚上零点查询一次站点和设备以及电枪接口数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(fixedRate = 1000 * 60)
    public void queryData() {
        SubstationRunner.interflowMap.keySet().forEach(platformId -> interflowService.queryStationsInfo(platformId));
    }

}
