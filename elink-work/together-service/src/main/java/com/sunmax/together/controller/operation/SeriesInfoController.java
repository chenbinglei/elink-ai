package com.sunmax.together.controller.operation;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.seriesInfo.ModuleLibraryDto;
import com.sunmax.together.dto.operation.seriesInfo.SeriesConfigInfoDto;
import com.sunmax.together.dto.operation.seriesInfo.SeriesDeviceListDto;
import com.sunmax.together.service.operation.SeriesInfoService;
import com.sunmax.together.vo.operation.seriesInfo.ModuleLibraryChangeVo;
import com.sunmax.together.vo.operation.seriesInfo.ModuleLibraryQueryVo;
import com.sunmax.together.vo.operation.seriesInfo.SeriesConfigChangeVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("seriesInfo")
@Api(tags = "组串管理")
public class SeriesInfoController {

    @Autowired
    private SeriesInfoService seriesInfoService;

    @PostMapping("saveOrUpdateModuleLibrary")
    @ApiOperation("新增或编辑组件库信息")
    @WebLog("组串管理-新增或编辑组件库信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> saveOrUpdateModuleLibrary(ModuleLibraryChangeVo moduleChangeVo, String userId) {
        return seriesInfoService.saveOrUpdateModuleLibrary(moduleChangeVo, userId);
    }

    @PostMapping("batchDeleteModuleLibrary")
    @ApiOperation("批量删除组件库信息")
    @WebLog("组串管理-批量删除组件库信息")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "多个组件id(['aaa','bbb'])", paramType = "query", required = true),
    })
    public ResponseResult<String> batchDeleteModuleLibrary(String ids) {
        return seriesInfoService.batchDeleteModuleLibrary(ids);
    }

    @PostMapping("findModuleLibraryList")
    @ApiOperation("查询组件库列表")
    @ApiOperationSupport(order = 3)
    public ResponseResult<?> findModuleLibraryList(ModuleLibraryQueryVo moduleLibraryQueryVo) {
        return seriesInfoService.findModuleLibraryList(moduleLibraryQueryVo);
    }

    @PostMapping("importModuleLibraryList")
    @ApiOperation("导入组件库列表数据")
    @WebLog("组串管理-导入组件库列表数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件", dataType = "File", required = true)
    })
    @ApiOperationSupport(order = 4)
    public ResponseResult<String> importModuleLibraryList(String userId, MultipartFile file) {
        return seriesInfoService.importModuleLibraryList(userId, file);
    }

    @PostMapping("findModuleFactoryList")
    @ApiOperation("查询组件厂家列表")
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<String>> findModuleFactoryList() {
        return seriesInfoService.findModuleFactoryList();
    }

    @PostMapping("findModuleModelList")
    @ApiOperation("查询组件型号列表")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleFactory", value = "组件厂家", dataType = "String", required = true)
    })
    public ResponseResult<List<ModuleLibraryDto>> findModuleModelList(String moduleFactory) {
        return seriesInfoService.findModuleModelList(moduleFactory);
    }

    @PostMapping("saveSeriesConfigList")
    @ApiOperation("保存组件配置列表")
    @WebLog("组串管理-保存组件配置列表")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "所属设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "seriesConfigChangeVos", value = "组串配置信息json字符串对象列表", dataType = "String", required = true)
    })
    public ResponseResult<String> saveSeriesConfigList(String deviceId, String seriesConfigChangeVos) {
        return seriesInfoService.saveSeriesConfigList(deviceId, JSON.parseArray(seriesConfigChangeVos, SeriesConfigChangeVo.class));
    }

    @PostMapping("findSeriesConfigInfo")
    @ApiOperation("根据设备id查询组串配置信息")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "所属设备id", dataType = "String", required = true)
    })
    public ResponseResult<List<SeriesConfigInfoDto>> findSeriesConfigInfo(String deviceId) {
        return seriesInfoService.findSeriesConfigInfo(deviceId);
    }

    @PostMapping("purgeSeriesConfigById")
    @ApiOperation("根据多个设备id清除组串配置列表")
    @WebLog("组串管理-清除组串配置列表")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIds", value = "多个所属设备id(['aaa','bbb'])", dataType = "String", required = true)
    })
    public ResponseResult<String> purgeSeriesConfigById(String deviceIds) {
        return seriesInfoService.purgeSeriesConfigById(JSON.parseArray(deviceIds, String.class));
    }

    @PostMapping("findInverterDeviceList")
    @ApiOperation("根据站点id查询逆变器设备列表")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "所属站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "configStatus", value = "配置状态 1-未配置 2-已配置", dataType = "Integer"),
            @ApiImplicitParam(name = "equipmentModel", value = "设备型号", dataType = "String")
    })
    public ResponseResult<List<SeriesDeviceListDto>> findInverterDeviceList(String siteId, Integer configStatus, String equipmentModel) {
        return seriesInfoService.findInverterDeviceList(siteId, configStatus, equipmentModel);
    }

    @PostMapping("findInverterDeviceModelList")
    @ApiOperation("根据站点id查询逆变器设备型号列表")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "所属站点id", dataType = "String", required = true)
    })
    public ResponseResult<List<String>> findInverterDeviceModelList(String siteId) {
        return seriesInfoService.findInverterDeviceModelList(siteId);
    }
}
