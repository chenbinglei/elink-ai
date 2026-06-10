package com.sunmax.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "点表编辑参数实体类")
public class PointTableVo {

    /**
     * 数据点号
     */
    @Schema(description = "数据点号")
    private Long dataId;

    /**
     * 数据类型
     */
    @Schema(description = "数据类型")
    private Integer dataType;

    /**
     * 更新类型 1-新增 2-编辑 3-删除
     */
    @Schema(description = "更新类型 1-新增 2-编辑 3-删除")
    private Integer updateType;


}
