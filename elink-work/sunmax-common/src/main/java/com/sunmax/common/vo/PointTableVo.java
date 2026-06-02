package com.sunmax.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "PointTableVo", description = "点表编辑参数实体类")
public class PointTableVo {

    /**
     * 数据点号
     */
    @ApiModelProperty(value = "数据点号")
    private Long dataId;

    /**
     * 数据类型
     */
    @ApiModelProperty(value = "数据类型")
    private Integer dataType;

    /**
     * 更新类型 1-新增 2-编辑 3-删除
     */
    @ApiModelProperty(value = "更新类型 1-新增 2-编辑 3-删除")
    private Integer updateType;


}
