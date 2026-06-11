package com.sunmax.together.dto.monitor.systemMonitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Schema(description = "电芯曲线返回实体类")
public class CellCurveDto {

    /**
     * 时间列表
     */
    @Schema(description = "时间列表")
    private Set<String> dateList = Sets.newHashSet();

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<Object> dataList = Lists.newArrayList();

}
