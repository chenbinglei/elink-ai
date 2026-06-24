<template>
  <view class="content">
    <image class="content-bgc" src="/static/image/bgc.png"></image>
    <view class="home-containers">
      <view class="selevt-site" @click="goSelectSite()">
        <image class="site-image" style="border-radius: 20rpx;" src="/static/image/nenglian.png"></image>
        <view>
          <view class="select-name">场站 <u-icon name="arrow-down-fill" color="#000000" size="12"></u-icon></view>
          <view class="select-title">{{ company[0] }}</view>
        </view>
      </view>
      <view style="height: 90%;overflow: auto;">
      
        <view class="site-list">
          <view class="site-item" v-for="(item, index) in siteTypeList" :key="index" @click="item.type !== 'more' ? typeChart(item) : null"
            :class="{ 'active': item.type === selectType }">
            <view class="site-image-container" :style="{ backgroundColor: item.bgcColor }">
              <image :style="{ backgroundColor: item.color }" :src="item.image" class="site-image"></image>
            </view>
            <view class="site-content" v-if="item.name !== '更多'">
              <view class="site-name">{{ $filters.numberUnit(item.siteValue) }}<text v-if="item.siteUnitValue">/{{ $filters.numberUnit(item.siteUnitValue) }}</text></view>
              <view class="site-value-container">
                <view class="site-value">{{ item.name }}</view>
                <view class="site-unit" v-if="item.unit">({{ item.unit }})</view>
              </view>
            </view>
            <view v-else class="site-content">
              <view class="site-name">{{ item.name }}</view>
            </view>
          </view>
        </view>

        <view class="task-container">
          <homePage :isRefreshing="isRefreshing" :siteNameBar="siteNameBar" :siteIds="selectedSite" :selectDeviceType="selectType"></homePage>
        </view>
        <view class="social-Contribution">
          <view class="social-Contribution-title">
            社会贡献
          </view>
          <view class="social-Contribution-content">
            <view class="social-Contribution-item bgc1">
              <view class="number">{{ socialList?.standardCoalReduction >=
                1000 ? (socialList.standardCoalReduction / 1000).toFixed(2) : socialList.standardCoalReduction }}
                {{ socialList?.standardCoalReduction >= 1000 ? '吨' : '千克' }}</view>
              <view class="text">节约标准煤</view>
              <image class="social-Contribution-icon" src="/static/image/home/mei.png" mode="aspectFit"></image>
            </view>
            <view class="social-Contribution-item bgc2">

              <view class="number">{{ socialList?.co2Reduction >= 1000 ? (socialList.co2Reduction
                / 1000).toFixed(2) : socialList.co2Reduction }} {{ socialList?.co2Reduction >= 1000 ? '吨' : '千克' }}
              </view>
              <view class="text">CO2减排量</view>
              <image class="social-Contribution-icon" src="/static/image/home/tan.png" mode="aspectFit"></image>
            </view>
            <view class="social-Contribution-item bgc3">
              <view class="number">{{ socialList.treeReduction }} 棵</view>
              <view class="text">等效植树量</view>
              <image class="social-Contribution-icon" src="/static/image/home/shu.png" mode="aspectFit"></image>

            </view>

          </view>
        </view>
      </view>
    </view>

  </view>
</template>

<script setup>

import { ref, watch } from 'vue'

import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getTenantSiteList, getDeviceCap } from '@/api/homePage.js'
import homePage from './components/homePie.vue'
import RefreshList from '@/components/refreshPage.vue';
const company = ref('')
const siteTypeList = ref([
  {
    type: 'dianzhuang',
    typeName: '电能',
    name: '电桩容量',
    unit: 'kW',
    siteValue: '',
    image: '/static/image/home/dianzhuang.png',
    color: 'rgba(70, 113, 231, 1)',
    bgcColor: 'rgba(70, 113, 231, 0.1)',
    scenarioTypes: "3",
  },
  {
    type: 'chuneng',
    typeName: '储能',
    name: '储能容量',
    unit: 'kW/kWh',
    siteValue: '',
    siteUnitValue: '',
    image: '/static/image/home/chuneng.png',
    color: 'rgba(1, 157, 103, 1)',
    bgcColor: 'rgba(1, 157, 103, 0.1)',
    scenarioTypes: "2",
  },
  {
    type: 'guangdong',
    typeName: '光伏',
    name: '光伏容量',
    unit: 'kWp',
    scenarioTypes: "1",
    siteValue: '',
    image: '/static/image/home/guangfu.png',
    color: 'rgba(244, 167, 8, 1)',
    bgcColor: 'rgba(244, 167, 8, 0.1)',
  },
  {
    type: 'more',
    name: '更多',
    image: '/static/image/home/more.png',
    color: 'rgba(89, 70, 231, 1)',
    bgcColor: 'rgba(89, 70, 231, 0.1)',
  },
])
const selectType = ref('dianzhuang')
const siteNameBar = ref('电桩')
const selectedSite = ref('');
const socialList = ref({})
const siteIds = ref([])
const pageId = ref('homePage')

