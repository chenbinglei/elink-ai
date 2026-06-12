<template>
  <div class="canvasInteractionCom">
    <Tabs v-model:tabsIndex="tabsIndex" :tabsArray="tabsArray"></Tabs>
    <div class="content_body scrollbarStyle">
      <template v-if="componentName === 'canvas'">
        <CanvasPaperConfig v-if="tabsIndex === 1"></CanvasPaperConfig>
      </template>
      <template v-if="componentName === 'singleElement'">
        <CurrentElementAppearance v-if="tabsIndex === 1"></CurrentElementAppearance>
        <CurrentElementAnimation v-if="tabsIndex === 2"></CurrentElementAnimation>
        <CurrentElementData v-if="tabsIndex === 3"></CurrentElementData>
        <CurrentElementEvents v-if="tabsIndex === 4"></CurrentElementEvents>
      </template>
      <template v-if="componentName === 'multipleElement'">
        <MultipleElementAppearance v-if="tabsIndex === 1"></MultipleElementAppearance>
      </template>
    </div>
  </div>
</template>
<script>
import { useMeta2dStore } from '@/stores/index';

import {reactive, toRefs, defineComponent, computed, watch} from "vue";
import {CanvasPaperConfig, CurrentElementAppearance, CurrentElementAnimation, CurrentElementData, CurrentElementEvents, MultipleElementAppearance} from "./CanvasInteractionCom/index"

export default defineComponent({
  name: 'CanvasInteractionCom',
  components: {CanvasPaperConfig, CurrentElementAppearance, CurrentElementAnimation, CurrentElementData, CurrentElementEvents, MultipleElementAppearance},
  setup() {

    const meta2dStore = useMeta2dStore();
    const current_active_pel_list = computed(() => {
      return meta2dStore.current_active_pel_list;
    });

    const that = reactive({
      tabsIndex: 1,
      tabsArray: [],
      componentName: "canvas",

      canvas_list: [{id: 1, name: "画布"}],
      multiple_element_list: [{id: 1, name: "外观"}], //多个图元展示的数组
      single_element_list: [{id: 1, name: "外观"}, {id: 2, name: "动画"}, {id: 3, name: "数据"}, {id: 4, name: "交互"}],
    })

    const watchCurrentActivePelList = watch(() => current_active_pel_list, (newCurrentActivePelList) => {
      // console.log(newCurrentActivePelList);
      let componentName = "canvas";
      let tabsArray = JSON.parse(JSON.stringify(that.canvas_list));
      if (newCurrentActivePelList.value && newCurrentActivePelList.value.length) {
        if (newCurrentActivePelList.value.length === 1) {
          componentName = "singleElement";
          tabsArray = JSON.parse(JSON.stringify(that.single_element_list));
        } else {
          componentName = "multipleElement";
          tabsArray = JSON.parse(JSON.stringify(that.multiple_element_list));
        }
      }

      that.tabsIndex = 1;
      that.componentName = componentName;
      that.tabsArray = JSON.parse(JSON.stringify(tabsArray));
    }, {deep: true, immediate: true})

    return {...toRefs(that), current_active_pel_list, watchCurrentActivePelList}
  }
})
</script>
<style lang="scss" scoped>
.canvasInteractionCom {
  width: 300px;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 10px;
  box-sizing: border-box;
  background-color: var(--color-background);

  .content_body{
    flex: 1;
    height: 2px;
    overflow-y: auto;
  }
}
</style>