<template>
  <div class="app-container-right">
    <template v-if="routeMenuList && routeMenuList.length">
      <Tabs v-model:tabsIndex="componentName" label="label" :tabs-array="routeMenuList"></Tabs>
      <component :is="componentName" @changEvent="changEvent"></component>
    </template>
    <null-data v-else words="请联系管理员开放权限！！！"></null-data>
  </div>
</template>

<script>
import {getLeftTreeDataFun} from "@/utils";
import {onMounted, reactive, toRefs, defineComponent, getCurrentInstance} from "vue";
import CsMiniProgramUsersList from "./CsMiniProgramUsers/CsMiniProgramUsersList.vue";
import CsMiniProgramUsersLogout from "./CsMiniProgramUsers/CsMiniProgramUsersLogout.vue";

export default defineComponent({
  name: "CsMiniProgramUsers",
  components: {CsMiniProgramUsersList, CsMiniProgramUsersLogout},
  setup() {

    const {emit} = getCurrentInstance();

    const that = reactive({
      componentName: "",
      routeMenuList: [],
    });

    const changEvent = (data)=>{
      emit("changEvent",data);
    };

    const findRouteMenuListFun = () => {
      that.routeMenuList = getLeftTreeDataFun("/operationManagement/CsMiniProgramUsers", 0);
      // console.log(that.handleMenuArray);
    };

    onMounted(() => {
      findRouteMenuListFun();
    });

    return {...toRefs(that), findRouteMenuListFun, changEvent};
  }
});

</script>