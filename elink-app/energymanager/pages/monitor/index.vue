<template>
  <view class="content">
    <image class="content-bgc" src="/static/image/bgc.png"></image>
    <view class="container">
      <view style="width: 90%;margin: 0 auto;">
        <view class="tabs">监控</view>
        <view class="tabs-list">
          <view class="tabs-item" :class="{ 'active': item.name === currentTab }" v-for="item in list1" :key="item.name"
            @click="currentTabs(item)">
            {{ item.name }}</view>
        </view>
      </view>
      <view v-if="currentTab === '地图'" class="monitor-details" style="width: 100%;">
        <Map ref="mapRef" style="width: 100%;height: 100%;" :ismarkerStatus="markerStatus"></Map>

      </view>
      <view v-else-if="currentTab === '电站'" class="monitor-details">
        <Stations ref="stationsRef" style="width: 100%;height: 100%;"></Stations>
      </view>
      <view v-else-if="currentTab === '设备'" class="monitor-details">
        <Device ref="deviceRef" style="width: 100%;height: 100%;" :sites="sites"></Device>
      </view>
    </view>

  </view>
</template>

<script setup>
import Map from '../monitor/components/map.vue'
import Stations from '../monitor/components/stations.vue'
import Device from '../monitor/components/device.vue'
import { onLoad, onShow, onPullDownRefresh, onHide } from '@dcloudio/uni-app'
import { useStore } from 'vuex';
import { ref } from 'vue';
const store = useStore();
const list1 = ref([
  {
    name: '地图'
  },
  {
    name: '电站'
  }, {
    name: '设备'
  }
])
const currentTab = ref('地图') // 默认显示 "电站"
const sites = ref([])
// 组件引用
const mapRef = ref(null)
const stationsRef = ref(null)
const deviceRef = ref(null)
const pageId = ref('monitor')
const isRefreshing = ref(false)
const currentTabs = (val) => {
  currentTab.value = val.name
  uni.removeStorageSync('monitorCurrent')
}
const markerStatus = ref(false);
const eventReceived = ref(false);

onShow(() => {
  // 获取当前页面实例
  const monitorCurrent = uni.getStorageSync('monitorCurrent')
  if (monitorCurrent) {
    currentTab.value = monitorCurrent
    refreshCurrentTab()
  } else if(!eventReceived.value) {
    currentTab.value = '地图';
    markerStatus.value = true;

  }

  // 监听事件
  uni.$on(`selectSiteEvent_${pageId.value}`, (value) => {
    eventReceived.value = true; // 标记事件已触发
    sites.value = value;
  });

});
onHide(() => {
  markerStatus.value = false;
  eventReceived.value = false
  uni.removeStorageSync('monitorCurrent')

})
// 下拉刷新事件
onPullDownRefresh(async () => {

  try {
    // 设置刷新状态
    isRefreshing.value = true

    // 根据当前标签页执行不同的刷新逻辑
    await refreshCurrentTab()

    // // 显示刷新成功提示
    // uni.showToast({
    // 	title: '刷新成功',
    // 	icon: 'success',
    // 	duration: 1500
    // })
  } catch (error) {
    uni.showToast({
      title: '刷新失败',
      icon: 'error',
      duration: 1500
    })
  } finally {
    // 停止下拉刷新动画
    isRefreshing.value = false
    uni.stopPullDownRefresh()
  }
})
// 刷新当前标签页的数据
const refreshCurrentTab = async () => {
  switch (currentTab.value) {
    case '地图':
      await refreshMap()
      break
    case '电站':
      await refreshStations()
      break
    case '设备':
      await refreshDevice()
      break
    default:
      console.log('未知标签页')
  }
}
// 刷新地图组件
const refreshMap = async () => {
  try {
    // 如果地图组件有刷新方法，则调用
    if (mapRef.value && typeof mapRef.value.refreshData === 'function') {
      await mapRef.value.refreshData()
    } else {
      // 否则调用地图组件的getMapSiteList方法
      mapRef.value.getMapSiteList()
    }
  } catch (error) {
    throw error
  }
}

// 刷新电站组件
const refreshStations = async () => {
  try {
    // 如果电站组件有刷新方法，则调用
    if (stationsRef.value && typeof stationsRef.value.refreshData === 'function') {
      await stationsRef.value.refreshData()
    } else {
      // 否则调用电站组件的getquerySiteList方法
      stationsRef.value.getquerySiteList(1, true)
    }
  } catch (error) {
    console.error('刷新电站失败:', error)
    throw error
  }
}

// 刷新设备组件
const refreshDevice = async () => {
  try {
    // 如果设备组件有刷新方法，则调用
    if (deviceRef.value && typeof deviceRef.value.refreshData === 'function') {
      await deviceRef.value.refreshData()
    } else {
      // 否则调用设备组件的getqueryDeviceList方法
      deviceRef.value.getqueryDeviceList()
    }
  } catch (error) {
    console.error('刷新设备失败:', error)
    throw error
  }
}


// 页面加载
onLoad((options) => {
 
  uni.getLocation({
    type: 'gcj02', // 适配小程序坐标系
    success: (res) => {
      const { latitude, longitude } = res;
      store.commit('TOGGLE_LATANDLON', {
        centerLat: latitude,
        centerLon: longitude
      });
    }
  })
})
</script>

<style scoped lang="scss">
.content {
  position: relative;
  width: 100%;
  height: 100%;
}

.content-bgc {
  position: absolute;
  width: 100%;
  height: 100%;
}

.container {
  width: 100%;
  margin: 0 auto;
}

.tabs {
  padding-top: 16%;
  width: 100%;
  margin: 0 auto;
  display: flex;
  font-weight: 500;
  font-size: 36rpx;
  color: #000000;
}

.tabs-list {
  display: flex;
  margin: 35rpx auto;
  font-weight: 500;
  font-size: 28rpx;
  color: #666666;
  width: 56%;
  justify-content: space-between;

  .active {
    color: #388bff;
    border-bottom: 4rpx solid #388bff;
  }
}

.monitor-details {
  height: 82%;
  width: 90%;
  margin: 0 auto;
}
</style>
