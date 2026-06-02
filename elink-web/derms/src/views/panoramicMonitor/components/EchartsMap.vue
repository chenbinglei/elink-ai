<template>
  <div class="map-container">
    <div class="main-map" id="main-map"></div>
    <div class="province-thumbnail">
      <div class="arrow-top">
        <el-icon>
          <CaretTop :class="{
            'caret-top-disabled': startIndex === 0,
            'caret-top': startIndex !== 0
          }" @click="scrollThumbnails(-1)" />
        </el-icon>
      </div>
      <div class="province-thumbnail-item-container">
        <div class="scroll-wrapper" :style="scrollStyle">
          <div class="province-thumbnail-item" v-for="item in visibleThumbnails" :key="item.adcode" @click="handleProvinceClick(item)"
            :class="{ 'active': currentProvince?.adcode === item.adcode }">
            <div class="province-thumbnail-item-adcode" :id="`province-thumbnail-item-${item.adcode}`"></div>
            <div class="province-thumbnail-item-name">{{ item.province }}</div>
          </div>
        </div>
      </div>

      <div class="arrow-bottom">
        <el-icon>
          <CaretTop :class="{
            'caret-top-disabled': endIndex >= provinceList.length,
            'caret-top': endIndex < provinceList.length
          }" @click="scrollThumbnails(1)" />
        </el-icon>
      </div>
    </div>
  </div>
</template>

<script setup>
import * as echarts from "echarts";

import locationIcon from '@/assets/image/location.png';
import icon1 from '@/assets/image/panoramic-monitor/number-stations-icon-map1.png';
import icon2 from '@/assets/image/panoramic-monitor/number-stations-icon-map2.png';
import icon3 from '@/assets/image/panoramic-monitor/number-stations-icon-map3.png';
import icon4 from '@/assets/image/panoramic-monitor/number-stations-icon-map4.png';
import icon5 from '@/assets/image/panoramic-monitor/number-stations-icon-map5.png';
import axios from "axios";

import { ref, onMounted, onBeforeUnmount, computed, nextTick, defineEmits, watch } from 'vue';
import { cityCode } from './cityCode.js';
import { CaretTop } from '@element-plus/icons-vue';

const props = defineProps({
  scatteredPoints: {
    type: Array,
    default: () => []
  }
})

// 响应式数据
const myChart = ref(null);
const thumbnailCharts = ref({}); // 存储所有缩略图的echarts实例
const mapCode = ref([]);
const distributionOptions = ref(null);
const geographicalCoordinates = ref([]);
const currentProvince = ref(null); // 当前选中的省份
const startIndex = ref(0); // 当前显示的起始索引
const VISIBLE_COUNT = 3; // 可见的缩略图数量
const provinceList = ref([]); // 省份列表数据
const emit = defineEmits(['main-map', 'main-cityMap', 'main-countryCity', 'current-point-change'])

// 自动轮播相关数据
const currentIndex = ref(0); // 当前选中的点位索引
const intervalId = ref(null); // 定时器 ID
const currentLevel = ref('province'); // 当前层级：province/city/county
const currentLevelName = ref(''); // 当前层级名称
const currentLevelPoints = ref([]); // 当前层级的点位列表
const resumeTimerId = ref(null); // 恢复轮播的定时器

// 计算属性
const endIndex = computed(() => startIndex.value + VISIBLE_COUNT);

const visibleThumbnails = computed(() => {
  if (provinceList.value.length > 0) {
    return provinceList.value.slice(startIndex.value, startIndex.value + VISIBLE_COUNT);
  }
  return [];
});

const scrollStyle = computed(() => {
  return {
    transition: 'transform 0.3s ease'
  };
});

// 获取当前选中的点位信息
const getCurrentPoint = () => {
  const points = currentLevelPoints.value;
  if (!points || points.length === 0 || currentIndex.value >= points.length) {
    return null;
  }
  return points[currentIndex.value];
}

// 发送当前点位给父组件
const emitCurrentPoint = () => {
  const currentPoint = getCurrentPoint();
  if (currentPoint) {
    emit('current-point-change', {
      point: currentPoint,
      index: currentIndex.value,
      level: currentLevel.value,
      levelName: currentLevelName.value,
      totalPoints: currentLevelPoints.value.length
    });
  }
}

// 清除恢复定时器
const clearResumeTimer = () => {
  if (resumeTimerId.value) {
    clearTimeout(resumeTimerId.value);
    resumeTimerId.value = null;
  }
};

