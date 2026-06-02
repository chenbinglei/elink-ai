package com.sunmax.together.dto.operation.runScene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "SitePileMonitorDto", description = "站点监视数据")
public class SitePileMonitorDto {

    /**
     * 全部
     */
    @ApiModelProperty("全部")
    private Long whole = 0L;

    /**
     * 未知
     */
    @ApiModelProperty("未知")
    private Long unknown = 0L;

    /**
     * 空闲
     */
    @ApiModelProperty("空闲")
    private Long idle = 0L;

    /**
     * 充电中
     */
    @ApiModelProperty("充电")
    private Long charge = 0L;

    /**
     * 放电中
     */
    @ApiModelProperty("放电")
    private Long discharge = 0L;

    /**
     * 占用
     */
    @ApiModelProperty("占用")
    private Long employ = 0L;

    /**
     * 未注册
     */
    @ApiModelProperty("未注册")
    private Long unregistered = 0L;

    /**
     * 离线
     */
    @ApiModelProperty("离线")
    private Long offline = 0L;

    /**
     * 故障
     */
    @ApiModelProperty("故障")
    private Long fault = 0L;

    /**
     * 预约中
     */
    @ApiModelProperty("预约中")
    private Long reservation = 0L;

    /**
     * 站点电桩数据
     */
    @ApiModelProperty("站点电桩数据")
    private List<SitePile> sitePileList = Lists.newArrayList();

    @Data
    @ApiModel(value = "SitePile", description = "站点电桩数据")
    public static class SitePile {

        /**
         * 站点id
         */
        @ApiModelProperty(value = "站点id")
        private String siteId;

        /**
         * 站点名称
         */
        @ApiModelProperty(value = "站点名称")
        private String siteName;

        /**
         * 今日充电量
         */
        @ApiModelProperty(value = "今日充电量")
        private Double todayCharge = 0.0;

        /**
         * 今日充电金额
         */
        @ApiModelProperty(value = "今日充电金额")
        private BigDecimal todayChargeMoney = new BigDecimal("0.0");

        /**
         * 电桩枪数据
         */
        @ApiModelProperty(value = "电桩数据")
        private List<PileGun> pileGunList = Lists.newArrayList();

    }

    @Data
    @ApiModel(value = "PileGun", description = "电桩枪数据")
    public static class PileGun {

        /**
         * 电桩设备id
         */
        @ApiModelProperty("电桩设备id")
        private String pileId;

        /**
         * 电桩编号
         */
        @ApiModelProperty("电桩编号")
        private String pileCode;

        /**
         * 电枪编号
         */
        @ApiModelProperty("电枪编号")
        private String gunCode;

        /**
         * 电桩类型 28-交流 29-直流 30-V2G
         */
        @ApiModelProperty("电桩类型 28-交流 29-直流 30-V2G")
        private String pileType;

        /**
         * 额定功率
         */
        @ApiModelProperty("额定功率")
        private Double ratedPower;

        /**
         * soc
         */
        @ApiModelProperty("soc")
        private Integer batterySOC;

        /**
         * 状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
         */
        @ApiModelProperty("状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
        private Integer gunWorkState;

        /**
         * 实时数据原始电枪状态 枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障
         */
        @ApiModelProperty("实时数据原始电枪状态 枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障")
        private Integer originalGunWorkState;

    }

}
