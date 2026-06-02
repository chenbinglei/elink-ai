<template>
  <el-form :model="active_pel_date" class="content_item_body" label-width="auto">
    <el-collapse-item name="11" title="其余属性">
      <el-form-item label="鼠标提示">
        <el-button :icon="Setting" size="small" @click="clickTooltipFun">设置</el-button>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="active_pel_date.flipX" @change="setValueCanvasMeta2dPenFun">水平翻转</el-checkbox>
        <el-checkbox v-model="active_pel_date.flipY" @change="setValueCanvasMeta2dPenFun">垂直翻转</el-checkbox>
      </el-form-item>
      <el-form-item label="锚点半径">
        <el-input type="number" v-model="active_pel_date.anchorRadius" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
    </el-collapse-item>
    <el-collapse-item name="12" title="禁止">
      <el-form-item label="是否禁止">
        <el-checkbox v-model="active_pel_date.disabled" @change="setValueCanvasMeta2dPenFun">禁用</el-checkbox>
      </el-form-item>
      <el-form-item>
        <div class="flex-ai-center">
          <el-checkbox v-model="active_pel_date.disableRotate" @change="setValueCanvasMeta2dPenFun">禁止旋转</el-checkbox>
          <el-checkbox v-model="active_pel_date.disableSize" @change="setValueCanvasMeta2dPenFun">禁止缩放</el-checkbox>
          <el-checkbox v-model="active_pel_date.disableAnchor" @change="setValueCanvasMeta2dPenFun">禁用锚点</el-checkbox>
        </div>
      </el-form-item>
      <el-form-item>
        <div class="flex-ai-center jc-space-between" style="width: 100%;">
          <lx-input-color-picker v-model:color="active_pel_date.disabledBackground" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>禁用背景</template>
          </lx-input-color-picker>
          <lx-input-color-picker v-model:color="active_pel_date.disabledColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>禁用颜色</template>
          </lx-input-color-picker>
          <lx-input-color-picker v-model:color="active_pel_date.disabledTextColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>禁用文字颜色</template>
          </lx-input-color-picker>
        </div>
      </el-form-item>
    </el-collapse-item>
    <PenTooltipDialog v-if="penTooltipVisible" v-model:isVisible="penTooltipVisible" :activeEditInfo="activeEditInfo" @changeEvent="changeEvent" />
  </el-form>
</template>

<script>
import {useStore} from "vuex";
import { Setting } from '@element-plus/icons-vue';
import PenTooltipDialog from "./PenTooltipDialog.vue";
import {LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, computed, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PenAdditionalConfigCom",
  components: {LxInputColorPicker, PenTooltipDialog},
  props: {
    activePelDate: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["update:activePelDate","changeEvent"],
  setup(props) {

    const store = useStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    const that = reactive({
      Setting,
      activeEditInfo: {},
      active_pel_date: {},
      penTooltipVisible: false,
    })

    // 设置鼠标提示语
    const clickTooltipFun = ()=>{
      that.activeEditInfo = {title: that.active_pel_date.title, titleFnJs: that.active_pel_date.titleFnJs}
      that.penTooltipVisible = true;
    }

    const changeEvent = (data)=>{
      // console.log(data);
      that.active_pel_date.titleFn = null;
      that.active_pel_date.title = data.title;
      that.active_pel_date.titleFnJs = data.titleFnJs;
      setValueCanvasMeta2dPenFun();
    }

    const setValueCanvasMeta2dPenFun = () => {
      emit("update:activePelDate", that.active_pel_date);
      emit("changeEvent");
    }

    const watchActivePelDate = watch(() => props.activePelDate, (newActivePelDate) => {
      if(JSON.stringify(that.active_pel_date) !== JSON.stringify(newActivePelDate)){
        that.active_pel_date = JSON.parse(JSON.stringify(newActivePelDate));
      }
    }, {deep: true,immediate: true})

    return {...toRefs(that), canvasMeta2d, watchActivePelDate, setValueCanvasMeta2dPenFun, changeEvent, clickTooltipFun}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 4px;
}
</style>