package com.sunmax.configure.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.dto.PelDetailDto;
import com.sunmax.configure.dto.PelListDto;
import com.sunmax.configure.service.PelService;
import com.sunmax.configure.vo.PelChangeVo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("pel")
@Tag(name = "图元管理控制层")
public class PelController {

    @Resource
    private PelService pelService;

    @PostMapping("saveGraphPel")
    @Operation(summary = "新建或编辑图元数据")
    @Parameters({
            @Parameter(name = "file", description = "图元文件"),
            @Parameter(name = "dataFile", description = "数据文件")
    })
    
    public ResponseResult<Void> savePel(PelChangeVo pelChangeVo, MultipartFile file, MultipartFile dataFile) {
        return pelService.savePel(pelChangeVo, file, dataFile);
    }

    @PostMapping("deletePelByIds")
    @Operation(summary = "根据多个图元id删除图元数据")
    @Parameter(name = "ids", description = "多个图元id")
    
    public ResponseResult<Void> deletePelByIds(String ids) {
        return pelService.deletePelByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("queryPelList")
    @Operation(summary = "查询图元管理列表")
    @Parameter(name = "name", description = "名称(模糊查询)")
    
    public ResponseResult<List<PelListDto>> queryPelList(String name) {
        return pelService.queryPelList(name);
    }

    @PostMapping("findPelById")
    @Operation(summary = "根据图元id查询图元详情数据")
    @Parameter(name = "id", description = "图元id")
    
    public ResponseResult<PelDetailDto> findPelById(String id) {
        return pelService.findPelById(id);
    }

    @PostMapping("findPelList")
    @Operation(summary = "查询图元管理数据")
    
    public ResponseResult<List<PelDetailDto>> findPelList() {
        return pelService.findPelList();
    }

}
