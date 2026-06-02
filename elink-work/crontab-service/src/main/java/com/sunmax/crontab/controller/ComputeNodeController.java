package com.sunmax.crontab.controller;

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
import io.swagger.annotations.*;
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
@Api(tags = "计算节点管理")
public class ComputeNodeController {

    @Autowired
    private ComputeNodeService computeNodeService;

    @PostMapping("saveOrUpdateComputeNodeInfo")
    @ApiOperation("保存或编辑计算节点信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> saveOrUpdateComputeNodeInfo(ComputeNodeChangeVo computeNodeChangeVo) {
        return computeNodeService.saveOrUpdateComputeNodeInfo(computeNodeChangeVo);
    }

    @PostMapping("findComputeNodeByPage")
    @ApiOperation("分页查询计算节点数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<ComputeNodeListDto>> findComputeNodeByPage(ComputeNodeListVo computeNodeListVo) {
        return computeNodeService.findComputeNodeByPage(computeNodeListVo);
    }

    @PostMapping("findComputeNodeInfoById")
    @ApiOperation("根据id查询计算节点详情")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "节点唯一id", dataType = "String", required = true)
    })
    public ResponseResult<ComputeNodeInfoDto> findComputeNodeInfoById(String id) {
        return computeNodeService.findComputeNodeInfoById(id);
    }

    @PostMapping("deleteAllComputeNodeById")
    @ApiOperation("根据节点id删除计算节点数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "节点唯一id", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteAllComputeNodeById(String id) {
        return computeNodeService.deleteAllComputeNodeById(id);
    }

    @PostMapping("findSiteDeviceDataById")
    @ApiOperation("根据站点id查询下面设备列表")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    })
    public ResponseResult<List<DeviceBasicInfoDto>> findSiteDeviceDataById(String siteId) {
        return computeNodeService.findSiteDeviceDataById(siteId);
    }

    @PostMapping("findDeviceFunctionListById")
    @ApiOperation("根据设备id查询功能点列表")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    })
    public ResponseResult<List<ModelFunctionListDto>> findDeviceFunctionListById(String deviceId) {
        return computeNodeService.findDeviceFunctionListById(deviceId);
    }

    @PostMapping("findComputeNodeListById")
    @ApiOperation("根据站点/设备id查询计算节点列表")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    })
    public ResponseResult<List<ComputeNodeListDto>> findComputeNodeListById(String deviceId) {
        return computeNodeService.findComputeNodeListById(deviceId);
    }

    @PostMapping("findLocalCacheDataByIds")
    @ApiOperation("根据多个节点id查询本地缓存数据")
    @ApiOperationSupport(order = 8)
    public ResponseResult<Map<String, LocalCacheDto>> findLocalCacheDataByIds(String ids) {
        return computeNodeService.findLocalCacheDataByIds(ids);
    }

    @PostMapping("findNodeLogInfoListByPage")
    @ApiOperation("分页查询节点日志信息")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "queryDate", value = "查询日期", dataType = "String", required = true),
            @ApiImplicitParam(name = "queryType", value = "查询类型(1-定时任务 2-数据补录)", dataType = "Integer"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "size", value = "条数", dataType = "Integer", required = true),
    })
    public ResponseResult<PageDto<NodeLogInfoDto>> findNodeLogInfoListByPage(String nodeId, String queryDate, Integer queryType, Integer page, Integer size) {
        return computeNodeService.findNodeLogInfoListByPage(nodeId, queryDate, queryType, page, size);
    }

    @PostMapping("removeNodeLogInfo")
    @ApiOperation("清除日志")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "removeDate", value = "清除日期", dataType = "String", required = true),
            @ApiImplicitParam(name = "removeType", value = "清除类型(1-本次查询 2-清除全部)", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "queryType", value = "查询类型(1-定时任务 2-数据补录)", dataType = "Integer"),
    })
    public ResponseResult<String> removeNodeLogInfo(String nodeId, String removeDate, Integer removeType, Integer queryType) {
        return computeNodeService.removeNodeLogInfo(nodeId, removeDate, removeType, queryType);
    }
}
