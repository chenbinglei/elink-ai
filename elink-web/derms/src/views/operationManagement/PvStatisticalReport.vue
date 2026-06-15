<template>
  <!-- 光伏电站报表 -->
  <div class="app-container">
    <div class="app-container-right">
      <template v-if="routeMenuList && routeMenuList.length">
        <Tabs :tabsArray="routeMenuList" v-model:tabsIndex="componentName" label="label"></Tabs>

        <component :is="componentName" :componentName="componentName"></component>
      </template>
      <template v-else><null-data words="请联系管理员开放权限！！！"></null-data></template>
    </div>
  </div>
</template>

<script lang="ts">
import { getLeftTreeDataFun } from "@/utils";
import { onMounted, reactive, toRefs, defineComponent } from "vue";
import PvInverterReport from './_components/operationManagement/PhotovoltaicOperation/PvStatisticalReport/PvInverterReport.vue';
import PvPowerStationReport from './_components/operationManagement/PhotovoltaicOperation/PvStatisticalReport/PvPowerStationReport.vue';

export default defineComponent({
  name: "PvStatisticalReport",
  components: { PvPowerStationReport, PvInverterReport },
  setup () {

    const that = reactive({
      routeMenuList: [],
      componentName: "",
    });

    // 获取用户页面权限
    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun("/operationManagement/PvStatisticalReport", 0);
      // console.log(that.routeMenuList);
    };

    onMounted(() => {
      findRouteMenuListFun();
    });

    return { ...toRefs(that), findRouteMenuListFun };
  }
});
</script>

<style lang="scss" scoped>
.app-container-right {
  width: 100%;
  height: 100%;
}
</style>