<script lang="ts">
import {reactive, defineComponent, toRefs, watch} from "vue";

export default defineComponent({
  name: "TabGroup",
  props: {
    tabList: {
      default: () => [],
      type: Array,
    },
    tabActive: {
      type: Number,
      default: 0,
    }
  },
  emits: ['tabChangeEvent'],
  setup(props, {emit}) {
    const state = reactive({
      tab_active: 0,
    })
    const clickStationChangeFun = (index) => {
      state.tab_active = index;
      emit("tabChangeEvent", state.tab_active);

    }
    // 监听 updateTimeNum 重新获取资产数据
    const watchTabActive = watch(() => props.tabActive, () => {
      state.tab_active = props.tabActive;
    }, {deep: true})
    return {...toRefs(state), clickStationChangeFun, watchTabActive};
  }
});
</script>

<template>
  <div class="panoramic-tab-list">
    <div class="right-tab-item" v-for="(value,index) in tabList" :key="index"
         :class="tab_active===index?'active':''" @click="clickStationChangeFun(index)"> {{ value }}
    </div>
  </div>
</template>

<style scoped lang="scss">
.panoramic-tab-list {
  display: flex;
  flex-direction: row;
  .right-tab-item{
    width: 64px;
    height: 28px;
    background: #002546;
    cursor: pointer;

    border: 1px solid #005A88;
    font-size: 14px;
    line-height: 28px;
    text-align: center;
    color: #7DCBFF;
    margin-left: -2px;
    font-family: Microsoft YaHei, Microsoft YaHei;
    font-weight: bold;
    &:first-child{
      border-radius: 4px 0px 0px 4px;
    }
    &:last-child{
      border-radius: 0px 4px 4px 0px;
    }
    &.active,&:hover{
      color: #FFFFFF;
      background: linear-gradient( 180deg, #003447 0%, #0088CF 100%);
      //border-radius: 4px 0px 0px 4px;
    }
  }
}
</style>