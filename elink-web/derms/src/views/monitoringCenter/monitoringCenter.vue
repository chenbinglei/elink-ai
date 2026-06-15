<template>
  <div class="body">
    <div class="menu">
      <!-- <div :class="activeMenu == index ? 'handleMenus' : 'handleMenus_active'" @click="activeMenu = index"
        v-for="(item, index) in handleMenusArray" :key="index">
        <span :class="activeMenu == index ? 'handleMenus_span' : 'handleMenus_span_active'">{{ item }}</span>
      </div> -->
      <div :class="activeMenu == index ? 'handleMenus' : 'handleMenus_active'" @click="activeMenu = index,activuSiteMeau=item"
        v-for="(item, index) in filteredMenus" :key="index">
        <span :class="activeMenu == index ? 'handleMenus_span' : 'handleMenus_span_active'">{{ item }}</span>


      </div>
      <div class="status">
        <div class="status_01"></div>
        <div class="status_text">
          <span>正常投运 </span>
          <div class="status_num_01">
            {{ statisticsData?.["正常投运"] ?? "" }}
          </div>
        </div>
        <div class="status_02"></div>
        <div class="status_text">
          <span>维护中</span>
          <div class="status_num_02">
            {{ statisticsData?.["维护中"] ?? "" }}
          </div>
        </div>
        <div class="status_03"></div>
        <div class="status_text">
          <span>关闭下线</span>
          <div class="status_num_03">
            {{ statisticsData?.["关闭下线"] ?? "" }}
          </div>
        </div>
      </div>
      <div class="status2">
        <div class="status_04"></div>
        <div class="status2_text no-dot" style="margin-left: 10px">
          <span>需关注</span>
          <div style="color: #00ccffff">
          </div>
        </div>
        <div class="status2_text">
          <span>设备故障</span>
          <div style="color: #ff0000ff">
            {{ statisticsData?.["设备故障"] ?? "" }}
          </div>
        </div>
        <div class="status2_text">
          <span>设备离线</span>
          <div style="color: #aaaaaaff">
            {{ statisticsData?.["设备离线"] ?? "" }}
          </div>
        </div>
      </div>
    </div>
    <!-- 场站 -->
    <Station v-if="activuSiteMeau== '场站'" @update-data="handleActiveSiteIdUpdate" @isVisible="isVisible"></Station>
    <Photovoltaic v-else-if="activuSiteMeau== '光伏'"></Photovoltaic>
    <!-- 储能 -->
    <EnergyStorage v-else-if="activuSiteMeau== '储能'"></EnergyStorage>
    <!-- 充电 -->
    <Charge v-else-if="activuSiteMeau== '充电'"></Charge>
    <!-- 换电 -->
    <SwapBattery v-else-if="activuSiteMeau== '换电'"></SwapBattery>
  </div>
</template>

<script lang="ts">
import { Search } from "@element-plus/icons-vue";
import { defineComponent, onMounted, ref, computed } from "vue";

import Station from "./entralizedMonitoring/station.vue";
import Photovoltaic from "./entralizedMonitoring/photovoltaic.vue";
import EnergyStorage from "./entralizedMonitoring/energyStorage.vue";
import Charge from "./entralizedMonitoring/charge.vue";
import SwapBattery from "./entralizedMonitoring/swapBattery.vue";
import { photovoltaicPage } from "@/api/monitoringCenter/monitoringCenter.js";
import { energyStoragePage } from "@/api/monitoringCenter/monitoringCenter.js";
import { batterySupplyPage } from "@/api/monitoringCenter/monitoringCenter.js";
import { batteryChangePage } from "@/api/monitoringCenter/monitoringCenter.js";
export default defineComponent({
  name: "concentrate",
  emits: ["update:activeSiteId"],
  components: {
    Search,
    Station,
    Photovoltaic,
    EnergyStorage,
    Charge,
    SwapBattery,
  },
  props: {
    activeSiteId: {
      type: [Number, String],
      default: "",
    },
    comeFromName: {
      type: String,
      default: "default",
    },
  },
  setup (props) {
    const handleMenusArray = ref(["场站", "光伏", "储能", "充电", "换电"]);
    const activeMenu = ref(0);
    const activuSiteMeau = ref("场站");

    const statisticsData = ref();
    // const menuVisible = ref(false);
    const menuData = ref({
      0: true, // 场站
      1: false, // 光伏
      2: false, // 储能
      3: false, // 充电
      4: false, // 换电
    });
    function handleActiveSiteIdUpdate (data) {
      statisticsData.value = data;
    }
    const isVisible = (visible, index) => {
      menuData.value[index] = visible;
      // menuVisible.value = true;
      if (!visible) handleMenusArray.value = handleMenusArray.value.filter((item, i) => i !== index);
    }
    const filteredMenus = computed(() => {
      return handleMenusArray.value.filter((_, index) => menuData.value[index]);
    });
    const photoPage = async () => {
      let data = {
        page: 1,
        size: 10,
      };
      const res = await photovoltaicPage(data);
      if (res?.data?.totalSize > 0) {
        menuData.value[1] = true;
      } else {
        menuData.value[1] = false;
      }
      console.log(res?.data?.totalSize, 'guangdu');
    };
    const energyPage = async () => {
      let data = {
        page: 1,
        size: 10,
      };
      const res = await energyStoragePage(data);
      if (res?.data?.totalSize > 0) {
        menuData.value[2] = true;
      } else {
        menuData.value[2] = false;
      }
      console.log(res?.data?.totalSize, 'chuneng');
    };
    const batteryPage = async () => {
      let data = {
        page: 1,
        size: 10,
      };
      const res = await batterySupplyPage(data);
      if (res?.data?.totalSize > 0) {
        menuData.value[3] = true;
      } else {
        menuData.value[3] = false;
      }
      console.log(res?.data?.totalSize, 'chongdian');
    };
    const SwapBatteryPage = async () => {
      let data = {
        page: 1,
        size: 10,
      };
      const res = await batteryChangePage(data);
      if (res?.data?.totalSize > 0) {
        menuData.value[4] = true;
      } else {
        menuData.value[4] = false;
      }
      console.log(res?.data?.totalSize, 'huandian');
    };

    onMounted(() => {
      photoPage();
      energyPage();
      batteryPage();
      SwapBatteryPage();
    });

    return {
      handleMenusArray,
      activeMenu,
      activuSiteMeau,
      handleActiveSiteIdUpdate,
      statisticsData,
      isVisible,
      filteredMenus,
    };
  },
});
</script>
<style lang="scss" scoped>
.body {
  box-sizing: border-box;
  width: 100%;
  background: url("@/assets/image/station-details/sta-detail-bg.png") no-repeat;
  background-size: 100% 100%;
  padding: 0 24px;
  height: 100%;
}

