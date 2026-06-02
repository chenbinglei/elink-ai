package com.sunmax.common.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteOperateDto", description = "站点关联运营配置返回实体类")
public class SiteOperateDto {

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id")
    private String siteId;

    /**
     * 所属运营商id
     */
    @ApiModelProperty(value = "所属运营商id")
    private String operateId;

    /**
     * 资源编号
     */
    @ApiModelProperty(value = "资源编号")
    private String resourceSn;

}
