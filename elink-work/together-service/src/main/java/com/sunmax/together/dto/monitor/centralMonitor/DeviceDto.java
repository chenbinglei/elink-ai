package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "设备数据返回实体类")
public class DeviceDto {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备状态
     * 设备状态 -1-未知 0-未注册 1-在线 2-故障 88-离线
     * 电池簇状态 -1-未知 0-待机 1-禁充 2-禁放 3-故障 4-告警 5-充电 6-放电 7-停机 8-休眠
     * 枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
     */
    @Schema(description = "设备状态")
    private Integer deviceStatus = -1;

    /**
     * 设备状态名称
     */
    @Schema(description = "设备状态名称")
    private String deviceStatusName;

    /**
     * 设备数据列表
     */
    @Schema(description = "设备数据列表")
    private List<DataListDto> dataList;

    @Data
    @Schema(description = "数据列表返回实体类")
    public static class DataListDto {

        /**
         * 数据名称
         */
        @Schema(description = "数据名称")
        private String name;

        /**
         * 数据值
         */
        @Schema(description = "数据值")
        private Object value;

    }

}
