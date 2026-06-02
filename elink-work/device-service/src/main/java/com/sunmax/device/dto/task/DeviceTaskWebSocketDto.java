package com.sunmax.device.dto.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
public class DeviceTaskWebSocketDto {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    private String taskId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * 任务状态 1-待执行 2-执行中 3-执行关闭
     */
    @ApiModelProperty(value = "任务状态 1-待执行 2-执行中 3-执行关闭")
    private Integer taskStatus;

    /**
     * 设备升级数据列表
     */
    @ApiModelProperty(value = "设备升级数据列表")
    private List<DeviceUpdateData> deviceDataList = Lists.newArrayList();

    @Data
    public static class DeviceUpdateData {

        /**
         * 设备序列号
         */
        @ApiModelProperty(value = "设备序列号")
        private String deviceNumber;

        /**
         * 设备任务状态 0-无需升级 1-等待启动 2-下载中 3-下载失败 4-升级中 5-升级失败 6-升级成功
         */
        @ApiModelProperty(value = "设备任务状态 0-无需升级 1-等待启动 2-下载中 3-下载失败 4-升级中 5-升级失败 6-升级成功")
        private Integer status;

        /**
         * 进度条 百分比
         */
        @ApiModelProperty(value = "进度条 百分比")
        private Long progress;

        /**
         * 升级时间
         */
        @ApiModelProperty(value = "升级时间")
        private String upgradeTime;

        /**
         * 结束时间
         */
        @ApiModelProperty(value = "结束时间")
        private String endTime;

        /**
         * 原因
         */
        @ApiModelProperty(value = "原因")
        private String reason;

    }

}
