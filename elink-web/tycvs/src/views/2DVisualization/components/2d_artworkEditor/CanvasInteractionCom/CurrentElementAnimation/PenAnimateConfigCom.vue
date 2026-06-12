<template>
  <el-form :model="animate_info" label-width="auto">
    <template v-if="activePelDate.type !== 1">
      <el-form-item label="动画类型">
        <el-select v-model="animate_info.animateId" size="small" @change="animateChangeFun">
          <el-option v-for="ts in animateArray" :key="ts.value" :label="ts.name" :value="ts.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="播放次数">
        <el-input v-model="animate_info.animateCycle" placeholder="无限次" size="small" type="number" @change="updateActiveAnimateConfigFun"></el-input>
      </el-form-item>
      <el-form-item label="结束状态">
        <el-select v-model="animate_info.keepAnimateState" clearable placeholder="初始状态" size="small" @change="updateActiveAnimateConfigFun">
          <el-option v-for="ts in keepAnimateStateArray" :key="ts.id" :label="ts.name" :value="ts.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="线性播放">
        <el-select v-model="animate_info.linear" clearable size="small" @change="updateActiveAnimateConfigFun">
          <el-option v-for="ts in linear_list" :key="ts.id" :label="ts.name" :value="ts.id"></el-option>
        </el-select>
      </el-form-item>
    </template>

    <!--            线条动画-->
    <template v-if="activePelDate.type === 1">
      <el-form-item label="动画类型">
        <el-select v-model="animate_info.lineAnimateType" size="small" @change="updateActiveAnimateConfigFun">
          <el-option v-for="ts in lineAnimateArray" :key="ts.value" :label="ts.name" :value="ts.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="运动速度">
        <el-slider v-model="animate_info.animateSpan" :max="10" :min="0" :step="0.1" size="small" @change="updateActiveAnimateConfigFun"></el-slider>
      </el-form-item>
      <el-form-item v-if="animate_info.lineAnimateType === 2" label="圆点大小">
        <el-input v-model="animate_info.animateDotSize" min="6" placeholder="最小值 6" size="small" type="number" @change="updateActiveAnimateConfigFun" />
      </el-form-item>
      <el-form-item label="动画颜色">
        <lx-input-color-picker v-model:color="animate_info.animateColor" @changEvent="updateActiveAnimateConfigFun" />
      </el-form-item>
      <el-form-item label="发光效果">
        <el-switch v-model="animate_info.animateShadow" :active-value="true" :inactive-value="false" size="small" @change="updateActiveAnimateConfigFun" />
      </el-form-item>
      <template v-if="animate_info.animateShadow">
        <el-form-item label="发光颜色">
          <lx-input-color-picker v-model:color="animate_info.animateShadowColor" @changEvent="updateActiveAnimateConfigFun" />
        </el-form-item>
        <el-form-item label="发光模糊">
          <el-input-number v-model="animate_info.animateShadowBlur" controls-position="right" placeholder="默认6" size="small" @change="updateActiveAnimateConfigFun" />
        </el-form-item>
      </template>
      <el-form-item label="轨迹宽度">
        <el-input-number v-model="animate_info.animateLineWidth" controls-position="right" placeholder="默认6" size="small" @change="updateActiveAnimateConfigFun"/>
      </el-form-item>
      <el-form-item label="反向流动">
        <el-switch v-model="animate_info.animateReverse" :active-value="true" :inactive-value="false" size="small" @change="updateActiveAnimateConfigFun" />
      </el-form-item>
      <el-form-item label="播放次数">
        <el-input v-model="animate_info.animateCycle" placeholder="无限次" size="small" @change="updateActiveAnimateConfigFun" />
      </el-form-item>
    </template>
    <el-form-item label="自动播放">
      <el-switch v-model="animate_info.autoPlay" :active-value="true" :inactive-value="false" size="small" @change="updateActiveAnimateConfigFun" />
    </el-form-item>
    <el-form-item label="下个类型">
      <el-radio-group v-model="animate_info.temType" @change="animateTemTypeChangeFun">
        <el-radio v-for="ts in temTypeArray" :key="ts.id" :value="ts.id" size="small">{{ ts.name }}</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="下个动画">
      <template v-if="animate_info.temType === 'tag'">
        <el-select v-model="animate_info.nextAnimate" clearable size="small" @change="updateActiveAnimateConfigFun">
          <el-option v-for="ts in combinePenList" :key="ts.id" :label="ts.chineseName" :value="ts.id"></el-option>
        </el-select>
      </template>
      <template v-else>
        <el-select v-model="animate_info.nextAnimate" clearable size="small" @change="updateActiveAnimateConfigFun">
          <el-option v-for="ts in canvasPenList" :key="ts.id" :label="ts.chineseName" :value="ts.id"></el-option>
        </el-select>
      </template>
      <!--      <el-input v-model="animate_info.nextAnimate" size="small" placeholder="图元id/tag"></el-input>-->
    </el-form-item>
  </el-form>
