<template>
  <div class="sideBarComponent scrollbarStyle">
    <el-menu :active-text-color="activeTextColor" :collapse="!isCollapse" :default-active="$route.name" :text-color="textColor" mode="vertical" unique-opened>
      <sidebar-item :routes="routes"></sidebar-item>
    </el-menu>
  </div>
</template>

<script>
import {useStore} from "vuex";
import SidebarItem from "./SidebarItem.vue";
import {computed, onMounted, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "SideBarComponent",
  components: {SidebarItem},
  setup() {

    const store = useStore();
    const routes = computed(() => {
      return JSON.parse(localStorage.getItem("SIDEBAR"));
    });

    const isCollapse = computed(() => {
      return store.state.sidebar.isCollapse;
    });

    const that = reactive({
      activeTextColor: "#242424",
      textColor: "rgba(0,0,0,.7)",
    });

    onMounted(() => {});

    return {...toRefs(that), routes, isCollapse};
  }
});
</script>

<style lang="scss" scoped>
.sideBarComponent {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  transition: width 0.28s;

  :deep(.el-menu) {
    width: 100%;
    --el-menu-border-color: none;
    --el-menu-base-level-padding: 16px;
    --el-menu-text-color: #121C3F !important;

    .el-menu-item{
      padding-left: calc(var(--el-menu-level) * var(--el-menu-level-padding));

      .router_content{
        width: 100%;
        white-space: nowrap;
        box-sizing: border-box;
        padding-left: var(--el-menu-base-level-padding);
      }
    }

    .is-active{
      .el-sub-menu__title {
        color: #007FEB !important;
      }
    }
  }
}
</style>
