<template>
  <div class="content_body">
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.activePower) }}</div>
      <div class="title">有功功率（{{ $filters.numberUnit(deviceInfo.activePower, 'kW') }}）</div>
    </div>
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.power) }}</div>
      <div class="title">额定功率（{{ $filters.numberUnit(deviceInfo.power, 'kW') }}）</div>
    </div>
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.lastDayEff) }}</div>
      <div class="title">昨日充放电效率（{{ $filters.numberUnit(deviceInfo.lastDayEff, '%') }}）</div>
    </div>
  </div>
</template>

<script>
import {reactive, defineComponent, toRefs, ref} from "vue";

export default defineComponent({
  name: "PCSDeviceCardInfo",
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