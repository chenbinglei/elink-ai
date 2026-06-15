package com.sunmax.crontab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 计算节点列表查询实体类
 */
@Data
@Schema(description = "计算节点列表查询信息参数")
public class ComputeNodeListVo {

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
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;

    /**
     * 关键字类型 1-节点名称 2-节点标识
     */
    @Schema(description = "关键字类型 1-节点名称 2-节点标识")
    private Integer keywordType;

    /**
     * 策略类型 1-每次存储 2-变化存储 3-不存储
     */
    @Schema(description = "策略类型 1-每次存储 2-变化存储 3-不存储")
    private Integer strategyType;
}
