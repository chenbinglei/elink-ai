package com.sunmax.system.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.OperatorInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.dto.*;
import com.sunmax.system.service.ConfigureCenterService;
import com.sunmax.system.vo.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("configureCenter")
@Tag(name = "配置中心")
public class ConfigureCenterController {

    @Autowired
    private ConfigureCenterService configureCenterService;

    @PostMapping("saveOrUpdateProduct")
    @Operation(summary = "添加或修改产品信息")
    
    public ResponseResult<String> saveOrUpdateProduct(ProductChangeVo productVo) {
        return configureCenterService.saveOrUpdateProduct(productVo);
    }

    @PostMapping("queryProductList")
    @Operation(summary = "查询产品列表")
    @Parameters({
            @Parameter(name = "productId", description = "产品id"),
            @Parameter(name = "keyWords", description = "关键词")
    })
    
    public ResponseResult<List<ProductListDto>> queryProductList(String productId, String keyWords) {
        return configureCenterService.queryProductList(productId, keyWords);
    }

    @PostMapping("deleteModuleById")
    @Operation(summary = "根据模块id删除模块数据")
    @Parameter(name = "id", description = "模块id")
    
    public ResponseResult<String> deleteModuleById(String id) {
        return configureCenterService.deleteModuleById(id);
    }

    @PostMapping("saveOrUpdatePermission")
    @Operation(summary = "添加或修改权限信息")
    
    public ResponseResult<String> saveOrUpdatePermission(PermissionChangeVo permissionVo) {
        return configureCenterService.saveOrUpdatePermission(permissionVo);
    }

    @PostMapping("findPermissionByModuleId")
    @Operation(summary = "根据模块id查询权限数据")
    @Parameters({
            @Parameter(name = "moduleId", description = "模块id")
    })
    
    public ResponseResult<List<PermissionListDto>> findPermissionByModuleId(String moduleId) {
        return configureCenterService.findPermissionByModuleId(moduleId);
    }

    @PostMapping("deletePermissionById")
    @Operation(summary = "根据id删除权限数据")
    @Parameter(name = "id", description = "权限id")
    
    public ResponseResult<String> deletePermissionById(String id) {
        return configureCenterService.deletePermissionById(id);
    }

    @PostMapping("saveOrUpdatePlatformInfo")
    @Operation(summary = "添加或修改平台信息")
    
    public ResponseResult<String> saveOrUpdatePlatformInfo(ChargePlatformInfoVo chargePlatformInfoVo) {
        return configureCenterService.saveOrUpdatePlatformInfo(chargePlatformInfoVo);
    }

    @PostMapping("deletePlatformInfoById")
    @Operation(summary = "根据平台id删除平台信息")
    
    @Parameter(name = "id", description = "平台id")
    public ResponseResult<String> deletePlatformInfoById(String id) {
        return configureCenterService.deletePlatformInfoById(id);
    }

    @PostMapping("findPlatformInfoListByPage")
    @Operation(summary = "分页查询充电平台列表信息")
    
    @Parameters({
            @Parameter(name = "keyword", description = "模糊查询关键字"),
            @Parameter(name = "page", description = "页数"),
            @Parameter(name = "size", description = "条数(可传0，传0则查询全部)")
    })
    public ResponseResult<?> findPlatformInfoListByPage(String keyword, Integer page, Integer size) {
        return configureCenterService.findPlatformInfoListByPage(keyword, page, size);
    }

    @PostMapping("getProtocolList")
    @Operation(summary = "获取接入协议列表")
    
    public ResponseResult<List<ProtocolListDto>> getProtocolList() {
        return configureCenterService.getProtocolList();
    }

    @PostMapping("findProtocolFieldByCode")
    @Operation(summary = "根据协议标识查询协议字段列表")
    @Parameter(name = "code", description = "协议标识")
    
    public ResponseResult<List<ProtocolFieldDto>> findProtocolFieldByCode(String code) {
        return configureCenterService.findProtocolFieldByCode(code);
    }

    @PostMapping("saveDataForward")
    @Operation(summary = "新建或编辑数据转发数据")
    
