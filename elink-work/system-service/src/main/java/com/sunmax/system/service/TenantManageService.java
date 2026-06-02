package com.sunmax.system.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.dto.AccountListDto;
import com.sunmax.system.dto.PermissionListDto;
import com.sunmax.system.vo.AccountVo;
import com.sunmax.system.vo.OrganStructureVo;
import com.sunmax.system.vo.TenantInfoVo;
import com.sunmax.system.vo.TenantListQueryVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TenantManageService {

    /**
     * 保存或编辑租户信息
     *
     * @param tenantInfoVo
     * @param businessLicenseFile
     * @param logoFile
     * @param userId
     * @return
     */
    ResponseResult<String> saveOrUpdateTenantInfo(TenantInfoVo tenantInfoVo, MultipartFile businessLicenseFile, MultipartFile logoFile, String userId);

    /**
     * 根据id更新租户状态
     * @param id
     * @param tenantState
     * @return
     */
    ResponseResult<String> updateTenantStateById(String id, Integer tenantState);

    /**
     * 根据id删除租户信息
     * @param id
     * @return
     */
    ResponseResult<String> deleteTenantInfoById(String id);

    /**
     * 分页查询租户信息
     * @param tenantListQueryVo
     * @return
     */
    ResponseResult<?> findTenantInfoByPage(TenantListQueryVo tenantListQueryVo);

    /**
     * 根据id查询租户详情信息
     * @param id
     * @return
     */
    ResponseResult<TenantDetailsDto> findTenantDetailsById(String id);

    /**
     * 保存或编辑组织架构信息
     * @param organStructureVo
     * @return
     */
    ResponseResult<String> saveOrUpdateOrganStructure(OrganStructureVo organStructureVo);

    /**
     * 根据组织架构id删除指定组织架构信息
     * @param organId
     * @return
     */
    ResponseResult<String> deleteOrganStructureById(String organId);

    /**
     * 根据租户id查询租户下组织架构信息
     * @param tenantId
     * @return
     */
    ResponseResult<List<OrganStructureTreeDto>> findOrganStructureByTenantId(String tenantId);

    /**
     * 添加资产授权信息
     *
     * @param siteIdList
     * @param authority
     * @param organId
     * @param tenantId
     * @return
     */
    ResponseResult<String> addOrganEmpowerInfo(List<String> siteIdList, Integer authority, String organId, String tenantId);

    /**
     * 修改资产授权信息
     *
     * @param empowerId
     * @param authority
     * @param empowerType
     * @return
     */
    ResponseResult<String> updateEmpowerAuthorityById(String empowerId, Integer authority, Integer empowerType);

    /**
     * 根据id删除授权信息限
     *
     * @param empowerId
     * @param empowerType
     * @return
     */
    ResponseResult<String> deleteEmpowerInfoById(String empowerId, Integer empowerType);

    /**
     * 根据组织机构id分页查询资产授权列表
     *
     * @param organId
     * @param page
     * @param size
     * @return
     */
    ResponseResult<PageDto<OrganEmpowerListDto>> findEmpowerListByPage(String organId, Integer page, Integer size);

    /**
     * 保存租户应用授权信息
     * @param tenantApplyEmpowerVos
     * @return
     */
    ResponseResult<String> saveTenantApplyEmpowerInfo(String tenantApplyEmpowerVos);

    /**
     * 保存租户应用授权信息(新版)
     * @param tenantId 租户id
     * @param moduleId 模块id
     * @param tenantApplyVos 租户授权信息
     * @return 状态码
     */
    ResponseResult<Void> saveTenantApplyInfo(String tenantId, String moduleId, String tenantApplyVos);

    /**
     * 查询指定租户指定模块下配置的应用授权数据
     * @param tenantId
     * @param moduleId
     * @return
     */
    ResponseResult<List<PermissionListDto>> findTenantApplyEmpowerInfoById(String tenantId, String moduleId);

    /**
     * 查询资产授权站点列表信息
     *
     * @param tenantId
     * @param siteNameLike
     * @param isQueryAll
     * @return
     */
    ResponseResult<List<SiteInfoDto>> findOrganEmpowerSiteList(String tenantId, String siteNameLike, Integer isQueryAll);

    /**
     * 根据用户id查询控件权限列表
     * @param userId
     * @return
     */
    ResponseResult<List<PermissionListDto>> findControlPermissionListByUserId(String userId);

    /**
     * 根据租户id查询组织架构信息列表
     * @param tenantId
     * @return
     */
    ResponseResult<List<OrganStructureListDto>> findOrganStructureListByTenantId(String tenantId);

    /**
     * 根据租户id查询用户列表
     * @param tenantId
     * @return
     */
    ResponseResult<List<UserDto>> findUserListByTenantId(String tenantId);

    /**
     * 根据多个租户id查询租户基本信息
     * @param tenantIdList
     * @return
     */
    ResponseResult<List<TenantDetailsDto>> findAllTenantInfoByIds(List<String> tenantIdList);

    /**
     * 查询租户下组织架构资产授权站点列表
     * @param tenantId
     * @param organId
     * @return
     */
    ResponseResult<List<SiteInfoDto>> findTenantOrganSiteList(String tenantId, String organId);

    /**
     * 批量添加资产授权信息
     * @param organEmpowerInfoStr
     * @return
     */
    ResponseResult<String> batchSaveOrganEmpower(String organEmpowerInfoStr);

    /**
     * 新增或编辑租户账户信息
     * @param accountVo 租户账户编辑参数
     * @param keyPemFile 商户key文件
     * @param pubKeyFile 商户公钥文件
     * @return 状态码
     */
    ResponseResult<Void> saveTenantAccount(AccountVo accountVo, MultipartFile keyPemFile, MultipartFile pubKeyFile);

    /**
     * 根据租户id查询租户账户列表
     * @param tenantId 租户id
     * @return 租户账户列表
     */
    ResponseResult<List<AccountListDto>> findTenantAccountList(String tenantId);

    /**
     * 根据主键id删除租户账户信息
     * @param id 主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteTenantAccountById(String id);

    /**
     * 根据租户id查询租户账户信息列表
     * @param tenantId 租户id
     * @param platformType 平台类型 1-微信平台 2-支付宝平台
     * @return 租户账户信息列表
     */
    ResponseResult<List<AccountDto>> findAccountListByTenantId(String tenantId, Integer platformType);

    /**
     * 根据多个账户id查询租户账户信息列表
     * @param accountIds 多个账户id
     * @return 租户账户信息列表
     */
    ResponseResult<Map<String, AccountDto>> findAccountListByAccountIds(Set<String> accountIds);

    /**
     * 根据用户id查询所有租户列表数据
     * @param userId 用户id
     * @return
     */
    ResponseResult<List<TenantDetailsDto>> findTenantListByUserId(String userId);

    /**
     * 根据商户证书号查询商户账户信息
     * @param rsaSerialNo RSA商户证书号
     * @return 商户账户信息
     */
    ResponseResult<AccountDto> findAccountByRsaSerialNo(String rsaSerialNo);
}
