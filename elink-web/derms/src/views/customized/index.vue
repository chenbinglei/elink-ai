<template>
  <div class="container-screen">
    <!-- 标题层：固定高度比例 -->
    <div class="custom-title">
      <div class="custom-title-tips"></div>
      <div class="custom-title-bgc">
        <div class="custom-title-tip"></div>
      </div>
      <div class="custom-title-timeBtn">
        <div class="custom-title-timeBtn-time">
          <div class="custom-title-timeBtn-date">{{ formattedTime }}</div>
          <div class="custom-title-timeBtn-year">{{ formattedDate }}</div>
        </div>
        <div class="custom-title-timeBtn-icon" @click="goLaster()">
          <img src="/src/assets/customized/close.png" alt="">
        </div>
      </div>
    </div>

    <!-- 内容主体层：自动占满剩余高度 -->
    <div class="custom-body">
      <!-- 地图层：永远居中不变形 -->

      <div class="map-box">
        <CustomizedCenter @ImmDialogVisible="goDialog" :websoctList="websoctList" :site="websoctList.siteId"
          :ImmDialogVisible="ImmDialogVisible" />




      </div>
      <!-- 左右数据层：高度自动适配，绝不位移 -->
      <div class="custom-content">
        <div class="custom-content-left">
          <CustomizedLeft style="height: 100%;width: 100%;" :websoctList="websoctList" :site="websoctList.siteId" />

        </div>
        <div class="custom-content-center">
          <div class="custom-content-containers">
            <div class="custom-item-title">功率曲线分析
              <div class="power-curve">
                <div class="stat-item" :class="{ active: item.id === chooseTimeType }" v-for="item in timeTypeList"
                  :key="item.id" @click="chooseTimeType = item.id">
                  {{ item.name }}
                </div>
              </div>
            </div>
            <lineCharts style="width:100%;height:calc(100% - 30px);position:absolute;bottom:0;z-index:10"
              :websoctList="websoctList" :chooseTimeType="chooseTimeType" />
          </div>


        </div>
        <div class="custom-content-right">
          <CustomizedRight style="height: 100%;width: 100%;" :websoctList="websoctList" :site="websoctList.siteId" />
        </div>
      </div>
    </div>
    <ImmDialog :isVisible="ImmDialogVisible" @close="goDialogClose" :immList="immList" />

  </div>
</template>

<script setup>
import { formatDateTime } from "@/utils/dateTime";
import { ref, onMounted, onUnmounted } from 'vue';
import ImmDialog from './customizedCompont/immDialog.vue'
import WebSocketManager from "@/utils/websocket.js";
import CustomizedLeft from './customizedCompont/customizedLeft.vue'
import CustomizedRight from './customizedCompont/customizedRight.vue'
import lineCharts from './customizedCompont/lineCharts.vue';
import CustomizedCenter from './customizedCompont/customizedCenter.vue';
import {
  customSystemWebSocket,
} from "@/api/websocket/webSocket.js";
const formattedTime = ref('')
const formattedDate = ref('')
// ==================== 弹窗相关 ====================
const chooseTimeType = ref(1)
const timeTypeList = ref([
  { id: 1, name: '今日' },
  { id: 2, name: '昨日' },
])
const immList = ref([])
const powerList = ref([])
const websoctList = ref([])


const ImmDialogVisible = ref(false)
const chooseName = ref('')

const goDialog = (visible, type) => {
  console.log('数据一点击***', visible, type)
  if (visible) {
    chooseName.value = type

    ImmDialogVisible.value = visible
    immList.value = type
  }

}

// ==================== 地图点击 ====================
const onMapClick = (e) => {
  const id = e.target.dataset.id
  if (id) {
    console.log('点击设备：', id)
  }
}

// ==================== WebSocket 核心修复 ====================
let socketManager = null; // 用 let 全局挂载，不被响应式回收
let intervalId = 0;
// 页面挂载后建立连接
onMounted(() => {
  intervalId = setInterval(() => {
    formattedTime.value = formatDateTime(new Date(), "HH:mm:ss");
    formattedDate.value = formatDateTime(new Date(), "YYYY-MM-DD");
  }, 1000); // 1000毫秒，即1秒
  if (!socketManager) {
    socketManager = new WebSocketManager();

    socketManager.addConnection("chat", customSystemWebSocket(), {
      onMessage: (data) => {
        try {
          const realData = JSON.parse(data); // 🔥 修复：定义变量
          websoctList.value = realData
          if (chooseTimeType === 1) {
            powerList.value = [
              realData.powerCurveAnalysis.pvTodayPowerList,
              realData.powerCurveAnalysis.seTodayPowerList,
              realData.powerCurveAnalysis.loadTodayPowerList,
              realData.powerCurveAnalysis.busTodayPowerList,
            ]
          }

          console.log("WebSocket 收到消息：", realData);
        } catch (err) {
          console.log("消息非JSON：", data);
        }
      },
      onClose: () => {
        console.log("chat 连接关闭");
      },
      onError: (err) => {
        console.error("WebSocket 错误：", err);
      }
    });
  }
});
const goLaster = () => {
  window.close(); // 只能关闭通过 window.open 打开的页面
}

