package com.sunmax.devops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备查询参数实体类")
public class DeviceQueryVo {

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * 关键字类型 1-设备编号 2-设备名称
     */
    @Schema(description = "关键字类型 1-设备编号 2-设备名称")
    private Integer keywordType;

    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;

    /**
     * 通信状态 0-未注册 1-在线 2-故障 88-离线
     */
    @Schema(description = "通信状态 0-未注册 1-在线 2-故障 88-离线")
    private String txStatus;

    /**
     * 设备类型id
     */
    @Schema(description = "设备类型id")
    private String typeId;

}
