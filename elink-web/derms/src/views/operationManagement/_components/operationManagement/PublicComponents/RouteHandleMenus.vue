<template>
  <div class="routeHandleMenus">
    <template v-if="handleMenuArray && handleMenuArray.length">
      <div v-for="(item,index) in handleMenuArray" :key="index" class="content_list">
        <div class="content_list_title">
          <span :class="item.meta.icon" class="iconfont"></span>
          <span class="title">{{ item.label }}</span>
        </div>
        <el-row class="content_route_list">
          <template v-if="item.children && item.children.length">
            <el-col v-for="(childItem,childIndex) in item.children" :key="childIndex" :span="12"
                    :class="{marginNoBottom: childIndex >= item.children.length - 2}"
            >
              <div :class="{active_class: childItem.name === routeName }" class="content_route_li" @click="clickRouteItemFun(childItem)">
                <div class="route_title">{{ childItem.label }}</div>
              </div>
            </el-col>
          </template>
        </el-row>
      </div>
    </template>
    <div v-else class="flex-jc-ai-center null-data">暂无数据</div>
  </div>
</template>

<script lang="ts">
import { useOperationManagementStore } from '@/stores/index';

import {defineComponent, getCurrentInstance, reactive, toRefs, watch} from "vue";

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
    }
  },
  emits: ["update:componentName"],
  setup(props) {

    const operationManagementStore = useOperationManagementStore();
    const {emit} = getCurrentInstance();

    const that = reactive({
      handleMenuArray: [],
      routeName: props.componentName,
    });

    const clickRouteItemFun = (childItem) => {
      that.routeName = childItem.name;
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

    const watchRouteMenuList = watch(() => props.routeMenuList, (newRouteMenuList) => {
      that.handleMenuArray = JSON.parse(JSON.stringify(newRouteMenuList));
      if (!that.routeName || JSON.stringify(newRouteMenuList).indexOf(that.routeName) === -1) findChildrenRouteItemFun();
    }, {deep: true, immediate: true});

    const watchComponentName = watch(()=> props.componentName,(newComponentName)=>{
      that.routeName = newComponentName;
    },{deep: true});

    return {...toRefs(that), watchRouteMenuList, clickRouteItemFun, findChildrenRouteItemFun, recursionSearchItemFun, watchComponentName};
  }
});
</script>

<style lang="scss" scoped>
.routeHandleMenus {
  height: 100%;
  overflow-y: auto;
  box-sizing: border-box;
  padding: 28px 12px 14px 32px;

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

      .title{
        font-weight: 600;
      }
    }

    .content_route_list {
      padding-top: 24px;
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