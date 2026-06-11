package com.sunmax.device.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模型扩展属性查询类
 */
@Data
@Schema(description = "模型扩展属性查询类")
public class ReaQueryVo {

    /**
     * 关联资产分类id
     */
    @Schema(description = "关联资产分类id")
    private String typeId;

    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;

    /**
     * 扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间
     */
    @Schema(description = "扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间")
    private Integer reaType;

//    /**
//     * 读写类型 1-只读 2-读写
//     */
//    @Schema(description = "读写类型 1-只读 2-读写")
//    private Integer readWriteType;

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

}
