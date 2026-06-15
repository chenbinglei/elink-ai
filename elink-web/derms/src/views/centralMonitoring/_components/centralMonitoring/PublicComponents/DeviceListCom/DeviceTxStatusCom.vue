<template>
  <div class="content_body">
    <div :class="'txStatus' + deviceInfo.txStatus" class="txStatus">
      <span>{{ $filters.deviceStatus(deviceInfo.txStatus) }}</span>
    </div>
  </div>
</template>

<script lang="ts">
import {reactive, defineComponent, toRefs} from "vue";

export default defineComponent({
  name: "DeviceTxStatusCom",
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
.txStatus {
  padding: 2px 10px;
  color: #ffffff45;
  font-size: 12px;
  border-radius: 2px;
  background: #ffffff26;
  box-sizing: border-box;
  border: 1px solid #ffffff26;
}

.txStatus1{
  color: #00ff90;
  background: #0D6257;
  border: 1px solid #00ff90;
}

.txStatus2{
  color: #ffba26;
  background: #83240545;
  border: 1px solid #eca706;
}

.txStatus3{
  color: #ff4a26;
  background: #83240545;
  border: 1px solid #c21919;
}

.txStatus88{
  color: #ffffffcc;
  background: #434D5E;
  border: 1px solid #727780;
}
</style>