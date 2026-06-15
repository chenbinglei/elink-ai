<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="980"
          :confirmVisible="!formDialog.previewCom" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <div class="content_header">
          <el-button type="primary" :icon="Document" @click="clickDocumentButFun">参数说明</el-button>
        </div>
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" :disabled="formDialog.previewCom" label-width="auto" label-position="top">
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="组件厂家：" prop="moduleFactory">
                <el-input v-model="formDialog.moduleFactory" maxlength="255" placeholder="请输入组件厂家"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件型号：" prop="moduleModel">
                <el-input v-model="formDialog.moduleModel" maxlength="255" placeholder="请输入组件型号"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件类型：" prop="moduleType">
                <el-select v-model="formDialog.moduleType" clearable filterable placeholder="请选择组件类型">
                  <el-option v-for="item in moduleTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件电池片数（片/组件）：" prop="batteryPieces">
                <el-input-number v-model="formDialog.batteryPieces" :min="0" :max="300" controls-position="right" placeholder="请输入组件电池片数"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="填充因子（%）：" >
                <el-input-number v-model="formDialog.fillFactor" :min="65" :max="95" controls-position="right" placeholder="请输入填充因子"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件最大功率(Pmax)(W)：" prop="maxPower">
                <el-input-number v-model="formDialog.maxPower" :min="0" :max="2000" controls-position="right" placeholder="请输入组件最大功率"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件最佳工作电压(Vmp)：" prop="bestWorkVoltage">
                <el-input-number v-model="formDialog.bestWorkVoltage" :min="0" :max="95" controls-position="right" placeholder="请输入组件最佳工作电压"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件最佳工作电流(Imp) (A)：" prop="bestWorkCurrent">
                <el-input-number v-model="formDialog.bestWorkCurrent" :min="0" :max="50" controls-position="right" placeholder="请输入组件最佳工作电流"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件开路电压(Voc)(V)：" prop="openVoltage">
                <el-input-number v-model="formDialog.openVoltage" :min="0" :max="100" controls-position="right" placeholder="请输入组件开路电压"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件短路电流(Isc)(A)：" prop="shortCurrent">
                <el-input-number v-model="formDialog.shortCurrent" :min="0" :max="50" controls-position="right" placeholder="请输入组件短路电流"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="最大功率(Pmax)的温度系数(%/°C)：" prop="maxPowerTempCoeff">
                <el-input-number v-model="formDialog.maxPowerTempCoeff" :min="-1" :max="0" controls-position="right" placeholder="请输入最大功率的温度系数"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="开路电压(Voc)的温度系数(%/°C)：" prop="openVoltTempCoeff">
                <el-input-number v-model="formDialog.openVoltTempCoeff" :min="-1" :max="0" controls-position="right" placeholder="请输入开路电压的温度系数"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="短路电流(Isc)的温度系数(%/°C)：" prop="shortCurrTempCoeff">
                <el-input-number v-model="formDialog.shortCurrTempCoeff" :min="0" :max="0.2" controls-position="right" placeholder="请输入短路电流的温度系数"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="标称组件转换效率(%)：">
                <el-input-number v-model="formDialog.convertEffi" :min="10" :max="50" controls-position="right" placeholder="请输入标称组件转换效率"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件首年衰减率(%/y)：" prop="firstDecayRate">
                <el-input-number v-model="formDialog.firstDecayRate" :min="0" :max="100" controls-position="right" placeholder="请输入组件首年衰减率"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组件逐年衰减率(%/y)：" prop="passingDecayRate">
                <el-input-number v-model="formDialog.passingDecayRate" :min="0" :max="100" controls-position="right" placeholder="请输入组件逐年衰减率"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <ParameterDescriptionDialog v-if="parameterDescriptionVisible" v-model:isVisible="parameterDescriptionVisible"></ParameterDescriptionDialog>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {onlyNum} from "@/utils/validate";
import {Document} from "@element-plus/icons-vue";
import ParameterDescriptionDialog from "./ParameterDescriptionDialog.vue";
import {saveOrUpdateModuleLibrary} from "@/api/operationManagement/PvComponentLibrary";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddComponentLibraryDialog",
  components:{ParameterDescriptionDialog},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新增组件库"
    },
    activeEditInfo: {
      type: Object,
      default: ()=>{
        return { };
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateChangeInput = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入该项参数"));
      } else {
        callback();
      }
    };

    const validateChangeSelect = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择该项参数"));
      } else {
        callback();
      }
    };

    const validateChangeNum = (rule, value, callback) => {
      if (!onlyNum(value)) {
        callback(new Error("请输入该项参数"));
      } else {
        callback();
      }
    };

    const that = reactive({
      Document,
      formDialog: {
        maxPowerTempCoeff: -0.41,
        openVoltTempCoeff: -0.31,
        shortCurrTempCoeff: 0.053,
      },
      listLoading: false,
      dialog_visible: props.isVisible,
      parameterDescriptionVisible: false,
      moduleTypeArray: [{id: 1, name: "多晶"}, {id: 2, name: "单晶"}, {id: 3, name: "叠瓦"}, {id: 4, name: "P型双面"}, {id: 5, name: "N型双面"}],
      rules: {
        moduleFactory: [{required: true, trigger: "change", validator: validateChangeInput }],
        moduleModel: [{required: true, trigger: "change", validator: validateChangeInput }],
        moduleType: [{required: true, trigger: "change", validator: validateChangeSelect }],
        batteryPieces: [{required: true, trigger: "change", validator: validateChangeNum }],
        maxPower: [{required: true, trigger: "change", validator: validateChangeNum }],
        bestWorkVoltage: [{required: true, trigger: "change", validator: validateChangeNum }],
        bestWorkCurrent: [{required: true, trigger: "change", validator: validateChangeNum }],
        openVoltage: [{required: true, trigger: "change", validator: validateChangeNum }],
        shortCurrent: [{required: true, trigger: "change", validator: validateChangeNum }],
        maxPowerTempCoeff: [{required: true, trigger: "change", validator: validateChangeNum }],
        openVoltTempCoeff: [{required: true, trigger: "change", validator: validateChangeNum }],
        firstDecayRate: [{required: true, trigger: "change", validator: validateChangeNum }],
        passingDecayRate: [{required: true, trigger: "change", validator: validateChangeNum }],
      }
    });

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          saveOrUpdateModuleLibrary({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const clickDocumentButFun = ()=>{
      that.parameterDescriptionVisible = true;
    };

    const initParamConfigFun = () => {
      that.formDialog = Object.assign({},props.activeEditInfo,that.formDialog);
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, clickDocumentButFun};
  }
});

</script>

<style scoped lang="scss">
//.textTwo{
//  -webkit-line-clamp: 1;
//}
//
.content_header{
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}
</style>