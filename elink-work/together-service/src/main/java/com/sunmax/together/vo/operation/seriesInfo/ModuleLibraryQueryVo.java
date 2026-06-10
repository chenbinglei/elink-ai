package com.sunmax.together.vo.operation.seriesInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "组件库查询参数")
public class ModuleLibraryQueryVo {

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数(传0不分页，返回所有列表)")
    private Integer size;

    /**
     * 组件厂家模糊查询
     */
    @Schema(description = "组件厂家模糊查询")
    private String moduleFactory;

    /**
     * 组件型号模糊查询
     */
    @Schema(description = "组件型号模糊查询")
    private String moduleModel;

    /**
     * 组件类型 1-多晶 2-单晶 3-叠瓦 4-P型双面 5-N型双面
     */
    @Schema(description = "组件类型 1-多晶 2-单晶 3-叠瓦 4-P型双面 5-N型双面")
    private Integer moduleType;
}
