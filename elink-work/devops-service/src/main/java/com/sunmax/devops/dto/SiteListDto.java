package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "电站列表返回实体类")
public class SiteListDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String id;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点位置
     */
    @Schema(description = "站点位置")
    private String location;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)
     */
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)")
    private String scenarioTypes;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 时间列表
     */
    @Schema(description = "时间列表")
    private List<String> timeList = Lists.newArrayList();

    /**
     * 光伏功率列表
     */
    @Schema(description = "光伏功率列表")
    private List<Double> pvPowerList = Lists.newArrayList();

    /**
     * 储能功率列表
     */
    @Schema(description = "储能功率列表")
    private List<Double> storagePowerList = Lists.newArrayList();

    /**
     * 充电桩功率列表
     */
    @Schema(description = "充电桩功率列表")
    private List<Double> pilePowerList = Lists.newArrayList();

}
