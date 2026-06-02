<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680px" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="平台名称：" prop="platformName">
            <el-input v-model="formDialog.platformName" maxlength="32" placeholder="请输入平台名称" type="text" show-word-limit></el-input>
          </el-form-item>
          <el-form-item label="平台标识：" prop="platformLogo">
            <el-input v-model="formDialog.platformLogo" placeholder="请输入平台标识" type="text" maxlength="6" show-word-limit></el-input>
          </el-form-item>
          <el-form-item label="ip地址：" prop="ipAddress">
            <el-input v-model="formDialog.ipAddress" placeholder="请输入ip地址" type="text"></el-input>
          </el-form-item>
          <el-form-item label="端口号：" prop="portNumber">
            <el-input v-model="formDialog.portNumber" placeholder="请输入端口号" type="text"></el-input>
          </el-form-item>
          <el-form-item label="协议类型：" prop="protocolType">
            <el-select v-model="formDialog.protocolType" placeholder="请选择协议类型">
              <el-option v-for="item in protocolTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
<!--          <el-form-item label="备注：">-->
<!--            <el-input v-model="formDialog.notes" placeholder="请输入" maxlength="200" :rows="3" type="textarea" show-word-limit></el-input>-->
<!--          </el-form-item>-->
        </el-form>
      </div>
    </template>
  </Dialog>
</template>
<script>
import {ElMessage} from "element-plus";
import {saveOrUpdatePlatformInfo} from "@/api/configCenter/platformManagement";
import {letterNumLine, onlyNum, serviceAddressVerification, someCharmap} from "@/utils/validate";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddWhiteListDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: [Number, String],
      default: "添加白名单"
    },
    activeEditInfo: {
      type: Object,
      default: ()=>{
        return{}
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validatePlatformName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的平台名称"));
      } else {
        callback();
      }
    };

    const validatePlatformLogo = (rule, value, callback) => {
      if (!value || !letterNumLine(value)) {
        callback(new Error("请输入正确的平台标识"));
      } else {
        callback();
      }
    };

    const validateIpAddress = (rule, value, callback) => {
      if (!serviceAddressVerification(value)) {
        callback(new Error("请输入正确的ip地址"));
      } else {
        callback();
      }
    };

    const validatePortNumber = (rule, value, callback) => {
      if (!onlyNum(value)) {
        callback(new Error("请输入正确的端口号"));
      } else {
        callback();
      }
    };

    const validateProtocolType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择协议类型"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {  },
      listLoading: false,
      dialog_visible: props.isVisible,
      protocolTypeArray:[{id:"ykcProtShadow",name:"云快充协议"},{id:"smProtShadow",name:"晟曼协议"}],
      rules: {
        platformName: [{required: true, trigger: "change", validator: validatePlatformName}],
        platformLogo: [{required: true, trigger: "change", validator: validatePlatformLogo}],
        ipAddress: [{required: true, trigger: "change", validator: validateIpAddress}],
        portNumber: [{required: true, trigger: "change", validator: validatePortNumber}],
        protocolType: [{required: true, trigger: "change", validator: validateProtocolType}],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          // 添加或修改平台信息
          saveOrUpdatePlatformInfo({ ...that.formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const initParamConfigFun = () => {
      that.formDialog = Object.assign({},props.activeEditInfo,that.formDialog);
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef}
  }
})
</script>
<style scoped lang="scss">

</style>