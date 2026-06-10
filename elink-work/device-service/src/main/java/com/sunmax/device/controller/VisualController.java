package com.sunmax.device.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.GraphListDto;
import com.sunmax.device.dto.GraphTypeListDto;
import com.sunmax.device.service.VisualService;
import com.sunmax.device.vo.GraphChangeVo;
import com.sunmax.device.vo.GraphTypeChangeVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("visual")
@Tag(name = "可视化管理")
public class VisualController {

    @Autowired
    private VisualService visualService;

    @PostMapping("saveGraphType")
    @Operation(summary = "新增编辑图形分类")
    
    public ResponseResult<Void> saveGraphType(GraphTypeChangeVo graphTypeVo) {
        return visualService.saveGraphType(graphTypeVo);
    }

    @PostMapping("findGraphTypeListByTypeId")
    @Operation(summary = "根据资产分类id查询图形分类列表")
    
    public ResponseResult<List<GraphTypeListDto>> findGraphTypeListByTypeId(String typeId) {
        return visualService.findGraphTypeListByTypeId(typeId);
    }

    @PostMapping("deleteGraphTypeById")
    @Operation(summary = "删除图形分类")
    
    @Parameter(name = "id", description = "主键id")
    public ResponseResult<Void> deleteGraphTypeById(String id) {
        return visualService.deleteGraphTypeById(id);
    }

    @PostMapping("saveGraph")
    @Operation(summary = "新增或编辑图形")
    
    public ResponseResult<Void> saveGraph(GraphChangeVo graphVo) {
        return visualService.saveGraph(graphVo);
    }

    @PostMapping("findGraphListByDeviceId")
    @Operation(summary = "根据设备id查询图形列表")
    
    public ResponseResult<PageDto<GraphListDto>> findGraphListByDeviceId(String deviceId, Integer page, Integer size) {
        return visualService.findGraphListByDeviceId(deviceId, page, size);
    }

    @PostMapping("deleteGraphById")
    @Operation(summary = "删除图形")
    
    @Parameter(name = "id", description = "主键id")
    public ResponseResult<Void> deleteGraphById(String id) {
        return visualService.deleteGraphById(id);
    }

    @PostMapping("findGraphRelevancyListByDeviceId")
    @Operation(summary = "根据设备id查询图形关联列表")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "站点/设备/子系统Id"),
            @Parameter(name = "graphTypeCode", description = "图形分类标识")
    })
    public ResponseResult<List<GraphListDto>> findGraphRelevancyListByDeviceId(String deviceId, String graphTypeCode) {
        return visualService.findGraphRelevancyListByDeviceId(deviceId, graphTypeCode);
    }
}
