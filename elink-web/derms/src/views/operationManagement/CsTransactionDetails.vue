<template>
  <!-- 交易明细 -->
  <div class="app-container">
    <div class="content_body">
      <div class="content_body_right">
        <template v-if="routeMenuList && routeMenuList.length">
          <Tabs v-model:tabsIndex="componentName" label="label" :tabs-array="routeMenuList"></Tabs>
          <component :is="componentName" :routeInfo="routeInfo" @changEvent="changEvent"></component>
        </template>
        <null-data v-else words="请联系管理员开放权限！！！"></null-data>
      </div>
    </div>
  </div>
</template>

<script>
import { getLeftTreeDataFun } from "@/utils";
import {
  defineComponent,
  getCurrentInstance,
  onMounted,
  reactive,
  toRefs,
} from "vue";
import CsTransactionChargingOrder from "./_components/operationManagement/ChargingStationOperation/CsTransactionDetails/CsTransactionChargingOrder.vue";
import CsTransactionOccupancyOrder from "./_components/operationManagement/ChargingStationOperation/CsTransactionDetails/CsTransactionOccupancyOrder.vue";
import CsTransactionV2GWalletOrder from "./_components/operationManagement/ChargingStationOperation/CsTransactionDetails/CsTransactionV2GWalletOrder.vue";
import { useRoute, useRouter } from 'vue-router';
export default defineComponent({
  name: "CsTransactionDetails",
  props: {
    routeInfo: {
      type: Object,
      default: () => {
        return {};
      },
    },
  },
  components: {
    CsTransactionChargingOrder,
    CsTransactionV2GWalletOrder,
    CsTransactionOccupancyOrder,
  },
  setup (props) {
    const route = useRoute();
    const { emit } = getCurrentInstance();

    const that = reactive({
      componentName: "",
      routeMenuList: [],
    });

    const changEvent = (data) => {
      emit("changEvent", data);
    };

    const findRouteMenuListFun = () => {
      // console.log(props.routeInfo);
      if (props.routeInfo.childComponentName)
        that.componentName = props.routeInfo.childComponentName;
      that.routeMenuList = getLeftTreeDataFun(
        "/operationManagement/CsTransactionDetails",
        0
      );
      // console.log(that.handleMenuArray);
    };

    onMounted(() => {
      console.log(route.query, 'yemian9999接受的值');
      if (route.query) {
        that.componentName = route.query.childComponentName;
        emit("changEvent", route.query);
      }

      findRouteMenuListFun();
    });

    return { ...toRefs(that), findRouteMenuListFun, changEvent };
  },
});
</script>

<style lang="scss" scoped>
.app-container {
  position: relative;
  flex-direction: column;

  .content_body {
    width: 100%;
    height: 96%;
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
        transition: all 0.28s;
      }
    }

    .content_border {
      height: 100%;
    }
  }
}
</style>