// 下拉刷新标志
const isRefreshing = ref(false)
// 加载数据函数
const loadData = async (siteIds) => {
  try {
    isRefreshing.value = true
    selectType.value = 'dianzhuang'
    await getDeviceCapList(siteIds)
    // 如果有其他数据需要刷新，可以在这里添加
  } catch (error) {
    uni.showToast({
      title: '数据加载失败',
      icon: 'error'
    })
  } finally {
    isRefreshing.value = false
  }
}
// 监听下拉刷新事件
onPullDownRefresh(async () => {

  try {
    // 如果有选中的场站，刷新当前场站数据
    if (selectedSite.value && selectedSite.value.length > 0) {
      await loadData(selectedSite.value)
    } else {
      // 刷新所有数据
      const ids = uni.getStorageSync('SITE_LIST')
      selectedSite.value = ids
      await loadData(ids)
    }
  } catch (error) {
    uni.showToast({
      title: '刷新失败',
      icon: 'error',
      duration: 1500
    })
  } finally {
    // 停止下拉刷新动画
    uni.stopPullDownRefresh()
  }
})
onLoad(() => {
  const userId = uni.getStorageSync('USER_ID');
  if (!userId) {
    uni.reLaunch({ url: '/pages/login/index' });
    return;
  }
  initSiteList(userId).finally(() => {
    company.value = uni.getStorageSync('COMPANY');
    const ids = uni.getStorageSync('SITE_LIST');
    selectedSite.value = ids;
    getDeviceCapList(ids);
  });
  uni.$on(`selectSiteEvent_${pageId.value}`, (value, scenarioTypes) => {
    selectedSite.value = value;
    getScenarioTypes(scenarioTypes)
    getDeviceCapList(selectedSite.value);

  });
});
const getDeviceCapList = (val) => {
  let siteIds = val.filter(id => id !== '593');
  let obj = {
    siteIds: JSON.stringify(siteIds)
  };
  getDeviceCap(obj).then(res => {
    socialList.value = res.data
    siteTypeList.value = siteTypeList.value.map(item => {
      switch (item.type) {
        case 'dianzhuang':
          item.siteValue = res.data.pileCap;
          break;
        case 'chuneng':
          item.siteValue = res.data.pcsPower;
          item.siteUnitValue = res.data.batteryCap;
          break;
        case 'guangdong':
          item.siteValue = res.data.pvCap;
          break;
        default:
          break;
      }
      return item;
    });
  });
};
const initSiteList = async (userId) => {
  const siteList = uni.getStorageSync('SITE_LIST');
  if (!siteList || !Array.isArray(siteList) || siteList.length === 0) {
    try {
      const res = await getTenantSiteList({ userId });
      if (res.message) {
        const arr = res.data
          .filter(item => item.type === 2)
          .map(item => item.id)
          .join(',');
        const ids = arr.split(',');
        const company = res.data
          .filter(item => item.type === 1)
          .map(item => item.name);
        const allScenarioTypes = res.data
          .filter(item => item.scenarioTypes && typeof item.scenarioTypes === 'string')
          .flatMap(item => {
            // 如果是逗号分隔的字符串，拆分成数字数组
            return item.scenarioTypes.split(',').map(Number);
          });
        // 去重
        const uniqueScenarioTypes = Array.from(new Set(allScenarioTypes));
        uni.setStorageSync('COMPANY', company);
        uni.setStorageSync('SITE_LIST', ids);
        uni.setStorageSync('SCENARIO_TYPES', uniqueScenarioTypes)
        const earliestCreateTime = getEarliestCreateTime(res.data);
        uni.setStorageSync('EARLIEST_CREATE_TIME', earliestCreateTime);
      }
    } catch (err) {
      console.error('获取 SITE_LIST 失败:', err);
    }
  }
};
// watch(scenarioTypes.value, (newVal, oldData) => {
//   if (newVal.length !== oldData.length) {
//     getDeviceCapList(selectedSite.value);
//   }
// }, { deep: true })
const getScenarioTypes = (val) => {
  const list = [
    {
      type: 'dianzhuang',
      typeName: '电能',
      name: '电桩容量',
      unit: 'kW',
      siteValue: '',
      image: '/static/image/home/dianzhuang.png',
      color: 'rgba(70, 113, 231, 1)',
      bgcColor: 'rgba(70, 113, 231, 0.1)',
      scenarioTypes: "3",
    },
    {
      type: 'chuneng',
      typeName: '储能',
      name: '储能容量',
      unit: 'kW/kWh',
      siteValue: '',
      siteUnitValue: '',
      image: '/static/image/home/chuneng.png',
      color: 'rgba(1, 157, 103, 1)',
      bgcColor: 'rgba(1, 157, 103, 0.1)',
      scenarioTypes: "2",
    },
    {
      type: 'guangdong',
      typeName: '光伏',
      name: '光伏容量',
      unit: 'kWp',
      scenarioTypes: "1",
      siteValue: '',
      image: '/static/image/home/guangfu.png',
      color: 'rgba(244, 167, 8, 1)',
      bgcColor: 'rgba(244, 167, 8, 0.1)',
    },
    {
      type: 'more',
      name: '更多',
      image: '/static/image/home/more.png',
      color: 'rgba(89, 70, 231, 1)',
      bgcColor: 'rgba(89, 70, 231, 0.1)',
    },
  ]
  const allowedValues = [1, 2, 3];
  const filteredArray = val.filter(item => allowedValues.includes(item));
  siteTypeList.value = list.filter(item => {
    if (item.type === 'more') {
      return filteredArray.length !== 2;
    }


    // 获取 scenarioTypes 并转为数字
    const scenarioValue = item.scenarioTypes;
    const valueNum = Number(scenarioValue);

    // 判断是否在允许的列表中
    return filteredArray.includes(valueNum);
  });
}
onShow(() => {

});
// 点击事件
const typeChart = (type) => {
  selectType.value = type.type
  siteNameBar.value = type.typeName
}
// 跳转值选择场站页面
const goSelectSite = () => {
  uni.navigateTo({
    url: `/thirdPackage/pages/components/selectSite?selectedSite=${encodeURIComponent(JSON.stringify(selectedSite.value))}&source=${pageId.value}`
  });
}
</script>

