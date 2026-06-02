<template>
  <div class="containers">
    <div class="power-energy">
      <!-- 顶部类型切换 -->
      <div class="power-title horizontal">
        <div class="power-title-item" @click="getchoosePower(item.id)" :class="{ active: item.id === choosePower }"
          v-for="item in powerTypeList" :key="item">
          {{ item.label }}
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="power-content">
        <!-- 直流母线特殊结构 -->
        <div class="power-data" v-if="choosePower === 2">
          <div class="power-data-group" v-for="group in DC_BUS_DATA" :key="group.name">
            <div class="group-title horizontal">{{ group.name }}</div>
            <div class="data-item horizontal" v-for="(item, idx) in group.children" :key="item.name"
              :class="{ 'bg-line': idx % 2 === 0 }">
              <span class="label">{{ item.name }}</span>
              <!-- <render-value :data="item" /> -->
              <span class="value value-1" v-if="item.value1 != undefined">{{ item.value1 }}<span class="unit">{{
                item.unit }}</span></span>
              <span class="value value-2" v-if="item.value2 != undefined">{{ item.value2 }}<span class="unit">{{
                item.unit }}</span></span>
              <span class="value value-3" v-if="item.value3 != undefined">{{ item.value3 }}<span class="unit">{{
                item.unit }}</span></span>
            </div>
          </div>
        </div>

        <!-- 其他常规类型 -->
        <div class="power-data" v-else>
          <div class="data-item horizontal" v-for="(item, idx) in currentPowerData" :key="item.name"
            :class="{ 'bg-line': idx % 2 === 0 }">
            <span class="label">{{ item.name }}</span>
            <!-- <render-value :data="item" /> -->
            <span class="value value-1" v-if="item.value1 != undefined">{{ item.value1 }}<span class="unit">{{ item.unit
                }}</span></span>
            <span class="value value-2" v-if="item.value2 != undefined">{{ item.value2 }}<span class="unit">{{ item.unit
                }}</span></span>
            <span class="value value-3" v-if="item.value3 != undefined">{{ item.value3 }}<span class="unit">{{ item.unit
                }}</span></span>
            <span class="value value-2" v-if="item.value != undefined">{{ item.value ?? '--' }}<span class="unit">{{
              item.unit
            }}</span></span>
          </div>
        </div>
      </div>

      <!-- 分割线 -->
      <div class="h-line" />

      <!-- 电量统计 + 时间切换 -->
      <div class="power-statistics horizontal">
        <div class="power-statistics-left horizontal">
          <img src="@/assets/customized/arrow.png" class="power-statistics-icon" alt="">
          电量统计
        </div>
        <div class="power-statistics-right horizontal">
          <div class="stat-item" :class="{ active: item.id === chooseTimeType }" v-for="item in timeTypeList"
            :key="item.id" @click="getchooseTimeType(item.id)">
            {{ item.name }}
          </div>
        </div>
      </div>
      <!-- 电量统计-柱状图 -->
      <div class="power-charts">
        <barCharts style="width: 100%; height: 100%;" :lineList="lineList" :dataList="dataList" />


      </div>

    </div>
    <div class="alarm-content">
      <div class="custom-item-title">告警信息</div>
      <div class="alarm-item-content" v-if="ALARM_INFO.length > 0">
        <div class="alarm-item-content-item horizontal" v-for="item in ALARM_INFO" :key="item.id">
          <div class="alarm-item-content-item-left">
            <img :src="item.img" alt="" class="alarm-item-icon" />
            <span :class="`alarm-status-${item.eventLevel}`">{{ alarmStatusMap[item.eventLevel] }}</span>
          </div>
          <div class="alarm-item-content-item-name">
            <el-tooltip :content="item.eventName" placement="top">
              <div>{{ item.eventName }}</div>

            </el-tooltip>
            <el-tooltip :content="item.deviceName" placement="top">
              <span style="color:rgba(255, 255, 255, .5);">{{ item.deviceName }}</span>
            </el-tooltip>


          </div>
          <div class="alarm-item-content-item-center">
            <!-- <el-tooltip :content="item.createTime" placement="top">
              <span>{{ item.createTime }}</span>
            </el-tooltip> -->
             <span>{{ item.createTime }}</span>
          </div>



        </div>

      </div>
      <div class="alarm-item-content horizontal" style="justify-content: center;" v-else>

        <div class="empty"> <img src="/src/assets/customized/empty.png" />暂无告警信息</div>
      </div>

    </div>
  </div>
