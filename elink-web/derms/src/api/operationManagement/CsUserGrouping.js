import request from '@/utils/request';

// 分页查询用户分组列表
export function queryUserGroupList(data) {
    return request({
        url: '/together/appletUser/queryUserGroupList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询全部站点信息列表
export function queryAllSiteInfoList(data) {
    return request({
        url: '/together/appletUser/queryAllSiteInfoList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 新增或编辑用户分组
export function saveOrUpdateUserGroup(data) {
    return request({
        url: '/together/appletUser/saveOrUpdateUserGroup',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据分组id删除用户分组信息
export function deleteUserGroupById(data) {
    return request({
        url: '/together/appletUser/deleteUserGroupById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据分组id查询关联小程序用户列表
export function queryAppletUserListByGroupId(data) {
    return request({
        url: '/together/appletUser/queryAppletUserListByGroupId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 管理分组下小程序用户
export function manageGroupUserByGroupId(data) {
    return request({
        url: '/together/appletUser/manageGroupUserByGroupId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}