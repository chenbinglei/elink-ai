<template>
  <div class="currentElementAnimation">
    <template v-if="activePelDate.animations && activePelDate.animations.length">
      <lx-collapse v-for="(item,index) in activePelDate.animations" :key="index">
        <template #title>
          <div class="collapse_title">
            <div class="collapse_title_left flex-all">
              <el-input v-model="item.name" style="width: 80px" placeholder="请输入" size="small"></el-input>
              <div class="flex-ai-center pointer" @click.stop="clickAnimationButFun(index)">
                <span class="iconfont icon-bofang" v-if="activePelDate.currentAnimation !== index"></span>
                <span class="iconfont icon-jieshu" v-else></span>
              </div>
            </div>
            <div class="collapse_title_right">
              <template v-if="activePelDate.type !== 1 && item.animateId">
                <span class="iconfont icon-bianji" @click.stop="clickItemButton(1,index)"></span>
              </template>
              <el-popover v-model:visible="visibleObj['visible' + index]" placement="left-start" :width="180" trigger="contextmenu">
                <template #reference>
                  <span class="iconfont icon-shanchu" @click.stop="visibleObj['visible' + index] = true"></span>
                </template>
                <div style="margin-bottom: 4px">
                  <span class="iconfont icon-tishi" style="margin-right: 4px;color: #007FEB"></span>
                  <span style="color: #666666;font-size: 14px">确认删除该动画吗？</span>
                </div>
                <div class="flex-ai-center jc-end">
                  <el-button size="small" text @click="visibleObj['visible' + index] = false">取消</el-button>
                  <el-button size="small" type="primary" @click.stop="clickItemButton(2,index)">确定</el-button>
                </div>
              </el-popover>
            </div>
          </div>
        </template>
        <template #content>
            <div class="content_body_list">
              <PenAnimateConfigCom v-model:animateInfo="activePelDate.animations[index]" :activePelDate="activePelDate" :animateIndex="index" @changeEvent="setValueCanvasMeta2dPenFun" />
            </div>
        </template>
      </lx-collapse>
      <div class="bottom_button">
        <el-button class="whiteFontButtons" @click="clickAddAnimation">添加动画</el-button>
      </div>
    </template>
    <template v-else>
      <null-data words="请先添加动画">
        <template #content>
          <el-button class="whiteFontButtons" @click="clickAddAnimation">添加动画</el-button>
        </template>
      </null-data>
    </template>

    <AnimateFrameConfigDialog v-if="animateFrameConfigVisible" v-model:isVisible="animateFrameConfigVisible" :titleName="titleName" :activeFrame="activeFrame" @changeEvent="changeEvent" />
  </div>
</template>

<script>
import { useMeta2dStore } from '@/stores/index';

import {deepClone} from "@meta2d/core";
import {LxCollapse, LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, computed, watch, nextTick} from "vue";
import {AnimateFrameConfigDialog, PenAnimateConfigCom} from "./CurrentElementAnimation/index";

export default defineComponent({
  name: "CurrentElementAnimation",
  components:{LxInputColorPicker,LxCollapse,AnimateFrameConfigDialog,PenAnimateConfigCom},
  setup() {

    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const current_active_pel_list = computed(() => {
      return meta2dStore.current_active_pel_list;
    });

    const current_active_pel_num = computed(() => {
      return meta2dStore.current_active_pel_num;
    });

    const delete_field_list = computed(() => {
      return meta2dStore.setValueDeleteFieldList;
    });

    const that = reactive({
      visibleObj: {},
      activePelDate: {},

      titleName: "",
      activeFrame: [],
      activeOperateIndex: 0,
      animateFrameConfigVisible: false,
    })

    const findPenIdConfigFun = ()=>{
      // console.log(current_active_pel_list);
      let pen_id = current_active_pel_list.value[0];
      let activePelDate = canvasMeta2d.value.findOne(pen_id);
      const penRect = canvasMeta2d.value.getPenRect(activePelDate);
      let clonePelDate = deepClone(activePelDate ? activePelDate : {});
      that.activePelDate = JSON.parse(JSON.stringify({...deepClone(clonePelDate), ...penRect}));
      // console.log(that.activePelDate);
    };

    const clickAddAnimation = ()=>{
      if(!that.activePelDate.animations)that.activePelDate.animations = [];
      that.activePelDate.animations.push({ name:`动画${ that.activePelDate.animations.length + 1 }` });
      setValueCanvasMeta2dPenFun();
    }

    const clickItemButton = (operateType,index)=>{

      if(operateType === 1){
        that.activeOperateIndex = index;
        that.titleName = that.activePelDate.animations[index].name;
        that.activeFrame = that.activePelDate.animations[index]?.frames ?? [];
        that.animateFrameConfigVisible = true;
      }

      if(operateType === 2){
        that.visibleObj['visible' + index] = false;
        that.activePelDate.animations.splice(index,1);

        // 如果播放的是当前动画，就停止播放
        if(that.activePelDate.currentAnimation === index){
          that.activePelDate.currentAnimation = "";
          setValueCanvasMeta2dPenFun();
          nextTick(()=>canvasMeta2d.value.stopAnimate(that.activePelDate.id));
        }
      }
    }

    // 播放暂停动画
    const clickAnimationButFun = (index)=>{
      let currentAnimation = "";
      if(that.activePelDate.currentAnimation !== index) currentAnimation = index;
      that.activePelDate.currentAnimation = currentAnimation;
      setValueCanvasMeta2dPenFun();

      nextTick(()=>canvasMeta2d.value.startAnimate(that.activePelDate.id,currentAnimation));
    }

    const changeEvent = (data)=>{
      if(data.type === "AnimateFrameConfigDialog"){
        that.activePelDate.animations[that.activeOperateIndex].frames = JSON.parse(JSON.stringify(data.frames));
        // console.log(that.activePelDate.animations);
        setValueCanvasMeta2dPenFun();
      }
    }

    const setValueCanvasMeta2dPenFun = ()=>{
      // 删除对应的视图数据
      let active_pel_date = JSON.parse(JSON.stringify(that.activePelDate));
      for(let i = 0;i < delete_field_list.value.length;i++) delete active_pel_date[delete_field_list.value[i]];
      canvasMeta2d.value.setValue(active_pel_date);
    }

    const watchCurrentActivePelList = watch([() => current_active_pel_list, () => current_active_pel_num], ([newCurrentActivePelList]) => {
      if(newCurrentActivePelList.value && newCurrentActivePelList.value.length) findPenIdConfigFun();
    }, {deep: true, immediate: true})

    return {...toRefs(that), canvasMeta2d, current_active_pel_list, watchCurrentActivePelList, clickAddAnimation, clickItemButton, clickAnimationButFun,
      changeEvent, delete_field_list, current_active_pel_num, setValueCanvasMeta2dPenFun}
  }
})
</script>

<style lang="scss" scoped>
.currentElementAnimation{
  height: 100%;

  .collapse_title{
    width: 100%;
    display: flex;
    align-items: center;

    .collapse_title_left{
      display: flex;
      align-items: center;
      justify-content: flex-start;
    }

    .iconfont{
      margin-left: 10px;
    }
  }

  .content_body_list{
    padding: 0 12px 0 16px;
    box-sizing: border-box;
  }

  .bottom_button{
    padding: 0 10px;
    box-sizing: border-box;

    .el-button{
      width: 100%;
    }
  }
}

:deep(.nullData){
  height: 100%;

  .noCartImages {
    max-width: 120px;
  }

  .textFont {
    font-size: 14px;
    margin-bottom: 10px;
    color: var(--color-gray);
  }
}
</style>