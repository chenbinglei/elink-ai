<template>
  <div class="app-container-right">
    <template v-if="routeMenuList && routeMenuList.length">
      <div class="content_border content_top">
        <Tabs :tabsArray="routeMenuList" v-model:tabsIndex="componentName" label="label"></Tabs>
      </div>
      <component :is="componentName" :componentName="componentName"></component>
    </template>
    <template v-else><null-data words="请联系管理员开放权限！！！"></null-data></template>
  </div>
</template>

<script>
import {getLeftTreeDataFun} from "@/utils";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import CsOperationOverview from "./CsBusinessAnalysis/CsOperationOverview.vue";

export default defineComponent({
  name: "CsBusinessAnalysis",
  components: { CsOperationOverview },
  setup() {

    const that = reactive({
      routeMenuList: [],
      componentName: "",
    });

    // 获取用户页面权限
    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun("/operationManagement/CsBusinessAnalysis", 0);
      // console.log(that.routeMenuList);
    };

    onMounted(() => {
      findRouteMenuListFun();
    });

    return {...toRefs(that), findRouteMenuListFun};
  }
});
</script>

<style lang="scss" scoped>
.content_top{
  margin-bottom: 12px;
  box-sizing: border-box;
  padding: 14px 16px 0 10px;

  :deep(.tabs){
    .tabs_slide {
      background: none;
    }
  }
}
</style>