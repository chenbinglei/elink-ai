package com.sunmax.crontab.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.crontab.dto.ComputeNodeInfoDto;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.crontab.dto.NodeLogInfoDto;
import com.sunmax.crontab.service.ComputeNodeService;
import com.sunmax.crontab.vo.ComputeNodeChangeVo;
import com.sunmax.crontab.vo.ComputeNodeListVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 计算节点管理
 */

@RestController
@CrossOrigin
@RequestMapping("computeNode")
@Tag(name = "计算节点管理")
public class ComputeNodeController {

    @Autowired
    private ComputeNodeService computeNodeService;

    @PostMapping("saveOrUpdateComputeNodeInfo")
    @Operation(summary = "保存或编辑计算节点信息")
    
    public ResponseResult<String> saveOrUpdateComputeNodeInfo(ComputeNodeChangeVo computeNodeChangeVo) {
        return computeNodeService.saveOrUpdateComputeNodeInfo(computeNodeChangeVo);
    }

    @PostMapping("findComputeNodeByPage")
    @Operation(summary = "分页查询计算节点数据")
    
    public ResponseResult<PageDto<ComputeNodeListDto>> findComputeNodeByPage(ComputeNodeListVo computeNodeListVo) {
        return computeNodeService.findComputeNodeByPage(computeNodeListVo);
    }

    @PostMapping("findComputeNodeInfoById")
    @Operation(summary = "根据id查询计算节点详情")
    
    @Parameters({
            @Parameter(name = "id", description = "节点唯一id")
    })
    public ResponseResult<ComputeNodeInfoDto> findComputeNodeInfoById(String id) {
        return computeNodeService.findComputeNodeInfoById(id);
    }

    @PostMapping("deleteAllComputeNodeById")
    @Operation(summary = "根据节点id删除计算节点数据")
    
    @Parameters({
            @Parameter(name = "id", description = "节点唯一id")
    })
    public ResponseResult<String> deleteAllComputeNodeById(String id) {
        return computeNodeService.deleteAllComputeNodeById(id);
    }

    @PostMapping("findSiteDeviceDataById")
    @Operation(summary = "根据站点id查询下面设备列表")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<List<DeviceBasicInfoDto>> findSiteDeviceDataById(String siteId) {
        return computeNodeService.findSiteDeviceDataById(siteId);
    }

    @PostMapping("findDeviceFunctionListById")
    @Operation(summary = "根据设备id查询功能点列表")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id")
    })
    public ResponseResult<List<ModelFunctionListDto>> findDeviceFunctionListById(String deviceId) {
        return computeNodeService.findDeviceFunctionListById(deviceId);
    }

    @PostMapping("findComputeNodeListById")
    @Operation(summary = "根据站点/设备id查询计算节点列表")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id")
    })
    public ResponseResult<List<ComputeNodeListDto>> findComputeNodeListById(String deviceId) {
        return computeNodeService.findComputeNodeListById(deviceId);
    }

    @PostMapping("findLocalCacheDataByIds")
    @Operation(summary = "根据多个节点id查询本地缓存数据")
    
    public ResponseResult<Map<String, LocalCacheDto>> findLocalCacheDataByIds(String ids) {
        return computeNodeService.findLocalCacheDataByIds(ids);
    }

    @PostMapping("findNodeLogInfoListByPage")
    @Operation(summary = "分页查询节点日志信息")
    
    @Parameters({
            @Parameter(name = "nodeId", description = "节点id"),
            @Parameter(name = "queryDate", description = "查询日期"),
            @Parameter(name = "queryType", description = "查询类型(1-定时任务 2-数据补录)"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "条数"),
    })
    public ResponseResult<PageDto<NodeLogInfoDto>> findNodeLogInfoListByPage(String nodeId, String queryDate, Integer queryType, Integer page, Integer size) {
        return computeNodeService.findNodeLogInfoListByPage(nodeId, queryDate, queryType, page, size);
    }

    @PostMapping("removeNodeLogInfo")
    @Operation(summary = "清除日志")
    
    @Parameters({
            @Parameter(name = "nodeId", description = "节点id"),
            @Parameter(name = "removeDate", description = "清除日期"),
            @Parameter(name = "removeType", description = "清除类型(1-本次查询 2-清除全部)"),
            @Parameter(name = "queryType", description = "查询类型(1-定时任务 2-数据补录)"),
    })
    public ResponseResult<String> removeNodeLogInfo(String nodeId, String removeDate, Integer removeType, Integer queryType) {
        return computeNodeService.removeNodeLogInfo(nodeId, removeDate, removeType, queryType);
    }
}
