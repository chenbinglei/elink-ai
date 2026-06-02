<template>
  <div class="content_body">
    <div :class="'deviceStatus' + deviceInfo.pcsOperativeMode" class="deviceStatus">
      <span>{{ $filters.pcsWorkStatus(deviceInfo.pcsOperativeMode) }}</span>
    </div>
  </div>
</template>

<script>
import {reactive, defineComponent, toRefs} from "vue";

export default defineComponent({
  name: "PCSDeviceStatusCom",
  props: {
    deviceInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup() {
    const that = reactive({});

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>
.deviceStatus {
  padding: 2px 10px;
  color: #ffffff45;
  font-size: 12px;
  border-radius: 2px;
  background: #ffffff26;
  box-sizing: border-box;
  border: 1px solid #ffffff26;
}

.deviceStatus2 {
  color: #26c5ff;
  background: #055283;
  border: 1px solid #06a7ec;
}

.deviceStatus1 {
  color: #ffba26;
  background: #83240545;
  border: 1px solid #eca706;
}

.deviceStatus3 {
  color: #ff4a26;
  background: #83240545;
  border: 1px solid #c21919;
}

.deviceStatus0 {
  color: #ffffffcc;
  background: #434D5E;
  border: 1px solid #727780;
}
</style>