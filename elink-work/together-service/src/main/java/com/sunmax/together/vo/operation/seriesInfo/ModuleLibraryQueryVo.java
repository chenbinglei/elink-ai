package com.sunmax.together.vo.operation.seriesInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ModuleLibraryQueryVo", description = "组件库查询参数")
public class ModuleLibraryQueryVo {

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数(传0不分页，返回所有列表)", required = true)
    private Integer size;

    /**
     * 组件厂家模糊查询
     */
    @ApiModelProperty(value = "组件厂家模糊查询")
    private String moduleFactory;

    /**
     * 组件型号模糊查询
     */
    @ApiModelProperty(value = "组件型号模糊查询")
    private String moduleModel;

    /**
     * 组件类型 1-多晶 2-单晶 3-叠瓦 4-P型双面 5-N型双面
     */
    @ApiModelProperty(value = "组件类型 1-多晶 2-单晶 3-叠瓦 4-P型双面 5-N型双面")
    private Integer moduleType;
}
