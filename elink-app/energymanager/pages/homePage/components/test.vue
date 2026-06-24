<template>
  <view class="line-area">
    <!-- 图表容器 -->
    <view v-if="showChart">
      <qiun-data-charts v-if="selectType == '1'" type="mix" :chartData="chartData" :opts="opts" style="width: 100%; height: 300rpx;" :canvas2d="true"
        :ontouch="true" canvas-id="column" inScrollView="true" />
      <qiun-data-charts v-else type="mix" :chartData="chartDatas" :opts="barOpts" style="width: 100%; height: 300rpx;" :canvas2d="true" :ontouch="true"
        canvas-id="column" inScrollView="true" />
    </view>

    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 空状态 -->
    <empty v-if="!isLoading && (!props.lendList?.timeList || props.lendList.timeList.length === 0)" style="width: 100%; height: 300rpx;" />
  </view>
</template>

<script setup>
import empty from '../../../components/uni-custom/empty.vue'
import { ref, watch, onUnmounted } from 'vue';

const props = defineProps({
  lendList: {
    type: Object,
    default: () => ({})
  },
  selectType: {
    type: String,
    default: '1'
  },
  selectDeviceType: {
    type: String,
    default: 'dianzhuang'
  }
});

// 响应式数据
const chartData = ref(null)
const chartDatas = ref(null)
const showChart = ref(false)
const isLoading = ref(false)
// 图表配置
const barOpts = ref({
  padding: [10, 15, 20, 15], // 底部留空给滚动条
  dataLabel: false,
  dataPointShape: false,
  enableScroll: true, // 开启图表滚动总开关
  tooltip: {
    show: true,
    showArrow: true,
    bgColor: 'rgba(0,0,0,0.7)',
    padding: [5, 10],
    fontSize: 12,
    fontColor: '#fff',
    zIndex: 9999,
    preventDefault: true
  },
  legend: {
    show: true,
    position: 'top',
    float: 'right',
    margin: 5,
    padding: 5,
    itemGap: 10,
    fontSize: 12,
    fontColor: '#333'
  },
  // X轴滚动条核心配置 + 你的formatter逻辑
  xAxis: {
    disableGrid: true,
    // 1. 开启X轴滚动（必配）
    scrollShow: true,        // 显示滚动条
    scrollAlign: 'left',     // 滚动起始位置
    itemCount: 5,            // 一屏显示6个数据项（可根据需求调整）
    scrollColor: '#CCCCCC',  // 滚动条轨道颜色
    scrollBgColor: '#388BFF',// 滚动条滑块颜色
    scrollSize: 8,           // 滚动条高度（小程序端）
    // 2. 你的原有formatter逻辑
    formatter: (value, index) => {
      if (props.selectType === '1') {
        if (/^\d{2}:00$/.test(value)) {
          const hour = parseInt(value.split(':')[0]);
          return hour % 2 === 0 ? hour.toString() : '';
        } else {
          return '';
        }
      } else if (props.selectType === '2') {
        return value % 2 === 1 ? value.toString() : "";
      } else {
        return value;
      }
    },
    fontColor: '#666666',
    fontSize: 10
  },
  // Y轴配置（保留你的原有配置）
  yAxis: {
    show: true,
    disableGrid: false,
    gridType: "dash",
    dashLength: 4,
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
        titleFontSize: 10,
        titleFontColor: '#666666',
        min: 0 // 建议添加：强制Y轴从0开始，柱状图更直观
      }
    ]
  },
  // 柱状图配置（保留你的原有配置 + 滚动兼容）
  extra: {
    line: {
      width: 1,
    },
    column: {
      barBorderCircle: true, // 柱子圆角
      type: "group",         // 分组柱状图
      width: 10,             // 柱子宽度
      activeBgColor: "#000000",
      activeBgOpacity: 0.08,
      // 新增：分组柱状图滚动适配
      groupPadding: 10,      // 组内间距
      barPadding: 5          // 柱子间距
    },
    // 滚动条样式全局配置
    scroll: {
      size: 8,               // 滚动条高度（H5端）
      color: '#388BFF',      // 滑块颜色
      bgColor: '#f5f5f5'     // 轨道颜色
    }
  },
})


// 图表配置
const opts = ref({
  padding: [10, 15, 20, 0],
  enableScroll: false,
  dataLabel: false,
  animate: false, // 保持动画
  enableZoom: false,
  enablePan: false,
  legend: {
    show: true,
    position: 'top',
    float: 'right',
    margin: 2,
    padding: 5,
    itemGap: 10,
    fontSize: 12,
    color: '#333'
  },
  xAxis: {
    formatter: (value, index) => {
      if (props.selectType === '1') {
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
      } else if (props.selectType === '2') {
        if (value % 2 === 1) {
          return value.toString();
        }
        return "";
      } else {
        return value;
      }
    }
  },
  extra: {
    line: {
      width: 1,
    },
    column: {
      // type: "group",
      barBorderCircle: true
    }
  },
  yAxis: {
    show: true,
    disableGrid: false,
    gridType: "dash",
    dashLength: 4,
    gridColor: "#CCCCCC",
    padding: 10,
    showTitle: true,
    grid: {
      show: false
    },
    data: [
      {
        position: "left",
        title: 'kW'
      }
    ]
  }
})

