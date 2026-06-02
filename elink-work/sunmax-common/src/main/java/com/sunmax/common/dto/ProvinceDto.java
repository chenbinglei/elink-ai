package com.sunmax.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2022/6/811:14
 * @version: 1.0
 * @注释: 省份返回实体类
 */
@Data
@ApiModel("ProvinceDto")
public class ProvinceDto {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private Long id;

    /**
     * 全国省id
     */
    @ApiModelProperty("全国省id")
    private String provinceId;

    /**
     * 全国省名称
     */
    @ApiModelProperty("全国省名称")
    private String provinceName;
}
