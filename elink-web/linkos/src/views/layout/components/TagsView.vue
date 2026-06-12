<template>
  <div class="tagsView" v-resize="setMaxNumberFun">

    <!--    详情页等返回按钮-->
    <el-page-header v-if="isShowBackBut" title="返回" @back="clickBackBut"></el-page-header>

    <div ref="routeListRef" class="content_center">
      <div class="content_route_list">
        <template v-for="(item,index) in visitedViews" :key="index">
          <template v-if="index < maxNumber && item.name !== 'homePage'">
            <div class="route_list pointer" :class="{ active_route: JSON.stringify(item) === JSON.stringify(routeViews) }" @click="clickOpenRoute(item)">
              <div class="route_li noSelect">
                <div class="routeName textTwo">
                  <span>{{ item.query && item.query.subTitle ? item.query.subTitle : item.meta.title }}</span>
                </div>
                <div class="close" @click.stop="clickCloseRoute(item)">
                  <el-icon size="18"><Close/></el-icon>
                </div>
              </div>
              <div class="route_line"></div>
            </div>
            <div class="menu_line"></div>

          </template>
        </template>
      </div>
    </div>

    <div class="content_right">
      <div class="buttonClass" @click="clickButArrow('arrowLeft')"><el-icon class="text"><ArrowLeft/></el-icon></div>
      <div class="buttonClass noSelect">
        <el-popover :show-arrow="false" :width="260" placement="bottom" popper-class="route_popover" trigger="click">
          <template #reference><span class="text">三</span></template>
          <div class="more_list">
            <div class="more_list_top">
              <div class="list_li" @click="clickCloseActiveRoute">关闭标签</div>
              <div class="list_li" @click="clickCloseOtherRoute">关闭其他标签</div>
              <div class="list_li" @click="clickCloseAllRoute">关闭全部标签</div>
            </div>
            <template v-if="visitedViews && visitedViews.length > maxNumber">
              <div class="more_line"><div class="line"></div></div>
              <div class="more_list_bottom scrollbarStyle">
                <template v-for="(item,index) in visitedViews" :key="index">
                  <template v-if="index >= maxNumber && item.name !== 'homePage'">
                    <div :class="['list_li',item.name === route.name ? 'active_route': '']" @click="clickOpenRoute(item)">
                      <div class="list_li_left textTwo">{{ item.query && item.query.subTitle ? item.query.subTitle : item.meta.title }}</div>
                      <div class="list_li_right" @click.stop="clickCloseRoute(item)"><el-icon size="18"><Close/></el-icon></div>
                    </div>
                  </template>
                </template>
              </div>
            </template>
          </div>
        </el-popover>
      </div>
      <div class="buttonClass" @click="clickButArrow('arrowRight')">
        <el-icon class="text"><ArrowRight/></el-icon>
      </div>
    </div>

  </div>
</template>

<script>
import { useTagsViewStore } from '@/stores/index';

import {getLeftTreeDataFun} from "@/utils";
import { useRouter, useRoute } from "vue-router";
import {ArrowLeft, Close, ArrowRight} from '@element-plus/icons-vue';
import { computed, defineComponent, reactive, toRefs, watch, ref } from "vue";

export default defineComponent({
  name: "TagsView",
  components: {ArrowLeft, Close, ArrowRight},
  setup() {
    const tagsViewStore = useTagsViewStore();
    const route = useRoute();
    const vueRouter = useRouter();

    const visitedViews = computed(() => {
      return tagsViewStore.visitedviews
    });

    const routeViews = computed(()=>{
      return { name: route.name, path: route.path, meta: route.meta,query: route.query }
    })

    // 缓存的路由（需要返回的页面）
    const backButArray = computed(() => {
      return tagsViewStore.backButArray
    });

    const that = reactive({
      maxNumber: 5, // 页面可视标签数量
      isShowBackBut: false, // 是否显示返回按钮
    });

    // 点击返回按钮
    const clickBackBut = () => {
      const findItem = backButArray.value.find(item => item.showButRoute === route.path);
      tagsViewStore.delVisitedViews(routeViews.value).then((views)=>{
        vueRouter.push({ path: findItem.backRouteName });
      })
    }

    // 点击回到第一个模块下的第一个页面
    const clickHomeBut = ()=>{
      let routerName = "/blank";
      let isHomePageArray = getLeftTreeDataFun("/homePage/index",1,0);
      // console.log(isHomePageArray);
      if(isHomePageArray && isHomePageArray.length)routerName = "/homePage/index";
      vueRouter.push({ path: routerName });
      // emit("clickEvent",{ type:"clickHomeBut" });
    }

    // 点击打开对应的缓存路由
    const clickOpenRoute = (data)=>{
      // 当前路由不可点击
      if (JSON.stringify(routeViews.value) === JSON.stringify(data))return;
      vueRouter.push({ name: data.name,query: data.query });
    }

    // 点击关闭某个标签卡
    const clickCloseRoute = (data)=>{
      tagsViewStore.delVisitedViews(data).then(views=>{
        //只有在关闭当前打开的标签页才会有影响
        if(isActiveRoute(data)){
          let query = {};
          let routerName = ""; // 默认回归到首页
          let lastView = views.slice(-1)[0]; //选取路由数组中的最后一位
          if (lastView){
            query = lastView.query;
            routerName = lastView.name;
          }

          if(routerName){
            vueRouter.push({ name: routerName,query: query });
            return
          }

          clickHomeBut(); // 返回空白页
        }
      })
    }

    // 点击左右切换页卡
    const clickButArrow = (type)=>{
      if(visitedViews.value && visitedViews.value.length){
        for(let i = 0;i < visitedViews.value.length;i++){
          if (JSON.stringify(visitedViews.value[i]) === JSON.stringify(routeViews.value)) {
            let routeIndex = i;

            if(type === "arrowLeft"){
              routeIndex -= 1;
              if(routeIndex < 0)routeIndex = visitedViews.value.length - 1;
            }

            if(type === "arrowRight"){
              routeIndex += 1;
              if(routeIndex >= visitedViews.value.length)routeIndex = 0;
            }

            let findItem = visitedViews.value[routeIndex];
            if(findItem && routeIndex !== i)vueRouter.push({ name: findItem.name,query: findItem.query });
            break
          }
        }
      }
    }

    // 关闭当前路由
    const clickCloseActiveRoute = ()=>{
      clickCloseRoute(routeViews.value);
    }

    // 点击关闭所有标签
    const clickCloseAllRoute = ()=>{
      tagsViewStore.delAllRouteViews([]).then((views)=>{
        // console.log(views);
        vueRouter.push({ path: '/homePage/index' });
      })
    }

    // 关闭其他标签
    const clickCloseOtherRoute = ()=>{
      tagsViewStore.delOthersViews(routeViews.value);
    }

    // 判断是否是当前路由
    const isActiveRoute = (data)=> {
      return data.path === route.path;
    }

    const recordRouteFun = (data)=> {
      tagsViewStore.addVisitedViews(data);

      // 设置显示按钮是否显示
      let isShowBackBut = false;
      if(backButArray.value && backButArray.value.length){
        const findItem = backButArray.value.find(item => item.showButRoute === route.path);
        if(findItem)isShowBackBut = true;
      }
      that.isShowBackBut = isShowBackBut;
    }

    // 监听路由发生改变执行
    const watchRoute = watch(()=>route,()=>{
      recordRouteFun(route);
    },{ deep:true,immediate:true })

    // 计算可视标签个数
    const routeListRef = ref(null);
    const setMaxNumberFun = () => {
      let clientWidth = Number(routeListRef.value.clientWidth);
      that.maxNumber = parseInt(clientWidth / 170);
    }

    return {...toRefs(that), route, routeListRef, setMaxNumberFun, clickBackBut, clickButArrow,visitedViews,routeViews,recordRouteFun, clickCloseOtherRoute,
      isActiveRoute,watchRoute,clickOpenRoute,clickCloseRoute,clickHomeBut,clickCloseActiveRoute, clickCloseAllRoute,backButArray}
  }
})
</script>

