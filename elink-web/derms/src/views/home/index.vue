<template>
  <div class="app" v-loading="loading">
    <div class="right-containers" v-if="isfull">
      <div class="background"></div>
      <div v-for="item in deviceType" :key="item.id" class="device-type-item"
        :class="{ active: selectedDevice === item.id }" @click="selectedType(item)">
        <!-- {{ selectedDevice }} -->
        <div v-if="item.id !== 'changeData' && deviceAll[item.id]?.siteNum !== 0"
          style="display: flex; align-items: center;">
          <img :src="item.image" />
          <span class="device-type">{{ item.name }}</span>
        </div>
        <div v-if="item.id === 'changeData' && deviceAll[item.id]?.changeSiteNum !== 0"
          style="display: flex; align-items: center;">
          <img :src="item.image" />
          <span class="device-type">{{ item.name }}</span>
        </div>

      </div>
    </div>
    <div class="left-containers" v-if="isfull">
      <div class="background-left"></div>
      <!-- 默认全局 {{ deviceTypeArray }} -->
      <div class="tipsContainer" v-show="!searchStaition">
        <div class="tips">{{ deviceTypeArray?.totalName }}</div>
        <div class="tipsNum">
          <div>
            <span class="tipsNum_span">{{ deviceTypeArray?.TatolNum1 }}</span>
            <span v-if="['pileData', 'changeData'].includes(selectedDevice)">次</span>
            <span v-else-if="selectedDevice === 'pvData'">kWp</span>
            <span v-else>kW</span>
          </div>
          <div v-if="['pileData', 'storageData'].includes(selectedDevice)">
            /
            <span class="tipsNum_span">{{ deviceTypeArray?.TatolNum2 }}</span>
            <span v-if="selectedDevice === 'pileData'">次</span>
            <span v-else>kWh</span>
          </div>
        </div>

        <div class="tips">{{ deviceTypeArray?.chargeValue }}</div>
        <div class="tipsNum" v-if="selectedDevice !== 'pileData'">

          <div>
            <span class="tipsNum_span">{{ deviceTypeArray?.chargeNum1 }}</span>
            <span v-if="selectedDevice === 'pileData'">台</span>
            <span v-else-if="['storageData', 'pvData'].includes(selectedDevice)">座</span>
            <span v-else>kWh</span>
          </div>
        </div>

        <div v-if="selectedDevice === 'pileData'">
          <div class="tips">场站数量</div>
          <div class="tipsNum">
            <div>
              <span class="tipsNum_span">{{ deviceTypeArray?.chargeNum2 }}</span>
              <span>座</span>
            </div>
          </div>
        </div>
        <div class="tips">{{ deviceTypeArray?.chargeNum }}</div>
        <div class="tipsNum">
          <div>
            <span class="tipsNum_span">{{ deviceTypeArray?.chargeNum3 }}</span>
            <span v-if="selectedDevice === 'changeData'">座</span>
            <span v-else>台</span>
          </div>
        </div>
        <div class="tips">{{ deviceTypeArray?.totalSumValue }}</div>
        <div class="tipsNum">
          <div>
            <span class="tipsNum_span">{{ deviceTypeArray?.totalSumNum1 }}</span>
            <span v-if="selectedDevice === 'chargeData'">kWp</span>
            <span v-else>kWh</span>
          </div>
        </div>
        <div class="tips">{{ deviceTypeArray?.totalSumValueV2G }}</div>
        <div class="tipsNum" v-if="['pileData', 'storageData'].includes(selectedDevice)">
          <span class="tipsNum_span">{{ deviceTypeArray?.totalSumNumDis1 }}</span>
          <!-- <span v-if="selectedDevice === 'pileData'">kWh</span>
          <span v-else>度kWh</span> -->
          <span>kWh</span>
        </div>
        <div class="date">*数据截止至 {{ timer }}</div>
      </div>
      <!-- 搜索选中场站 -->
      <div class="search-station" v-show="searchStaition">
        <div class="search-station-title">
          <div class="search-station-icon" @click="toggleSearchStation()">
            <img src="./image/home.png" alt="" />
          </div>
          <div class="content_form_filter-methodtop_left">
            <div class="content_form_top_left_icon">
              <el-select v-model="siteId" placeholder="请选择站点" filterable :filter-method="filterMethod"
                @change="handleSelectChange">
                <el-option v-for="item in siteOptions" :key="item.value" :label="item.label" :value="item.value">
                  <span style="display: flex; align-items: center;">
                    <img :src="getIcon(item)" alt="" style="width: 16px; height: 16px; margin-right: 8px;" />
                    {{ item.label }}
                  </span>
                </el-option>
              </el-select>
            </div>

          </div>

        </div>
        <div class="site_single" v-show="siteSingle.value && Object.keys(siteSingle.value).length > 0">
          <div style="display: flex; align-items: center;">
            <img :src="location" alt="" style="width: 16px; height: 16px; margin-right: 8px;" />
            {{ siteSingle.label }}
          </div>
          <div class="site_single_content">

            <!-- {{ parsedAddress }} -->
            <el-tooltip :content="parsedAddress" placement="top">
              <div class="address-ellipsis">{{ truncatedAddress }}</div>
            </el-tooltip>
          </div>
          <div class="site_single_line" @click="handleClick">
            <img :src="textImg" alt="" />
            <p>站点详情</p>
          </div>
        </div>
      </div>
    </div>
    <div class="china-containers">
      <mapView @iscreen="iscreen" :selectedDevice="selectedDevice" :location="deviceTypeArray"
        :searchStaition="searchStaition" @iSearch="iSearch" @searchValue="searchValue" :siteId="siteId"></mapView>
    </div>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
