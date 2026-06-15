<template>
  <div class="app-container-right">
    <template v-if="routeMenuList && routeMenuList.length">
      <div class="content_top">
        <Tabs :tabsArray="routeMenuList" v-model:tabsIndex="componentName" label="label"></Tabs>
      </div>
      <component :is="componentName" :componentName="componentName"></component>
    </template>
    <template v-else><null-data words="请联系管理员开放权限！！！"></null-data></template>
  </div>
</template>

<script lang="ts">
import {getLeftTreeDataFun} from "@/utils";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import PvInverterReport from './PvStatisticalReport/PvInverterReport.vue';
import PvPowerStationReport from './PvStatisticalReport/PvPowerStationReport.vue';

export default defineComponent({
  name: "PvStatisticalReport",
  components: { PvPowerStationReport, PvInverterReport },
  setup() {

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

    return {...toRefs(that), findRouteMenuListFun};
  }
});
</script>

<style lang="scss" scoped>
</style>