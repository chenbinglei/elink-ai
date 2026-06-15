package com.sunmax.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2022/6/811:14
 * @version: 1.0
 * @注释: 省份返回实体类
 */
@Data
@Schema(description = "ProvinceDto")
public class ProvinceDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private Long id;

    /**
     * 全国省id
     */
    @Schema(description = "全国省id")
    private String provinceId;

    /**
     * 全国省名称
     */
    @Schema(description = "全国省名称")
    private String provinceName;
}
