package com.sunmax.device.controller;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.GraphListDto;
import com.sunmax.device.dto.GraphTypeListDto;
import com.sunmax.device.service.VisualService;
import com.sunmax.device.vo.GraphChangeVo;
import com.sunmax.device.vo.GraphTypeChangeVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("visual")
@Api(tags = "可视化管理")
public class VisualController {

    @Autowired
    private VisualService visualService;

    @PostMapping("saveGraphType")
    @ApiOperation("新增编辑图形分类")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveGraphType(GraphTypeChangeVo graphTypeVo) {
        return visualService.saveGraphType(graphTypeVo);
    }

    @PostMapping("findGraphTypeListByTypeId")
    @ApiOperation("根据资产分类id查询图形分类列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<GraphTypeListDto>> findGraphTypeListByTypeId(String typeId) {
        return visualService.findGraphTypeListByTypeId(typeId);
    }

    @PostMapping("deleteGraphTypeById")
    @ApiOperation("删除图形分类")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParam(name = "id", value = "主键id", dataType = "String", required = true)
    public ResponseResult<Void> deleteGraphTypeById(String id) {
        return visualService.deleteGraphTypeById(id);
    }

    @PostMapping("saveGraph")
    @ApiOperation("新增或编辑图形")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Void> saveGraph(GraphChangeVo graphVo) {
        return visualService.saveGraph(graphVo);
    }

    @PostMapping("findGraphListByDeviceId")
    @ApiOperation("根据设备id查询图形列表")
    @ApiOperationSupport(order = 5)
    public ResponseResult<PageDto<GraphListDto>> findGraphListByDeviceId(String deviceId, Integer page, Integer size) {
        return visualService.findGraphListByDeviceId(deviceId, page, size);
    }

    @PostMapping("deleteGraphById")
    @ApiOperation("删除图形")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParam(name = "id", value = "主键id", dataType = "String", required = true)
    public ResponseResult<Void> deleteGraphById(String id) {
        return visualService.deleteGraphById(id);
    }

    @PostMapping("findGraphRelevancyListByDeviceId")
    @ApiOperation("根据设备id查询图形关联列表")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "站点/设备/子系统Id", dataType = "String", required = true),
            @ApiImplicitParam(name = "graphTypeCode", value = "图形分类标识", dataType = "String")
    })
    public ResponseResult<List<GraphListDto>> findGraphRelevancyListByDeviceId(String deviceId, String graphTypeCode) {
        return visualService.findGraphRelevancyListByDeviceId(deviceId, graphTypeCode);
    }
}
