import request from "@/utils/request";

// 租户管理模块接口
// 根据租户id查询组织架构信息列表
export function findOrganStructureListByTenantId(data) {
    return request({
        url: "/system/tenantManage/findOrganStructureListByTenantId",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据租户id查询用户列表
export function findUserListByTenantId(data) {
    return request({
        url: "/system/tenantManage/findUserListByTenantId",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 配置中心模块接口

// 分页查询用户组列表信息
export function findUserGroupListByPage(data) {
    return request({
        url: "/system/systemManage/findUserGroupListByPage",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 保存或编辑用户组信息
export function saveOrUpdateUserGroup(data) {
    return request({
        url: "/system/systemManage/saveOrUpdateUserGroup",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据用户组id删除用户组信息
export function deleteUserGroupById(data) {
    return request({
        url: "/system/systemManage/deleteUserGroupById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 查询指定用户组指定模块下配置的应用授权数据
export function findGroupApplyEmpowerInfoById(data) {
    return request({
        url: "/system/systemManage/findGroupApplyEmpowerInfoById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 保存用户组应用授权信息
export function saveGroupApplyEmpowerInfo(data) {
    return request({
        url: "/system/systemManage/saveGroupApplyEmpowerInfo",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 删除指定用户组下关联的指定权限数据
export function deleteGroupApplyEmpowerByGroupId(data) {
    return request({
        url: "/system/systemManage/deleteGroupApplyEmpowerByGroupId",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 用户组管理人员
export function groupManageUser(data) {
    return request({
        url: "/system/systemManage/groupManageUser",
        portNum: "60002",
        method: "post",
        data: data,
    });
}
