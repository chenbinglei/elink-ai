package com.sunmax.common.vo.device;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 设备批量升级编辑参数实体类
 */
@Data
@Schema(description = "设备批量升级编辑参数实体类")
public class DeviceBatchUpdateVo {

    /**
     * 多个设备任务编号
     * 任务id -> 多个设备编号
     */
    @Schema(description = "多个设备任务编号")
    private Map<String, List<DeviceUpdateInfo>> deviceUpdateInfoMap = Maps.newHashMap();

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
     * 任务状态 1-待执行 2-执行中 3-执行关闭
     */
    @Schema(description = "任务状态 1-待执行 2-执行中 3-执行关闭")
    private Integer taskStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceUpdateInfo {

        /**
         * 设备编号
         */
        @Schema(description = "设备编号")
        private String deviceCode;

        /**
         * 状态 0-无需升级 1-等待启动 2-下载中 3-下载失败 4-升级中 5-升级失败 6-升级成功
         */
        @Schema(description = "状态 0-无需升级 1-等待启动 2-下载中 3-下载失败 4-升级中 5-升级失败 6-升级成功")
        private Integer status;

        /**
         * 失败原因 0-成功 1-数据校验失败 2-应答超时 3-flash擦除失败 4-flash写入失败 5-同版本不升级 255-其他原因 256-控制板信息上报不一致
         */
        @Schema(description = "失败原因 0-成功 1-数据校验失败 2-应答超时 3-flash擦除失败 4-flash写入失败 5-同版本不升级 255-其他原因 256-控制板信息上报不一致")
        private Integer failReason;
    }

}
