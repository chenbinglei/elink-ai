import request from '@/utils/request';

// 查询站点账户数据列表
export function querySiteAccountList(data) {
    return request({
        url: '/together/settlement/querySiteAccountList',
        method: 'post',
        portNum: 60009,
        data: data
    });
}

// 根据租户id查询租户账户信息
export function findTenantAccountList(data) {
    return request({
        url: '/system/tenantManage/findTenantAccountList',
        method: 'post',
        portNum: 60002,
        data: data
    });
}
export function findAccountListByUserId(data) {
    return request({
        url: '/together/settlement/findAccountListByUserId',
        method: 'post',
        portNum: 60002,
        data: data
    });
}
// 根据用户id查询所有租户列表数据
export function findTenantListByUserId(data) {
    return request({
        url: '/together/settlement/findTenantListByUserId',
        method: 'post',
        portNum: 60009,
        data: data
    });
}

// 新增或编辑站点账户数据
export function saveSiteAccount(data) {
    return request({
        url: '/together/settlement/saveSiteAccount',
        method: 'post',
        portNum: 60009,
        data: data
    });
}

// 根据站点id和类型查询站点账户数据
export function findSiteAccountListBySiteIdAndType(data) {
    return request({
        url: '/together/settlement/findSiteAccountListBySiteIdAndType',
        method: 'post',
        portNum: 60009,
        data: data
    });
}

// 根据主键id删除站点账户数据
export function deleteSiteAccountById(data) {
    return request({
        url: '/together/settlement/deleteSiteAccountById',
        method: 'post',
        portNum: 60009,
        data: data
    });
}