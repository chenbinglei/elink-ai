<template>
  <div :key="route_data.name" class="routerLinkItemCom">
    <template v-if="route_data.link">
      <a :href="route_data.link" target=“_blank”>
        <el-menu-item :index="route_data.name || route_data.path">
          <i v-if="route_data.meta&&route_data.meta.icon" :class="route_data.meta.icon" class="iconfont"></i>
          <template #title>
            <span v-if="route_data.meta&&route_data.meta.title">{{ route_data.meta.title }}</span>
          </template>
        </el-menu-item>
      </a>
    </template>
    <template v-else>
      <router-link :to="{ name: route_data.name }">
        <el-menu-item :index="route_data.name || route_data.path">
          <i v-if="route_data.meta&&route_data.meta.icon" :class="route_data.meta.icon" class="iconfont"></i>
          <template #title>
            <span v-if="route_data.meta&&route_data.meta.title">{{ route_data.meta.title }}</span>
          </template>
        </el-menu-item>
      </router-link>
    </template>
  </div>
</template>

<script>
import {reactive, toRefs, defineComponent, watch} from "vue";

export default defineComponent({
  name: "RouterLinkItemCom",
  props: {
    route: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {
    const that = reactive({
      route_data: {}
    });

    const watchRoute = watch(() => props.route, (newRoute) => {
      that.route_data = JSON.parse(JSON.stringify(newRoute));
    }, {deep: true, immediate: true})

    return {...toRefs(that), watchRoute};
  }
})
</script>

<style lang="scss" scoped>
.router-link-active .is-active {
  transition: all 0.2s;
  background-color: #E7ECEF;
}

.iconfont {
  margin-right: 8px;
}
</style>