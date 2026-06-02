import request from "@/utils/request";

// 分页查询用户列表数据
export function findUserListByPage(data) {
    return request({
        url: "/system/systemManage/findUserListByPage",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据id更新用户状态
export function updateUserStateById(data) {
    return request({
        url: "/system/systemManage/updateUserStateById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 查询租户下用户组列表
export function findUserGroupListById(data) {
    return request({
        url: "/system/systemManage/findUserGroupListById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 保存或编辑用户信息
export function saveOrUpdateUserInfo(data) {
    return request({
        url: "/system/systemManage/saveOrUpdateUserInfo",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据用户id删除用户信息
export function deleteUserInfoById(data) {
    return request({
        url: "/system/systemManage/deleteUserInfoById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 添加/修改用户组信息-人员管理
export function updateUserGroup(data) {
    return request({
        url: "/system/systemManage/updateUserGroup",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 添加/修改组织信息
export function updateUserOrganStructure(data) {
    return request({
        url: "/system/systemManage/updateUserOrganStructure",
        portNum: "60002",
        method: "post",
        data: data,
    });
}
