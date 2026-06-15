package com.sunmax.together.dto.energy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "策略数据返回实体类")
public class StrategyDataDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 配置文件内容
     */
    @Schema(description = "模板文件内容")
    private String templateContent;

    /**
     * 配置文件内容
     */
    @Schema(description = "配置文件内容")
    private String configContent;

    /**
     * 下发时间
     */
    @Schema(description = "下发时间")
    private String issueTime;

}
