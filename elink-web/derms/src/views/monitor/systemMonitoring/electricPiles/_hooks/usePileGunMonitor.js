import { ref, computed, onUnmounted, watch } from "vue";
import { useStore } from "vuex";
import { pileRealWebSocket } from "@/api/websocket/webSocket";
import { ElMessage } from "element-plus";

import WSMonitor from "@/utils/monitor";

export default function usePileGunMonitor({ pileCode }) {
  const store = useStore();
  const userInfo = computed(() => {
    return store.state.app.userInfo;
  });

  let wsInstance;
  const pileRealtimeData = ref({});
  const gunRealtimeDataMap = ref({});

  const onOpen = () => {};
  const onMessage = (data) => {
    const { gunRealModelList = [], ...rest } = data;
    pileRealtimeData.value = rest;
    gunRealtimeDataMap.value = gunRealModelList.reduce((map, item) => {
      map[item.gunCode] = item;
      return map;
    }, {});
  };
  const onError = () => {};
  const onClose = (e) => {
    console.log(e);
  };

  const initWs = () => {
    if (typeof WebSocket === "undefined") {
      ElMessage({
        type: "error",
        showClose: true,
        message: "您的浏览器不支持websocket",
      });
      return;
    }
    wsInstance = new WSMonitor({
      url: pileRealWebSocket({
        userId: userInfo.value.userId,
        pileCode: pileCode.value,
      }),
      retryCount: 5,
      listener: {
        onMessage,
        onOpen,
        onClose,
        onError,
      },
    });
  };
  const disconnectWs = () => {
    wsInstance?.disconnect();
    wsInstance = null;
  };

  watch(pileCode, () => {
    if (!pileCode.value) {
      return;
    }
    initWs();
  });
  onUnmounted(() => {
    disconnectWs();
  });

  return {
    gunRealtimeDataMap,
  };
}
