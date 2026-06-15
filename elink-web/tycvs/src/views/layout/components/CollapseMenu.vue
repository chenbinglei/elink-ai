<template>
  <div class="collapseMenu">

    <div class="retract pointer" @click="toggleSideBar">
      <i v-if="!isCollapse" class="iconfont icon-zuocelan-lanzhankai"></i>
      <i v-else class="iconfont icon-zuocelan-lansuohui"></i>
    </div>

  </div>
</template>

<script lang="ts">
import { useSidebarStore } from '@/stores/index';

import {computed, onMounted, reactive, toRefs} from "vue";

export default {
  name: "CollapseMenu",
  setup() {

    const sidebarStore = useSidebarStore();
    const isCollapse = computed(() => {
      return sidebarStore.isCollapse
    });

    const that = reactive({});
    const toggleSideBar = () => {
      sidebarStore.toggleSideBar()
    };

    onMounted(() => {
    });

    return {...toRefs(that), isCollapse, toggleSideBar};
  }
}
</script>

<style lang="scss" scoped>
.collapseMenu {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  box-sizing: border-box;
  padding: 0 12px;

  .iconfont {
    font-size: 20px;
    color: #162a48;
  }
}
</style>
