package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "设备统计返回实体类")
public class SystemStatisticsDto {

    /**
     * 功能标志
     */
    @Schema(description = "功能标志")
    private String functionLogos;

    /**
     * 故障数量
     */
    @Schema(description = "故障数量")
    private Integer error;

    /**
     * 未注册数量
     */
    @Schema(description = "未注册数量")
    private Integer unregistered;

    /**
     * 离线数量
     */
    @Schema(description = "离线数量")
    private Integer offline;

    /**
     * 正常设备数量
     */
    @Schema(description = "正常设备数量")
    private Integer normal;

    /**
     * 功率曲线列表
     */
    @Schema(description = "功率曲线")
    private List<CurveDto> historys;

}
