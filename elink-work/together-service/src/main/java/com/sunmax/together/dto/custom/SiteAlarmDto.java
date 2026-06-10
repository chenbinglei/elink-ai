package com.sunmax.together.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点告警信息返回实体类")
public class SiteAlarmDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
     */
    @Schema(description = "事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警")
    private Integer eventLevel;

    /**
     * 事件名称
     */
    @Schema(description = "事件名称")
    private String eventName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 编辑时间
     */
    @Schema(description = "编辑时间")
    private String updateTime;

}
