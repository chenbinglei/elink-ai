package com.sunmax.together.dto.websocket;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "电桩实时websocket实体")
public class PileRealWebsocketDto {

    @Schema(description = "电桩编号")
    private String pileCode;

    @Schema(description = "电桩工作状态 0-未注册 -1-未知 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus;

    @Schema(description = "当前总功率")
    private Double totalPower;

    @Schema(description = "当前充电功率")
    private Double recChargePower;

    @Schema(description = "当前放电功率")
    private Double disChargePower;

    @Schema(description = "充电枪实时数据")
    private List<PileRealWebsocketDto.GunRealModel> gunRealModelList = Lists.newArrayList();

    @Data
    @Schema(description = "充电枪实时数据")
    public static class GunRealModel {

        @Schema(description = "枪名称")
        private String gunName;

        @Schema(description = "枪编号")
        private String gunCode;

        @Schema(description = "状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
        private Integer gunWorkState;

        @Schema(description = "枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障")
        private Integer gunStatus;

        @Schema(description = "交易流水号")
        private String serialNum;

        @Schema(description = "电池Soc")
        private Integer batterySoc;

        @Schema(description = "输出电压 精度1V")
        private Double outVolt;

        @Schema(description = "输出电流 精度1A")
        private Double outCurrent;

        @Schema(description = "输出功率 精度1kW")
        private Double outPower;

        @Schema(description = "运行时间")
        private Integer runTime;

        @Schema(description = "剩余时间")
        private String remainTime;

        @Schema(description = "总电量")
        private Double totalQt;

        @Schema(description = "总费用")
        private BigDecimal totalCost;

    }

}
