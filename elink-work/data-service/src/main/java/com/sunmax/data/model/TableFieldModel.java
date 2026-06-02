package com.sunmax.data.model;

import lombok.Data;

/**
 * 表字段实体类
 */
@Data
public class TableFieldModel {

    /**
     * 字段名称
     */
    private String field;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段长度
     */
    private Integer length;

    /**
     * 标签
     */
    private String note;

}
