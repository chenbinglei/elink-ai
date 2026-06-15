<template>
  <!-- 电桩日志 -->
  <div class="app-container">
    <div class="app-container-right">
      <template v-if="routeMenuList && routeMenuList.length">
        <div class="content_border content_top">
          <Tabs v-model:tabsIndex="componentName" :tabsArray="routeMenuList" label="label"></Tabs>
        </div>
        <component :is="componentName" :componentName="componentName"></component>
      </template>
      <template v-else>
        <null-data words="请联系管理员开放权限！！！"></null-data>
      </template>
    </div>
  </div>
</template>

<script lang="ts">
import { getLeftTreeDataFun } from "@/utils";
import { defineComponent, onMounted, reactive, toRefs } from "vue";
import CsProtocolLog from "./_components/operationManagement/ChargingStationOperation/CsElectricPileLog/CsProtocolLog.vue";
import CsOriginalMessage from "./_components/operationManagement/ChargingStationOperation/CsElectricPileLog/CsOriginalMessage.vue";

export default defineComponent({
  name: "CsElectricPileLog",
  components: { CsProtocolLog, CsOriginalMessage },
  setup () {

    const that = reactive({
      routeMenuList: [],
      componentName: "",
    });

    // 获取用户页面权限
    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun(`/operationManagement/CsElectricPileLog`, 0);
      // console.log(that.routeMenuList);
    };

    onMounted(() => {
      findRouteMenuListFun();
    });

    return { ...toRefs(that), findRouteMenuListFun };
  }
});
</script>

<style lang="scss" scoped>
.content_top {
  padding-top: 12px;
  margin-bottom: 12px;
  box-sizing: border-box;
}
</style>