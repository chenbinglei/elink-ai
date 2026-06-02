package com.sunmax.device.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.InterflowDeviceVo;
import com.sunmax.device.dto.GatewaySubDeviceDto;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.device.*;
import com.sunmax.device.dto.model.ModelFieldUpdateDto;
import com.sunmax.device.dto.model.ModelNameDto;
import com.sunmax.device.vo.device.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DeviceService {

    /**
     * 新增编辑设备数据
     * @param deviceChangeVo 设备编辑参数
     * @param imageFiles 多个设备图片文件
     * @return 状态码
     */
    ResponseResult<Void> saveDevice(DeviceChangeVo deviceChangeVo, MultipartFile[] imageFiles);

    /**
     * 批量添加设备数据
     * @param typeId 设备类型id
     * @param modelId 模型id
     * @param siteId 站点id
     * @param parentId 父节点id
     * @param dataFile 设备数据文件
     * @return 导入结果信息
     */
    ResponseResult<ImportResultDto> batchInsertDevice(String userId, String typeId, String modelId, String siteId, String parentId, MultipartFile dataFile);

    /**
     * 根据分类id获取模型名称列表
     * @param typeId 设备类型id
     * @return 模型名称列表
     */
    ResponseResult<List<ModelNameDto>> getModelNameListByTypeId(String typeId);

    /**
     * 根据模型id获取模型编辑字段列表
     * @param modelId 模型id
     * @return 模型编辑字段列表数据
     */
    ResponseResult<List<ModelFieldUpdateDto>> getModelFieldUpdateListByModelId(String modelId);

    /**
     * 查询设备数据列表
     * @param deviceQueryVo 设备查询条件
     * @return 设备数据列表
     */
    ResponseResult<PageDto<DeviceListDto>> queryDeviceList(DeviceQueryVo deviceQueryVo);

    /**
     * 删除设备数据
     * @param id 设备id
     * @param deleteLogo 删除标识 true-删除 false-不删除
     * @return 状态码
     */
    ResponseResult<Void> deleteDeviceById(String id, Boolean deleteLogo);

    /**
     * 根据设备id查询设备基本信息数据
     * @param deviceId 设备id
     * @return 设备基本信息数据
     */
    ResponseResult<DeviceBasicInfoDto> findDeviceBasicInfoById(String deviceId);

    /**
     * 根据设备id查询设备功能属性列表数据
     * @param deviceId 设备id
     * @return 设备功能属性列表数据
     */
    ResponseResult<List<DeviceFunctionDto>> findDeviceFunctionListById(String deviceId);

    /**
     * 根据查询条件查询设备功能点数据
     * @param functionQueryVo 设备功能点查询条件
     * @return 设备功能点数据
     */
    ResponseResult<DeviceFunctionValueDto> queryDeviceFunctionValueList(DeviceFunctionQueryVo functionQueryVo);

    /**
     * 根据多个设备id查询设备功能属性列表数据
     * @param deviceIds 多个设备id
     * @return 功能属性列表数据
     */
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findDeviceFunctionListByIds(Set<String> deviceIds);

    /**
     * 根据设备id查询设备功能属性列表数据
     * @param deviceId 设备id
     * @return 设备功能属性列表数据
     */
    ResponseResult<List<DeviceReaDto>> findDeviceReaListById(String deviceId);

    /**
     * 根据设备id查询设备拓扑图列表
     * @param deviceId 设备id
     * @return 设备拓扑图数据列表
     */
    ResponseResult<List<DeviceNodeListDto>> findDeviceNodeListById(String deviceId);

    /**
     * 查询设备拓扑图编辑列表
     * @param deviceId 设备id
     * @param nodeId 节点id
     * @return 设备节点编辑数据
     */
    ResponseResult<DeviceNodeUpdateDto> findDeviceNodeUpdateList(String deviceId, String nodeId);

    /**
     * 批量绑定设备拓扑图数据
     * @param deviceId 设备id
     * @param nodeId 节点id
     * @param bindData 绑定数据 例如["设备id,节点id1","设备id,节点id2"]
     * @return 状态码
     */
    ResponseResult<Void> batchBindDeviceTopology(String deviceId, String nodeId, List<String> bindData);

    /**
     * 根据主键id删除设备节点数据
     * @param id 主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteDeviceTopologyById(String id);

    /**
     * 根据查询条件查询设备事件列表
     * @param eventQueryVo 设备事件查询参数
     * @return 设备事件列表
     */
    ResponseResult<PageDto<DeviceEventListDto>> findDeviceEventList(DeviceEventQueryVo eventQueryVo);

    /**
     * 根据设备事件id消除告警
     * @param id 设备事件id
     * @return 状态码
     */
    ResponseResult<Void> updateDeviceEventStatusById(String id);

    /**
     * 获取站点设备树形结构
     * @param userId 用户id
     * @param type 类型 0-所有数据 1-站点数据 2-设备数据 3-子设备数据 4-站点设备数据 5-站点设备子设备数据
     * @return 站点设备数据
     */
    ResponseResult<List<SiteDeviceTreeDto>> getSiteDeviceTreeList(String userId, Integer type);


    /**
     * 获取站点设备资产树形结构
     * @param userId 用户id
     * @return 站点设备数据
     */
    ResponseResult<List<SiteDeviceTreeDto>> getSiteAssetsTreeList(String userId);

    /**
     * 根据网关id查询网关下所有子设备信息
     * @param gatewayId 网关设备id
     * @return 子设备信息列表
     */
    ResponseResult<List<GatewaySubDeviceDto>> findGatewaySubDeviceList(String gatewayId);

    /**
     * 根据多个设备编码查询设备详情数据
     * @param deviceCodeList 多个设备编码
     * @return 设备编码 -> 设备详情数据
     */
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(List<String> deviceCodeList);

    /**
     * 根据网关id查询网关下所有子设备信息
     * @param gatWayId 网关设备id
     * @return 网关下的子设备信息列表
     */
    ResponseResult<List<DeviceBasicInfoDto>> findGatewayChildDeviceById(String gatWayId);

    /**
     * 根据多个设备id查询设备电枪数据
     * @param deviceIds 多个设备id
     * @return 设备id -> 设备电枪信息列表
     */
    ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(List<String> deviceIds);

    /**
     * 根据网关id查询网关下站点子设备列表
     * @param gatewayId 网关id
     * @return 网关下站点子设备列表
     */
    ResponseResult<List<GatewaySubDeviceDto>> findSiteSubDeviceList(String gatewayId);

    /**
     * 批量修改网关子设备数据
     * @param gatewayId 网关id
     * @param subDeviceIds 多个子设备id 例如['1','2','3']
     * @param type 类型 1-批量添加 2-批量删除
     * @return 状态码
     */
    ResponseResult<Void> batchUpdateGatewaySubDevice(String gatewayId, List<String> subDeviceIds, Integer type);

    /**
     * 查询所有电桩设备信息列表
     * @return 设备基本信息列表
     */
    ResponseResult<List<DeviceBasicInfoDto>> findAllPileDeviceInfoList();

    /**
     * 根据站点id查询设备资产父节点数据
     * @param siteId 站点id
     * @return 站点下所有设备资产列表
     */
    ResponseResult<List<DeviceAssetDto>> getDeviceAssetList(String siteId);

    /**
     * 添加设备枪数据
     * @param deviceGunChangeVo 设备枪数据编辑实体类
     * @return 状态码
     */
    ResponseResult<Void> saveDeviceGun(DeviceGunChangeVo deviceGunChangeVo);

    /**
     * 根据设备id查询设备枪列表数据
     * @param deviceId 设备id
     * @return 设备枪列表数据
     */
    ResponseResult<List<DeviceGunListDto>> findDeviceGunListByDeviceId(String deviceId);

    /**
     * 批量新增或编辑互联互通设备数据
     * @param interflowDeviceVos 互联互通设备数据
     * @return 状态码
     */
    ResponseResult<Void> saveOrUpdateInterflowDevice(List<InterflowDeviceVo> interflowDeviceVos);

    /**
     * 批量添加设备枪数据
     * @param deviceGunIds 多个设备枪主键id
     * @param deviceIds 多个设备id
     * @return 状态码
     */
    ResponseResult<Void> saveAllDeviceGun(List<String> deviceGunIds, List<String> deviceIds);

    /**
     * 批量删除设备枪数据
     * @param deviceGunIds 多个设备枪主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteAllDeviceGun(List<String> deviceGunIds);

    /**
     * 保存设备功能点字段
     * @param deviceId 设备id
     * @param functionFields 多个功能点字段id
     * @return 状态码
     */
    ResponseResult<Void> saveDeviceFunctionField(String deviceId, String functionFields);

    /**
     * 根据设备id查询设备功能点列表数据
     * @param deviceId 设备id
     * @return 设备功能点列表数据
     */
    ResponseResult<List<ModelFunctionListDto>> findDeviceFunctionListByDeviceId(String deviceId);

    /**
     * 根据站点id查询站点的设备数据
     * @param siteId 站点id
     * @return 站点设备数据
     */
    ResponseResult<List<SiteDeviceTreeDto>> findSiteDeviceListBySiteId(String siteId);

    /**
     * 根据设备id查询设备所有未恢复事件告警数据
     * @param deviceId
     * @return
     */
    ResponseResult<List<DeviceAlarmEventListDto>> findDeviceNotRecoveEventList(String deviceId);

    /**
     * 根据设备id查询设备功能属性列表数据
     * @param deviceId
     * @return
     */
    ResponseResult<List<FunctionDataDto>> findDeviceFunctionDataList(String deviceId);

    /**
     * 根据多个模型id和多个功能点标识查询模型功能点列表数据
     * @param modelIds 多个模型id
     * @param functionLogos 多个功能点标识
     * @return 模型功能点列表数据
     */
    ResponseResult<Map<String, Map<String,ModelFunctionListDto>>> getModelFunctionListByModelIds(Set<String> modelIds, String functionLogos);
}
