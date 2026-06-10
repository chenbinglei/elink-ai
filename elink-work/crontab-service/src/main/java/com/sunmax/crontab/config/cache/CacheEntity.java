package com.sunmax.crontab.config.cache;

import com.sunmax.common.dto.crontab.LocalCacheDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
*本地缓存保存的实体
*/
@Data
@Schema(description = "CacheEntity")
public class CacheEntity implements Serializable {

    private static final long serialVersionUID = 7172649826282703560L;

    /**
     * 值
     */
    @Schema(description = "值")
    private LocalCacheDto value;

    /**
     * 保存的时间戳
     */
    @Schema(description = "保存的时间戳")
    private long gmtModify;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private int expire;

    public CacheEntity(LocalCacheDto value, long gmtModify, int expire) {
        super();
        this.value = value;
        this.gmtModify = gmtModify;
        this.expire = expire;
    }
}
