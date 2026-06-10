package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 固件下发参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "firmwareIssuedVo")
public class FirmwareIssuedVo {

    /**
     * 充电桩编码
     */
    @Schema(description = "充电桩编码")
    private String pileCode;

    /**
     * 强制升级类型 0-不强制 1-强制
     */
    @Schema(description = "强制升级类型 0-不强制 1-强制")
    private Integer upgradeType;

    /**
     * 新固件主版本号
     */
    @Schema(description = "新固件主版本号")
    private String firmwareMajorVersion;

    /**
     * 新固件次版本号
     */
    @Schema(description = "新固件次版本号")
    private String firmwareMinorVersion;

}
