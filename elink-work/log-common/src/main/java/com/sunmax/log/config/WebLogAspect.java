package com.sunmax.log.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sunmax.log.dao.AccessLogDao;
import com.sunmax.log.entity.AccessLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class WebLogAspect {


    @Autowired
    private AccessLogDao accessLogDao;

    @Pointcut("@annotation(com.sunmax.log.config.WebLog)")
    public void webLog() {}

    @Around("webLog()")
    @Transactional(transactionManager = "logJpaTransactionManager", rollbackFor = Exception.class)
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object result = joinPoint.proceed();
        try {
            AccessLogEntity accessLog = new AccessLogEntity();
            //客户端id
            accessLog.setClientId(getClientId(authentication));

            //用户账号
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
                String userAccount = authentication.getPrincipal().toString();
                accessLog.setUserAccount(userAccount);
            }
            //远程ip地址
            accessLog.setRemoteAddr(getLoginIp(request));

            //远程端口地址
            accessLog.setRemotePort(request.getRemotePort());

            //本地ip地址
            accessLog.setLocalAddr(request.getLocalAddr());

            //本地端口地址
            accessLog.setLocalPort(request.getLocalPort());

            //方法名
            accessLog.setMethod(request.getMethod());

            //请求url
            accessLog.setUrl(request.getRequestURI());

            //获取响应状态码和响应结果
            if (result != null) {
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(result));
                accessLog.setCode(jsonObject.getInteger("code"));
                accessLog.setMessage(jsonObject.getString("message"));
            }

            //操作内容
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            Object target = joinPoint.getTarget(); // 获取目标对象（即 controller 实例）
            // 先看方法上有没有注解
            if (method.isAnnotationPresent(WebLog.class)) {
                WebLog webLog = method.getAnnotation(WebLog.class);
                accessLog.setContent(webLog.value());
            } else {
                // 再看看类上有没有
                Class<?> targetClass = target.getClass();
                if (targetClass.isAnnotationPresent(WebLog.class)) {
                    WebLog webLog = targetClass.getAnnotation(WebLog.class);
                    accessLog.setContent(webLog.value());
                }
            }
            accessLogDao.save(accessLog);
        } catch (Exception e) {
            log.error("记录日志失败", e);
        }
        return result;
    }

    /**
     * 获取当前登录客户端id
     * @param authentication
     * @return
     */
    public static String getClientId(Authentication authentication) {
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oauthAuthentication = (OAuth2Authentication) authentication;
            return oauthAuthentication.getOAuth2Request().getClientId();
        }
        return null;
    }

    public static String getLoginIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    return "";
                }
                ipAddress = inet.getHostAddress();
            }
        }
        // 如果通过代理访问,可能获取2个IP,这时候去第二个(代理服务端IP)
        if (ipAddress.split(",").length > 1) {
            ipAddress = ipAddress.split(",")[1].trim();
        }
        return ipAddress;
    }

}
