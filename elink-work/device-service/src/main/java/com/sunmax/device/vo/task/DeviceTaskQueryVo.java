package com.sunmax.device.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备任务列表查询参数实体类
 */
@Data
@Schema(description = "设备任务列表查询参数实体类")
public class DeviceTaskQueryVo {

    /**
     * 关键词
     */
    @Schema(description = "关键词")
    private String keyword;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

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