// 延迟恢复轮播
const resumeAutoPlayAfterDelay = (delay = 5000) => {
  // 先清除之前的恢复定时器
  clearResumeTimer();
  
  // 设置新的恢复定时器
  resumeTimerId.value = setTimeout(() => {
    startAutoPlay();
    resumeTimerId.value = null;
  }, delay);
};

// 自动轮播相关方法
const startAutoPlay = () => {
  // 先清除恢复定时器
  clearResumeTimer();
  
  // 先清除之前的定时器
  if (intervalId.value) {
    clearInterval(intervalId.value);
  }
  
  // 获取当前层级的点位
  const points = currentLevelPoints.value;
  if (!points || points.length === 0) return;
  
  intervalId.value = setInterval(() => {
    // 计算下一个索引
    const nextIndex = (currentIndex.value + 1) % points.length;
    currentIndex.value = nextIndex;
    
    // 更新地图高亮
    updateMapHighlight();
    
    // 发送当前点位给父组件
    emitCurrentPoint();
  }, 4000);
}

const stopAutoPlay = () => {
  if (intervalId.value) {
    clearInterval(intervalId.value);
    intervalId.value = null;
  }
  // 同时清除恢复定时器
  clearResumeTimer();
}

// 更新当前层级的点位列表
const updateCurrentLevelPoints = (name, level) => {
  currentLevel.value = level;
  currentLevelName.value = name;
  
  // 根据层级筛选点位
  if (level === 'province') {
    currentLevelPoints.value = props.scatteredPoints.filter(x => x.province === name);
  } else if (level === 'city') {
    currentLevelPoints.value = props.scatteredPoints.filter(x => x.city === name);
  } else if (level === 'county') {
    currentLevelPoints.value = props.scatteredPoints.filter(x => x.county === name);
  }
  
  // 重置当前索引
  currentIndex.value = 0;
  
  // 发送初始点位给父组件
  setTimeout(() => {
    emitCurrentPoint();
  }, 100);
  
  // 重新开始轮播
  stopAutoPlay();
  startAutoPlay();
}

// 更新地图高亮显示
const updateMapHighlight = () => {
  if (!myChart.value || !distributionOptions.value) return;
  
  const points = currentLevelPoints.value;
  if (!points || points.length === 0) return;
  
  const currentPoint = points[currentIndex.value];
  if (!currentPoint) return;
  
  // 重新生成 series 数据，标记当前选中的点位
  const seriesData = points.map((point, index) => ({
    name: point.name,
    value: [point.value[0], point.value[1], point, index === currentIndex.value],
    siteType: point.siteType,
    city: point.city,
    isCurrent: index === currentIndex.value
  }));
  
  // 更新图表的 series 数据
  myChart.value.setOption({
    series: [{
      data: seriesData
    }]
  });
}

// 根据点击更新选中的点位
const updateSelectedPointByClick = (clickedName) => {
  const points = currentLevelPoints.value;
  if (!points || points.length === 0) return;
  
  // 查找点击的点位
  const clickedIndex = points.findIndex(point => 
    point.name === clickedName || 
    point.city === clickedName || 
    point.county === clickedName
  );
  
  if (clickedIndex !== -1) {
    // 更新当前索引
    currentIndex.value = clickedIndex;
    
    // 更新地图高亮
    updateMapHighlight();
    
    // 发送当前点位给父组件
    emitCurrentPoint();
  }
}

// 初始化主地图
const initMainMap = (val) => {
  const obj = val || (provinceList.value.length > 0 ? provinceList.value[0] : null);
  if (!obj) return;

  emit('main-map', obj);
  currentProvince.value = obj;

  // 更新当前层级的点位为省级点位
  updateCurrentLevelPoints(obj.province, 'province');

  let path = `https://geo.datav.aliyun.com/areas_v3/bound/${obj.adcode}_full.json`;

  axios.get(path).then((res) => {
    mapCode.value = res.data.features.map(x => ({
      adcode: x.properties.adcode,
      name: x.properties.name,
    }));

    echarts.registerMap(obj.province || obj.name, res.data);

    changeOptions(obj.province || obj.name);

    if (!myChart.value) {
      myChart.value = echarts.init(document.querySelector(`#main-map`), null, {
        renderer: 'canvas'
      });
    }

    nextTick(() => {
      myChart.value.setOption(distributionOptions.value, true);
      setTimeout(() => {
        myChart.value.resize();
      }, 0);
    });

    myChart.value.off("click");
    myChart.value.on("click", handleMapClick);
  }).catch(err => {
    console.error('加载主地图失败:', err);
  });
}

