package com.sunmax.common.vo.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AppInspectSiteVo", description = "巡检站点报告编辑实体类")
public class AppInspectSiteVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id", required = true)
    private String id;

    /**
     * 巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃
     */
    @ApiModelProperty(value = "巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃", required = true)
    private Integer status;

    /**
     * 多个巡检项检查状态(检查状态 1-未检查 2-正常 3-异常) {"巡检项id1": "检查状态","巡检项id2": "检查状态"}
     */
    @ApiModelProperty(value = "多个巡检项检查状态(检查状态 1-未检查 2-正常 3-异常) {\"巡检项id1\": \"检查状态\",\"巡检项id2\": \"检查状态\"}")
    private String itemStates;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 删除路径 用逗号分隔
     */
    @ApiModelProperty(value = "删除路径 用逗号分隔")
    private String deletePaths;

}
