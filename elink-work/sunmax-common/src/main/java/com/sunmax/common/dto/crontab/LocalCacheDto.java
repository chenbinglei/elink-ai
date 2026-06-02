package com.sunmax.common.dto.crontab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 本地缓存实体类
 */
@Data
@ApiModel("LocalCacheDto")
public class LocalCacheDto implements Serializable {

    /**
     * 缓存时间
     */
    @ApiModelProperty("缓存时间")
    private String cacheTime;

    /**
     * 数据值
     */
    @ApiModelProperty("数据值")
    private Double resultValue;

    /**
     * 下次存储至数据库时间
     */
    @ApiModelProperty("下次存储至数据库时间")
    private String nextStorageTime;
}
