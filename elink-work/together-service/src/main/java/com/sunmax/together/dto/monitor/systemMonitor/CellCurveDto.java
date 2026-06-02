package com.sunmax.together.dto.monitor.systemMonitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@ApiModel(value = "CellCurveDto", description = "电芯曲线返回实体类")
public class CellCurveDto {

    /**
     * 时间列表
     */
    @ApiModelProperty(value = "时间列表")
    private Set<String> dateList = Sets.newHashSet();

    /**
     * 数据列表
     */
    @ApiModelProperty(value = "数据列表", required = true)
    private List<Object> dataList = Lists.newArrayList();

}
