<template>
  <div class="optimizeControl">
    <buttons-tabs :tabsIndex="componentName" :tabsArray="tabsArray"></buttons-tabs>
    <div class="content_bottom scrollbarStyle">
      <component :is="componentName" :siteId="siteId"></component>
    </div>
  </div>
</template>

<script>
import {reactive, defineComponent, toRefs} from "vue";
import OperationControl from "./OptimizeControl/OperationControl.vue";
import StrategicManagement from "./OptimizeControl/StrategicManagement.vue";
import StrategyOptimization from "./OptimizeControl/StrategyOptimization.vue";

export default defineComponent({
  name: "OptimizeControl",
  components: {OperationControl, StrategicManagement, StrategyOptimization},
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
    code: {
      type: [Number, String],
      default: ""
    },
  },
  setup() {

    const that = reactive({
      componentName: "OperationControl",
      tabsArray: [{id: "OperationControl", name: "运行控制"}, {id: "StrategicManagement", name: "策略管理"}, {id: "StrategyOptimization", name: "策略评估优化"}],
    });

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>
.optimizeControl {
  height: 100%;
  padding: 10px 12px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  .content_bottom {
    flex: 1;
    overflow-y: auto;
    padding-top: 12px;
    box-sizing: border-box;
  }
}
</style>