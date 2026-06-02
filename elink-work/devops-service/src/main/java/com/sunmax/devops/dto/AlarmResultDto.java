package com.sunmax.devops.dto;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "AlarmResultDto", description = "告警列表返回实体类")
public class AlarmResultDto {

    /**
     * 总条数
     */
    @ApiModelProperty("总条数")
    protected int totalSize;

    /**
     * 每页条数
     */
    @ApiModelProperty(value = "每页条数")
    protected int pageSize;

    /**
     * 第几页
     */
    @ApiModelProperty(value = "第几页")
    protected int index;

    /**
     * 紧急条数
     */
    @ApiModelProperty(value = "紧急条数")
    private Long emergency = 0L;

    /**
     * 重要条数
     */
    @ApiModelProperty(value = "重要条数")
    private Long important = 0L;

    /**
     * 次要条数
     */
    @ApiModelProperty(value = "次要条数")
    private Long secondary = 0L;

    /**
     * 提示条数
     */
    @ApiModelProperty(value = "提示条数")
    private Long tip = 0L;

    /**
     * 离线条数
     */
    @ApiModelProperty(value = "离线条数")
    private Long offLine = 0L;

    /**
     * 未知条数
     */
    @ApiModelProperty(value = "未知条数")
    private Long unknown = 0L;

    /**
     * 日期告警列表
     */
    @ApiModelProperty(value = "日期告警列表")
    private Map<String, List<AlarmDto>> alarmMap = Maps.newHashMap();

    @Data
    @ApiModel(value = "AlarmDto", description = "告警返回实体类")
    public static class AlarmDto {

        /**
         * 设备事件主键id
         */
        @ApiModelProperty(value = "设备事件主键id")
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
         * 站点名称
         */
        @ApiModelProperty(value = "站点名称")
        private String siteName;

        /**
         * 事件名称
         */
        @ApiModelProperty(value = "事件名称")
        private String eventName;

        /**
         * 事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
         */
        @ApiModelProperty(value = "事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警")
        private Integer eventLevel;

        /**
         * 事件状态 0-未恢复 1-已修复
         */
        @ApiModelProperty(value = "事件状态 0-未恢复 1-已修复")
        private Integer eventStatus;

        /**
         * 事件类型 1-模型事件 2-故障定义
         */
        @ApiModelProperty(value = "事件类型 1-模型事件 2-故障定义")
        private Integer type;

        /**
         * 创建时间
         */
        @ApiModelProperty(value = "创建时间")
        private String createTime;

        /**
         * 编辑时间
         */
        @ApiModelProperty(value = "编辑时间")
        private String updateTime;
    }

}