import { onMounted, ref, computed, onBeforeUnmount } from "vue";
import { useRouter } from 'vue-router';
import dianzhuang from './image/dianzhuang.png';
import mapView from "./components/mapView.vue";
import chuneng from './image/chuneng.png';
import guangfu from './image/guangfu.png';
import huandian from './image/huandian.png';
import location from './image/location.png';
import locationbg from './image/location-bg.png';
import textImg from './image/text.png';
import { homeWebSocket } from "@/api/websocket/webSocket";
import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";
const deviceList = ref([
  // 电桩
  // {
  //   id: 'pileData',
  //   totalName: "累计充放电次数",
  //   TatolNum1: 'totalChargeNum',
  //   TatolNum2: 'totalDisChargeNum',
  //   chargeValue: '充电场站',
  //   chargeNum1: 'chargePileNum',
  //   chargeNum2: 'siteNum',
  //   chargeNum: 'V2G场站',
  //   chargeNum3: 'v2gPileNum',
  //   totalSumValue: '累计充电量',
  //   totalSumNum1: 'totalChargeQt',
  //   totalSumValueV2G: '累计V2G电量',
  //   totalSumNumDis1: 'totalDisChargeQt',
  //   siteList: 'siteList'
  // },
  {
    id: 'pileData',
    totalName: "累计充放电次数",
    TatolNum1: 'totalChargeNum',
    TatolNum2: 'totalDisChargeNum',
    // chargeValue: '充电场站',
    // chargeNum1: 'chargePileNum',
    chargeNum2: 'siteNum',
    chargeNum: '电桩数量',
    chargeNum3: 'pileNum',
    totalSumValue: '累计充电量',
    totalSumNum1: 'totalChargeQt',
    totalSumValueV2G: '累计V2G电量',
    totalSumNumDis1: 'totalDisChargeQt',
    siteList: 'siteList'
  },
  // 换电
  {
    id: 'changeData',
    totalName: "累计换电次数",
    TatolNum1: 'totalChangeNum',
    chargeValue: '换电总装机量',
    chargeNum1: 'changeCapacity',
    chargeNum: '换电场站',
    chargeNum3: 'changeSiteNum',
    totalSumValue: '累计换电量',
    totalSumNum1: 'totalChangeQt',
    siteList: 'siteList'
  },
  // 光伏
  {
    id: 'pvData',
    totalName: "光伏总装机量",
    TatolNum1: 'pvCapacity',
    chargeValue: '光伏场站',
    chargeNum1: 'siteNum',
    chargeNum: '逆变器数量',
    chargeNum3: 'inverterNum',
    totalSumValue: '累计发电量',
    totalSumNum1: 'totalQt',
    siteList: 'siteList'
  },
  // 储能
  {
    id: 'storageData',
    totalName: "储能总装机量",
    TatolNum1: 'pcsRatedPower',
    TatolNum2: 'batteryRatedCapacity',
    chargeValue: '储能场站',
    chargeNum1: 'siteNum',
    chargeNum: '储能柜数量',
    chargeNum3: 'storageNum',
    totalSumValue: '累计充电量',
    totalSumNum1: 'totalChargeQt',
    totalSumValueV2G: '累计放电量',
    totalSumNumDis1: 'totalDisChargeQt',
    siteList: 'siteList'
  },
]);
const loading = ref(true)

