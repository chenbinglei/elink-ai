import request from "@/utils/request";

// 分页查询数据补录信息
export function findNodeAddRecordListByPage(data) {
    return request({
        url: "/crontab/nodeAddRecord/findNodeAddRecordListByPage",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 添加数据补录信息
export function saveNodeAddRecord(data) {
    return request({
        url: "/crontab/nodeAddRecord/saveNodeAddRecord",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 根据节点id和时间补录taos数据
export function addRecordNodeDataById(data) {
    return request({
        url: "/crontab/nodeAddRecord/addRecordNodeDataById",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 根据设备id查询计算节点列表
export function findNodeListByDeviceId(data) {
    return request({
        url: "/crontab/nodeAddRecord/findNodeListByDeviceId",
        portNum: 60006,
        method: "post",
        data: data,
    });
}
