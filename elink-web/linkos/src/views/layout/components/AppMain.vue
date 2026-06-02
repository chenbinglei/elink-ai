<template>
  <div class="appMain" :style="{ maxHeight: contentMainMaxHeight + 'px'}">
    <router-view v-slot="{ Component, route }">
      <keep-alive :include="cachedViews">
        <component :is="Component" :key="route.fullPath" :contentMaxHeight="contentMainMaxHeight" />
      </keep-alive>
    </router-view>
  </div>
</template>

<script>
import {useStore} from "vuex";
import {computed, reactive, toRefs} from "vue";

export default {
  name: "AppMain",
  setup() {
    const store = useStore();

    const cachedViews = computed(() => {
      return store.state.tagsView.cachedViews;
    });

    const contentMainMaxHeight = computed(() => {
      return store.state.app.contentMainMaxHeight;
    });

    const that = reactive({});

    return {...toRefs(that), cachedViews, contentMainMaxHeight};
  }
};
</script>
<style lang="scss" scoped>
.appMain {
  width: 100%;
  display: flex;
  padding: 12px 14px;
  box-sizing: border-box;
  background-color: #F8F8F8;
}
</style>

