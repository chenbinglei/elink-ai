<template>
  <div class="componentParameCom">
    <el-form ref="formDialogRef" :model="formDialog" disabled label-width="auto" label-position="top">
      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="组件类型：" prop="moduleType">
            <el-select v-model="formDialog.moduleType" clearable filterable placeholder="请选择组件类型">
              <el-option v-for="item in moduleTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组件电池片数（片/组件）：" prop="batteryPieces">
            <el-input-number v-model="formDialog.batteryPieces" :max="300" :min="0" controls-position="right" placeholder="请输入组件电池片数"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="填充因子（%）：">
            <el-input-number v-model="formDialog.fillFactor" :max="95" :min="65" controls-position="right" placeholder="请输入填充因子"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组件最大功率(Pmax)(W)：" prop="maxPower">
            <el-input-number v-model="formDialog.maxPower" :max="2000" :min="0" controls-position="right" placeholder="请输入组件最大功率"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组件最佳工作电压(Vmp)：" prop="bestWorkVoltage">
            <el-input-number v-model="formDialog.bestWorkVoltage" :max="95" :min="0" controls-position="right" placeholder="请输入组件最佳工作电压"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组件最佳工作电流(Imp) (A)：" prop="bestWorkCurrent">
            <el-input-number v-model="formDialog.bestWorkCurrent" :max="50" :min="0" controls-position="right" placeholder="请输入组件最佳工作电流"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组件开路电压(Voc)(V)：" prop="openVoltage">
            <el-input-number v-model="formDialog.openVoltage" :max="100" :min="0" controls-position="right" placeholder="请输入组件开路电压"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组件短路电流(Isc)(A)：" prop="shortCurrent">
            <el-input-number v-model="formDialog.shortCurrent" :max="50" :min="0" controls-position="right" placeholder="请输入组件短路电流"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最大功率(Pmax)的温度系数(%/°C)：" prop="maxPowerTempCoeff">
            <el-input-number v-model="formDialog.maxPowerTempCoeff" :max="0" :min="-1" controls-position="right" placeholder="请输入最大功率的温度系数"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="开路电压(Voc)的温度系数(%/°C)：" prop="openVoltTempCoeff">
            <el-input-number v-model="formDialog.openVoltTempCoeff" :max="0" :min="-1" controls-position="right" placeholder="请输入开路电压的温度系数"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="短路电流(Isc)的温度系数(%/°C)：" prop="shortCurrTempCoeff">
            <el-input-number v-model="formDialog.shortCurrTempCoeff" :max="0.2" :min="0" controls-position="right" placeholder="请输入短路电流的温度系数"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="标称组件转换效率(%)：">
            <el-input-number v-model="formDialog.convertEffi" :max="50" :min="10" controls-position="right" placeholder="请输入标称组件转换效率"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组件首年衰减率(%/y)：">
            <el-input-number v-model="formDialog.firstDecayRate" :max="100" :min="0" controls-position="right" placeholder="请输入组件首年衰减率"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组件逐年衰减率(%/y)：">
            <el-input-number v-model="formDialog.passingDecayRate" :max="100" :min="0" controls-position="right" placeholder="请输入组件逐年衰减率"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import {module_type_array} from "@/utils/setVariate";
import {defineComponent, getCurrentInstance, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "ComponentParameCom",
  props: {
    componentParame: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      formDialog: {},
      moduleTypeArray: module_type_array,
    });

    const watchComponentParame = watch(() => props.componentParame, (newComponentParame) => {
      that.formDialog = JSON.parse(JSON.stringify(newComponentParame));
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchComponentParame};
  }
});
</script>

<style lang="scss" scoped>
.componentParameCom{
  width: 970px;
  box-sizing: border-box;
  padding: 12px 12px 0 12px;

  .content_header{
    display: flex;
    justify-content: flex-end;
    margin-bottom: 12px;
  }
}
</style>