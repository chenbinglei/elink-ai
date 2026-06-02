import request from '@/utils/request';

// 根据小程序用户id查询用户详情数据
export function queryAppletUserDetailById(data) {
    return request({
        url: '/together/appletUser/queryAppletUserDetailById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据小程序用户id查询用户V2G钱包列表
export function findUserDisWalletListById(data) {
    return request({
        url: '/together/appletUser/findUserDisWalletListById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}