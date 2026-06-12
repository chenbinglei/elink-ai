<template>
  <div class="topToolbarCom">
    <div class="top_toolbar_left">
      <div class="back_class" @click="clickBackButFun">
        <span class="iconfont icon-zuojiantou"></span>
        <span class="split_line">|</span>
        <span class="text">返回管理页</span>
      </div>
      <CanvasEditShortcutCom ref="canvasEditShortcutComRef"></CanvasEditShortcutCom>
      <CanvasToolShortcutCom ref="CanvasToolShortcutComRef"></CanvasToolShortcutCom>
    </div>
    <div class="top_toolbar_center">
      <el-input v-model="fileName" placeholder="请输入文件名称" @change="changeFileNameFun"></el-input>
    </div>
    <div class="top_toolbar_right">
      <el-avatar :size="28" :src="userInfo.userProfile">
        <span v-if="userInfo.fullName">{{ userInfo.fullName.slice(0, 1) }}</span>
      </el-avatar>
      <div class="userName">{{ $filters.moreData(userInfo.fullName) }}</div>
    </div>
  </div>
</template>
<script>
import { useAppStore, useMeta2dStore } from '@/stores/index';

import {useRouter, useRoute} from "vue-router";
import {reactive, toRefs, defineComponent, computed,watch} from "vue";
import {CanvasEditShortcutCom, CanvasToolShortcutCom} from "./Meta2dCanvasCom/index";

export default defineComponent({
  name: 'TopToolbarCom',
  components: {CanvasEditShortcutCom, CanvasToolShortcutCom},
  setup() {

    const appStore = useAppStore();
    const meta2dStore = useMeta2dStore();
    const route = useRoute();
    const vueRouter = useRouter();
    const userInfo = computed(() => {
      return appStore.userInfo;
    });

    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const canvasMeta2dData = computed(() => {
      return meta2dStore.canvasMeta2dData;
    });

    const that = reactive({
      fileName: '新建图纸',
      activeRoutePath: route.path, // 当前页面路径
    })

    const changeFileNameFun = ()=>{
      let canvasData = JSON.parse(JSON.stringify(canvasMeta2dData.value));
      if(that.fileName){
        canvasData.name = that.fileName;
        meta2dStore.updateCanvasMeta2dData(canvasData);
      } else {
        that.fileName = canvasData.name;
      }
      // 设置图纸名称，svg下载使用
      canvasMeta2d.value.store.data.name = that.fileName;
    }

    const watchCanvasMeta2dData = watch(()=> canvasMeta2dData,(newCanvasMeta2dData)=>{
      that.fileName = newCanvasMeta2dData.value.name;
    },{ deep: true })

    // 后退一页  回去
    const clickBackButFun = () => {
      vueRouter.go(-1);
      setTimeout(() => {
        let activeRoutePath = route.path;
        // 检测当前页的路由是否还是原来的，如果是原来的则重新回退
        if (activeRoutePath === that.activeRoutePath) {
          clickBackButFun();
        }
      }, 20)
    }

    return {...toRefs(that), userInfo, clickBackButFun, canvasMeta2dData, watchCanvasMeta2dData, changeFileNameFun, canvasMeta2d}
  }
})
</script>

<style lang="scss" scoped>
.topToolbarCom {
  height: 40px;
  display: flex;
  align-items: center;
  padding: 0 12px;
  box-sizing: border-box;
  background-color: var(--color-background);
  border-bottom: 1px solid var(--color-border);

  .top_toolbar_left {
    display: flex;
    align-items: center;
    font-size: 14px;

    .back_class {
      cursor: pointer;
      padding: 2px 10px;
      border-radius: 4px;
      background: #F1F3FA;
      display: flex;
      align-items: center;

      .split_line {
        color: #666666;
        margin: 0 8px;
      }

      .text {
        font-size: 12px;
      }
    }
  }

  .top_toolbar_center {
    flex: 1;

    :deep(.el-input){
      --el-input-bg-color: none;

      .el-input__inner{
        text-align: center;
      }

      .iconfont {
        color: #1E71EC;
        margin-right: 10px;
      }
    }
  }

  .top_toolbar_right {
    display: flex;
    align-items: center;

    .userName {
      font-size: 14px;
      margin-left: 10px;
      color: rgba(18, 28, 63, 0.6);
    }
  }
}
</style>