import request from "@/utils/request";

// 查询产品列表
export function queryProductList(data) {
    return request({
        url: "/system/configureCenter/queryProductList",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 添加或修改产品信息
export function saveOrUpdateProduct(data) {
    return request({
        url: "/system/configureCenter/saveOrUpdateProduct",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据模块id删除模块数据
export function deleteModuleById(data) {
    return request({
        url: "/system/configureCenter/deleteModuleById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据模块id查询权限数据
export function findPermissionByModuleId(data) {
    return request({
        url: "/system/configureCenter/findPermissionByModuleId",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 添加或修改权限信息
export function saveOrUpdatePermission(data) {
    return request({
        url: "/system/configureCenter/saveOrUpdatePermission",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据id删除权限数据
export function deletePermissionById(data) {
    return request({
        url: "/system/configureCenter/deletePermissionById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}
