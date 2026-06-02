package com.sunmax.device.dto.webserver;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "SiteLedgerDto", description = "站点设备台账数据")
public class SiteLedgerDto {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 站点编码
     */
    @ApiModelProperty(value = "站点编码")
    private String siteCode;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @ApiModelProperty(value = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 站点描述
     */
    @ApiModelProperty(value = "站点描述")
    private String siteDescribe;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    /**
     * 站点扩展字段数据列表
     */
    @ApiModelProperty(value = "站点扩展字段数据列表")
    private List<FieldDataDto> fieldDataList = Lists.newArrayList();

    /**
     * 设备信息数据
     */
    @ApiModelProperty(value = "设备信息数据")
    private List<DeviceLedgerDto> deviceDataList = Lists.newArrayList();


}
