package com.sunmax.protocol.dto.virtual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * 电桩实时数据返回实体类
 */
@Data
@Schema(description = "电桩实时数据返回实体类")
public class VirtualPileRealDto {

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus = -1;

    /**
     * 当前总功率
     */
    @Schema(description = "当前总功率")
    private Double totalPower;

    /**
     * 当前充电功率
     */
    @Schema(description = "当前充电功率")
    private Double recChargePower;

    /**
     * 当前放电功率
     */
    @Schema(description = "当前放电功率")
    private Double disChargePower;

    /**
     * 数据时间
     */
    @Schema(description = "数据时间")
    private String dateTime;

    /**
     * 充电枪实时数据列表
     */
    @Schema(description = "充电枪实时数据列表")
    private List<VirtualPileRealDto.GunRealData> gunRealDataList = Lists.newArrayList();

    @Data
    @Schema(description = "充电枪实时数据")
    public static class GunRealData {

        /**
         * 枪编号
         */
        @Schema(description = "枪编号")
        private Integer gunCode;

        /**
         * 枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障
         */
        @Schema(description = "枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障")
        private Integer gunStatus;

        /**
         * 车辆连接状态 0-未连接 1-半连接 2-连接 3-连接故障
         */
        @Schema(description = "车辆连接状态 0-未连接 1-半连接 2-连接 3-连接故障")
        private Integer vehicleConnState;

        /**
         * 电池Soc
         */
        @Schema(description = "电池Soc")
        private Integer batterySoc;

        /**
         * 运行模式 -1-未知 0-充电模式 1-放电模式
         */
        @Schema(description = "运行模式 -1-未知 0-充电模式 1-放电模式")
        private Integer runMode = -1;

        /**
         * 输出电压 精度1V
         */
        @Schema(description = "输出电压 精度1V")
        private Double outVolt;

        /**
         * 输出电流 精度1A
         */
        @Schema(description = "输出电流 精度1A")
        private Double outCurrent;

        /**
         * 输出功率 精度1kW
         */
        @Schema(description = "输出功率 精度1kW")
        private Double outPower;

        /**
         * 需求电压 精度1V
         */
        @Schema(description = "需求电压 精度1V")
        private Double reqVolt;

        /**
         * 需求电流 精度1A
         */
        @Schema(description = "需求电流 精度1A")
        private Double reqCurrent;

        /**
         * 需求功率 精度1kW
         */
        @Schema(description = "需求功率 精度1kW")
        private Double reqPower;

        /**
         * 总电量
         */
        @Schema(description = "总电量")
        private Double totalQt;

        /**
         * 总费用
         */
        @Schema(description = "总费用")
        private BigDecimal totalCost;
    }

}
