package com.sunmax.together.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.OrderChangeVo;
import com.sunmax.common.vo.together.RefundRecordChangeVo;
import com.sunmax.common.vo.together.UserDisWalletChangeVo;
import com.sunmax.together.dao.*;
import com.sunmax.together.dao.asset.ChargerPriceDao;
import com.sunmax.together.dao.asset.ChargerPriceInfoDao;
import com.sunmax.together.dao.asset.OccupyPilePriceDao;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dao.order.RefundRecordDao;
import com.sunmax.together.dao.order.SettlementRecordDao;
import com.sunmax.together.entity.AppletUserEntity;
import com.sunmax.together.entity.SiteAccountEntity;
import com.sunmax.together.entity.UserDisWalletEntity;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.entity.order.SettlementRecordEntity;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.impl.WebAppFeignServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("WebAppFeignService 单元测试")
class WebAppFeignServiceTest {

    @Mock private OrderRecordDao orderRecordDao;
    @Mock private AppletUserDao appletUserDao;
    @Mock private SettlementRecordDao settlementRecordDao;
    @Mock private SiteAccountDao siteAccountDao;
    @Mock private SystemService systemService;
    @Mock private ChargerPriceInfoDao chargerPriceInfoDao;
    @Mock private ChargerPriceDao chargerPriceDao;
    @Mock private DeviceService deviceService;
    @Mock private OccupyPilePriceDao occupyPilePriceDao;
    @Mock private UserDisWalletDao userDisWalletDao;
    @Mock private RefundRecordDao refundRecordDao;

    @InjectMocks private WebAppFeignServiceImpl webAppFeignService;

    @Test
    @DisplayName("生成订单号-首次生成成功")
    void generateOrderNum_firstTime_success() {
        when(orderRecordDao.findFirstByOrderByCreateTimeDesc()).thenReturn(null);

        ResponseResult<String> result = webAppFeignService.generateOrderNum("P001", "001");
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("查询小程序用户-无数据返回空Map")
    void findAppletUserByIds_noData_returnsEmpty() {
        when(appletUserDao.findAllById(any())).thenReturn(Collections.emptyList());

        ResponseResult<?> result = webAppFeignService.findAppletUserByIds(Set.of("au-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询小程序用户-有数据返回Map")
    void findAppletUserByIds_withData_returnsMap() {
        AppletUserEntity user = new AppletUserEntity();
        user.setId("au-001");
        user.setAppletId("app-001");
        when(appletUserDao.findAllById(any())).thenReturn(List.of(user));
        when(systemService.findAppletListByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        ResponseResult<?> result = webAppFeignService.findAppletUserByIds(Set.of("au-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("创建充电订单-订单已存在返回错误")
    void createPileOrder_orderExists_returnsError() {
        OrderChangeVo vo = new OrderChangeVo();
        vo.setOrderNum("ORD001");
        OrderRecordEntity existing = new OrderRecordEntity();
        when(orderRecordDao.findOne(any(org.springframework.data.domain.Example.class))).thenReturn(Optional.of(existing));

        ResponseResult<Void> result = webAppFeignService.createPileOrder(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("创建充电订单-成功创建")
    void createPileOrder_success() {
        OrderChangeVo vo = new OrderChangeVo();
        vo.setOrderNum("ORD001");
        vo.setSettlementState(0);
        vo.setPayWay(1);
        when(orderRecordDao.findOne(any(org.springframework.data.domain.Example.class))).thenReturn(Optional.empty());
        when(orderRecordDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(settlementRecordDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = webAppFeignService.createPileOrder(vo);
        assertTrue(result.isSuccess());
        verify(orderRecordDao).save(any());
        verify(settlementRecordDao).save(any());
    }

    @Test
    @DisplayName("查询站点账户-无数据返回空Map")
    void findSiteAccountListBySiteIds_noData_returnsEmpty() {
        when(siteAccountDao.findBySiteIdInAndType(any(), any())).thenReturn(Collections.emptyList());

        ResponseResult<?> result = webAppFeignService.findSiteAccountListBySiteIds(Set.of("site-001"), 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点账户-有数据返回Map")
    void findSiteAccountListBySiteIds_withData_returnsMap() {
        SiteAccountEntity sa = new SiteAccountEntity();
        sa.setSiteId("site-001");
        sa.setAccountId("acc-001");
        sa.setType(1);
        when(siteAccountDao.findBySiteIdInAndType(any(), any())).thenReturn(List.of(sa));
        when(systemService.findAccountListByAccountIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        ResponseResult<?> result = webAppFeignService.findSiteAccountListBySiteIds(Set.of("site-001"), 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新用户放电钱包-参数为空返回错误")
    void updateUserDisWallet_emptyParams_returnsError() {
        UserDisWalletChangeVo vo = new UserDisWalletChangeVo();
        vo.setAppletUserId(null);
        vo.setAccountId(null);

        ResponseResult<?> result = webAppFeignService.updateUserDisWallet(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新用户放电钱包-交易金额为0返回错误")
    void updateUserDisWallet_zeroMoney_returnsError() {
        UserDisWalletChangeVo vo = new UserDisWalletChangeVo();
        vo.setAppletUserId("au-001");
        vo.setAccountId("acc-001");
        vo.setTradeMoney(BigDecimal.ZERO);

        ResponseResult<?> result = webAppFeignService.updateUserDisWallet(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新用户放电钱包-放电收益成功")
    void updateUserDisWallet_dischargeIncome_success() {
        UserDisWalletChangeVo vo = new UserDisWalletChangeVo();
        vo.setAppletUserId("au-001");
        vo.setAccountId("acc-001");
        vo.setTradeMoney(new BigDecimal("10.00"));
        vo.setTradeType(1);

        UserDisWalletEntity wallet = new UserDisWalletEntity();
        wallet.setBalance(new BigDecimal("100.00"));
        wallet.setFreezeBalance(new BigDecimal("0.00"));
        when(userDisWalletDao.findAllByAppletUserIdAndAccountId("au-001", "acc-001")).thenReturn(wallet);
        when(userDisWalletDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<BigDecimal> result = webAppFeignService.updateUserDisWallet(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新用户放电钱包-新建钱包成功")
    void updateUserDisWallet_newWallet_success() {
        UserDisWalletChangeVo vo = new UserDisWalletChangeVo();
        vo.setAppletUserId("au-001");
        vo.setAccountId("acc-001");
        vo.setTradeMoney(new BigDecimal("10.00"));
        vo.setTradeType(1);

        when(userDisWalletDao.findAllByAppletUserIdAndAccountId("au-001", "acc-001")).thenReturn(null);
        when(userDisWalletDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<BigDecimal> result = webAppFeignService.updateUserDisWallet(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存退款记录-无订单返回错误")
    void saveRefundRecord_noOrder_returnsError() {
        RefundRecordChangeVo vo = new RefundRecordChangeVo();
        vo.setOrderNum("ORD001");
        when(orderRecordDao.findByOrderNum("ORD001")).thenReturn(null);

        ResponseResult<String> result = webAppFeignService.saveRefundRecord(vo);
        assertFalse(result.isSuccess());
    }
}
