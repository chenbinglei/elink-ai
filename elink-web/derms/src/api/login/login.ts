import request from '@/utils/request';

// 登录
export function login(data) {
    return request({
        url: '/sauth/oauth/token',
        // portNum: 60001,
        method: 'post',
        data: data
    });
}
// 根据用户账号获取密码
export function getPasswordByAccount(data) {
    return request({
        url: '/together/systemUser/getPasswordByAccount',
        portNum: 60009,
        method: 'post',
        data: data
    });
}