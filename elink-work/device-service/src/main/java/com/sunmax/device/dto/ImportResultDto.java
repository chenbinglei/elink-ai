package com.sunmax.device.dto;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据导入返回实体类
 */
@Data
@Schema(description = "ImportResultDto")
public class ImportResultDto {

    /**
     * 正常数量
     */
    @Schema(description = "正常数量")
    private Integer normalNum = 0;

    /**
     * 异常数量
     */
    @Schema(description = "异常数量")
    private Integer errorNum = 0;

    /**
     * 异常错误数据
     */
    @Schema(description = "异常错误数据")
    private List<Map<String, String>> errDataList = Lists.newArrayList();

    /**
     * 异常错误描述
     */
    @Schema(description = "异常错误描述")
    private List<String> errDescList = Lists.newArrayList();

}
