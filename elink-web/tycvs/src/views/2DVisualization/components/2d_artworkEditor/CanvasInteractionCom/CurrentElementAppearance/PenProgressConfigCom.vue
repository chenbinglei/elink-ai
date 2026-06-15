<template>
  <el-collapse-item name="3" title="进度属性">
    <el-form :model="active_pel_date" class="content_item_body">
      <el-form-item>
        <div class="flex-ai-center" style="width: 100%;">
          <el-slider v-model="active_pel_date.progress" :max="1" :min="0" :step="0.1" size="small" @change="setValueCanvasMeta2dPenFun"></el-slider>
          <div class="globalAlpha">{{ active_pel_date.progress }}</div>
        </div>
      </el-form-item>
      <el-form-item label="颜色">
        <div style="width: 100%;">
          <lx-input-color-picker v-model:color="active_pel_date.progressColor" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>进度颜色</template>
          </lx-input-color-picker>
        </div>
      </el-form-item>
      <el-form-item>
        <div class="flex-ai-center">
          <el-checkbox v-model="active_pel_date.verticalProgress" @change="setValueCanvasMeta2dPenFun">垂直进度</el-checkbox>
          <el-checkbox v-model="active_pel_date.reverseProgress" @change="setValueCanvasMeta2dPenFun">反向进度</el-checkbox>
        </div>
      </el-form-item>
    </el-form>
  </el-collapse-item>
</template>

<script lang="ts">
import { useMeta2dStore } from '@/stores/index';

import {LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, computed, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PenProgressConfigCom",
  components: {LxInputColorPicker},
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

    const meta2dStore = useMeta2dStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

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

    return {...toRefs(that), canvasMeta2d, watchActivePelDate, setValueCanvasMeta2dPenFun}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 4px;

  .globalAlpha {
    width: 12px;
    font-size: 12px;
    text-align: center;
    margin-left: 12px;
  }
}
</style>