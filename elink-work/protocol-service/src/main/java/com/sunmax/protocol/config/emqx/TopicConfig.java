package com.sunmax.protocol.config.emqx;

import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisUtil;
import com.sunmax.common.constant.WebTopicConstant;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.util.JsonUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.protocol.service.feign.DeviceService;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class TopicConfig {

    @Resource
    private DeviceService deviceService;

    public Set<String> getWebTopicList() {
        //获取所有注册过的网关编号
        Set<String> topicList = deviceService.getDeviceNumberList(2).getData().stream().filter(deviceCode -> {
            String deviceKey = KeyUtil.DEVICE_KEY + deviceCode;
            if (RedisUtil.hasKey(deviceKey)) {
                DeviceModel deviceModel = JsonUtil.objectToEntity(RedisUtil.get(deviceKey), DeviceModel.class);
                return StringUtil.isNotEmpty(deviceModel.getTxStatus()) && !Objects.equals(deviceModel.getTxStatus(), 0);
            }
            return false;
        }).map(WebTopicConstant::getAllTopics).flatMap(Collection::stream).collect(Collectors.toSet());
        topicList.add(WebTopicConstant.PLATFORM_REQUEST);
        topicList.add(WebTopicConstant.PLATFORM_RESPONSE);
        return topicList;
    }

}
