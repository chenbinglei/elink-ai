import request from '@/utils/request';

// 查询用户反馈列表
export function queryUserFeedbackList(data) {
    return request({
        url: '/together/feedback/queryUserFeedbackList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据id查询用户反馈详情数据
export function findUserFeedbackById(data) {
    return request({
        url: '/together/feedback/findUserFeedbackById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 更新用户反馈状态
export function updateUserFeedbackStatus(data) {
    return request({
        url: '/together/feedback/updateUserFeedbackStatus',
        portNum: 60009,
        method: 'post',
        data: data
    });
}