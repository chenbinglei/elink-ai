<template>
  <div class="app-container">
    <StationMenuListCom :comeFromName="comeFromName" v-model:activeSiteId="activeSiteId"></StationMenuListCom>
    <StrategyControlCom v-if="activeSiteId" :activeSiteId="activeSiteId" @changeEvent="changeEvent"></StrategyControlCom>
    <null-data v-else words="请先选择站点"></null-data>
  </div>
</template>

<script lang="ts">
import {defineComponent, getCurrentInstance, reactive, toRefs} from "vue";
import StrategyControlCom from "./EMControlStrategy/StrategyControlCom.vue";
import StationMenuListCom from "../centralMonitoring/StationMenuListCom.vue";

export default defineComponent({
  name: "EMControlStrategy",
  components: {StationMenuListCom,StrategyControlCom},
  setup() {
    const {emit} = getCurrentInstance();

    const that = reactive({
      activeSiteId: "",
      comeFromName: "EMControlStrategy",
    });

    const changeEvent = (data)=>{
      emit("changeEvent",data);
    };

    return {...toRefs(that),changeEvent};
  }
});
</script>

<style lang="scss" scoped>
.app-container{
  padding: 0;
  flex-direction: initial !important;
}
</style>