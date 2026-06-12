<template>
  <div class="systemLogo" :title="platName">
    <img v-if="isCollapse" class="home_logo" src="@/assets/image/login_logo.png" alt=""/>
    <img v-else class="homeLogo" src="@/assets/image/homeLogo.png" alt=""/>
  </div>
</template>

<script>
import { useSidebarStore } from '@/stores/index';

import {computed, onMounted, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "SystemLogo",
  setup() {

    const sidebarStore = useSidebarStore();
    const isCollapse = computed(() => {
      return sidebarStore.isCollapse
    });

    const that = reactive({
      platName: "SUN-OS"
    });

    onMounted(() => {
    });

    return {...toRefs(that), isCollapse};
  }
})
</script>

<style lang="scss" scoped>
.systemLogo {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  border-right: 1px solid #F2F2F2;
  padding: 0 16px;

  .homeLogo{
    width: 24px;
    height: 24px;
    transition: all 0.28s;
  }

  .home_logo{
    flex: 1;
    height: 24px;
    transition: all 0.28s;
  }
}
</style>
