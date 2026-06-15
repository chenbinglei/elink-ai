package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/10/717:14
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "运营商列表查询实体类")
public class OperatorListQueryVo {

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

    /**
     * 运营商名称
     */
    @Schema(description = "运营商名称")
    private String operatorName;
}
