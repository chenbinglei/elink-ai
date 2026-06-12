<template>
  <el-collapse-item name="4" title="大屏对齐">
    <div class="content_icon_list">
      <template v-for="icon in align_list" :key="icon.fieldName">
        <div class="content_list_li">
          <el-tooltip :content="icon.name" effect="dark" placement="top-start">
            <div class="icon_li" @click="clickCanvasAlignIcon(icon.fieldName)">
              <span :class="icon.iconName" class="iconfont"></span>
            </div>
          </el-tooltip>
        </div>
      </template>
    </div>
  </el-collapse-item>
</template>

<script>
import { useMeta2dStore } from '@/stores/index';

import {reactive, toRefs, defineComponent, computed, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PenLargeScreenAlignmentCom",
  props: {
    activePelDate: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["changeEvent"],
  setup(props) {

    const meta2dStore = useMeta2dStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const that = reactive({
      align_list: [
        {name: "左对齐", fieldName: "left", iconName: "icon-align-left"},
        {name: "水平居中对齐", fieldName: "center", iconName: "icon-align-center"},
        {name: "右对齐", fieldName: "right", iconName: "icon-align-right"},
        {name: "顶部对齐", fieldName: "top", iconName: "icon-align-top"},
        {name: "垂直居中对齐", fieldName: "middle", iconName: "icon-align-middle"},
        {name: "底部对齐", fieldName: "bottom", iconName: "icon-align-bottom"},
      ]
    })

    // 对齐
    const clickCanvasAlignIcon = (align) => {
      let pens = canvasMeta2d.value.find(props.activePelDate.id);
      if (pens.length) canvasMeta2d.value.alignNodesV(align, pens);
      emit("changeEvent");
    }

    return {...toRefs(that), canvasMeta2d, clickCanvasAlignIcon}
  }
})
</script>

<style lang="scss" scoped>
.content_icon_list {
  width: 100%;
  display: flex;
  align-items: center;

  .content_list_li {
    cursor: pointer;
    margin-right: 10px;

    &:last-child {
      margin-right: 0;
    }
  }
}
</style>