package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 固件下发参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("firmwareIssuedVo")
public class FirmwareIssuedVo {

    /**
     * 充电桩编码
     */
    @ApiModelProperty(value = "充电桩编码", required = true)
    private String pileCode;

    /**
     * 强制升级类型 0-不强制 1-强制
     */
    @ApiModelProperty(value = "强制升级类型 0-不强制 1-强制", required = true)
    private Integer upgradeType;

    /**
     * 新固件主版本号
     */
    @ApiModelProperty(value = "新固件主版本号", required = true)
    private String firmwareMajorVersion;

    /**
     * 新固件次版本号
     */
    @ApiModelProperty(value = "新固件次版本号", required = true)
    private String firmwareMinorVersion;

}
