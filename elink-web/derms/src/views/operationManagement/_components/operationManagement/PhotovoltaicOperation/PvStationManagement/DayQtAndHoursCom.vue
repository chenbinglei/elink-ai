<template>
  <div class="dayQtAndHoursCom">
    <div class="field_class">
      <span class="number">{{ $filters.moreData(returnDataInfo.dayQt) }}</span>
      <span class="unit">kWh</span>
    </div>
    <span class="split_line">/</span>
    <div class="field_class">
      <span class="number">{{ $filters.moreData(returnDataInfo.dayQtHours) }}</span>
      <span class="unit">h</span>
    </div>
  </div>
</template>

<script>
import {onMounted, reactive, ref, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: 'DayQtAndHoursCom',
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({});

    return {...toRefs(that),};
  }
});
</script>

<style lang="scss" scoped>
.dayQtAndHoursCom{
  display: flex;
  align-items: center;

  .split_line{
    margin: 0 2px;
  }

  .unit{
    color: #ffffff99;
    margin-left: 1px;
  }
}
</style>