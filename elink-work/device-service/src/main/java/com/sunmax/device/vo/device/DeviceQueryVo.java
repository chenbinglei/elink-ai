package com.sunmax.device.vo.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备查询条件参数")
public class DeviceQueryVo {

    /**
     * 关键词(设备ID+设备名称)
     */
    @Schema(description = "关键词(设备ID+设备名称)")
    private String keyword;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    public String deviceNumber;

    /**
     * 模型id
     */
    @Schema(description = "模型id")
    public String modelId;

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @Schema(description = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    public Integer accessType;

    /**
     * 通信状态 0-未注册 1-在线 2-故障 88-离线
     */
    @Schema(description = "通信状态 0-未注册 1-在线 2-故障 88-离线")
    public Integer txStatus;

    /**
     * 告警状态 1-无告警 2-有告警
     */
    @Schema(description = "告警状态 1-无告警 2-有告警")
    private Integer alarmStatus;

    /**
     * 关联资产分类id
     */
    @Schema(description = "关联资产分类id")
    private Integer typeId;

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
