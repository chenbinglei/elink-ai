package com.sunmax.device.dto.webserver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "站点设备台账数据")
public class SiteLedgerDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 站点编码
     */
    @Schema(description = "站点编码")
    private String siteCode;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @Schema(description = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 站点描述
     */
    @Schema(description = "站点描述")
    private String siteDescribe;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private String tenantId;

    /**
     * 租户名称
     */
    @Schema(description = "租户名称")
    private String tenantName;

    /**
     * 站点扩展字段数据列表
     */
    @Schema(description = "站点扩展字段数据列表")
    private List<FieldDataDto> fieldDataList = Lists.newArrayList();

    /**
     * 设备信息数据
     */
    @Schema(description = "设备信息数据")
    private List<DeviceLedgerDto> deviceDataList = Lists.newArrayList();


}
