package com.sunmax.together;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients //启启用feign客户端
@EnableScheduling //开启定时任务功能
@EnableDiscoveryClient//注册服务中心
@EnableSwaggerBootstrapUI
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.sunmax.common", "com.sunmax.log", "com.sunmax.together"})
//@EnableJpaRepositories(repositoryBaseClass = BaseDaoImpl.class)
public class TogetherApplication {

    public static void main(String[] args) {
        SpringApplication.run(TogetherApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    /**
     * 手动创建一个 TaskScheduler Bean
     * 这将解决 WebSocket 和 @Scheduled 之间的冲突
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("my-task-scheduler-");
        scheduler.setPoolSize(6); // 根据您的需求调整线程池大小
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }

}
