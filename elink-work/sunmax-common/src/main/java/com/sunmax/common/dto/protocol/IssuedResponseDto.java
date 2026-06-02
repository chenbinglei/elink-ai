package com.sunmax.common.dto.protocol;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 下发响应结果返回实体类
 */
@Data
public class IssuedResponseDto {

    /**
     * 充电桩编号
     */
    @ApiModelProperty("充电桩编号")
    private String pileCode;

    /**
     * 计费模板id
     */
    @ApiModelProperty("计费模板id")
    private String billTemplateId;

    /**
     * 下发状态 0-下发成功 1-下发失败
     */
    @ApiModelProperty("下发状态 0-下发成功 1-下发失败")
    private Integer issuedStatus;

}
