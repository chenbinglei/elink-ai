<template>
  <view class="containers">
    <select-list :isHowSelect="isHowSelect" :isIconImage="isIconImage" :placeholder="placeholder" @change="change"></select-list>
    <scroll-view scroll-y @scrolltolower="loadMoreData" style="height: 90%;">
      <view class="stations-list" v-if="stationList?.length > 0">
        <view class="station-list-item" v-for="(item, index) in stationList" :key="item.siteId">
          <view class="station-list-title">
            <view class="station-list-tips">
              <view>{{ item.siteName }}</view>
              <view class="station-list-title-address">
                <u-icon name="map-fill" color="rgba(0, 0, 0, 0.50)" size="22"></u-icon>
                <view class="station-list-title-address-text">{{ item.location?.address }}</view>

              </view>
            </view>
            <view>
              <view class="station-list-title-text"><u-icon name="search" color="#fff" size="20" @click="goDetailsInformation(item)"></u-icon>
              </view>
            </view>
          </view>
          <tabs-list :dateType="item.devices" v-if="item.devices?.length > 0" :selectType="selectedTabs[index]"
            @dateTypeClick="(val) => dateTypeClick(index, val)" />
          <view class="stations-lineCharts" style="touch-action: none;">
            <view style="width: 100%; height: 100%;" v-if="item.timeList.length > 0 && item.chartData.series?.length > 0">
              <qiun-data-charts type="line" :opts="opts" :chartData="item.chartData" style="width: 100%; height: 100%;" :canvas2d="true" :ontouch="true"
                :canvasId="'chart-area-' + index" :inScrollView="true"></qiun-data-charts>
            </view>
            <view v-else style="width: 100%; height: 100%;">
              <empty></empty>
            </view>

          </view>
        </view>
      </view>
      <view style="height: 90%; width: 100%;" v-else>
        <empty></empty>
      </view>
    </scroll-view>

  </view>
</template>

<script setup>
import empty from '../../../components/uni-custom/empty.vue'
import { querySiteList } from '../../../api/monitor'
import { onUnload} from '@dcloudio/uni-app'
import selectList from '../../../components/select-list.vue';
import tabsList from '../../../components/tabs-list.vue';
import { ref, onMounted, defineExpose } from 'vue';

// const selectType = ref('')
const placeholder = ref('搜索电站')
const isIconImage = ref(false)
const isHowSelect = ref(false)
const scenarioTypeList = ref([
  {
    type: 1,
    label: "光伏"
  },
  {
    type: 2,
    label: "储能"
  },
  {
    type: 3,
    label: "电桩"
  },


])
const siteName = ref('')

const stationList = ref([

])
// 跳转到详情信息
const goDetailsInformation = (item) => {
  uni.setStorageSync('detailsInformationTime', item.createTime)
  uni.navigateTo({ url: '/secondPackage/pages/detailsInformation?siteId=' + item.id+'&scenarioTypes='+item.scenarioTypes+'&siteName='+item.siteName+'&current=电站' })

}

// 每个 tabs-list 的选中状态
const selectedTabs = ref([])
const page = ref(1);
const hasMore = ref(true);
// 修正 opts 配置 - 关键修复在这里
const opts = ref({
  padding: [10, 15, 20, 0],
  dataLabel: false,
  dataPointShape: false,
  legend: {
    show: true,
    position: 'top',
    float: 'right',
    margin: 5,
    padding: 5,
    itemGap: 10,
    fontSize: 12,
    fontColor: '#333'  // 注意这里是 fontColor，不是 color
  },
  // 修正 tooltip 配置 - 根据 uCharts 源码格式
  extra: {
    area: {
      type: "curve",
      opacity: 0.6,
      addLine: true,
      width: 2,
      gradient: true
    }
  },
  xAxis: {
    disableGrid: true,
    fontColor: '#666666',  // 添加字体颜色
    fontSize: 10,          // 设置字体大小
    formatter: (value, index) => {
      if (/^\d{2}:00$/.test(value)) {
        const hour = parseInt(value.split(':')[0]);
        if (hour % 2 === 0) {
          return hour.toString();
        } else {
          return '';
        }
      } else {
        return '';
      }
    }
  },
  yAxis: {
    show: true,
    disableGrid: false,
    gridType: "dash",
    splitNumber: 4,
    gridColor: "#CCCCCC",
    padding: 10,
    showTitle: true,
    grid: {
      show: false
    },
    data: [
      {
        position: "left",
        title: 'kW',

      }
    ]
  }
  // yAxis: {
  //   data: [
  //     {
  //       position: "left",
  //       title: 'kW',
  //       // showTitle: true,
  //       // titleFontSize: 10,
  //       // titleFontColor: '#666666',
  //       // disabled: false,     // 确保Y轴不隐藏
  //       // disableGrid: false,  // 显示网格
  //       // gridType: "dash",
  //       // dashLength: 4,
  //       // gridColor: "#CCCCCC"
  //     }
  //   ]
  // }
})

// 默认选中第一个 tab
onMounted(() => {
  getquerySiteList()
})
// 添加刷新方法
const refreshData = async () => {
  try {
    page.value = 1
    selectedTabs.value = []
    stationList.value = []
    await getquerySiteList(1, true)
    return true
  } catch (error) {
    console.error('刷新电站数据失败:', error)
    throw error
  }
}
const change = (val) => {
  siteName.value = val
  page.value = 1
  // if (val) {
  selectedTabs.value = []
  stationList.value = []
  getquerySiteList()
  // }

}

