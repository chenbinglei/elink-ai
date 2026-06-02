<template>
  <div class="panoramic-monitor" ref="appRef">
    <div style="width: 100%; height: 1080px"></div>
    <div style="
        /* width: 1980px;
        height: 1080px; */
        width: 100%;
        height: 1080px;
        display: inline-block;
        position: absolute;
        top: 0px;
        /* left: 50%; */
        /* transform: translateX(-50%); */
      ">
      <EchartsMap :mapName="mapName" ref="mapRef" :scatteredPoints="state.scatteredPoints" @changeMap="changeMapHandler"
        @main-countryCity="mainCountryCityHandler"
        @main-cityMap="mainCityMapHandler" @main-map="mainMapHandler" @current-point-change="handleCurrentPointChange" />


    </div>
    <PanoramicMonitorTitle></PanoramicMonitorTitle>
    <div class="map-name-content">
      <div class="map-name-point"></div>
      <!-- {{ defaultMapName }} -->
      {{ mainMapName?.province}}
      <div class="map-name-next" v-show="mapName !== null">
        <div class="map-name-next-icon"></div>
        <div>{{ mapName?.name }}</div>

      </div>
      <div class="map-name-next" v-show="countryMapName !== null">
        <div class="map-name-next-icon"></div>
        <div>{{ countryMapName?.name }}</div>

      </div>
      <div class="map-name-return" v-show="mapName !== null || countryMapName !== null" @click="mapNameBackHandler">
        <div class="map-name-return-icon"></div>
        返回
      </div>
    </div>
    <PanoramicMonitorBottom :data="state.siteMapRow" :optionList="optionList" :mainMapName="mainMapName"></PanoramicMonitorBottom>
    <NumberStations style="position: absolute; bottom: 130px; right: 25%" :data="state.mapSite"></NumberStations>
    <div class="panoramic-monitor-left">
      <div class="item-left" style="height: 30%">
        <AssetOverview :data="state.assetOverview"></AssetOverview>
      </div>
      <div class="item-left" style="height: 35%">
        <RealTimePower :data="realData.realPower"></RealTimePower>
      </div>
      <div class="item-left" style="height: 35%">
        <EquipmentStatus :data="state.deviceAlarm" :deviceStatus="realData.deviceStatus"></EquipmentStatus>
      </div>
    </div>
    <div class="panoramic-monitor-right">
      <div class="item-right" style="height: 50%">
        <ChargingSwappingOperation :changeOperation="realData.changeOperation" :pileOperation="realData.pileOperation"
          :operationDateList="realData.operationDateList"></ChargingSwappingOperation>
      </div>
      <div class="item-right" style="height: 50%">
        <OpticalStorageOperation :storageOperation="realData.storageOperation" :pvOperation="realData.pvOperation"
          :operationDateList="realData.operationDateList"></OpticalStorageOperation>
      </div>
    </div>
  </div>
</template>
<script setup>
import { onMounted, onUnmounted, ref, nextTick, reactive } from "vue";
import PanoramicMonitorTitle from "@/views/panoramicMonitor/components/PanoramicMonitorTitle.vue"; //标题
import NumberStations from "@/views/panoramicMonitor/components/NumberStations.vue"; //场站数量
import AssetOverview from "@/views/panoramicMonitor/components/AssetOverview.vue"; //资产总览
import RealTimePower from "@/views/panoramicMonitor/components/RealTimePower.vue"; //实时功率
import ChargingSwappingOperation from "@/views/panoramicMonitor/components/ChargingSwappingOperation.vue"; //充换电运营
import OpticalStorageOperation from "@/views/panoramicMonitor/components/OpticalStorageOperation.vue"; //光储运营
import EquipmentStatus from "@/views/panoramicMonitor/components/EquipmentStatus.vue"; //设备状态
import PanoramicMonitorBottom from "@/views/panoramicMonitor/components/PanoramicMonitorBottom.vue"; //底部
import EchartsMap from "@/views/panoramicMonitor/components/EchartsMap.vue"; //底部
import icon5 from "@/assets/image/panoramic-monitor/number-stations-icon-map5.png";
import drawMixin from "./drawMixin";
import WebSocketManager from "@/utils/websocket.js";
import service from "@/utils/request";
import {
  largeRealWebSocket,
  largeStaticWebSocket,
} from "@/api/websocket/webSocket.js";
const defaultMapName = "巴中市";
const mapName = ref(null);
const mainMapName = ref(null);
const countryMapName = ref(null);
const mapRef = ref(null);

