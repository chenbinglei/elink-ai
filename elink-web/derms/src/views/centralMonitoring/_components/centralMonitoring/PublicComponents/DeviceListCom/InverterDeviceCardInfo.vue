<template>
  <div class="content_body">
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.activePower) }}</div>
      <div class="title">有功功率（{{ $filters.numberUnit(deviceInfo.activePower, 'kW') }}）</div>
    </div>
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.dayQt) }}</div>
      <div class="title">今日发电（{{ $filters.numberUnit(deviceInfo.dayQt, 'kWh') }}）</div>
    </div>
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.equivalentHours) }}</div>
      <div class="title">等效发电（{{ $filters.numberUnit(deviceInfo.equivalentHours, 'h') }}）</div>
    </div>
  </div>
</template>

<script lang="ts">
import {reactive, defineComponent, toRefs, ref} from "vue";

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
  setup() {
    const that = reactive({});

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>
.content_body {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .right_info_bt_li {
    flex: 1;
    text-align: center;

    .number {
      color: #08f7fd;
      font-size: 16px;
      font-weight: 700;
    }

    .title {
      font-size: 12px;
      color: #ffffffcc;
      margin-top: 10px;
      white-space: nowrap;
    }
  }
}
</style>