package com.sunmax.configure.vo.storage;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "储能平台数据请求公共参数")
public class StorageCommonVo {

    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    private String appId;

    /**
     * 公钥
     */
    @Schema(description = "公钥")
    private String publicKey;

    /**
     * 私钥
     */
    @Schema(description = "私钥")
    private String privateKey;

    /**
     * url地址
     */
    @Schema(description = "url地址")
    private String baseUrl;

    /**
     * 站点资源编号数据(站点id -> 资源数据)
     */
    @Schema(description = "站点资源编号数据(站点id -> 资源数据)")
    @Builder.Default
    private Map<String, SiteResource> siteResourceMap = Maps.newHashMap();

    @Data
    @Schema(description = "站点资数据返回实体类")
    public static class SiteResource {

        /**
         * 站点id
         */
        @Schema(description = "站点id")
        private String siteId;

        /**
         * 资源编号
         */
        @Schema(description = "资源编号")
        private String resourceNo;

        /**
         * 多个PCS设备模型
         */
        @Schema(description = "多个PCS设备模型")
        private List<DeviceInfo> pcsModels;

        /**
         * 多个电池蔟设备模型
         */
        @Schema(description = "多个电池蔟设备模型")
        private List<DeviceInfo> batteryModels;

    }

    @Data
    public static class DeviceInfo {

        /**
         * 设备id
         */
        @Schema(description = "设备id")
        private String deviceId;

        /**
         * 模型id
         */
        @Schema(description = "模型id")
        private String modelId;

    }

}
