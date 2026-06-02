package com.sunmax.together.dto.energy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StrategyDataDto", description = "策略数据返回实体类")
public class StrategyDataDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 配置文件内容
     */
    @ApiModelProperty(value = "模板文件内容")
    private String templateContent;

    /**
     * 配置文件内容
     */
    @ApiModelProperty(value = "配置文件内容")
    private String configContent;

    /**
     * 下发时间
     */
    @ApiModelProperty(value = "下发时间")
    private String issueTime;

}
