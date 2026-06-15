package com.sunmax.common.dto.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 下发响应结果返回实体类
 */
@Data
public class IssuedResponseDto {

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 计费模板id
     */
    @Schema(description = "计费模板id")
    private String billTemplateId;

    /**
     * 下发状态 0-下发成功 1-下发失败
     */
    @Schema(description = "下发状态 0-下发成功 1-下发失败")
    private Integer issuedStatus;

}
