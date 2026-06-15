package com.sunmax.common.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "数据转发响应实体类")
public class DataForwardDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 通道名称
     */
    @Schema(description = "通道名称")
    private String channelName;

    /**
     * 接入协议类型 1-mqtt 2-http
     */
    @Schema(description = "接入协议类型 1-mqtt 2-http")
    private Integer protocolType;

    /**
     * 接入协议标识
     */
    @Schema(description = "接入协议标识")
    private String protocolCode;

    /**
     * 动态字段
     */
    @Schema(description = "动态字段")
    private String dynamicFields;

    /**
     * 地址
     */
    @Schema(description = "地址")
    private String address;

    /**
     * 数据配置列表
     */
    @Schema(description = "数据配置列表")
    private List<DataConfigDto> dataConfigList = Lists.newArrayList();

}
