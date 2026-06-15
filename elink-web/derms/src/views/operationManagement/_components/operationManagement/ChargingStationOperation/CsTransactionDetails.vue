<template>
  <div class="app-container-right">
    <template v-if="routeMenuList && routeMenuList.length">
      <Tabs v-model:tabsIndex="componentName" label="label" :tabs-array="routeMenuList"></Tabs>
      <component :is="componentName" :routeInfo="routeInfo" @changEvent="changEvent"></component>
    </template>
    <null-data v-else words="请联系管理员开放权限！！！"></null-data>
  </div>
</template>

<script lang="ts">
import {getLeftTreeDataFun} from "@/utils";
import {defineComponent, getCurrentInstance, onMounted, reactive, toRefs} from "vue";
import CsTransactionChargingOrder from "./CsTransactionDetails/CsTransactionChargingOrder.vue";
import CsTransactionOccupancyOrder from "./CsTransactionDetails/CsTransactionOccupancyOrder.vue";
import CsTransactionV2GWalletOrder from "./CsTransactionDetails/CsTransactionV2GWalletOrder.vue";

export default defineComponent({
  name: "CsTransactionDetails",
  props: {
    routeInfo: {
      type: Object,
      default: ()=>{
        return { };
      }
    },
  },
  components:{CsTransactionChargingOrder,CsTransactionV2GWalletOrder,CsTransactionOccupancyOrder},
  setup(props) {

    const {emit} = getCurrentInstance();

    const that = reactive({
      componentName: "",
      routeMenuList: [],
    });

    const changEvent = (data)=>{
      emit("changEvent",data);
    };

    const findRouteMenuListFun = () => {
      // console.log(props.routeInfo);
      if(props.routeInfo.childComponentName) that.componentName = props.routeInfo.childComponentName;
      that.routeMenuList = getLeftTreeDataFun("/operationManagement/CsTransactionDetails", 0);
      // console.log(that.handleMenuArray);
    };

    onMounted(() => {
      findRouteMenuListFun();
    });

    return {...toRefs(that), findRouteMenuListFun, changEvent};
  }
});
</script>

<style lang="scss" scoped>

</style>