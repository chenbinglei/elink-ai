package com.sunmax.device.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "事件存储数据实体类")
public class EventStoreDataDto {

    /**
     * 功能点标识
     */
    private String functionLogo;

    /**
     * 点位
     */
    private int point;

    /**
     * 位运算结果 0或1
     */
    private int result;

}
