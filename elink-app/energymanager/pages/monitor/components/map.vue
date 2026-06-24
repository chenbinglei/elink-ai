<template>
  <view class="content">
    <map id="map" v-if="pointsArray.length > 0" class="map" :markers="markerList" @markertap="markertap"
      @regionchange="regionchange" :show-scale="optionsMap.showScale" :show-compass="optionsMap.showCompass"
      :latitude="latAndLongitude.centerLat" :longitude="latAndLongitude.centerLon" :enable-3D="optionsMap.enable3D"
      :show-location="optionsMap.showlocation" :enable-rotate="optionsMap.enableRotate"
      :enable-overlooking="optionsMap.enableOverlooking" :scale="optionsMap.scale" :min-scale="optionsMap.minScale"
      :max-scale="optionsMap.maxScale"></map>

    <view class="content_right_bottom" @click="clickMapToLocation">
      <text class="iconfont icon-fuwei"></text>
    </view>
    <view class="content_bottom">
      <view class="site-card">
        <sm-station-card v-if="markerStatus" :siteInfo="markerInfo"></sm-station-card>
      </view>
    </view>
  </view>
</template>
<script setup>
import { ref, computed, onMounted, defineExpose, defineEmits, getCurrentInstance, watch } from 'vue'
import { onReady } from '@dcloudio/uni-app'
import { getTenantSiteList, getSiteMapBySiteId } from '@/api/homePage.js'
import { useStore } from 'vuex';
import smStationCard from '@/components/uni-custom/sm-station-card.vue'
const store = useStore();
const instance = getCurrentInstance();
const props = defineProps({
  ismarkerStatus: {
    type: Boolean,
    default: true
  }
})
// 地图相关数据
const map = ref(null);
const markerList = ref([]);
const pointsArray = ref([]);
const mapSiteList = ref([]);
const optionsMap = ref({
  scale: 4,        // 初始默认缩放（城市级别，正常显示）
  minScale: 3,      // 最小：球体（能看到整个地球）
  maxScale: 18,     // 最大：街道（最清晰街道级别）
  enable3D: true,
  showScale: false,
  showCompass: false,
  enableRotate: true,
  showlocation: true,
  enableOverlooking: true
});
const markerStatus = ref(false);
const markerInfo = ref({});

const markerStyle = ref({
  width: 30,
  height: 45,
  joinCluster: true
});
const markerClusterStyle = ref({
  width: 0,
  height: 0,
  label: {
    width: 38,
    height: 38,
    fontSize: 16,
    color: '#ffffff',
    bgColor: '#09C095',
    borderWidth: 2,
    borderColor: '#ffffff',
    borderRadius: 19,
    textAlign: 'center',
    anchorX: -15,
    anchorY: -45
  }
});
watch(() => props.ismarkerStatus, (newVal) => {
  if (!newVal) {
    markerStatus.value = false;
    regionchange();
  }

})
// 当前激活标记点信息
const activeMarkerId = ref("");
const activeClickIndex = ref(-1);

// 地图中心坐标
const latAndLongitude = computed(() => store.state.latAndLongitude);

// 添加刷新方法
const refreshData = async () => {
  try {
    await getMapSiteList()
    return true
  } catch (error) {
    console.error('刷新地图数据失败:', error)
    throw error
  }
}
// 初始化地图
const initCreatedMap = () => {
  map.value = uni.createMapContext('map', instance); // 使用顶层获取的instance
  if (map.value) {
    createdMarkerCluster();
  } else {
    console.error('地图上下文初始化失败！');
  }
};

// 创建点聚合
const createdMarkerCluster = () => {
  map.value.initMarkerCluster({
    enableDefaultStyle: false,
    zoomOnClick: true,
    gridSize: 60,
    success: res => {
    }
  });

  // #ifdef MP-WEIXIN 
  map.value.on('markerClusterCreate', e => {
    let clusterMarkers = [];
    const clusters = e?.clusters || []; // 空值兜底
    clusters.forEach((cluster, index) => {
      const { center, clusterId, markerIds } = cluster || {};
      if (!center || !markerIds) return;
      markerClusterStyle.value.label.content = markerIds.length + '';
      let clusterObj = { ...center, clusterId, ...markerClusterStyle.value };
      clusterMarkers.push(clusterObj);
    });

    map.value.addMarkers({
      clear: false,
      markers: clusterMarkers
    });
  });
  // #endif
};
const clickMapToLocation = () => {
  moveTolocation();
}
// 视野变化时触发
const regionchange = (e) => {
  // markerStatus.value = false;
  clearActiveMarkerFun();
};

const clearActiveMarkerFun = () => {
  if (activeClickIndex.value !== -1) {
    markerList.value[activeClickIndex.value].iconPath = getStaticFilePath('select.png');
    markerStatus.value = false;
    activeClickIndex.value = -1;
    activeMarkerId.value = "";
  }
};

