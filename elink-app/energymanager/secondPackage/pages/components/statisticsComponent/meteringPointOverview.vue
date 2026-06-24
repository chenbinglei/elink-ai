<template>
  <!-- 关口总览 -->
  <view class="container">
    <scroll-view class="scroll-wrapper" scroll-y>
      <view class="title-card">
        <view class="title-card-item">
          <view class="title-card-image-bgc">
            <image class="title-card-image" src="../../../assets/statisticsComponent/zyggl.png"></image>

          </view>
          <view class="title-card-item-text">

            总有有功率
            <view class="title-card-item-num">{{ $filters.numberUnit(GwStaticData?.power) }} <text
                class="title-card-item-text">kW</text>
            </view>

          </view>
        </view>
        <view class="title-card-item">
          <view class="title-card-image-bgc">
            <image class="title-card-image" src="../../../assets/statisticsComponent/glys.png"></image>
          </view>
          <view class="title-card-item-text">
            功率因数
            <view class="title-card-item-num">{{ $filters.numberUnit(GwStaticData?.powerFactor) }} <text
                class="title-card-item-text"></text></view>

          </view>
        </view>
      </view>
      <view class="time-card">
        <dateRange @handleDateTypeDate="handleDateTypeDate" @selectType="getselectType"></dateRange>

        <view class="power-list">
          <view class="title-card-item">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/xwdl.png"></image>

            </view>
            <view class="title-card-item-text">

              下网电量
              <view class="title-card-item-num">{{ $filters.numberUnit(lendList?.supKwh) }} <text
                  class="title-card-item-text">kWh</text></view>

            </view>
          </view>
          <view class="title-card-item">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/swdl.png"></image>
            </view>
            <view class="title-card-item-text">
              上网电量
              <view class="title-card-item-num">{{ $filters.numberUnit(lendList?.revKwh) }}<text
                  class="title-card-item-text">kWh</text></view>
            </view>
          </view>
        </view>
        <view class="site-name-text">电能趋势</view>
        <view class="bar-echarts">
          <lineArea style="width: 100%; height: 70%;" :lendList="lendList" :selectType="selectType"
            :selectDeviceType="'guangkou'"></lineArea>
        </view>
        <view class="site-name-text">分时电量</view>
        <view class="pie-echarts">
          <view class="pie-echarts-item">
            <pieEchart style="width: 100%; height: 100%;" :title="chartsTitle[0]" :piechartData="chartData"
              :sum="lendList?.supKwh ?? 0"></pieEchart>
          </view>
          <view class="pie-echarts-item">
            <pieEchart style="width: 100%; height: 100%;" :title="chartsTitle[1]" :piechartData="chartData2"
              :sum="lendList?.revKwh ?? 0">
            </pieEchart>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>

</template>
<script setup>
import { findAllEnergyPile } from '../../../../api/homePage'
import lineArea from '../../../components/lineArea.vue'
import dateRange from '../../../components/date-month-year.vue'
import pieEchart from './pieEchart.vue'
import { findSiteGwCurveData, findSiteGwStaticData } from '../../../api/index'
import { ref, onMounted, watch } from 'vue'

const props = defineProps({
  siteId: {
    type: String,
    default: ''
  },
  currentMetering: {
    type: Object,
    default: () => ({})
  }
})
const GwStaticData = ref({})


const selectType = ref('1')
const chartsTitle = ref(['下网电量', '上网电量'])
const dataTime = ref([])
const chartData = ref([])
const chartData2 = ref([])
const handleDateTypeDate = (val) => {
  dataTime.value = val
  console.log(val, '切换时间')
  getFindSiteGwCurveData()
}

