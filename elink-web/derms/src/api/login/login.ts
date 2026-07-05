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

// 登出（通过 Authorization Header 传递 token，由拦截器自动注入）
export function logout() {
    return request({
        url: '/sauth/oauth/logout',
        method: 'post'
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
