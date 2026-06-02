package com.sunmax.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "OrganEmpowerListDto", description = "资产授权列表信息返回实体类")
public class OrganEmpowerListDto {


    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 权限 1-只读 2-读写
     */
    @ApiModelProperty(value = "权限 1-只读 2-读写")
    private Integer authority;
}
