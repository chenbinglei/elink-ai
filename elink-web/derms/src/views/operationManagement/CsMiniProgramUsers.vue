<template>
  <!-- 小程序用户 -->
  <div class="app-container">
    <div class="app-container-right" v-show="!secondaryDetailsVisible">
      <template v-if="routeMenuList && routeMenuList.length">
        <Tabs v-model:tabsIndex="componentName" label="label" :tabs-array="routeMenuList"></Tabs>
        <component :is="componentName" @changEvent="changEvent"></component>
      </template>
      <null-data v-else words="请联系管理员开放权限！！！"></null-data>
    </div>
    <div class="app-container-right" v-if="secondaryDetailsVisible">
      <SecondaryDetailsCom ref="secondaryDetailsComRef" @changEvent="changEvent"></SecondaryDetailsCom>
    </div>
  </div>
</template>

<script>
import { useStore } from 'vuex';
import { getLeftTreeDataFun } from "@/utils";
import { onMounted, reactive, toRefs, defineComponent, getCurrentInstance, computed } from "vue";
import CsMiniProgramUsersList from "./_components/operationManagement/ChargingStationOperation/CsMiniProgramUsers/CsMiniProgramUsersList.vue";
import CsMiniProgramUsersLogout from "./_components/operationManagement/ChargingStationOperation/CsMiniProgramUsers/CsMiniProgramUsersLogout.vue";
import SecondaryDetailsCom from "./_components/operationManagement/SecondaryDetailsCom.vue";

export default defineComponent({
  name: "CsMiniProgramUsers",
  components: { CsMiniProgramUsersList, CsMiniProgramUsersLogout,SecondaryDetailsCom  },
  setup () {

    const { emit } = getCurrentInstance();
    const secondaryDetailsVisible = computed(() => {
      return store.state.operationManagement.secondaryDetailsVisible;
    });
        const store = useStore();
    const that = reactive({
      componentName: "",
      routeMenuList: [],
    });

    const changEvent = (data) => {
      emit("changEvent", data);
    };

    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun("/operationManagement/CsMiniProgramUsers", 0);
      // console.log(that.handleMenuArray);
    };

    onMounted(() => {
      findRouteMenuListFun();
      store.dispatch("updateSecondaryVisible", false);
      store.dispatch("updateSecondaryInfo", {});
    });

    return { ...toRefs(that), findRouteMenuListFun, changEvent,secondaryDetailsVisible };
  }
});

</script>