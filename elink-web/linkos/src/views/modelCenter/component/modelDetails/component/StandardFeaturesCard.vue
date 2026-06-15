<template>
  <div class="standardFeaturesCard">
    <div class="card_left">
      <div class="card_left_top">
        <div class="functionName textTwo">{{ $filters.moreData(cardInfo.functionName) }}</div>
        <div class="functionType">{{ $filters.functionType(cardInfo.functionType) }}</div>
      </div>
      <div class="card_left_bottom">
        <span class="margin_class">{{ $filters.moreData(cardInfo.functionLogo) }}</span>
        <span class="margin_class">{{ $filters.dataType(cardInfo.dataType) }}</span>
        <span class="unit_text">单位：</span>
        <span class="unit">{{ $filters.moreData(cardInfo.unit) }}</span>
      </div>
    </div>
    <div class="card_right" @click="clickOperateBut">
      <template v-if="operateType === 1">
        <div class="bg_class flex-jc-ai-center"><el-icon><ArrowRightBold /></el-icon></div>
      </template>
      <template v-if="operateType === 2">
        <div class="flex-jc-ai-center delete_class"><el-icon><Delete /></el-icon></div>
      </template>
    </div>
  </div>
</template>

<script lang="ts">
import {ArrowRightBold, Delete} from "@element-plus/icons-vue";
import {getCurrentInstance, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "StandardFeaturesCard",
  components:{ArrowRightBold,Delete},
  props:{
    operateType:{
      type:[Number,String],
      default: 1
    },
    cardInfo:{
      type: Object,
      default:()=>{
        return { }
      }
    }
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const that = reactive({

    })

    const clickOperateBut = ()=>{
      emit("changeEvent",{ type: "standardFeaturesCard",operateType: props.operateType,id: props.cardInfo.id });
    }

    return { ...toRefs(that),clickOperateBut }
  }
})
</script>

<style lang="scss" scoped>
.standardFeaturesCard {
  padding: 10px 12px;
  border-radius: 6px;
  box-sizing: border-box;
  border: 1px solid #DBDBDD;
  margin-bottom: 12px;
  display: flex;
  align-items: center;

  .card_left{
    flex: 1;

    .card_left_top{
      display: flex;
      align-items: center;
      margin-bottom: 16px;

      .functionName{
        flex: 1;
        font-size: 18px;
        font-weight: bold;
        margin-right: 12px;
        -webkit-line-clamp: 1;
      }
      .functionType{
        color: #FFFFFF;
        padding: 2px 8px;
        border-radius: 4px;
        background: #1F74E2;
        border: 1px solid #1F74E2;
      }
    }

    .margin_class{
      margin-right: 12px;
    }
  }
  .card_right{
    margin-left: 18px;

    .flex-jc-ai-center{
      width: 28px;
      height: 28px;
      cursor: pointer;
      border-radius: 50%;
      color: #FFFFFF;
    }

    .bg_class{
      font-size: 18px;
      background: #1F74E2;
    }

    .delete_class{
      color: #FF7B7B;
      font-size: 20px;
    }
  }
}
</style>
