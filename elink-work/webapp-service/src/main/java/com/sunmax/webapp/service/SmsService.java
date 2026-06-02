package com.sunmax.webapp.service;


/**
 * @author yqz
 */
public interface SmsService {
    /**
     * 发送短信验证码
     * @param mobile
     * @param code
     * @return
     */
    boolean sendSms(String mobile, String code);
}
