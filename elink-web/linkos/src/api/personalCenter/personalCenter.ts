import request from "@/utils/request";

// 根据用户id查询用户详情
export function findUserDetailsById(data) {
    return request({
        url: "/system/systemManage/findUserDetailsById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 保存或编辑用户信息
export function saveOrUpdateUserInfo(data) {
    return request({
        url: "/system/systemManage/saveOrUpdateUserInfo",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 修改用户密码
export function updateUserPassword(data) {
    return request({
        url: "/oauth/user/updateUserPassword",
        portNum: "60001",
        method: "post",
        data: data,
    });
}
