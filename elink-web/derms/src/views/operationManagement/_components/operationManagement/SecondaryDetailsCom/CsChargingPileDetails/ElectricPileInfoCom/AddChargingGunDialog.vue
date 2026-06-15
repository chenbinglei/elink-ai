<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="640" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main scrollbarStyle" v-loading="listLoading">
        <el-form ref="formDialogRef" :model="formdialog" :rules="rules" label-width="145px">
          <el-form-item label="枪名称：" prop="gunName">
            <el-input v-model="formdialog.gunName" maxlength="32" placeholder="请输入枪名称" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="枪编号：" prop="gunCode">
            <el-input v-model="formdialog.gunCode" placeholder="请输入枪编号" :disabled="formDialog.id"></el-input>
          </el-form-item>
          <el-form-item label="枪型号：" prop="type">
            <el-select v-model="formdialog.type" placeholder="请选择枪型号">
              <el-option v-for="item in typeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="额定功率：" prop="ratedPower">
            <el-input-number v-model="formdialog.ratedPower" placeholder="请输入额定功率" controls-position="right"/>
          </el-form-item>
          <el-form-item label="额定电流：" prop="ratedCurrent">
            <el-input-number v-model="formdialog.ratedCurrent" placeholder="请输入额定电流" controls-position="right"/>
          </el-form-item>
          <el-form-item label="额定电压上限：" prop="voltageUpperLimits">
            <el-input-number v-model="formdialog.voltageUpperLimits" placeholder="请输入额定电压上限" controls-position="right"/>
          </el-form-item>
          <el-form-item label="额定电压下限：" prop="voltageLowerLimits">
            <el-input-number v-model="formdialog.voltageLowerLimits" placeholder="请输入额定电压下限" controls-position="right"/>
          </el-form-item>
          <el-form-item label="车位号：">
            <el-input v-model="formdialog.parkNo" placeholder="请输入车位号" type="text"></el-input>
          </el-form-item>
          <el-form-item label="国家标准：" prop="nationalStandard">
            <el-select v-model="formdialog.nationalStandard" placeholder="请选择枪型号">
              <el-option v-for="item in nationalStandardArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="辅助电源：" prop="auxPower">
            <el-select v-model="formdialog.auxPower" placeholder="请选择枪型号">
              <el-option v-for="item in auxPowerArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="设备接口唯一码：">
            <el-input v-model="formdialog.connectorUniqueId" placeholder="请输入充电设备接口唯一码" type="text"></el-input>
          </el-form-item>
          <el-form-item label="二维码解析地址：">
            <el-input v-model="formdialog.qrCodes" maxlength="100" placeholder="请输入二维码解析地址" :rows="2" show-word-limit type="textarea"></el-input>
          </el-form-item>
          <el-form-item label="防护等级：">
            <el-input v-model="formdialog.ipGrade" placeholder="请输入防护等级" type="text"></el-input>
          </el-form-item>
          <el-form-item label="外观：">
            <el-input v-model="formdialog.appearance" maxlength="100" placeholder="请输入外观" :rows="2" show-word-limit type="textarea"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {letterNumLine, someCharmap} from "@/utils/validate";
import {getCurrentInstance, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {updatePileGunDetailById} from "@/api/operationManagement/CsPileGunRunningStatus";

export default defineComponent({
  name: "AddChargingGunDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加充电枪"
    },
    formDialog: {
      type: Object,
      default: ()=>{
        return { };
      }
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();

    const validateGunName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的枪名称"));
      } else {
        callback();
      }
    };

    const validateGunCode = (rule, value, callback) => {
      if (!value || !letterNumLine(value)) {
        callback(new Error("请输入正确的枪编号"));
      } else {
        callback();
      }
    };

    const validateNoSelect = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择"));
      } else {
        callback();
      }
    };

    const validateNoInput = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      formdialog: props.formDialog,
      dialog_visible: props.isVisible,
      auxPowerArray:[{id: 1,name: "12V"},{id: 2,name: "24V"},{id: 3,name: "兼容12V和24V"}],
      typeArray:[{id: 1,name: "家用插座"},{id: 2,name: "交流接口插座"},{id: 3,name:"交流接口插头"},{id: 4,name: "直流接口枪头"}],
      nationalStandardArray:[{id: 1,name: "2011"},{id: 2,name: "2015"},{id: 3,name: "兼容2011和2015"},{id: 4,name: "2023"}],

      rules: {
        type: [{ required: true, trigger: "change", validator: validateNoSelect }],
        gunName: [{ required: true, trigger: "change", validator: validateGunName }],
        gunCode: [{ required: true, trigger: "change", validator: validateGunCode }],
        ratedPower: [{ required: true, trigger: "change", validator: validateNoInput }],
        ratedCurrent: [{ required: true, trigger: "change", validator: validateNoInput }],
        nationalStandard: [{ required: true, trigger: "change", validator: validateNoSelect }],
        voltageUpperLimits: [{ required: true, trigger: "change", validator: validateNoInput }],
        voltageLowerLimits: [{ required: true, trigger: "change", validator: validateNoInput }],
        qrCodes: [{ required: true, trigger: "change", validator: validateNoInput }],
        auxPower: [{ required: true, trigger: "change", validator: validateNoSelect }],
      }
    });

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formdialog));
          for(let key in formDialog)if(!formDialog[key])formDialog[key] = "";

          updatePileGunDetailById({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return { ...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog };
  }
});
</script>

<style scoped>
.dialog-main{
  max-height: 620px;
  overflow-y: auto;
}
</style>
