package com.sunmax.together.dto.monitor.systemMonitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 数据查询返回实体类
 */
@Data
@Schema(description = "系统曲线数据返回实体类")
public class SystemCurveDto {

    /**
     * 时间列表
     */
    @Schema(description = "时间列表")
    private Set<String> dateList = Sets.newHashSet();

    /**
     * 数据信息列表
     */
    @Schema(description = "数据信息列表")
    private List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

    /**
     * 数据信息
     */
    @Data
    public static class DataInfo {

        /**
         * 编号
         */
        @Schema(description = "编号")
        private String code;

        /**
         * 名称
         */
        @Schema(description = "名称")
        private String name;

        /**
         * 数据
         */
        @Schema(description = "数据列表")
        private List<Object> dataList = Lists.newArrayList();
    }


}
