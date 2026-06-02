import request from '@/utils/request';

// 分页查询电桩列表信息
export function findPileListByPage(data) {
    return request({
        url: '/together/deviceManage/findPileListByPage',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 修改设备运营状态
export function updateDeviceOperateStatus(data) {
    return request({
        url: '/together/deviceManage/updateDeviceOperateStatus',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 为电桩设置二维码
export function pileSetQr(data) {
    return request({
        url: '/together/deviceManage/pileSetQr',
        portNum: 60009,
        method: 'post',
        data: data
    });
}