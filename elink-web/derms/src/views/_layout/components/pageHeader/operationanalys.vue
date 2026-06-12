<template>
  <div class="routeHandleMenus">
    <template v-if="handleMenuArray && handleMenuArray.length">
      <div class="content_list">
        <el-row :gutter="24" type="flex" justify="space-between" class="content_route_list">
          <el-col v-for="(item, index) in secondLevelChildren" :key="index" :span="12"
            :class="{ marginNoBottom: index >= item.length - 2 }">
            <div :class="{ active_class: item.name === routeName }" class="content_route_li"
              @click="clickRouteItemFun(item)">
              <div class="route_title">{{ item.title }}</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </template>
    <div v-else class="flex-jc-ai-center null-data">暂无数据</div>
  </div>
</template>

<script>
import { useOperationManagementStore } from '@/stores/index';

import { useRouter, useRoute } from "vue-router";
import { defineComponent, onMounted, getCurrentInstance, reactive, toRefs, watch } from "vue";

export default defineComponent({
  name: "ChargingStationOperation",
  props: {
    routeMenuList: {
      type: Array,
      default: () => []
    },
    componentName: {
      type: String,
      default: ""
    },
    analysList: {
      type: Array,
      default: () => []
    }
  },
  emits: ["update:componentName"],
  onMounted () {
    getAnalusList()
  },
  setup (props) {
    onMounted(() => {
      getAnalusList();
    });
    const vueRouter = useRouter();
    const route = useRoute();
    const operationManagementStore = useOperationManagementStore();
    const { emit } = getCurrentInstance();

    const that = reactive({
      handleMenuArray: [],
      routeName: props.componentName,
      analysList: props.analysList,
      secondLevelChildren: []
    });
    const getAnalusList = () => {
      const secondLevelChildren = [];
      for (const item of props.analysList) {
        if (item.children && item.children.length > 0) {
          secondLevelChildren.push(...item.children);
        }
      }
      that.secondLevelChildren = secondLevelChildren
    }

    const clickRouteItemFun = (childItem) => {
      console.log(childItem, 'childItem', childItem.path,childItem.name);

      that.routeName = childItem.name;
      console.log(that.routeName, 'that.routeName');
      // 因为储能模块的页面跳转首次进入的页面为tab便签  所以需要修改
      // if(that.routeName === 'energyChargingDischarging'){
      //   childItem.path = '/operationManagement/productionStatistics'
      //   that.routeName = 'productionStatistics'
      // }

      vueRouter.push(childItem.path); // 直接跳转到 /user/detail
      operationManagementStore.updateSecondaryVisible(false);
      emit("update:componentName", that.routeName);
    };

    const findChildrenRouteItemFun = () => {
      let findActiveRouteItem = recursionSearchItemFun(that.handleMenuArray);
      if (findActiveRouteItem) clickRouteItemFun(findActiveRouteItem);
    };

    const recursionSearchItemFun = (routeMenuList = []) => {
      let routeFindItem = null;
      if (routeMenuList && routeMenuList.length) {
        routeFindItem = routeMenuList[0];
        for (let i = 0; i < routeMenuList.length; i++) {
          if (routeMenuList[i].children && routeMenuList[i].children.length) {
            routeFindItem = recursionSearchItemFun(routeMenuList[i].children);
            break;
          }
        }
      }
      return routeFindItem;
    };
    // 如果需要查找子级中的路径
    const findAllNamesByPath = (menu, targetPath) => {
      const result = [];

      const search = (items) => {
        for (const item of items) {
          if (item.path === targetPath) {
            result.push(item.name);
          }
          if (item.children && item.children.length > 0) {
            search(item.children);
          }
        }
      };

      search(menu);
      return result;
    };
    const watchRouteMenuList = watch(() => props.routeMenuList, (newRouteMenuList) => {
      that.handleMenuArray = JSON.parse(JSON.stringify(newRouteMenuList));
      // -----------------------
      const menuList = findAllNamesByPath(newRouteMenuList, route.path)
      if (menuList) {
        that.routeName = menuList[0]
      }

      if (!that.routeName || JSON.stringify(newRouteMenuList).indexOf(that.routeName) === -1) findChildrenRouteItemFun();

      //进入之后默认选中第一条数据
      // if (!that.routeName || JSON.stringify(newRouteMenuList).indexOf(that.routeName) === -1) findChildrenRouteItemFun();
    }, { deep: true, immediate: true });

    const watchComponentName = watch(() => props.componentName, (newComponentName) => {
      that.routeName = newComponentName;
    }, { deep: true });

    return { ...toRefs(that), findAllNamesByPath, watchRouteMenuList, clickRouteItemFun, findChildrenRouteItemFun, recursionSearchItemFun, getAnalusList, watchComponentName };
  }
});
</script>

<style lang="scss" scoped>
.routeHandleMenus {
  width: 320px;
  overflow-y: auto;
  box-sizing: border-box;
  padding: 28px 12px 14px 32px;
  z-index: 999;

  .content_list {
    margin-bottom: 28px;

    .content_list_title {
      color: #ffffff;
      font-size: 16px;
      font-weight: 700;

      .iconfont {
        font-size: 18px;
        margin-right: 10px;
      }

      .title {
        font-weight: 600;
      }
    }

    .content_route_list {
      // padding-top: 24px;
      box-sizing: border-box;

      .content_route_li {
        cursor: pointer;
        color: #ffffffd9;
        margin-bottom: 21px;

        &:hover {
          .route_title {
            color: #4bc9ff;
          }
        }
      }

      .active_class {
        color: #4bc9ff;

        .route_title {
          text-decoration: underline;
        }
      }

      .marginNoBottom {
        .content_route_li {
          margin-bottom: 0 !important;
        }
      }
    }

    &:last-child {
      margin-bottom: 0;
    }
  }


  .null-data {
    height: 100%;
    color: rgba(255, 255, 255, .8);
  }
}
</style>