const deviceAll = ref([]);
const originalData = ref({})
const siteId = ref(null)
const siteSingle = ref({})
// 选择项数据
const siteOptions = ref([
  // { label: '站点1', value: '1' },
  // { label: '站点2', value: '2' },
  // { label: '站点3', value: '3' }
]);
const deviceType = ref([
  {
    id: "pileData", name: "电桩", image: dianzhuang, type: "3",
  },
  { id: 'pvData', name: "光伏", image: guangfu, type: "1" },
  { id: "storageData", name: "储能", image: chuneng, type: "2" },
  { id: "changeData", name: "换电", image: huandian, type: "6" },
]);
const deviceTypeArray = ref()
// ----------------------------------------------------------------
// 搜索关键词
const searchKeyword = ref('');



//获取选中的场站信息
const handleSelectChange = (val) => {
  //  console.log(siteId.value,val)

  siteSingle.value = siteOptions.value.find(item => item.value === val);
  siteId.value = val
}
// 根据是否匹配返回不同图标路径
const getIcon = (item) => {
  const matchedItem = siteOptions.value.find(item => item.value === siteId.value);
  if (matchedItem) {
    searchKeyword.value = matchedItem.label;
  }

  const isMatched = searchKeyword.value && item.label.includes(searchKeyword.value);
  return isMatched ? location : locationbg;
};
const closeWebSocket = () => {
  if (window.ws && window.ws.readyState !== WebSocket.CLOSED) {
    window.ws.close();
    // console.log("✅ WebSocket 已手动关闭");
  } else {
    console.warn(" WebSocket 已经关闭或未初始化");
  }
};
// 自定义过滤方法
// const customFilter = (query) => {
//   searchKeyword.value = query;
// };
// ----------------------------------------------------------------
// 搜索框返回的数据
// ----------------------------------------------------------------
const iSearch = (val) => {
  searchStaition.value = val
}
const searchValue = (val) => {

  siteSingle.value = siteOptions.value.find(item => item.value === val.id);
  siteId.value = val.id
}
// 获取站点列表
const fetchSiteList = async () => {
  try {
    const res = await findSiteListByUserId({ scenarioTypes: selectTypeNum.value });
    siteOptions.value = res.data.map(item => ({
      value: item.id,        // 假设每个站点有 id 字段
      label: item.siteName,  // 假设每个站点有 siteName 字段
      location: item.location
    }));
  } catch (error) {
    console.error("获取站点列表失败:", error);
  }
};
// ----------------------------------------------------------------
const selectTypeNum = ref('3')
const selectedDevice = ref(null)

const selectedType = (val) => {
  selectTypeNum.value = val.type
  searchStaition.value = false

  selectedDevice.value = val.id
  originalData.value = deviceAll.value[selectedDevice.value] || {};
  deviceTypeArray.value = convertToConfig(originalData.value, selectedDevice.value);
  fetchSiteList()
  siteId.value = null
  siteSingle.value = {}

}

const activeDevice = computed(() => {
  return selectedDevice.value
})