// 初始化所有可见缩略图
const initVisibleThumbnails = async () => {
  await nextTick();
  const thumbnails = visibleThumbnails.value;
  await Promise.all(thumbnails.map(item => initThumbnail(item)));
}

// 初始化单个缩略图
const initThumbnail = (item) => {
  return new Promise((resolve) => {
    const containerId = `province-thumbnail-item-${item.adcode}`;
    const container = document.getElementById(containerId);

    if (!container) {
      console.warn(`容器 ${containerId} 不存在`);
      resolve();
      return;
    }

    if (thumbnailCharts.value[item.adcode]) {
      thumbnailCharts.value[item.adcode].dispose();
    }

    container.style.width = '100%';
    container.style.height = '100%';

    let path = `https://geo.datav.aliyun.com/areas_v3/bound/${item.adcode}_full.json`;

    axios.get(path).then((res) => {
      const mapName = `thumbnail-${item.adcode}`;
      echarts.registerMap(mapName, res.data);

      setTimeout(() => {
        const chart = echarts.init(container);
        const options = {
          geo: {
            map: mapName,
            roam: false,
            zoom: 1,
            label: { show: false },
            emphasis: { label: { show: false } },
            itemStyle: {
              normal: {
                borderColor: '#5999C4',
                borderWidth: 1,
                areaColor: '#5999C4',
              },
              emphasis: {
                areaColor: '#389BB7',
              }
            },
          }
        };
        chart.setOption(options);
        thumbnailCharts.value[item.adcode] = chart;
        resolve();
      }, 50);

    }).catch(error => {
      getThumbnailCounty(item).then(resolve);
    });
  });
}

// 获取县级缩略图数据
const getThumbnailCounty = (item) => {
  return new Promise((resolve) => {
    const containerId = `province-thumbnail-item-${item.adcode}`;
    const container = document.getElementById(containerId);
    if (!container) {
      resolve();
      return;
    }

    let path = `https://geo.datav.aliyun.com/areas_v3/bound/${item.adcode}.json`;
    axios.get(path).then((res) => {
      const mapName = `thumbnail-${item.adcode}`;
      echarts.registerMap(mapName, res.data);

      setTimeout(() => {
        const chart = echarts.init(container);
        const options = {
          geo: {
            map: mapName,
            roam: false,
            zoom: 1,
            label: { show: false },
            emphasis: { label: { show: false } },
            itemStyle: {
              normal: {
                borderColor: '#5999C4',
                borderWidth: 1,
                areaColor: '#5999C4',
              },
              emphasis: {
                areaColor: '#389BB7',
              }
            },
          }
        };
        chart.setOption(options);
        thumbnailCharts.value[item.adcode] = chart;
        resolve();
      }, 50);
    }).catch(error => {
      console.error(`加载缩略图 ${item.province} 失败:`, error);
      resolve();
    });
  });
}

// 切换缩略图
const scrollThumbnails = async (direction) => {
  stopAutoPlay();
  
  const newIndex = startIndex.value + direction;
  if (direction === -1 && newIndex < 0) return;
  if (direction === 1 && newIndex + VISIBLE_COUNT > provinceList.value.length) return;

  startIndex.value = newIndex;

  await nextTick();

  Object.keys(thumbnailCharts.value).forEach(adcode => {
    const isVisible = visibleThumbnails.value.some(item => item.adcode.toString() === adcode);
    if (!isVisible && thumbnailCharts.value[adcode]) {
      thumbnailCharts.value[adcode].dispose();
      delete thumbnailCharts.value[adcode];
    }
  });

  await initVisibleThumbnails();
  
  // 5秒后自动恢复轮播
  resumeAutoPlayAfterDelay(6000);
}

// 点击省份缩略图
const handleProvinceClick = (province) => {
  stopAutoPlay();
  initMainMap(province);
  emit('main-cityMap', null, null);
  emit('main-countryCity', null, null);
  
  // 5秒后自动恢复轮播
  resumeAutoPlayAfterDelay(5000);
};

