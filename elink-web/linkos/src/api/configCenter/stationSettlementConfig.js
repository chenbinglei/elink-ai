import request from '@/utils/request'

// 分页查询站点信息列表
export function findSiteInfoListByPage(data) {
    return request({
        url: '/operate/siteInfo/findSiteInfoListByPage',
        method: 'post',
        data: data
    })
}

// 查询站点下拉列表
export function findSiteBasicInfoByTenantId(data) {
    return request({
        url: '/operate/order/findSiteBasicInfoByTenantId',
        method: 'post',
        data: data
    })
}

// 查询运营商下拉列表
export function findOperatorInfoByTenantId(data) {
    return request({
        url: '/operate/order/findOperatorInfoByTenantId',
        method: 'post',
        data: data
    })
}