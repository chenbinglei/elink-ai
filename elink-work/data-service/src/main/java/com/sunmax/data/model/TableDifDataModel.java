package com.sunmax.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableDifDataModel {

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * last字段值
     */
    private Object lastFieldValue;

    /**
     * last时间
     */
    private String lastDataTime;

    /**
     * first字段值
     */
    private Object firstFieldValue;

    /**
     * first时间
     */
    private String firstDataTime;
}
