<script lang="ts">
import { reactive, defineComponent, toRefs, watch } from "vue";

export default defineComponent({
  name: "StationTabGroup",
  props: {
    tabList: {
      default: () => [],
      type: Array,
    },
    tabActive: {
      type: Number,
      default: 0,
    },
  },
  emits: ["tabChangeEvent"],
  setup(props, { emit }) {
    const state = reactive({
      tab_active: 0,
    });
    const clickStationChangeFun = (index) => {
      state.tab_active = index;
      emit("tabChangeEvent", state.tab_active);
    };
    // 监听 updateTimeNum 重新获取资产数据
    const watchTabActive = watch(
      () => props.tabActive,
      () => {
        state.tab_active = props.tabActive;
      },
      { deep: true }
    );
    return { ...toRefs(state), clickStationChangeFun, watchTabActive };
  },
});
</script>

<template>
  <div class="station-tab-list">
    <div
      class="station-tab"
      v-for="(value, index) in tabList"
      :key="index"
      :class="tab_active === index ? 'active' : ''"
      @click="clickStationChangeFun(index)"
    >
      {{ value }}
    </div>
  </div>
</template>

<style scoped lang="scss">
.station-tab-list {
  width: 100%;
  display: flex;
  flex-direction: row;
  .station-tab {
    width: 30%;
    padding: 5px 0;
    background: url("@/assets/image/station-details/tab1_normal.png") no-repeat;
    background-size: 100% 100%;
    font-family: Microsoft YaHei, Microsoft YaHei;
    font-weight: 400;
    font-size: 14px;
    color: #00ccff;
    text-align: center;
    cursor: pointer;
    position: relative;
    right: 20px;
    &:first-child {
      background: url("@/assets/image/station-details/tab1_start_normal.png") no-repeat;
      background-size: 100% 100%;
      width: 30%;
      position: relative;
      right: 0;
    }
    &:last-child {
      background: url("@/assets/image/station-details/tab1_end_normal.png") no-repeat;
      background-size: 100% 100%;
      width: 30%;
    }
    &.active,
    &:hover {
      color: #fff;
      background: url("@/assets/image/station-details/tab1_over.png");
      background-size: 100% 100%;
      &:first-child {
        background: url("@/assets/image/station-details/tab1_start_over.png") no-repeat;
        background-size: 100% 100%;
      }
      &:last-child {
        background: url("@/assets/image/station-details/tab1_end_over.png") no-repeat;
        background-size: 100% 100%;
      }
    }
  }
}
</style>