// 点击标记点时触发
const markertap = (e) => {
  let { markerId } = e.detail;

  if (activeMarkerId.value !== markerId) clearActiveMarkerFun();

  let findItem = mapSiteList.value.find(item => item.siteIndex === markerId);
  map.value.moveToLocation({
    latitude: parseFloat(findItem.latitude),
    longitude: parseFloat(findItem.longitude),
    success: res => {
      console.log('移动完成:', res)
      setTimeout(() => {
        queryMarkerInfo(e)
      }, 800)
    },
    fail: err => {
      console.log('移动失败:', err)
      uni.showToast({
        icon: 'none',
        title: '请点击右上角打开定位权限'
      })
    }
  })

};
const queryMarkerInfo = (e) => {
  let { markerId } = e.detail;

  if (activeMarkerId.value !== markerId) clearActiveMarkerFun();

  let findItem = mapSiteList.value.find(item => item.siteIndex === markerId);
  markerInfo.value = {
    ...findItem,
  }
  activeClickIndex.value = markerList.value.findIndex(item => item.siteIndex === markerId);
  if (activeClickIndex.value !== -1) {
    markerList.value[activeClickIndex.value].iconPath = getStaticFilePath('select-no.png');
  }

  activeMarkerId.value = markerId;
  querygetSiteMapBySiteId(findItem.id)
}
const querygetSiteMapBySiteId = async (val) => {
  let obj = {
    siteId: val
  }
  getSiteMapBySiteId(obj).then(res => {
    if (res.message) {
      markerInfo.value = {
        ...res.data,
        ...markerInfo.value
      }
      markerStatus.value = true;
    }
  })

};
// 移动到当前位置
const moveTolocation = () => {
  map.value.moveToLocation({
    success: res => {
      console.log('移动完成:', res);
    },
    fail: err => {
      console.log('移动失败:', err);
      uni.showToast({
        icon: 'none',
        title: '请点击右上角打开定位权限'
      });
    }
  });
};
// 获取站点列表 - 适配新的数据结构
const getMapSiteList = () => {
  const userId = uni.getStorageSync('USER_ID')
  let obj = {
    userId: userId
  }

  getTenantSiteList(obj).then(res => {
    let tempMarkerList = [];
    let tempMapSiteList = [];
    let tempPointsArray = [];
    let allSiteList = res.data.filter(item => item.type === 2).map(item => {
      return {
        ...item,
        location: JSON.parse(item.location)
      }
    });

    tempPointsArray.push({ latitude: latAndLongitude.value.centerLat, longitude: latAndLongitude.value.centerLon });

    if (allSiteList && allSiteList.length) {
      for (let i = 0; i < allSiteList.length; i++) {
        const site = allSiteList[i];
        if (site.location) {
          try {
            // 解析location字段
            const locationData = site.location;
            // 为新数据结构创建站点对象
            const siteObj = {
              ...site,
              siteIndex: i + 1,
              // 保持向后兼容的字段名
              siteId: site.id, // 将新数据结构的id映射到siteId
              siteName: site.name, // 将新数据结构的name映射到siteName
              // 从location解析出的数据
              latitude: parseFloat(locationData.latitude),
              longitude: parseFloat(locationData.longitude),
              address: locationData.address,
              city: locationData.city,
              county: locationData.county,
              province: locationData.province,
              stationLat: parseFloat(locationData.latitude), // 纬度
              stationLng: parseFloat(locationData.longitude) // 经度
            };

            tempMapSiteList.push(siteObj);
            tempMarkerList.push({
              ...markerStyle.value,
              id: siteObj.siteIndex,
              latitude: siteObj.latitude,
              longitude: siteObj.longitude,
              siteIndex: siteObj.siteIndex,
              iconPath: getStaticFilePath(siteObj.siteIndex === activeMarkerId.value ? 'select-no.png' : 'select.png')
            });
            tempPointsArray.push({ latitude: siteObj.latitude, longitude: siteObj.longitude });
          } catch (error) {
            console.error('解析location数据失败:', site.location, error);
            // 跳过无法解析的数据
            continue;
          }
        }
      }
    }

    mapSiteList.value = tempMapSiteList;
    pointsArray.value = tempPointsArray;
    markerList.value = tempMarkerList;
  }).catch(error => {
    console.error('获取地图站点列表失败:', error);
    uni.showToast({
      icon: 'none',
      title: '获取站点数据失败'
    });
  });
};

// 辅助方法：获取静态资源路径
const getStaticFilePath = (fileName) => {
  return `/static/image/${fileName}`;
};
const getMarkerStatus = () => {
  console.log('每次接受的值')
}
// 定义事件
const emit = defineEmits(['changEvent']);

// 生命周期钩子
onMounted(() => {
  initCreatedMap();
  getMapSiteList()

});
// 暴露方法给父组件
defineExpose({
  refreshData,
  getMapSiteList,
  moveTolocation,
  getMarkerStatus
})

</script>

<style lang="scss" scoped>
.content {
  height: 100%;
  width: 100%;
  overflow: hidden;
  position: relative;

  .map {
    height: 104%;
    width: 100%;
  }

  .content_bottom {
    width: 100%;
    z-index: 10000;
    position: absolute;
    left: 0;
    bottom: 1%;
  }

  .content_right_bottom {
    position: absolute;
    right: 13rpx;
    bottom: 24%;
    width: 80rpx;
    height: 80rpx;
    align-items: center;
    display: flex;
    justify-content: center;
    background: #fff;
    opacity: 0.8;
    border-radius: 10rpx;

    .icon-fuwei {
      font-size: 44rpx;
      /* 设置图标大小 */
    }
  }
}

.site-card {
  width: 90%;
  bottom: 3px;
  margin: 0 auto;
  background: linear-gradient(360deg, #e6f3ff 0%, #ffffff 100%);
  border-radius: 39rpx 39rpx 39rpx 39rpx;
  // border: 2rpx solid #ffffff;
}
</style>