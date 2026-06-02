package com.sunmax.configure.common;

import com.sunmax.common.constant.WebTopicConstant;
import com.sunmax.common.util.oss.FileUtil;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class TopicConfig {

    public Set<String> getWebTopicList() {
        //获取所有注册过的网关编号
        Set<String> topicList = new HashSet<>();
        topicList.add(WebTopicConstant.TOPIC_PREFIX + FileUtil.PLUS + WebTopicConstant.SERVICE_DATA);
        topicList.add(WebTopicConstant.TOPIC_PREFIX + FileUtil.PLUS + WebTopicConstant.SERVICE_RESPONSE);
        return topicList;
    }

}
