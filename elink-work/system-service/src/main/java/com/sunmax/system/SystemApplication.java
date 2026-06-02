package com.sunmax.system;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients //启启用feign客户端
//@EnableEurekaClient //注册到注册中心
@EnableDiscoveryClient//注册服务中心
@EnableSwaggerBootstrapUI
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.sunmax.system", "com.sunmax.log","com.sunmax.common"})
//@EnableJpaRepositories(repositoryBaseClass = BaseDaoImpl.class)
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
