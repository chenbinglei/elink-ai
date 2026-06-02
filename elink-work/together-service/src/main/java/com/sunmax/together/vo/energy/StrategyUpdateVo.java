package com.sunmax.together.vo.energy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 策略编辑参数实体类
 */
@Data
@ApiModel(value = "StrategyUpdateVo", description = "策略编辑参数实体类")
public class StrategyUpdateVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id", required = true)
    private String id;

    /**
     * 策略名称
     */
    @ApiModelProperty(value = "策略名称", required = true)
    private String strategyName;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

}