</template>

<script setup>
import status1 from '@/assets/customized/status2.png'
import status2 from '@/assets/customized/status4.png'
import status3 from '@/assets/customized/status3.png'
import status4 from '@/assets/customized/status1.png'
import status5 from '@/assets/customized/status5.png'
import barCharts from './barCharts.vue'
import { getSiteQtCurve, getSiteAlarmList } from '@/api/customized/api'
import { ref, computed, defineProps, h, watch } from 'vue'
const props = defineProps({
  websoctList: {
    type: Object,
    default: () => ({})
  },
  site: {
    type: String,
    default: ''
  }
})
const lineList = ref([])
const dataList = ref([])
const statusMap = {
  '1': status1,
  '2': status2,
  '3': status3,
  '4': status4,
  '5': status5
}
// —————————— 所有类型数据（统一对象结构，不再用数组套对象） ——————————
const POWER_DATA_MAP = ref({
  关口: [
    { name: '三相电压', value1: '380', value2: '380', value3: '380', unit: 'V' },
    { name: '三相电流', value1: '380', value2: '380', value3: '380', unit: 'A' },
    { name: '总有功功率', value: '3', unit: 'kW' },
    { name: '功率因数', value: '3' },
    { name: '频率', value: '3', unit: 'Hz' }
  ],
  光伏: [
    { name: '母线电压', value: '3', unit: 'V' },
    { name: '母线电流', value: '3', unit: 'A' },
    { name: '光伏功率', value: '3', unit: 'kW' },
    { name: 'MPPT电压', value: '3', unit: 'V' },
    { name: '运行状态', value: '开机/关机' },
    { name: '今日发电量', value: '120', unit: 'kWh' },
    { name: '累计发电量', value: '1.12万', unit: 'kWh' }
  ],
  储能: [
    { name: '母线电压', value: '3', unit: 'V' },
    { name: '母线电流', value: '3', unit: 'A' },
    { name: '储能功率', value: '3', unit: 'kW' },
    { name: '电池总电压', value: '650', unit: 'V' },
    { name: '运行状态', value: '充电/放电/静置' },
    { name: 'SOC', value: '50%' },
    { name: '今日充/放电量', value: '120', value1: '120', unit: 'kWh' },
    { name: '累计充/放电量', value: '120', value1: '120', unit: 'kWh' }
  ],
  负载: [
    { name: '母线电压', value: '3', unit: 'V' },
    { name: '母线电流', value: '3', unit: 'A' },
    { name: '负载功率', value: '3', unit: 'kW' },
    { name: '累计总电量', value: '3', unit: 'kWh' }
  ]
})
// —————————— 直流母线固定数据 ——————————
const DC_BUS_DATA = ref([
  {
    name: '交流侧',
    children: [
      { name: '三相电压', value1: '10', value2: '10', value3: '10', unit: 'V' },
      { name: '三相电流', value1: '10', value2: '10', value3: '10', unit: 'A' },
      { name: '总有功功率', value1: '10', unit: 'kW' }
    ]
  },
  {
    name: '直流侧',
    children: [
      { name: '母线电压', value1: '10', unit: 'V' },
      { name: '母线电流', value1: '10', unit: 'A' },
      { name: '母线功率', value1: '10', unit: 'kW' }
    ]
  }
])
watch(() => props.websoctList, (newVal) => {
  if (newVal && Object.keys(newVal).length > 0) {

    console.log('realData 变化了:', newVal, newVal.stageGate)

    // 假设 props.websoctList 是一个对象，包含 dcBus 字段
    DC_BUS_DATA.value = [
      {
        name: '交流侧',
        children: [
          { name: '三相电压', value1: newVal.dcBus.voltageA, value2: newVal.dcBus.voltageB, value3: newVal.dcBus.voltageC, unit: 'V' },
          { name: '三相电流', value1: newVal.dcBus.currentA, value2: newVal.dcBus.currentB, value3: newVal.dcBus.currentC, unit: 'A' },
          { name: '总有功功率', value2: newVal.dcBus.power, unit: 'kW' }
        ]
      },
      {
        name: '直流侧',
        children: [
          { name: '母线电压', value2: newVal.dcBus.busVoltage, unit: 'V' },
          { name: '母线电流', value2: newVal.dcBus.busCurrent, unit: 'A' },
          { name: '母线功率', value2: newVal.dcBus.busPower, unit: 'kW' }
        ]
      }
    ]
    POWER_DATA_MAP.value = {
      1: [
        { name: '三相电压', value1: newVal.stageGate.voltageA ?? '--', value2: newVal.stageGate.voltageB ?? '--', value3: newVal.stageGate.voltageC ?? '--', unit: 'V' },
        { name: '三相电流', value1: newVal.stageGate.currentA ?? '--', value2: newVal.stageGate.currentB ?? '--', value3: newVal.stageGate.currentC ?? '--', unit: 'A' },
        { name: '总有功功率', value: newVal.stageGate.power ?? '--', unit: 'kW' },
        { name: '功率因数', value: newVal.stageGate.powerFactor ?? '--', unit: '' },
        { name: '频率', value: newVal.stageGate.frequency ?? '--', unit: 'Hz' }
      ],
      3: [
        { name: '母线电压', value: newVal.photovoltaic.busVoltage ?? '--', unit: 'V' },
        { name: '母线电流', value: newVal.photovoltaic.busCurrent ?? '--', unit: 'A' },
        { name: '光伏功率', value: newVal.photovoltaic.power ?? '--', unit: 'kW' },
        { name: 'MPPT电压', value: newVal.photovoltaic.mpptVoltage ?? '--', unit: 'V' },
        { name: '运行状态', value: newVal.photovoltaic.runStatusName ?? '--', unit: '' },
        { name: '今日发电量', value: newVal.photovoltaic.dayQt ?? '--', unit: 'kWh' },
        { name: '累计发电量', value: newVal.photovoltaic.totalQt ?? '--', unit: 'kWh' }
      ],
      4: [
        { name: '母线电压', value: newVal.energyStorage.busVoltage ?? '--', unit: 'V' },

        { name: '母线电流', value: newVal.energyStorage.busCurrent ?? '--', unit: 'A' },
        { name: '储能功率', value: newVal.energyStorage.power ?? '--', unit: 'kW' },
        { name: '电池总电压', value: newVal.energyStorage.batteryVoltage ?? '--', unit: 'V' },
        { name: '运行状态', value: newVal.energyStorage.runStatusName ?? '--', unit: '' },

        { name: 'SOC', value: newVal.energyStorage.soc },
        { name: '今日充/放电量', value: newVal.energyStorage.dischargeQt ?? '--', value1: newVal.energyStorage.chargeQt ?? '--', unit: 'kWh' },

        { name: '累计充/放电量', value: newVal.energyStorage.totalDischargeQt ?? '--', value1: newVal.energyStorage.totalChargeQt ?? '--', unit: 'kWh' }
      ],
      5: [
        { name: '母线电压', value: newVal.load.busVoltage ?? '--', unit: 'V' },
        { name: '母线电流', value: newVal.load.busCurrent ?? '--', unit: 'A' },
        { name: '负载功率', value: newVal.load.power ?? '--', unit: 'kW' },
        { name: '累计总电量', value: newVal.load.totalQt ?? '--', unit: 'kWh' }
      ]
    }
    // 
  }
}, { immediate: true })
watch(() => props.site, (newVal, oldVal) => {
  console.log('site:', newVal)
  if (newVal) {
    querySiteQtCurve()
    querySiteAlarmList()
  }
})
const getchooseTimeType = (type) => {
  chooseTimeType.value = type
  querySiteQtCurve()
}
const querySiteAlarmList = () => {
  getSiteAlarmList({ siteId: props.websoctList.siteId, }).then(res => {
    ALARM_INFO.value = res.data?.map(item => ({
      ...item,
      img: statusMap[item.eventLevel]
    }))
    console.log('ALARM_INFO.value:', ALARM_INFO.value)

  })
}
const getchoosePower = (type) => {
  choosePower.value = type
  querySiteQtCurve()
}
const querySiteQtCurve = () => {
  const obj = {
    type: choosePower.value,
    siteId: props.websoctList.siteId,
    dateType: chooseTimeType.value
  }
  getSiteQtCurve(obj).then(res => {
    dataList.value = res.data.dateList

    if (choosePower.value === 1 || choosePower.value === 2) {
      lineList.value = [{
        name: "正向电量",
        value: res.data.curve1List,
        color: 'rgba(0, 232, 131, 1)',
        opacityColor: 'rgba(0, 232, 131, 0.3)',
      }, {
        name: "反向电量",
        value: res.data.curve2List,
        color: 'rgba(24, 144, 255, 1)',
        opacityColor: 'rgba(24, 144, 255, 0.3)',
      }]
    } else if (choosePower.value === 3) {
      lineList.value = [{
        name: "光伏发电量",
        value: res.data.curve1List,
        color: 'rgba(0, 232, 131, 1)',
        opacityColor: 'rgba(0, 232, 131, 0.3)',

      }]
    } else if (choosePower.value === 4) {
      lineList.value = [{
        name: "储能充电量",
        value: res.data.curve1List,
        color: 'rgba(0, 232, 131, 1)',
        opacityColor: 'rgba(0, 232, 131, 0.3)',

      }, {
        name: "储能放电量",
        value: res.data.curve2List,
        color: 'rgba(24, 144, 255, 1)',
        opacityColor: 'rgba(0, 232, 131, 0.3)',

      }]
    } else if (choosePower.value === 5) {
      lineList.value = [{
        name: "负载用电量",
        value: res.data.curve1List,
        color: 'rgba(0, 232, 131, 1)',
        opacityColor: 'rgba(0, 232, 131, 0.3)',
      }]
    }
    // lineList.value = res.data
  })
}


