package com.sunmax.configure.controller;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.common.dto.device.SiteDeviceTreeDto;
import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.dto.*;
import com.sunmax.configure.service.DeviceFeignService;
import com.sunmax.configure.service.GraphService;
import com.sunmax.configure.vo.GraphChangeVo;
import com.sunmax.configure.vo.GraphSourceChangeVo;
import com.sunmax.configure.vo.GraphVariableChangeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("graph")
@Tag(name = "图模管理控制层")
public class GraphController {

    @Resource
    private GraphService graphService;

    @Resource
    private DeviceFeignService deviceFeignService;

    @PostMapping("saveGraph")
    @Operation(summary = "新建或编辑图模数据")
    @Parameter(name = "file", description = "图模文件")
    public ResponseResult<String> saveGraph(GraphChangeVo graphChangeVo, MultipartFile file) {
        return graphService.saveGraph(graphChangeVo, file);
    }

    @PostMapping("deleteGraphByIds")
    @Operation(summary = "根据多个id删除数据")
    @Parameter(name = "ids", description = "多个id")
    public ResponseResult<Void> deleteGraphByIds(String ids) {
        return graphService.deleteGraphByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("queryGraphList")
    @Operation(summary = "查询图模管理列表")
    @Parameter(name = "name", description = "名称(模糊查询)")
    public ResponseResult<List<GraphListDto>> queryGraphList(String name) {
        return graphService.queryGraphList(name);
    }

    @PostMapping("findGraphById")
    @Operation(summary = "根据图模id查询图模文件数据")
    @Parameter(name = "id", description = "图模id")
    public ResponseResult<GraphDetailDto> findGraphById(String id) {
        return graphService.findGraphById(id);
    }

    @PostMapping("relationDataSource")
    @Operation(summary = "图模关联数据源")
    public ResponseResult<Void> relationDataSource(GraphSourceChangeVo graphSourceChangeVo) {
        return graphService.relationDataSource(graphSourceChangeVo);
    }

    @PostMapping("checkDataSource")
    @Operation(summary = "校验数据源是否调通")
    @Parameters({
            @Parameter(name = "dataSourceId", description = "数据源id"),
            @Parameter(name = "requestValue", description = "请求参数数据")
    })
    public ResponseResult<String> checkDataSource(String dataSourceId, String requestValue) {
        return graphService.checkDataSource(dataSourceId, requestValue);
    }

    @PostMapping("findGraphSourceByGraphId")
    @Operation(summary = "根据图模id查询关联数据源数据")
    @Parameters({
            @Parameter(name = "graphId", description = "图模id"),
            @Parameter(name = "name", description = "名称(模糊搜索)")
    })
    public ResponseResult<List<GraphSourceDto>> findGraphSourceByGraphId(String graphId, String name) {
        return graphService.findGraphSourceByGraphId(graphId, name);
    }

    @PostMapping("saveGraphVariable")
    @Operation(summary = "新增或编辑图模变量数据")
    public ResponseResult<Void> saveGraphVariable(GraphVariableChangeVo graphVariableChangeVo) {
        return graphService.saveGraphVariable(graphVariableChangeVo);
    }

    @PostMapping("saveAllGraphVariable")
    @Operation(summary = "批量编辑图模变量数据")
    public ResponseResult<Void> saveAllGraphVariable(String graphVariableChangeVos) {
        return graphService.saveAllGraphVariable(JSON.parseArray(graphVariableChangeVos, GraphVariableChangeVo.class));
    }

    @PostMapping("deleteGraphVariableByIds")
    @Operation(summary = "根据多个变量id批量删除图模变量数据")
    @Parameter(name = "ids", description = "多个变量id 例如['1','2']")
    public ResponseResult<Void> deleteGraphVariableByIds(String ids) {
        return graphService.deleteGraphVariableByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("findGraphVariableByGraphId")
    @Operation(summary = "根据图模id查询关联变量数据")
    @Parameters({
            @Parameter(name = "graphId", description = "图模id"),
            @Parameter(name = "keyword", description = "关键词")
    })
    public ResponseResult<List<GraphVariableListDto>> findGraphVariableByGraphId(String graphId, String keyword) {
        return graphService.findGraphVariableByGraphId(graphId, keyword);
    }

    @PostMapping("findGraphDataListByGraphId")
    @Operation(summary = "根据图模id查询图模接口数据")
    @Parameters({
            @Parameter(name = "id", description = "数据id"),
            @Parameter(name = "type", description = "类型 1-图模id 2-域名id")
    })
    public ResponseResult<GraphDataDto> findGraphDataListByGraphId(String id, Integer type) {
        return graphService.findGraphDataListByGraphId(id, type);
    }

    @PostMapping("querySystemDeviceList")
    @Operation(summary = "查询系统设备列表")
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<SystemDeviceListDto> querySystemDeviceList(String siteId) {
        return deviceFeignService.querySystemDeviceList(siteId);
    }

    @PostMapping("findComputeNodeListByDeviceId")
    @Operation(summary = "根据站点/设备id查询计算节点列表")
    @Parameter(name = "deviceId", description = "站点/设备唯一id")
    public ResponseResult<List<ComputeNodeListDto>> findComputeNodeListByDeviceId(String deviceId) {
        return deviceFeignService.findComputeNodeListByDeviceId(deviceId);
    }

    @PostMapping("cloneGraph")
    @Operation(summary = "克隆图模")
    @Parameters({
            @Parameter(name = "id", description = "图模id"),
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "file", description = "图模文件")
    })
    public ResponseResult<String> cloneGraph(String id, String userId, MultipartFile file) {
        return graphService.cloneGraph(id, userId, file);
    }

    @PostMapping("findSiteListByUserId")
    @Operation(summary = "根据用户id查询站点列表")
    @Parameter(name = "userId", description = "用户id")
    public ResponseResult<List<SiteListDto>> findSiteListByUserId(String userId) {
        return deviceFeignService.findSiteListByUserId(userId);
    }

    @PostMapping("findFunctionListByDeviceId")
    @Operation(summary = "根据设备id获取功能点数据")
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<List<FunctionListDto>> findFunctionListByDeviceId(String deviceId) {
        return deviceFeignService.findFunctionListByDeviceId(deviceId);
    }

    @PostMapping("findSiteDeviceListBySiteId")
    @Operation(summary = "根据站点id查询站点的设备数据")
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<List<SiteDeviceTreeDto>> findSiteDeviceListBySiteId(String siteId) {
        return deviceFeignService.findSiteDeviceListBySiteId(siteId);
    }

    @PostMapping("importGraph")
    @Operation(summary = "导入图模数据")
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "file", description = "图模文件")
    })
    public ResponseResult<Void> importGraph(String userId, MultipartFile file) {
        return graphService.importGraph(userId, file);
    }

}
