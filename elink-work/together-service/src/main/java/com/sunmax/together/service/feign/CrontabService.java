package com.sunmax.together.service.feign;

import com.sunmax.common.dto.crontab.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.DeviceNodeValueVo;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.common.vo.crontab.VarNodeCuntFunQueryVo;
import com.sunmax.common.vo.crontab.VarNodeValueVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "crontab-service", path = "/crontab/feign/together")
public interface CrontabService {

    @PostMapping("findDeviceVarNodeValueByIds")
    @Operation(summary = "根据多个设备id和系统变量查询关联节点数据")
    
    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findDeviceVarNodeValueByIds(@RequestBody VarNodeValueVo varNodeValueVo);

    @PostMapping("findSiteVarNodeValueByIds")
    @Operation(summary = "根据多个站点id和系统变量查询关联节点数据")
    
    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findSiteVarNodeValueByIds(@RequestBody VarNodeValueVo varNodeValueVo);

    @PostMapping("findNodeCacheByVarCodes")
    @Operation(summary = "根据多个变量编码和设备/站点id查询计算节点实时缓存数据")
    
    @Parameters({
            @Parameter(name = "varCodeList", description = "多个系统变量编码"),
            @Parameter(name = "queryIds", description = "多个设备/站点id(以逗号分割)")
    })
    ResponseResult<Map<String, Map<String, LocalCacheDto>>> findNodeCacheByVarCodes(@RequestBody List<String> varCodeList, @RequestParam String queryIds);

    @PostMapping("findVarNodeDataByCountFun")
    @Operation(summary = "根据多个系统变量编码和多个设备/站点id查询计算节点统计值数据")
    
    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findVarNodeDataByCountFun(@RequestBody VarNodeCuntFunQueryVo varNodeCuntFunQueryVo);

    @PostMapping("batchUpdateStrategyTask")
    @Operation(summary = "批量修改自动策略任务")
    
    ResponseResult<Void> batchUpdateStrategyTask(@RequestBody List<StrategyTaskVo> strategyTaskVos);

    @PostMapping("batchDeleteStrategyTask")
    @Operation(summary = "批量删除自动策略任务")
    
    ResponseResult<Void> batchDeleteStrategyTask(@RequestBody List<String> strategyTaskIds);

    @PostMapping("findSystemVarDataListById")
    @Operation(summary = "根据站点/设备id查询所关联所有系统变量数据列表")
    
    @Parameters({
            @Parameter(name = "varCodes", description = "多个系统变量编码(以逗号分割，不传则查询所关联全部系统变量)"),
            @Parameter(name = "queryId", description = "设备/站点id")
    })
    ResponseResult<List<SystemVarDataDto>> findSystemVarDataListById(@RequestParam String varCodes, @RequestParam String queryId);

    @PostMapping("findNodeDifDataListFeign")
    @Operation(summary = "查询计算节点指定时间段内的last和first历史数据")
    
    ResponseResult<Map<String, Map<String, List<NodeDifDataDto>>>> findNodeDifDataListFeign(@RequestBody VarNodeValueVo varNodeValueVo);

    @PostMapping("findComputeNodeListByDeviceIds")
    @Operation(summary = "根据多个设备id查询计算节点列表")
    
    ResponseResult<Map<String, List<ComputeNodeListDto>>> findComputeNodeListByDeviceIds(@RequestBody List<String> deviceIds);

    @PostMapping("findDeviceNodeValueList")
    @Operation(summary = "查询设备节点历史数据")
    
    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findDeviceNodeValueList(@RequestBody DeviceNodeValueVo deviceNodeValueVo);

}
