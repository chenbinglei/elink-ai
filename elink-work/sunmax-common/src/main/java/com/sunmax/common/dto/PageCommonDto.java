package com.sunmax.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "PageCommonDto")
public class PageCommonDto {

    /**
     * 所有id
     */
    @Schema(description = "所有id")
    private List<?> ids;

    /**
     * 分页展示数据
     */
    @Schema(description = "分页展示数据")
    private PageDto<?> pageDto = new PageDto<>();

}