// —————————— 常量配置（纯数据，便于维护） ——————————
const powerTypeList = ref([
  { id: 1, label: '关口' },
  { id: 2, label: '直流母线' },
  { id: 3, label: '光伏' },
  { id: 4, label: '储能' },
  { id: 5, label: '负载' },])
const timeTypeList = ref([
  { id: 1, name: '日' },
  { id: 2, name: '月' },
  { id: 3, name: '年' }
])

// —————————— 状态 ——————————
const choosePower = ref(1)
const chooseTimeType = ref(1)
//—————————— 告警信息固定数据 —————————
const ALARM_INFO = ref([

])




// —————————— 计算属性：当前选中的数据 ——————————
const currentPowerData = computed(() => {
  return POWER_DATA_MAP.value[choosePower.value] || []
})

const alarmStatusMap = computed(() => ({
  4: '提示',
  1: '次要',
  2: '重要',
  3: '紧急',
  5: '离线'
}));

</script>

<style lang="scss" scoped>
/* 全局布局 */
.containers {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 12px;
  box-sizing: border-box;
}

.power-energy {
  width: 100%;
  height: 64%;
  background: url(@/assets/customized/right-bg.png) no-repeat;
  background-size: 100% 100%;
}

/* 顶部标题 */
.power-title {
  padding-left: 17.8%;
  padding-top: 1%;
  font-family: YouSheBiaoTiHei, sans-serif;
  font-size: 20px;
  color: #54a1df;
  gap: 12px;

  &-item {
    cursor: pointer;
    transition: color 0.2s;

    &.active {
      color: #fff;
    }
  }
}

