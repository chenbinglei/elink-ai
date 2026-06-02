package com.sunmax.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("PageCommonDto")
public class PageCommonDto {

    /**
     * 所有id
     */
    @ApiModelProperty("所有id")
    private List<?> ids;

    /**
     * 分页展示数据
     */
    @ApiModelProperty("分页展示数据")
    private PageDto<?> pageDto = new PageDto<>();

}
