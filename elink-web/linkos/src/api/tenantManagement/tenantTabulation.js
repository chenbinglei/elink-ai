import request from "@/utils/request";

// 分页查询租户信息
export function findTenantInfoByPage(data) {
    return request({
        url: "/system/tenantManage/findTenantInfoByPage",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 保存或编辑租户信息
export function saveOrUpdateTenantInfo(data) {
    return request({
        url: "/system/tenantManage/saveOrUpdateTenantInfo",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据id删除租户信息
export function deleteTenantInfoById(data) {
    return request({
        url: "/system/tenantManage/deleteTenantInfoById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据id查询租户详情信息
export function findTenantDetailsById(data) {
    return request({
        url: "/system/tenantManage/findTenantDetailsById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据id更新租户状态
export function updateTenantStateById(data) {
    return request({
        url: "/system/tenantManage/updateTenantStateById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据租户id查询租户下组织架构信息
export function findOrganStructureByTenantId(data) {
    return request({
        url: "/system/tenantManage/findOrganStructureByTenantId",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 保存或编辑组织架构信息
export function saveOrUpdateOrganStructure(data) {
    return request({
        url: "/system/tenantManage/saveOrUpdateOrganStructure",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据组织架构id删除指定组织架构信息
export function deleteOrganStructureById(data) {
    return request({
        url: "/system/tenantManage/deleteOrganStructureById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据组织机构id分页查询资产授权列表
export function findEmpowerListByPage(data) {
    return request({
        url: "/system/tenantManage/findEmpowerListByPage",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 添加资产授权信息
export function addOrganEmpowerInfo(data) {
    return request({
        url: "/system/tenantManage/addOrganEmpowerInfo",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据id修改资产授权信息
export function updateEmpowerAuthorityById(data) {
    return request({
        url: "/system/tenantManage/updateEmpowerAuthorityById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据id删除资产授权信息
export function deleteEmpowerInfoById(data) {
    return request({
        url: "/system/tenantManage/deleteEmpowerInfoById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 查询指定租户指定模块下配置的应用授权数据
export function findTenantApplyEmpowerInfoById(data) {
    return request({
        url: "/system/tenantManage/findTenantApplyEmpowerInfoById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 保存租户应用授权信息
export function saveTenantApplyEmpowerInfo(data) {
    return request({
        url: "/system/tenantManage/saveTenantApplyEmpowerInfo",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 查询资产授权站点列表信息
export function findOrganEmpowerSiteList(data) {
    return request({
        url: "/system/tenantManage/findOrganEmpowerSiteList",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据用户id查询控件权限列表
export function findControlPermissionListByUserId(data) {
    return request({
        url: "/system/tenantManage/findControlPermissionListByUserId",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 查询租户下组织架构资产授权站点列表
export function findTenantOrganSiteList(data) {
    return request({
        url: "/system/tenantManage/findTenantOrganSiteList",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 查询租户下组织架构资产授权站点列表
export function batchSaveOrganEmpower(data) {
    return request({
        url: "/system/tenantManage/batchSaveOrganEmpower",
        portNum: "60002",
        method: "post",
        data: data,
    });
}