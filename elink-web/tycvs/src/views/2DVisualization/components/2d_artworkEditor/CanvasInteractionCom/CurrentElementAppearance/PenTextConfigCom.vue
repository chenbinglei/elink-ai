<template>
  <el-collapse-item name="2" title="文字属性">
    <el-form :model="active_pel_date" label-width="auto" class="content_item_body">
      <el-form-item label="样式">
        <el-select v-model="active_pel_date.fontFamily" clearable size="small" @change="setValueCanvasMeta2dPenFun">
          <el-option v-for="(item,index) in font_family_list" :key="index" :label="item.name" :value="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="大小">
        <el-input-number v-model="active_pel_date.fontSize" controls-position="right" min="0" size="small" @change="setValueCanvasMeta2dPenFun"></el-input-number>
      </el-form-item>
      <el-form-item label="对齐">
        <div class="flex-ai-center" style="width: 100%;">
          <div class="flex-all">
            <el-select v-model="active_pel_date.textAlign" size="small" @change="setValueCanvasMeta2dPenFun">
              <el-option v-for="item in text_align_list" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </div>
          <div style="width: 5px"></div>
          <div class="flex-all">
            <el-select v-model="active_pel_date.textBaseline" size="small" @change="setValueCanvasMeta2dPenFun">
              <el-option v-for="item in text_base_line_list" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </div>
          <div class="border_icon" :class="{ active_border_icon: active_pel_date.fontWeight === 'bold'}" @click="clickBorderIconFun('B')">B</div>
          <div class="border_icon" :class="{ active_border_icon: active_pel_date.fontStyle === 'italic'}" @click="clickBorderIconFun('I')">I</div>
        </div>
      </el-form-item>

      <el-form-item label="行高">
        <el-input-number v-model="active_pel_date.lineHeight" controls-position="right" placeholder="行高" size="small" @change="setValueCanvasMeta2dPenFun" />
      </el-form-item>
      <el-form-item label="小数点">
        <el-input-number v-model="active_pel_date.keepDecimal" min="0" placeholder="显示时保留小数位数" controls-position="right" size="small" @change="setValueCanvasMeta2dPenFun" />
      </el-form-item>
      <el-form-item>
        <div class="flex-ai-center jc-space-between" style="width: 100%;">
          <lx-input-color-picker v-model:color="active_pel_date.textColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>前景</template>
          </lx-input-color-picker>
          <lx-input-color-picker v-model:color="active_pel_date.textBackground" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>背景</template>
          </lx-input-color-picker>
          <lx-input-color-picker v-model:color="active_pel_date.hoverTextColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>悬停</template>
          </lx-input-color-picker>
          <lx-input-color-picker v-model:color="active_pel_date.activeTextColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>选中</template>
          </lx-input-color-picker>
        </div>
      </el-form-item>
      <el-form-item>
        <div class="flex-ai-center ai-center">
          <el-tooltip effect="dark" content="水平偏移" placement="top-start">
            <el-input type="number" v-model="active_pel_date.textLeft" class="flex-all" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
          </el-tooltip>
          <el-tooltip effect="dark" content="垂直偏移" placement="top-start">
            <el-input type="number" v-model="active_pel_date.textTop" class="flex-all" style="margin-left: 5px" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
          </el-tooltip>
          <el-tooltip effect="dark" content="宽" placement="top-start">
            <el-input type="number" v-model="active_pel_date.textWidth" class="flex-all" style="margin-left: 5px" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
          </el-tooltip>
          <el-tooltip effect="dark" content="高" placement="top-start">
            <el-input type="number" v-model="active_pel_date.textHeight" class="flex-all" style="margin-left: 5px" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
          </el-tooltip>
        </div>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="active_pel_date.ellipsis" @change="setValueCanvasMeta2dPenFun">省略号</el-checkbox>
        <el-checkbox v-model="active_pel_date.disableInput" @change="setValueCanvasMeta2dPenFun">只读</el-checkbox>
        <el-checkbox v-model="active_pel_date.whiteSpace" true-value="break-all" @change="setValueCanvasMeta2dPenFun">换行</el-checkbox>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="active_pel_date.textAutoAdjust" @change="setValueCanvasMeta2dPenFun">自动调整</el-checkbox>
        <el-checkbox v-model="active_pel_date.hiddenText" @change="setValueCanvasMeta2dPenFun">隐藏文字</el-checkbox>
      </el-form-item>
      <el-form-item label="文本内容">
        <el-input type="textarea" v-model="active_pel_date.text" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
    </el-form>
  </el-collapse-item>
</template>

<script>
import {LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance} from "vue";
import {fontFamilyArray, textAlignArray, verticalAlignArray} from "@/utils/publicParam";

export default defineComponent({
  name: "PenTextConfigCom",
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
      text_align_list: textAlignArray,
      font_family_list: fontFamilyArray,
      text_base_line_list: verticalAlignArray,
    })

    const clickBorderIconFun = (operateType)=>{
      if(operateType === "B"){
        let fontWeight = "bold";
        if(that.active_pel_date.fontWeight === 'bold') fontWeight = "normal";
        that.active_pel_date.fontWeight = fontWeight;
      }

      if(operateType === "I"){
        let fontStyle = "italic";
        if(that.active_pel_date.fontStyle === 'italic') fontStyle = "normal";
        that.active_pel_date.fontStyle = fontStyle;
      }
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

    return {...toRefs(that), watchActivePelDate, setValueCanvasMeta2dPenFun, clickBorderIconFun}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 4px;

  .el-input-number{
    width: 100%;
  }

  .border_icon{
    width: 21px;
    height: 21px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-left: 5px;
    border-radius: 4px;
    box-sizing: border-box;

    &:hover{
      cursor: pointer;
      color: var(--color-primary);
      border: 1px solid var(--color-primary);
    }
  }

  .active_border_icon{
    color: var(--color-title);
    border: 1px solid var(--color-border-input);
    background-color: var(--color-border-input);
  }
}
</style>