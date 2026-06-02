package com.sunmax.device.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.GraphListDto;
import com.sunmax.device.dto.GraphTypeListDto;
import com.sunmax.device.vo.GraphChangeVo;
import com.sunmax.device.vo.GraphTypeChangeVo;

import java.util.List;

public interface VisualService {

    /**
     * 新增编辑图形分类
     * @param graphTypeVo 图形分类编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveGraphType(GraphTypeChangeVo graphTypeVo);

    /**
     * 根据资产分类id查询图形分类列表
     * @param typeId 资产分类id
     * @return 图形分类列表
     */
    ResponseResult<List<GraphTypeListDto>> findGraphTypeListByTypeId(String typeId);

    /**
     * 删除图形分类
     * @param id 主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteGraphTypeById(String id);

    /**
     * 新增或编辑图形
     * @param graphVo 图形编辑参数实体类
     * @return 状态码
     */
    ResponseResult<Void> saveGraph(GraphChangeVo graphVo);

    /**
     * 根据资产分类id查询图形列表
     * @param deviceId 设备id
     * @param page 当前页
     * @param size 当前页条数
     * @return 图形列表数据
     */
    ResponseResult<PageDto<GraphListDto>> findGraphListByDeviceId(String deviceId, Integer page, Integer size);

    /**
     * 删除图形
     * @param id 主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteGraphById(String id);

    /**
     * 根据设备id查询图形关联列表
     * @param deviceId 站点/设备/子系统Id
     * @param graphTypeCode 图形分类标识
     * @return 图形关联列表数据
     */
    ResponseResult<List<GraphListDto>> findGraphRelevancyListByDeviceId(String deviceId, String graphTypeCode);
}
