import request from '@/utils/request';
import requestVue from '@/utils/requestVue';

// 统计站点电桩监视数据
export function countSitePileMonitor (data) {
    return request({
        url: '/together/runScene/countSitePileMonitor',
        method: 'post',
        portNum: 60009,
        data: data
    });
}

// 根据id查询电桩详情信息
export function findPileDetailById (data) {
    return request({
        url: '/together/runScene/findPileDetailById',
        method: 'post',
        portNum: 60009,
        data: data
    });
}

// 编辑电枪数据
export function updatePileGunDetailById (data) {
    return request({
        url: '/together/runScene/updatePileGun',
        method: 'post',
        portNum: 60009,
        data: data
    });
}

// 编辑电桩扩展属性数据
export function updatePileReaById (data) {
    return request({
        url: '/together/runScene/updatePileRea',
        method: 'post',
        portNum: 60009,
        data: data
    });
}


// 启动充放电
export function pileStartDisAndCharging (data) {
    return request({
        url: "/together/siteInfo/pileStart",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 功率控制
export function pilePowerCtrl (data) {
    return request({
        url: "/together/siteInfo/powerCtrl",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 停止充放电/停止预约
export function pileStopDisAndCharging (data) {
    return request({
        url: "/together/siteInfo/pileStop",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 电桩复位
export function pileReset (data) {
    console.log("datadayin", data)
    return request({
        url: "/together/siteInfo/pileReset",
        portNum: 60009,
        method: "post",
        data: data,
        
        headers: {
            // "Content-Type": "application/json"
             'X-Skip-Transform':'true'
        },
       
    });
}