function isFieldName (str) {
  return /^[a-z][a-zA-Z0-9]*$/.test(str); // 匹配驼峰命名的字段名
}
const convertToConfig = (val, id) => {
  const typeList = deviceList.value.find(item => item.id === id);
  if (!typeList) return {};

  const result = {};
  for (const key in typeList) {
    const configKey = typeList[key];

    if (typeof configKey === 'string' && isFieldName(configKey)) {
      result[key] = val.hasOwnProperty(configKey) ? val[configKey] : '--';
    } else {
      result[key] = configKey;
    }
  }
  loading.value = false;

  return result;
};
const searchStaition = ref(false)
const toggleSearchStation = () => {
  searchStaition.value = false;
};
const isfull = ref(true)
const iscreen = (val) => {
  isfull.value = val
}
const parsedAddress = computed(() => {
  if (!siteSingle.value || !siteSingle.value.location) return '';
  try {
    const loc = JSON.parse(siteSingle.value.location);
    return loc.address || '';
  } catch (e) {
    return '';
  }
});
// 截断显示
const truncatedAddress = computed(() => {
  return parsedAddress.value.slice(0, 15) + '...';
})
const vueRouter = useRouter();
// 点击跳转到详情页
const handleClick = () => {
  vueRouter.push({
    path: "/stationDetails/stationDetails",
    query: {
      siteName: siteSingle.value.label,
      siteId: siteSingle.value.value,

    },
  });
}
const userInfo = computed(() => {
  return store.state.app.userInfo;
});
const store = useStore();
const timer = ref(null)
// ------------------------------------------------------------
let ws = null;
let reconnectInterval = null;
const retryCount = ref(0);
const MAX_RETRY = 3;
let finalReconnectTimer = null; // 持续尝试连接的定时器
const initWebSocket = () => {
  if (ws) {
    ws.close();
    ws = null;
  }

  const wsUrl = homeWebSocket({
    userId: userInfo.value.userId,
  });

  const pileMonitorWs = new WebSocket(wsUrl);

  // 绑定事件
  pileMonitorWs.onopen = webSocketOpen;
  pileMonitorWs.onerror = webSocketError;
  pileMonitorWs.onclose = webSocketClose;
  pileMonitorWs.onmessage = webSocketMessage;

  // 挂载到 window 上
  window.ws = pileMonitorWs;
  ws = pileMonitorWs;
};

const webSocketOpen = () => {
  retryCount.value = 0; // 重置重试次数
  if (reconnectInterval) {
    clearInterval(reconnectInterval);
    reconnectInterval = null;
  }
  if (finalReconnectTimer) {
    clearInterval(finalReconnectTimer);
    finalReconnectTimer = null;
  }
};

const webSocketError = (error) => {
  reconnectWebSocket();
};

const webSocketClose = () => {
  // reconnectWebSocket()
  // if (retryCount.value < MAX_RETRY) {
  // setTimeout(() => {
  //   reconnectWebSocket()
  // }, 3000);
}

// };

// const webSocketMessage = (event) => {
//   try {
//     deviceAll.value = JSON.parse(event.data);
//     console.log("✅ WebSocket 数据解析成功", deviceAll.value);
//     // 过滤出 siteNum 或 changeSiteNum 不为 0 的设备类型
//     // 过滤出 siteNum 或 changeSiteNum 不为 0 的设备类型
//     const filteredDeviceType = deviceType.value.filter(item => {
//       const key = item.id;
//       if (deviceAll.value[key]) {
//         return deviceAll.value[key].siteNum !== '0' && deviceAll.value[key].changeSiteNum !== "0";
//       }
//       return false;
//     });
//     console.log(filteredDeviceType, 'filteredDeviceType');
//     selectedDevice.value = deviceType.value[0].id;
//     originalData.value = deviceAll.value[selectedDevice.value] || {};
//     deviceTypeArray.value = convertToConfig(originalData.value, selectedDevice.value);

