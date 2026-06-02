<template>
  <router-view v-slot="{ Component, route }">
    <keep-alive :include="cachedViews">
      <component :is="Component" :key="route.fullPath" />
    </keep-alive>
  </router-view>
</template>

<script>
import {useStore} from "vuex";
import {computed, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "AppMain",
  setup() {
    const store = useStore();

    const cachedViews = computed(() => {
      return store.state.tagsView.cachedViews;
    });

    const that = reactive({});

    return {...toRefs(that), cachedViews};
  }
});
</script>
<style lang="scss" scoped></style>

