<template>
  <div class="monitoring_content" v-loading="listLoading">
    <DeviceListCom v-model:activeDeviceId="activeDeviceId" :deviceList="pileDeviceList" @changeEvent="changeEvent">
      <template v-slot:status="{ data }">
        <ElectricPileDeviceStatusCom :deviceInfo="data"></ElectricPileDeviceStatusCom>
      </template>
      <template v-slot:content="{ data }">
        <ElectricPileDeviceCardInfo :deviceInfo="data"></ElectricPileDeviceCardInfo>
      </template>
    </DeviceListCom>
    <div class="flex-all content_right">
      <template v-if="pileDeviceList && pileDeviceList.length">
        <ElectricPileDeviceInfo :activeDeviceInfo="activeDeviceInfo" @changeEvent="changeEvent" />
        <div class="chargingGunList flex-all">
          <template v-if="chargingGunList && chargingGunList.length">
            <template v-for="(item,index) in chargingGunList" :key="index">
              <ChargingGunCardCom :gunDataInfo="item" :activeDeviceInfo="activeDeviceInfo" :gunIndex="index"></ChargingGunCardCom>
            </template>
          </template>
          <null-data v-else words="暂无枪数据列表"></null-data>
        </div>
      </template>
      <null-data v-else words="暂无设备数据列表"></null-data>
    </div>
  </div>
</template>

<script lang="ts">
import { useAppStore } from '@/stores/index';

import {ElMessage} from "element-plus";
import {pileRealWebSocket} from "@/api/websocket/webSocket";
import {findPileDeviceList} from "@/api/centralMonitoring/centralMonitoring";
import ChargingGunCardCom from "./ChargingPileMonitoring/ChargingGunCardCom.vue";
import {reactive, defineComponent, toRefs, watch, onUnmounted, computed} from "vue";
import ElectricPileDeviceInfo from "./ChargingPileMonitoring/ElectricPileDeviceInfo.vue";
import {DeviceListCom, ElectricPileDeviceCardInfo, ElectricPileDeviceStatusCom} from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "ChargingPileMonitoring",
  components: {ElectricPileDeviceStatusCom, ElectricPileDeviceCardInfo, ElectricPileDeviceInfo, DeviceListCom, ChargingGunCardCom},
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {
    const appStore = useAppStore();
    const userInfo = computed(() => {
      return appStore.userInfo;
    });

    const that = reactive({
      pileDeviceList: [],
      listLoading: false,
      activeDeviceId: "",
      activeDeviceInfo: {},
      chargingGunList: [], // 充电枪列表

      websocketNum: 1,
      pileMonitorWs: null,
    });

    // 查询光伏逆变器列表
    const queryPileDeviceList = () => {
      that.listLoading = true;
      findPileDeviceList({siteId: props.siteId, timer: new Date()}).then(res => {
        that.pileDeviceList = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.pileDeviceList = [];
        that.listLoading = false;
      });
    };

    const changeEvent = (data) => {
      if (data.operateType === "InverterDeviceInfo") queryPileDeviceList();
      if (data.operateType === "DeviceListCom"){
        closeWebSocketFun();
        that.activeDeviceInfo = JSON.parse(JSON.stringify(data ?? {}));
        if(that.pileDeviceList && that.pileDeviceList.length){
          setTimeout(()=> initWebSocket(),150);
        }
      }
    };

    //初始化 initWebSocket
    const initWebSocket = () => {
      if (typeof WebSocket === 'undefined') {
        ElMessage({ type:"error",showClose: true,message:"您的浏览器不支持websocket" });
        return;
      }

      that.listLoading = true;
      let wsUrl = pileRealWebSocket({
        userId: userInfo.value.userId,
        pileCode: that.activeDeviceInfo.deviceNumber,
      });

      that.pileMonitorWs = new WebSocket(wsUrl);
      that.pileMonitorWs.onopen = webSocketOpen;
      that.pileMonitorWs.onerror = webSocketError;
      that.pileMonitorWs.onclose = webSocketClose;
      that.pileMonitorWs.onmessage = webSocketMessage;
    };

    const webSocketMessage = (message) => {
      try {
        let data = JSON.parse(message.data);
        if (typeof data === "object"){
          // console.log(data);
          that.chargingGunList = data.gunRealModelList;
        }
      } catch (e) {
        //TODO handle the exception
        console.log(e);
      }
    };

    const webSocketOpen = () => {
      that.websocketNum = 0;
      setTimeout(()=>{
        that.listLoading = false;
        console.log('枪桩设备 实时数据连接成功');
      },500);
    };

    const webSocketError = () => {
      // 连接建立失败重连
      that.websocketNum++;
      if (that.websocketNum <= 5) {
        // 延迟三秒在重新进行连接
        setTimeout(() => {
          initWebSocket();
        }, 3000);
      } else {
        that.listLoading = false;
        console.log('枪桩设备 ----> 连接失败!!!');
      }
    };

    // 断开 websocket
    const webSocketClose = (e) => {
      console.log('枪桩设备 ----> 断开连接!!!');
    };

    //关闭websocket
    const closeWebSocketFun = ()=>{
      that.websocketNum = 11;
      that.chargingGunList = [];
      console.log('枪桩设备 ----> 准备断开连接!!!');
      if (that.pileMonitorWs)that.pileMonitorWs.close();
    };

    const watchSiteId = watch(() => props.siteId, (newSiteId) => {
      if (newSiteId) queryPileDeviceList();
    }, {deep: true, immediate: true});

    onUnmounted(() => {
      closeWebSocketFun(); //关闭websock
    });

    return {...toRefs(that),queryPileDeviceList, changeEvent, watchSiteId, initWebSocket, webSocketClose,webSocketError,webSocketOpen, webSocketMessage, closeWebSocketFun};
  }
});
</script>

<style lang="scss" scoped>
.monitoring_content {
  height: 100%;
  display: flex;
  padding: 12px 8px;
  box-sizing: border-box;

  .content_right {
    width: 2px;
    display: flex;
    flex-direction: column;
    padding-left: 12px;
    box-sizing: border-box;
    overflow-y: auto;

    .chargingGunList{
      margin-top: 12px;
    }
  }
}
</style>