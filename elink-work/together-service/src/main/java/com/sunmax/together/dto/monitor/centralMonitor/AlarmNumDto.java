package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "告警数量返回实体类")
public class AlarmNumDto {

    /**
     * 数据id
     */
    @Schema(description = "数据id")
    private String dataId;

    /**
     * 数据名称
     */
    @Schema(description = "数据名称")
    private String dataName;

    /**
     * 告警数量
     */
    @Schema(description = "告警数量")
    @Builder.Default
    private Long alarmNum = 0L;

}
