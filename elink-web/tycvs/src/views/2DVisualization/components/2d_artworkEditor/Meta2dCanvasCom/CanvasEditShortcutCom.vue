<template>
  <el-popover :width="200" placement="bottom-start" trigger="hover">
    <template #reference>
      <div class="canvasEditShortcutCom pointer">编辑</div>
    </template>
    <div class="content_list">
      <template v-for="(item,index) in list" :key="index">
        <div v-if="item.fieldName" class="content_list_li" @click="clickItemFun(item.fieldName)">
          <div class="content_list_li_left">{{ item.name }}</div>
          <div class="content_list_li_right">{{ item.shortcutKey }}</div>
        </div>
        <div v-else class="content_list_line"></div>
      </template>
    </div>
  </el-popover>
</template>
<script>
import {useStore} from "vuex";
import {reactive, toRefs, defineComponent, computed} from "vue";

export default defineComponent({
  name: "CanvasEditShortcutCom",
  setup() {
    const store = useStore();
    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    const that = reactive({
      list: [
        {name: "撤销", fieldName: "undo", shortcutKey: "Ctrl + Z"},
        {name: "重做", fieldName: "redo", shortcutKey: "Ctrl + Y"},
        {},
        {name: "剪切", fieldName: "cut", shortcutKey: "Ctrl + X"},
        {name: "复制", fieldName: "copy", shortcutKey: "Ctrl + C"},
        {name: "粘贴", fieldName: "paste", shortcutKey: "Ctrl + V"},
        {},
        {name: "全选", fieldName: "activeAll", shortcutKey: "Ctrl + A"},
        {name: "删除", fieldName: "delete", shortcutKey: "DELETE"},
      ]
    })

    const clickItemFun = (fieldName) => {
      if (fieldName === "undo") canvasMeta2d.value.undo();
      if (fieldName === "redo") canvasMeta2d.value.redo();
      if (fieldName === "cut") canvasMeta2d.value.cut();
      if (fieldName === "copy") canvasMeta2d.value.copy();
      if (fieldName === "paste") canvasMeta2d.value.paste();
      if (fieldName === "activeAll") canvasMeta2d.value.activeAll();
      if (fieldName === "delete") canvasMeta2d.value.delete();
    }

    return {...toRefs(that), clickItemFun, canvasMeta2d}
  }
})
</script>
<style lang="scss" scoped>
.canvasEditShortcutCom {
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