package com.sunmax.webapp.service.impl;


import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.sunmax.common.util.Constants;
import com.sunmax.common.util.SmsUtil;
import com.sunmax.webapp.mapper.DictMapper;
import com.sunmax.webapp.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * @author yqz
 * @description
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private DictMapper dictMapper;

    @Override
//    @Transactional(transactionManager = "", rollbackFor = Exception.class)
    public boolean sendSms(String mobile, String code) {

        String template_code="";
        String template_param="{\"code\":\""+code+"\"}";
        try{
            Map<String,Object> mapSmsCode=dictMapper.getDictByCode(Constants.Sms.SMS_TEMPLATE_CODE);
            if(mapSmsCode.size()>0){
                template_code=mapSmsCode.get("dictname")+"";
            }else {
                return false;
            }
            //根据短信模板的不同，传不同的参数
            SendSmsResponse sendSmsResponse = SmsUtil.sendSms(mobile,template_code,"晟曼电力",template_param);
            if(null==sendSmsResponse.getCode()  || !"OK".equals(sendSmsResponse.getCode())) {
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



}
