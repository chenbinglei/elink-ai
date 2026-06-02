package com.sunmax.common.vo.protocol.mqtt.web.command;

import lombok.Data;

import java.util.List;

/**
 * 更新子设备 订阅实体类
 */
@Data
public class CmdToPoUpdateSubscribeVo {

    private List<DeviceStatusVo> deviceStatuses;

    @Data
    public static class DeviceStatusVo {

        /**
         * 设备id
         */
        private String deviceId;

        /**
         * 子设备状态
         * ONLINE：设备在线
         * OFFLINE：设备离线
         */
        private String status;

        /**
         * 子设备上线原因 1-复位上线 2-离网上线 3-离网断开
         */
        private Integer reason;

        /**
         * 特征码
         */
        private Long featureCode;

    }
}
