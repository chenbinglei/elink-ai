<template>
  <!-- 电桩总览 -->
  <view class="container">
    <scroll-view class="scroll-wrapper" scroll-y>
      <view class="title-card-echarts">
        <view class="zhuang-title">
          <view class="zhaung-item">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/change-zl.png"></image>
            </view>
            <view class="zhuang-item-name">
              <view class="zhuang-name">直流桩</view>
              <view class="zhuang-item-type">
                <text class="zhuang-item-num">{{$filters.numberUnit(pileStaticData?.dcPileNum ) }}</text>台 / <text
                  class="zhuang-item-num">{{$filters.numberUnit(pileStaticData?.dcGunNum) }}</text>枪


              </view>
            </view>

          </view>
          <view class="zhaung-item">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/change-jl.png"></image>
            </view>
            <view class="zhuang-item-name">
              <view class="zhuang-name">交流桩</view>
              <viw class="zhuang-item-type">
                <text class="zhuang-item-num">{{ $filters.numberUnit(pileStaticData?.acPileNum) }}</text>台 / <text
                  class="zhuang-item-num">{{ $filters.numberUnit(pileStaticData?.acGunNum) }}</text>枪
              </viw>
            </view>

          </view>
        </view>
        <view class="zhuang_type">
          <view class="zhuang-typr-list">
            <view class="zhuang-type-list-item" v-for="item in chartData" :key="item.color">
              <view class="zhuang-type-list-name">{{ item.name }}</view>
              <view class="zhaung-type-list-numColor">
                <view class="zhuang-item-color" :style="{ background: item.color }"></view>
                <view class="zhuang-item-text">{{$filters.numberUnit(item.value)  }}</view>
              </view>
            </view>
          </view>
          <view class="zhuang-pie">
            <changePie :piechartData="chartData" style="width: 100%; height: 100%;" :sum="pileStaticData?.whole ?? '0'">
            </changePie>
          </view>
        </view>
      </view>
      <view class="time-card">
        <dateRange @handleDateTypeDate="handleDateTypeDate" @selectType="getselectType"></dateRange>
        <view class="title-card" style="padding:22rpx 0;">
          <view class="title-card-item maxwidth">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/change-cd.png"></image>
            </view>
            <view class="title-card-item-text">
              <view style="display: flex; align-items: center;">
                <view class="title-card-item-text-title">充电金额 </view>
                <uni-tooltip content="示例文字" placement="top">
                  <uni-icons type="info" size="20" color="#808080"></uni-icons>
                </uni-tooltip>

              </view>
              <view class="title-card-item-num">{{ $filters.numberUnit(lendList?.chargeMoney) }} <text
                  class="title-card-item-text">元</text></view>

            </view>
          </view>
          <view class="title-card-item">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/change-power.png"></image>


            </view>
            <view class="title-card-item-text">
              充电电量
              <view class="title-card-item-num">{{ $filters.numberUnit(lendList?.chargeQt) }} <text
                  class="title-card-item-text">KWh</text></view>
            </view>
          </view>
        </view>
        <view class="title-card" style="padding:22rpx 0;">
          <view class="title-card-item maxwidth">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/change-fd.png"></image>



            </view>
            <view class="title-card-item-text">
              <!-- 放电金额 -->
              <view style="display: flex; align-items: center;">
                <view class="title-card-item-text-title">放电金额 </view>
                <uni-tooltip content="示例文字" placement="top">
                  <uni-icons type="info" size="20" color="#808080"></uni-icons>
                </uni-tooltip>

              </view>
              <view class="title-card-item-num">{{$filters.numberUnit(lendList?.dischargeMoney) }} <text
                  class="title-card-item-text">元</text></view>
            </view>
          </view>
          <view class="title-card-item ">
            <view class="title-card-image-bgc">
              <image class="title-card-image" src="../../../assets/statisticsComponent/chnge-dispower.png"></image>


            </view>
            <view class="title-card-item-text">
              V2G电量
              <view class="title-card-item-num">{{ $filters.numberUnit(lendList?.dischargeQt ) }} <text
                  class="title-card-item-text">kWh</text></view>
            </view>
          </view>
        </view>
        <view class="site-name-text">电能趋势(直流测)</view>
        <view class="bar-echarts">
          <lineArea style="width: 100%; height: 70%;" :lendList="lendList" :selectType="selectType" :selectDeviceType="'dianzhuang'"></lineArea>
        </view>
      </view>
    </scroll-view>
  </view>

