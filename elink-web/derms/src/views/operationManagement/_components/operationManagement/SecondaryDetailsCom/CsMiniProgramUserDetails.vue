<template>
  <div class="app-container-right">
    <Tabs v-model:tabsIndex="componentName" :tabsArray="handleMenuArray"></Tabs>
    <div class="tableContent">
      <component :is="componentName" :routeInfo="routeInfo" @changEvent="changEvent"></component>
    </div>
  </div>
</template>

<script>
import {defineComponent, getCurrentInstance, reactive, toRefs} from "vue";
import MiniProgramUserInfoCom from "./CsMiniProgramUserDetails/MiniProgramUserInfoCom.vue";
import MiniProgramUserWalletCom from "./CsMiniProgramUserDetails/MiniProgramUserWalletCom.vue";
import MiniProgramUserVehiclesCom from "./CsMiniProgramUserDetails/MiniProgramUserVehiclesCom.vue";

export default defineComponent({
  name: "CsMiniProgramUserDetails",
  components: {MiniProgramUserInfoCom, MiniProgramUserWalletCom, MiniProgramUserVehiclesCom},
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
      componentName: "MiniProgramUserInfoCom",
      handleMenuArray: [
        {id: "MiniProgramUserInfoCom", name: "用户信息"},
        {id: "MiniProgramUserWalletCom", name: "V2G钱包"},
        {id: "MiniProgramUserVehiclesCom", name: "关联车辆"},
      ],
    });

    const changEvent = (data)=>{
      emit("changEvent",data);
    };

    return {...toRefs(that),changEvent};
  }
});

</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;
  border: 1px solid #106ec499;
}
</style>