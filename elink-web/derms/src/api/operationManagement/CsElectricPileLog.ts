import request from '@/utils/request';

// 查询协议日志列表
export function queryProtocolList(data) {
    return request({
        url: '/together/pileLog/queryProtocolList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 分页查询告警列表数据
export function findAlarmListByPage(data) {
    return request({
        url: '/together/deviceManage/findAlarmListByPage',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
