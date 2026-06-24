<template>
  <!-- 储能总览 -->
  <view class="container">
    <scroll-view class="scroll-wrapper" scroll-y>
      <view class="title-card-echarts">
        <view style="width: 25%;height: 98%;">
          <energyPie style="width: 30%;height: 100%;" :piechartData="percentageData" :title="'soc'"></energyPie>
        </view>
        <view class="echarts-title-card">
          <view class="title-card-item">
            <view class="title-card-item-text">
              有功功率
              <view class="title-card-item-num">{{$filters.numberUnit(SeStaticData?.power) }} <text
                  class="title-card-item-text">kW</text></view>

            </view>
          </view>
          <view class="title-card-item ">

            <view class="title-card-item-text">
              装机容量
              <view class="title-card-item-num">{{ $filters.numberUnit(SeStaticData?.pcsPower) }} <text
                  class="title-card-item-text">kW /
                </text>{{ $filters.numberUnit(SeStaticData?.batteryCap) }}<text class="title-card-item-text">kWh</text></view>

            </view>
          </view>
        </view>

      </view>
      <view class="time-card">
        <dateRange @handleDateTypeDate="handleDateTypeDate" @selectType="getselectType"></dateRange>

        <view class="title-card" style="padding:22rpx 10rpx;">
          <view class="title-card-item maxwidth">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/energy_money.png"></image>
            </view>
            <view class="title-card-item-text">
              <view style="display: flex; align-items: center;">
                <view class="title-card-item-text-title">储能收益 </view>
                <uni-tooltip content="示例文字" placement="top">
                  <uni-icons type="info" size="20" color="#808080"></uni-icons>
                </uni-tooltip>
                <view class="price">电价设置</view>
              </view>
              <view class="title-card-item-num">{{$filters.numberUnit(lendList?.income ) }} <text
                  class="title-card-item-text">元</text></view>

            </view>
          </view>
          <view class="title-card-item">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/energy_for.png"></image>


            </view>
            <view class="title-card-item-text">
              循环次数
              <view class="title-card-item-num">{{ $filters.numberUnit(lendList?.cycleNum) }} <text
                  class="title-card-item-text">次</text></view>
            </view>
          </view>
        </view>
        <view class="title-card" style="padding:22rpx 10rpx;">
          <view class="title-card-item maxwidth">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/energy_power.png"></image>


            </view>
            <view class="title-card-item-text">
              充电量
              <view class="title-card-item-num">{{$filters.numberUnit(lendList?.chargeQt) }} <text
                  class="title-card-item-text">kWh</text></view>
            </view>
          </view>
          <view class="title-card-item ">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/energy_dispower.png"></image>


            </view>
            <view class="title-card-item-text">
              放电量
              <view class="title-card-item-num">{{ $filters.numberUnit(lendList?.dischargeQt)}} <text
                  class="title-card-item-text">kWh</text></view>
            </view>
          </view>
        </view>
        <view class="site-name-text">电能趋势(直流测)</view>
        <view class="bar-echarts">
          <lineArea style="width: 100%; height: 70%;" :lendList="lendList"  :selectType="selectType" :selectDeviceType="'chuneng'"></lineArea>
        </view>
        <view class="site-name-text">分时电量（并网点）</view>
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
import lineArea from '../../../components/lineArea.vue'
import dateRange from '../../../components/date-month-year.vue'
import pieEchart from './pieEchart.vue'
import energyPie from './energyPie.vue'
import { ref, onMounted, watch } from 'vue'
import { findSiteSeCurveData, findSiteSeStaticData } from '../../../api/index'
const selectType = ref('')
const chartsTitle = ref(['储能购电量', '储能售电量'])
const SeStaticData = ref({})
const lendList = ref({})
const dataTime = ref([])
const chartData2 = ref([])
const chartData = ref([
])
const percentageData = ref([])

