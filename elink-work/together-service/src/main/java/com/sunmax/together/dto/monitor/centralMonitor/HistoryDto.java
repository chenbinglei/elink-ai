package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "曲线返回实体类")
public class HistoryDto {

    /**
     * 时间
     */
    @Schema(description = "时间")
    private String time;

    /**
     * 值
     */
    @Schema(description = "值")
    private Object value;

}
