<template>
  <div class="collapseMenu">
    <div class="retract pointer" @click="toggleSideBar">
      <i v-if="!isCollapse" class="iconfont icon-zuocelan-lanzhankai"></i>
      <i v-else class="iconfont icon-zuocelan-lansuohui"></i>
    </div>
  </div>
</template>

<script>
import {useStore} from "vuex";
import {computed, onMounted, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "CollapseMenu",
  setup() {

    const store = useStore();
    const isCollapse = computed(() => {
      return store.state.sidebar.isCollapse
    });

    const that = reactive({});
    const toggleSideBar = () => {
      store.dispatch('toggleSideBar')
    };

    onMounted(() => {
    });

    return {...toRefs(that), isCollapse, toggleSideBar};
  }
})
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
