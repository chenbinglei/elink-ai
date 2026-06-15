import request from "@/utils/request";

// 添加或编辑系统变量数据
export function saveOrUpdateSystemVariable(data) {
    return request({
        url: "/crontab/systemVariable/saveOrUpdateSystemVariable",
        portNum: "60006",
        method: "post",
        data: data,
    });
}

// 分页查询系统变量数据
export function findSystemVariableListByPage(data) {
    return request({
        url: "/crontab/systemVariable/findSystemVariableListByPage",
        portNum: "60006",
        method: "post",
        data: data,
    });
}

// 根据关联实例id删除关联实例数据
export function deleteVariableNodeById(data) {
    return request({
        url: "/crontab/systemVariable/deleteVariableNodeById",
        portNum: "60006",
        method: "post",
        data: data,
    });
}

// 根据变量id删除系统变量数据
export function deleteSystemVariableById(data) {
    return request({
        url: "/crontab/systemVariable/deleteSystemVariableById",
        portNum: "60006",
        method: "post",
        data: data,
    });
}

// 分页查询未被关联的计算节点数据
export function findNotComputeNodeByPage(data) {
    return request({
        url: "/crontab/systemVariable/findNotComputeNodeByPage",
        portNum: "60006",
        method: "post",
        data: data,
    });
}

// 分页查询未被关联的模型功能点
export function findModelFunctionListByPage(data) {
    return request({
        url: "/crontab/systemVariable/findModelFunctionListByPage",
        portNum: "60006",
        method: "post",
        data: data,
    });
}

// 添加实例
export function addVariableNode(data) {
    return request({
        url: "/crontab/systemVariable/addVariableNode",
        portNum: "60006",
        method: "post",
        data: data,
    });
}
