<template>
  <el-collapse-item name="9" title="开关属性">
    <el-form :model="active_pel_data" class="content_item_body" label-width="auto">
      <el-form-item label="数据">
        <el-button :icon="DArrowLeft" size="small" @click="clickCodeOptionFun"></el-button>
      </el-form-item>
      <el-form-item label="当前选中">
        <el-select v-model="active_pel_data.checked" size="small" @change="setValueCanvasMeta2dPenFun">
          <el-option v-for="(item,index) in active_pel_data.options" :key="index" :label="item.text" :value="item.text"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="排列方向">
        <el-select v-model="active_pel_data.direction" size="small" @change="setValueCanvasMeta2dPenFun">
          <el-option v-for="item in direction_list" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <template v-if="active_pel_data.direction === 'vertical'">
        <el-form-item label="间距">
          <el-input v-model="active_pel_data.optionInterval" placeholder="默认20" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
        </el-form-item>
        <el-form-item label="高度">
          <el-input v-model="active_pel_data.optionHeight" placeholder="默认20" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
        </el-form-item>
      </template>
    </el-form>
    <PenCodeOptionDialog v-if="penCodeOptionVisible" v-model:isVisible="penCodeOptionVisible" :activeEditInfo="activeEditInfo" @changeEvent="changeEvent"/>
  </el-collapse-item>
</template>

<script>
import {DArrowLeft} from "@element-plus/icons-vue";
import PenCodeOptionDialog from "./PenCodeOptionDialog.vue";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PenRadioConfigCom",
  components: {PenCodeOptionDialog},
  props: {
    activePelDate: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["update:activePelDate", "changeEvent"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      DArrowLeft,
      active_pel_data: {},
      activeEditInfo: {},
      penCodeOptionVisible: false,
      theme_list: [{name: "通用模式", id: "normal"}, {name: "按钮模式", id: "button"}],
      direction_list: [{name: "横向", id: "horizontal"}, {name: "纵向", id: "vertical"}],
    })

    const clickCodeOptionFun = () => {
      let dataOption = that.active_pel_data?.options;
      that.activeEditInfo = JSON.parse(JSON.stringify(dataOption ?? []));
      that.penCodeOptionVisible = true;
    }

    const changeEvent = (data) => {
      that.active_pel_data.options = data.codeValue ?? [];
      setValueCanvasMeta2dPenFun();
    }

    const setValueCanvasMeta2dPenFun = () => {
      console.log(that.active_pel_data);
      emit("update:activePelDate", that.active_pel_data);
      emit("changeEvent");
    }

    const watchActivePelDate = watch(() => props.activePelDate, (newActivePelDate) => {
      if (JSON.stringify(that.active_pel_data) !== JSON.stringify(newActivePelDate)) {
        that.active_pel_data = JSON.parse(JSON.stringify(newActivePelDate));
      }
    }, {deep: true, immediate: true})

    return {...toRefs(that), watchActivePelDate, setValueCanvasMeta2dPenFun, clickCodeOptionFun, changeEvent}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 4px;
}
</style>