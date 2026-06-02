package com.sunmax.device.vo.firmware;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@ApiModel("FirmwareQueryVo")
public class FirmwareQueryVo {

    /**
     * 设备类型id
     */
    @ApiModelProperty("设备类型id")
    private String typeId;

    /**
     * 关建词
     */
    @ApiModelProperty("关键词")
    private String keyword;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数 ")
    @Min(value = 1,message = "当前页条数不能小于1")
    private Integer size;

    /**
     * 当前页数
     */
    @ApiModelProperty(value = "当前页数")
    @Min(value = 1,message = "页数不能小于1")
    private Integer page;

}
