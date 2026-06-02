package com.sunmax.configure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GraphChangeVo", description = "图模编辑参数实体类")
public class GraphChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    private String name;

    /**
     * 类型 1-文件夹 2-图模文件
     */
    @ApiModelProperty(value = "类型 1-文件夹 2-图模文件", required = true)
    private Integer type;

    /**
     * 状态 0-无 1-有更新 2-已发布
     */
    @ApiModelProperty(value = "状态 0-无 1-有更新 2-已发布")
    private Integer status;

    /**
     * 锁定状态 0-未锁定 1-锁定
     */
    @ApiModelProperty(value = "锁定状态 0-未锁定 1-锁定")
    private Integer lockStatus;

    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id")
    private String parentId;

    /**
     * 域名id
     */
    @ApiModelProperty(value = "域名id")
    private String domainId;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 设备变量标识
     */
    @ApiModelProperty(value = "设备变量标识")
    private String deviceVariables;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 编辑类型 1-新增 2-移动 3-重命名 4-锁定状态 5-保存 6-发布 7-关联站点
     */
    @ApiModelProperty(value = "编辑类型 1-新增 2-移动 3-重命名 4-锁定状态 5-保存 6-发布 7-关联站点")
    private Integer updateType;

}
