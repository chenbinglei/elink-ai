package com.sunmax.together.dto.websocket;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "PileRealWebsocketDto", description = "电桩实时websocket实体")
public class PileRealWebsocketDto {

    @ApiModelProperty(value = "电桩编号")
    private String pileCode;

    @ApiModelProperty(value = "电桩工作状态 0-未注册 -1-未知 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus;

    @ApiModelProperty(value = "当前总功率")
    private Double totalPower;

    @ApiModelProperty(value = "当前充电功率")
    private Double recChargePower;

    @ApiModelProperty(value = "当前放电功率")
    private Double disChargePower;

    @ApiModelProperty(value = "充电枪实时数据")
    private List<PileRealWebsocketDto.GunRealModel> gunRealModelList = Lists.newArrayList();

    @Data
    @ApiModel(value = "GunRealModel", description = "充电枪实时数据")
    public static class GunRealModel {

        @ApiModelProperty(value = "枪名称")
        private String gunName;

        @ApiModelProperty(value = "枪编号")
        private String gunCode;

        @ApiModelProperty("状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
        private Integer gunWorkState;

        @ApiModelProperty(value = "枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障")
        private Integer gunStatus;

        @ApiModelProperty(value = "交易流水号")
        private String serialNum;

        @ApiModelProperty(value = "电池Soc")
        private Integer batterySoc;

        @ApiModelProperty(value = "输出电压 精度1V")
        private Double outVolt;

        @ApiModelProperty(value = "输出电流 精度1A")
        private Double outCurrent;

        @ApiModelProperty(value = "输出功率 精度1kW")
        private Double outPower;

        @ApiModelProperty(value = "运行时间")
        private Integer runTime;

        @ApiModelProperty(value = "剩余时间")
        private String remainTime;

        @ApiModelProperty(value = "总电量")
        private Double totalQt;

        @ApiModelProperty(value = "总费用")
        private BigDecimal totalCost;

    }

}
