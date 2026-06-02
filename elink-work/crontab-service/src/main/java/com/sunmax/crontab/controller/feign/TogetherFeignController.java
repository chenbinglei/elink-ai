package com.sunmax.crontab.controller.feign;

import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.dto.crontab.NodeDifDataDto;
import com.sunmax.common.dto.crontab.NodeHistoryDataDto;
import com.sunmax.common.dto.crontab.SystemVarDataDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.DeviceNodeValueVo;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.common.vo.crontab.VarNodeCuntFunQueryVo;
import com.sunmax.common.vo.crontab.VarNodeValueVo;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.crontab.service.TogetherFeignService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/feign/together")
@Api(tags = "提供给能源聚合服务调用的远程接口")
@ApiIgnore()
public class TogetherFeignController {

    @Autowired
    private TogetherFeignService togetherFeignService;

    @PostMapping("findDeviceVarNodeValueByIds")
    @ApiOperation("根据多个设备id和系统变量查询关联节点数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findDeviceVarNodeValueByIds(@RequestBody VarNodeValueVo varNodeValueVo) {
        return togetherFeignService.findDeviceVarNodeValueByIds(varNodeValueVo);
    }

    @PostMapping("findSiteVarNodeValueByIds")
    @ApiOperation("根据多个站点id和系统变量查询关联节点数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findSiteVarNodeValueByIds(@RequestBody VarNodeValueVo varNodeValueVo) {
        return togetherFeignService.findSiteVarNodeValueByIds(varNodeValueVo);
    }

    @PostMapping("findNodeCacheByVarCodes")
    @ApiOperation("根据多个变量编码和设备/站点id查询计算节点实时缓存数据")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "varCodeList", value = "多个系统变量编码", dataType = "String", required = true),
            @ApiImplicitParam(name = "queryIds", value = "多个设备/站点id(以逗号分割)", dataType = "String", required = true)
    })
    public ResponseResult<Map<String, Map<String, LocalCacheDto>>> findNodeCacheByVarCodes(@RequestBody List<String> varCodeList, @RequestParam String queryIds) {
        return togetherFeignService.findNodeCacheByVarCodes(varCodeList, Arrays.stream(queryIds.split(",")).collect(Collectors.toList()));
    }

    @PostMapping("findVarNodeDataByCountFun")
    @ApiOperation("根据多个系统变量编码和多个设备/站点id查询计算节点统计值数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findVarNodeDataByCountFun(@RequestBody VarNodeCuntFunQueryVo varNodeCuntFunQueryVo) {
        return togetherFeignService.findVarNodeDataByCountFun(varNodeCuntFunQueryVo);
    }

    @PostMapping("batchUpdateStrategyTask")
    @ApiOperation("批量修改自动策略任务")
    @ApiOperationSupport(order = 5)
    public ResponseResult<Void> batchUpdateStrategyTask(@RequestBody List<StrategyTaskVo> strategyTaskVos) {
        return togetherFeignService.batchUpdateStrategyTask(strategyTaskVos);
    }

    @PostMapping("batchDeleteStrategyTask")
    @ApiOperation("批量删除自动策略任务")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Void> batchDeleteStrategyTask(@RequestBody List<String> strategyTaskIds) {
        return togetherFeignService.batchDeleteStrategyTask(strategyTaskIds);
    }

    @PostMapping("findSystemVarDataListById")
    @ApiOperation("根据站点/设备id查询所关联所有系统变量数据列表")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "varCodes", value = "多个系统变量编码(以逗号分割，不传则查询所关联全部系统变量)", dataType = "String"),
            @ApiImplicitParam(name = "queryId", value = "设备/站点id", dataType = "String", required = true)
    })
    public ResponseResult<List<SystemVarDataDto>> findSystemVarDataListById(@RequestParam(required = false) String varCodes, @RequestParam String queryId) {
        return togetherFeignService.findSystemVarDataListById(varCodes, queryId);
    }

    @PostMapping("findNodeDifDataListFeign")
    @ApiOperation("查询计算节点指定时间段内的last和first历史数据")
    @ApiOperationSupport(order = 8)
    public ResponseResult<Map<String, Map<String, List<NodeDifDataDto>>>> findNodeDifDataListFeign(@RequestBody VarNodeValueVo varNodeValueVo) {
        return togetherFeignService.findNodeDifDataListFeign(varNodeValueVo);
    }

    @PostMapping("findComputeNodeListByDeviceIds")
    @ApiOperation("根据多个设备id查询计算节点列表")
    @ApiOperationSupport(order = 9)
    public ResponseResult<Map<String, List<ComputeNodeListDto>>> findComputeNodeListByDeviceIds(@RequestBody List<String> deviceIds) {
        return togetherFeignService.findComputeNodeListByDeviceIds(deviceIds);
    }

    @PostMapping("findDeviceNodeValueList")
    @ApiOperation("查询设备节点历史数据")
    @ApiOperationSupport(order = 10)
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findDeviceNodeValueList(@RequestBody DeviceNodeValueVo deviceNodeValueVo) {
        return togetherFeignService.findDeviceNodeValueList(deviceNodeValueVo);
    }

}
