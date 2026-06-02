<template>
  <div class="menu-wrapper">
    <template v-for="item in routes">
      <template v-if="!item.hidden">
        <template v-if="item.children && item.children.length">
          <el-sub-menu :key="item.name" :index="item.name || item.path">
            <template #title>
              <i v-if="item.meta&&item.meta.icon" :class="item.meta.icon" class="iconfont"></i>
              <span v-if="item.meta&&item.meta.title">{{ item.meta.title }}</span>
            </template>
            <template v-for="child in item.children">
              <template v-if="!child.hidden">
                <sidebar-item v-if="child.children&&isShowSidebarItem(child.children)" :key="child.path" :routes="[child]" class="nest-menu" is-nest></sidebar-item>
                <RouterLinkItemCom v-else :route="child"></RouterLinkItemCom>
              </template>
            </template>
          </el-sub-menu>
        </template>
        <RouterLinkItemCom v-else :route="item"></RouterLinkItemCom>
      </template>
    </template>
  </div>
</template>

<script>
import {reactive, toRefs, defineComponent} from "vue";
import RouterLinkItemCom from "./RouterLinkItemCom.vue";

export default defineComponent({
  name: 'SidebarItem',
  components:{RouterLinkItemCom},
  props: {
    routes: {
      type: Array,
      default: () => []
    }
  },
  setup() {

    const that = reactive({});

    const isShowSidebarItem = (children) => {
      if (!children.length) return false;
      for (let i = 0; i < children.length; i++) {
        if (!children[i].hidden) {
          return true;
        }
      }
      return false;
    }

    return {...toRefs(that), isShowSidebarItem};
  }
})
</script>

<style lang="scss" scoped>
.iconfont {
  margin-right: 8px;
}
</style>
