package com.sunmax.device.controller.model;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.model.ReaListDto;
import com.sunmax.device.service.ReaService;
import com.sunmax.device.vo.model.ReaChangeVo;
import com.sunmax.device.vo.model.ReaQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 模型扩展属性管理控制层
 */
@RestController
@CrossOrigin
@RequestMapping("rea")
@Api(tags = "模型扩展属性管理")
@ApiIgnore()
public class ReaController {

    @Autowired
    private ReaService reaService;

    @PostMapping("saveSea")
    @ApiOperation("新增编辑模型扩展属性")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveSea(ReaChangeVo reaChangeVo) {
        return reaService.saveSea(reaChangeVo);
    }

    @PostMapping("querySeaList")
    @ApiOperation("查询模型扩展属性列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<ReaListDto>> querySeaList(ReaQueryVo reaQueryVo) {
        return reaService.querySeaList(reaQueryVo);
    }

    @PostMapping("deleteSeaById")
    @ApiOperation("删除模型扩展属性")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParam(name = "id", value = "模型扩展属性id", dataType = "String", required = true)
    public ResponseResult<Void> deleteSeaById(String id) {
        return reaService.deleteSeaById(id);
    }

    @PostMapping("findSeaDetailById")
    @ApiOperation("根据主键id查询扩展属性详情")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "id", value = "模型扩展属性id", dataType = "String", required = true)
    public ResponseResult<ReaListDto> findSeaDetailById(String id) {
        return reaService.findSeaDetailById(id);
    }

}
