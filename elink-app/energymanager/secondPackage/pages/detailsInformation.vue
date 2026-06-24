<template>
  <view class="container">
    <image class="content-bgc" src="../static/bgc.png" v-if="currentTab == 'power'"></image>

    <image class="content-bgc" src="@/static/image/bgc.png" v-else></image>
    <view class="middle-content">
      <view class="site-title">
        <u-icon name="arrow-left" size="24" color="#007AFF" @click="navigateBack"></u-icon>
        <view class="message-text">{{ siteName }}</view>
      </view>
      <view class="content-containers">
        <powerComponent :siteId="siteId" :scenarioTypes="scenarioTypes" style="width: 100%; height: 100%;"
          v-if="currentTab == 'power'"></powerComponent>
        <statisticsComponent :siteId="siteId" :scenarioTypes="scenarioTypes" style="width: 100%; height: 100%;"
          v-if="currentTab == 'statistics'"></statisticsComponent>
        <alarmComponent style="width: 100%; height: 100%;" v-if="currentTab == 'alarm'"></alarmComponent>
        <deviceComponent style="width: 100%; height: 100%;" v-if="currentTab == 'device'" :siteId="siteId">
        </deviceComponent>

      </view>
    </view>
    <view class="bottom-content">
      <view v-for="(item, index) in tabList" :key="index" class="tab-item"
        :class="{ 'active': currentTab == item.label }" @click="currentTab = item.label">
        <image :src="currentTab == item.label ? item.image_active : item.image" class="tab-image"></image>
        <view class="tab-name" :class="{ 'active': currentTab == item.label }">{{ item.name }}</view>
      </view>
    </view>
  </view>
</template>

<script setup name="setup">

import { ref } from 'vue'
const currentTab = ref('power')
import powerImg from '../assets/tabbar/power.png'
import powerActiveImg from '../assets/tabbar/power_active.png'
import statisticsImg from '../assets/tabbar/statistics.png'
import statisticsActiveImg from '../assets/tabbar/statistics_active.png'
import alarmImg from '../assets/tabbar/alarm.png'
import alarmActiveImg from '../assets/tabbar/alarm_active.png'
import deviceImg from '../assets/tabbar/device.png'
import deviceActiveImg from '../assets/tabbar/device_active.png'
import powerComponent from './components/power.vue'
import statisticsComponent from './components/statistics.vue'
import alarmComponent from './components/alarm.vue'
import deviceComponent from './components/device.vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
const siteId = ref('')
const scenarioTypes = ref('')
const siteName = ref('')
const tabList = ref([
  {
    name: '电站概览',
    label: 'power',
    image: powerImg,
    image_active: powerActiveImg
  },
  {
    name: '统计数据',
    label: 'statistics',
    image: statisticsImg,
    image_active: statisticsActiveImg
  },
  {
    name: '告警分析',
    label: 'alarm',
    image: alarmImg,
    image_active: alarmActiveImg
  },
  {
    name: '设备',
    label: 'device',
    image: deviceImg,
    image_active: deviceActiveImg
  }
])
const monitorCurrent = ref('')

onLoad((options) => {
  siteId.value = options.siteId
  scenarioTypes.value = options.scenarioTypes
  siteName.value = options.siteName
  monitorCurrent.value = options.current

  console.log(siteId.value, scenarioTypes.value, typeof (scenarioTypes.value), '传递过来的值');


})
onUnload(() => {
  uni.removeStorageSync('detailsInformationTime')
})

const navigateBack = () => {
  uni.setStorageSync('monitorCurrent', monitorCurrent.value)
  uni.switchTab({
    url: '/pages/monitor/index'
  })
}


</script>

<style scoped lang="less">
.container {
  height: 100%;
  width: 100%;
  position: relative;

  .content-bgc {
    position: absolute;
    width: 100%;
    height: 100%;
  }

  .bottom-content {
    position: fixed;
    width: 100%;
    height: 10%;
    bottom: 0;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-around;

    .tab-item {
      text-align: center;
      height: 90%;

      .tab-image {
        width: 60rpx;
        height: 60rpx;
      }

      .tab-name {
        color: rgba(211, 212, 222, 1);
        font-size: 24rpx;
      }

      .active {
        color: rgb(56, 139, 255);
      }
    }
  }
}

.middle-content {
  width: 100%;
  margin: 16% auto 10%;
  height: 88%;

  .site-title {
    display: flex;
    align-items: center;
    color: rgba(0, 0, 0, 1);
    font-family: "PingFang SC";
    font-weight: 600;
    font-size: 36rpx;
    line-height: 50rpx;
  }
}

.content-containers {
  height: 87%;
  width: 100%;
  margin-top: 41rpx;
  overflow: auto;
}
</style>