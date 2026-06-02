package com.sunmax.together.controller.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.feedback.UserFeedbackDetailDto;
import com.sunmax.together.dto.operation.feedback.UserFeedbackListDto;
import com.sunmax.together.service.operation.FeedbackService;
import com.sunmax.together.vo.operation.feedback.UserFeedbackQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("feedback")
@Api(tags = "用户反馈管理")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("queryUserFeedbackList")
    @ApiOperation("查询用户反馈列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<PageDto<UserFeedbackListDto>> queryUserFeedbackList(UserFeedbackQueryVo userFeedbackQueryVo) {
        return feedbackService.queryUserFeedbackList(userFeedbackQueryVo);
    }

    @PostMapping("findUserFeedbackById")
    @ApiOperation("根据id查询用户反馈详情数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<UserFeedbackDetailDto> findUserFeedbackById(String id) {
        return feedbackService.findUserFeedbackById(id);
    }

    @PostMapping("updateUserFeedbackStatus")
    @ApiOperation("更新用户反馈状态")
    @WebLog("用户反馈管理-更新用户反馈状态")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Void> updateUserFeedbackStatus(String id, Integer status) {
        return feedbackService.updateUserFeedbackStatus(id, status);
    }

}
