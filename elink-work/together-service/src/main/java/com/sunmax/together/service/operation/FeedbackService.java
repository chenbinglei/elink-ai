package com.sunmax.together.service.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.feedback.UserFeedbackDetailDto;
import com.sunmax.together.dto.operation.feedback.UserFeedbackListDto;
import com.sunmax.together.vo.operation.feedback.UserFeedbackQueryVo;

public interface FeedbackService {

    /**
     * 查询用户反馈列表
     * @param userFeedbackQueryVo 查询条件
     * @return 用户反馈数据列表
     */
    ResponseResult<PageDto<UserFeedbackListDto>> queryUserFeedbackList(UserFeedbackQueryVo userFeedbackQueryVo);

    /**
     * 根据id查询用户反馈详情数据
     * @param id 主键id
     * @return 用户反馈详情数据
     */
    ResponseResult<UserFeedbackDetailDto> findUserFeedbackById(String id);

    /**
     * 更新用户反馈状态
     * @param id 主键id
     * @param status 状态
     * @return 状态码
     */
    ResponseResult<Void> updateUserFeedbackStatus(String id, Integer status);

}
