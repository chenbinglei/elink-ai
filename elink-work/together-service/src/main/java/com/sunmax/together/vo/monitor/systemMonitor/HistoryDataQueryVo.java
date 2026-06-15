package com.sunmax.together.vo.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "历史数据查询条件参数实体类")
public class HistoryDataQueryVo {

    /**
     * 多个功能点数据
     */
    @Schema(description = "多个功能点数据")
    private String functions;

    /**
     * 多个计算节点数据
     */
    @Schema(description = "多个计算节点数据")
    private String nodes;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "开始时间(yyyy-MM-dd HH:mm:ss)")
    private String startTime;

    /**
     * 结束时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    private String endTime;

    /**
     * 数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年
     */
    @Schema(description = "数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年")
    private String timeInterval;

    @Data
    public static class DeviceDataVo {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

        /**
         * 类型 1-站点级 2-设备级
         */
        @Schema(description = "类型 1-站点级 2-设备级")
        private Integer type;

        /**
         * 字段数据
         */
        @Schema(description = "字段数据")
        private List<DeviceFieldVo> fields = Lists.newArrayList();

    }

    @Data
    public static class DeviceFieldVo {

        /**
         * 字段编码
         */
        @Schema(description = "字段编码")
        private String code;

        /**
         * 数组下标(可为空 例如[0,1,2])
         */
        @Schema(description = "数组下标(可为空 例如[0,1,2])")
        private String indexes;
    }

}
