<template>
  <div class="MiniProgramUserInfoCom">
    <BasicInformationCom :routeInfo="routeInfo"></BasicInformationCom>
    <CommonChargingStationsCom :routeInfo="routeInfo"></CommonChargingStationsCom>
    <CommonChargingPeriodsCom :routeInfo="routeInfo"></CommonChargingPeriodsCom>
  </div>
</template>

<script lang="ts">
import {defineComponent, reactive, toRefs} from "vue";
import BasicInformationCom from "./BasicInformationCom.vue";
import CommonChargingStationsCom from "./CommonChargingStationsCom.vue";
import CommonChargingPeriodsCom from "./CommonChargingPeriodsCom.vue";

export default defineComponent({
  name: "MiniProgramUserInfoCom",
  components: {BasicInformationCom, CommonChargingStationsCom, CommonChargingPeriodsCom},
  props: {
    routeInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const that = reactive({});

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>

</style>