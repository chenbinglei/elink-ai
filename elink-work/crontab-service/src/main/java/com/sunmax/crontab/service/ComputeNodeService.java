package com.sunmax.crontab.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.crontab.dto.ComputeNodeInfoDto;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.crontab.dto.NodeLogInfoDto;
import com.sunmax.crontab.vo.ComputeNodeChangeVo;
import com.sunmax.crontab.vo.ComputeNodeListVo;
import com.sunmax.crontab.vo.ComputeNodeTaskVo;

import java.util.List;
import java.util.Map;

public interface ComputeNodeService {

    /**
     * 保存或编辑计算节点信息
     * @param computeNodeChangeVo
     * @return
     */
    ResponseResult<String> saveOrUpdateComputeNodeInfo(ComputeNodeChangeVo computeNodeChangeVo);

    /**
     * 分页查询计算节点数据
     * @param computeNodeListVo
     * @return
     */
    ResponseResult<PageDto<ComputeNodeListDto>> findComputeNodeByPage(ComputeNodeListVo computeNodeListVo);

    /**
     * 根据id查询计算节点详情
     * @param id
     * @return
     */
    ResponseResult<ComputeNodeInfoDto> findComputeNodeInfoById(String id);

    /**
     * 根据节点id删除计算节点数据
     * @param id
     * @return
     */
    ResponseResult<String> deleteAllComputeNodeById(String id);

    /**
     * 根据设备id查询计算节点列表
     * @param deviceId
     * @return
     */
    ResponseResult<List<ComputeNodeListDto>> findNodeListByDeviceId(String deviceId);

    /**
     * 查询全部计算节点任务列表
     * @return
     */
    List<ComputeNodeTaskVo> findAllComputeNodeList();

    /**
     * 根据站点id查询下面设备列表
     * @param siteId
     * @return
     */
    ResponseResult<List<DeviceBasicInfoDto>> findSiteDeviceDataById(String siteId);

    /**
     * 根据设备id查询功能点列表
     * @param deviceId
     * @return
     */
    ResponseResult<List<ModelFunctionListDto>> findDeviceFunctionListById(String deviceId);

    /**
     * 根据站点/设备id查询计算节点列表
     *
     * @param deviceId
     * @return
     */
    ResponseResult<List<ComputeNodeListDto>> findComputeNodeListById(String deviceId);

    /**
     * 根据多个节点id查询本地缓存数据
     * @param ids
     * @return
     */
    ResponseResult<Map<String, LocalCacheDto>> findLocalCacheDataByIds(String ids);

    /**
     * 分页查询节点日志信息
     * @param nodeId
     * @param queryDate
     * @param queryType
     * @return
     */
    ResponseResult<PageDto<NodeLogInfoDto>> findNodeLogInfoListByPage(String nodeId, String queryDate, Integer queryType, Integer page, Integer size);

    /**
     * 清除日志
     *
     * @param nodeId
     * @param removeDate
     * @param removeType
     * @param queryType
     * @return
     */
    ResponseResult<String> removeNodeLogInfo(String nodeId, String removeDate, Integer removeType, Integer queryType);
}
