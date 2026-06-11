package com.sunmax.common.feign.fallback;

import com.sunmax.common.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 通用Feign降级工厂
 * 使用JDK动态代理实现，无需为每个FeignClient接口单独创建FallbackFactory
 *
 * 用法：在@FeignClient注解中指定 fallbackFactory = GenericFeignFallbackFactory.class
 * 或继承此类创建特定服务的降级工厂
 */
@Slf4j
public class GenericFeignFallbackFactory implements FallbackFactory<Object>, InvocationHandler {

    private String serviceName;
    private Throwable cause;

    @Override
    public Object create(Throwable cause) {
        this.cause = cause;
        return this;
    }

    /**
     * 创建指定FeignClient接口的降级实例
     */
    @SuppressWarnings("unchecked")
    public <T> T create(String serviceName, Class<T> feignClientInterface, Throwable cause) {
        this.serviceName = serviceName;
        this.cause = cause;
        log.error("{} Feign调用降级: {}", serviceName, cause.getMessage());
        return (T) Proxy.newProxyInstance(
            feignClientInterface.getClassLoader(),
            new Class[]{feignClientInterface},
            this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        String svc = serviceName != null ? serviceName : "unknown";

        // Handle Object methods
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        log.error("Feign降级响应: service={}, method={}, error={}", svc, methodName, cause.getMessage());

        // Return a fallback ResponseResult
        Class<?> returnType = method.getReturnType();
        if (returnType == ResponseResult.class) {
            return ResponseResult.error("服务[" + svc + "]调用失败: " + cause.getMessage());
        }

        return null;
    }
}
