<template>
  <div class="lx-color-picker">

    <template v-if="pickerType === 'input'">
      <el-input v-model="colorName" :placeholder="placeholder" size="small" @change="changeColorFun" @click.stop="clickInputFun">
        <template #prefix>
          <el-color-picker ref="colorPickerRef" v-model="colorName" :predefine="predefineColors" show-alpha size="small" @change="changeColorFun"></el-color-picker>
        </template>
      </el-input>
    </template>

    <template v-if="pickerType === 'text'">
      <div class="content_color" @click.stop="clickInputFun">
        <el-color-picker ref="colorPickerRef" v-model="colorName" :predefine="predefineColors" show-alpha size="small" @change="changeColorFun"></el-color-picker>
        <div class="content_text">
          <slot name="content"></slot>
        </div>
      </div>
    </template>

  </div>
</template>

<script>
import {defineComponent, getCurrentInstance, reactive, ref, toRefs, watch} from "vue";

export default defineComponent({
  name: "lx-color-picker",
  props: {
    color: {
      type: String,
      default: ""
    },
    pickerType: {
      type: String,
      default: "input"
    },
    placeholder: {
      type: String,
      default: "请输入颜色"
    },
  },
  emits: ["update:color","changEvent"],
  setup(props) {

    const {emit} = getCurrentInstance();
    const colorPickerRef = ref(null);

    const that = reactive({
      visible: false,
      colorName: props.color,
      predefineColors: ['#ff4500', '#ff8c00', '#ffd700', '#90ee90', '#00ced1', '#1e90ff', '#c71585', '#c7158577']
    })

    const clickInputFun = () => {
      colorPickerRef.value.show();
    }

    const changeColorFun = () => {
      emit("update:color", that.colorName);
      emit("changEvent");
    }

    const watchColor = watch(() => props.color, (newColor) => {
      that.colorName = newColor;
    }, {deep: true})

    return {...toRefs(that), changeColorFun, colorPickerRef, clickInputFun, watchColor}
  }
})
</script>

<style lang="scss" scoped>
.content_color{
  display: flex;
  align-items: center;
  cursor: pointer;

  .content_text{
    color: #737A8A;
    font-size: 12px;
    margin-left: 4px;
    white-space: nowrap;
  }
}
</style>