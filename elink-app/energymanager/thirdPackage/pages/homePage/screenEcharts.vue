<template>
  <view class="screen-containers">
    <image class="content-bgc" src="/static/image/bgc.png"></image>
    <view class="screen-content">
      <view class="screen-left">
        <lineArea style="width: 100%; height: 100%;" :lendList="lendList" :selectType="selectType" :selectDeviceType="selectDeviceType"></lineArea>
      </view>
      <view class="screen-right">
        <view class="site-value-container">
          <view class="site-value-item">
            <view class="circle" style="background-color: #19CE89;"></view>
            <view class="text">{{ selectDeviceType === "dianzhuang" ? '电桩' : selectDeviceType === "chuneng" ? '储能充' : '光伏发'
        }}电量：{{ $filters.moreData(selectDeviceType === "dianzhuang" ? lendList.chargeQt : selectDeviceType ===
            "chuneng" ?
            lendList.chargeQt : lendList.generateQt) }} 度</view>
          </view>
          <view class="site-value-item">
            <view class="circle" style="background-color: #FAB758;"></view>
            <view class="text">{{ selectDeviceType === "dianzhuang" ? '电桩放' : selectDeviceType === "chuneng" ? '储能放' : '上网'
        }}电量：{{ $filters.moreData(selectDeviceType === "dianzhuang" ? lendList.dischargeQt : selectDeviceType ===
            "chuneng"
            ? lendList.dischargeQt : lendList.netQt) }} 度</view>

          </view>
          <view class="site-value-item" v-if="selectDeviceType !== 'chuneng'">
            <view class="circle" style="background-color: #FAB758;"></view>
            <view class="text">{{ selectDeviceType === "dianzhuang" ? '充电金额' : '消纳电量'
        }}：{{ $filters.moreData(selectDeviceType === "dianzhuang" ? lendList.chargeMoney : lendList.consumeQt) }}
              {{ selectDeviceType === "dianzhuang" ? '元' : '度' }}</view>
          </view>
          <view class="site-value-item" v-if="selectDeviceType !== 'chuneng'">
            <view class="circle" style="background-color: #B775E6;"></view>
            <view class="text">{{ selectDeviceType === "dianzhuang" ? '充电次数' : '等效发电时长'
        }}：{{ $filters.moreData(selectDeviceType === "dianzhuang" ? lendList.chargeCount : lendList.effectiveTime) }}
              {{ selectDeviceType === "dianzhuang" ? '次' : '小时' }}</view>

          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup name="setup">
import { ref } from 'vue'
import lineArea from '../../../pages/homePage/components/lineArea.vue'
import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app'
const lendList = ref([])
const selectType = ref(null)
const selectDeviceType = ref(null)
onLoad((options) => {
  lendList.value = JSON.parse(options.lendList)
  selectType.value = options.selectType
  selectDeviceType.value = options.selectDeviceType
})

</script>

<style scoped lang="less">
.screen-containers {
  width: 100%;
  height: 100%;

  position: relative;
}
.content-bgc {
  position: absolute;
  width: 100%;
  height: 100%;
}
.screen-content {
  display: flex;
  justify-content: space-between;
  margin: 0 auto;
  width: 90%;
  height: 90%;
}

.screen-left {
  width: 70%;
  height: 80%;
  margin-top: 10%;
}
.screen-right {
  width: 28%;
  height: 100%;
  display: flex;
  align-items: center;
  .site-value-container {
    background: rgba(56, 139, 255, 0.05);
    border-radius: 30rpx;
    padding: 20rpx 18rpx;
  }
  .site-value-item {
    display: flex;
    margin: 10rpx 0;
    align-items: center;
    color: #333333;
    font-size: 12rpx;
    .circle {
      width: 10rpx;
      height:10rpx;
      border-radius: 50%;
    }
  }
}
</style>