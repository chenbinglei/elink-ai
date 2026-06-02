<template>
  <div class="csChargingPileDetails">
    <template v-if="routeMenuList && routeMenuList.length">
      <Tabs v-model:tabsIndex="componentName" :tabsArray="routeMenuList" label="label"></Tabs>
      <div class="tableContent">
        <component :is="componentName" ref="componentRef" :routeInfo="routeInfo" @changEvent="changEvent"></component>
      </div>
    </template>
    <template v-else><null-data words="请联系管理员开放权限！！！"></null-data></template>
  </div>
</template>

<script>
import {getLeftTreeDataFun} from "@/utils";
import {onMounted, reactive, toRefs, defineComponent, getCurrentInstance} from "vue";
import {ElectricPileInfoCom, RealTimeStatusCom, ChargingRateCom, DisChargingRateCom, OccupancyRateCom, FirmwareInfoCom} from "./CsChargingPileDetails/index";

export default defineComponent({
  name: "CsChargingPileDetails",
  components: {ElectricPileInfoCom, RealTimeStatusCom, ChargingRateCom, DisChargingRateCom, OccupancyRateCom, FirmwareInfoCom},
  props: {
    routeInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup() {

    const {emit} = getCurrentInstance();
    const that = reactive({
      routeMenuList: [],
      componentName: "",
    });

    // 获取左侧树结构的数据
    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun("/operationManagement/CsChargingPileDetails", 0);
      // console.log(that.routeMenuList);
    };

    const changEvent = (data)=>{
      emit("changEvent",data);
    };

    onMounted(() => {
      findRouteMenuListFun();
    });

    return {...toRefs(that),findRouteMenuListFun,changEvent};
  }
});
</script>

<style lang="scss" scoped>
.csChargingPileDetails{
  height: 100%;
  display: flex;
  flex-direction: column;

  .tableContent{
    flex: 1;
    height: 2px;
    flex-basis: auto;
    overflow-y: auto;
    padding-top: 12px;
    box-sizing: border-box;
  }
}
</style>