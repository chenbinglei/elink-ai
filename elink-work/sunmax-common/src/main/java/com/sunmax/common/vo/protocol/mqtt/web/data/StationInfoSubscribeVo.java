package com.sunmax.common.vo.protocol.mqtt.web.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "StationInfoSubscribeVo", description = "站点信息数据实体类")
public class StationInfoSubscribeVo {

    /**
     * 当前配电负荷
     */
    @ApiModelProperty(value = "当前配电负荷")
    private Double curLoad;

    /**
     * 当前充电功率
     */
    @ApiModelProperty(value = "当前充电功率")
    private Double curPc;

    /**
     * 当前放电功率
     */
    @ApiModelProperty(value = "当前放电功率")
    private Double curPd;

    /**
     * 剩余可调充电功率
     */
    @ApiModelProperty(value = "剩余可调充电功率")
    private Double remainPc;

    /**
     * 剩余可调放电功率
     */
    @ApiModelProperty(value = "剩余可调放电功率")
    private Double remainPd;

    /**
     * 充电桩数量
     */
    @ApiModelProperty(value = "充电桩数量")
    private Integer pileNum;

    /**
     * 充电桩信息表
     */
    @ApiModelProperty(value = "充电桩信息表")
    private List<PileInfoDto> pileInfoList;

    @Data
    @ApiModel(value = "PileInfoDto", description = "电桩信息返回实体类")
    public static class PileInfoDto implements Serializable {

        /**
         * 电桩编号
         */
        @ApiModelProperty(value = "电桩编号")
        private String cid;

        /**
         * 工作状态 0-待机 1-工作 2-维护 3-故障 88-离线
         */
        @ApiModelProperty(value = "工作状态 0-待机 1-工作 2-维护 3-故障 88-离线")
        private Integer workState;

        /**
         * 充放电接口数量
         */
        @ApiModelProperty(value = "充放电接口数量")
        private Integer itfNum;

        /**
         * 充放电接口信息
         */
        @ApiModelProperty(value = "充放电接口信息")
        private List<ItfInfoDto> itfInfoList;
    }

    @Data
    @ApiModel(value = "ItfInfoDto", description = "充放电接口信息")
    public static class ItfInfoDto implements Serializable {

        /**
         * 充放电接口编号
         */
        @ApiModelProperty(value = "充放电接口编号")
        private String itfCode;

        /**
         * 充放电接口状态
         * -1 未知；
         * 0 空闲；
         * 1 充电准备;
         * 2 充电中；
         * 3 充电完成；
         * 4 放电准备;
         * 5 放电中；
         * 6 放电完成；
         * 7 预约；
         * 255 故障；
         */
        @ApiModelProperty(value = "充放电接口状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-充电完成 4-放电准备 5-放电中 6-放电完成 7-预约 255-故障")
        private Integer workState;

        /**
         * 车辆连接状态
         * -1 未知； 0 未连接；1 半连接；2 连接；
         */
        @ApiModelProperty(value = "车辆连接状态 -1-未知 0-未连接 1-半连接 2-已连接")
        private Integer evConnectState;

        /**
         * 是否支持V2G 0-是 1-否
         */
        @ApiModelProperty(value = "是否支持V2G 0-是 1-否")
        private Integer v2gFlag;

        /**
         * 当前SOC
         */
        @ApiModelProperty(value = "当前SOC")
        private Integer curSoc;

        /**
         * 当前电压
         */
        @ApiModelProperty(value = "当前电压")
        private Double curVolt;

        /**
         * 当前电流
         */
        @ApiModelProperty(value = "当前电流")
        private Double curCurrent;

        /**
         * 当前电量
         */
        @ApiModelProperty(value = "当前电量")
        private Double curQ;

    }

}
