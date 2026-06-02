package com.sunmax.webapp.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author yqz
 * @description
 */
@Mapper
public interface DictMapper {
    Map<String,Object> getDictByCode(String dict_section_code);
}