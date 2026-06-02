<template>
  <div class="content_body">
    <div class="content_body_left content_border">
      <RouteHandleMenus v-model:componentName="componentName" :routeMenuList="routeMenuList"></RouteHandleMenus>
    </div>
    <div class="content_body_right">
      <template v-if="routeMenuList && routeMenuList.length">
        <component ref="componentRef" :is="componentName" :componentName="componentName" @changEvent="changEvent"></component>
      </template>
      <template v-else><null-data words="请联系管理员开放权限！！！"></null-data></template>
    </div>
  </div>
</template>

<script>
import {getLeftTreeDataFun} from "@/utils";
import {RouteHandleMenus} from "./PublicComponents/index";
import {defineComponent, onMounted, reactive, ref, toRefs} from "vue";
import {PvStationManagement,PvStatisticalReport,PvCompositeAnalysis,PvComponentLibrary,PvStringConfigCenter} from "./PhotovoltaicOperation/index";

export default defineComponent({
  name: "PhotovoltaicOperation",
  components: {RouteHandleMenus,PvStationManagement,PvStatisticalReport,PvCompositeAnalysis,PvComponentLibrary,PvStringConfigCenter},
  setup() {

    const that = reactive({
      componentName: "",
      routeMenuList: [],
    });

    const componentRef = ref(null);
    const changEvent = (data)=>{
      if(data.operateType === "switchComponents"){
        that.routeInfo = JSON.parse(JSON.stringify(data));
        that.componentName = data.componentName;
      }

      // if(data.operateType === "clearRouteInfo") that.routeInfo = {};
    };

    // 获取左侧树结构的数据
    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun("/operationManagement/PhotovoltaicOperation", 0);
      // console.log(that.routeMenuList);
    };

    onMounted(() => {
      findRouteMenuListFun();
    });

    return {...toRefs(that), findRouteMenuListFun, changEvent, componentRef};
  }
});
</script>

<style lang="scss" scoped>
.content_body {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;

  .content_body_left {
    width: 300px;
    margin-right: 10px;
  }

  .content_body_right {
    flex: 1;
    width: 2px;
    height: 100%;
    flex-basis: auto;

    .content_class{
      width: 100%;
      height: 100%;
    }

    .secondary_details{
      width: 100%;
      height: 100%;
      z-index: 100;
      border-radius: 6px;
      transition: all .28s;
    }
  }

  .content_border {
    height: 100%;
  }
}
</style>