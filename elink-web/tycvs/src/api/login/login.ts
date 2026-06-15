import request from '@/utils/request'

// 登录
export function login(data) {
    return request({
        url: '/sauth/oauth/token',
        portNum: "60001",
        method: 'post',
        data: data
    })
}