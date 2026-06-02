package com.sunmax.system.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.ChargePlatformInfoDto;
import com.sunmax.common.dto.system.DataForwardDto;
import com.sunmax.common.dto.system.OperatorInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.dto.*;
import com.sunmax.system.vo.*;

import java.util.List;
import java.util.Map;

public interface ConfigureCenterService {

    /**
     * 添加或修改产品信息
     * @param productVo
     * @return
     */
    ResponseResult<String> saveOrUpdateProduct(ProductChangeVo productVo);

    /**
     * 查询产品列表
     * @param productId
     * @param keyWords
     * @return
     */
    ResponseResult<List<ProductListDto>> queryProductList(String productId, String keyWords);

    /**
     * 根据模块id删除模块数据
     * @param id
     * @return
     */
    ResponseResult<String> deleteModuleById(String id);

    /**
     * 根据模块id删除模块数据
     * @param permissionVo
     * @return
     */
    ResponseResult<String> saveOrUpdatePermission(PermissionChangeVo permissionVo);

    /**
     * 根据模块id查询权限数据
     *
     * @param moduleId
     * @return
     */
    ResponseResult<List<PermissionListDto>> findPermissionByModuleId(String moduleId);

    /**
     * 根据模块id查询权限数据
     * @param id
     * @return
     */
    ResponseResult<String> deletePermissionById(String id);

    /**
     * 添加或修改平台信息
     * @param chargePlatformInfoVo
     * @return
     */
    ResponseResult<String> saveOrUpdatePlatformInfo(ChargePlatformInfoVo chargePlatformInfoVo);

    /**
     * 根据平台id删除平台信息
     * @param id
     * @return
     */
    ResponseResult<String> deletePlatformInfoById(String id);

    /**
     * 分页查询充电平台列表信息
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    ResponseResult<?> findPlatformInfoListByPage(String keyword, Integer page, Integer size);

    /**
     * 根据多个平台标识查询平台信息
     * @param platformLogoList
     * @return
     */
    ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByLogos(List<String> platformLogoList);

    /**
     * 根据多个平台id查询平台信息
     * @param platformIdList
     * @return 平台id -> 平台信息
     */
    ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByIds(List<String> platformIdList);

    /**
     * 获取接入协议列表
     * @return 协议数据列表
     */
    ResponseResult<List<ProtocolListDto>> getProtocolList();

    /**
     * 根据协议标识查询协议字段列表
     * @param code 协议标识
     * @return 协议字段列表
     */
    ResponseResult<List<ProtocolFieldDto>> findProtocolFieldByCode(String code);

    /**
     * 新建或编辑数据转发数据
     * @param dataForwardVo 数据转发编辑参数实体类
     * @return 状态码
     */
    ResponseResult<Void> saveDataForward(DataForwardChangeVo dataForwardVo);

    /**
     * 查询数据转发列表数据
     * @param keyword 关键词
     * @param page 当前页
     * @param size 当前页条数
     * @return 数据转发列表数据
     */
    ResponseResult<PageDto<DataForwardListDto>> queryDataForwardList(String keyword, Integer page, Integer size);

    /**
     * 根据主键id查询数据转发详情数据
     * @param id 主键id
     * @return 数据转发详情数据
     */
    ResponseResult<DataForwardDetailDto> findDataForwardById(String id);

    /**
     * 更新数据转发状态
     * @param id 主键id
     * @param status 状态 1-启用 2-断开
     * @return 状态码
     */
    ResponseResult<Void> updateForwardStatus(String id, Integer status);

    /**
     * 根据主键id删除数据转发数据
     * @param id 主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteDataForwardById(String id);

    /**
     * 根据数据转发id查询数据配置数据
     * @param forwardId 数据转发id
     * @return 数据配置数据
     */
    ResponseResult<List<DataConfigListDto>> findDataConfigByForwardId(String forwardId);

    /**
     * 批量编辑数据配置数据
     * @param forwardId 数据转发id
     * @param dataConfigVos 多个数据配置参数
     * @return 状态码
     */
    ResponseResult<Void> batchUpdateDataConfig(String forwardId, List<DataConfigChangeVo> dataConfigVos);

    /**
     * 查询数据转发列表数据
     * @param protocolType 协议类型 1-MQTT 2-Http
     * @param status 状态 1-启用 2-断开
     * @return 转发数据列表
     */
    ResponseResult<List<DataForwardDto>> getDataForwardList(Integer protocolType, Integer status);

    /**
     * 保存或编辑运营商信息
     * @param operatorInfoVo
     * @return
     */
    ResponseResult<String> saveOrUpdateOperatorInfo(OperatorInfoVo operatorInfoVo);

    /**
     * 根据id查询运营商信息
     * @param operatorId
     * @return
     */
    ResponseResult<OperatorInfoDto> findOperatorDetailsById(String operatorId);

    /**
     * 根据id删除运营商信息
     * @param operatorId
     * @return
     */
    ResponseResult<String> deleteOperatorInfoById(String operatorId);

    /**
     * 分页查询运营商信息
     * @param operatorListQueryVo
     * @return
     */
    ResponseResult<PageDto<OperatorInfoDto>> findOperatorInfoByPage(OperatorListQueryVo operatorListQueryVo);

    /**
     * 查询所有运营商列表
     * @return 运营商列表
     */
    ResponseResult<List<OperatorInfoDto>> findAllOperatorInfoList();

    /**
     * 根据客户端id查询数据转发信息
     * @param clientId 客户端id
     * @return 数据转发数据
     */
    ResponseResult<DataForwardDto> findDataForwardByClientId(String clientId);


}
