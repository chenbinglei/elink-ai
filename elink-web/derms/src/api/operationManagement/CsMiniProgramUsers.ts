import request from '@/utils/request';

// 分页查询小程序用户列表
export function queryAppletUserList(data) {
    return request({
        url: '/together/appletUser/queryAppletUserList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询全部用户分组列表
export function queryAllUserGroupList(data) {
    return request({
        url: '/together/appletUser/queryAllUserGroupList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 新增或编辑小程序用户
export function saveOrUpdateAppletUser(data) {
    return request({
        url: '/together/appletUser/saveOrUpdateAppletUser',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 修改小程序用户状态
export function updateAppletUserState(data) {
    return request({
        url: '/together/appletUser/updateAppletUserState',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 分页查询小程序用户注销申请列表
export function findAppletCancelListByPage(data) {
    return request({
        url: '/together/appletUser/findAppletCancelListByPage',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 注销小程序用户账号
export function cancelAppletUser(data) {
    return request({
        url: '/together/appletUser/cancelAppletUser',
        portNum: 60009,
        method: 'post',
        data: data
    });
}