// 数据降采样（解决性能问题但保留数据）
const downSampleData = (dataList, targetCount = 200) => {
  if (!Array.isArray(dataList) || dataList.length <= targetCount) {
    return dataList ? [...dataList] : [];
  }

  const step = Math.ceil(dataList.length / targetCount);
  const result = [];

  for (let i = 0; i < dataList.length; i += step) {
    const value = dataList[i];

    // 如果是数字且小数位数 > 4，则保留四位小数
    if (typeof value === 'number') {
      const str = value.toString();
      const decimalIndex = str.indexOf('.');
      if (decimalIndex !== -1 && str.length - decimalIndex - 1 > 4) {
        result.push(Number(value.toFixed(4)));
      } else {
        result.push(value);
      }
    } else {
      result.push(value);
    }
  }

  // 确保最后一个元素是原始数据的最后一个
  if (result.length && result[result.length - 1] !== dataList[dataList.length - 1]) {
    result[result.length - 1] = dataList[dataList.length - 1];
  }

  return result;
};

// 日模式图表
const dateLineCharts = () => {
  const { timeList = [], curve1List = [], curve2List = [] } = props.lendList;
  if (!timeList.length || !curve1List.length || !curve2List.length) {
    chartData.value = null;
    showChart.value = false;
    return;
  }

  const sampledTimeList = downSampleData(timeList, 200);
  const sampledCurve1List = downSampleData(curve1List, 200);
  const sampledCurve2List = downSampleData(curve2List, 200);

  const res = {
    categories: sampledTimeList,
    series: [
      {
        name: "当日",
        type: "area",
        style: "curve",
        color: "#25D500",
        smooth: true,
        showPoint: false,
        data: sampledCurve2List,
        legendShape: 'rect'
      },
      {
        name: '前一日',
        type: "line",
        smooth: true,
        color: "#00BFFF",
        data: sampledCurve1List,
        legendShape: 'rect'
      },
    ]
  };

  chartData.value = res;
  showChart.value = true;
  isLoading.value = false;

};

// 周/月模式图表
const weekLineCharts = () => {
  const { timeList = [], curve1List = [], curve2List = [] } = props.lendList;
  if (!timeList.length || !curve1List.length || !curve2List.length) {
    chartData.value = null;
    showChart.value = false;
    return;
  }

  const sampledTimeList = downSampleData(timeList, 150);
  const sampledCurve1List = downSampleData(curve1List, 150);
  const sampledCurve2List = downSampleData(curve2List, 150);

  const seriesName1 = props.selectDeviceType == 'dianzhuang'
    ? '电桩充电电量'
    : props.selectDeviceType == 'chuneng'
      ? '储能充电电量'
      : '光伏实际发电量';

  const seriesName2 = props.selectDeviceType == 'dianzhuang'
    ? '电桩放电电量'
    : props.selectDeviceType == 'chuneng'
      ? '储能放电电量'
      : '光伏理论发电量';

  const res = {
    categories: sampledTimeList,
    series: [
      {
        name: seriesName1,
        type: "column",
        style: "curve",
        color: "#19CE89",
        smooth: true,
        showPoint: false,
        data: sampledCurve1List,
        legendShape: 'circle'
      },
      {
        name: seriesName2,
        type: "column",
        smooth: true,
        color: "#FAB758",
        data: sampledCurve2List,
        showPoint: false,
        legendShape: 'circle'
      },
    ]
  };

  chartDatas.value = res;
  showChart.value = true;
};

// 查询图表数据
const queryDataCharts = async () => {
  chartData.value = null;
  showChart.value = false;
  if (isLoading.value) return;

  try {
    isLoading.value = true;

    dateLineCharts();
  } catch (error) {
  } finally {
    isLoading.value = false;
  }
};
// 查询图表数据
const queryDataChart = async () => {
  chartDatas.value = null;
  showChart.value = false;
  if (isLoading.value) return;

  try {
    isLoading.value = true;

    weekLineCharts();

  } catch (error) {
  } finally {
    isLoading.value = false;
  }
};

// 监听数据变化
watch(
  () => [props.lendList?.timeList?.length],
  () => {
    chartDatas.value = null;
    chartData.value = null;

    if (props.selectType === '1') {
      queryDataCharts();
    } else {
      queryDataChart()
    }

  },
  { immediate: true }
);

// 更新 Y 轴标题
watch(() => props.selectType, (newVal) => {
  if (newVal === '1') {
    opts.value.yAxis.data[0].title = 'kW';
  } else {
    barOpts.value.yAxis.data[0].title = 'kWh';
  }
});

// 组件卸载清理
onUnmounted(() => {
  // 可选：清理定时器等资源
});
</script>

<style lang="scss" scoped>
.line-area {
  height: 100%;
  width: 100%;

  .loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    z-index: 10;

    .loading-spinner {
      width: 40rpx;
      height: 40rpx;
      border: 3rpx solid #f3f3f3;
      border-top: 3rpx solid #388bff;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin-bottom: 16rpx;
    }

    .loading-text {
      font-size: 24rpx;
      color: #666;
    }

    @keyframes spin {
      0% {
        transform: rotate(0deg);
      }

      100% {
        transform: rotate(360deg);
      }
    }
  }
}
</style>