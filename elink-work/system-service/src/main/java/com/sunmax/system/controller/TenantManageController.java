package com.sunmax.system.controller;

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
import io.swagger.annotations.*;
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
@Api(tags = "租户管理")
public class TenantManageController {

    @Autowired
    private TenantManageService tenantManageService;

    @PostMapping("saveOrUpdateTenantInfo")
    @ApiOperation("保存或编辑租户信息")
    @WebLog("保存或编辑租户信息")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessLicenseFile", value = "营业执照文件", dataType = "MultipartFile"),
            @ApiImplicitParam(name = "logoFile", value = "标识照片文件", dataType = "MultipartFile")
    })
    public ResponseResult<String> saveOrUpdateTenantInfo(TenantInfoVo tenantInfoVo, MultipartFile businessLicenseFile, MultipartFile logoFile, String userId) {
        return tenantManageService.saveOrUpdateTenantInfo(tenantInfoVo, businessLicenseFile, logoFile, userId);
    }

    @PostMapping("updateTenantStateById")
    @ApiOperation("根据id更新租户状态")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "租户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "tenantState", value = "租户状态 0-关闭 1-开启", dataType = "Integer", required = true)
    })
    public ResponseResult<String> updateTenantStateById(String id, Integer tenantState) {
        return tenantManageService.updateTenantStateById(id, tenantState);
    }

    @PostMapping("deleteTenantInfoById")
    @ApiOperation("根据id删除租户信息")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "租户id", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteTenantInfoById(String id) {
        return tenantManageService.deleteTenantInfoById(id);
    }

    @PostMapping("findTenantInfoByPage")
    @ApiOperation("分页查询租户信息")
    @ApiOperationSupport(order = 4)
    public ResponseResult<?> findTenantInfoByPage(TenantListQueryVo tenantListQueryVo) {
        return tenantManageService.findTenantInfoByPage(tenantListQueryVo);
    }

    @PostMapping("findTenantDetailsById")
    @ApiOperation("根据id查询租户详情信息")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "租户id", dataType = "String", required = true)
    })
    public ResponseResult<TenantDetailsDto> findTenantDetailsById(String id) {
        return tenantManageService.findTenantDetailsById(id);
    }

    @PostMapping("saveOrUpdateOrganStructure")
    @ApiOperation("保存或编辑组织架构信息")
    @ApiOperationSupport(order = 6)
    public ResponseResult<String> saveOrUpdateOrganStructure(OrganStructureVo organStructureVo) {
        return tenantManageService.saveOrUpdateOrganStructure(organStructureVo);
    }

    @PostMapping("deleteOrganStructureById")
    @ApiOperation("根据组织架构id删除指定组织架构信息")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organId", value = "组织架构id", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteOrganStructureById(String organId) {
        return tenantManageService.deleteOrganStructureById(organId);
    }

    @PostMapping("findOrganStructureByTenantId")
    @ApiOperation("根据租户id查询租户下组织架构信息")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true)
    })
    public ResponseResult<List<OrganStructureTreeDto>> findOrganStructureByTenantId(String tenantId) {
        return tenantManageService.findOrganStructureByTenantId(tenantId);
    }

    @PostMapping("addOrganEmpowerInfo")
    @ApiOperation("添加资产授权信息")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "站点id(多个以逗号分割)", dataType = "String", required = true),
            @ApiImplicitParam(name = "authority", value = "权限 1-只读 2-读写", dataType = "Integer"),
            @ApiImplicitParam(name = "organId", value = "组织架构id", dataType = "String", required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true)
    })
    public ResponseResult<String> addOrganEmpowerInfo(String siteIds, Integer authority, String organId, String tenantId) {
        return tenantManageService.addOrganEmpowerInfo(Arrays.stream(siteIds.split(",")).map(String::trim).collect(Collectors.toList()), authority, organId, tenantId);
    }

    @PostMapping("updateEmpowerAuthorityById")
    @ApiOperation("根据id修改资产授权信息")
    @WebLog("修改资产授权信息")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "empowerId", value = "授权id", dataType = "String", required = true),
            @ApiImplicitParam(name = "authority", value = "权限 1-只读 2-读写", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "empowerType", value = "资产类型 1-租户 2-企业", dataType = "Integer", required = true)
    })
    public ResponseResult<String> updateEmpowerAuthorityById(String empowerId, Integer authority, Integer empowerType) {
        return tenantManageService.updateEmpowerAuthorityById(empowerId, authority, empowerType);
    }

    @PostMapping("deleteEmpowerInfoById")
    @ApiOperation("根据id删除资产授权信息")
    @WebLog("删除资产授权信息")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "empowerId", value = "授权id", dataType = "String", required = true),
            @ApiImplicitParam(name = "empowerType", value = "资产类型 1-租户 2-企业", dataType = "Integer", required = true)
    })
    public ResponseResult<String> deleteEmpowerInfoById(String empowerId, Integer empowerType) {
        return tenantManageService.deleteEmpowerInfoById(empowerId, empowerType);
    }

    @PostMapping("findEmpowerListByPage")
    @ApiOperation("根据组织机构id分页查询资产授权列表")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "organId", value = "组织架构id", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "Integer", required = true)
    })
    public ResponseResult<PageDto<OrganEmpowerListDto>> findEmpowerListByPage(String organId, Integer page, Integer size) {
        return tenantManageService.findEmpowerListByPage(organId, page, size);
    }

    @PostMapping("saveTenantApplyEmpowerInfo")
    @ApiOperation("保存租户应用授权信息")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantApplyEmpowerVos", value = "配置租户应用授权数据对象字符串", dataType = "String", required = true)
    })
    public ResponseResult<String> saveTenantApplyEmpowerInfo(String tenantApplyEmpowerVos) {
        return tenantManageService.saveTenantApplyEmpowerInfo(tenantApplyEmpowerVos);
    }

    @PostMapping("saveTenantApplyInfo")
    @ApiOperation("保存租户应用授权信息(新版)")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "moduleId", value = "模块id", dataType = "String", required = true),
            @ApiImplicitParam(name = "tenantApplyVos", value = "配置租户应用授权对象数组字符串 例如[{\"permissionId\": \"权限id1\",\"operate\": \"1\"},{\"permissionId\": \"权限id2\",\"operate\": \"1\"}]", dataType = "String")
    })
    public ResponseResult<Void> saveTenantApplyInfo(String tenantId, String moduleId, String tenantApplyVos) {
        return tenantManageService.saveTenantApplyInfo(tenantId, moduleId, tenantApplyVos);
    }

    @PostMapping("findTenantApplyEmpowerInfoById")
    @ApiOperation("查询指定租户指定模块下配置的应用授权数据")
    @ApiOperationSupport(order = 14)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "moduleId", value = "模块id", dataType = "String", required = true)
    })
    public ResponseResult<List<PermissionListDto>> findTenantApplyEmpowerInfoById(String tenantId, String moduleId) {
        return tenantManageService.findTenantApplyEmpowerInfoById(tenantId, moduleId);
    }

    @PostMapping("findOrganEmpowerSiteList")
    @ApiOperation("查询资产授权站点列表信息")
    @ApiOperationSupport(order = 15)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "siteNameLike", value = "站点名称模糊查询", dataType = "String"),
            @ApiImplicitParam(name = "isQueryAll", value = "是否查询全部 1-是", dataType = "Integer")
    })
    public ResponseResult<List<SiteInfoDto>> findOrganEmpowerSiteList(String tenantId, String siteNameLike, Integer isQueryAll) {
        return tenantManageService.findOrganEmpowerSiteList(tenantId, siteNameLike, isQueryAll);
    }

    @PostMapping("findControlPermissionListByUserId")
    @ApiOperation("根据用户id查询控件权限列表")
    @ApiOperationSupport(order = 16)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true)
    })
    public ResponseResult<List<PermissionListDto>> findControlPermissionListByUserId(String userId) {
        return tenantManageService.findControlPermissionListByUserId(userId);
    }

    @PostMapping("findOrganStructureListByTenantId")
    @ApiOperation("根据租户id查询组织架构信息列表")
    @ApiOperationSupport(order = 17)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true)
    })
    public ResponseResult<List<OrganStructureListDto>> findOrganStructureListByTenantId(String tenantId) {
        return tenantManageService.findOrganStructureListByTenantId(tenantId);
    }

    @PostMapping("findUserListByTenantId")
    @ApiOperation("根据租户id查询用户列表")
    @ApiOperationSupport(order = 18)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true)
    })
    public ResponseResult<List<UserDto>> findUserListByTenantId(String tenantId) {
        return tenantManageService.findUserListByTenantId(tenantId);
    }

    @PostMapping("findTenantOrganSiteList")
    @ApiOperation("查询租户下组织架构资产授权站点列表")
    @ApiOperationSupport(order = 19)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "organId", value = "组织架构id", dataType = "String", required = true)
    })
    public ResponseResult<List<SiteInfoDto>> findTenantOrganSiteList(String tenantId, String organId) {
        return tenantManageService.findTenantOrganSiteList(tenantId, organId);
    }

    @PostMapping("batchSaveOrganEmpower")
    @ApiOperation("批量添加资产授权信息")
    @WebLog("批量添加资产授权信息")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organEmpowerInfoStr", value = "json格式资产授权数组对象:[{\"siteId\":\"\",\"organId\":\"\",\"authority\":\"\",\"tenantId\":\"\"}]", dataType = "String", required = true)
    })
    public ResponseResult<String> batchSaveOrganEmpower(String organEmpowerInfoStr) {
        return tenantManageService.batchSaveOrganEmpower(organEmpowerInfoStr);
    }

    @PostMapping("saveTenantAccount")
    @ApiOperation("新增或编辑租户账户信息")
    @WebLog("新增或编辑租户账户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyPemFile", value = "商户key文件()", dataType = "String", required = true),
            @ApiImplicitParam(name = "pubKeyFile", value = "商户公钥文件(pub_key.pem)", dataType = "String", required = true)
    })
    @ApiOperationSupport(order = 21)
    public ResponseResult<Void> saveTenantAccount(AccountVo accountVo, MultipartFile keyPemFile, MultipartFile pubKeyFile) {
        return tenantManageService.saveTenantAccount(accountVo, keyPemFile, pubKeyFile);
    }

    @PostMapping("findTenantAccountList")
    @ApiOperation("根据租户id查询租户账户信息")
    @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true)
    @ApiOperationSupport(order = 22)
    public ResponseResult<List<AccountListDto>> findTenantAccountList(String tenantId) {
        return tenantManageService.findTenantAccountList(tenantId);
    }

    @PostMapping("deleteTenantAccountById")
    @ApiOperation("根据主键id删除租户账户信息")
    @ApiImplicitParam(name = "id", value = "主键id", dataType = "String", required = true)
    @ApiOperationSupport(order = 23)
    public ResponseResult<Void> deleteTenantAccountById(String id) {
        return tenantManageService.deleteTenantAccountById(id);
    }

}
