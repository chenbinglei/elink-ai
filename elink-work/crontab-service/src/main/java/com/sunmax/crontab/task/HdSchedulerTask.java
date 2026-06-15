package com.sunmax.crontab.task;

import com.sunmax.crontab.service.HdDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.annotation.Resource;

@Configuration
@Slf4j
public class HdSchedulerTask {

    @Resource
    private HdDataService hdDataService;

    @Scheduled(cron = "10 0/1 * * * ?")
    public void stationTask() {
        //推送华电充电站负荷信息
        hdDataService.stationTask();
    }

}
