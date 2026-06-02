<template>
  <el-collapse-item name="9" title="开关属性">
    <el-form :model="active_pel_date" class="content_item_body" label-width="auto">
      <el-form-item label="开颜色">
        <lx-input-color-picker v-model:color="active_pel_date.onColor" @changEvent="setValueCanvasMeta2dPenFun"></lx-input-color-picker>
      </el-form-item>
      <el-form-item label="关颜色">
        <lx-input-color-picker v-model:color="active_pel_date.offColor" @changEvent="setValueCanvasMeta2dPenFun"></lx-input-color-picker>
      </el-form-item>
      <el-form-item label="开描边颜色">
        <lx-input-color-picker v-model:color="active_pel_date.onStrokeColor" @changEvent="setValueCanvasMeta2dPenFun"></lx-input-color-picker>
      </el-form-item>
      <el-form-item label="关描边颜色">
        <lx-input-color-picker v-model:color="active_pel_date.offStrokeColor" @changEvent="setValueCanvasMeta2dPenFun"></lx-input-color-picker>
      </el-form-item>
      <el-form-item label="是否禁用">
        <el-switch v-model="active_pel_date.disable" :inactive-value="false" :active-value="true" @change="setValueCanvasMeta2dPenFun"></el-switch>
      </el-form-item>
      <el-form-item label="开(禁)颜色">
        <lx-input-color-picker v-model:color="active_pel_date.disableOnColor" @changEvent="setValueCanvasMeta2dPenFun"></lx-input-color-picker>
      </el-form-item>
      <el-form-item label="关(禁)颜色">
        <lx-input-color-picker v-model:color="active_pel_date.disableOffColor" @changEvent="setValueCanvasMeta2dPenFun"></lx-input-color-picker>
      </el-form-item>
    </el-form>
  </el-collapse-item>
</template>

<script>
import {LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PenSwitchConfigCom",
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
      active_pel_date: {},
    })

    const setValueCanvasMeta2dPenFun = () => {
      emit("update:activePelDate", that.active_pel_date);
      emit("changeEvent");
    }

    const watchActivePelDate = watch(() => props.activePelDate, (newActivePelDate) => {
      if(JSON.stringify(that.active_pel_date) !== JSON.stringify(newActivePelDate)){
        that.active_pel_date = JSON.parse(JSON.stringify(newActivePelDate));
      }
    }, {deep: true,immediate: true})

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