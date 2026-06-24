import request from "@/utils/request";


// 根据报表数据id查询报表数据
export function findReportDataById(data) {
    return request({
        url: "/wisdomcharge/reportdata/findReportDataById",
        method: 'post',
        data: data
    })
}

// 自定义请求接口
export function customRequestPort(data,requestUrl) {
    return request({
        url: requestUrl,
        method: 'post',
        data: data
    })
}