</template>

<script setup>
import lineArea from '../../../components/lineArea.vue'
import dateRange from '../../../components/date-month-year.vue'
import changePie from './changePie.vue'
import { findSitePileCurveData, findSitePileStaticData } from '../../../api/index'
import { ref, onMounted, watch } from 'vue'
const props = defineProps({
  siteId: {
    type: String,
    default: ''
  },

})
const dataTime = ref([])
const selectType = ref('')
const chartsTitle = ref(['储能购电量', '储能售电量'])
const chartData = ref({})
const pileStaticData = ref({})
const handleDateTypeDate = (val) => {
  dataTime.value = val
  getFindSitePileCurveData()
  console.log(val, 'handleDateTypeDatehandleDateTypeDatehandleDateTypeDate')
}
const getFindSitePileCurveData = async () => {
  const res = await findSitePileCurveData({
    dateType: selectType.value,
    siteId: props.siteId,
    startTime: dataTime.value.startTime,
    endTime: dataTime.value.endTime
  })
  lendList.value = res.data
}
const getselectType = (val) => {
  selectType.value = val

  console.log(val, 'getselectType')
}
const lendList = ref({

})
watch(() => props.siteId, (newVal, oldVal) => {
  console.log(newVal, '站点ID光伏');
  if (newVal) {
    findSitePileStaticData({
      siteId: newVal
    }).then(res => {
      pileStaticData.value = res.data
      chartData.value = [
        { "name": "充电", "value": pileStaticData?.value.charge ?? '0', "color": "#2AD557" },
        { "name": "放电", "value": pileStaticData?.value.discharge ?? '0', "color": "#D5992A" },
        { "name": "空闲", "value": pileStaticData?.value.idle ?? '0', "color": "#2A9CD5" },
        { "name": "占用", "value": pileStaticData?.value.employ ?? '0', "color": "#D52A2A" },
        { "name": "其他", "value": pileStaticData?.value.other ?? '0', "color": "#D52ACF" }]

    })

  }
}, { immediate: true })
onMounted(() => {
  // const arr = ['2c99698b9932659e0199f0349d8800e9']
  // let obj = {
  //   siteIds: JSON.stringify(arr),
  //   startTime: '2026-03-19 00:00:00',
  //   endTime: '2026-03-19 23:59:59',
  //   dateType: 1
  // }
  // findAllEnergyPile(obj).then(res => {
  //   lendList.value = res.data

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

  .title-card-echarts {
    width: 100%;
    height: 292rpx;
    background-color: #fff;
    padding: 0 16rpx;
    border-radius: 24rpx;

    .zhuang-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 20rpx;

      .zhaung-item {
        display: flex;
        align-items: center;
        width: 48%;

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

        .zhuang-item-name {
          color: rgba(0, 0, 0, 0.5);
          font-size: 28rpx;

          .zhuang-item-type {
            margin-top: 11rpx;
            font-size: 24rpx;
          }

          .zhuang-item-num {
            font-size: 28rpx;
            color: rgba(0, 0, 0, 1);
            font-weight: 700;
            padding-right: 10rpx;
          }
        }

      }
    }

    .zhuang_type {
      display: flex;
      align-items: center;

      .zhuang-typr-list {
        width: 70%;
        display: flex;
        align-items: center;
        justify-content: space-around;
      }
    }

    .zhuang-type-list-item {
      .zhuang-type-list-name {
        color: rgba(0, 0, 0, 0.5);
        font-size: 28rpx;
      }

      .zhaung-type-list-numColor {
        display: flex;
        align-items: center;
        margin-top: 10rpx;

        .zhuang-item-color {
          width: 14rpx;
          height: 14rpx;
          border-radius: 50%;
        }

        .zhuang-item-text {
          color: rgba(0, 0, 0, 1);
          font-weight: 700;
          font-size: 28rpx;
          margin-left: 18rpx;
        }
      }
    }

    .zhuang-pie {
      width: 30%;
      height: 155rpx;
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

    .title-card-item-text-title {}

    .price {
      color: rgba(56, 139, 255, 1);
      background: rgba(242, 245, 249, 1);
      padding: 1rpx 11rpx;
      border-radius: 18rpx;
      font-size: 20rpx;
    }

    .maxwidth {
      width: 50% !important;
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