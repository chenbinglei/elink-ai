<template>
  <!-- 光伏电站管理 -->
  <div class="app-container">
    <div class="content_body_right">
      <component ref="componentRef" :is="componentName" :componentName="componentName" :routeInfo="routeInfo"
        @changEvent="changEvent"></component>
    </div>
  </div>
</template>

<script>
import { defineComponent, onMounted, reactive, toRefs } from "vue";
import {
  PvStationManagement, PvStatisticalReport, PvCompositeAnalysis, PvComponentLibrary, PvStringConfigCenter
} from "./_components/operationManagement/PhotovoltaicOperation/index";

export default defineComponent({
  name: "CsElectricPileLog",
  components: { PvStationManagement, PvStatisticalReport, PvCompositeAnalysis, PvComponentLibrary, PvStringConfigCenter },
  setup () {

    const that = reactive({
      routeMenuList: [],
      componentName: "PvStationManagement",
    });


    onMounted(() => {
    });

    return { ...toRefs(that), };
  }
});
</script>

<style lang="scss" scoped>
.content_body_right{
  width: 100%;
}

.content_top {
  padding-top: 12px;
  margin-bottom: 12px;
  box-sizing: border-box;
}
</style>