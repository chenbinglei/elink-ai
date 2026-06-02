package com.sunmax.crontab;

import com.sunmax.common.dao.base.impl.BaseDaoImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling // 开启定时任务功能
@EnableFeignClients //启启用feign客户端
//@EnableEurekaClient //注册到注册中心
@EnableDiscoveryClient//注册服务中心
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.sunmax.crontab","com.sunmax.common"})
@EnableJpaRepositories(repositoryBaseClass = BaseDaoImpl.class)
public class CrontabApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrontabApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
