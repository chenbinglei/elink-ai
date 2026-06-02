package com.sunmax.configure.controller;

import com.alibaba.fastjson.JSON;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.dto.PelDetailDto;
import com.sunmax.configure.dto.PelListDto;
import com.sunmax.configure.service.PelService;
import com.sunmax.configure.vo.PelChangeVo;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("pel")
@Api(tags = "图元管理控制层")
public class PelController {

    @Resource
    private PelService pelService;

    @PostMapping("saveGraphPel")
    @ApiOperation("新建或编辑图元数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "图元文件", dataType = "File"),
            @ApiImplicitParam(name = "dataFile", value = "数据文件", dataType = "File")
    })
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> savePel(PelChangeVo pelChangeVo, MultipartFile file, MultipartFile dataFile) {
        return pelService.savePel(pelChangeVo, file, dataFile);
    }

    @PostMapping("deletePelByIds")
    @ApiOperation("根据多个图元id删除图元数据")
    @ApiImplicitParam(name = "ids", value = "多个图元id", dataType = "String", required = true)
    @ApiOperationSupport(order = 2)
    public ResponseResult<Void> deletePelByIds(String ids) {
        return pelService.deletePelByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("queryPelList")
    @ApiOperation("查询图元管理列表")
    @ApiImplicitParam(name = "name", value = "名称(模糊查询)", dataType = "String")
    @ApiOperationSupport(order = 3)
    public ResponseResult<List<PelListDto>> queryPelList(String name) {
        return pelService.queryPelList(name);
    }

    @PostMapping("findPelById")
    @ApiOperation("根据图元id查询图元详情数据")
    @ApiImplicitParam(name = "id", value = "图元id", dataType = "String", required = true)
    @ApiOperationSupport(order = 4)
    public ResponseResult<PelDetailDto> findPelById(String id) {
        return pelService.findPelById(id);
    }

    @PostMapping("findPelList")
    @ApiOperation("查询图元管理数据")
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<PelDetailDto>> findPelList() {
        return pelService.findPelList();
    }

}
