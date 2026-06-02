<template>
  <div class="container">
    <div class="custom-item">
      <div class="custom-item-title">电站概览</div>

      <!-- 内容区 -->
      <div class="custom-item-content">
        <!-- 第一行 -->
        <div class="row">
          <div class="custom-item-content-item" v-for="item in customItemContent.slice(0, 2)" :key="item.title">
            <div class="custom-item-content-item-value">
              {{ item.value1 }} <span class="custom-unit">{{ item.unit1 }}</span> <span v-if="item.value2">{{
                item.value2 }}</span> <span v-if="item.value2" class="custom-unit">{{ item.unit2 }}</span>
            </div>
            <div class="custom-item-content-item-name">{{ item.title }}</div>
            <img :src="item.image" alt="" class="custom-item-content-item-img">
          </div>
        </div>

        <!-- 中间横线 -->
        <div class="h-line"></div>

        <!-- 第二行 -->
        <div class="row">
          <div class="custom-item-content-item" v-for="item in customItemContent.slice(2)" :key="item.title">
            <div class="custom-item-content-item-value">
              {{ item.value1 }} <span class="custom-unit">{{ item.unit1 }}</span> <span v-if="item.value2">{{
                item.value2 }}</span> <span v-if="item.value2" class="custom-unit">{{ item.unit2 }}</span>
            </div>
            <div class="custom-item-content-item-name">{{ item.title }}</div>
            <img :src="item.image" alt="" class="custom-item-content-item-img">
          </div>
        </div>
      </div>
    </div>

    <div class="custom-item">
      <div class="custom-item-title">运行模式</div>
      <div class="custom-item-mode">
        <div class="custom-item-mode-title">
          <div class="custom-item-mode-tips">策略类型<span>
              <el-tooltip :content="alarmStatusMap[props.websoctList?.runMode?.strategyType]" placement="top">
                <span>{{ alarmStatusMap[props.websoctList?.runMode?.strategyType] }}</span>
              </el-tooltip>
            </span>
          </div>
          <div class="custom-item-mode-tips">运行控制<span>{{ props.websoctList?.runMode?.runControl == 1 ? '手动控制' : '自动控制'
          }}</span>
          </div>
        </div>
        <div class="custom-item-mode-content">
          <div v-for="item in modeList" :key="item.deviceId" class="custom-item-mode-content-item">
            <img src="/src/assets/customized/mode-device.png" alt="" class="custom-item-mode-content-item-img" />
            <div class="custom-item-mode-content-item-name">{{ item.deviceNumber }}</div>
            <div class="custom-item-mode-content-item-text">
              <div class="circle" :class="`circle${item.txStatus}`"></div>
              {{ item.txStatus === 0 ? '未注册' : item.txStatus === 1 ? '在线' : item.txStatus === 2 ? '故障' : '离线' }}
            </div>
          </div>

        </div>

      </div>
    </div>
    <div class="custom-item">
      <div class="custom-item-title single">VS交流系统
        <view class="vs-right">
          <img src="/src/assets/customized/vs-details.png" alt="" class="vs-right-img"
            @click="ComprehensiveDialogVisible = true">
          <div class="vs-right-button">
            <div class="vs-right-button-item" :class="{ 'active': InstantType === 1 }" @click="changeType(1)">平均</div>
            <div class="vs-right-button-item" :class="{ 'active': InstantType === 2 }" @click="changeType(2)">
              累计</div>
          </div>
        </view>
      </div>
      <div class="directCurrent-type">
        <div class="directCurrent-type-item between">
          <div class="directCurrent-type-left-circle"></div>
          <div class="directCurrent-type-text">直流系统</div>
        </div>
        <div class="directCurrent-type-item between">
          <div class="directCurrent-type-right-circle"></div>
          <div class="directCurrent-type-text">交流系统</div>
        </div>
      </div>
      <div class="directCurrent-content between">
        <div class="directCurrent-content-left">
          <img src="/src/assets/customized/ydcb.png" alt="" class="directCurrent-content-left-img">
          <div class="directCurrent-content-left-text">用电成本</div>
        </div>
        <div class="directCurrent-content-right">
          <div class="demo-progress between">
            <el-progress :percentage="vsList.dcSystemCostPercent ? vsList.dcSystemCostPercent : '0'"
              class="custom-progress-one" />
            <div class="custom-progress-text">{{ $filters.moreData(vsList.dcSystemCost) }}<span>元</span></div>
          </div>
          <div class="demo-progress between">
            <el-progress :percentage="vsList.acSystemCostPercent ? vsList.acSystemCostPercent : '0'"
              class="custom-progress-two" />
            <div class="custom-progress-text">{{ $filters.moreData(vsList.acSystemCost) }}<span>元</span></div>
          </div>

        </div>

      </div>
      <div class="h-line"></div>
      <div class="directCurrent-content between">
        <div class="directCurrent-content-left">
          <img src="/src/assets/customized/xtsh.png" alt="" class="directCurrent-content-left-img">
          <div class="directCurrent-content-left-text">系统损耗</div>
        </div>
        <div class="directCurrent-content-right">
          <div class="demo-progress between">
            <el-progress :percentage="vsList.dcSystemLossPercent ? vsList.dcSystemLossPercent : '0'"
              class="custom-progress-three" />
            <!-- <div class="custom-progress-text">{{ $filters.moreData(vsList.dcSystemLoss) }}<span>kWh</span></div> -->
            <div class="custom-progress-text">
              {{ parseFloat($filters.moreData(vsList.dcSystemLoss)).toFixed(2) }}<span>kWh</span>
            </div>
          </div>
          <div class="demo-progress between">
            <el-progress :percentage="vsList.acSystemLossPercent ? vsList.acSystemLossPercent : '0'"
              class="custom-progress-two" />
            <div class="custom-progress-text">
              {{ parseFloat($filters.moreData(vsList.acSystemLoss)).toFixed(2) }}<span>kWh</span>
            </div>
            <!-- <div class="custom-progress-text">{{ $filters.moreData(vsList.acSystemLoss) }}<span>kWh</span></div> -->
          </div>

        </div>

      </div>
    </div>

  </div>
  <ComprehensiveDialog :isVisible="ComprehensiveDialogVisible" @close="ComprehensiveDialogVisible = false"
    :siteId="props.site" />

