<template>
  <Dialog v-model:isVisible="dialog_visible" :confirmVisible="false" :title="titleName" width="720">
    <template v-slot:content>
      <div class="dialog-main">
        <template v-for="(item,index) in list" :key="index">
          <div class="content_list">
            <div class="content_title">{{ item.title }}：</div>
            <div class="content_describe">{{ item.describe }}</div>
          </div>
        </template>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {defineComponent, getCurrentInstance, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "ParameterDescriptionDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      titleName: "参数说明",
      dialog_visible: props.isVisible,
      list: [
        {title: "组件厂家", describe: "组件制造商(品牌)名称。"},
        {title: "组件型号", describe: "组件制造商规定的某一产品的编号，如逆变器的产品型号。"},
        {title: "组件类型", describe: "组件电池片技术类型。"},
        {title: "组件电池片数(片/组件)", describe: "组件内部串联电池片数量，目前主流60片/72片。"},
        {title: "填充因子(%)", describe: "电池具有最大输出功率时的电流和电压的乘积与短路电流和开路电压乘积的比值。"},
        {title: "组件最大功率(Pmax)(W)", describe: "标准测试条件下(AM1.5、组件温度25℃，辐照度1000W/m²)光伏组件最大输出功率。"},
        {title: "组件最佳工作电压(Vmp) (V)", describe: "参考组件规格书，STC条件组件MPPT点电压值，单位V。"},
        {title: "组件最佳工作电流(Imp) (A)", describe: "参考组件规格书，STC条件组件MPPT点电流值，单位A。"},
        {title: "组件开路电压(Voc)(V)", describe: "参考组件规格书，STC条件组件开路电压值，单位V。"},
        {title: "组件短路电流(Isc)(A)", describe: "参考组件规格书，STC条件组件短路电流值，单位A。"},
        {title: "最大功率(Pmax)的温度系数 (%/℃)", describe: "最大功率(Pmax)的温度系数 (%/℃)。"},
        {title: "开路电压(Voc)的温度系数 (%/℃)", describe: "参考组件规格书，组件开路电压随温度变化关系，单位%/℃。"},
        {title: "短路电流(Isc)的温度系数 (%/℃)", describe: "参考组件规格书，组件短路电流随温度变化关系，单位%/℃。"},
        {title: "标称组件转换效率(%)", describe: "标准测试条件下(AM1.5、组件温度25℃，辐照度1000W/m²)光伏组件最大输出功率与照射在该组件上的太阳光功率的比值。"},
        {title: "组件首年衰减率(%/y)", describe: "咨询组件制造商，组件运行首年衰减速率，单位%/y。"},
        {title: "组件逐年衰减率(%/y)", describe: "咨询组件制造商，组件运行次年起年衰减速率，单位%/y。"},
      ]
    });

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return {...toRefs(that), watchVisible, watchDialogVisible};
  }
});
</script>

<style lang="scss" scoped>
.content_list{
  display: flex;
  margin-bottom: 10px;

  .content_title{
    white-space: nowrap;
  }

  &:last-child{
    margin-bottom: 0;
  }
}
</style>