<template>
  <el-popover v-model:visible="dialog_visible" :width="225" placement="right-end" :show-arrow="false" trigger="click">
    <template #reference>
      <div :style="{left:menu_left+'px',top:menu_top+'px'}" class="rightClickMenuCom"></div>
    </template>
    <div class="content_list">
      <template v-for="(item,index) in list" :key="index">
        <template v-if="item.fieldName">
          <template v-if="(current_active_pel_list.length > 1 && (item.fieldName === 'combine' || item.fieldName === 'combines')) ||
          (current_active_pel_list.length === 1 && item.fieldName === 'uncombine' && activePelDate.name === 'combine') ||
          ((current_active_pel_list.length === 1 && item.fieldName === 'lock' && !activePelDate.locked) || !current_active_pel_list.length && item.fieldName === 'lock') ||
          (current_active_pel_list.length === 1 && item.fieldName === 'unlock' && activePelDate.locked) ||
          (item.fieldName !== 'combine' && item.fieldName !== 'combines' && item.fieldName !== 'uncombine' && item.fieldName !== 'lock' && item.fieldName !== 'unlock')">
            <div class="content_list_li" @click="clickItemFun(item.fieldName)"
                 :class="{ no_click_class: !current_active_pel_list.length && item.fieldName !== 'undo' && item.fieldName !== 'paste' }"
            >
              <div class="content_list_li_left">{{ item.name }}</div>
              <div class="content_list_li_right">{{ item.shortcutKey }}</div>
            </div>
          </template>
        </template>
        <div v-else class="content_list_line"></div>
      </template>
    </div>
  </el-popover>

</template>

<script lang="ts">
import { useMeta2dStore } from '@/stores/index';

import {deepClone} from "@meta2d/core";
import {reactive, toRefs, defineComponent, computed, getCurrentInstance, watch} from "vue";

export default defineComponent({
  name: "RightClickMenuCom",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    rightClickMenuX: {
      type: Number,
      default: 0
    },
    rightClickMenuY: {
      type: Number,
      default: 0
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const current_active_pel_list = computed(() => {
      return meta2dStore.current_active_pel_list;
    });

    const that = reactive({
      pens: [],
      activePelDate: {},
      menu_top: props.rightClickMenuY,
      menu_left: props.rightClickMenuX,
      dialog_visible: props.isVisible,

      list:[
        {name: "置顶", fieldName: "top", shortcutKey: ""},
        {name: "置底", fieldName: "bottom", shortcutKey: ""},
        {name: "上一个图层", fieldName: "up", shortcutKey: ""},
        {name: "下一个图层", fieldName: "down", shortcutKey: ""},
        {},
        {name: "组合", fieldName: "combine", shortcutKey: ""},
        {name: "组合为状态", fieldName: "combines", shortcutKey: ""},
        {name: "取消组合", fieldName: "uncombine", shortcutKey: ""},
        {name: "锁定", fieldName: "lock", shortcutKey: ""},
        {name: "取消锁定", fieldName: "unlock", shortcutKey: ""},
        {},
        {name: "删除", fieldName: "delete", shortcutKey: "DELETE"},
        {},
        {name: "撤销", fieldName: "undo", shortcutKey: "Ctrl + Z"},
        {name: "重做", fieldName: "redo", shortcutKey: "Shift + Z"},
        {},
        {name: "剪切", fieldName: "cut", shortcutKey: "Ctrl + X"},
        {name: "复制", fieldName: "copy", shortcutKey: "Ctrl + C"},
        {name: "粘贴", fieldName: "paste", shortcutKey: "Ctrl + V"},
      ]

    })

    const clickItemFun = (fieldName)=>{

      if(!current_active_pel_list.value.length && fieldName !== 'undo' && fieldName !== 'paste'){
        return
      }

      if (fieldName === "top") canvasMeta2d.value.top(that.activePelDate);
      if (fieldName === "bottom") canvasMeta2d.value.bottom(that.activePelDate);
      if (fieldName === "up") canvasMeta2d.value.up(that.activePelDate);
      if (fieldName === "down") canvasMeta2d.value.down(that.activePelDate);

      if (fieldName === "undo") canvasMeta2d.value.undo();
      if (fieldName === "redo") canvasMeta2d.value.redo();
      if (fieldName === "cut") canvasMeta2d.value.cut();
      if (fieldName === "copy") canvasMeta2d.value.copy();
      if (fieldName === "paste") canvasMeta2d.value.paste();
      if (fieldName === "delete") canvasMeta2d.value.delete();

      if (fieldName === "lock"){
        that.activePelDate.locked = 2;
        canvasMeta2d.value.setValue(that.activePelDate);
      }

      if (fieldName === "unlock"){
        that.activePelDate.locked = 0;
        canvasMeta2d.value.setValue(that.activePelDate);
      }

      if (fieldName === "combine"){
        canvasMeta2d.value.combine(that.pens);
      }

      if (fieldName === "combines"){
        canvasMeta2d.value.combine(that.pens,0);
      }

      if (fieldName === "uncombine"){
        canvasMeta2d.value.uncombine(that.activePelDate);
        canvasMeta2d.value.inactive();
      }

      that.dialog_visible = false;
    }

    const findPenIdConfigFun = () => {
      // console.log(current_active_pel_list);
      let pen_id = current_active_pel_list.value[0];
      let activePelDate = canvasMeta2d.value.findOne(pen_id);
      const penRect = canvasMeta2d.value.getPenRect(activePelDate);
      // console.log(penRect)
      let clonePelDate = deepClone(activePelDate ? activePelDate : {});
      that.activePelDate = Object.assign({}, clonePelDate, penRect);
      // console.log(that.activePelDate);
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    });

    const watchLeftAndTop = watch([()=>props.rightClickMenuX,()=>props.rightClickMenuY],([newMenuLeft,newMenuTop])=>{
      that.menu_top = newMenuTop;
      that.menu_left = newMenuLeft;
    },{ deep: true })

    const watchCurrentActivePelList = watch(() => current_active_pel_list, (newCurrentActivePelList) => {
      // console.log(newCurrentActivePelList);
      let pens = [];
      that.activePelDate = {};
      if(newCurrentActivePelList.value && newCurrentActivePelList.value.length){
        if(newCurrentActivePelList.value.length === 1) findPenIdConfigFun();
        for(let i = 0;i < newCurrentActivePelList.value.length;i++){
          let activePelDate = canvasMeta2d.value.findOne(newCurrentActivePelList.value[i]);
          pens.push(activePelDate);
        }
      }
      that.pens = pens;
    }, {deep: true,immediate: true});

    return {...toRefs(that), watchVisible, watchDialogVisible, watchLeftAndTop,canvasMeta2d,current_active_pel_list,watchCurrentActivePelList,
      clickItemFun, findPenIdConfigFun}

  }
})
</script>

<style lang="scss" scoped>
.rightClickMenuCom {
  z-index: 9999;
  position: absolute;
}

.content_list {
  width: 100%;
  --color: #737A8A;
  --color-gray: #bfbfbf;

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

  .no_click_class{
    color: var(--color-gray);

    &:hover{
      background: none;
      cursor: not-allowed;
    }
  }

  .content_list_line {
    height: 1px;
    margin: 4px 0;
    background-color: #EAEEF1;
  }
}
</style>