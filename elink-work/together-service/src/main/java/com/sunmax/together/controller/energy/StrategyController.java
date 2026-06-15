package com.sunmax.together.controller.energy;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

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
@Tag(name = "策略管理")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;

    @PostMapping("saveOrUpdateTemplate")
    @Operation(summary = "新增或编辑策略模板数据")
    @WebLog("策略管理-新增或编辑策略模板数据")
    @Parameters({
            @Parameter(name = "explainFile", description = "策略说明文件"),
            @Parameter(name = "configFile", description = "配置文件")
    })
    
    public ResponseResult<Void> saveOrUpdateTemplate(TemplateChangeVo templateChangeVo, MultipartFile explainFile, MultipartFile configFile) {
        return strategyService.saveOrUpdateTemplate(templateChangeVo, explainFile, configFile);
    }

    @PostMapping("queryTemplateList")
    @Operation(summary = "查询策略模板列表数据")
    
    public ResponseResult<PageDto<TemplateListDto>> queryTemplateList(TemplateQueryVo templateQueryVo) {
        return strategyService.queryTemplateList(templateQueryVo);
    }

    @PostMapping("deleteTemplateById")
    @Operation(summary = "根据主键id批量删除策略模板数据")
    @WebLog("策略管理-根据主键id批量删除策略模板数据")
    @Parameter(name = "id", description = "主键id")
    
    public ResponseResult<Void> deleteTemplateById(String id) {
        return strategyService.deleteTemplateById(id);
    }

    @PostMapping("parseTemplateContent")
    @Operation(summary = "查看策略模板文件内容")
    @Parameters({
            @Parameter(name = "id", description = "主键id"),
            @Parameter(name = "type", description = "类型 1-策略说明 2-配置文件")
    })
    
    public ResponseResult<String> parseTemplateContent(String id, Integer type) {
        return strategyService.parseTemplateContent(id, type);
    }

    @PostMapping("findTemplateById")
    @Operation(summary = "根据id查询策略模板详情")
    @Parameter(name = "id", description = "主键id")
    
    public ResponseResult<TemplateListDto> findTemplateById(String id) {
        return strategyService.findTemplateById(id);
    }

    @PostMapping("findGatewayDataBySiteId")
    @Operation(summary = "根据站点id查询网关数据")
    @Parameter(name = "siteId", description = "站点id")
    
    public ResponseResult<List<GatewayDataDto>> findGatewayDataBySiteId(String siteId) {
        return strategyService.findGatewayDataBySiteId(siteId);
    }

    @PostMapping("saveStrategy")
    @Operation(summary = "新增策略数据")
    @WebLog("策略管理-新增策略数据")
    
    public ResponseResult<Void> saveStrategy(StrategySaveVo strategySaveVo) {
        return strategyService.saveStrategy(strategySaveVo);
    }

    @PostMapping("updateStrategy")
    @Operation(summary = "编辑策略数据")
    @WebLog("策略管理-编辑策略数据")
    @Parameter(name = "configFile", description = "配置文件")
    
    public ResponseResult<Void> updateStrategy(StrategyUpdateVo strategyUpdateVo, MultipartFile configFile) {
        return strategyService.updateStrategy(strategyUpdateVo, configFile);
    }

    @PostMapping("queryStrategyList")
    @Operation(summary = "查询策略管理列表")
    
    public ResponseResult<List<StrategyListDto>> queryStrategyList(String siteId, String deviceId) {
        return strategyService.queryStrategyList(siteId, deviceId);
    }

    @PostMapping("findStrategyById")
    @Operation(summary = "根据策略id和类型查询策略配置")
    @Parameters({
            @Parameter(name = "id", description = "策略id"),
            @Parameter(name = "type", description = "类型 1-保存数据 2-读取数据")
    })
    
    public ResponseResult<StrategyDataDto> findStrategyById(String id, Integer type) {
        return strategyService.findStrategyById(id, type);
    }

    @PostMapping("deleteStrategyById")
    @Operation(summary = "根据策略id删除策略数据")
    @WebLog("策略管理-根据策略id删除策略数据")
    @Parameter(name = "id", description = "策略id")
    
    public ResponseResult<Void> deleteStrategyById(String id) {
        return strategyService.deleteStrategyById(id);
    }

    @PostMapping("cloneStrategy")
    @Operation(summary = "克隆策略数据")
    @WebLog("策略管理-克隆策略数据")
    @Parameters({
            @Parameter(name = "id", description = "策略id"),
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "strategyType", description = "策略类型 1-边缘网关 2-云网关 3-云平台")
    })
    
    public ResponseResult<Void> cloneStrategy(String id, String siteId, String deviceId, Integer strategyType) {
        return strategyService.cloneStrategy(id, siteId, deviceId, strategyType);
    }

    @PostMapping("issuedStrategy")
    @Operation(summary = "配置下发")
    @WebLog("策略管理-配置下发")
    @Parameter(name = "id", description = "策略id")
    
    public ResponseResult<Void> issuedStrategy(String id) {
        return strategyService.issuedStrategy(id);
    }

}
