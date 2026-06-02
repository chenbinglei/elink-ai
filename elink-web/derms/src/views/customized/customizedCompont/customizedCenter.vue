<template>
  <div class="center-content">
    <img class="bg-img" src="/src/assets/customized/home0001.png" alt="" />
    <!-- 直流配电柜-直流 --线条-->
    <div class="line-test"></div>
    <div class="line-test8 "></div>
    <div class="line-test-1 line-test2"></div>
    <!-- 直流配电柜-注塑机ju --线条2 -->
    <div class="line-test1"></div>
    <div class="line-test2 "></div>
    <!-- 直流配电柜-注塑机ma--线条2 -->
    <div class="line-test3 line-test1"></div>
    <div class="line-test4"></div>
    <!-- 直流配电柜-注塑机VEG--线条2 -->
    <div class="line-test5 line-test1"></div>
    <div class="line-test6 line-test2"></div>
    <!-- 交流-直线 -->
    <div class="line-test7"></div>


    <!-- 设备点击项 -->
    <div class="centerTips">
      <div v-for="item in deviceConfig" :key="item.type" class="centerTips-item"
        :class="[item.class, { active: chooseType === item.type }]" @click="changeType(item)">
        {{ item.label }}
      </div>
    </div>

    <!-- 连线 -->
    <div :class="`line-${chooseType} line-chooseType`" v-if="chooseType && !chooseType.includes('IMM')">
      <div class="line"></div>
      <img src="/src/assets/customized/down-arrow.png" class="image-line" />
    </div>

    <!-- 悬浮提示框 -->
    <div :class="`tooltip-${chooseType} tooltip`" v-if="chooseType && !chooseType.includes('IMM')">
      <div class="tooltip-title">{{ chooseName }}</div>

      <!-- 普通设备 -->
      <div class="tooltip-content" v-if="chooseType !== 'ESC'">
        <div v-for="item in tooltipTypeList" :key="item.name" class="tooltip-content-item">
          <div>{{ item.name }}</div>
          <div class="content"><span>{{ item.value??'--' }}</span>{{ item.unit }}</div>

        </div>
      </div>

      <!-- 储能柜 -->
      <div class="tooltip-content esc" v-else>
        <div v-for="item in tooltipTypeList" :key="item.name" class="tooltip-content-item">
          <div class="title" style="color:#00ffff;margin-bottom: 8px;">{{ item.name }}</div>
          <div v-for="i in item.children" :key="i.name" class="esc-content">
            <div>{{ i.name }}</div>
            <div class="content"><span>{{ i.value??'--' }}</span>{{ i.unit }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="filter"></div>
    <div class="center-name">直流展示区</div>
  </div>
</template>

<script setup>
import { ref, watch, onUnmounted, onMounted } from 'vue'
import { setTooltipList } from './centerTooltip.js'
import {
  getPvDcDcDeviceData,
  getSeCabinetDeviceData,
  getAcGGDDeviceData,
  getDCADDeviceData,
  getDCBusDeviceData,
} from '@/api/customized/api'

const emit = defineEmits(['ImmDialogVisible'])
const props = defineProps({
  site: {
    type: Object,
    default: () => ({}),
  },
  ImmDialogVisible: {
    type: Boolean,
    default: false
  }
})

// 设备配置
const deviceConfig = ref([
  { id: 1, type: 'ACDC', label: '交流配电柜', class: 'ACDC' },
  { id: 2, type: 'ACBC', label: '直流母线柜', class: 'ACBC' },
  { id: 3, type: 'IMMJU', label: '注塑机JU5500V', class: 'IMMJU' },
  { id: 4, type: 'IMMMA', label: '注塑机MA1600V', class: 'IMMMA' },
  { id: 5, type: 'IMMVE', label: '注塑机VE1500V', class: 'IMMVE' },
  { id: 6, type: 'PVA', label: '光伏阵列', class: 'PVA' },
  { id: 7, type: 'DCDC', label: '直流配电柜', class: 'DCDC' },
  { id: 8, type: 'ESC', label: '储能柜', class: 'ESC' },
])

// 排除注塑机
const excludeTypes = ['IMMJU', 'IMMMA', 'IMMVE']
const autoTipDevices = deviceConfig.value.filter(d => !excludeTypes.includes(d.type))

const apiMap = {
  PVA: getPvDcDcDeviceData,
  ACDC: getAcGGDDeviceData,
  ACBC: getDCBusDeviceData,
  DCDC: getDCADDeviceData,
  ESC: getSeCabinetDeviceData,
}

const siteId = ref('')
const chooseType = ref('')
const chooseName = ref('')
const tooltipTypeList = ref([])

let autoTipTimer = null
let currentIndex = 0

// 停止自动轮播
function stopAutoTip () {
  if (autoTipTimer) {
    clearInterval(autoTipTimer)
    autoTipTimer = null
  }
}

// ✅ 统一关闭所有提示（核心）
const closeAll = () => {
  chooseType.value = ''
  chooseName.value = ''
  tooltipTypeList.value = []
  emit('ImmDialogVisible', false)
  startAutoTip() // 关闭后恢复自动轮播
}

// 其他任何地方（图片、空白、tooltip、线条）都关闭
const handleClick = (e) => {
  // 查找点击的元素是否是【设备按钮】
  const isClickItem = e.target.closest('.centerTips-item')
  
  // 不是按钮 → 关闭所有
  if (!isClickItem) {
    closeAll()
  }
}
onMounted(() => {
  window.addEventListener('click', handleClick)
})

onUnmounted(() => {
  window.removeEventListener('click', handleClick)
  stopAutoTip()
})

// 自动轮播
function startAutoTip () {
  stopAutoTip()
  autoTipTimer = setInterval(() => {
    const item = autoTipDevices[currentIndex]
    currentIndex = (currentIndex + 1) % autoTipDevices.length
    showTip(item)
  }, 5000)
}

// 显示提示（先请求再显示）
const showTip = async (item) => {
  chooseType.value = ''
  chooseName.value = ''
  tooltipTypeList.value = []
  await fetchDeviceData(item.type)
  chooseType.value = item.type
  chooseName.value = item.label
}

watch(
  () => props.site,
  (newVal) => {
    if (newVal) siteId.value = newVal.id
  },
  { immediate: true }
)

watch(
  () => props.ImmDialogVisible,
  (newVal) => {
    if (!newVal) {
      chooseType.value = ''
      chooseName.value = ''
      tooltipTypeList.value = []
    }
  },
  { immediate: true }
)

// 点击设备按钮
const changeType = (item) => {
  stopAutoTip()

  if (chooseType.value === item.type) {
    closeAll()
    return
  }

  chooseType.value = item.type
  chooseName.value = item.label
  tooltipTypeList.value = []

  if (item.type.includes('IMM')) {
    emit('ImmDialogVisible', true, item)
  } else {
    fetchDeviceData(item.type)
  }
}

// 请求数据
const fetchDeviceData = async (type) => {
  const api = apiMap[type]
  if (!api) return

  let params = { deviceId: siteId.value }
  if (type === 'ESC') params = { ...params, batteryIds: '1', seDcIds: '1' }

  const res = await api(params)
  const list = res.data
  const tooltip = setTooltipList(list).value
  tooltipTypeList.value = tooltip[type]
}

// 启动
startAutoTip()
</script>
<style scoped lang="scss">
@import './style.scss';

/* 你的样式完全保留，不动 */
.center-content {
  width: 100%;
  height: 100%;
  position: relative;
  z-index: 1;

  .bg-img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: fill;
    z-index: 1;
  }


  .center-name {
    position: absolute;
    left: 50%;
    bottom: 48px;
    transform: translateX(-50%);
    z-index: 10;
    width: 38%;
    height: 8.8%;
    background: url(/src/assets/customized/center-bottom.png) no-repeat center center;
    background-size: 100% 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: YouSheBiaoTiHei, sans-serif;
    font-size: 20px;
    color: #fff;
    pointer-events: none;
  }

  .filter {
    position: absolute;
    top: 82%;
    z-index: 2;
    width: 100%;
    height: 24px;
    background: rgba(255, 255, 255, 0.6);
    opacity: 0.25;
    filter: blur(14px);
    pointer-events: none;
  }

  .centerTips {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 99;
    pointer-events: auto;
  }

  .centerTips-item {
    position: absolute;
    background: url(/src/assets/customized/center-tips.png) no-repeat center center;
    background-size: 100% 100%;
    font-family: YouSheBiaoTiHei, sans-serif;
    font-size: 16px;
    color: #fff;
    padding: 5px 12px;
    cursor: pointer;
    user-select: none;
    white-space: nowrap;
  }

  .centerTips-item.active {
    color: #00ffff;
  }

  .tooltip {
    position: absolute;
    z-index: 1000;

    .tooltip-title {
      background: url(/src/assets/customized/tooltip-title.png) no-repeat center center;
      background-size: 100% 100%;
      width: 100px;
      height: 30px;
      font-family: YouSheBiaoTiHei, YouSheBiaoTiHei;
      font-weight: 400;
      font-size: 16px;
      color: #FFFFFF;
      padding: 3px 48px;
      display: flex;
      align-items: center;
    }

    .esc {
      height: 200px;
      overflow: auto;

      .tooltip-content-item {
        display: inline-block !important;
        width: 86%;
        margin: 0 auto;
      }

      .esc-content {
        display: flex;
        align-items: center;
        font-size: 12px;
        justify-content: space-between;
      }
    }

    .tooltip-content {
      background: url(/src/assets/customized/tooltip-content.png) no-repeat center center;
      background-size: 100% 100%;
      width: 100%;
      top: 30px;
      position: absolute;
      padding: 8px 0 10px 0;
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 14px;
      color: rgba(255, 255, 255, .9);

      .tooltip-content-item {
        display: flex;
        align-items: center;
        padding: 3px 12px;
        justify-content: space-between;
        font-family: Microsoft YaHei, Microsoft YaHei;
        font-weight: 400;
        font-size: 14px;
        color: #FFFFFF;
        text-stroke: 1px rgba(0, 0, 0, 0);

        .content {
          font-family: DIN Next LT Pro, DIN Next LT Pro;
          font-weight: 400;
          font-size: 14px;
          color: #FFFFFF;

          span {
            font-family: DIN, DIN;
            font-weight: 500;
            font-size: 18px;
            color: #00FFFF;
            padding-right: 5px;
          }
        }
      }
    }
  }

  .tooltip-ACDC {
    top: 20%;
    left: 46%;
  }

  .ACDC {
    top: 31%;
    left: 28%;
  }

  .line-chooseType {
    position: absolute;
    z-index: 5;
  }

  .line-ACDC {
    top: 32.5%;
    left: 35.4%;
    width: 12%;
    display: flex;
    align-items: center;

    .image-line {
      width: 18px;
      height: 16px;
      transform: rotate(90deg);
      position: absolute;
      left: -5px;
    }

    .line {
      border-bottom: 1px dashed #00FFFF;
      width: 90%;
    }
  }

  .tooltip-ACBC {
    top: 22%;
    left: 46%;
  }

  .line-ACBC {
    top: 33.5%;
    left: 61%;
    width: 9%;
    display: flex;
    align-items: center;

    .image-line {
      transform: rotate(-90deg);
      width: 18px;
      height: 16px;
    }

    .line {
      border-bottom: 1px dashed #00FFFF;
      width: 90%;
    }
  }

  .tooltip-ESC {
    top: 6%;
    left: 78%;
  }

  .line-ESC {
    top: 40%;
    left: 84.5%;
    height: 10%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    .image-line {
      width: 18px;
      height: 16px;
    }

    .line {
      border-left: 1px dashed #00FFFF;
      height: 90%;
    }
  }

  .tooltip-PVA {
    top: 7%;
    left: 67%;
  }

  .line-PVA {
    top: 31.5%;
    left: 73.5%;
    height: 19%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    .image-line {
      width: 18px;
      height: 16px;
    }

    .line {
      border-left: 1px dashed #00FFFF;
      height: 90%;
    }
  }

  .tooltip-DCDC {
    top: 28%;
    left: 4%;
  }

  .line-DCDC {
    top: 45.5%;
    left: 9.5%;
    height: 19%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    .image-line {
      width: 18px;
      height: 16px;
    }

    .line {
      border-left: 1px dashed #00FFFF;
      height: 90%;
    }
  }

  .ACBC {
    top: 33%;
    left: 70%;
  }

  .IMMJU {
    top: 48%;
    left: 27.5%;
  }

  .IMMMA {
    top: 61.5%;
    left: 24.5%;
  }

  .IMMVE {
    top: 64%;
    left: 52%;
  }

  .PVA {
    top: 50%;
    left: 71%;
  }

  .DCDC {
    top: 65%;
    left: 6%;
  }

  .ESC {
    top: 49.6%;
    left: 82.5%;
  }
}
</style>