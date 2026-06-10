package com.sunmax.device.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备任务记录列表返回实体类
 */
@Data
@Schema(description = "设备任务记录列表返回实体类")
public class DeviceTaskRecordListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

    /**
     * 状态 0-无需升级 1-等待启动 2-下载中 3-下载失败 4-升级中 5-升级失败 6-升级成功
     */
    @Schema(description = "状态 0-无需升级 1-等待启动 2-下载中 3-下载失败 4-升级中 5-升级失败 6-升级成功")
    private Integer status;

    /**
     * 原因
     */
    @Schema(description = "原因")
    private String reason;

    /**
     * 源版本
     */
    @Schema(description = "源版本")
    private String sourceVersion;

    /**
     * 目标版本
     */
    @Schema(description = "目标版本")
    private String targetVersion;

//    /**
//     * 进度条
//     */
//    @Schema(description = "进度条")
//    private Integer progress;

    /**
     * 升级时间
     */
    @Schema(description = "升级时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime upgradeTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
