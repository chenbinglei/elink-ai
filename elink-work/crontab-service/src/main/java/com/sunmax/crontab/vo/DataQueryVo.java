package com.sunmax.crontab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据查询信息参数")
public class DataQueryVo {

    /**
     * 多个功能点标识
     */
    @Schema(description = "多个功能点标识")
    private String functionLogos;

    /**
     * 多个数组类型功能点标识
     */
    @Schema(description = "多个数组类型功能点标识")
    private String arrayFunctionLogos;

    /**
     * 多个计算节点唯一id
     */
    @Schema(description = "多个计算节点唯一id")
    private String nodeIds;

    /**
     * 所选设备id
     */
    @Schema(description = "所选设备id")
    private String deviceId;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "开始时间(yyyy-MM-dd HH:mm:ss)")
    private String startTime;

    /**
     * 结束时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    private String endTime;

    /**
     * 索引
     */
    @Schema(description = "索引")
    private Integer index;
}
