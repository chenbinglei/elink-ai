package com.sunmax.protocol.vo.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩启动参数实体类")
public class PlatformPileStartVo {

    /**
     * 电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "充电桩枪编号")
    private Integer gunCode;

    /**
     * 充放电策略 0-自动充满 1-soc电量 2-金额 3-电量
     */
    @Schema(description = "充放电策略 0-自动充满 1-soc电量 2-金额 3-电量")
    private Integer strategy;

    /**
     * 充放电策略参数 金额 电量
     */
    @Schema(description = "充放电策略参数")
    private Double strategyCfg;

    /**
     * 充放电方向 1-充电模式 2-放电模式
     */
    @Schema(description = "充放电方向 0-充电模式 1-放电模式")
    private Integer direction;

    /**
     * 账户类型 1-充/放电卡 2-VIN码 3-手机号
     */
    @Schema(description = "账户类型 1-充/放电卡 2-VIN码 3-手机号")
    private Integer accountType;

    /**
     * 账户数据
     */
    @Schema(description = "账户数据")
    private String accountData;

}