const loadMoreData = () => {
  if (!hasMore.value) return;
  page.value += 1;
  getquerySiteList(page.value);
};
const getquerySiteList = (pageNum = 1, isRefresh = false) => {
  const userId = uni.getStorageSync('USER_ID')
  const obj = {
    page: pageNum,
    size: 10,
    userId: userId,
    siteName: siteName.value,
  };
  querySiteList(obj).then(res => {
    const arr = res.data.items.map(item => {
      // 处理 scenarioTypes，防止为空时报错
      const scenarioTypesStr = item.scenarioTypes || '';
      const types = scenarioTypesStr.split(',').map(Number).filter(t => !isNaN(t));

      // 匹配 scenarioTypeList 中的标签
      const matchedLabels = scenarioTypeList.value
        .filter(type => types.includes(type.type))
        .map(type => ({ type: type.type, functionLogos: type.label }));

      // 3. 提取对应的功率数据
      const devices = matchedLabels.map(label => {
        let powerData = [];

        switch (label.type) {
          case 1: // 光伏
            powerData = item.pvPowerList || [];
            break;
          case 2: // 储能
            powerData = item.storagePowerList || [];
            break;
          case 3: // 充电桩
            powerData = item.pilePowerList || [];
            break;

          default:
            powerData = [];
        }

        return {
          ...label,
          powerData: powerData
        };
      });
      return {
        ...item,
        devices: devices,
        location: JSON.parse(item.location),
        chartData: {  // 为每个 item 添加自己的 chartData
          categories: item.timeList || [],
          series: []
        }
      };
    });

    if (pageNum === 1 || isRefresh) {
      stationList.value = arr; // 第一次加载清空旧数据
      // 初始化所有选中状态
      selectedTabs.value = arr.map(item => initItemSelectedState(item));
    } else {
      stationList.value.push(...arr); // 后续追加
      // 只为新添加的项初始化选中状态
      const newSelectedStates = arr.map(item => initItemSelectedState(item));
      selectedTabs.value = [...selectedTabs.value, ...newSelectedStates];
    }
    if (stationList.value.length >= res.data.totalSize) {
      hasMore.value = false;
    } else {
      hasMore.value = true;
    }
    // 初始化 tabs 选中状态
    // selectedTabs.value = stationList.value.map(item => {
    //   return item.devices.length > 0 ? item.devices[0].functionLogos : '';
    // });
  });
}
// 初始化单个item的选中状态
const initItemSelectedState = (item) => {
  return item.devices.length > 0 ? item.devices[0].functionLogos : '';
}

const dateTypeClick = (index, val) => {
  selectedTabs.value[index] = val
  // 更新对应 item 的 chartData
  stationList.value[index].chartData = {
    categories: stationList.value[index].timeList,
    series: [
      {
        name: val.functionLogos + '功率',
        data: val.powerData,
        color: '#388BFF', // 你可以根据类型设置不同颜色
        type: "area",
        legendShape: 'circle',
        showPoint: false // 全局隐藏点
      }
    ]
  };
  const powerData = val.powerData;
  const filteredData = powerData.filter(val => val !== null);
  // 查看当前数据是否有负数
  const hasNegative = filteredData.some(v => v < 0);
  // 如果有负数
  if (hasNegative) {
    // 转换为绝对值
    const dataAObs = powerData.map(Math.abs)
    // 获取最大值
    const maxLine = Math.max(...dataAObs)
    // 设置 min 和 max
    // const yAxisMin = hasNegative ? -maxLine : 0
    // const yAxisMax = maxLine
    // opts.value.yAxis.data[0].min = yAxisMin;
    // opts.value.yAxis.data[0].max = yAxisMax;
    // 设置当前图表的 Y 轴范围
    stationList.value[index].chartData.yAxis = {
      min: hasNegative ? -maxLine : 0,
      max: maxLine
    };
  }

}
// ---l-------ineCharts----------------

defineExpose({
  refreshData,
  getquerySiteList,
  loadMoreData
})
</script>

<style lang="scss" scoped>
.containers {
  width: 100%;
  height: 100%;
}

.stations-list {
  margin: 5rpx auto;
  box-sizing: border-box;

  .station-list-item {
    margin-top: 20rpx;
    height: 530rpx;
    background: linear-gradient(360deg, #e6f3ff 0%, #ffffff 100%);
    box-shadow: 0rpx 0rpx 2rpx 0rpx rgba(0, 0, 0, 0.05);
    border-radius: 30rpx 30rpx 30rpx 30rpx;
    border: 2rpx solid #ffffff;
    padding: 23rpx 28rpx;

    .station-list-title {
      display: flex;
      align-content: center;
      justify-content: space-between;

      .station-list-tips {
        font-weight: 600;
        font-size: 32rpx;
        color: #2c353e;
      }

      .station-list-title-address {
        display: flex;
        align-items: center;
        color: rgba(0, 0, 0, 0.5);
        font-size: 24rpx;

        .station-list-title-address-text {
          padding: 18rpx;
        }
      }

      .station-list-title-text {
        padding: 10rpx;
        background: rgba(0, 0, 0, 0.1);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    // .stations-lineCharts {
    //   width: 100%;
    //   height: 70%;
    //   /* 核心修复：必须加以下样式 */
    //   overflow: visible !important; // 允许 tooltip 溢出
    //   position: relative; // 作为 tooltip 定位基准
    //   z-index: 100; // 提升层级，避免被遮挡
    //   // touch-action: none; // 禁止浏览器默认触摸行为
    //   touch-action: auto;
    // }
    .stations-lineCharts {
      width: 100%;
      height: 70%;
      overflow: visible !important;
      position: relative;
      touch-action: pan-y !important; /* 允许垂直滚动 */
      -webkit-overflow-scrolling: touch;

      /* 让 canvas 元素事件穿透 */
      canvas {
        pointer-events: none !important;
        touch-action: pan-y !important;
      }
    }
  }
}
</style>