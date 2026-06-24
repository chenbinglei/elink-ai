<template>
  <!-- 光伏概览 -->
  <view class="container">
    <scroll-view class="scroll-wrapper" scroll-y>
      <view class="title-card">
        <view class="title-card-item">
          <view class="title-card-image-bgc">
            <image class="title-card-image" src="../../../assets/statisticsComponent/pvpower.png"></image>
          </view>
          <view class="title-card-item-text">

            实时功率
            <view class="title-card-item-num">{{ $filters.numberUnit(PvStaticData?.power)}} <text class="title-card-item-text">kW</text>
            </view>
          </view>
        </view>
        <view class="title-card-item">
          <view class="title-card-image-bgc">
            <image class="title-card-image" src="../../../assets/statisticsComponent/pvzj.png"></image>
          </view>
          <view class="title-card-item-text">
            装机容量
            <view class="title-card-item-num">{{ $filters.numberUnit(PvStaticData?.pvCap) }} <text
                class="title-card-item-text">kWp</text></view>

          </view>
        </view>
      </view>
      <view class="time-card">
        <dateRange @handleDateTypeDate="handleDateTypeDate" @selectType="getselectType"></dateRange>
        <view class="title-card" style="padding:22rpx 10rpx;">
          <view class="title-card-item">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/money.png"></image>
            </view>
            <view class="title-card-item-text">
              <view style="display: flex; align-items: center;">
                <view class="title-card-item-text-title">光伏收益 </view>
                <uni-tooltip content="示例文字" placement="top">
                  <uni-icons type="info" size="20" color="#808080"></uni-icons>
                </uni-tooltip>
                <view class="price">电价设置</view>
              </view>
              <view class="title-card-item-num">{{$filters.numberUnit(lendList?.income) }} <text
                  class="title-card-item-text">元</text></view>
            </view>
          </view>
          <view class="title-card-item">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/pvlight.png"></image>

            </view>
            <view class="title-card-item-text">
              发电量
              <view class="title-card-item-num">{{ $filters.numberUnit(lendList?.generateQt) }} <text
                  class="title-card-item-text">kWh</text></view>
            </view>
          </view>
        </view>
        <view class="site-name-text">发电量</view>
        <view class="pie-echarts">
          <pieEchart style="width: 100%; height: 100%;" :piechartData="chartData" :sum="lendList?.generateQt "></pieEchart>
        </view>
        <view class="site-name-text">电能趋势</view>
        <view class="bar-echarts">
          <view style="height: 330rpx; width: 100%;">

            <lineArea style="width: 100%; height: 100%;" :lendList="lendList" :selectType="selectType" :selectDeviceType="'guangfu'"></lineArea>
          </view>

        </view>
      </view>
    </scroll-view>
  </view>

</template>

<script setup>
import lineArea from '../../../components/lineArea.vue'
import dateRange from '../../../components/date-month-year.vue'
import pieEchart from './pieEchart.vue'
import { ref, onMounted, watch } from 'vue'
import { findSitePvCurveData, findSitePvStaticData } from '../../../api/index'
const props = defineProps({
  siteId: {
    type: String,
    default: ''
  },

})
const PvStaticData = ref({})

watch(() => props.siteId, (newVal, oldVal) => {
  console.log(newVal, '站点ID光伏');
  if (newVal) {
    findSitePvStaticData({
      siteId: newVal
    }).then(res => {
      PvStaticData.value = res.data

    })

  }
}, { immediate: true })
const dataTime = ref([])
const selectType = ref('')
const chartData = ref([

])

const lendList = ref({})


const handleDateTypeDate = (val) => {
  dataTime.value = val
  getFindSitePvCurveData()
}
const getFindSitePvCurveData = async () => {
  const res = await findSitePvCurveData({
    dateType: selectType.value,
    siteId: props.siteId,
    startTime: dataTime.value.startTime,
    endTime: dataTime.value.endTime
  })
  lendList.value = res.data
  chartData.value = [
    { "name": "消纳电量", "value": lendList.value?.consumeQt ?? '0', "color": "#FFF2D5" },
    { "name": "上网电量", "value": lendList.value?.netQt ?? '0', "color": "#FFAD0D" },
  ]
}
const getselectType = (val) => {
  selectType.value = val
}

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

    .title-card-item-text-title {}

    .price {
      color: rgba(56, 139, 255, 1);
      background: rgba(242, 245, 249, 1);
      padding: 1rpx 11rpx;
      border-radius: 18rpx;
      font-size: 20rpx;
    }
  }

  .time-card {
    margin-top: 24rpx;
    border: 2rpx solid white;
    background: white;
    border-radius: 24rpx;
    padding: 50rpx 24rpx 20rpx 24rpx;

  }

  .site-name-text {
    font-weight: 600;
    font-size: 28rpx;
    color: #000000;
    margin-top: 40rpx;
  }

  .pie-echarts {
    width: 100%;
    height: 217rpx;
    margin-top: 32rpx;
  }

  .bar-echarts {
    width: 100%;
    height: 520rpx;
    background-color: #fff;
    border-radius: 30rpx;
  }
}
</style>