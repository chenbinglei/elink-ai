<template>
  <div class="app-container-right">
    <template v-if="handleMenuArray && handleMenuArray.length">
      <Tabs v-model:tabsIndex="componentName" :tabsArray="handleMenuArray" label="label"></Tabs>
      <div class="tableContent">
        <component :is="componentName" :authority="authority" :siteId="siteId"></component>
      </div>
    </template>
    <div v-else class="null-data"><null-data words="请联系管理员开放权限！！！"></null-data></div>
  </div>
</template>

<script lang="ts">
import {getLeftTreeDataFun} from "@/utils";
import {defineComponent, onMounted, reactive, toRefs} from "vue";
import {CsDisChargingPrice, CsNetworkTopology, CsSiteInformation, CsChargingPrice, CsWhiteList, CsOccupyingPilePrice} from "./CsStationDetails/index";

export default defineComponent({
  name: "CsStationDetails",
  components: {CsDisChargingPrice, CsNetworkTopology, CsSiteInformation, CsChargingPrice, CsWhiteList, CsOccupyingPilePrice},
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
    //权限 1-只读 2-读写
    authority: {
      type: Number,
      default: 1
    },
  },
  setup() {

    const that = reactive({
      componentName: "",
      handleMenuArray: [],
    });

    // 获取顶部菜单权限列表
    const findLeftTreeDataFun = () => {
      // let componentName = route.params?.componentName || history.state?.componentName;
      // if(componentName) that.componentName = componentName;
      that.handleMenuArray = getLeftTreeDataFun("/operationManagement/CsStationDetails");  
      console.log(that.handleMenuArray);
    };

    onMounted(() => {
      findLeftTreeDataFun();
    });

    return {...toRefs(that), findLeftTreeDataFun};
  }
});

</script>

<style lang="scss" scoped>
.null-data {
  height: 100%;
}
</style>