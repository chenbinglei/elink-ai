<template>
  <el-popover :width="200" placement="bottom-start" trigger="hover">
    <template #reference>
      <div class="canvasToolShortcutCom pointer">工具</div>
    </template>
    <div class="content_list">
      <template v-for="(item,index) in list" :key="index">
        <div v-if="item.fieldName" class="content_list_li" @click="clickItemFun(item.fieldName)">
          <div class="content_list_li_left">{{ item.name }}</div>
          <div class="content_list_li_right">
            <span v-if="item.iconStatus" class="iconfont icon-duihao"></span>
          </div>
        </div>
        <div v-else class="content_list_line"></div>
      </template>
    </div>
  </el-popover>
</template>
<script lang="ts">
import { useMeta2dStore } from '@/stores/index';

import {reactive, toRefs, defineComponent, computed} from "vue";

export default defineComponent({
  name: "CanvasToolShortcutCom",
  setup() {

    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const that = reactive({
      list: [
        {name: "放大", fieldName: "amplify"},
        {name: "缩小", fieldName: "reduce"},
        {name: "100%视图", fieldName: "bfbView"},
        {},
        {name: "缩略图", fieldName: "thumbnail", iconStatus: false},
        {name: "放大镜", fieldName: "magnifier", iconStatus: false},
        {},
        {name: "显示锚点", fieldName: "displayAnchor", iconStatus: true},
        {name: "自动锚点", fieldName: "autoAnchor", iconStatus: false},
        {},
        {name: "添加/删除锚点", fieldName: "toggleAnchor"},
      ]
    })

    const clickItemFun = (fieldName) => {

      if (fieldName === "amplify") {
        const canvasData = canvasMeta2d.value.data();
        let scaleValue = canvasData.scale += 0.1;
        if (scaleValue >= 10) scaleValue = 10;
        canvasMeta2d.value.scale(scaleValue);
      }

      if (fieldName === "reduce") {
        const canvasData = canvasMeta2d.value.data();
        let scaleValue = canvasData.scale -= 0.1;
        if (scaleValue <= 0.2) scaleValue = 0.2;
        canvasMeta2d.value.scale(scaleValue);
      }

      if (fieldName === "bfbView") {
        canvasMeta2d.value.scale(1);
        canvasMeta2d.value.screenView(10); //大屏范围居中视图
      }

      if (fieldName === "thumbnail") {
        let findIndex = that.list.findIndex(item => item.fieldName === fieldName);
        let iconStatus = that.list[findIndex].iconStatus;

        if (iconStatus) canvasMeta2d.value.hideMap();
        if (!iconStatus) canvasMeta2d.value.showMap();
        that.list[findIndex].iconStatus = !iconStatus;
      }

      if (fieldName === "magnifier") {
        let findIndex = that.list.findIndex(item => item.fieldName === fieldName);
        let iconStatus = that.list[findIndex].iconStatus;

        if (iconStatus) canvasMeta2d.value.hideMagnifier();
        if (!iconStatus) canvasMeta2d.value.showMagnifier();
        that.list[findIndex].iconStatus = !iconStatus;
      }

      if (fieldName === "displayAnchor") {
        let findIndex = that.list.findIndex(item => item.fieldName === fieldName);
        let iconStatus = that.list[findIndex].iconStatus;
        that.list[findIndex].iconStatus = !iconStatus;
        canvasMeta2d.value.setOptions({disableAnchor: !!iconStatus});
      }

      if (fieldName === "autoAnchor") {
        let findIndex = that.list.findIndex(item => item.fieldName === fieldName);
        let iconStatus = that.list[findIndex].iconStatus;
        that.list[findIndex].iconStatus = !iconStatus;
        canvasMeta2d.value.setOptions({autoAnchor: !!iconStatus});
      }

      if (fieldName === "toggleAnchor") canvasMeta2d.value.toggleAnchorMode();
    }

    return {...toRefs(that), clickItemFun}
  }
})
</script>
<style lang="scss" scoped>
.canvasToolShortcutCom {
  padding: 0 10px;
  margin: 0 10px 0 16px;
}

.content_list {
  width: 100%;

  .content_list_li {
    height: 35px;
    padding: 0 12px;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: var(--color);
    font-size: 12px;

    .iconfont {
      font-size: 12px;
    }

    &:hover {
      cursor: pointer;
      background: var(--el-menu-hover-bg-color);
    }
  }

  .content_list_line {
    height: 1px;
    margin: 4px 0;
    background-color: #EAEEF1;
  }
}
</style>