//     const now = new Date();
//     timer.value = now.toLocaleString();
//     isfull.value = true;
//   } catch (err) {
//     console.error('解析 WebSocket 数据失败:', err);
//   }
// };
const webSocketMessage = (event) => {
  try {
    deviceAll.value = JSON.parse(event.data);

    // 过滤出 siteNum 或 changeSiteNum 不为 0 的设备类型
    const filteredDeviceType = deviceType.value.filter(item => {
      const key = item.id;
      if (deviceAll.value[key]) {
        // 优先判断 siteNum
        if (deviceAll.value[key].hasOwnProperty('siteNum') && deviceAll.value[key].siteNum !== 0) {
          return true;
        }
        // 如果 siteNum 不存在或为 0，再判断 changeSiteNum
        if (deviceAll.value[key].hasOwnProperty('changeSiteNum') && deviceAll.value[key].changeSiteNum !== 0) {
          return true;
        }
      }
      return false;
    });
    // ✅ 更新 deviceType.value
    deviceType.value = filteredDeviceType;

    // 设置默认选中设备
    selectedDevice.value = deviceType.value[0]?.id || 'pileData';
    originalData.value = deviceAll.value[selectedDevice.value] || {};
    deviceTypeArray.value = convertToConfig(originalData.value, selectedDevice.value);

    const now = new Date();
    timer.value = now.toLocaleString();
    isfull.value = true;
  } catch (err) {
    console.error('解析 WebSocket 数据失败:', err);
  }
};


const reconnectWebSocket = () => {
  if (retryCount.value >= MAX_RETRY) {
    console.error(`已达到最大重试次数 ${MAX_RETRY}，停止重连`);
    // 清除之前的重连定时器
    if (reconnectInterval) {
      clearInterval(reconnectInterval);
      reconnectInterval = null;
    }
    // 启动持续尝试连接（每 1 分钟一次）
    if (!finalReconnectTimer) {
      finalReconnectTimer = setInterval(() => {
        // console.log("【持续尝试】1分钟后尝试重新连接...");
        initWebSocket();
      }, 60000); // 1分钟 = 60 * 1000 ms
    }
    return;
  }

  retryCount.value++;

  // console.log(`尝试第 ${retryCount.value} 次重连...`);

  if (reconnectInterval) clearInterval(reconnectInterval);
  reconnectInterval = setInterval(() => {
    // console.log("正在尝试重新连接...");
    initWebSocket();
  }, 5000);
};

// ------------------------------------------------------------


onBeforeUnmount(() => {
  ws.onclose();
  ws = null;

});
onMounted(() => {

  initWebSocket();
  fetchSiteList()
});
</script>

<style lang="scss" scoped>
button {
  z-index: 10;
}