</template>

<script setup name="setup">
import ComprehensiveDialog from './comprehensiveDialog.vue'
import zlmb from '@/assets/customized/station-zlmb.png'
import power from '@/assets/customized/station-power.png'
import cn from '@/assets/customized/station-cn.png'
import zlzsj from '@/assets/customized/station-zlzsj.png'
import { getSiteAcSystem } from '@/api/customized/api'

import { ref, watch, computed } from 'vue'
const ComprehensiveDialogVisible = ref(false)
const vsList = ref({})
const InstantType = ref(1)
const percentage = ref(80)
const modeList = ref([])
const customItemContent = ref([{
  title: '直流母线',
  value1: '240',
  unit1: 'kW/',
  value2: '650',
  unit2: 'Vdc',
  image: zlmb
}, {
  title: '光伏',
  value1: '64',
  unit1: 'kWp',
  image: power
}, {
  title: '储能',
  value1: '60',
  unit1: 'kW/',
  value2: '150',
  unit2: 'kWh',
  image: cn
}, {
  title: '直流注塑机',
  value1: '3',
  unit1: '台/',
  value2: '187',
  unit2: 'kW',
  image: zlzsj
}])
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
const alarmStatusMap = computed(() => ({
  1: '综合智能策略',
  2: '峰谷套利策略',
  3: '削峰策略',
  4: '定时策略',
  5: '限电策略',
  6: '变压器扩容',
  7: '负载扩容策略',
  8: '备用电源策略'
}));
watch(() => props.site, (newVal, oldVal) => {
  console.log('site:', newVal)
  if (newVal) {
    querySiteAcSystem()
  }
})
watch(() => props.websoctList, (newVal) => {
  if (newVal && Object.keys(newVal).length > 0) {
    modeList.value = newVal?.runMode?.gatewayDeviceList;

  }
}, { immediate: true })
const querySiteAcSystem = () => {
  getSiteAcSystem({
    siteId: props.site,
    type: InstantType.value
  })
    .then((res) => {
      vsList.value = res.data
    })
}
const changeType = (type) => {
  console.log(InstantType.value, '切换')
  InstantType.value = type

  querySiteAcSystem()
}
</script>

