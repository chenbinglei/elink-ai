package com.sunmax.together.dto.operation.runScene;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "站点监视数据")
public class SitePileMonitorDto {

    /**
     * 全部
     */
    @Schema(description = "全部")
    private Long whole = 0L;

    /**
     * 未知
     */
    @Schema(description = "未知")
    private Long unknown = 0L;

    /**
     * 空闲
     */
    @Schema(description = "空闲")
    private Long idle = 0L;

    /**
     * 充电中
     */
    @Schema(description = "充电")
    private Long charge = 0L;

    /**
     * 放电中
     */
    @Schema(description = "放电")
    private Long discharge = 0L;

    /**
     * 占用
     */
    @Schema(description = "占用")
    private Long employ = 0L;

    /**
     * 未注册
     */
    @Schema(description = "未注册")
    private Long unregistered = 0L;

    /**
     * 离线
     */
    @Schema(description = "离线")
    private Long offline = 0L;

    /**
     * 故障
     */
    @Schema(description = "故障")
    private Long fault = 0L;

    /**
     * 预约中
     */
    @Schema(description = "预约中")
    private Long reservation = 0L;

    /**
     * 站点电桩数据
     */
    @Schema(description = "站点电桩数据")
    private List<SitePile> sitePileList = Lists.newArrayList();

    @Data
    @Schema(description = "站点电桩数据")
    public static class SitePile {

        /**
         * 站点id
         */
        @Schema(description = "站点id")
        private String siteId;

        /**
         * 站点名称
         */
        @Schema(description = "站点名称")
        private String siteName;

        /**
         * 今日充电量
         */
        @Schema(description = "今日充电量")
        private Double todayCharge = 0.0;

        /**
         * 今日充电金额
         */
        @Schema(description = "今日充电金额")
        private BigDecimal todayChargeMoney = new BigDecimal("0.0");

        /**
         * 电桩枪数据
         */
        @Schema(description = "电桩数据")
        private List<PileGun> pileGunList = Lists.newArrayList();

    }

    @Data
    @Schema(description = "电桩枪数据")
    public static class PileGun {

        /**
         * 电桩设备id
         */
        @Schema(description = "电桩设备id")
        private String pileId;

        /**
         * 电桩编号
         */
        @Schema(description = "电桩编号")
        private String pileCode;

        /**
         * 电枪编号
         */
        @Schema(description = "电枪编号")
        private String gunCode;

        /**
         * 电桩类型 28-交流 29-直流 30-V2G
         */
        @Schema(description = "电桩类型 28-交流 29-直流 30-V2G")
        private String pileType;

        /**
         * 额定功率
         */
        @Schema(description = "额定功率")
        private Double ratedPower;

        /**
         * soc
         */
        @Schema(description = "soc")
        private Integer batterySOC;

        /**
         * 状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
         */
        @Schema(description = "状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
        private Integer gunWorkState;

        /**
         * 实时数据原始电枪状态 枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障
         */
        @Schema(description = "实时数据原始电枪状态 枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障")
        private Integer originalGunWorkState;

    }

}
