package com.sunmax.together.dto.operation.operationAnalysis;

import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

@Data
public class PvSiteGenerationDto {

    /**
     * 站点id
     */
    private String siteId;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 站点光伏装机量
     */
    private Double pvCapacity = 0.0;

    /**
     * 光伏发电量
     */
    private Double pvGeneration = 0.0;

    /**
     * 光伏等效时长
     */
    private Double pvGenerationTime = 0.0;

    /**
     * 二氧化碳(CO2)减排量(kg)
     */
    private Double co2Reduction;

    /**
     * 节约标煤量(kg)
     */
    private Double standardCoalReduction;

    /**
     * 等效植树量(颗)
     */
    private Double treeReduction;

    /**
     * 光伏发电量数据来源 20-逆变器 65-光伏气象站 66-并网点
     */
    private Integer pvQtSource;

    /**
     * 多个设备id(统计光伏发电量的设备id)
     */
    private List<String> deviceIds = Lists.newArrayList();

    /**
     * 发电量数据(日期 -> 发电量)
     */
    private Map<String, Double> pvGenerationMap = Maps.newHashMap();

    /**
     * 等效发电时长(日期 -> 等效发电时长)
     */
    private Map<String, Double> pvGenerationTimeMap = Maps.newHashMap();

}
