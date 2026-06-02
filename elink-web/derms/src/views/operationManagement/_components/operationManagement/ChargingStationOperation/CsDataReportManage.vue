<template>
  <div class="app-container-right">
    <template v-if="routeMenuList && routeMenuList.length">
      <Tabs v-model:tabsIndex="componentName" :tabsArray="routeMenuList" class="tabs_no_line" label="label"></Tabs>
      <component :is="componentName" :routeName="componentName"></component>
    </template>
    <template v-else>
      <null-data words="请联系管理员开放权限！！！"></null-data>
    </template>
  </div>
</template>

<script>
import {getLeftTreeDataFun} from "@/utils";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import {CsPowerStationReport, CsElectricPileReport, CsChannelReport, CsFleetReport} from "./CsDataReportManage/index";

export default defineComponent({
  name: "CsDataReportManage",
  components: {CsPowerStationReport, CsElectricPileReport, CsChannelReport, CsFleetReport},
  setup() {
    const that = reactive({
      routeMenuList: [],
      componentName: "",
    });

    // 获取用户页面权限
    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun("/operationManagement/CsDataReportManage", 0);
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
:deep(.tabs_no_line) {
  .tabs_li {
    font-size: 16px !important;
  }

  .tabs_slide {
    display: none;
  }
}
</style>