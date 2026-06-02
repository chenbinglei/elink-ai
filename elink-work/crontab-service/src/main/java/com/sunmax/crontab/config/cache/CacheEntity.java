package com.sunmax.crontab.config.cache;

import com.sunmax.common.dto.crontab.LocalCacheDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
*本地缓存保存的实体
*/
@Data
@ApiModel("CacheEntity")
public class CacheEntity implements Serializable {

    private static final long serialVersionUID = 7172649826282703560L;

    /**
     * 值
     */
    @ApiModelProperty("值")
    private LocalCacheDto value;

    /**
     * 保存的时间戳
     */
    @ApiModelProperty("保存的时间戳")
    private long gmtModify;

    /**
     * 过期时间
     */
    @ApiModelProperty("过期时间")
    private int expire;

    public CacheEntity(LocalCacheDto value, long gmtModify, int expire) {
        super();
        this.value = value;
        this.gmtModify = gmtModify;
        this.expire = expire;
    }
}