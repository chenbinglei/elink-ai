<template>
  <router-view v-slot="{ Component, route }">
    <keep-alive :include="cachedViews">
      <component :is="Component" :key="route.fullPath" />
    </keep-alive>
  </router-view>
</template>

<script lang="ts">
import { useTagsViewStore } from '@/stores/index';

import {computed, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "AppMain",
  setup() {
    const tagsViewStore = useTagsViewStore();

    const cachedViews = computed(() => {
      return tagsViewStore.cachedViews;
    });

    const that = reactive({});

    return {...toRefs(that), cachedViews};
  }
});
</script>
<style lang="scss" scoped></style>

