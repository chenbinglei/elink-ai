package com.sunmax.together.controller.energy;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.energy.GatewayDataDto;
import com.sunmax.together.dto.energy.StrategyDataDto;
import com.sunmax.together.dto.energy.StrategyListDto;
import com.sunmax.together.vo.energy.StrategySaveVo;
import com.sunmax.together.vo.energy.StrategyUpdateVo;
import com.sunmax.together.dto.energy.TemplateListDto;
import com.sunmax.together.service.energy.StrategyService;
import com.sunmax.together.vo.energy.TemplateChangeVo;
import com.sunmax.together.vo.energy.TemplateQueryVo;
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
@RequestMapping("strategy")
@Api(tags = "策略管理")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;

    @PostMapping("saveOrUpdateTemplate")
    @ApiOperation("新增或编辑策略模板数据")
    @WebLog("策略管理-新增或编辑策略模板数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "explainFile", value = "策略说明文件", dataType = "MultipartFile"),
            @ApiImplicitParam(name = "configFile", value = "配置文件", dataType = "MultipartFile")
    })
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveOrUpdateTemplate(TemplateChangeVo templateChangeVo, MultipartFile explainFile, MultipartFile configFile) {
        return strategyService.saveOrUpdateTemplate(templateChangeVo, explainFile, configFile);
    }

    @PostMapping("queryTemplateList")
    @ApiOperation("查询策略模板列表数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<TemplateListDto>> queryTemplateList(TemplateQueryVo templateQueryVo) {
        return strategyService.queryTemplateList(templateQueryVo);
    }

    @PostMapping("deleteTemplateById")
    @ApiOperation("根据主键id批量删除策略模板数据")
    @WebLog("策略管理-根据主键id批量删除策略模板数据")
    @ApiImplicitParam(name = "id", value = "主键id", dataType = "String", required = true)
    @ApiOperationSupport(order = 3)
    public ResponseResult<Void> deleteTemplateById(String id) {
        return strategyService.deleteTemplateById(id);
    }

    @PostMapping("parseTemplateContent")
    @ApiOperation("查看策略模板文件内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键id", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "类型 1-策略说明 2-配置文件", dataType = "int", required = true)
    })
    @ApiOperationSupport(order = 4)
    public ResponseResult<String> parseTemplateContent(String id, Integer type) {
        return strategyService.parseTemplateContent(id, type);
    }

    @PostMapping("findTemplateById")
    @ApiOperation("根据id查询策略模板详情")
    @ApiImplicitParam(name = "id", value = "主键id", dataType = "String", required = true)
    @ApiOperationSupport(order = 5)
    public ResponseResult<TemplateListDto> findTemplateById(String id) {
        return strategyService.findTemplateById(id);
    }

    @PostMapping("findGatewayDataBySiteId")
    @ApiOperation("根据站点id查询网关数据")
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    @ApiOperationSupport(order = 6)
    public ResponseResult<List<GatewayDataDto>> findGatewayDataBySiteId(String siteId) {
        return strategyService.findGatewayDataBySiteId(siteId);
    }

    @PostMapping("saveStrategy")
    @ApiOperation("新增策略数据")
    @WebLog("策略管理-新增策略数据")
    @ApiOperationSupport(order = 7)
    public ResponseResult<Void> saveStrategy(StrategySaveVo strategySaveVo) {
        return strategyService.saveStrategy(strategySaveVo);
    }

    @PostMapping("updateStrategy")
    @ApiOperation("编辑策略数据")
    @WebLog("策略管理-编辑策略数据")
    @ApiImplicitParam(name = "configFile", value = "配置文件", dataType = "MultipartFile")
    @ApiOperationSupport(order = 8)
    public ResponseResult<Void> updateStrategy(StrategyUpdateVo strategyUpdateVo, MultipartFile configFile) {
        return strategyService.updateStrategy(strategyUpdateVo, configFile);
    }

    @PostMapping("queryStrategyList")
    @ApiOperation("查询策略管理列表")
    @ApiOperationSupport(order = 9)
    public ResponseResult<List<StrategyListDto>> queryStrategyList(String siteId, String deviceId) {
        return strategyService.queryStrategyList(siteId, deviceId);
    }

    @PostMapping("findStrategyById")
    @ApiOperation("根据策略id和类型查询策略配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "策略id", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "类型 1-保存数据 2-读取数据", dataType = "int", required = true)
    })
    @ApiOperationSupport(order = 10)
    public ResponseResult<StrategyDataDto> findStrategyById(String id, Integer type) {
        return strategyService.findStrategyById(id, type);
    }

    @PostMapping("deleteStrategyById")
    @ApiOperation("根据策略id删除策略数据")
    @WebLog("策略管理-根据策略id删除策略数据")
    @ApiImplicitParam(name = "id", value = "策略id", dataType = "String", required = true)
    @ApiOperationSupport(order = 11)
    public ResponseResult<Void> deleteStrategyById(String id) {
        return strategyService.deleteStrategyById(id);
    }

    @PostMapping("cloneStrategy")
    @ApiOperation("克隆策略数据")
    @WebLog("策略管理-克隆策略数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "策略id", dataType = "String", required = true),
            @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "strategyType", value = "策略类型 1-边缘网关 2-云网关 3-云平台", dataType = "String", required = true)
    })
    @ApiOperationSupport(order = 12)
    public ResponseResult<Void> cloneStrategy(String id, String siteId, String deviceId, Integer strategyType) {
        return strategyService.cloneStrategy(id, siteId, deviceId, strategyType);
    }

    @PostMapping("issuedStrategy")
    @ApiOperation("配置下发")
    @WebLog("策略管理-配置下发")
    @ApiImplicitParam(name = "id", value = "策略id", dataType = "String", required = true)
    @ApiOperationSupport(order = 13)
    public ResponseResult<Void> issuedStrategy(String id) {
        return strategyService.issuedStrategy(id);
    }

}
