package com.sunmax.together.controller.asset;

import com.alibaba.fastjson.JSON;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.asset.electConfig.ElectConfigDetailDto;
import com.sunmax.together.dto.asset.electConfig.ElectConfigListDto;
import com.sunmax.together.service.asset.ElectConfigService;
import com.sunmax.together.vo.operation.electConfig.ElectConfigChangeVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("electConfig")
@Api(tags = "电价配置管理")
public class ElectConfigController {

    @Autowired
    private ElectConfigService electConfigService;

    @PostMapping("saveElectConfig")
    @ApiOperation("新增或编辑电价策略配置数据")
    @WebLog("电价配置-新增或编辑电价策略配置数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveElectConfig(ElectConfigChangeVo electConfigVo) {
        return electConfigService.saveElectConfig(electConfigVo);
    }

    @PostMapping("deleteAllElectConfigByIds")
    @ApiOperation("批量删除电价策略配置")
    @WebLog("电价配置-批量删除电价策略配置")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "ids", value = "多个电价策略配置id 例如[电价策略配置1,电价策略配置2]", required = true)
    public ResponseResult<Void> deleteAllElectConfigByIds(String ids) {
        return electConfigService.deleteAllElectConfigByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("queryElectConfigList")
    @ApiOperation("根据站点id查询电价策略配置列表")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParam(name = "siteId", value = "站点id", required = true)
    public ResponseResult<Map<Integer, List<ElectConfigListDto>>> queryElectConfigList(String siteId) {
        return electConfigService.queryElectConfigList(siteId);
    }

    @PostMapping("findElectConfigById")
    @ApiOperation("根据电价策略配置id查询电价策略配置数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "id", value = "电价策略配置id", required = true)
    public ResponseResult<ElectConfigDetailDto> findElectConfigById(String id) {
        return electConfigService.findElectConfigById(id);
    }


    @PostMapping("applyElectConfigToOtherSite")
    @ApiOperation("批量把电价策略应用到其它站点")
    @WebLog("电价配置-批量把电价策略应用到其它站点")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "electConfigIds", value = "多个电价策略配置id 例如[电价策略配置id1,电价策略配置id2]", required = true),
            @ApiImplicitParam(name = "siteIds", value = "多个站点id 例如[站点id1,站点id2]", required = true)
    })
    public ResponseResult<List<String>> applyElectConfigToOtherSite(String electConfigIds, String siteIds) {
        return electConfigService.applyElectConfigToOtherSite(JSON.parseArray(electConfigIds, String.class), JSON.parseArray(siteIds, String.class));
    }

}