// 地图点击事件处理
const handleMapClick = (chinaParam) => {
  console.log('点击地图:', chinaParam);
  
  // 暂停自动轮播
  stopAutoPlay();
  
  const code = mapCode.value.find(
    (x) => chinaParam.name.indexOf(x ? x.name : "") !== -1
  );

  if (code && mapCode.value.length !== 1) {
    if (chinaParam.name.endsWith('市')) {
      emit('main-cityMap', chinaParam.name, Number(code.adcode));
      getProvinceMapOpt(Number(code.adcode), chinaParam.name, 'city');
    } else {
      emit('main-countryCity', chinaParam, Number(code.adcode));
      getProvinceMapOpt(Number(code.adcode), chinaParam.name, 'county');
    }
  } else {
    // 如果点击的是当前层级的区域，更新选中点位
    updateSelectedPointByClick(chinaParam.name);
  }
  
  // 5秒后自动恢复轮播
  resumeAutoPlayAfterDelay(5000);
};

// 下钻显示省市地图
const getProvinceMapOpt = (provinceAlphabet, name, level = 'city') => {
  // 更新当前层级的点位
  updateCurrentLevelPoints(name, level);

  const path = `https://geo.datav.aliyun.com/areas_v3/bound/${provinceAlphabet}_full.json`;

  axios.get(path).then((res) => {
    mapCode.value = res.data.features.map(x => ({
      adcode: x.properties.adcode,
      name: x.properties.name,
    }));

    echarts.registerMap(name, res.data);
    changeOptions(name);
    myChart.value.setOption(distributionOptions.value, true);
  }).catch((error) => {
    getProvinceMapOptCounty(provinceAlphabet, name, level);
  });
};

// 获取县级别的地图数据
const getProvinceMapOptCounty = (provinceAlphabet, name, level = 'county') => {
  // 更新当前层级的点位
  updateCurrentLevelPoints(name, level);

  const path = `https://geo.datav.aliyun.com/areas_v3/bound/${provinceAlphabet}.json`;
  axios.get(path).then((res) => {
    mapCode.value = res.data.features.map(x => ({
      adcode: x.properties.adcode,
      name: x.properties.name
    }));

    echarts.registerMap(name, res.data);
    changeOptions(name);
    myChart.value.setOption(distributionOptions.value, true);
  }).catch((error) => {
    console.error('请求出错:', error);
  });
};

// 修改地图配置
const changeOptions = (name) => {
  // 使用当前层级的点位列表
  const points = currentLevelPoints.value;

  const seriesData = points.map((point, index) => ({
    name: point.name,
    value: [point.value[0], point.value[1], point, index === currentIndex.value],
    siteType: point.siteType,
    city: point.city,
    isCurrent: index === currentIndex.value
  }));
  
  const iconMap = {
    1: icon1,
    2: icon2,
    3: icon3,
    4: icon4,
    0: icon5,
    'default': locationIcon
  };
  
  const series = [
    {
      type: "scatter",
      coordinateSystem: "geo",
      data: seriesData,
      symbolSize: (params) => {
        const isCurrent = params[3];
        return isCurrent ? 40 : 24;
      },
         symbol: (params) => {
        const siteType = params[2]?.siteType;
        const isCurrent = params[3];

        if (isCurrent) {
          return `image://${locationIcon}`;
        }

        return `image://${iconMap[siteType] || iconMap.default}`;
      },
      emphasis: {
        scale: 1.2,
        itemStyle: {
          shadowBlur: 10,
          shadowColor: '#00ccff'
        }
      },
      tooltip: {
        trigger: 'item',
        formatter: function (params) {
          return `${params.data.name}`;
        }
      }
    }
  ];

  distributionOptions.value = {
    tooltip: {
      backgroundColor: 'rgba(0, 45, 57, 0.7)',
      borderRadius: 5,
      borderColor: '#00ccff',
      shadowColor: 'rgba(0, 204, 255, 0.3)',
      shadowOffsetX: 2,
      shadowOffsetY: 2,
      shadowBlur: 6,
      textStyle: {
        color: '#ffffff',
        fontSize: 14
      }
    },
    series: series,
    geo: {
      map: name,
      layoutCenter: ["50%", "50%"],
      layoutSize: "55%",
      roam: true,
      zoom: 1.6,
      label: {
        normal: {
          show: false,
          textStyle: {
            color: "#00C8FA",
            fontSize: 16,
            fontFamily: "Arial",
          },
        },
      },
      tooltip: {
        show: true,
        formatter: function (params) {
          return ''
        }
      },
      itemStyle: {
        normal: {
          borderColor: '#055889',
          borderWidth: 1,
          areaColor: '#042A4F',
          shadowOffsetX: -2,
          shadowOffsetY: 2,
          shadowBlur: 10
        },
        emphasis: {
          areaColor: '#389BB7',
          borderWidth: 0
        }
      },
      regions: [],
    },
  };
};

