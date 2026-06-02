<template>
  <div class="functionalAttrCard">
    <div class="card_list">
      <div class="functionName textTwo">{{ $filters.moreData(function_card_info.functionName) }}</div>
      <div class="crad_list_right pointer" @click="clickHistoryIcon">
        <span class="iconfont icon-lishishuju"></span>
      </div>
    </div>

    <div class="card_list textTwo">
      <span>{{ $filters.moreData(function_card_info.value) }}</span>
      <template v-if="function_card_info.dataType === 5 || function_card_info.dataType === 6">
        <span>：</span>
        <span>{{ $filters.moreData(queryFunctionValueText()) }}</span>
      </template>
    </div>
    <div class="card_list flex ai-center jc-space-between">
      <span class="dateTime">{{ $filters.moreData(function_card_info.dateTime) }}</span>
      <span class="dataType">{{ $filters.dataType(function_card_info.dataType) }}</span>
    </div>

    <function-history-data v-if="functionHistoryVisible" v-model:isVisible="functionHistoryVisible" :cardInfo="function_card_info" />
  </div>
</template>

<script>
import {ElMessage} from "element-plus";
import FunctionHistoryData from "./FunctionHistoryData";
import {reactive, toRefs, defineComponent, watch} from "vue";

export default defineComponent({
  name: "FunctionalAttrCard",
  components:{FunctionHistoryData},
  props: {
    cardInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {
    const that = reactive({
      function_card_info: {},
      functionHistoryVisible: false,
    })

    const clickHistoryIcon = ()=>{
      // if(that.function_card_info.value === null){
      //   ElMessage({ type: "warning", showClose: true, message: `无历史数据！` });
      //   return
      // }
      that.functionHistoryVisible = true;
    }

    const queryFunctionValueText = ()=>{
      let functionValueText = that.function_card_info.value;

      // 枚举项
      if(that.function_card_info.dataType === 5){
        let enumArray = that.function_card_info.dataObject.enumArray;
        if(enumArray && enumArray.length){
          let findItem = enumArray.find(item => String(item.id) === String(that.function_card_info.value));
          if(findItem) functionValueText = findItem.name;
        }
      }

      // 布尔值
      if(that.function_card_info.dataType === 6){
        let trueValueText = that.function_card_info.dataObject.trueValue;
        let falseValueText = that.function_card_info.dataObject.falseValue;
        functionValueText = that.function_card_info.value ? trueValueText : falseValueText;
      }

      return functionValueText
    }

    const watchCardInfo = watch(()=>props.cardInfo,(newCardInfo)=>{
      try {
        newCardInfo.dataObject = JSON.parse(newCardInfo.dataObject);
      } catch (e) {}
      that.function_card_info = JSON.parse(JSON.stringify(newCardInfo));
    },{ deep: true,immediate: true})

    return { ...toRefs(that), clickHistoryIcon, queryFunctionValueText, watchCardInfo }
  }
})
</script>

<style lang="scss" scoped>
.functionalAttrCard {
  padding: 12px 16px;
  border-radius: 4px;
  border: 1px solid #DBDBDD;
  margin-bottom: 12px;

  .card_list{
    display: flex;
    align-items: center;
    margin-bottom: 12px;

    .functionName{
      flex: 1;
      font-weight: bold;
    }
    .crad_list_right{
      color: #1F74E2;

      .iconfont{
        font-size: 18px;
        font-weight: bold;
      }
    }

    &:last-child{
      margin-bottom: 0;
      font-size: 14px;
    }
  }
}
</style>
