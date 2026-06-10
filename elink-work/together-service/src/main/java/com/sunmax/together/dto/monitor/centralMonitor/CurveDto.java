package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "曲线返回实体类")
public class CurveDto {

    /**
     * 曲线名称
     */
    @Schema(description = "曲线名称")
    private String name;

    /**
     * 功率曲线
     */
    @Schema(description = "功率曲线")
    private List<HistoryDto> data;
}
