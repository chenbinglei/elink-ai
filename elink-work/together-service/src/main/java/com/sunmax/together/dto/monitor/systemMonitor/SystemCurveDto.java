package com.sunmax.together.dto.monitor.systemMonitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 数据查询返回实体类
 */
@Data
@ApiModel(value = "SystemCurveDto", description = "系统曲线数据返回实体类")
public class SystemCurveDto {

    /**
     * 时间列表
     */
    @ApiModelProperty(value = "时间列表")
    private Set<String> dateList = Sets.newHashSet();

    /**
     * 数据信息列表
     */
    @ApiModelProperty(value = "数据信息列表")
    private List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

    /**
     * 数据信息
     */
    @Data
    public static class DataInfo {

        /**
         * 编号
         */
        @ApiModelProperty(value = "编号", required = true)
        private String code;

        /**
         * 名称
         */
        @ApiModelProperty(value = "名称", required = true)
        private String name;

        /**
         * 数据
         */
        @ApiModelProperty(value = "数据列表", required = true)
        private List<Object> dataList = Lists.newArrayList();
    }


}
