package com.sunmax.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 点表编辑参数
 */
@Data
@Schema(description = "pointTableChangeVo")
public class PointTableChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 通道id
     */
    @Schema(description = "通道id")
    private String channelId;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 功能点id
     */
    @Schema(description = "功能点id")
    private String functionId;

    /**
     * 功能点下标
     */
    @Schema(description = "功能点下标")
    private Integer functionIndex;

    /**
     * 数据点号
     */
    @Schema(description = "数据点号")
    private Long dataId;

    /**
     * 系数
     */
    @Schema(description = "系数")
    private Float coefficient;

    /**
     * 偏移量
     */
    @Schema(description = "偏移量")
    private Integer offset;

    /**
     * 编辑类型 1-新增 2-编辑 3-删除
     */
    @Schema(description = "编辑类型 1-新增 2-编辑 3-删除")
    private Integer updateType;

}
