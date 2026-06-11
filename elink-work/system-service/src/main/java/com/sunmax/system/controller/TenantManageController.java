package com.sunmax.system.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.system.dto.AccountListDto;
import com.sunmax.system.dto.PermissionListDto;
import com.sunmax.system.service.TenantManageService;
import com.sunmax.system.vo.AccountVo;
import com.sunmax.system.vo.OrganStructureVo;
import com.sunmax.system.vo.TenantInfoVo;
import com.sunmax.system.vo.TenantListQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户管理
 */
@RestController
@CrossOrigin
@RequestMapping("tenantManage")
@Tag(name = "租户管理")
public class TenantManageController {

    @Autowired
    private TenantManageService tenantManageService;

    @PostMapping("saveOrUpdateTenantInfo")
    @Operation(summary = "保存或编辑租户信息")
    @WebLog("保存或编辑租户信息")
    
    @Parameters({
            @Parameter(name = "businessLicenseFile", description = "营业执照文件"),
            @Parameter(name = "logoFile", description = "标识照片文件")
    })
    public ResponseResult<String> saveOrUpdateTenantInfo(TenantInfoVo tenantInfoVo, MultipartFile businessLicenseFile, MultipartFile logoFile, String userId) {
        return tenantManageService.saveOrUpdateTenantInfo(tenantInfoVo, businessLicenseFile, logoFile, userId);
    }

    @PostMapping("updateTenantStateById")
    @Operation(summary = "根据id更新租户状态")
    
    @Parameters({
            @Parameter(name = "id", description = "租户id"),
            @Parameter(name = "tenantState", description = "租户状态 0-关闭 1-开启")
    })
    public ResponseResult<String> updateTenantStateById(String id, Integer tenantState) {
        return tenantManageService.updateTenantStateById(id, tenantState);
    }

    @PostMapping("deleteTenantInfoById")
    @Operation(summary = "根据id删除租户信息")
    
    @Parameters({
            @Parameter(name = "id", description = "租户id")
    })
    public ResponseResult<String> deleteTenantInfoById(String id) {
        return tenantManageService.deleteTenantInfoById(id);
    }

    @PostMapping("findTenantInfoByPage")
    @Operation(summary = "分页查询租户信息")
    
    public ResponseResult<?> findTenantInfoByPage(TenantListQueryVo tenantListQueryVo) {
        return tenantManageService.findTenantInfoByPage(tenantListQueryVo);
    }

    @PostMapping("findTenantDetailsById")
    @Operation(summary = "根据id查询租户详情信息")
    
    @Parameters({
            @Parameter(name = "id", description = "租户id")
    })
    public ResponseResult<TenantDetailsDto> findTenantDetailsById(String id) {
        return tenantManageService.findTenantDetailsById(id);
    }

    @PostMapping("saveOrUpdateOrganStructure")
    @Operation(summary = "保存或编辑组织架构信息")
    
    public ResponseResult<String> saveOrUpdateOrganStructure(OrganStructureVo organStructureVo) {
        return tenantManageService.saveOrUpdateOrganStructure(organStructureVo);
    }

    @PostMapping("deleteOrganStructureById")
    @Operation(summary = "根据组织架构id删除指定组织架构信息")
    
    @Parameters({
            @Parameter(name = "organId", description = "组织架构id")
    })
    public ResponseResult<String> deleteOrganStructureById(String organId) {
        return tenantManageService.deleteOrganStructureById(organId);
    }

    @PostMapping("findOrganStructureByTenantId")
    @Operation(summary = "根据租户id查询租户下组织架构信息")
    
    @Parameters({
            @Parameter(name = "tenantId", description = "租户id")
    })
    public ResponseResult<List<OrganStructureTreeDto>> findOrganStructureByTenantId(String tenantId) {
        return tenantManageService.findOrganStructureByTenantId(tenantId);
    }

