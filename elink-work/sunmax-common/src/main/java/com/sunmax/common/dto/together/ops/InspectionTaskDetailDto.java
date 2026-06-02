package com.sunmax.common.dto.together.ops;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "InspectionTaskDetailDto", description = "巡检任务详情实体类")
public class InspectionTaskDetailDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * 任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结
     */
    @ApiModelProperty(value = "任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结")
    private Integer taskStatus;

    /**
     * 巡检场站列表
     */
    @ApiModelProperty(value = "巡检场站列表")
    private List<InspectionSiteDto> inspectionSiteList = Lists.newArrayList();

    /**
     * 巡检记录列表
     */
    @ApiModelProperty(value = "巡检记录列表")
    private List<InspectionRecordDto> inspectionRecordList = Lists.newArrayList();

    @Data
    @ApiModel(value = "InspectionSiteDto", description = "巡检站点列表返回实体类")
    public static class InspectionSiteDto {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private String id;

        /**
         * 场站id
         */
        @ApiModelProperty(value = "场站id")
        private String siteId;

        /**
         * 场站名称
         */
        @ApiModelProperty(value = "场站名称")
        private String siteName;

        /**
         * 经度
         */
        @ApiModelProperty(value = "经度")
        private String longitude;

        /**
         * 纬度
         */
        @ApiModelProperty(value = "纬度")
        private String latitude;

        /**
         * 巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃
         */
        @ApiModelProperty(value = "巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃")
        private Integer status;

        /**
         * 完成时间
         */
        @ApiModelProperty(value = "完成时间")
        private String finishTime;

        /**
         * 异常数量
         */
        @ApiModelProperty(value = "异常数量")
        private Long exceptionNum;

    }

    @Data
    @ApiModel(value = "InspectionRecordDto", description = "巡检记录列表返回实体类")
    public static class InspectionRecordDto {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private String id;

        /**
         * 节点名称
         */
        @ApiModelProperty(value = "节点名称")
        private String nodeName;

        /**
         * 处理结果 1-已提交 2-已退回 3-已交接
         */
        @ApiModelProperty(value = "处理结果 1-已提交 2-已退回 3-已交接")
        private Integer result;

        /**
         * 创建人名称
         */
        @ApiModelProperty(value = "创建人名称")
        private String createName;

        /**
         * 创建时间
         */
        @ApiModelProperty("创建时间")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        /**
         * 流转意见
         */
        @ApiModelProperty(value = "流转意见")
        private String flowOpinion;

    }

}
