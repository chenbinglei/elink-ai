<template>
  <MonitoringThirdLevelRoutingCom ref="monitoringThirdLevelRoutingComRef" :code="code" :routeName="routeName" :siteId="siteId"/>
</template>

<script>
import {defineComponent, reactive, toRefs, ref} from "vue";
import MonitoringThirdLevelRoutingCom from "./MonitoringThirdLevelRoutingCom.vue";

export default defineComponent({
  name: "ElectricPileMonitoring",
  components: {MonitoringThirdLevelRoutingCom},
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
    code: {
      type: String,
      default: ""
    },
  },
  setup() {

    const that = reactive({
      routeName: "ElectricPileMonitoring"
    });

    // 自动切换路由
    const monitoringThirdLevelRoutingComRef = ref(null);
    const switchRouteNameFun = (routeList = [])=>{
      // console.log(routeList);
      if(routeList && routeList.length){
        monitoringThirdLevelRoutingComRef.value.componentName = routeList[0].name;
      }
    };

    return { ...toRefs(that), monitoringThirdLevelRoutingComRef, switchRouteNameFun };
  }
});
</script>

<style lang="scss" scoped>
</style>