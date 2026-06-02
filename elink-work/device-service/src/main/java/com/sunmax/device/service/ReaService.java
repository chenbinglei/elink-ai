package com.sunmax.device.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.model.ReaListDto;
import com.sunmax.device.vo.model.ReaChangeVo;
import com.sunmax.device.vo.model.ReaQueryVo;

public interface ReaService {

    /**
     * 新增编辑模型扩展属性
     * @param reaChangeVo 模型扩展属性编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveSea(ReaChangeVo reaChangeVo);

    /**
     * 查询模型扩展属性列表
     * @param reaQueryVo 扩展属性查询条件
     * @return 模型扩展属性列表
     */
    ResponseResult<PageDto<ReaListDto>> querySeaList(ReaQueryVo reaQueryVo);

    /**
     * 删除模型扩展属性
     * @param id 扩展属性id
     * @return 状态码
     */
    ResponseResult<Void> deleteSeaById(String id);

    /**
     * 根据主键id查询扩展属性详情
     * @param id 扩展属性id
     * @return 扩展属性数据
     */
    ResponseResult<ReaListDto> findSeaDetailById(String id);

}
