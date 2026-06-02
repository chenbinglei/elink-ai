<template>
  <div class="content_body">
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.cabinetTemp) }}</div>
      <div class="title">柜内温度（℃）</div>
    </div>
    <div class="right_info_bt_li">
      <div class="number">{{ $filters.numberValue(deviceInfo.cabinetHumidity) }}</div>
      <div class="title">柜内湿度（{{ $filters.numberUnit(deviceInfo.cabinetHumidity, '%RH') }}）</div>
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