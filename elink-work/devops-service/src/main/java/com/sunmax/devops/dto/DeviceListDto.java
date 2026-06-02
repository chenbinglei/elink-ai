package com.sunmax.devops.dto;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "DeviceListDto", description = "设备列表返回实体类")
public class DeviceListDto {

    /**
     * 总条数
     */
    @ApiModelProperty(value = "总条数")
    private Long total = 0L;

    /**
     * 在线条数
     */
    @ApiModelProperty(value = "在线条数")
    private Long online = 0L;

    /**
     * 离线条数
     */
    @ApiModelProperty(value = "离线条数")
    private Long offline = 0L;

    /**
     * 故障条数
     */
    @ApiModelProperty(value = "故障条数")
    private Long fault = 0L;

    /**
     * 未注册条数
     */
    @ApiModelProperty(value = "未注册条数")
    private Long unregister = 0L;

    /**
     * 站点设备列表(站点名称 -> 设备数据列表)
     */
    @ApiModelProperty(value = "站点设备列表(站点名称 -> 设备数据列表)")
    private Map<String, List<Device>> siteDeviceMap = Maps.newHashMap();

    @Data
    @ApiModel(value = "Device", description = "设备列表返回实体类")
    public static class Device {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private String id;

        /**
         * 设备名称
         */
        @ApiModelProperty(value = "设备名称")
        private String deviceName;

        /**
         * 设备序列号
         */
        @ApiModelProperty(value = "设备序列号")
        private String deviceNumber;

        /**
         * 设备类型名称
         */
        @ApiModelProperty(value = "设备类型名称")
        private String typeName;

        /**
         * 通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
         */
        @ApiModelProperty(value = "通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
        private Integer txStatus = 0;

        /**
         * 模型logo路径
         */
        @ApiModelProperty(value = "模型logo路径")
        private String logoPath;

        /**
         * 额定功率或额定容量
         */
        @ApiModelProperty(value = "额定功率或额定容量")
        private Double ratedPower;

        /**
         * 设备型号
         */
        @ApiModelProperty(value = "设备型号")
        private String equipmentModel;

        /**
         * 创建时间
         */
        @ApiModelProperty(value = "创建时间")
        private String createTime;

        /**
         * 站点名称
         */
        @ApiModelProperty(value = "站点名称")
        private String siteName;

        /**
         * 告警列表
         */
        @ApiModelProperty(value = "告警列表")
        private List<Alarm> alarmList;

    }

    @Data
    @ApiModel(value = "Alarm", description = "告警返回实体类")
    public static class Alarm {

        /**
         * 告警名称
         */
        @ApiModelProperty(value = "告警名称")
        private String alarmName;

        /**
         * 告警时间
         */
        @ApiModelProperty(value = "告警时间")
        private String alarmTime;

    }

}
