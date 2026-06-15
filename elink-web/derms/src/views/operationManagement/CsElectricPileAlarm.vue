<template>
  <!-- 电桩告警 -->
  <div class="app-container">
    <component ref="componentRef" :is="componentName" :componentName="componentName" :routeInfo="routeInfo"
      @changEvent="changEvent"></component>
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, reactive, toRefs } from "vue";
import SecondaryDetailsCom from "./_components/operationManagement/SecondaryDetailsCom.vue";
import {
  CsPileGunRunningStatus, CsStationManagement, CsChargingRecord, CsDisChargingRecord, CsUserFeedback, CsPileOccupationRecord, CsSettlementManagement,
  CsUserGrouping, CsMiniProgramUsers, CsTransactionDetails, CsInvoiceManage, CsElectricPileLog, CsElectricPileAlarm, CsPileGunManagement, CsBusinessAnalysis,
  CsDataReportManage
} from "./_components/operationManagement/ChargingStationOperation/index";

export default defineComponent({
  name: "CsElectricPileLog",
  components: {
    SecondaryDetailsCom,  CsPileGunRunningStatus, CsStationManagement, CsChargingRecord, CsDisChargingRecord, CsUserFeedback,
    CsPileOccupationRecord, CsSettlementManagement, CsUserGrouping, CsMiniProgramUsers, CsTransactionDetails, CsInvoiceManage, CsElectricPileLog, CsElectricPileAlarm,
    CsPileGunManagement, CsBusinessAnalysis, CsDataReportManage
  },
  setup () {

    const that = reactive({
      routeMenuList: [],
      componentName: "CsElectricPileAlarm",
    });


    onMounted(() => {
    });

    return { ...toRefs(that), };
  }
});
</script>

<style lang="scss" scoped>
.content_top {
  padding-top: 12px;
  margin-bottom: 12px;
  box-sizing: border-box;
}
</style>