package com.sunmax.together.vo.energy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 策略编辑参数实体类
 */
@Data
@Schema(description = "策略编辑参数实体类")
public class StrategyUpdateVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 策略名称
     */
    @Schema(description = "策略名称")
    private String strategyName;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

}
