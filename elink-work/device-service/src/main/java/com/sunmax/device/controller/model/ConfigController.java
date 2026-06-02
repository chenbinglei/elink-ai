package com.sunmax.device.controller.model;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.model.ConfigListDto;
import com.sunmax.device.service.ConfigService;
import com.sunmax.device.vo.model.ConfigChangeVo;
import com.sunmax.device.vo.model.ConfigQueryVo;
import io.swagger.annotations.*;
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
@Api(tags = "参数配置管理控制层")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @PostMapping("saveConfig")
    @ApiOperation("新增编辑参数配置")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveConfig(ConfigChangeVo configChangeVo) {
        return configService.saveConfig(configChangeVo);
    }

    @PostMapping("queryConfigList")
    @ApiOperation("查询参数配置列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<ConfigListDto>> queryConfigList(ConfigQueryVo configQueryVo) {
        return configService.queryConfigList(configQueryVo);
    }

    @PostMapping("deleteConfigById")
    @ApiOperation("根据主键id删除参数配置")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParam(name = "id", value = "参数配置id", dataType = "String", required = true)
    public ResponseResult<Void> deleteConfigById(String id) {
        return configService.deleteConfigById(id);
    }

    @PostMapping("findConfigDetailById")
    @ApiOperation("根据主键id查询参数配置详情")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "id", value = "参数配置id", dataType = "String", required = true)
    public ResponseResult<ConfigListDto> findConfigDetailById(String id) {
        return configService.findConfigDetailById(id);
    }

    @PostMapping("importConfigList")
    @ApiOperation("导入参数配置数据")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParam(name = "file", value = "文件", dataType = "File", required = true)
    public ResponseResult<ImportResultDto> importConfigList(MultipartFile file) {
        return configService.importConfigList(file);
    }

}