<style lang="scss" scoped>
.tagsView {
  height: 100%;
  display: flex;
  align-items: center;

  :deep(.el-page-header) {
    .el-page-header__left{
      margin-right: 0;
    }
  }

  .content_center {
    flex: 1;
    width: 2px;
    height: 100%;

    .content_route_list{
      height: 100%;
      width: fit-content;
      display: flex;
      align-items: center;

      .route_list{
        width: 170px;
        height: 100%;
        position: relative;
        padding-left: 16px;
        box-sizing: border-box;
        display: flex;
        flex-direction: column;

        .route_li{
          flex: 1;
          display: flex;
          align-items: center;
          justify-content: space-between;
          opacity: 0.8;
          color: #333333;
          font-size: 14px;

          .routeName{
            flex: 1;
            padding-right: 10px;
            -webkit-line-clamp: 1;
            box-sizing: border-box;
          }

          .close{
            width: 32px;
            height: 100%;
            font-size: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }

        .route_line{
          width: 100%;
          height: 1px;
          background: #1E71EC;
          position: absolute;
          left: 0;bottom: 0;
          display: none;
        }
      }

      .active_route{
        background-color: rgba(30,113,236,0.6);

        .route_li{
          color: #FFFFFF;
          font-weight: bold;
        }

        .route_line{
          display: none;
        }
      }
    }

    .menu_line{
      width: 1px;
      height: 20px;
      background: rgba(189,202,212,0.4);
    }
  }

  .content_right {
    display: flex;
    align-items: center;
    position: relative;
    padding-left: 12px;
    box-sizing: border-box;

    .buttonClass {
      width: 21px;
      height: 21px;
      cursor: pointer;
      border-radius: 4px;
      border: 1px solid #2A424F;
      margin-right: 8px;

      opacity: 0.8;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .buttonClass:last-child {
      margin-right: 0;
    }
  }
}

:deep(.el-popper){
  border-radius: 4px;
}

.more_list{

  .more_list_top{

    .list_li{
      height: 32px;
      display: flex;
      align-items: center;
      font-size: 14px;

      &:hover{
        cursor: pointer;
        color: #1E71EC;
        font-weight: bold;
      }
    }
  }

  .more_line{
    width: 100%;
    padding: 12px 0;
    box-sizing: border-box;

    .line{
      height: 1px;
      background: rgba(0,0,0,.6);
    }
  }

  .more_list_bottom{
    max-height: 300px;
    overflow-y: auto;

    .list_li{
      height: 32px;
      display: flex;
      align-items: center;
      padding-left: 12px;
      box-sizing: border-box;
      margin-bottom: 2px;
      opacity: 0.8;
      font-size: 14px;

      .list_li_left{
        flex: 1;
        padding-right: 10px;
        -webkit-line-clamp: 1;
        box-sizing: border-box;
      }

      .list_li_right{
        width: 32px;
        height: 100%;
        display: none;
        align-items: center;
        justify-content: center;
      }

      &:hover{
        cursor: pointer;
        color: #1E71EC;
        background-color: rgba(30,113,236,0.6);

        .list_li_right{
          display: flex;
        }
      }
    }

    .active_route{
      color: #1E71EC;
      background-color: rgba(30,113,236,0.6);

      .list_li_right{
        display: flex;
      }
    }
  }
}
</style>
