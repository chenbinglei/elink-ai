package com.sunmax.common.vo.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "设备系统变量节点数据查询实体类")
public class VarNodeValueVo {

    /**
     * 多个变量编码
     */
    @Schema(description = "多个变量编码")
    private List<String> varCodeList;

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
     * 多个站点/设备id
     */
    @Schema(description = "多个站点/设备id")
    private List<String> deviceIdList;

    /**
     * 查询条数
     */
    @Schema(description = "查询条数")
    private Integer limitSize;

    /**
     * 索引号
     */
    @Schema(description = "索引号")
    private Integer index;

    /**
     * 时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年
     */
    @Schema(description = "时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年")
    private String timeInterval;
}