    public ResponseResult<Void> saveDataForward(DataForwardChangeVo dataForwardVo) {
        return configureCenterService.saveDataForward(dataForwardVo);
    }

    @PostMapping("queryDataForwardList")
    @Operation(summary = "查询数据转发列表数据")
    @Parameters({
            @Parameter(name = "keyword", description = "关键词(通道名称或协议标识)"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    
    public ResponseResult<PageDto<DataForwardListDto>> queryDataForwardList(String keyword, Integer page, Integer size) {
        return configureCenterService.queryDataForwardList(keyword, page, size);
    }

    @PostMapping("findDataForwardById")
    @Operation(summary = "根据主键id查询数据转发详情数据")
    @Parameter(name = "id", description = "主键id")
    
    public ResponseResult<DataForwardDetailDto> findDataForwardById(String id) {
        return configureCenterService.findDataForwardById(id);
    }

    @PostMapping("updateForwardStatus")
    @Operation(summary = "更改数据转发状态")
    @Parameters({
            @Parameter(name = "id", description = "主键id"),
            @Parameter(name = "status", description = "状态 1-启用 2-断开")
    })
    
    public ResponseResult<Void> updateForwardStatus(String id, Integer status) {
        return configureCenterService.updateForwardStatus(id, status);
    }

    @PostMapping("deleteDataForwardById")
    @Operation(summary = "根据主键id删除数据转发数据")
    @Parameter(name = "id", description = "主键id")
    
    public ResponseResult<Void> deleteDataForwardById(String id) {
        return configureCenterService.deleteDataForwardById(id);
    }

    @PostMapping("findDataConfigByForwardId")
    @Operation(summary = "根据数据转发id查询数据配置数据")
    @Parameter(name = "forwardId", description = "数据转发id")
    
    public ResponseResult<List<DataConfigListDto>> findDataConfigByForwardId(String forwardId) {
        return configureCenterService.findDataConfigByForwardId(forwardId);
    }

    @PostMapping("batchUpdateDataConfig")
    @Operation(summary = "批量编辑数据配置数据")
    @Parameters({
            @Parameter(name = "forwardId", description = "数据转发id"),
            @Parameter(name = "dataConfigVos", description = "多个数据配置数据(数组格式)")
    })
    
    public ResponseResult<Void> batchUpdateDataConfig(String forwardId, String dataConfigVos) {
        return configureCenterService.batchUpdateDataConfig(forwardId, JSON.parseArray(dataConfigVos, DataConfigChangeVo.class));
    }

    @PostMapping("saveOrUpdateOperatorInfo")
    @Operation(summary = "保存或编辑运营商信息")
    
    public ResponseResult<String> saveOrUpdateOperatorInfo(OperatorInfoVo operatorInfoVo) {
        return configureCenterService.saveOrUpdateOperatorInfo(operatorInfoVo);
    }

    @PostMapping("findOperatorDetailsById")
    @Operation(summary = "根据id查询运营商信息")
    
    @Parameters({
            @Parameter(name = "id", description = "运营商唯一id")
    })
    public ResponseResult<OperatorInfoDto> findOperatorDetailsById(String id) {
        return configureCenterService.findOperatorDetailsById(id);
    }

    @PostMapping("deleteOperatorInfoById")
    @Operation(summary = "根据id删除运营商信息")
    
    @Parameters({
            @Parameter(name = "id", description = "运营商唯一id")
    })
    public ResponseResult<String> deleteOperatorInfoById(String id) {
        return configureCenterService.deleteOperatorInfoById(id);
    }

    @PostMapping("findOperatorInfoByPage")
    @Operation(summary = "分页查询运营商信息")
    
    public ResponseResult<PageDto<OperatorInfoDto>> findOperatorInfoByPage(OperatorListQueryVo operatorListQueryVo) {
        return configureCenterService.findOperatorInfoByPage(operatorListQueryVo);
    }

    @PostMapping("findAllOperatorInfoList")
    @Operation(summary = "查询所有运营商列表")
    
    public ResponseResult<List<OperatorInfoDto>> findAllOperatorInfoList() {
        return configureCenterService.findAllOperatorInfoList();
    }
}
