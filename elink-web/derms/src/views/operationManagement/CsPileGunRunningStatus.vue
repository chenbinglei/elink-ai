<template>
  <!-- 运行实况 -->
  <div class="app-container">
    <div class="content_body">

      <div class="content_body_right">
        <div class="content_class" v-show="!secondaryDetailsVisible">
          <component ref="componentRef" :is="componentName" :componentName="componentName" :routeInfo="routeInfo"
            @changEvent="changEvent"></component>

        </div>
        <div class="secondary_details" v-if="secondaryDetailsVisible">
          <SecondaryDetailsCom ref="secondaryDetailsComRef" @changEvent="changEvent"></SecondaryDetailsCom>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useStore } from 'vuex';
import { getLeftTreeDataFun } from "@/utils";
import { RouteHandleMenus } from "./_components/operationManagement/PublicComponents/index";
import { computed, defineComponent, onMounted, reactive, toRefs, ref } from "vue";
import SecondaryDetailsCom from "./_components/operationManagement/SecondaryDetailsCom.vue";
import {
  CsPileGunRunningStatus, CsChargingRecord, CsDisChargingRecord, CsPileOccupationRecord, CsSettlementManagement
} from "./_components/operationManagement/ChargingStationOperation/index";
export default defineComponent({
  name: "ChargingStationOperation",
  components: { SecondaryDetailsCom, CsDisChargingRecord, RouteHandleMenus, CsPileGunRunningStatus, CsChargingRecord, CsPileOccupationRecord, CsSettlementManagement },
  setup () {
    const store = useStore();
    const secondaryDetailsVisible = computed(() => {
      return store.state.operationManagement.secondaryDetailsVisible
    });

    const that = reactive({
      routeInfo: {},
      componentName: "CsPileGunRunningStatus",
      routeMenuList: [],
    });

    const componentRef = ref(null);
    const changEvent = (data) => {
      if (data.operateType === "switchComponents") {
        that.routeInfo = JSON.parse(JSON.stringify(data));
        that.componentName = data.componentName;
      }

      if (data.operateType === "clearRouteInfo") that.routeInfo = {};
    }

    // 获取左侧树结构的数据
    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun("/operationManagement/ChargingStationOperation", 0);
    }

    onMounted(() => {
      store.dispatch("updateSecondaryVisible", false);
      store.dispatch("updateSecondaryInfo", {});
      findRouteMenuListFun()
    })

    return { ...toRefs(that), findRouteMenuListFun, secondaryDetailsVisible, changEvent, componentRef };
  }
})
</script>

<style lang="scss" scoped>
.app-container {
  position: relative;
  flex-direction: column;

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

      .content_class {
        width: 100%;
        height: 100%;
      }

      .secondary_details {
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
}
</style>