const lendList = ref({

})
const getselectType = (val) => {
  selectType.value = val
  console.log(val, '切换日期')
  // getFindSiteGwCurveData()

}
// 查询电站关口总览曲线数据
const getFindSiteGwCurveData = () => {

  let obj = {
    dateType: selectType.value,
    siteId: props.siteId,
    startTime: dataTime.value.startTime,
    endTime: dataTime.value.endTime,
    deviceId: props.currentMetering.id

  }
  console.log(obj, '查询关口总览曲线数据', props.currentMetering);
  findSiteGwCurveData(obj).then(res => {
    lendList.value = res.data
    // 下网电量
    chartData.value = [
      { "name": "尖", "value": lendList.value?.topSupKwh ?? 0, "color": "#FF0D0D" },
      { "name": "峰", "value": lendList.value?.peakSupKwh ?? 0, "color": "#FF9E0D" },
      { "name": "平", "value": lendList.value?.plainSupKwh ?? 0, "color": "#0D8EFF" },
      { "name": "谷", "value": lendList.value?.valleySupKwh ?? 0, "color": "#4EDC3C" },
      { "name": "深谷", "value": lendList.value?.deepSupKwh ?? 0, "color": "#CC00A7" }]
    // 上网电量
    chartData2.value = [
      { "name": "尖", "value": lendList.value?.topRevKwh ?? 0, "color": "#FF0D0D" },
      { "name": "峰", "value": lendList.value?.peakRevKwh ?? 0, "color": "#FF9E0D" },
      { "name": "平", "value": lendList.value?.plainRevKwh ?? 0, "color": "#0D8EFF" },
      { "name": "谷", "value": lendList.value?.valleyRevKwh ?? 0, "color": "#4EDC3C" },
      { "name": "深谷", "value": lendList.value?.deepRevKwh ?? 0, "color": "#CC00A7" }]
  })

}

// 用来防止重复请求
const isLoading = ref(false)

watch(() => (props.currentMetering), async (newVal, oldVal) => {
  console.log(newVal, oldVal, '关口表');
  if (newVal.id) {
    findSiteGwStaticData({ deviceId: newVal.id }).then(res => {
      GwStaticData.value = res.data
    })
    // getFindSiteGwCurveData()
  }
  if (newVal.id !== oldVal.id && oldVal.id) {
    getFindSiteGwCurveData()
  }

}, { immediate: true, })

onMounted(() => {
  // const arr = ['2c99698b9932659e0199f0349d8800e9']
  // let obj = {
  //   siteIds: JSON.stringify(arr),
  //   startTime: '2026-03-19 00:00:00',
  //   endTime: '2026-03-19 23:59:59',
  //   dateType: 1
  // }
  // findAllEnergyPile(obj).then(res => {


  // })
})

</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;
  overflow: hidden;

  /* 核心：内部滚动区域 */
  .scroll-wrapper {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
  }

  .title-card {
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 22rpx 41rpx;
    border-radius: 24rpx;

    .title-card-item {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title-card-image-bgc {
        width: 70rpx;
        height: 70rpx;
        background-color: #FAFAFA;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;

        .title-card-image {
          width: 36rpx;
          height: 36rpx;
        }
      }

      .title-card-item-num {
        color: rgba(0, 0, 0, 1);
        font-weight: 700;
        margin-top: 16rpx;
        font-size: 28rpx;
      }

      .title-card-item-text {
        margin-left: 20rpx;
        color: rgba(0, 0, 0, 0.5);
        font-family: "PingFang SC";
        font-weight: 400;
        font-size: 28rpx;


      }
    }
  }

  .time-card {
    margin-top: 24rpx;
    border: 2rpx solid white;
    background: white;
    border-radius: 24rpx;
    padding: 50rpx 24rpx 20rpx 24rpx;

    .power-list {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 50rpx;
      padding: 0 36rpx;

      .title-card-item {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .title-card-image-bgc {
          width: 70rpx;
          height: 70rpx;
          background-color: #FAFAFA;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;

          .title-card-image {
            width: 36rpx;
            height: 36rpx;
          }
        }

        .title-card-item-num {
          color: rgba(0, 0, 0, 1);
          font-weight: 700;
          margin-top: 16rpx;
          font-size: 28rpx;
        }

        .title-card-item-text {
          margin-left: 10rpx;
          color: rgba(0, 0, 0, 0.5);
          font-family: "PingFang SC";
          font-weight: 400;
          font-size: 28rpx;


        }
      }
    }

    .site-name-text {
      font-weight: 600;
      font-size: 28rpx;
      color: #000000;
      margin-top: 40rpx;
    }

    .bar-echarts {
      width: 100%;
      height: 480rpx;
      margin-top: 32rpx;
      background-color: #fff;
      border-radius: 30rpx;
    }

    .pie-echarts {
      width: 100%;
      height: 680rpx;
      margin-top: 32rpx;
      border-radius: 30rpx;

    }
  }

  .pie-echarts-item {
    width: 100%;
    height: 49%;
    background-color: #fff;
    border-radius: 30rpx;

  }
}
</style>