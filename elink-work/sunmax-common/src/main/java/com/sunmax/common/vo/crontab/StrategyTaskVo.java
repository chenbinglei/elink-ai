package com.sunmax.common.vo.crontab;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class StrategyTaskVo {

    /**
     * 策略id
     */
    @Schema(description = "策略id")
    private String id;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 停止日期
     */
    @Schema(description = "停止日期")
    private String filterDates;

    /**
     * 执行类型 1-全时段 2-工作日 3-周末 4-自定义时段
     */
    @Schema(description = "执行类型 1-全时段 2-工作日 3-周末 4-自定义时段")
    private Integer executeType;

    /**
     * 执行时段 自定义时段{1:["00:00:00","01:00:00"],2:["00:00:00"]}
     */
    @Schema(description = "执行时段 自定义时段{1:['00:00:00','01:00:00'],2:['00:00:00']}")
    private String executeTime;

    /**
     * 修改人id
     */
    @Schema(description = "修改人id")
    private String updateId;

    /**
     * 策略使能 true-开启 false-关闭
     */
    @Schema(description = "策略使能 true-开启 false-关闭")
    private Boolean enabled = false;

    /**
     * 站点最大充电功率
     */
    @Schema(description = "站点最大功率")
    private Double maxP;

    /**
     * 功率允许波动值
     */
    @Schema(description = "功率允许波动值")
    private Double wavePower;

    /**
     * 优先级 1-SOC优先 2-顺序优先 3-综合
     */
    @Schema(description = "优先级 1-SOC优先 2-顺序优先 3-综合")
    private Integer priority;

    /**
     * 控制周期(秒)
     */
    @Schema(description = "控制周期(秒)")
    private Integer period;

    /**
     * 目标值
     */
    @Schema(description = "目标值")
    private BigDecimal targetP;

    /**
     * 调控时间
     */
    @Schema(description = "调控时间")
    private Long controlTime;

    /**
     * 有效期时长
     */
    @Schema(description = "有效期时长")
    private Integer validTime;

    /**
     * 控制对象(被控制电桩数据 桩编号+枪编号 -> 控制电桩数据)
     */
    @Schema(description = "控制对象(被控制电桩数据 桩编号+枪编号 -> 控制电桩数据)")
    private Map<String, StrategyPile> strategyPileMap = Maps.newHashMap();

    /**
     * 调控时段数组列表
     */
    @Schema(description = "调控时段数组列表")
    private List<StrategyTime> strategyTimeList = Lists.newArrayList();

    @Data
    public static class StrategyPile {

        /**
         * 电桩编号
         */
        @Schema(description = "电桩编号")
        private String cid;

        /**
         * 枪编号
         */
        @Schema(description = "枪编号")
        private Integer gid;

        /**
         * 桩额定功率（kW）
         */
        @Schema(description = "桩额定功率（kW）")
        private Double ratedP;

        /**
         * 最大功率（kW）
         */
        @Schema(description = "最大功率（kW）")
        private Double maxP;

        /**
         * 最小功率（kW）
         */
        @Schema(description = "最小功率（kW）")
        private Double minP;

        /**
         * 充电类型 1-快充 2-慢充
         */
        @Schema(description = "充电类型")
        private Integer type;

        /**
         * 控制类型 1-可调控 2-不可调控
         */
        @Schema(description = "控制类型 1-可调控 2-不可调控")
        private Integer controlType;

        /**
         * 离线最大占用时长(h)
         */
        @Schema(description = "离线最大占用时长(h)")
        private Double offlineDuration;

    }

    @Data
    public static class StrategyTime {

        /**
         * 时段开始时间
         */
        @Schema(description = "时段开始时间")
        private String beginTime;

        /**
         * 时段结束时间
         */
        @Schema(description = "时段结束时间")
        private String endTime;

        /**
         * 类型 1-充电 2-放电 3-静置
         */
        @Schema(description = "类型 1-充电 2-放电 3-静置")
        private Integer type;

        /**
         * 重复次数
         */
        @Schema(description = "重复次数")
        private Integer repeatNum = 0;

    }

}
