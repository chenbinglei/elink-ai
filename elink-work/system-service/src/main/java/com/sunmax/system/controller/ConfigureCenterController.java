package com.sunmax.system.controller;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.OperatorInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.dto.*;
import com.sunmax.system.service.ConfigureCenterService;
import com.sunmax.system.vo.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("configureCenter")
@Api(tags = "配置中心")
public class ConfigureCenterController {

    @Autowired
    private ConfigureCenterService configureCenterService;

    @PostMapping("saveOrUpdateProduct")
    @ApiOperation("添加或修改产品信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> saveOrUpdateProduct(ProductChangeVo productVo) {
        return configureCenterService.saveOrUpdateProduct(productVo);
    }

    @PostMapping("queryProductList")
    @ApiOperation("查询产品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品id", dataType = "String"),
            @ApiImplicitParam(name = "keyWords", value = "关键词", dataType = "String")
    })
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<ProductListDto>> queryProductList(String productId, String keyWords) {
        return configureCenterService.queryProductList(productId, keyWords);
    }

    @PostMapping("deleteModuleById")
    @ApiOperation("根据模块id删除模块数据")
    @ApiImplicitParam(name = "id", value = "模块id", dataType = "String", required = true)
    @ApiOperationSupport(order = 3)
    public ResponseResult<String> deleteModuleById(String id) {
        return configureCenterService.deleteModuleById(id);
    }

    @PostMapping("saveOrUpdatePermission")
    @ApiOperation("添加或修改权限信息")
    @ApiOperationSupport(order = 4)
    public ResponseResult<String> saveOrUpdatePermission(PermissionChangeVo permissionVo) {
        return configureCenterService.saveOrUpdatePermission(permissionVo);
    }

    @PostMapping("findPermissionByModuleId")
    @ApiOperation("根据模块id查询权限数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "模块id", dataType = "String", required = true)
    })
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<PermissionListDto>> findPermissionByModuleId(String moduleId) {
        return configureCenterService.findPermissionByModuleId(moduleId);
    }

    @PostMapping("deletePermissionById")
    @ApiOperation("根据id删除权限数据")
    @ApiImplicitParam(name = "id", value = "权限id", dataType = "String", required = true)
    @ApiOperationSupport(order = 6)
    public ResponseResult<String> deletePermissionById(String id) {
        return configureCenterService.deletePermissionById(id);
    }

    @PostMapping("saveOrUpdatePlatformInfo")
    @ApiOperation("添加或修改平台信息")
    @ApiOperationSupport(order = 7)
    public ResponseResult<String> saveOrUpdatePlatformInfo(ChargePlatformInfoVo chargePlatformInfoVo) {
        return configureCenterService.saveOrUpdatePlatformInfo(chargePlatformInfoVo);
    }

    @PostMapping("deletePlatformInfoById")
    @ApiOperation("根据平台id删除平台信息")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParam(name = "id", value = "平台id", dataType = "String", required = true)
    public ResponseResult<String> deletePlatformInfoById(String id) {
        return configureCenterService.deletePlatformInfoById(id);
    }

    @PostMapping("findPlatformInfoListByPage")
    @ApiOperation("分页查询充电平台列表信息")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "模糊查询关键字", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页数", dataType = "Integer"),
            @ApiImplicitParam(name = "size", value = "条数(可传0，传0则查询全部)", dataType = "Integer")
    })
    public ResponseResult<?> findPlatformInfoListByPage(String keyword, Integer page, Integer size) {
        return configureCenterService.findPlatformInfoListByPage(keyword, page, size);
    }

    @PostMapping("getProtocolList")
    @ApiOperation("获取接入协议列表")
    @ApiOperationSupport(order = 10)
    public ResponseResult<List<ProtocolListDto>> getProtocolList() {
        return configureCenterService.getProtocolList();
    }

    @PostMapping("findProtocolFieldByCode")
    @ApiOperation("根据协议标识查询协议字段列表")
    @ApiImplicitParam(name = "code", value = "协议标识", dataType = "String", required = true)
    @ApiOperationSupport(order = 11)
    public ResponseResult<List<ProtocolFieldDto>> findProtocolFieldByCode(String code) {
        return configureCenterService.findProtocolFieldByCode(code);
    }

    @PostMapping("saveDataForward")
    @ApiOperation("新建或编辑数据转发数据")
    @ApiOperationSupport(order = 12)
    public ResponseResult<Void> saveDataForward(DataForwardChangeVo dataForwardVo) {
        return configureCenterService.saveDataForward(dataForwardVo);
    }

    @PostMapping("queryDataForwardList")
    @ApiOperation("查询数据转发列表数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键词(通道名称或协议标识)", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "Integer", required = true)
    })
    @ApiOperationSupport(order = 13)
    public ResponseResult<PageDto<DataForwardListDto>> queryDataForwardList(String keyword, Integer page, Integer size) {
        return configureCenterService.queryDataForwardList(keyword, page, size);
    }

    @PostMapping("findDataForwardById")
    @ApiOperation("根据主键id查询数据转发详情数据")
    @ApiImplicitParam(name = "id", value = "主键id", dataType = "String", required = true)
    @ApiOperationSupport(order = 14)
    public ResponseResult<DataForwardDetailDto> findDataForwardById(String id) {
        return configureCenterService.findDataForwardById(id);
    }

    @PostMapping("updateForwardStatus")
    @ApiOperation("更改数据转发状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键id", dataType = "String", required = true),
            @ApiImplicitParam(name = "status", value = "状态 1-启用 2-断开", dataType = "Integer", required = true)
    })
    @ApiOperationSupport(order = 15)
    public ResponseResult<Void> updateForwardStatus(String id, Integer status) {
        return configureCenterService.updateForwardStatus(id, status);
    }

    @PostMapping("deleteDataForwardById")
    @ApiOperation("根据主键id删除数据转发数据")
    @ApiImplicitParam(name = "id", value = "主键id", dataType = "String", required = true)
    @ApiOperationSupport(order = 16)
    public ResponseResult<Void> deleteDataForwardById(String id) {
        return configureCenterService.deleteDataForwardById(id);
    }

    @PostMapping("findDataConfigByForwardId")
    @ApiOperation("根据数据转发id查询数据配置数据")
    @ApiImplicitParam(name = "forwardId", value = "数据转发id", dataType = "String", required = true)
    @ApiOperationSupport(order = 17)
    public ResponseResult<List<DataConfigListDto>> findDataConfigByForwardId(String forwardId) {
        return configureCenterService.findDataConfigByForwardId(forwardId);
    }

    @PostMapping("batchUpdateDataConfig")
    @ApiOperation("批量编辑数据配置数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "forwardId", value = "数据转发id", dataType = "String"),
            @ApiImplicitParam(name = "dataConfigVos", value = "多个数据配置数据(数组格式)", dataType = "String", required = true)
    })
    @ApiOperationSupport(order = 18)
    public ResponseResult<Void> batchUpdateDataConfig(String forwardId, String dataConfigVos) {
        return configureCenterService.batchUpdateDataConfig(forwardId, JSON.parseArray(dataConfigVos, DataConfigChangeVo.class));
    }

    @PostMapping("saveOrUpdateOperatorInfo")
    @ApiOperation("保存或编辑运营商信息")
    @ApiOperationSupport(order = 19)
    public ResponseResult<String> saveOrUpdateOperatorInfo(OperatorInfoVo operatorInfoVo) {
        return configureCenterService.saveOrUpdateOperatorInfo(operatorInfoVo);
    }

    @PostMapping("findOperatorDetailsById")
    @ApiOperation("根据id查询运营商信息")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "运营商唯一id", dataType = "String", required = true)
    })
    public ResponseResult<OperatorInfoDto> findOperatorDetailsById(String id) {
        return configureCenterService.findOperatorDetailsById(id);
    }

    @PostMapping("deleteOperatorInfoById")
    @ApiOperation("根据id删除运营商信息")
    @ApiOperationSupport(order = 21)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "运营商唯一id", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteOperatorInfoById(String id) {
        return configureCenterService.deleteOperatorInfoById(id);
    }

    @PostMapping("findOperatorInfoByPage")
    @ApiOperation("分页查询运营商信息")
    @ApiOperationSupport(order = 22)
    public ResponseResult<PageDto<OperatorInfoDto>> findOperatorInfoByPage(OperatorListQueryVo operatorListQueryVo) {
        return configureCenterService.findOperatorInfoByPage(operatorListQueryVo);
    }

    @PostMapping("findAllOperatorInfoList")
    @ApiOperation("查询所有运营商列表")
    @ApiOperationSupport(order = 23)
    public ResponseResult<List<OperatorInfoDto>> findAllOperatorInfoList() {
        return configureCenterService.findAllOperatorInfoList();
    }
}
