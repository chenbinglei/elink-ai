<script>
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
  setup (props, { emit }) {
    const state = reactive({
      tab_active: props.tabActive,
    });
    const clickStationChangeFun = (index) => {
      state.tab_active = index;
      emit("tabChangeEvent", state.tab_active);
    };
    // 监听 updateTimeNum 重新获取资产数据
    const watchTabActive = watch(
      () => props.tabActive,
      () => {
        console.log("剪影")
        state.tab_active = props.tabActive;
        console.log(state.tab_active,'tabActive1')
      },
      { deep: true }
    );
    return { ...toRefs(state), clickStationChangeFun, watchTabActive };
  },
});
</script>

<template>
  <div class="station-tab-list">
    <div class="station-tab" v-for="(value, index) in tabList" :key="index"
      :class="tab_active === index ? 'active' : ''" @click="clickStationChangeFun(index)">
      {{ value.id ? value.label : value }}
    </div>
  </div>
</template>

<style scoped lang="scss">
.station-tab-list {
  display: flex;
  flex-direction: row;
  padding: 10px 10px 0px 19px;

  .station-tab {
    align-items: center;
    width: 72px;
    height: 24px;
    background: url("@/assets/image/station-details/tab1_normal.png");
    background-size: 100% 100%;
    font-family: Microsoft YaHei, Microsoft YaHei;
    line-height: 24px;
    font-weight: 400;
    font-size: 14px;
    color: #00ccff;
    text-align: center;
    font-style: normal;
    text-transform: none;
    margin-right: -8px;
    cursor: pointer;

    &:first-child {
      background: url("@/assets/image/station-details/tab1_start_normal.png");
      background-size: 100% 100%;
    }

    &:last-child {
      background: url("@/assets/image/station-details/tab1_end_normal.png");
      background-size: 100% 100%;
    }

    &.active,
    &:hover {
      width: 72px;
      height: 24px;
      background: url("@/assets/image/station-details/tab1_over.png");
      background-size: 100% 100%;
      line-height: 24px;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      text-align: center;
      font-style: normal;
      text-transform: none;
      -webkit-text-stroke: 1px rgba(0, 0, 0, 0);

      &:first-child {
        background: url("@/assets/image/station-details/tab1_start_over.png");
        background-size: 100% 100%;
      }

      &:last-child {
        background: url("@/assets/image/station-details/tab1_end_over.png");
        background-size: 100% 100%;
      }
    }
  }
}
</style>