/* 内容区域 */
.power-content {
  width: 90.1%;
  height: 36%;
  margin: 36px auto 0;
  overflow: auto;
  color: #fff;
  font-size: 14px;
}

.power-data {
  &-group {
    padding: 0 8px 0 5px;
  }
}

.group-title {
  height: 26px;
}

.data-item {
  height: 36px;
  padding-left: 10px;
  padding-right: 8px;

  &.bg-line {
    background: rgba(0, 149, 255, 0.1);
  }
}

.label {
  flex: 1;
}

.power-statistics-icon {
  width: 38px;
  height: 38px;
}

/* 数值样式 */
.value {
  font-family: DIN-Medium, DIN, sans-serif;
  font-weight: 500;
  font-size: 18px;


  &-1 {
    color: #ffbb00;
  }

  &-2 {
    color: #00ffff;
    padding-left: 8px;
  }

  &-3 {
    color: #ff0000;
    padding-left: 8px;
  }

  .unit {
    font-family: DIN Next LT Pro, DIN Next LT Pro;
    font-weight: 400;
    font-size: 14px;
    color: #FFFFFF;
    padding-left: 3px;
  }

}

.unit {
  font-family: DIN Next LT Pro, sans-serif;
  font-size: 14px;
  color: #fff;
}

/* 统计区域 */
.power-statistics {
  justify-content: space-between;

  &-left {
    gap: 12px;
    font-family: YouSheBiaoTiHei, sans-serif;
    font-size: 20px;
    color: #fff;
  }

  &-right {
    color: #54a1df;
    border: 1px solid #0071cc;
    border-radius: 4px;
    font-size: 14px;
    margin-right: 16px;
    overflow: hidden;
    cursor: pointer;

    .active {
      font-weight: bold;
      color: #fff;
      background: linear-gradient(180deg, #54a1df 0%, #005599 82.42%, #40a5fe 100%);
    }
  }
}

// 告警信息
.alarm-content {
  background: url(/src/assets/customized/left-top.png) no-repeat;
  background-size: 100% 100%;
  height: 31.5%;
  width: 100%;
  display: flex;
  flex-direction: column;

  .custom-item-title {
    font-family: YouSheBiaoTiHei, Microsoft YaHei;
    font-size: 20px;
    color: #ffffff;
    text-align: left;
    font-weight: 400;
    // transform: skewX(-5deg);
    padding-left: 17.8%;
    padding-top: 1%;
  }

  .alarm-item-content {
    color: #fff;
    margin: 10px 16px 21px 16px;
    flex: 1;
    overflow: auto;

    .alarm-item-content-item {
      padding: 0 12px;
      height: 28.5%;
      justify-content: space-between;
      margin: 10px 0;
      background: url(@/assets/customized/alarm-item-bg.png) no-repeat;
      background-size: 100% 100%;

      .alarm-item-content-item-left {

        font-family: Microsoft YaHei, Microsoft YaHei;
        font-weight: 400;
        font-size: 14px;
        line-height: 19px;
        display: flex;
        width: 25%;

        .alarm-item-icon {
          width: 16px;
          height: 16px;

          padding-right: 5px;
        }

        .alarm-status-1 {
          color: #209BFF;
        }

        .alarm-status-2 {
          color: #FFBB00;
        }

        .alarm-status-3 {
          color: #FF6E00;
        }

        .alarm-status-4 {
          color: #FF1103;
        }

        .alarm-status-5 {
          color: #D9D9D9;
        }


      }

      .alarm-item-content-item-name {
        font-family: Microsoft YaHei, Microsoft YaHei;
        font-weight: 400;
        font-size: 14px;
        line-height: 19px;
        color: #FFFFFF;
        width: 50%;

        div {
          margin-top: 5px;
        }
      }

      .alarm-item-content-item-center {
        width: 29%;
        font-family: Microsoft YaHei, Microsoft YaHei;
        font-weight: 400;
        font-size: 14px;
        line-height: 19px;
        color: #FFFFFF;
        // white-space: nowrap;
        overflow: hidden;
      }


    }
  }
}

// 统计区域
.power-charts {
  width: 100%;
  height: 43%;
}

.stat-item {
  padding: 4px 19px;
}

/* 工具类 */
.horizontal {
  display: flex;
  align-items: center;
}

.h-line {
  width: 90.5%;
  height: 2.39%;
  margin: 0 auto;
  background: url(@/assets/customized/station-line.png) no-repeat;
  background-size: 100% 100%;
}

.empty {

  font-family: Microsoft YaHei, Microsoft YaHei;
  font-weight: 400;
  font-size: 12px;
  color: #54A1DF;
  text-align: center;
}
</style>