    @PostMapping("addOrganEmpowerInfo")
    @Operation(summary = "添加资产授权信息")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "站点id(多个以逗号分割)"),
            @Parameter(name = "authority", description = "权限 1-只读 2-读写"),
            @Parameter(name = "organId", description = "组织架构id"),
            @Parameter(name = "tenantId", description = "租户id")
    })
    public ResponseResult<String> addOrganEmpowerInfo(String siteIds, Integer authority, String organId, String tenantId) {
        return tenantManageService.addOrganEmpowerInfo(Arrays.stream(siteIds.split(",")).map(String::trim).collect(Collectors.toList()), authority, organId, tenantId);
    }

    @PostMapping("updateEmpowerAuthorityById")
    @Operation(summary = "根据id修改资产授权信息")
    @WebLog("修改资产授权信息")
    
    @Parameters({
            @Parameter(name = "empowerId", description = "授权id"),
            @Parameter(name = "authority", description = "权限 1-只读 2-读写"),
            @Parameter(name = "empowerType", description = "资产类型 1-租户 2-企业")
    })
    public ResponseResult<String> updateEmpowerAuthorityById(String empowerId, Integer authority, Integer empowerType) {
        return tenantManageService.updateEmpowerAuthorityById(empowerId, authority, empowerType);
    }

    @PostMapping("deleteEmpowerInfoById")
    @Operation(summary = "根据id删除资产授权信息")
    @WebLog("删除资产授权信息")
    
    @Parameters({
            @Parameter(name = "empowerId", description = "授权id"),
            @Parameter(name = "empowerType", description = "资产类型 1-租户 2-企业")
    })
    public ResponseResult<String> deleteEmpowerInfoById(String empowerId, Integer empowerType) {
        return tenantManageService.deleteEmpowerInfoById(empowerId, empowerType);
    }

    @PostMapping("findEmpowerListByPage")
    @Operation(summary = "根据组织机构id分页查询资产授权列表")
    
    @Parameters({
//            @Parameter(name = "organId", description = "组织架构id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<OrganEmpowerListDto>> findEmpowerListByPage(String organId, Integer page, Integer size) {
        return tenantManageService.findEmpowerListByPage(organId, page, size);
    }

    @PostMapping("saveTenantApplyEmpowerInfo")
    @Operation(summary = "保存租户应用授权信息")
    
    @Parameters({
            @Parameter(name = "tenantApplyEmpowerVos", description = "配置租户应用授权数据对象字符串")
    })
    public ResponseResult<String> saveTenantApplyEmpowerInfo(String tenantApplyEmpowerVos) {
        return tenantManageService.saveTenantApplyEmpowerInfo(tenantApplyEmpowerVos);
    }

    @PostMapping("saveTenantApplyInfo")
    @Operation(summary = "保存租户应用授权信息(新版)")
    
    @Parameters({
            @Parameter(name = "tenantId", description = "租户id"),
            @Parameter(name = "moduleId", description = "模块id"),
            @Parameter(name = "tenantApplyVos", description = "配置租户应用授权对象数组字符串 例如[{\"permissionId\": \"权限id1\",\"operate\": \"1\"},{\"permissionId\": \"权限id2\",\"operate\": \"1\"}]")
    })
    public ResponseResult<Void> saveTenantApplyInfo(String tenantId, String moduleId, String tenantApplyVos) {
        return tenantManageService.saveTenantApplyInfo(tenantId, moduleId, tenantApplyVos);
    }

    @PostMapping("findTenantApplyEmpowerInfoById")
    @Operation(summary = "查询指定租户指定模块下配置的应用授权数据")
    
    @Parameters({
            @Parameter(name = "tenantId", description = "租户id"),
            @Parameter(name = "moduleId", description = "模块id")
    })
    public ResponseResult<List<PermissionListDto>> findTenantApplyEmpowerInfoById(String tenantId, String moduleId) {
        return tenantManageService.findTenantApplyEmpowerInfoById(tenantId, moduleId);
    }

    @PostMapping("findOrganEmpowerSiteList")
    @Operation(summary = "查询资产授权站点列表信息")
    
    @Parameters({
            @Parameter(name = "tenantId", description = "租户id"),
            @Parameter(name = "siteNameLike", description = "站点名称模糊查询"),
            @Parameter(name = "isQueryAll", description = "是否查询全部 1-是")
    })
    public ResponseResult<List<SiteInfoDto>> findOrganEmpowerSiteList(String tenantId, String siteNameLike, Integer isQueryAll) {
        return tenantManageService.findOrganEmpowerSiteList(tenantId, siteNameLike, isQueryAll);
    }

    @PostMapping("findControlPermissionListByUserId")
    @Operation(summary = "根据用户id查询控件权限列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id")
    })
    public ResponseResult<List<PermissionListDto>> findControlPermissionListByUserId(String userId) {
        return tenantManageService.findControlPermissionListByUserId(userId);
    }

    @PostMapping("findOrganStructureListByTenantId")
    @Operation(summary = "根据租户id查询组织架构信息列表")
    
    @Parameters({
            @Parameter(name = "tenantId", description = "租户id")
    })
    public ResponseResult<List<OrganStructureListDto>> findOrganStructureListByTenantId(String tenantId) {
        return tenantManageService.findOrganStructureListByTenantId(tenantId);
    }

    @PostMapping("findUserListByTenantId")
    @Operation(summary = "根据租户id查询用户列表")
    
    @Parameters({
            @Parameter(name = "tenantId", description = "租户id")
    })
    public ResponseResult<List<UserDto>> findUserListByTenantId(String tenantId) {
        return tenantManageService.findUserListByTenantId(tenantId);
    }

    @PostMapping("findTenantOrganSiteList")
    @Operation(summary = "查询租户下组织架构资产授权站点列表")
    
    @Parameters({
            @Parameter(name = "tenantId", description = "租户id"),
            @Parameter(name = "organId", description = "组织架构id")
    })
    public ResponseResult<List<SiteInfoDto>> findTenantOrganSiteList(String tenantId, String organId) {
        return tenantManageService.findTenantOrganSiteList(tenantId, organId);
    }

    @PostMapping("batchSaveOrganEmpower")
    @Operation(summary = "批量添加资产授权信息")
    @WebLog("批量添加资产授权信息")
    
    @Parameters({
            @Parameter(name = "organEmpowerInfoStr", description = "json格式资产授权数组对象:[{\"siteId\":\"\",\"organId\":\"\",\"authority\":\"\",\"tenantId\":\"\"}]")
    })
    public ResponseResult<String> batchSaveOrganEmpower(String organEmpowerInfoStr) {
        return tenantManageService.batchSaveOrganEmpower(organEmpowerInfoStr);
    }

    @PostMapping("saveTenantAccount")
    @Operation(summary = "新增或编辑租户账户信息")
    @WebLog("新增或编辑租户账户信息")
    @Parameters({
            @Parameter(name = "keyPemFile", description = "商户key文件()"),
            @Parameter(name = "pubKeyFile", description = "商户公钥文件(pub_key.pem)")
    })
    
    public ResponseResult<Void> saveTenantAccount(AccountVo accountVo, MultipartFile keyPemFile, MultipartFile pubKeyFile) {
        return tenantManageService.saveTenantAccount(accountVo, keyPemFile, pubKeyFile);
    }

    @PostMapping("findTenantAccountList")
    @Operation(summary = "根据租户id查询租户账户信息")
    @Parameter(name = "tenantId", description = "租户id")
    
    public ResponseResult<List<AccountListDto>> findTenantAccountList(String tenantId) {
        return tenantManageService.findTenantAccountList(tenantId);
    }

    @PostMapping("deleteTenantAccountById")
    @Operation(summary = "根据主键id删除租户账户信息")
    @Parameter(name = "id", description = "主键id")
    
    public ResponseResult<Void> deleteTenantAccountById(String id) {
        return tenantManageService.deleteTenantAccountById(id);
    }

}
