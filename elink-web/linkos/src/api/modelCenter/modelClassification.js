import request from "@/utils/request";


// 查询模型分类树结构列表
export function getSortTreeList(data) {
    return request({
        url: "/device/sort/getSortTreeList",
        portNum: "60003",
        method: "post",
        data: data,
    });
}


// 查询模型分类列表
export function querySortList(data) {
    return request({
        url: "/device/sort/querySortList",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 新增编辑模型分类
export function saveSort(data) {
    return request({
        url: "/device/sort/saveSort",
        portNum: "60003",
        method: "post",
        data: data,
    });
}


// 根据模型分类id删除模型分类数据
export function deleteSortById(data) {
    return request({
        url: "/device/sort/deleteSortById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}