.app {
  width: 100%;
  height: 100%;

  display: flex;
  justify-content: space-between;
  position: relative;

  .content_form_filter-methodtop_left {
    margin-left: 10px;
  }

  .tipsContainer {
    padding: 30px;
    box-sizing: border-box;
    height: 100%;
    // border:1px solid #0e85e6;
    border-radius: 10px;
    // background: linear-gradient(to bottom, rgba(7, 38, 65, 0.8), rgba(10, 47, 81, 0.2), rgba(39, 63, 88, 0.8));
    // background: linear-gradient(to left, rgba(7, 38, 65, 0.8), rgba(10, 47, 81, 0.2), rgba(39, 63, 88, 0.8));
    // background: linear-gradient(to right, rgba(7, 38, 65, 0.8) 0%, rgba(10, 47, 81, 0.2) 50%, rgba(39, 63, 88, 0.8) 100%);
  }

  .left-containers {
    width: 16%;
    height: 100%;
    height: 100%;
    // padding: 34px 51px;
    box-sizing: border-box;
    // background-image: url('./image/20250827-112753.png');
    position: relative;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    color: rgba(255, 255, 255, 0.6);
    font-size: 16px;
    // background-color: #0A243C;
    // z-index: 2;

    .search-station {
      position: relative;
      z-index: 1;
      padding: 10px;

      .search-station-title {
        display: flex;
        align-items: center;
      }

      // display: flex;

      .search-station-icon {
        // z-index: 3;
        /* 增加 z-index */
        cursor: pointer;
        // width: 48px;
        padding: 5px;
        // height: 48px;
        background: rgba(0, 157, 242, 0.25);
        border-radius: 4px 4px 4px 4px;
        display: flex;
        align-items: center;
        /* 垂直居中 */
        justify-content: center;
        /* 水平居中 */
      }
    }

    .site_single {
      margin-top: 10px;
      margin-left: 50px;
      width: 80%;
      // width: 100%;
      /* 原始 */

      /* 改为 RGB */
      background: rgba(7, 16, 25, 0.6);
      border: 1px solid rgba(0, 230, 254, 0.6);
      border-radius: 4px 4px 4px 4px;
      padding: 10px;
      box-sizing: border-box;
      color: #0d96ff;
      font-size: 12px;
      box-shadow: inset 0px 0px 2px 1px #03baff;
    }

    .site_single_content {
      // z-index: 3;
      font-size: 10px;
      color: rgba(255, 255, 255, 0.6);
      margin-top: 10px;
      margin-left: 25px;
      position: relative;

      /* 确保 tooltip 可以定位 */
      .address-ellipsis {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        max-width: 200px;
        /* 根据需要调整 */
        pointer-events: auto;
        /* 确保可以触发 hover */
      }
    }

    .site_single_line {
      cursor: pointer;
      display: flex;
      justify-content: end;
      margin-top: 20px;

      p {
        margin-left: 10px;
        font-size: 12px;
      }
    }

    .date {
      font-size: 13px;
      position: relative;
      z-index: 1;
    }

    .tips {
      position: relative;
      z-index: 1;
      text-align: left;
      font-style: normal;
    }

    span {
      padding-right: 10px;
    }

    .tipsNum_span {
      color: rgba(13, 150, 255, 1);
      font-size: 28px;
    }

    .tipsNum {
      margin-top: 10px;
      display: flex;
      font-weight: 500;
      font-size: 24px;
      text-align: left;
      font-style: italic;
      margin-bottom: 20px;
      font-family: Agency FB, Agency FB;
      position: relative;
      z-index: 1;
    }

    .content_form_top_left {
      // z-index: 3;
      /* 增加 z-index */

      display: flex;
      align-items: center;
      margin-left: 10px;

      .content_form_top_left_icon {
        max-width: 340px;
      }
    }
  }

  .background-left {
    position: absolute;
    z-index: 1;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url("./image/20250827-112753.png");
    background-size: cover;
    background-position: center;
    pointer-events: none;
    // transform: rotate(180deg);
    // z-index: 0;
  }

  .china-containers {
    width: 100%;
    opacity: 0.8;
    height: 100%;
    position: absolute;
    // z-index: 1;

    // border-radius: 50%;
    // overflow: hidden;
    // aspect-ratio: 1 / 1;
    #map {
      width: 100%;
      height: 100%;
    }
  }

  .right-containers {
    // position: relative;
    position: fixed;
    width: 10%;
    right: 0;
    z-index: 99;
    // margin: 1% 3%;
    box-sizing: border-box;
    // background-color: #FFF;
    height: 100%;
    // z-index: 2;
    color: rgba(255, 255, 255, 0.8);
    cursor: pointer;
    pointer-events: none;
    /* 关键属性 */

    .background {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-image: url("./image/20250827-112753.png");
      background-size: cover;
      background-position: center;
      transform: rotate(180deg);

      // z-index: 2;
    }

    .device-type-item {
      margin-top: 20px;
      padding-left: 5px;
      display: flex;
      align-items: center;
      position: relative;
      /* 添加这一行 */
      // z-index: 3;
      /* 确保在背景之上 */
      pointer-events: auto;
    }

    .device-type-item.active {
      color: white;
      width: 129px;
      height: 40px;
      padding-left: 5px;
      background: radial-gradient(0% 49% at 84% 94%,
          rgba(97, 156, 214, 0.8) 0%,
          rgba(1, 9, 16, 0.2) 100%);
      border-radius: 6px 6px 6px 6px;
      border: 1px solid rgba(80, 154, 182, 1);
      border-image: radial-gradient(circle,
          rgba(70, 125, 168, 1),
          rgba(80, 154, 182, 1),
          rgba(70, 125, 168, 0)) 1 1;
    }

    .device-type {
      padding-left: 10px;
    }
  }
}

.content_form_top_left {
  width: 100%;
}

.app .left-containers .content_form_top_left .content_form_top_left_icon {
  width: 100%;
}

// ::v-deep .ui-design .el-select{
//   background: #071019 !important;
// }</style>