package com.sunmax.crontab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 节点补录列表查询入参
 */
@Data
@Schema(description = "节点补录列表查询入参")
public class NodeAddRecordQueryVo {

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

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 关键字类型 1-名称 2-编码
     */
    @Schema(description = "关键字类型 1-名称 2-编码")
    private Integer keyType;

    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyValue;
}
