import request from '@/utils/request';

// 分页查询站点信息列表
export function findSiteInfoListByPage(data) {
    return request({
        url: '/together/siteInfo/findSiteInfoListByPage',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询站点下拉列表
export function findSiteBasicInfoByTenantId(data) {
    return request({
        url: '/together/order/findSiteBasicInfoByTenantId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据登录用户id查询站点列表
export function findSiteInfoByUserId(data) {
    return request({
        url: '/together/siteInfo/findSiteInfoByUserId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据站点id修改站点状态
export function updateSiteStatusById(data) {
    return request({
        url: '/together/siteInfo/updateSiteStatusById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}