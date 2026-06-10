package com.sunmax.device.controller.model;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.model.ReaListDto;
import com.sunmax.device.service.ReaService;
import com.sunmax.device.vo.model.ReaChangeVo;
import com.sunmax.device.vo.model.ReaQueryVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * 模型扩展属性管理控制层
 */
@RestController
@CrossOrigin
@RequestMapping("rea")
@Tag(name = "模型扩展属性管理")
@Hidden()
public class ReaController {

    @Autowired
    private ReaService reaService;

    @PostMapping("saveSea")
    @Operation(summary = "新增编辑模型扩展属性")
    
    public ResponseResult<Void> saveSea(ReaChangeVo reaChangeVo) {
        return reaService.saveSea(reaChangeVo);
    }

    @PostMapping("querySeaList")
    @Operation(summary = "查询模型扩展属性列表")
    
    public ResponseResult<PageDto<ReaListDto>> querySeaList(ReaQueryVo reaQueryVo) {
        return reaService.querySeaList(reaQueryVo);
    }

    @PostMapping("deleteSeaById")
    @Operation(summary = "删除模型扩展属性")
    
    @Parameter(name = "id", description = "模型扩展属性id")
    public ResponseResult<Void> deleteSeaById(String id) {
        return reaService.deleteSeaById(id);
    }

    @PostMapping("findSeaDetailById")
    @Operation(summary = "根据主键id查询扩展属性详情")
    
    @Parameter(name = "id", description = "模型扩展属性id")
    public ResponseResult<ReaListDto> findSeaDetailById(String id) {
        return reaService.findSeaDetailById(id);
    }

}
