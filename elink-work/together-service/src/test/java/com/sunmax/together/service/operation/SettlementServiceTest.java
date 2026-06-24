package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.together.dao.SiteAccountDao;
import com.sunmax.together.dto.operation.settlement.SiteAccountDetailDto;
import com.sunmax.together.entity.SiteAccountEntity;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.operation.impl.SettlementServiceImpl;
import com.sunmax.together.vo.operation.settlement.SiteAccountChangeVo;
import com.sunmax.together.vo.operation.settlement.SiteAccountQueryVo;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.AccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SettlementService 单元测试")
class SettlementServiceTest {

    @Mock private DeviceService deviceService;
    @Mock private SystemService systemService;
    @Mock private SiteAccountDao siteAccountDao;

    @InjectMocks private SettlementServiceImpl settlementService;

    @Test
    @DisplayName("查询站点账户列表-无授权返回空分页")
    void querySiteAccountList_noAuth_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        SiteAccountQueryVo vo = new SiteAccountQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = settlementService.querySiteAccountList(vo);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().getItems().isEmpty());
    }

    @Test
    @DisplayName("查询站点账户列表-有授权无站点返回空分页")
    void querySiteAccountList_hasAuthNoSite_returnsEmpty() {
        OrganEmpowerListDto organDto = new OrganEmpowerListDto();
        organDto.setSiteId("site-001");
        organDto.setSiteName("Test Site");
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(List.of(organDto)));
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        SiteAccountQueryVo vo = new SiteAccountQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = settlementService.querySiteAccountList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点账户列表-有站点返回数据")
    void querySiteAccountList_hasSite_returnsData() {
        OrganEmpowerListDto organDto = new OrganEmpowerListDto();
        organDto.setSiteId("site-001");
        organDto.setSiteName("Test Site");
        organDto.setAuthority(1);
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(List.of(organDto)));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setTenantId("tenant-001");
        siteInfo.setTenantName("Test Tenant");
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("site-001", siteInfo)));
        when(siteAccountDao.findBySiteIdInAndType(any(), anyInt())).thenReturn(Collections.emptyList());
        when(systemService.findAccountListByAccountIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        SiteAccountQueryVo vo = new SiteAccountQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = settlementService.querySiteAccountList(vo);
        assertTrue(result.isSuccess());
        assertFalse(result.getData().getItems().isEmpty());
    }

    @Test
    @DisplayName("查询站点账户列表-按租户过滤")
    void querySiteAccountList_filterByTenant_returnsFiltered() {
        OrganEmpowerListDto organDto = new OrganEmpowerListDto();
        organDto.setSiteId("site-001");
        organDto.setSiteName("Test Site");
        organDto.setAuthority(1);
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(List.of(organDto)));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setTenantId("tenant-001");
        siteInfo.setTenantName("Test Tenant");
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("site-001", siteInfo)));
        when(siteAccountDao.findBySiteIdInAndType(any(), anyInt())).thenReturn(Collections.emptyList());
        when(systemService.findAccountListByAccountIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        SiteAccountQueryVo vo = new SiteAccountQueryVo();
        vo.setUserId("user-001");
        vo.setTenantId("tenant-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = settlementService.querySiteAccountList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点账户详情-无数据返回空列表")
    void findSiteAccountListBySiteIdAndType_noData_returnsEmpty() {
        when(siteAccountDao.findBySiteIdAndTypeIn(any(), any())).thenReturn(Collections.emptyList());

        var result = settlementService.findSiteAccountListBySiteIdAndType("site-001", 1);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("查询站点账户详情-有数据返回列表")
    void findSiteAccountListBySiteIdAndType_hasData_returnsList() {
        SiteAccountEntity entity = new SiteAccountEntity();
        entity.setId("sa-001");
        entity.setSiteId("site-001");
        entity.setType(1);
        entity.setAccountId("acc-001");
        entity.setTenantId("tenant-001");
        when(siteAccountDao.findBySiteIdAndTypeIn(any(), any())).thenReturn(List.of(entity));
        when(systemService.findAllTenantInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));
        when(systemService.findAccountListByAccountIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = settlementService.findSiteAccountListBySiteIdAndType("site-001", 1);
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
    }

    @Test
    @DisplayName("根据账户ID查询账户-无数据返回空对象")
    void findAccountByAccountId_noData_returnsEmpty() {
        when(systemService.findAccountListByAccountIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = settlementService.findAccountByAccountId("acc-001");
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("根据账户ID查询账户-有数据返回账户")
    void findAccountByAccountId_hasData_returnsAccount() {
        AccountDto accountDto = new AccountDto();
        accountDto.setMchName("Test Merchant");
        accountDto.setMchId("mch-001");
        when(systemService.findAccountListByAccountIds(any())).thenReturn(ResponseResult.ok(Map.of("acc-001", accountDto)));

        var result = settlementService.findAccountByAccountId("acc-001");
        assertTrue(result.isSuccess());
        assertEquals("Test Merchant", result.getData().getMchName());
    }

    @Test
    @DisplayName("保存站点账户-新增成功")
    void saveSiteAccount_create_success() {
        when(siteAccountDao.findBySiteIdAndTypeIn(any(), any())).thenReturn(Collections.emptyList());
        when(siteAccountDao.save(any())).thenReturn(new SiteAccountEntity());

        SiteAccountChangeVo vo = new SiteAccountChangeVo();
        vo.setSiteId("site-001");
        vo.setType(1);
        vo.setAccountId("acc-001");
        var result = settlementService.saveSiteAccount(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存站点账户-新增时已存在返回错误")
    void saveSiteAccount_create_exists_returnsError() {
        SiteAccountEntity existing = new SiteAccountEntity();
        existing.setId("sa-001");
        when(siteAccountDao.findBySiteIdAndTypeIn(any(), any())).thenReturn(List.of(existing));

        SiteAccountChangeVo vo = new SiteAccountChangeVo();
        vo.setSiteId("site-001");
        vo.setType(1);
        vo.setAccountId("acc-001");
        var result = settlementService.saveSiteAccount(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存站点账户-修改成功")
    void saveSiteAccount_update_success() {
        SiteAccountEntity existing = new SiteAccountEntity();
        existing.setId("sa-001");
        existing.setCreateTime(java.time.LocalDateTime.of(2026, 1, 1, 0, 0));
        when(siteAccountDao.findBySiteIdAndTypeIn(any(), any())).thenReturn(Collections.emptyList());
        when(siteAccountDao.findById(any())).thenReturn(Optional.of(existing));
        when(siteAccountDao.save(any())).thenReturn(new SiteAccountEntity());

        SiteAccountChangeVo vo = new SiteAccountChangeVo();
        vo.setId("sa-001");
        vo.setSiteId("site-001");
        vo.setType(1);
        vo.setAccountId("acc-002");
        var result = settlementService.saveSiteAccount(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存站点账户-修改时已存在返回错误")
    void saveSiteAccount_update_exists_returnsError() {
        SiteAccountEntity existing = new SiteAccountEntity();
        existing.setId("sa-002");
        when(siteAccountDao.findBySiteIdAndTypeIn(any(), any())).thenReturn(List.of(existing));

        SiteAccountChangeVo vo = new SiteAccountChangeVo();
        vo.setId("sa-001");
        vo.setSiteId("site-001");
        vo.setType(1);
        vo.setAccountId("acc-002");
        var result = settlementService.saveSiteAccount(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除站点账户-成功")
    void deleteSiteAccountById_success() {
        doNothing().when(siteAccountDao).deleteById(any());

        var result = settlementService.deleteSiteAccountById("sa-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询用户账户列表-无用户返回空列表")
    void findAccountListByUserId_noUser_returnsEmpty() {
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        var result = settlementService.findAccountListByUserId("user-001", 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存站点账户-type=3跳过校验")
    void saveSiteAccount_type3_skipsValidation() {
        when(siteAccountDao.save(any())).thenReturn(new SiteAccountEntity());

        SiteAccountChangeVo vo = new SiteAccountChangeVo();
        vo.setSiteId("site-001");
        vo.setType(3);
        vo.setAccountId("acc-001");
        var result = settlementService.saveSiteAccount(vo);
        assertTrue(result.isSuccess());
    }
}
