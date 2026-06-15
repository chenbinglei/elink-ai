package com.sunmax.crontab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "计算节点编辑信息参数")
public class SystemVariableChangeVo {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 当前用户id
     */
    @Schema(description = "当前用户id")
    private String userId;

    /**
     * 变量名称
     */
    @Schema(description = "变量名称")
    private String varName;

    /**
     * 变量标识
     */
    @Schema(description = "变量标识")
    private String varCode;

    /**
     * 变量类型 1-设备类型 2-站点类型
     */
    @Schema(description = "变量类型 1-设备类型 2-站点类型")
    private Integer varType;

    /**
     * 数据来源 1-计算节点 2-模型功能点
     */
    @Schema(description = "数据来源 1-计算节点 2-模型功能点")
    private Integer dataSource;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
