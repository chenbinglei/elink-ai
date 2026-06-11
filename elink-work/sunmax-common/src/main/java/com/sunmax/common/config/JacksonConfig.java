package com.sunmax.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson 配置
 *
 * 确保 Spring MVC 使用的 ObjectMapper 不启用 DefaultTyping，
 * 避免序列化时输出 "@class" 类型信息字段。
 *
 * 背景：Spring Security OAuth2 Authorization Server 的自动配置
 * 会注册带 DefaultTyping 的 ObjectMapper bean，导致 REST API 响应
 * 中出现 "@class":"com.sunmax.common.util.ResponseResult" 等冗余字段。
 * Jackson2ObjectMapperBuilderCustomizer.defaultTyping(null) 无法覆盖，
 * Jackson2ObjectMapperBuilder 也可能被设置了 DefaultTyping，
 * 必须通过 @Primary new ObjectMapper() 直接替换。
 *
 * 手动注册 JavaTimeModule 以支持 Java 8 日期时间类型。
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 注册 JavaTimeModule 支持 Java 8 日期时间
        mapper.registerModule(new JavaTimeModule());
        // 日期序列化为字符串格式
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 日期格式
        mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return mapper;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .simpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