.menu {
  display: flex;
  flex-wrap: nowrap;
  /* 不换行 */
}

.handleMenus {
  width: 148px;
  height: 32px;
  margin-right: -10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: url("../../assets/images/组 10859.png") no-repeat;
  background-size: 100% 100%;
  cursor: pointer;
}

.handleMenus_span {
  font-family: Microsoft YaHei, Microsoft YaHei;
  font-weight: bold;
  font-size: 16px;
  color: #ffffff;
  white-space: nowrap;
  cursor: pointer;
}

.handleMenus_active {
  width: 148px;
  height: 32px;
  margin-right: -10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: url("../../assets/images/组 10861.png") no-repeat;
  background-size: 100% 100%;
  cursor: pointer;
}

.handleMenus_span_active {
  font-family: Microsoft YaHei, Microsoft YaHei;
  font-weight: bold;
  font-size: 16px;
  color: #7dcbff;
  white-space: nowrap;
  cursor: pointer;
}

.status {
  margin-left: auto;
  display: flex;
  align-items: center;
  white-space: nowrap;
  text-align: center;
  width: 432px;
  height: 47px;
  color: white;
  background-repeat: no-repeat;
  background: url("../../assets/images/组 118157.png") no-repeat;
  background-size: 100% 100%;
  padding-left: 20px;
}

.status2 {
  margin-left: auto;
  display: flex;
  align-items: center;
  text-align: center;
  width: 390px;
  height: 47px;
  color: white;
  background-repeat: no-repeat;
  background: url("../../assets/images/组 118158.png") no-repeat;
  background-size: 100% 100%;
}

.status_01 {
  width: 31px;
  height: 31px;
  background: url("../../assets/images/组 118116(1).png") no-repeat;
  background-size: 100% 100%;
}

.status_02 {
  width: 31px;
  height: 31px;
  background: url("../../assets/images/组 118117.png") no-repeat;
  background-size: 100% 100%;
}

.status_03 {
  width: 31px;
  height: 31px;
  background: url("../../assets/images/组 118118.png") no-repeat;
  background-size: 100% 100%;
}

.status_04 {
  margin-left: 16px;
  width: 31px;
  height: 31px;
  background: url("../../assets/images/组 118116.png") no-repeat;
  background-size: 100% 100%;
}

.status_text {
  margin-left: 10px;
  display: flex;
  align-items: center;
  text-align: center;
  font-size: 16px;
}

.status_num_01 {
  font-family: Agency FB, Agency FB;
  font-weight: 400;
  font-size: 24px;
  color: #34e800;
  text-align: left;
  font-style: normal;
  text-transform: none;
  margin-right: 24px;
  margin-left: 10px;
}

.status_num_02 {
  font-family: Agency FB, Agency FB;
  font-weight: 400;
  font-size: 24px;
  color: #e99d45;
  text-align: left;
  font-style: normal;
  text-transform: none;
  margin-right: 24px;
  margin-left: 10px;
}

.status_num_03 {
  font-family: Agency FB, Agency FB;
  font-weight: 400;
  font-size: 24px;
  color: #aaaaaaff;
  text-align: left;
  font-style: normal;
  text-transform: none;
  margin-right: 24px;
  margin-left: 10px;
}

.status2_text {
  display: flex;
  align-items: center;
  font-size: 16px;
  white-space: nowrap;
  position: relative;

  span {
    color: #ffffff;
    /* 白色文字 */
    font-weight: normal;
    margin-right: 8px;
    /* 和数字之间留点空隙 */
  }

  div {
    font-family: Agency FB, Agency FB;
    font-weight: 400;
    font-size: 24px;
    text-align: left;
    font-style: normal;
    text-transform: none;
    margin-right: 24px;
    margin-left: 10px;
  }
}

.status2_text:not(.no-dot)::before {
  content: "";
  position: absolute;
  left: -12px;
  /* 调整小圆点与文字的距离 */
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  border-radius: 50%;
  /* 创建圆形 */
}

.status2_text:nth-child(3)::before {
  background-color: #ff0000;
  /* 设备故障的颜色 */
}

.status2_text:nth-child(4)::before {
  background-color: #aaaaaa;
  /* 设备离线的颜色 */
}

.status2_text:nth-child(5)::before {
  background-color: #9671ff;
  /* 低效运行的颜色 */
}
</style>
