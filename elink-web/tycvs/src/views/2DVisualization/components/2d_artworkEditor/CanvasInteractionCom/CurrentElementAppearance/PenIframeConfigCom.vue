<template>
  <el-collapse-item name="9" title="iframe属性">
    <el-form :model="active_pel_date" class="content_item_body">
      <el-form-item label="网页地址">
        <el-input type="textarea" v-model="active_pel_date.iframe" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
      <el-form-item label="dom层级">
        <el-input v-model="active_pel_date.zIndex" placeholder="默认4" size="small" type="number" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
      <el-form-item label="可操作X">
        <el-input v-model="active_pel_date.operationalRect.x" placeholder="范围0-1" size="small" type="number" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
      <el-form-item label="可操作Y">
        <el-input v-model="active_pel_date.operationalRect.y" placeholder="范围0-1" size="small" type="number" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
      <el-form-item label="可操作宽">
        <el-input v-model="active_pel_date.operationalRect.width" placeholder="范围0-1" size="small" type="number" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
      <el-form-item label="可操作高">
        <el-input v-model="active_pel_date.operationalRect.height" placeholder="范围0-1" size="small" type="number" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
      <el-form-item label="背景模糊">
        <el-input v-model="active_pel_date.blur" placeholder="请输入" size="small" type="number" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
      <el-form-item label="毛玻璃颜色">
        <lx-input-color-picker v-model:color="active_pel_date.blurBackground" @changEvent="setValueCanvasMeta2dPenFun"></lx-input-color-picker>
      </el-form-item>
    </el-form>
  </el-collapse-item>
</template>

<script>
import {LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PenIframeConfigCom",
  components: {LxInputColorPicker},
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
    const {emit} = getCurrentInstance();

    const that = reactive({
      active_pel_date: props.activePelDate,
    })

    const setValueCanvasMeta2dPenFun = () => {
      emit("update:activePelDate", that.active_pel_date);
      emit("changeEvent");
    }

    const watchActivePelDate = watch(() => props.activePelDate, (newActivePelDate) => {
      that.active_pel_date = JSON.parse(JSON.stringify(newActivePelDate));
    }, {deep: true})

    return {...toRefs(that), watchActivePelDate, setValueCanvasMeta2dPenFun}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 4px;

  .lx-color-picker{
    width: 100%;
  }
}
</style>