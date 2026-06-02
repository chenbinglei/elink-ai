package com.sunmax.configure;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.sunmax.common.dao.base.impl.BaseDaoImpl;
import com.sunmax.configure.util.PlatformConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients //启启用feign客户端
@EnableDiscoveryClient//注册服务中心
@EnableJpaAuditing
@EnableSwaggerBootstrapUI
@ComponentScan(basePackages = {"com.sunmax.common", "com.sunmax.configure"})
@EnableJpaRepositories(repositoryBaseClass = BaseDaoImpl.class)
public class ConfigureApplication {

    @Value("${platform.id}")
    private String platformId;

    @Value("${platform.secret}")
    private String platformSecret;

    @Value("${platform.data-secret}")
    private String dataSecret;

    @Value("${platform.data-secret-iv}")
    private String dataSecretIv;

    @Value("${platform.sig-secret}")
    private String sigSecret;

    public static void main(String[] args) {
        SpringApplication.run(ConfigureApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        PlatformConfig.PLATFORM_ID = platformId;
        PlatformConfig.PLATFORM_SECRET = platformSecret;
        PlatformConfig.DATA_SECRET = dataSecret;
        PlatformConfig.DATA_SECRET_IV = dataSecretIv;
        PlatformConfig.SIG_SECRET = sigSecret;
        return new RestTemplate();
    }

}
