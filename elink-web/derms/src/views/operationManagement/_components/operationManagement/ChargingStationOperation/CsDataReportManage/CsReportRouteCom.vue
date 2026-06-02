<template>
  <div style="width: 100%;height: 97.5%;">
    <div class="app-container-right">
      <template v-if="routeMenuList && routeMenuList.length">
        <div class="content_border content_top">
          <Tabs v-model:tabsIndex="componentName" :tabsArray="routeMenuList" label="label"></Tabs>
        </div>
        <component :is="componentName" :componentName="componentName"></component>
      </template>
      <template v-else>
        <null-data words="请联系管理员开放权限！！！"></null-data>
      </template>
    </div>
  </div>
</template>

<script>
import { getLeftTreeDataFun } from "@/utils";
import { reactive, toRefs, defineComponent, watch } from "vue";

// 电站报表
import CsStationRunReport from "./CsPowerStationReport/CsStationRunReport.vue";
import CsStationV2GReport from "./CsPowerStationReport/CsStationChargingAndDisReport.vue";
import CsStationChargingReport from "./CsPowerStationReport/CsStationChargingAndDisReport.vue";

// 电桩报表
import CsPileRunReport from "./CsElectricPileReport/CsPileRunReport.vue";
import CsPileV2GReport from "./CsElectricPileReport/CsPileChargingAndDisReport.vue";
import CsPileChargingReport from "./CsElectricPileReport/CsPileChargingAndDisReport.vue";

// 渠道报表
import CsChannelSumStatistics from "./CsChannelReport/CsChannelSumStatistics.vue";
import CsChannelChargingDetails from "./CsChannelReport/CsChannelChargingDetails.vue";

export default defineComponent({
  name: "CsReportRouteCom",
  components: {
    CsStationChargingReport, CsStationV2GReport, CsStationRunReport, CsPileChargingReport, CsPileV2GReport, CsPileRunReport,
    CsChannelSumStatistics, CsChannelChargingDetails
  },
  props: {
    routeName: {
      type: String,
      default: ""
    }
  },
  setup (props) {

    const that = reactive({
      routeMenuList: [],
      componentName: "",
    });

    // 获取用户页面权限
    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun(`/operationManagement/${props.routeName}`, 0);
      // console.log(that.routeMenuList);
    };

    const watchRouteName = watch(() => props.routeName, (newRouteName) => {
      if (newRouteName) findRouteMenuListFun();
    }, { deep: true, immediate: true });

    return { ...toRefs(that), watchRouteName, findRouteMenuListFun };
  }
});
</script>

<style lang="scss" scoped>
.app-container-right {
  padding-top: 12px;
  box-sizing: border-box;

  .content_top {
    box-sizing: border-box;
    padding: 14px 16px 0 10px;

    :deep(.tabs) {
      .tabs_slide {
        background: none;
      }
    }
  }
}
</style>