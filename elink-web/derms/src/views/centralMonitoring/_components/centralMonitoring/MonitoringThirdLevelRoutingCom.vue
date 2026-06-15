<template>
  <div class="content_class_body">
    <template v-if="tabsArray && tabsArray.length">
      <TextTabs :tabsIndex="componentName" :tabsArray="tabsArray" isTableClass="bgTableClass" label="label"></TextTabs>
      <div class="monitoring_body flex-all">
        <component :is="componentName" :code="code" :siteId="siteId" :siteName="siteName"></component>
      </div>
    </template>
    <null-data v-else words="请联系管理员打开对应权限！"></null-data>
  </div>
</template>

<script lang="ts">
import {useRoute} from "vue-router";
import {getLeftTreeDataFun} from "@/utils";
import PhotovoltaicTopologyMonitoring from "./WebCanvasCom.vue";
import ElectricPileTopologyMonitoring from "./WebCanvasCom.vue";
import EnergyStorageTopologyMonitoring from "./WebCanvasCom.vue";
import {reactive, defineComponent, toRefs, onMounted} from "vue";
import {ChargingStationMonitoring, ChargingPileMonitoring} from "./ElectricPileMonitoring/index";
import {PhotovoltaicStationMonitoring, PhotovoltaicInverterMonitoring} from "./PhotovoltaicMonitoring/index";
import {EnergyStorageStationMonitoring, EnergyStoragePCSMonitoring, EnergyStorageBatteryMonitoring, EnergyStorageAssistantMonitoring} from "./EnergyStorageMonitoring/index";

export default defineComponent({
  name: "MonitoringThirdLevelRoutingCom",
  components: {PhotovoltaicTopologyMonitoring, PhotovoltaicStationMonitoring, PhotovoltaicInverterMonitoring, ElectricPileTopologyMonitoring, ChargingStationMonitoring,
    ChargingPileMonitoring, EnergyStorageTopologyMonitoring, EnergyStorageStationMonitoring, EnergyStoragePCSMonitoring, EnergyStorageBatteryMonitoring,
    EnergyStorageAssistantMonitoring},
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
    siteName: {
      type: String,
      default: ""
    },
    code: {
      type: String,
      default: ""
    },
    routeName: {
      type: String,
      default: ""
    },
  },
  setup(props) {
    const route = useRoute();
    const that = reactive({
      tabsArray: [],
      componentName: "",
    });

    const queryTabsArrayFun = () => {
      let routePathArray = route.path.split("/");
      routePathArray[routePathArray.length - 1] = props.routeName;
      let routePath = routePathArray.join("/");
      that.tabsArray = getLeftTreeDataFun(routePath, 0, 1);
    };

    onMounted(() => {
      queryTabsArrayFun();
    });

    return {...toRefs(that), queryTabsArrayFun};
  }
});
</script>

<style lang="scss" scoped>
.content_class_body {
  height: 100%;
  display: flex;
  flex-direction: column;

  .monitoring_body {
    height: 2px;
  }
}
</style>