const goDialogClose = () => {
  console.log('关闭弹窗')

  ImmDialogVisible.value = false
}
// 页面卸载时关闭连接
onUnmounted(() => {
  if (socketManager) {
    socketManager.closeAll();
    socketManager = null;
  }
  clearInterval(intervalId);
});

// 页面关闭/刷新时关闭
window.addEventListener("beforeunload", () => {
  if (socketManager) {
    console.log("页面即将关闭，关闭 WebSocket...");
    socketManager.closeAll();
  }
});
</script>

<style scoped lang="scss">
.container-screen {
  width: 100%;
  height: 100vh;
  /* 满屏高度，永远不滚动 */
  display: flex;
  flex-direction: column;
  /* 上下分层：标题 + 主体 */
  overflow: hidden;
  /* 永久隐藏滚动条 */
  background: url(/src/assets/image/station-details/sta-detail-bg.png) no-repeat;
  background-size: 100% 100%;
  position: relative;
  box-sizing: border-box;
}

.custom-title {
  flex-shrink: 0;
  /* 关键：禁止被压缩 */
  height: 7%;
  /* 保持你原来的比例 */
  background: url(/src/assets/customized/title.png) no-repeat;
  background-size: 100% 100%;
  display: flex;
  padding: 0 24px;
  justify-content: space-between;
  align-items: center;

  .custom-title-tips {
    background: url(/src/assets/customized/tips.png) no-repeat;
    background-size: 100% 100%;
    width: 8%;
    height: 42%;
  }

  .custom-title-bgc {
    width: 35%;
    height: 100%;
    background: url(/src/assets/customized/title-center.png) no-repeat;
    background-size: 100% 100%;
    position: relative;
  }

  .custom-title-tip {
    background: url(/src/assets/customized/title-center-tip.png) no-repeat;
    background-size: 100% 100%;
    width: 60.4%;
    height: 39%;
    position: absolute;
    top: 40%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .custom-title-timeBtn {
    display: flex;
    align-items: center;
    color: #fff;
    gap: 12px;


    .custom-title-timeBtn-time {
      text-align: center;
      font-weight: 500;

      .custom-title-timeBtn-date {
        font-size: 24px;
      }

      .custom-title-timeBtn-year {
        font-size: 17px;
      }
    }

    .custom-title-timeBtn-icon {
      width: 36px;
      height: 36px;
      background: url(/src/assets/customized/close-circle.png) no-repeat;
      background-size: 100% 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      pointer-events: auto;

      img {
        width: 12px;
        height: 12px;
      }
    }
  }
}

/* ########## 主体层：自动占满剩余高度，核心层 ########## */
.custom-body {
  flex: 1;
  /* 自动填满剩余高度 */
  position: relative;
  padding: 0 24px;
  box-sizing: border-box;
}

/* ########## 地图：垂直居中，永远不随控制台位移 ########## */
.map-box {
  position: absolute;
  top: 36%;
  left: 50%;
  transform: translate(-50%, -50%);
  /* 完美居中，永不抖动 */
  width: 67.6%;
  aspect-ratio: 1298 / 730;
  z-index: 5;
  pointer-events: auto;
  /* 确保可点击 */


}

/* ########## 左右内容层：高度自动100%，永远不位移 ########## */
.custom-content {
  width: 100%;
  height: 100%;
  display: flex;
  position: relative;
  // z-index: 6;

  .custom-content-left {
    width: 18%;
    height: 100%;
    /* 占满父级高度，稳定不抖 */
  }

  .custom-content-center {
    flex: 1;
    height: 100%;
    display: flex;
    flex-direction: column;
    padding-top: 12px;
    box-sizing: border-box;
    justify-content: end;
    // z-index: 6;

    /* 下方图表模块：带固定底部距离 */
    .custom-content-containers {
      width: 98%;
      height: 24%;
      /* 固定高度比例 */
      margin: 0 auto;
      margin-bottom: 30px;
      /* 🔥 关键：距离底部固定空白 */
      background: url(/src/assets/customized/power-bg.png) no-repeat;
      background-size: 100% 100%;
      position: relative;

      .custom-item-title {
        font-family: YouSheBiaoTiHei, Microsoft YaHei;
        font-size: 20px;
        color: #ffffff;
        text-align: left;
        font-weight: 400;
        padding-left: 5%;
        padding-top: 0.3%;
        position: relative;
        z-index: 10;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .power-curve {
          display: flex;
          align-items: center;
          color: #54a1df;
          border: 1px solid #0071cc;
          font-family: Microsoft YaHei, Microsoft YaHei;
          font-weight: bold;
          border-radius: 4px;
          font-size: 14px;
          margin-right: 16px;
          overflow: hidden;
          cursor: pointer;

          .active {
            font-weight: bold;
            color: #fff;
            background: linear-gradient(180deg, #54a1df 0%, #005599 82.42%, #40a5fe 100%);
          }

          .stat-item {
            padding: 4px 19px;
          }
        }
      }
    }
  }

  .custom-content-right {
    width: 18%;
    height: 100%;
  }
}
</style>