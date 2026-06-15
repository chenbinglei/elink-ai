<template>
  <div class="groupsPanelCom">
    <SideToolbarCom ref="sideToolbarComRef" v-model:tabsIndex="tabsIndex" v-model:componentName="componentName" :loading="loading"></SideToolbarCom>
    <div class="side_data_list">
      <component :is="componentName" :tabsIndex="tabsIndex" v-model:loading="loading"></component>
    </div>
  </div>
</template>
<script lang="ts">
import {reactive, toRefs, defineComponent} from "vue";
import {SideToolbarCom, CanvasStructureCom, CanvasElementList} from "./GroupsPanelCom/index";

export default defineComponent({
  name: 'GroupsPanelCom',
  components: {SideToolbarCom, CanvasStructureCom, CanvasElementList},
  setup() {

    const that = reactive({
      loading: false, // 是否可点击
      tabsIndex: "CanvasStructureCom",
      componentName: "CanvasStructureCom",
    })

    return {...toRefs(that)}
  }
})
</script>
<style lang="scss" scoped>
.groupsPanelCom {
  height: 100%;
  display: flex;

  .side_data_list {
    width: 250px;
    height: 100%;
    background-color: var(--color-background);
  }
}
</style>