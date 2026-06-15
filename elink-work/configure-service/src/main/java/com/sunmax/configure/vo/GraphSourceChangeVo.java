package com.sunmax.configure.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "图源关联数据源编辑参数实体类")
public class GraphSourceChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 关联图模id
     */
    @Schema(description = "关联图模id")
    private String graphId;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 数据源id
     */
    @Schema(description = "数据源id")
    private String dataSourceId;

    /**
     * 请求参数Value
     */
    @Schema(description = "请求参数Value")
    private String requestValue;

    /**
     * 响应参数
     */
    @Schema(description = "响应参数")
    private String responseValue;

    /**
     * 编辑类型 1-新增 2-编辑 3-删除
     */
    @Schema(description = "编辑类型 1-新增 2-编辑 3-删除")
    private Integer updateType;

}
