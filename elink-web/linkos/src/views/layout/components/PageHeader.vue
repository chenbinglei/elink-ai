<template>
  <div class="pageHeader flex jc-space-between ai-center">
    <div class="header-left"></div>
    <div class="header-right flex">
      <!-- 操作菜单 -->
      <RouteOperationMenu ref="routeOperationMenuRef"></RouteOperationMenu>
      <div class="company">{{ userInfo?.tenantName }}</div>
      <!-- 用户操作列表 -->
      <UserOperationMenu ref="userOperationMenuRef"></UserOperationMenu>
    </div>
  </div>
</template>

<script>
import { useAppStore } from '@/stores/index';

import UserOperationMenu from "./pageHeader/UserOperationMenu";
import RouteOperationMenu from "./pageHeader/RouteOperationMenu";
import {computed, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "PageHeader",
  components: { UserOperationMenu, RouteOperationMenu },
  setup() {
    const appStore = useAppStore();
    const userInfo = computed(() => {
      return appStore.userInfo;
    });

    const that = reactive({

    })

    return { ...toRefs(that),userInfo }
  }
})
</script>

<style scoped lang="scss">
.pageHeader {
  flex: 1;
  height: 100%;
  display: flex;
  padding: 0 32px;
  box-sizing: border-box;

  .header-left {
    flex: 1;
    height: 100%;
    display: flex;
    align-items: center;

    img {
      max-width: 342px;
    }
  }

  .header-right {
    height: 100%;
    display: flex;
    align-items: center;

    .company {
      padding-right: 16px;
    }
  }
}
</style>