// 静态数据
const state = reactive({
  assetOverview: {}, //资产总览
  deviceAlarm: {}, //设备告警
  mapSite: {}, //站点地图标点
  scatteredPoints: [], // 散点坐标
  siteMapRow: {}, // 底部数据
});
const colors = {
  0: "#00FFB1",
  1: "#FFA700",
  2: "#00CCFF",
  3: "#B24BFF",
};
// 动态数据
const realData = reactive({
  deviceStatus: [], // 设备状态底部
  changeOperation: {}, // 换电
  storageOperation: {}, // 储能
  realPower: {}, // 实时功率
  pvOperation: {}, // 光伏
  pileOperation: {}, // 充电
  operationDateList: [], // 运营日期
});
const changeMapHandler = (map) => {
  mapName.value = map;
};
// 主地图-省
const mainMapHandler = (val) => {
  console.log(val, '接受的治');
  mainMapName.value = val;
  // mapName.value = val;
}
const mainCityMapHandler = (val, adcode) => {
  if (val === null) {
    mapName.value = null;
    return;
  }
  let obj = {
    name: val,
    adcode: adcode,
  }
  console.log(obj, '接受的市');
  mapName.value = obj;
}
const mainCountryCityHandler = (val, adcode) => {
  if (val === null) {
    countryMapName.value = null;
    return;

  };
  let obj = {
    ...val,
    adcode: adcode,
  }
  countryMapName.value = obj;
}



// const getActiveSiteIndexHandler = (index) => {
//   if (index > state.mapSite?.siteList?.length - 1) return;
//   state.siteMapRow = state.mapSite?.siteList?.length
//     ? state.mapSite.siteList[index]
//     : {};
// };
const handleCurrentPointChange = (data) => {
  console.log(data, '当前点位');
  state.siteMapRow = data.point;
}
const mapNameBackHandler = () => {
  // mapName.value = defaultMapName;
  // console.log(mainMapName.value,'xuyao');
  // mapRef.value.initMainMap(mainMapName.value);
  if (countryMapName.value !== null) {
    console.log(countryMapName.value, 'xuyao');
    mapRef.value.getProvinceMapOpt(mapName.value.adcode, mapName.value.name);
    countryMapName.value = null;
  } else {
    console.log(mainMapName.value, 'mainMapName');
    mapRef.value.initMainMap(mainMapName.value);
    mapName.value = null;
  }




};
const appRef = ref(null);
const drawMixinData = drawMixin(appRef.value);
onMounted(() => {
  nextTick(() => {
    drawMixinData.setAppRef(appRef.value);
    drawMixinData.mounted();
  });
});
onUnmounted(() => {
  drawMixinData.unMounted();
});

const socketManager = new WebSocketManager();
// 添加多个连接
socketManager.addConnection("chat", largeStaticWebSocket(), {
  onMessage: (data) => {
    const res = JSON.parse(data);

    Object.assign(state, res);
    let arr = res.mapSite?.siteList.map((item, index) => {
      // let location = JSON.parse(item.location);
      let location = item.location ? JSON.parse(item.location) : {};
      return {
        siteId: item.siteId,
        name: item.siteName,
        value: [Number(location.longitude), Number(location.latitude)],
        county: location.county,
        province: location.province,
        city: location.city,
        symbolSize: Math.floor(Math.random() * 20 + 20),
        symbol: "pin",
        ...item,
        // itemStyle: {
        //   color: colors[index], // 单独设置该数据点的颜色
        // },
      };
    });
   
    state.scatteredPoints = arr;
     console.log(state.scatteredPoints, '获取到的数据***999');
    state.siteMapRow = res.mapSite?.siteList[0];
  },
});
socketManager.addConnection("notifications", largeRealWebSocket(), {
  onMessage: (data) => {
    console.log(realData, JSON.parse(data));
    Object.assign(realData, JSON.parse(data));
  },
});
// 获取状态
console.log(socketManager.getStatus("chat"));
// 关闭所有连接
window.addEventListener("beforeunload", () => {
  console.log("页面即将刷新/关闭，关闭 WebSocket...");
  socketManager.closeAll();
});
</script>

