import { ref } from 'vue'

export let selectList = ref([
  { name: '1', value: "创建时间" },
  { name: '2', value: "订单状态" },
  { name: '3', value: "异常类型" },
  { name: '4', value: "筛选" }
])

export let timeSelectList = ref([
  { value: '0', label: "今日" },
  { value: '1', label: "昨日" },
  { value: '2', label: "近七天" },
  { value: '3', label: "近一月" },
  { value: '5', label: "近三月" },
  { value: '6', label: "自定义" }
])

export let changeType = ref([
  { id: null, name: "全部" },
  // { id: 0, name: "未进行" },
  { id: 1, name: "充电中" },
  { id: 2, name: "充电完成" },
  { id: 3, name: "启动失败" },
  { id: 4, name: "订单挂起" },
  { id: 5, name: "订单取消" },
  { id: 6, name: "预约中" }
])
export let dischangeType = ref([
  { id: null, name: "全部" },
  // { id: 0, name: "未进行" },
  { id: 1, name: "放电中" },
  { id: 2, name: "放电完成" },
  { id: 3, name: "启动失败" },
  { id: 4, name: "订单挂起" },
  { id: 5, name: "订单取消" },
  { id: 6, name: "预约中" }
])

export let errType = ref([
  { id: null, name: "全部" },
  { id: 0, name: "无异常" },
  { id: 1, name: "时间异常", alterText: '订单时长大于24h' },
  { id: 2, name: "大额订单", alterText: '订单总金额大于1000元' },
  { id: 3, name: "电量异常", alterText: '电量大于500度' },
  { id: 4, name: "无效订单", alterText: '电量小于1度' },
  { id: 5, name: "费用异常", alterText: '订单金额为0元' }
])

export let typeListArray = ref([
  { id: null, name: "全部" },
  { id: 28, name: "交流" },
  { id: 29, name: "直流" },
  { id: 30, name: "V2G" },
])

export let runModeArray = ref([
  { id: null, name: "全部" },
  { id: 1, name: "App" },
  { id: 2, name: "平台" },
  { id: 3, name: "电卡" },
  { id: 4, name: "VIN码" },
  { id: 5, name: "电桩屏幕" },
  { id: 6, name: "有序控制" },
  { id: 7, name: "离线卡" },
])

export let repairStatusArray = ref([
  { id: null, name: "全部" },
  { id: 0, name: "挂单" },
  { id: 1, name: "自动恢复" },
  { id: 2, name: "人工恢复" },
  { id: 3, name: "正常" },
])

export let orderDetailTab=ref([
  {
    label:"订单信息",
    value:"1"
  },{
    label:"订单轨迹",
    value:"2"
  },{
    label:"订单结算",
    value:"3"
  },{
    label:"过程数据",
    value:"4"
  },{
    label:"计费详情",
    value:"5"
  },
])