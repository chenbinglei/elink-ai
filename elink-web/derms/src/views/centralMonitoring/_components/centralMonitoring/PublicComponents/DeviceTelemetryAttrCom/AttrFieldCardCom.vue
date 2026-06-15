<template>
  <div class="card_list_class" v-resize="detectTextOverflow">
    <div class="card_li_top">
      <div :id="'text-' + (function_card_info.functionId || function_card_info.varCode)" class="number flex-all textTwo">
        <el-tooltip :disabled="!isTooltipEl" effect="dark" placement="top-start">
          <template #content>
            <template v-if="(function_card_info.dataType >= 2 && function_card_info.dataType <= 4) || (isValueNumber(function_card_info.value) && function_card_info.varCode)">
              <span class="number_value">{{ $filters.moneyTwoNum(queryFunctionValueText(), 2, {isZeroFill: false}) }}</span>
            </template>
            <span v-else class="text_value">{{ $filters.moreData(queryFunctionValueText()) }}</span>
          </template>
          <template v-if="(function_card_info.dataType >= 2 && function_card_info.dataType <= 4) || (isValueNumber(function_card_info.value) && function_card_info.varCode)">
            <span class="text_value number_value">{{ $filters.moneyTwoNum(queryFunctionValueText(), 2, {isZeroFill: false}) }}</span>
          </template>
          <span v-else class="text_value">{{ $filters.moreData(queryFunctionValueText()) }}</span>
        </el-tooltip>
      </div>
      <div v-if="function_card_info.unit" class="unit">{{ $filters.moreData(function_card_info.unit) }}</div>
    </div>
    <div class="card_li_line">
      <div class="line_left"></div>
      <div class="line_right flex-all"></div>
    </div>
    <div class="card_li_bottom textTwo">{{ $filters.moreData(function_card_info.functionName || function_card_info.varName) }}</div>
  </div>
</template>

<script lang="ts">
import {isNumber} from "@/utils";
import {defineComponent, nextTick, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "AttrFieldCardCom",
  props: {
    attrFieldInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({
      isTooltipEl: false,
      function_card_info: {}
    });

    const isValueNumber = (value) =>{
      return isNumber(value);
    };

    const queryFunctionValueText = () => {
      let functionValueText = that.function_card_info.value;

      // 枚举项
      if (that.function_card_info.dataType === 5) {
        let enumArray = that.function_card_info.dataObject.enumArray;
        if (enumArray && enumArray.length) {
          let findItem = enumArray.find(item => String(item.id) === String(that.function_card_info.value));
          if (findItem) functionValueText = findItem.name;
        }
      }

      // 布尔值
      if (that.function_card_info.dataType === 6) {
        let trueValueText = that.function_card_info.dataObject.trueValue;
        let falseValueText = that.function_card_info.dataObject.falseValue;
        functionValueText = that.function_card_info.value ? trueValueText : falseValueText;
      }

      return functionValueText;
    };

    // 监测文字是否超出
    const detectTextOverflow = () => {
      nextTick(() => {
        let parentEl = document.getElementById('text-' + (that.function_card_info.functionId || that.function_card_info.varCode));
        let childrenEl = parentEl?.getElementsByClassName("text_value");
        let childrenElHeight = childrenEl && childrenEl[0].offsetHeight;
        that.isTooltipEl = (childrenElHeight - 5) > parentEl?.offsetHeight;
      });
    };

    const watchAttrFieldInfo = watch(() => props.attrFieldInfo, (newAttrFieldInfo) => {
      try {
        if (newAttrFieldInfo.dataObject) {
          newAttrFieldInfo.dataObject = JSON.parse(newAttrFieldInfo.dataObject);
        }
      } catch (e) {}
      that.function_card_info = JSON.parse(JSON.stringify(newAttrFieldInfo ?? {}));
      detectTextOverflow();
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchAttrFieldInfo, queryFunctionValueText, detectTextOverflow, isValueNumber};
  }
});

</script>

<style lang="scss" scoped>
.card_list_class {
  width: 100%;
  padding: 2px 8px;
  margin-bottom: 16px;
  box-sizing: border-box;

  .card_li_top {
    display: flex;
    align-items: center;

    .number {
      color: #08f7fd;
      font-size: 14px;
      font-weight: 700;
      -webkit-line-clamp: 1;
    }

    .unit {
      font-size: 12px;
      color: #ffffffcc;
      margin-left: 4px;
    }
  }

  .card_li_line {
    height: 2px;
    display: flex;
    align-items: center;
    margin-top: 2px;
    margin-bottom: 4px;

    .line_left {
      width: 35%;
      height: 100%;
      max-width: 36px;
      background: #00AEFF;
    }

    .line_right {
      height: 100%;
      background: #0D4D7A;
    }
  }

  .card_li_bottom {
    font-size: 12px;
    color: #ffffffcc;
    padding-bottom: 1px;
    -webkit-line-clamp: 1;
  }
}
</style>