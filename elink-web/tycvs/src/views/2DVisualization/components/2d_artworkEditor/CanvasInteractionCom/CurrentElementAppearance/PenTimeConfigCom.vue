<template>
  <el-collapse-item name="6" title="时间属性">
    <el-form :model="active_pel_date" class="content_item_body">
      <el-form-item label="是否填充">
        <el-switch v-model="active_pel_date.fillZero" size="small" @change="setValueCanvasMeta2dPenFun"></el-switch>
      </el-form-item>
      <el-form-item v-if="active_pel_date.name === 'time' || active_pel_date.name === 'countdown'" label="显示格式">
        <el-input v-model="active_pel_date.timeFormat" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
      <el-form-item v-if="active_pel_date.name === 'countdown'" label="截止时间">
        <el-input v-model="active_pel_date.deadline" placeholder="2099-01-01 00:00:00" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
    </el-form>
  </el-collapse-item>
</template>

<script>
import { useMeta2dStore } from '@/stores/index';

import {reactive, toRefs, defineComponent, computed, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PenTimeConfigCom",
  props: {
    activePelDate: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["update:activePelDate"],
  setup(props) {

    const meta2dStore = useMeta2dStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const that = reactive({
      active_pel_date: {},
      updateFieldList: ["fillZero", "timeFormat", "deadline"],
    })

    const setValueCanvasMeta2dPenFun = () => {
      let updateFieldObj = {};
      emit("update:activePelDate", that.active_pel_date);
      for (let key of that.updateFieldList) updateFieldObj[key] = that.active_pel_date[key];
      canvasMeta2d.value.setValue({id: that.active_pel_date.id, ...updateFieldObj});
    }

    const watchActivePelDate = watch(() => props.activePelDate, (newActivePelDate) => {
      if(JSON.stringify(that.active_pel_date) !== JSON.stringify(newActivePelDate)){
        that.active_pel_date = JSON.parse(JSON.stringify(newActivePelDate));
      }
    }, {deep: true,immediate: true})

    return {...toRefs(that), canvasMeta2d, watchActivePelDate, setValueCanvasMeta2dPenFun}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 4px;
}
</style>