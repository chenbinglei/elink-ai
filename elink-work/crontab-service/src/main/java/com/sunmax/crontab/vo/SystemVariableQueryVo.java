package com.sunmax.crontab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统变量列表查询信息参数")
public class SystemVariableQueryVo {

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;

    /**
     * 关键字类型 1-变量名称 2-变量标识
     */
    @Schema(description = "关键字类型 1-变量名称 2-变量标识")
    private Integer keywordType;

    /**
     * 变量类型 1-设备类型 2-站点类型
     */
    @Schema(description = "变量类型 1-设备类型 2-站点类型")
    private String varType;
}
