import request from '@/utils/request'

// 登录
export function login(data) {
    return request({
        url: '/sauth/oauth/token',
        method: 'post',
        data: data
    })
}

// 登出（通过 Authorization Header 传递 token，由拦截器自动注入）
export function logout() {
    return request({
        url: '/sauth/oauth/logout',
        method: 'post'
    })
}

