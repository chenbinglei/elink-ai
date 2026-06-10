package com.sunmax.common.dto.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 本地缓存实体类
 */
@Data
@Schema(description = "LocalCacheDto")
public class LocalCacheDto implements Serializable {

    /**
     * 缓存时间
     */
    @Schema(description = "缓存时间")
    private String cacheTime;

    /**
     * 数据值
     */
    @Schema(description = "数据值")
    private Double resultValue;

    /**
     * 下次存储至数据库时间
     */
    @Schema(description = "下次存储至数据库时间")
    private String nextStorageTime;
}
