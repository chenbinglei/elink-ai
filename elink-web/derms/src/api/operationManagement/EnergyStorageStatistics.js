import request from '@/utils/request';

// 根据站点id查询并网点电表数据
export function getMeterListBySiteId(data) {
    return request({
        url: '/together/storageCount/getMeterListBySiteId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
//统计储能充放电量
export function countStorageQt(data) {
    return request({
        url: '/together/storageCount/countStorageQt',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 统计储能收益分析
export function countStorageIncome(data) {
    return request({
        url: '/together/storageCount/countStorageIncome',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 度电收益
export function countStorageRevenue(data) {
    return request({
        url: '/together/storageCount/countStorageKwhIncome',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

