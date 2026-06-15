<template>
  <div class="app-container">
    <div class="content_body_bottom">
      <component
        :is="componentName"
        v-model:routeName="componentName"
        v-model:routeInfo="routeInfo"
        @changeEvent="changeEvent"
      ></component>
    </div>
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
      componentName: "EMStrategyTemplate",
    });

    // 获取左侧树结构的数据
    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun(
        "/centralMonitoring/energyManagement",
        0,
        1
      );
    };
    console.log(route.path);
    console.log(that.routeMenuList);
    const changeEvent = (data) => {
      console.log(1);
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