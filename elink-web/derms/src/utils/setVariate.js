// 站点状态
export let site_status_array = [
    {id: 1, name: "正常投运"},
    {id: 2, name: "关闭下线"},
    {id: 3, name: "维护中"},
    {id: 4, name: "建设中"}
];

export let site_status_all_array = [
    {id: 1, name: "正常投运"},
    {id: 2, name: "关闭下线"},
    {id: 3, name: "维护中"},
    {id: 4, name: "建设中"}
];


// 电桩工作状态
export let pile_status_array = [
    {id: 1, name: "在线"},
    {id: 2, name: "维护"},
    {id: 3, name: "故障"},
    {id: 88, name: "离线"}
];

//枪状态
export let gun_work_state_array = [
    {id: 1, name: "充电中"},
    {id: 2, name: "放电中"},
    {id: 3, name: "空闲中"},
    {id: 4, name: "占用中"},
    {id: 8, name: "预约中"},
    {id: 5, name: "故障"},
    {id: 6, name: "离线"},
    // {id: 7, name: "未注册"},
    // {id: -1, name: "未知"}
];

// 电桩类型
export let pile_type_array = [
    {id: 28, name: "交流"},
    {id: 29, name: "直流"},
    {id: 30, name: "V2G"}
];

// 充放电订单状态
export let dc_order_status_array = [
    {id: 0, name: "未进行"},
    {id: 1, name: "充电中", componentName: "CsChargingRecord"},
    {id: 2, name: "充电完成", componentName: "CsChargingRecord"},
    {id: 1, name: "放电中", componentName: "CsDisChargingRecord"},
    {id: 2, name: "放电完成", componentName: "CsDisChargingRecord"},
    {id: 3, name: "启动失败"},
    {id: 4, name: "订单挂起"},
    {id: 5, name: "订单取消"},
    {id: 6, name: "预约中"}
];

// 充放电订单状态
export let oc_order_status_array = [
    {id: 1, name: "在途"},
    {id: 2, name: "待支付"},
    {id: 3, name: "已完成"},
    {id: 9, name: "异常"}
];

// 电桩启动方式
export let pile_run_mode_array = [
    {id: 1, name: "App启动"},
    {id: 2, name: "平台启动"},
    {id: 3, name: "电卡"},
    {id: 4, name: "VIN码"},
    {id: 5, name: "电桩屏幕强制启动"},
    {id: 6, name: "有序控制"},
    {id: 7, name: "离线卡启动"}
];

// 建筑场所
export let build_site_array = [
    {id: 1, name: "居民区"},
    {id: 2, name: "公共机构"},
    {id: 3, name: "企事业单位"},
    {id: 4, name: "写字楼"},
    {id: 5, name: "工业园区"},
    {id: 6, name: "交通枢纽"},
    {id: 7, name: "大型文体设施"},
    {id: 8, name: "城市绿地"},
    {id: 9, name: "大型建筑配建停车场"},
    {id: 10, name: "路边停车位"},
    {id: 11, name: "城际高速服务区"},
    {id: 12, name: "国省道路沿线"},
    {id: 13, name: "城际快速公路沿线"},
    {id: 14, name: "其他"}
];

// 商户平台
export let pay_plat_form_array = [
    {id: 1, name: "微信"},
    {id: 2, name: "支付宝"},
    {id: 3, name: "银联商户"}
];


// 区域类型
export let area_type_array = [{id: 1, name: "省"}, {id: 2, name: "市"}];


// 策略类型
export let strategy_type_array = [
    {id: 1, name: "综合智能策略"},
    {id: 2, name: "峰谷套利策略"},
    {id: 3, name: "削峰策略"},
    {id: 4, name: "定时策略"},
    {id: 5, name: "限电策略"},
    {id: 6, name: "变压器扩容策略"},
    {id: 7, name: "负载扩容策略"},
    {id: 8, name: "备用电源策略"}
];

// 光伏类型
export let pv_type_array = [
    {id: 0, name: "分布式商业"},
    {id: 1, name: "分布式户用"},
    {id: 2, name: "集中式"},
];

// 消纳方式
export let consum_mode_array = [
    {id: 1, name: "自发自用余电上网"},
    {id: 2, name: "全额上网"},
    {id: 3, name: "离网自用"},
];

// 并网等级
export let tied_grade_array = [
    {id: 0, name: "0.4kV"},
    {id: 1, name: "10kV"},
    {id: 2, name: "20kV"},
    {id: 3, name: "35kV"},
    {id: 4, name: "110kV"},
    {id: 5, name: "220kV"},
];

// 组件库 ------》  组件类型
export let module_type_array = [
    {id: 1, name: "多晶"},
    {id: 2, name: "单晶"},
    {id: 3, name: "叠瓦"},
    {id: 4, name: "P型双面"},
    {id: 5, name: "N型双面"}
];

// ------------运维管理--任务状态
export let task_status_array = [
    // {id: 0, name: "全部"},
    {id: 1, name: "未分配"},
    {id: 2, name: "未开启"},
    {id: 3, name: "巡检中"},
    {id: 4, name: "待验收"},
    {id: 5, name: "完结"},
]