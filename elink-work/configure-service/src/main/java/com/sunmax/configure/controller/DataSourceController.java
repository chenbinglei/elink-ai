package com.sunmax.configure.controller;

import com.alibaba.fastjson.JSON;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.dto.DataSourceDetailDto;
import com.sunmax.configure.dto.DataSourceListDto;
import com.sunmax.configure.service.DataSourceService;
import com.sunmax.configure.vo.DataSourceChangeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("datasource")
@Api(tags = "数据源管理控制层")
public class DataSourceController {

    @Resource
    private DataSourceService dataSourceService;

    @PostMapping("saveDataSource")
    @ApiOperation("新建或编辑数据源")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveDataSource(DataSourceChangeVo dataSourceChangeVo) {
        return dataSourceService.saveDataSource(dataSourceChangeVo);
    }

    @PostMapping("deleteDataSourceByIds")
    @ApiOperation("根据多个数据源id删除数据源")
    @ApiImplicitParam(name = "ids", value = "多个数据源id", dataType = "String", required = true)
    @ApiOperationSupport(order = 2)
    public ResponseResult<Void> deleteDataSourceByIds(String ids) {
        return dataSourceService.deleteDataSourceByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("queryDataSourceList")
    @ApiOperation("查询数据源管理列表")
    @ApiImplicitParam(name = "name", value = "名称(模糊查询)", dataType = "String")
    @ApiOperationSupport(order = 3)
    public ResponseResult<List<DataSourceListDto>> queryDataSourceList(String name) {
        return dataSourceService.queryDataSourceList(name);
    }

    @PostMapping("findDataSourceById")
    @ApiOperation("根据数据源id查询数据源详情数据")
    @ApiImplicitParam(name = "id", value = "数据源id", dataType = "String", required = true)
    @ApiOperationSupport(order = 4)
    public ResponseResult<DataSourceDetailDto> findDataSourceById(String id) {
        return dataSourceService.findDataSourceById(id);
    }

}
