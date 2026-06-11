package com.sunmax.devops.dto;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "设备列表返回实体类")
public class DeviceListDto {

    /**
     * 总条数
     */
    @Schema(description = "总条数")
    private Long total = 0L;

    /**
     * 在线条数
     */
    @Schema(description = "在线条数")
    private Long online = 0L;

    /**
     * 离线条数
     */
    @Schema(description = "离线条数")
    private Long offline = 0L;

    /**
     * 故障条数
     */
    @Schema(description = "故障条数")
    private Long fault = 0L;

    /**
     * 未注册条数
     */
    @Schema(description = "未注册条数")
    private Long unregister = 0L;

    /**
     * 站点设备列表(站点名称 -> 设备数据列表)
     */
    @Schema(description = "站点设备列表(站点名称 -> 设备数据列表)")
    private Map<String, List<Device>> siteDeviceMap = Maps.newHashMap();

    @Data
    @Schema(description = "设备列表返回实体类")
    public static class Device {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

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
         * 设备类型名称
         */
        @Schema(description = "设备类型名称")
        private String typeName;

        /**
         * 通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
         */
        @Schema(description = "通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
        private Integer txStatus = 0;

        /**
         * 模型logo路径
         */
        @Schema(description = "模型logo路径")
        private String logoPath;

        /**
         * 额定功率或额定容量
         */
        @Schema(description = "额定功率或额定容量")
        private Double ratedPower;

        /**
         * 设备型号
         */
        @Schema(description = "设备型号")
        private String equipmentModel;

        /**
         * 创建时间
         */
        @Schema(description = "创建时间")
        private String createTime;

        /**
         * 站点名称
         */
        @Schema(description = "站点名称")
        private String siteName;

        /**
         * 告警列表
         */
        @Schema(description = "告警列表")
        private List<Alarm> alarmList;

    }

    @Data
    @Schema(description = "告警返回实体类")
    public static class Alarm {

        /**
         * 告警名称
         */
        @Schema(description = "告警名称")
        private String alarmName;

        /**
         * 告警时间
         */
        @Schema(description = "告警时间")
        private String alarmTime;

    }

}
