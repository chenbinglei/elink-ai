<template>
  <div class="intervalNumInputCom el-input">
    <div class="el-input__wrapper content_input">
      <el-row class="flex-all">
        <el-col :span="11">
          <el-input-number v-model="min_value" :controls="false" placeholder="最小值" @change="changeValueFun"></el-input-number>
        </el-col>
        <el-col :span="2">
          <div class="flex-jc-ai-center range-separator">{{ rangeSeparator }}</div>
        </el-col>
        <el-col :span="11">
          <el-input-number v-model="max_value" :controls="false" placeholder="最大值" @change="changeValueFun"></el-input-number>
        </el-col>
      </el-row>
      <div v-if="unit" class="unit">{{ unit }}</div>
    </div>
  </div>
</template>

<script>
import {defineComponent, getCurrentInstance, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "IntervalNumInputCom",
  props: {
    minValue: {
      type: [Number, Object],
      default: null
    },
    maxValue: {
      type: [Number, Object],
      default: null
    },
    // 分隔符
    rangeSeparator: {
      type: String,
      default: "~"
    },
    // 单位
    unit: {
      type: String,
      default: ""
    },
  },
  emits: ["update:minValue", "update:maxValue"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      min_value: null,
      max_value: null,
    })

    const changeValueFun = () => {
      let min_value = that.min_value;
      let max_value = that.max_value;

      if (that.min_value !== null && that.max_value !== null) {
        if (that.min_value > that.max_value) {
          min_value = that.max_value;
          max_value = that.min_value;
        }

        that.min_value = min_value;
        that.max_value = max_value;
      }

      emit("update:minValue", that.min_value ?? "");
      emit("update:maxValue", that.max_value ?? "");
      emit("changeEvent");
    }

    const watchInputValue = watch([() => props.minValue, () => props.maxValue], ([newMinValue, newMaxValue]) => {
      that.min_value = newMinValue;
      that.max_value = newMaxValue;
    }, {deep: true, immediate: true});

    return {...toRefs(that), changeValueFun, watchInputValue};
  }
})

</script>

<style lang="scss" scoped>
.content_input {
  padding: 0 !important;
  box-sizing: border-box;

  .el-input-number {
    width: 100%;
    box-sizing: border-box;

    :deep(.el-input) {
      box-sizing: border-box;
      --el-input-border: none;
      --el-input-bg-color: none;

      .el-input__wrapper {
        box-shadow: none;
      }
    }
  }

  .range-separator {
    color: var(--el-text-color-primary);
  }

  .unit {
    padding: 0 8px 0 8px;
    font-size: 12px;
    color: var(--el-text-color-placeholder);
  }
}
</style>