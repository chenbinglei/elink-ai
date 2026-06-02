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
              <template v-if="child.children&&isShowSidebarItem(child.children)">
                <sidebar-item :key="child.path" :routes="[child]" class="nest-menu" is-nest></sidebar-item>
              </template>
              <router-link v-else :key="child.name" :to="{ name: child.name }">
                <el-menu-item :index="child.name || child.path">
                  <template #title>
                    <div class="router_content">
                      <i v-if="child.meta&&child.meta.icon" :class="child.meta.icon" class="iconfont"></i>
                      <span v-if="child.meta&&child.meta.title">{{ child.meta.title }}</span>
                    </div>
                  </template>
                </el-menu-item>
              </router-link>
            </template>
          </el-sub-menu>
        </template>
        <template v-else>
          <router-link :key="item.name" :to="{ name: item.name }">
            <el-menu-item :key="item.name" :index="item.name || item.path">
              <template #title>
                <div class="router_content">
                  <i v-if="item.meta&&item.meta.icon" :class="item.meta.icon" class="iconfont"></i>
                  <span v-if="item.meta&&item.meta.title">{{ item.meta.title }}</span>
                </div>
              </template>
            </el-menu-item>
          </router-link>
        </template>
      </template>
    </template>
  </div>
</template>

<script>
import {reactive, toRefs} from "vue";

export default {
  name: 'SidebarItem',
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
}
</script>
<style lang="scss" scoped>
.menu-wrapper {

  .iconfont {
    margin-right: 10px;
  }

  .router-link-active .is-active {
    color: #FFFFFF;
    transition: all 0.2s;

    .router_content{
      width: 100%;
      border-radius: 8px;
      //box-sizing: border-box;
      background-color: #007FEB;
      padding-left: var(--el-menu-base-level-padding);
    }
  }
}
</style>