<style scoped lang="scss">
.panoramic-monitor {
  width: 100%;
  overflow: hidden;
  //height: 100%;
  position: absolute;
  top: 50%;
  left: 50%;
  background: url("@/assets/image/panoramic-monitor/panoramic-monitor-bg1.jpg") top left no-repeat;
  background-size: 100% 100%;
  transform-origin: left top;

  .map-name-content {
    width: 578px;
    height: 48px;
    position: absolute;
    left: 50%;
    top: 73px;
    transform: translateX(-50%);
    background: url("@/assets/image/panoramic-monitor/map_name_bg.png") no-repeat;
    background-size: 100% 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: Microsoft YaHei, Microsoft YaHei;
    font-weight: bold;
    font-size: 18px;
    color: #0097d1;
    text-align: center;
    font-style: normal;
    text-transform: none;

    .map-name-point {
      width: 29px;
      height: 28px;
      background: url("@/assets/image/panoramic-monitor/map_name_point.png") no-repeat;
      background-size: 100% 100%;
      margin-right: 20px;
    }

    .map-name-next {
      color: #ffffff;
      background-size: 100% 100%;
      display: flex;
      align-items: center;
      margin: 0 20px;

      .map-name-next-icon {
        margin-right: 10px;
        width: 10px;
        height: 16px;
        background: url("@/assets/image/panoramic-monitor/map-name-next-icon.png") no-repeat;
        background-size: 100% 100%;
      }
    }

    .map-name-return {
      position: absolute;
      right: -150px;
      width: 133px;
      height: 32px;
      background: url("@/assets/image/panoramic-monitor/map-name-return-bg.png") no-repeat;
      background-size: 100% 100%;
      display: flex;
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      text-align: center;
      font-style: normal;
      align-items: center;
      cursor: pointer;

      :hover {
        filter: brightness(1.75);
      }

      .map-name-return-icon {
        width: 14px;
        height: 14px;
        background: url("@/assets/image/panoramic-monitor/map-name-return-icon.png") no-repeat;
        background-size: 100% 100%;
        margin-left: 40px;
        margin-right: 10px;
      }
    }
  }

  .panoramic-monitor-left {
    width: 20%;
    position: absolute;
    top: 84px;
    left: 22px;
    height: calc(100% - 84px);
    display: flex;
    flex-direction: column;
    box-sizing: border-box;

    .item-left {
      padding-bottom: 20px;
    }
  }

  .panoramic-monitor-right {
    width: 20%;
    position: absolute;
    top: 84px;
    right: 22px;
    height: calc(100% - 84px);
    display: flex;
    flex-direction: column;
    box-sizing: border-box;

    .item-right {
      padding-bottom: 20px;
    }
  }
}
</style>
//页面的全局配置
<style lang="scss">
.panoramic-monitor {
  @font-face {
    font-family: AGENCYB;
    src: url("~@/assets/fonts/AGENCYB.TTF") format("truetype");
  }

  @font-face {
    font-family: zihun35hao-jindianyahei;
    src: url("~@/assets/fonts/yahei.ttf") format("truetype");
  }

  .flex_s_c {
    display: flex;
    align-items: center;
    justify-content: flex-start;
  }

  .flex_b_c {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .flex_e_c {
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }

  .flex_c_c {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .flex_a_c {
    display: flex;

    align-items: center;
    justify-content: space-around;
  }
}
</style>