package com.sunmax.configure.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.dto.DataSourceDetailDto;
import com.sunmax.configure.dto.DataSourceListDto;
import com.sunmax.configure.vo.DataSourceChangeVo;

import java.util.List;

public interface DataSourceService {

    /**
     * 新建或编辑数据源
     * @param dataSourceChangeVo  数据源编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveDataSource(DataSourceChangeVo dataSourceChangeVo);

    /**
     * 根据多个id删除数据源
     * @param ids 多个数据源id
     * @return 状态码
     */
    ResponseResult<Void> deleteDataSourceByIds(List<String> ids);

    /**
     * 查询数据源管理列表
     * @param name 名称(查询条件)
     * @return 数据源列表数据
     */
    ResponseResult<List<DataSourceListDto>> queryDataSourceList(String name);

    /**
     * 根据id查询数据源详情数据
     * @param id 数据源id
     * @return 数据源详情数据
     */
    ResponseResult<DataSourceDetailDto> findDataSourceById(String id);

}
