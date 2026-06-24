package com.sunmax.together.service.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dao.UserFeedbackDao;
import com.sunmax.together.dto.operation.feedback.UserFeedbackDetailDto;
import com.sunmax.together.dto.operation.feedback.UserFeedbackListDto;
import com.sunmax.together.entity.UserFeedbackEntity;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.operation.impl.FeedbackServiceImpl;
import com.sunmax.together.vo.operation.feedback.UserFeedbackQueryVo;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FeedbackService 单元测试")
class FeedbackServiceTest {

    @Mock
    private UserFeedbackDao userFeedbackDao;

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Test
    @DisplayName("根据ID查询反馈详情-不存在时返回空对象")
    void findUserFeedbackById_notExists_returnsEmptyDetail() {
        when(userFeedbackDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<UserFeedbackDetailDto> result = feedbackService.findUserFeedbackById("nonexistent");

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("根据ID查询反馈详情-存在时返回详情")
    void findUserFeedbackById_exists_returnsDetail() {
        UserFeedbackEntity entity = new UserFeedbackEntity();
        entity.setId("fb-001");
        entity.setSiteId("site-001");
        entity.setFeedbackType(1);
        entity.setDescription("测试反馈");
        when(userFeedbackDao.findById("fb-001")).thenReturn(Optional.of(entity));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setSiteName("测试站点");
        Map<String, SiteInfoDto> siteMap = new HashMap<>();
        siteMap.put("site-001", siteInfo);
        when(deviceService.findSiteBasicInfoByIds(anyList())).thenReturn(ResponseResult.ok(siteMap));

        ResponseResult<UserFeedbackDetailDto> result = feedbackService.findUserFeedbackById("fb-001");

        assertTrue(result.isSuccess());
        assertEquals("测试站点", result.getData().getSiteName());
    }

    @Test
    @DisplayName("更新反馈状态-不存在时返回参数错误")
    void updateUserFeedbackStatus_notExists_returnsParamError() {
        when(userFeedbackDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<Void> result = feedbackService.updateUserFeedbackStatus("nonexistent", 2);

        assertFalse(result.isSuccess());
        verify(userFeedbackDao, never()).save(any());
    }

    @Test
    @DisplayName("更新反馈状态-设为已受理(status=2)时设置受理时间")
    void updateUserFeedbackStatus_accepted_setsAcceptTime() {
        UserFeedbackEntity entity = new UserFeedbackEntity();
        entity.setId("fb-001");
        entity.setStatus(1);
        when(userFeedbackDao.findById("fb-001")).thenReturn(Optional.of(entity));

        ResponseResult<Void> result = feedbackService.updateUserFeedbackStatus("fb-001", 2);

        assertTrue(result.isSuccess());
        verify(userFeedbackDao).save(any(UserFeedbackEntity.class));
    }

    @Test
    @DisplayName("更新反馈状态-设为已完成(status=3)时设置完成时间")
    void updateUserFeedbackStatus_finished_setsFinishTime() {
        UserFeedbackEntity entity = new UserFeedbackEntity();
        entity.setId("fb-001");
        entity.setStatus(2);
        when(userFeedbackDao.findById("fb-001")).thenReturn(Optional.of(entity));

        ResponseResult<Void> result = feedbackService.updateUserFeedbackStatus("fb-001", 3);

        assertTrue(result.isSuccess());
        verify(userFeedbackDao).save(any(UserFeedbackEntity.class));
    }

    @Test
    @DisplayName("查询反馈列表-空条件返回空列表")
    void queryUserFeedbackList_emptyResult_returnsEmptyList() {
        when(userFeedbackDao.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(Collections.emptyList());

        UserFeedbackQueryVo vo = new UserFeedbackQueryVo();
        vo.setPage(1);
        vo.setSize(10);

        ResponseResult<PageDto<UserFeedbackListDto>> result = feedbackService.queryUserFeedbackList(vo);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().getItems().isEmpty());
    }
}
