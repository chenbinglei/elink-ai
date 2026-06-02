package com.sunmax.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2022/6/811:19
 * @version: 1.0
 * @注释: 区级数据返回实体类
 */
@Data
@ApiModel("CityDto")
public class AreaDto {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private Long id;

    /**
     * 全国区县id
     */
    @ApiModelProperty("全国区县id")
    private String areaId;

    /**
     * 全国区县名称
     */
    @ApiModelProperty("全国区县名称")
    private String areaName;

    /**
     * 全国城市id
     */
    @ApiModelProperty("全国城市id")
    private String cityId;
}
