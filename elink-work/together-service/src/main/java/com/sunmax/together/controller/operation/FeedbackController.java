package com.sunmax.together.controller.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.feedback.UserFeedbackDetailDto;
import com.sunmax.together.dto.operation.feedback.UserFeedbackListDto;
import com.sunmax.together.service.operation.FeedbackService;
import com.sunmax.together.vo.operation.feedback.UserFeedbackQueryVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("feedback")
@Tag(name = "用户反馈管理")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("queryUserFeedbackList")
    @Operation(summary = "查询用户反馈列表")
    
    public ResponseResult<PageDto<UserFeedbackListDto>> queryUserFeedbackList(UserFeedbackQueryVo userFeedbackQueryVo) {
        return feedbackService.queryUserFeedbackList(userFeedbackQueryVo);
    }

    @PostMapping("findUserFeedbackById")
    @Operation(summary = "根据id查询用户反馈详情数据")
    
    public ResponseResult<UserFeedbackDetailDto> findUserFeedbackById(String id) {
        return feedbackService.findUserFeedbackById(id);
    }

    @PostMapping("updateUserFeedbackStatus")
    @Operation(summary = "更新用户反馈状态")
    @WebLog("用户反馈管理-更新用户反馈状态")
    
    public ResponseResult<Void> updateUserFeedbackStatus(String id, Integer status) {
        return feedbackService.updateUserFeedbackStatus(id, status);
    }

}
