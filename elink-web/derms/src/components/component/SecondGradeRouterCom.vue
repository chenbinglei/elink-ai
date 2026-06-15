<template>
  <div class="app-container">
    <template v-if="routeMenuList && routeMenuList.length">
      <div class="content_body_top">
        <text-tabs
          v-model:tabsIndex="componentName"
          :tabsArray="routeMenuList"
          label="label"
        ></text-tabs>
      </div>
      <div class="content_body_bottom">
        <component
          :is="componentName"
          v-model:routeName="componentName"
          v-model:routeInfo="routeInfo"
          @changeEvent="changeEvent"
        ></component>
      </div>
    </template>
    <null-data v-else words="请联系管理员开放权限！！！"></null-data>
  </div>
</template>

<script lang="ts">
import { useRoute } from "vue-router";
import { getLeftTreeDataFun } from "@/utils";
import { reactive, defineComponent, toRefs, onMounted } from "vue";
import {
  EMControlStrategy,
  EMStrategyTemplate,
  EMControlLogs,
} from "@/views/centralMonitoring/_components";
import {
  ChargingStationOperation,
  EnergyStorageOperation,
  PhotovoltaicOperation,
} from "@/views/operationManagement/_components";

export default defineComponent({
  name: "SecondGradeRouterCom",
  components: {
    EMControlStrategy,
    EMStrategyTemplate,
    EMControlLogs,
    ChargingStationOperation,
    EnergyStorageOperation,
    PhotovoltaicOperation,
  },
  setup() {
    const route = useRoute();
    const that = reactive({
      routeInfo: {},
      routeMenuList: [],
      componentName: "",
    });

    // 获取左侧树结构的数据
    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun(route.path, 0, 1);
      // console.log(that.handleMenuArray);
    };

    const changeEvent = (data) => {
      // 跳转组件页面
      if (data.operateType === "switchComponents") {
        that.routeInfo = data;
        that.componentName = data.componentName;
      }
    };

    onMounted(() => {
      findRouteMenuListFun();
    });

    return { ...toRefs(that), findRouteMenuListFun, changeEvent };
  },
});
</script>

<style lang="scss" scoped>
.app-container {
  position: relative;
  flex-direction: column;

  .content_body_top {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: absolute;
    top: -35px;
    left: 0;
    z-index: 100;
  }

  .content_body_bottom {
    flex: 1;
    height: 2px;
    flex-basis: auto;
    box-sizing: border-box;
  }
}
</style>