<style scoped lang="scss">
.container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-sizing: border-box;
  padding-top: 12px;

  .custom-item {
    background: url(/src/assets/customized/left-top.png) no-repeat;
    background-size: 100% 100%;
    height: 31.5%;
    width: 100%;

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

    .single {
      padding-left: 17.4%;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-right: 6px;
      padding-top: 0.26%;

      .vs-right {
        display: flex;
        align-items: center;
        justify-content: center;

        .vs-right-img {
          width: 15px;
          height: 15px;
          padding: 7px;
          box-shadow: inset 0px 0px 15px 1px #0071cc;
          border-radius: 4px 4px 4px 4px;
          border: 1px solid #0071cc;
          margin-right: 5px;
          cursor: pointer;
        }

        .vs-right-button {
          display: flex;
          align-items: center;
          border-radius: 4px 4px 4px 4px;
          color: #ffffff;
          border: 1px solid #0071cc;

          font-family: Microsoft YaHei, Microsoft YaHei;
          font-weight: bold;

          .vs-right-button-item {
            padding: 5px 10px;
            font-size: 14px;
            line-height: normal;
            cursor: pointer;
            z-index: 10;
          }

          .active {
            background: linear-gradient(180deg,
                #54a1df 0%,
                #005599 82.42%,
                #40a5fe 100%);
          }
        }
      }
    }

    .directCurrent-type {
      display: flex;
      gap: 21px;
      justify-content: center;
      align-items: center;
      margin-top: 5%;
      margin-bottom: 1%;

      .directCurrent-type-item {
        font-family: Microsoft YaHei, Microsoft YaHei;
        font-weight: 400;
        font-size: 14px;
        color: #ffffff;
        line-height: 20px;

        .directCurrent-type-left-circle {
          width: 10px;
          height: 10px;
          background: #00e883;
          border-radius: 50%;
        }

        .directCurrent-type-right-circle {
          width: 10px;
          height: 10px;
          background: #FFBB00;
          border-radius: 50%;
        }

        .directCurrent-type-text {
          margin-left: 5px;
        }
      }
    }

    .directCurrent-content {
      height: 30.3%;
      width: 90.8%;
      margin: 0 auto;
      background: url(/src/assets/customized/directCurrent-bg.png) no-repeat;
      background-size: 100% 100%;
      margin-bottom: 3%;
      padding: 4px 9px;

      .directCurrent-content-left {
        .directCurrent-content-left-img {
          width: 90px;
          height: 55px;
        }

        .directCurrent-content-left-text {
          text-align: center;
          font-family: Microsoft YaHei, Microsoft YaHei;
          font-weight: bold;
          font-size: 14px;
          color: #FFFFFF;
        }
      }

      .directCurrent-content-right {
        width: 69%;
        gap: 6px;

        .demo-progress .el-progress--line {
          width: 54%;
        }

        .custom-progress-text {

          font-family: DIN-Medium;
          font-weight: 500;
          font-size: 20px;
          color: #00FFFF;
          padding-left: 9px;
          white-space: nowrap;


          span {
            font-family: Microsoft YaHei, Microsoft YaHei;
            font-weight: 400;
            font-size: 14px;
            color: #FFFFFF;
            padding-left: 11px;
          }
        }


      }

    }

    /* 内容布局 */
    .custom-item-content {
      width: 100%;
      display: flex;
      flex-direction: column;
      margin-top: 5%;
      gap: 1.5%;
      /* 行间距 */
      // padding: 0 4%;
      box-sizing: border-box;
      height: 84%;

      /* 每一行 */
      .row {
        display: flex;
        justify-content: space-around;
        /* 两个盒子自动平分 */
        height: 44%;
      }

      /* 中间横线 */


      /* 单个盒子 */
      .custom-item-content-item {
        width: 40%;
        height: 100%;
        background: url(/src/assets/customized/station-item-bgc.png) no-repeat;
        background-size: 100% 100%;
        position: relative;

        .custom-item-content-item-value {
          //  font-family: DIN, Arial, sans-serif;
          font-family: "DIN-Medium";
          font-weight: 500;
          font-size: 20px;
          color: #00ffff;
          text-align: center;
          font-style: normal;

          .custom-unit {
            font-family: "DIN-Medium";
            font-weight: 400;
            font-size: 14px;
            color: #ffffff;
            text-align: center;
          }
        }

        .custom-item-content-item-name {
          font-family: Microsoft YaHei, Microsoft YaHei;
          font-weight: 400;
          font-size: 14px;
          color: #ffffff;
          text-align: center;
          line-height: 19px;
          margin: 5px 0 6px 0;
        }

        .custom-item-content-item-img {
          width: 60%;
          height: 63%;
          margin: 0 auto;
          position: absolute;
          top: 79%;
          left: 50%;
          transform: translate(-50%, -50%);
        }
      }
    }

    .custom-item-mode {
      padding: 4.6%;
      box-sizing: border-box;
      width: 100%;
      height: 100%;

      .custom-item-mode-title {
        width: 100%;
        height: 52px;
        background: url(/src/assets/customized/mode-title.png) no-repeat;
        background-size: 100% 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .custom-item-mode-tips {
          line-height: 52px;
          font-family: Microsoft YaHei-Regular, Microsoft YaHei;
          font-weight: 400;
          font-size: 14px;
          color: #54a1df;
          width: 42%;
          overflow: hidden;
          white-space: nowrap;

          span {
            font-family: Microsoft YaHei-Bold, Microsoft YaHei;
            font-weight: bold;
            font-size: 14px;
            color: #ffffff;
            padding-left: 6px;
          }
        }
      }

      .custom-item-mode-content {
        width: 100%;
        margin-top: 2.5%;
        height: 68.77%;
        overflow-y: scroll;

        .custom-item-mode-content-item {
          width: 100%;
          height: 27.69%;
          background: url(/src/assets/customized/mode-border.png) no-repeat;
          background-size: 100% 100%;
          margin-top: 7px;
          display: flex;
          align-items: center;
          justify-content: space-around;

          .custom-item-mode-content-item-img {
            width: 67.97px;
            height: 66%;
          }

          .custom-item-mode-content-item-name {
            font-family: Microsoft YaHei-Regular, Microsoft YaHei;
            font-weight: 400;
            font-size: 14px;
            color: #ffffff;
            text-align: center;
          }

          .custom-item-mode-content-item-text {
            display: flex;
            align-items: center;
            color: #ffffff;
            gap: 9px;
            font-family: Microsoft YaHei, Microsoft YaHei;
            font-weight: bold;

            .circle {
              width: 11px;
              height: 11px;
              border-radius: 50%;
            }

            .circle0 {
              background: radial-gradient(circle at center, #0074d9 0%, #001f54 100%);
            }

            .circle1 {
              background: radial-gradient(circle at center,
                  #00ff3c 0%,
                  #25b17c 100%);
            }

            .circle2 {
              background: radial-gradient(circle at center,
                  #ff3c00 0%,
                  #b17c00 100%);
            }

            .circle88 {
              background: radial-gradient(circle at center, #808080 0%, #666666 100%);
            }
          }
        }
      }
    }
  }
}

.between {
  display: flex;
  align-items: center;
}

.h-line {
  width: 90.5%;
  background: url(/src/assets/customized/station-line.png) no-repeat;
  background-size: 100% 100%;
  height: 4.39%;
  margin: 0 auto;
}
</style>
<style scoped>
.custom-progress-one ::v-deep .el-progress-bar__inner {
  background: linear-gradient(90deg, rgba(0, 232, 131, 0) 0%, rgba(0, 232, 131, 1) 80%, rgba(241, 254, 254, 1) 100%);
}

.custom-progress-two ::v-deep .el-progress-bar__inner {
  background: linear-gradient(90deg, rgba(0, 232, 131, 0) 0%, rgba(255, 187, 0, 1) 80%, rgba(241, 254, 254, 1) 100%);
}

.custom-progress-three ::v-deep .el-progress-bar__inner {
  background: linear-gradient(90deg, rgba(0, 232, 131, 0) 0%, rgba(0, 255, 255, 1) 80%, rgba(241, 254, 254, 1) 100%);
}



::v-deep .el-progress-bar__outer {
  background: #012541;
  height: 5px !important;
}

::v-deep .el-progress__text {
  display: none;
}
</style>