</template>

<script>
import { useMeta2dStore } from '@/stores/index';

import {LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, onMounted, computed, watch, getCurrentInstance, nextTick} from "vue";

export default defineComponent({
  name: "PenAnimateConfigCom",
  components: {LxInputColorPicker},
  emits: ["changeEvent","update:animateInfo"],
  props: {
    activePelDate: {
      type: Object,
      default: () => {
        return {}
      }
    },
    animateInfo: {
      type: Object,
      default: () => {
        return {}
      }
    },
    animateIndex: {
      type: Number,
      default: 0
    },
  },
  setup(props) {

    const meta2dStore = useMeta2dStore();
    const {emit} = getCurrentInstance();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const that = reactive({
      animate_info: {},
      canvasPenList: [],
      combinePenList: [],
      linear_list: [{id: true, name: "是"}, {id: false, name: "否"}],
      temTypeArray: [{id: "id", name: "图元"}, {id: "tag", name: "组"}],
      keepAnimateStateArray: [{id: false, name: "初始动画"}, {id: true, name: "当前动画"}],
      lineAnimateArray: [{name: "水流", value: 0}, {name: "水珠流动", value: 1}, {name: "圆点", value: 2}, {name: "箭头", value: 3}, {name: "水滴", value: 4}],

      animateArray: [
        {name: "闪烁", value: "flicker", frames: [{duration: 300, visible: true}, {duration: 300, visible: false}]},
        {name: "缩放", value: "scale", frames: [{duration: 100, scale: 1.1}, {duration: 400, scale: 1}]},
        {name: "旋转", value: "rotate", frames: [{duration: 1000, rotate: 360}]},
        {name: "逆向旋转", value: "reverseRotate", frames: [{duration: 1000, rotate: -360}]},
        {name: "颜色变化", value: "colorChange", frames: [{duration: 200, color: "#4583ff"}, {duration: 200, color: "#ff4000"}]},
        {name: "背景变化", value: "backgroundChange", frames: [{duration: 300, background: "#4583ff"}, {duration: 300, background: "#ff4000"}]},
        {name: "文字变化", value: "textChange", frames: [{duration: 200, text: "乐吾乐"}, {duration: 200, text: "le5le"}]},
        {name: "状态变化", value: "stateChange", frames: [{duration: 200, showChild: 0}, {duration: 200, showChild: 1}]},
        {name: "自定义", value: "custom", frames: []}
      ],
    })

    // 动画发生改变执行
    const animateChangeFun = ()=>{
      let findItem = that.animateArray.find(item=> item.value === that.animate_info.animateId);
      that.animate_info.frames = findItem.frames ?? [];
      that.animate_info.animate = findItem.name ?? "";
      updateActiveAnimateConfigFun();
    }

    const animateTemTypeChangeFun = ()=>{
      that.animate_info.nextAnimate = "";
      updateActiveAnimateConfigFun();
    }

    const updateActiveAnimateConfigFun = ()=>{
      emit("update:animateInfo",that.animate_info);
      emit("changeEvent");

      nextTick(()=>{
        // 是否重新播放动画
        if(props.activePelDate.currentAnimation === props.animateIndex){
          canvasMeta2d.value.startAnimate(props.activePelDate.id,props.animateIndex);// 播放动画
        }
      })
    }

    // 查找画布上所有图元
    const queryCanvasMeta2dPens = () => {
      let canvasPenList = [], combinePenList = [];
      let {pens} = canvasMeta2d.value.data();
      // console.log(canvasMeta2dData);
      if (pens && pens.length) {
        for (let i = 0; i < pens.length; i++) {
          canvasPenList.push({id: pens[i].id, chineseName: pens[i].chineseName});
          if (pens[i].name === "combine") combinePenList.push({id: pens[i].id, chineseName: pens[i].chineseName});
        }
      }
      that.canvasPenList = JSON.parse(JSON.stringify(canvasPenList));
      that.combinePenList = JSON.parse(JSON.stringify(combinePenList));
    }

    const watchAnimateInfo = watch(() => props.animateInfo, (newAnimateInfo) => {
      if (JSON.stringify(that.animate_info) !== JSON.stringify(newAnimateInfo)) {
        that.animate_info = JSON.parse(JSON.stringify(newAnimateInfo));
      }
    }, {deep: true, immediate: true})

    onMounted(() => {
      queryCanvasMeta2dPens();
    })

    return {...toRefs(that), watchAnimateInfo, canvasMeta2d, queryCanvasMeta2dPens, animateChangeFun, updateActiveAnimateConfigFun, animateTemTypeChangeFun}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 8px;

  .lx-color-picker, .el-input-number {
    width: 100%;
  }
}
</style>