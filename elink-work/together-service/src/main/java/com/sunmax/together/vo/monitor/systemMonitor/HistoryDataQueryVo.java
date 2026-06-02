package com.sunmax.together.vo.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "HistoryDataQueryVo", description = "历史数据查询条件参数实体类")
public class HistoryDataQueryVo {

    /**
     * 多个功能点数据
     */
    @ApiModelProperty(value = "多个功能点数据")
    private String functions;

    /**
     * 多个计算节点数据
     */
    @ApiModelProperty(value = "多个计算节点数据")
    private String nodes;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)", required = true)
    private String startTime;

    /**
     * 结束时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)", required = true)
    private String endTime;

    /**
     * 数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年
     */
    @ApiModelProperty(value = "数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年", required = true)
    private String timeInterval;

    @Data
    public static class DeviceDataVo {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id", required = true)
        private String id;

        /**
         * 类型 1-站点级 2-设备级
         */
        @ApiModelProperty(value = "类型 1-站点级 2-设备级", required = true)
        private Integer type;

        /**
         * 字段数据
         */
        @ApiModelProperty(value = "字段数据")
        private List<DeviceFieldVo> fields = Lists.newArrayList();

    }

    @Data
    public static class DeviceFieldVo {

        /**
         * 字段编码
         */
        @ApiModelProperty(value = "字段编码", required = true)
        private String code;

        /**
         * 数组下标(可为空 例如[0,1,2])
         */
        @ApiModelProperty(value = "数组下标(可为空 例如[0,1,2])")
        private String indexes;
    }

}
