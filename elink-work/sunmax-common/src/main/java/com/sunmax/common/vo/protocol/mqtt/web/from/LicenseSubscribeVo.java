package com.sunmax.common.vo.protocol.mqtt.web.from;

import lombok.Data;

/**
 * 许可相关响应订阅实体类
 */
@Data
public class LicenseSubscribeVo {

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

    /**
     * 许可状态 0-未注册  1-许可有效 2-许可过期
     */
    private Integer status;

    /**
     * 结果 0-失败 1-成功 2许可更新失败
     */
    private Integer result;

}
