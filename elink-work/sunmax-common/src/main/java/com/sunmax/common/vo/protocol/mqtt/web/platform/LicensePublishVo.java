package com.sunmax.common.vo.protocol.mqtt.web.platform;

import lombok.Data;

/**
 * 下发许可发布实体类
 */
@Data
public class LicensePublishVo {

    /**
     * 设备key
     */
    private String key;

    /**
     * 许可证
     */
    private String license;

    /**
     * 有效期-时间戳
     */
    private Long validityPeriod;

}
