package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dao.ElectricCardBalanceDao;
import com.sunmax.together.dao.ElectricCardDao;
import com.sunmax.together.dao.ElectricCardRecordDao;
import com.sunmax.together.entity.ElectricCardBalanceEntity;
import com.sunmax.together.entity.ElectricCardEntity;
import com.sunmax.together.entity.ElectricCardRecordEntity;
import com.sunmax.together.service.operation.impl.ElectricCardServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ElectricCardService 单元测试")
class ElectricCardServiceTest {

    @Mock private ElectricCardDao electricCardDao;
    @Mock private ElectricCardRecordDao electricCardRecordDao;
    @Mock private ElectricCardBalanceDao electricCardBalanceDao;

    @InjectMocks private ElectricCardServiceImpl electricCardService;

    @Test
    @DisplayName("更新电卡状态-启用成功")
    void updateElectricCardState_enable_success() {
        ElectricCardEntity card = new ElectricCardEntity();
        card.setId("card-001");
        card.setState(2);
        when(electricCardDao.findById("card-001")).thenReturn(Optional.of(card));
        when(electricCardDao.save(any(ElectricCardEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(electricCardRecordDao.save(any(ElectricCardRecordEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = electricCardService.updateElectricCardState("card-001", 1, "user-001");
        assertTrue(result.isSuccess());
        verify(electricCardDao).save(any(ElectricCardEntity.class));
        verify(electricCardRecordDao).save(any(ElectricCardRecordEntity.class));
    }

    @Test
    @DisplayName("更新电卡状态-停用成功")
    void updateElectricCardState_disable_success() {
        ElectricCardEntity card = new ElectricCardEntity();
        card.setId("card-001");
        card.setState(1);
        when(electricCardDao.findById("card-001")).thenReturn(Optional.of(card));
        when(electricCardDao.save(any(ElectricCardEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(electricCardRecordDao.save(any(ElectricCardRecordEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = electricCardService.updateElectricCardState("card-001", 2, "user-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新电卡状态-不存在返回错误")
    void updateElectricCardState_notFound_returnsError() {
        when(electricCardDao.findById("card-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = electricCardService.updateElectricCardState("card-001", 1, "user-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("获取操作记录-成功")
    void getOperationRecords_success() {
        ElectricCardRecordEntity record = new ElectricCardRecordEntity();
        record.setId("rec-001");
        record.setCarId("card-001");
        when(electricCardRecordDao.findByCarId("card-001")).thenReturn(List.of(record));

        ResponseResult<?> result = electricCardService.getOperationRecords("card-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除电卡-不存在返回错误")
    void deleteElectricCard_notFound_returnsError() {
        when(electricCardDao.findById("card-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = electricCardService.deleteElectricCard("card-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除电卡-成功删除电卡及关联记录")
    void deleteElectricCard_success() {
        ElectricCardEntity card = new ElectricCardEntity();
        card.setId("card-001");
        when(electricCardDao.findById("card-001")).thenReturn(Optional.of(card));

        ElectricCardRecordEntity record = new ElectricCardRecordEntity();
        record.setId("rec-001");
        when(electricCardRecordDao.findByCarId("card-001")).thenReturn(List.of(record));

        ResponseResult<Void> result = electricCardService.deleteElectricCard("card-001");
        assertTrue(result.isSuccess());
        verify(electricCardRecordDao).deleteAll(List.of(record));
        verify(electricCardDao).delete(card);
    }

    @Test
    @DisplayName("删除电卡-无关联记录直接删除")
    void deleteElectricCard_noRecords_success() {
        ElectricCardEntity card = new ElectricCardEntity();
        card.setId("card-001");
        when(electricCardDao.findById("card-001")).thenReturn(Optional.of(card));
        when(electricCardRecordDao.findByCarId("card-001")).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = electricCardService.deleteElectricCard("card-001");
        assertTrue(result.isSuccess());
        verify(electricCardDao).delete(card);
    }

    @Test
    @DisplayName("充值-电卡不存在返回错误")
    void addElectricCardBalance_notFound_returnsError() {
        when(electricCardDao.findById("card-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = electricCardService.addElectricCardBalance("card-001", 100.0);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("充值-金额小于等于0返回错误")
    void addElectricCardBalance_invalidAmount_returnsError() {
        ElectricCardEntity card = new ElectricCardEntity();
        card.setId("card-001");
        when(electricCardDao.findById("card-001")).thenReturn(Optional.of(card));

        ResponseResult<Void> result = electricCardService.addElectricCardBalance("card-001", -10.0);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("充值-成功")
    void addElectricCardBalance_success() {
        ElectricCardEntity card = new ElectricCardEntity();
        card.setId("card-001");
        card.setCurrentBalance(50.0);
        when(electricCardDao.findById("card-001")).thenReturn(Optional.of(card));
        when(electricCardDao.save(any(ElectricCardEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(electricCardBalanceDao.save(any(ElectricCardBalanceEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = electricCardService.addElectricCardBalance("card-001", 100.0);
        assertTrue(result.isSuccess());
        verify(electricCardDao).save(any(ElectricCardEntity.class));
        verify(electricCardBalanceDao).save(any(ElectricCardBalanceEntity.class));
    }

    @Test
    @DisplayName("扣款-电卡不存在返回错误")
    void reduceElectricCardBalance_notFound_returnsError() {
        when(electricCardDao.findById("card-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = electricCardService.reduceElectricCardBalance("card-001", 10.0);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("扣款-余额不足返回错误")
    void reduceElectricCardBalance_insufficientBalance_returnsError() {
        ElectricCardEntity card = new ElectricCardEntity();
        card.setId("card-001");
        card.setCurrentBalance(50.0);
        when(electricCardDao.findById("card-001")).thenReturn(Optional.of(card));

        ResponseResult<Void> result = electricCardService.reduceElectricCardBalance("card-001", 100.0);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("扣款-成功")
    void reduceElectricCardBalance_success() {
        ElectricCardEntity card = new ElectricCardEntity();
        card.setId("card-001");
        card.setCurrentBalance(100.0);
        when(electricCardDao.findById("card-001")).thenReturn(Optional.of(card));
        when(electricCardDao.save(any(ElectricCardEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(electricCardBalanceDao.save(any(ElectricCardBalanceEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = electricCardService.reduceElectricCardBalance("card-001", 30.0);
        assertTrue(result.isSuccess());
        verify(electricCardDao).save(any(ElectricCardEntity.class));
    }
}
