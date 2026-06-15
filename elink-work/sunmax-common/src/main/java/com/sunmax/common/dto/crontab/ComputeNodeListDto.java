package com.sunmax.common.dto.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 计算节点列表返回实体类
 */
@Data
@Schema(description = "计算节点列表返回实体类")
public class ComputeNodeListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 节点存储id
     */
    @Schema(description = "节点存储id")
    private Long storageId;

    /**
     * 节点编码
     */
    @Schema(description = "节点编码")
    private String nodeCode;

    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    private String nodeName;

    /**
     * 站点/设备id
     */
    @Schema(description = "站点/设备id")
    private String deviceId;

    /**
     * 单位
     */
    @Schema(description = "单位")
    private String unit;

    /**
     * 策略类型 1-每次存储 2-变化存储 3-不存储
     */
    @Schema(description = "策略类型 1-每次存储 2-变化存储 3-不存储")
    private Integer strategyType;

    /**
     * 计算周期 年-y 月-n 日-d 时-h 分-m
     */
    @Schema(description = "计算周期 年-y 月-n 日-d 时-h 分-m")
    private String computePeriod;

    /**
     * 统计周期 自然年-y 自然月-n 日-d 时-h 分-m
     */
    @Schema(description = "统计周期 自然年-y 自然月-n 日-d 时-h 分-m")
    private String countPeriod;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}