<style lang="scss">
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

.home-containers {
  height: 100%;
  width: 90%;
  margin: 0 auto;
  overflow: hidden;
}

.selevt-site {
  padding-top: 16%;
  width: 100%;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 15rpx;

  .select-name {
    display: flex;
    align-content: center;
    font-weight: 500;
    font-size: 28rpx;
    color: #000000;
  }

  .site-image {
    width: 64rpx;
    height: 64rpx;
  }

  .select-title {
    font-weight: 400;
    font-size: 24rpx;
    color: #aeaeae;
    margin-top: 5rpx;
  }
}

.site-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-top: 50rpx;

  .active {
    background: rgba(0, 161, 255, 0.1) !important;
    border: 2rpx solid #00a1ff;
  }

  .site-item {
    width: 48%;
    box-sizing: border-box;
    padding: 23rpx 16rpx;
    display: flex;
    align-items: center;
    gap: 20rpx;
    background: linear-gradient(
      0deg,
      rgba(255, 255, 255, 1) 0%,
      rgba(230, 243, 255, 1) 100%
    );
    border-radius: 28rpx;

    .site-image-container {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 18rpx;
      border-radius: 28rpx;
    }

    .site-image {
      width: 45rpx;
      height: 45rpx;
      padding: 10rpx;
      border-radius: 50%;
    }

    .site-content {
      .site-name {
        font-size: 32rpx;
        color: #000000;
        font-weight: 500;
      }

      .site-value-container {
        display: flex;
        align-items: center;
        gap: 10rpx;
        margin-top: 10rpx;
        color: rgba(0, 0, 0, 0.5);
        font-size: 20rpx;
      }
    }
  }
}

.task-container {
  width: 100%;
  height: 64%;
  margin-top: 20rpx;
  background: linear-gradient(360deg, #ffffff 0%, #e6f3ff 100%);
  border-radius: 28rpx 28rpx 28rpx 28rpx;
}

.social-Contribution {
  width: 100%;
  margin: 20rpx 0 35rpx 0;
  background: linear-gradient(360deg, #ffffff 0%, #e6f3ff 100%);
  border-radius: 28rpx 28rpx 28rpx 28rpx;
  padding: 26rpx;

  .social-Contribution-title {
    font-weight: 600;
    font-size: 28rpx;
    color: #000000;
  }

  .social-Contribution-content {
    display: flex;
    align-items: center;
    gap: 23rpx;
    margin-top: 22rpx;

    .social-Contribution-item {
      width: 31%;
      height: 200rpx;
      border-radius: 12rpx 12rpx 12rpx 12rpx;
      padding: 12rpx 18rpx;
      position: relative;

      .number {
        font-weight: 600;
        font-size: 24rpx;
        color: #000000;
      }

      .text {
        font-weight: 400;
        font-size: 24rpx;
        color: #666666;
        margin-top: 10rpx;
      }

      .social-Contribution-icon {
        width: 90%;
        height: 60%;
        margin-top: 10rpx;
        position: absolute;
        bottom: 3rpx;
      }
    }

    .bgc1 {
      background: linear-gradient(360deg, #e1eeff 0%, #f7fafe 100%);
    }

    .bgc2 {
      background: linear-gradient(360deg, #ffecd9 0%, #fefbf9 100%);

      .social-Contribution-icon {
        position: absolute;
        bottom: 0;
      }
    }

    .bgc3 {
      background: linear-gradient(360deg, #daffee 0%, #fefbf9 100%);

      .social-Contribution-icon {
        position: absolute;
        bottom: -10rpx;
      }
    }
  }
}
</style>