import request from '@/utils/request';

// 查询巡检任务列表
export function queryInspectionTaskList(data) {
    return request({
        url: `/together/inspection/queryInspectionTaskList`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 根据用户id和节点类型查询巡检节点人员用户名称
export function findInspectionUserList(data) {
    return request({
        url: `/together/inspection/findInspectionUserList`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 修改巡检任务
export function updateInspectionTask(data) {
    return request({
        url: `/together/inspection/updateInspectionTask`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 新增或编辑节点人员
export function saveInspectionUser(data) {
    return request({
        url: `/together/inspection/saveInspectionUser`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 获取节点人员设置列表
export function getInspectionUserList(data) {
    return request({
        url: `/together/inspection/getInspectionUserList`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 根据巡检任务id查询巡检任务详情
export function findInspectionTaskDetailById(data) {
    return request({
        url: `/together/inspection/findInspectionTaskDetailById`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}



// 删除巡检任务
export function deleteInspectionTaskByIds(data) {
    return request({
        url: `/together/inspection/deleteInspectionTaskByIds`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 新增巡检任务
export function saveInspectionTask(data) {
    return request({
        url: `/together/inspection/saveInspectionTask`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 获取新增任务站点列表
export function getSiteSaveTaskList(data) {
    return request({
        url: `/together/inspection/getSiteSaveTaskList`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 根据站点id查询巡检项配置列表
export function queryInspectionItemList(data) {
    return request({
        url: `/together/inspection/queryInspectionItemList`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 新增或编辑巡检项配置

export function saveInspectionItem(data) {
    return request({
        url: `/together/inspection/saveInspectionItem`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 导入巡检项配置
export function importInspectionItem(data) {
    return request({
        url: `/together/inspection/importInspectionItem`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 删除巡检项配置
export function deleteAllInspectionItemByIds(data) {
    return request({
        url: `/together/inspection/deleteAllInspectionItemByIds`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
