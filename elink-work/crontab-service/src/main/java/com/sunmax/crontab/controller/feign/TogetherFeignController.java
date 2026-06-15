package com.sunmax.crontab.controller.feign;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/feign/together")
@Tag(name = "提供给能源聚合服务调用的远程接口")
@Hidden()
public class TogetherFeignController {

    @Autowired
    private TogetherFeignService togetherFeignService;

    @PostMapping("findDeviceVarNodeValueByIds")
    @Operation(summary = "根据多个设备id和系统变量查询关联节点数据")
    
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findDeviceVarNodeValueByIds(@RequestBody VarNodeValueVo varNodeValueVo) {
        return togetherFeignService.findDeviceVarNodeValueByIds(varNodeValueVo);
    }

    @PostMapping("findSiteVarNodeValueByIds")
    @Operation(summary = "根据多个站点id和系统变量查询关联节点数据")
    
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findSiteVarNodeValueByIds(@RequestBody VarNodeValueVo varNodeValueVo) {
        return togetherFeignService.findSiteVarNodeValueByIds(varNodeValueVo);
    }

    @PostMapping("findNodeCacheByVarCodes")
    @Operation(summary = "根据多个变量编码和设备/站点id查询计算节点实时缓存数据")
    
    @Parameters({
            @Parameter(name = "varCodeList", description = "多个系统变量编码"),
            @Parameter(name = "queryIds", description = "多个设备/站点id(以逗号分割)")
    })
    public ResponseResult<Map<String, Map<String, LocalCacheDto>>> findNodeCacheByVarCodes(@RequestBody List<String> varCodeList, @RequestParam String queryIds) {
        return togetherFeignService.findNodeCacheByVarCodes(varCodeList, Arrays.stream(queryIds.split(",")).collect(Collectors.toList()));
    }

    @PostMapping("findVarNodeDataByCountFun")
    @Operation(summary = "根据多个系统变量编码和多个设备/站点id查询计算节点统计值数据")
    
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findVarNodeDataByCountFun(@RequestBody VarNodeCuntFunQueryVo varNodeCuntFunQueryVo) {
        return togetherFeignService.findVarNodeDataByCountFun(varNodeCuntFunQueryVo);
    }

    @PostMapping("batchUpdateStrategyTask")
    @Operation(summary = "批量修改自动策略任务")
    
    public ResponseResult<Void> batchUpdateStrategyTask(@RequestBody List<StrategyTaskVo> strategyTaskVos) {
        return togetherFeignService.batchUpdateStrategyTask(strategyTaskVos);
    }

    @PostMapping("batchDeleteStrategyTask")
    @Operation(summary = "批量删除自动策略任务")
    
    public ResponseResult<Void> batchDeleteStrategyTask(@RequestBody List<String> strategyTaskIds) {
        return togetherFeignService.batchDeleteStrategyTask(strategyTaskIds);
    }

    @PostMapping("findSystemVarDataListById")
    @Operation(summary = "根据站点/设备id查询所关联所有系统变量数据列表")
    
    @Parameters({
            @Parameter(name = "varCodes", description = "多个系统变量编码(以逗号分割，不传则查询所关联全部系统变量)"),
            @Parameter(name = "queryId", description = "设备/站点id")
    })
    public ResponseResult<List<SystemVarDataDto>> findSystemVarDataListById(@RequestParam(required = false) String varCodes, @RequestParam String queryId) {
        return togetherFeignService.findSystemVarDataListById(varCodes, queryId);
    }

    @PostMapping("findNodeDifDataListFeign")
    @Operation(summary = "查询计算节点指定时间段内的last和first历史数据")
    
    public ResponseResult<Map<String, Map<String, List<NodeDifDataDto>>>> findNodeDifDataListFeign(@RequestBody VarNodeValueVo varNodeValueVo) {
        return togetherFeignService.findNodeDifDataListFeign(varNodeValueVo);
    }

    @PostMapping("findComputeNodeListByDeviceIds")
    @Operation(summary = "根据多个设备id查询计算节点列表")
    
    public ResponseResult<Map<String, List<ComputeNodeListDto>>> findComputeNodeListByDeviceIds(@RequestBody List<String> deviceIds) {
        return togetherFeignService.findComputeNodeListByDeviceIds(deviceIds);
    }

    @PostMapping("findDeviceNodeValueList")
    @Operation(summary = "查询设备节点历史数据")
    
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findDeviceNodeValueList(@RequestBody DeviceNodeValueVo deviceNodeValueVo) {
        return togetherFeignService.findDeviceNodeValueList(deviceNodeValueVo);
    }

}
