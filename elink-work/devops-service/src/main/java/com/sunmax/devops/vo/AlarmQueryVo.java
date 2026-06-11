package com.sunmax.devops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "告警查询实体类")
public class AlarmQueryVo {

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceNumber;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 多个设备类型id
     */
    @Schema(description = "多个设备类型id")
    private String typeIds;

    /**
     * 事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
     */
    @Schema(description = "事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警")
    private Integer eventLevel;

    /**
     * 事件状态 0-未恢复 1-已修复
     */
    @Schema(description = "事件状态 0-未恢复 1-已修复")
    private Integer eventStatus;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期 默认近一周", example = "yyyy-MM-dd")
    private String startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期 默认近一周", example = "yyyy-MM-dd")
    private String endDate;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}
