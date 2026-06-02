package com.sunmax.common.vo.protocol.mqtt.web.common;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * data主题公共类
 */
@Data
public class DataSubscribeVo {

    private List<DevicesVo> devices = new ArrayList<>();

    @Data
    public static class DevicesVo implements Serializable {

        /**
         * 设备id
         */
        private String deviceId;

        /**
         * 服务数据
         */
        private List<ServiceVo> services = new ArrayList<>();

    }

    @Data
    public static class ServiceVo implements Serializable {

        /**
         * 服务数据
         */
        private Object data;

        /**
         * 事件时间
         */
        private String eventTime;

        /**
         * 服务id
         */
        private String serviceId;

    }

}