const props = defineProps({
  siteId: {
    type: String,
    default: ''
  },
})
watch(() => props.siteId, (newVal, oldVal) => {
  console.log(newVal, '站点ID光伏');
  if (newVal) {
    findSiteSeStaticData({
      siteId: newVal
    }).then(res => {
      SeStaticData.value = res.data
      const soc = Number(res.data.soc) || 0;
      percentageData.value = [
        { name: "soc", value: soc, color: "#09A9FF" },
        { name: "其他", value: Math.max(0, 100 - soc), color: "#F6F5FC" }
      ]
    })

  }
}, { immediate: true })

const handleDateTypeDate = (val) => {
  dataTime.value = val
  getFindSiteSeCurveData()
  console.log(val, 'handleDateTypeDatehandleDateTypeDatehandleDateTypeDate')
}
const getFindSiteSeCurveData = async () => {
  const res = await findSiteSeCurveData({
    dateType: selectType.value,
    siteId: props.siteId,
    startTime: dataTime.value.startTime,
    endTime: dataTime.value.endTime
  })
  lendList.value = res.data
  // 储能购电量
  chartData.value = [
    { "name": "尖", "value": lendList.value?.topSupKwh ?? 0, "color": "#FF0D0D" },
    { "name": "峰", "value": lendList.value?.peakSupKwh ?? 0, "color": "#FF9E0D" },
    { "name": "平", "value": lendList.value?.plainSupKwh ?? 0, "color": "#0D8EFF" },
    { "name": "谷", "value": lendList.value?.valleySupKwh ?? 0, "color": "#4EDC3C" },
    { "name": "深谷", "value": lendList.value?.deepSupKwh ?? 0, "color": "#CC00A7" }]
  // 售电量
  chartData2.value = [
    { "name": "尖", "value": lendList.value?.topRevKwh ?? 0, "color": "#FF0D0D" },
    { "name": "峰", "value": lendList.value?.peakRevKwh ?? 0, "color": "#FF9E0D" },
    { "name": "平", "value": lendList.value?.plainRevKwh ?? 0, "color": "#0D8EFF" },
    { "name": "谷", "value": lendList.value?.valleyRevKwh ?? 0, "color": "#4EDC3C" },
    { "name": "深谷", "value": lendList.value?.deepRevKwh ?? 0, "color": "#CC00A7" }]
}
const getselectType = (val) => {
  selectType.value = val

  console.log(val, 'getselectType')
}
onMounted(() => {

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

  .title-card-echarts {
    width: 100%;
    height: 185rpx;
    background-color: #fff;
    display: flex;
    align-items: center;
    padding: 0 16rpx;
    border-radius: 24rpx;

    .echarts-title-card {
      display: flex;
      width: 74%;
      justify-content: space-between;
      padding: 0 20rpx;
    }
  }

  .echarts-title-card {
    display: flex;
    align-items: center;

    .title-card-item {
      display: flex;
      align-items: center;

      .title-card-item-num {
        color: rgba(0, 0, 0, 1);
        font-weight: 700;
        margin-top: 16rpx;
        font-size: 28rpx;
      }

      .title-card-item-text {
        // margin-left: 20rpx;
        color: rgba(0, 0, 0, 0.5);
        font-family: "PingFang SC";
        font-weight: 400;
        font-size: 28rpx;


      }
    }
  }

  .title-card {
    background-color: #fff;
    display: flex;
    align-items: center;
    padding: 22rpx 37rpx;
    border-radius: 24rpx;
    flex-wrap: wrap;

    .title-card-item-text-title {}

    .price {
      color: rgba(56, 139, 255, 1);
      background: rgba(242, 245, 249, 1);
      padding: 1rpx 11rpx;
      border-radius: 18rpx;
      font-size: 20rpx;
    }

    .maxwidth {
      width: 57% !important;
    }

    .title-card-item-text {}

    .title-card-item {
      display: flex;
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