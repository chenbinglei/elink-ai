<template>
  <div class="content_body">
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.soc) }}</div>
      <div class="title">SOC（%）</div>
    </div>
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.ratedCap) }}</div>
      <div class="title">额定容量（{{ $filters.numberUnit(deviceInfo.ratedCap, 'kWh') }}）</div>
    </div>
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.batterytotalvoltage) }}</div>
      <div class="title">电池总电压（{{ $filters.numberUnit(deviceInfo.batterytotalvoltage, 'V') }}）</div>
    </div>
  </div>
</template>

<script>
import {reactive, defineComponent, toRefs} from "vue";

export default defineComponent({
  name: "BatteryDeviceCardInfo",
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