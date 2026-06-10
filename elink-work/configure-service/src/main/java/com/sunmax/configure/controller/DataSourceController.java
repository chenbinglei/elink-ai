package com.sunmax.configure.controller;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.dto.DataSourceDetailDto;
import com.sunmax.configure.dto.DataSourceListDto;
import com.sunmax.configure.service.DataSourceService;
import com.sunmax.configure.vo.DataSourceChangeVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("datasource")
@Tag(name = "数据源管理控制层")
public class DataSourceController {

    @Resource
    private DataSourceService dataSourceService;

    @PostMapping("saveDataSource")
    @Operation(summary = "新建或编辑数据源")
    
    public ResponseResult<Void> saveDataSource(DataSourceChangeVo dataSourceChangeVo) {
        return dataSourceService.saveDataSource(dataSourceChangeVo);
    }

    @PostMapping("deleteDataSourceByIds")
    @Operation(summary = "根据多个数据源id删除数据源")
    @Parameter(name = "ids", description = "多个数据源id")
    
    public ResponseResult<Void> deleteDataSourceByIds(String ids) {
        return dataSourceService.deleteDataSourceByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("queryDataSourceList")
    @Operation(summary = "查询数据源管理列表")
    @Parameter(name = "name", description = "名称(模糊查询)")
    
    public ResponseResult<List<DataSourceListDto>> queryDataSourceList(String name) {
        return dataSourceService.queryDataSourceList(name);
    }

    @PostMapping("findDataSourceById")
    @Operation(summary = "根据数据源id查询数据源详情数据")
    @Parameter(name = "id", description = "数据源id")
    
    public ResponseResult<DataSourceDetailDto> findDataSourceById(String id) {
        return dataSourceService.findDataSourceById(id);
    }

}
