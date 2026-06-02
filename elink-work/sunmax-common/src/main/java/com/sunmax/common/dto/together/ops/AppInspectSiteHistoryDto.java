package com.sunmax.common.dto.together.ops;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "AppInspectSiteHistoryDto", description = "历史巡检任务站点列表返回实体类")
public class AppInspectSiteHistoryDto {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 巡检次数
     */
    @ApiModelProperty(value = "巡检次数")
    private Integer inspectCount;

    /**
     * 距离上次巡检天数
     */
    @ApiModelProperty(value = "距离上次巡检天数")
    private Long distanceDay;

    /**
     * 巡检人
     */
    @ApiModelProperty(value = "巡检人")
    private String userName;

    /**
     * 完成时间
     */
    @ApiModelProperty(value = "完成时间")
    private String finishTime;

    /**
     * 巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃
     */
    @ApiModelProperty(value = "巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃")
    private Integer status;

    /**
     * 站点历史列表
     */
    @ApiModelProperty(value = "站点历史列表")
    private List<AppSiteHistory> siteHistoryList = Lists.newArrayList();

    @Data
    @ApiModel(value = "AppSiteHistory", description = "巡检历史返回实体类")
    public static class AppSiteHistory {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private String id;

        /**
         * 巡检人
         */
        @ApiModelProperty(value = "巡检人")
        private String userName;

        /**
         * 巡检时间
         */
        @ApiModelProperty(value = "巡检时间")
        private String inspectTime;

        /**
         * 完成时间
         */
        @ApiModelProperty(value = "完成时间")
        private String finishTime;

        /**
         * 巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃
         */
        @ApiModelProperty(value = "巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃")
        private Integer status;

        /**
         * 异常数量
         */
        @ApiModelProperty(value = "异常数量")
        private Long exceptionNum;

        /**
         * 备注
         */
        @ApiModelProperty(value = "备注")
        private String remark;

        /**
         * 附件路径
         */
        @ApiModelProperty("附件路径")
        private String annexPath;

    }

}
