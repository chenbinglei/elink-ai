import request from "@/utils/request";

// 查询图标数据
export function findDataQueryList(data) {
    return request({
        url: "/crontab/dataQuery/findDataQueryList",
        portNum: "60006",
        method: "post",
        data: data,
    });
}
