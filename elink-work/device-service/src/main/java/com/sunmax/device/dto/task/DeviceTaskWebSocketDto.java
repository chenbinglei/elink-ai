package com.sunmax.device.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
public class DeviceTaskWebSocketDto {

    /**
     * 任务id
     */
    @Schema(description = "任务id")
    private String taskId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String taskName;

    /**
     * 任务状态 1-待执行 2-执行中 3-执行关闭
     */
    @Schema(description = "任务状态 1-待执行 2-执行中 3-执行关闭")
    private Integer taskStatus;

    /**
     * 设备升级数据列表
     */
    @Schema(description = "设备升级数据列表")
    private List<DeviceUpdateData> deviceDataList = Lists.newArrayList();

    @Data
    public static class DeviceUpdateData {

        /**
         * 设备序列号
         */
        @Schema(description = "设备序列号")
        private String deviceNumber;

        /**
         * 设备任务状态 0-无需升级 1-等待启动 2-下载中 3-下载失败 4-升级中 5-升级失败 6-升级成功
         */
        @Schema(description = "设备任务状态 0-无需升级 1-等待启动 2-下载中 3-下载失败 4-升级中 5-升级失败 6-升级成功")
        private Integer status;

        /**
         * 进度条 百分比
         */
        @Schema(description = "进度条 百分比")
        private Long progress;

        /**
         * 升级时间
         */
        @Schema(description = "升级时间")
        private String upgradeTime;

        /**
         * 结束时间
         */
        @Schema(description = "结束时间")
        private String endTime;

        /**
         * 原因
         */
        @Schema(description = "原因")
        private String reason;

    }

}
