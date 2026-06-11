package com.sunmax.common.vo.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "巡检站点报告编辑实体类")
public class AppInspectSiteVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃
     */
    @Schema(description = "巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃")
    private Integer status;

    /**
     * 多个巡检项检查状态(检查状态 1-未检查 2-正常 3-异常) {"巡检项id1": "检查状态","巡检项id2": "检查状态"}
     */
    @Schema(description = "多个巡检项检查状态(检查状态 1-未检查 2-正常 3-异常) {\"巡检项id1\": \"检查状态\",\"巡检项id2\": \"检查状态\"}")
    private String itemStates;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 删除路径 用逗号分隔
     */
    @Schema(description = "删除路径 用逗号分隔")
    private String deletePaths;

}
