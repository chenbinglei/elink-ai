package com.sunmax.protocol.vo.platform;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileStartVo", description = "电桩启动参数实体类")
public class PlatformPileStartVo {

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "充电桩编号", required = true)
    private String pileCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "充电桩枪编号", required = true)
    private Integer gunCode;

    /**
     * 充放电策略 0-自动充满 1-soc电量 2-金额 3-电量
     */
    @ApiModelProperty(value = "充放电策略 0-自动充满 1-soc电量 2-金额 3-电量", required = true)
    private Integer strategy;

    /**
     * 充放电策略参数 金额 电量
     */
    @ApiModelProperty(value = "充放电策略参数", required = true)
    private Double strategyCfg;

    /**
     * 充放电方向 1-充电模式 2-放电模式
     */
    @ApiModelProperty(value = "充放电方向 0-充电模式 1-放电模式", required = true)
    private Integer direction;

    /**
     * 账户类型 1-充/放电卡 2-VIN码 3-手机号
     */
    @ApiModelProperty(value = "账户类型 1-充/放电卡 2-VIN码 3-手机号", required = true)
    private Integer accountType;

    /**
     * 账户数据
     */
    @ApiModelProperty(value = "账户数据", required = true)
    private String accountData;

}
