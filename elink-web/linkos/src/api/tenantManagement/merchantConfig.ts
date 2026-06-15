import request from "@/utils/request";

// 根据租户id查询租户账户信息
export function findTenantAccountList(data) {
    return request({
        url: "/system/tenantManage/findTenantAccountList",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 根据主键id删除租户账户信息
export function deleteTenantAccountById(data) {
    return request({
        url: "/system/tenantManage/deleteTenantAccountById",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 新增或编辑租户账户信息
export function saveTenantAccount(data) {
    return request({
        url: "/system/tenantManage/saveTenantAccount",
        portNum: 60002,
        method: "post",
        data: data,
    });
}