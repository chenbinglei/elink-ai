<template>
  <div class="content_body">
    <div :class="'deviceStatus' + runState" class="deviceStatus">
      <span>{{ $filters.inverterRunState(runState) }}</span>
    </div>
  </div>
</template>

<script>
import {reactive, defineComponent, toRefs, watch} from "vue";

export default defineComponent({
  name: "InverterDeviceCardInfo",
  props: {
    deviceInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({
      runState: ""
    });

    const watchDeviceInfo = watch(() => props.deviceInfo, (newDeviceInfo) => {
      let runState = "";
      if (newDeviceInfo.runState || newDeviceInfo.runState === 0) {
        runState = newDeviceInfo.runState > 3 ? 4 : newDeviceInfo.runState;
      }
      that.runState = runState;
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchDeviceInfo};
  }
});
</script>

<style lang="scss" scoped>
.deviceStatus {
  padding: 2px 10px;
  color: #ffffffcc;
  font-size: 12px;
  border-radius: 2px;
  background: #ffffff26;
  box-sizing: border-box;
  border: 1px solid #ffffff26;
}

.deviceStatus0 {
  color: #ffba26;
  background: #83240545;
  border: 1px solid #eca706;
}

.deviceStatus1, .deviceStatus2, .deviceStatus3, {
  color: #26c5ff;
  background: #055283;
  border: 1px solid #06a7ec;
}

.deviceStatus4 {
  color: #ff4a26;
  background: #83240545;
  border: 1px solid #c21919;
}
</style>