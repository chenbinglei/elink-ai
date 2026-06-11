package com.sunmax.together.vo.energy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 策略新增参数实体类
 */
@Data
@Schema(description = "策略新增参数实体类")
public class StrategySaveVo {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 策略模板id
     */
    @Schema(description = "策略模板id")
    private String templateId;

    /**
     * 策略名称
     */
    @Schema(description = "策略名称")
    private String strategyName;

    /**
     * 类型 1-边缘网关 2-云网关 3-云平台
     */
    @Schema(description = "类型 1-边缘网关 2-云网关 3-云平台")
    private Integer strategyType;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

}
