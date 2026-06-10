package com.sunmax.device.controller.model;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.model.ConfigListDto;
import com.sunmax.device.service.ConfigService;
import com.sunmax.device.vo.model.ConfigChangeVo;
import com.sunmax.device.vo.model.ConfigQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 模型标准功能管理控制层
 */
@RestController
@CrossOrigin
@RequestMapping("config")
@Tag(name = "参数配置管理控制层")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @PostMapping("saveConfig")
    @Operation(summary = "新增编辑参数配置")
    
    public ResponseResult<Void> saveConfig(ConfigChangeVo configChangeVo) {
        return configService.saveConfig(configChangeVo);
    }

    @PostMapping("queryConfigList")
    @Operation(summary = "查询参数配置列表")
    
    public ResponseResult<PageDto<ConfigListDto>> queryConfigList(ConfigQueryVo configQueryVo) {
        return configService.queryConfigList(configQueryVo);
    }

    @PostMapping("deleteConfigById")
    @Operation(summary = "根据主键id删除参数配置")
    
    @Parameter(name = "id", description = "参数配置id")
    public ResponseResult<Void> deleteConfigById(String id) {
        return configService.deleteConfigById(id);
    }

    @PostMapping("findConfigDetailById")
    @Operation(summary = "根据主键id查询参数配置详情")
    
    @Parameter(name = "id", description = "参数配置id")
    public ResponseResult<ConfigListDto> findConfigDetailById(String id) {
        return configService.findConfigDetailById(id);
    }

    @PostMapping("importConfigList")
    @Operation(summary = "导入参数配置数据")
    
    @Parameter(name = "file", description = "文件")
    public ResponseResult<ImportResultDto> importConfigList(MultipartFile file) {
        return configService.importConfigList(file);
    }

}