// 窗口调整大小处理
const handleResize = () => {
  if (myChart.value) {
    myChart.value.resize();
  }

  Object.values(thumbnailCharts.value).forEach(chart => {
    if (chart && chart.resize) {
      chart.resize();
    }
  });
};

// 清理事件监听
const cleanup = () => {
  if (myChart.value) {
    myChart.value.dispose();
    myChart.value.off("click", handleMapClick);
  }

  Object.values(thumbnailCharts.value).forEach(chart => {
    if (chart && chart.dispose) {
      chart.dispose();
    }
  });
  thumbnailCharts.value = {};

  window.removeEventListener('resize', handleResize);
};

// 根据省份名称查找 adcode
const findAdcodeByProvince = (provinceName) => {
  for (const province of cityCode) {
    if (provinceName.includes(province.name)) {
      return province.adcode;
    }
  }
  return null;
};

// 处理点数据，生成省份列表
const getCountyList = () => {
  const groupedByProvince = props.scatteredPoints.reduce((acc, point) => {
    const province = point.province;
    if (!acc[province]) {
      acc[province] = [];
    }
    acc[province].push(point);
    return acc;
  }, {});

  const provinceArray = Object.entries(groupedByProvince)
    .map(([province, points]) => ({
      province,
      adcode: findAdcodeByProvince(province),
      points: points.map(x => ({ ...x }))
    }))
    .filter(item => item.province !== 'undefined' && item.adcode);

  provinceList.value = provinceArray;

  if (provinceList.value.length > 0) {
    initMainMap(provinceList.value[0]);

    nextTick(() => {
      initVisibleThumbnails();
    });
  }
}

// 监听 scatteredPoints 变化
watch(() => props.scatteredPoints, (newVal) => {
  if (newVal && newVal.length > 0) {
    getCountyList();
  }
}, { immediate: true });

// 暴露方法给父组件
defineExpose({ 
  initMainMap, 
  getProvinceMapOpt, 
  startAutoPlay, 
  stopAutoPlay,
  getCurrentPoint 
});

// 页面加载后执行
onMounted(() => {
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  stopAutoPlay();
  clearResumeTimer();
  cleanup();
});
</script>

<style lang="scss" scoped>
.map-container {
  width: 100%;
  height: 78%;
  margin: 0 auto;
  margin-top: 8%;
  position: relative;

  .main-map {
    height: 94%;
    width: 60%;
    margin: 0 auto;
  }

  .province-thumbnail {
    position: absolute;
    right: 25.5%;
    width: 7%;
    top: 5%;
    height: 50%;

    .arrow-top {
      text-align: center;
      background: url("@/assets/image/panoramic-monitor/Vector(1).png")
        no-repeat;
      background-size: contain;

      .el-icon {
        cursor: pointer;
        font-size: 20px;
      }
    }

    .arrow-bottom {
      text-align: center;
      background: url("@/assets/image/panoramic-monitor/Vector(1).png")
        no-repeat;
      background-size: contain;
      transform: rotate(180deg);
      transform-origin: center center;

      .el-icon {
        cursor: pointer;
        font-size: 20px;
      }
    }

    .province-thumbnail-item-container {
      width: 90%;
      margin: 0 auto;
      height: 80%;
    }

    .scroll-wrapper {
      display: flex;
      flex-direction: column;
      height: 100%;
      width: 100%;
      gap: 3%;
    }

    .province-thumbnail-item {
      height: 30%;
      width: 100%;
      background: radial-gradient(
        57.5% 57.5% at 50% 50%,
        rgba(4, 38, 67, 0.3) 0%,
        #03192b 100%
      );
      border-radius: 16px;
      border: 1px solid #0e4468;
      color: #5999c4;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        border-color: #0099ff;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 153, 255, 0.2);
      }
    }

    .province-thumbnail-item-adcode {
      width: 90%;
      height: 70%;
      margin: 5% auto 0;
    }

    .province-thumbnail-item-name {
      font-size: 12px;
      line-height: 20px;
    }
  }
}

.active {
  color: #0099ff !important;
  border: 1px solid #0099ff !important;
  background: radial-gradient(
    57.5% 57.5% at 50% 50%,
    rgba(0, 153, 255, 0.2) 0%,
    #03192b 100%
  ) !important;
}

.caret-top-disabled {
  pointer-events: none;
  opacity: 0.5;
  color: #c0c4cc;
}

.caret-top {
  color: #c0c4cc;
  cursor: pointer;

  &:hover {
    color: #0099ff;
  }
}
</style>