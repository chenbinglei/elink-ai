package com.sunmax.device.vo.firmware;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;

@Data
@Schema(description = "FirmwareQueryVo")
public class FirmwareQueryVo {

    /**
     * 设备类型id
     */
    @Schema(description = "设备类型id")
    private String typeId;

    /**
     * 关建词
     */
    @Schema(description = "关键词")
    private String keyword;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数 ")
    @Min(value = 1,message = "当前页条数不能小于1")
    private Integer size;

    /**
     * 当前页数
     */
    @Schema(description = "当前页数")
    @Min(value = 1,message = "页数不能小于1")
    private Integer page;

}
