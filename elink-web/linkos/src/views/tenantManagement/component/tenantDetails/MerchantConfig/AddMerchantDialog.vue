<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="140px">
          <el-form-item label="商户平台类型：" prop="platformType">
            <el-select v-model="formDialog.platformType">
              <el-option v-for="(item,index) in platformTypeArray" :key="index" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="商户全称：" prop="mchName">
            <el-input v-model="formDialog.mchName" maxlength="32" show-word-limit placeholder="请输入商户全称"></el-input>
          </el-form-item>
          <el-form-item label="商户类型：" prop="mchType">
            <el-select v-model="formDialog.mchType" placeholder="请选择商户类型">
              <el-option v-for="(item,index) in mchTypeArray" :key="index" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="商户号：" prop="mchId">
            <el-input v-model="formDialog.mchId"  maxlength="10" show-word-limit placeholder="请输入商户号"></el-input>
          </el-form-item>
          <el-form-item label="商户密钥：" prop="mchKey">
            <el-input v-model="formDialog.mchKey" maxlength="64" show-word-limit placeholder="请输入商户密钥"></el-input>
          </el-form-item>
          <el-form-item label="APIv3密钥：" prop="apiV3Key">
            <el-input v-model="formDialog.apiV3Key" maxlength="64" show-word-limit placeholder="请输入APIv3密钥"></el-input>
          </el-form-item>
          <el-form-item label="商户证书序列号：" prop="serialNo">
            <el-input v-model="formDialog.serialNo" maxlength="64" show-word-limit placeholder="请输入商户证书序列号"></el-input>
          </el-form-item>

          <el-form-item label="API类型：">
            <el-select v-model="formDialog.apiType" placeholder="请选择API类型">
              <el-option v-for="(item,index) in apiTypeArray" :key="index" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="rsaSerialNo">
            <template #label>
              <span v-if="formDialog.apiType === 1">RSA证书序列号：</span>
              <span v-if="formDialog.apiType === 2">商户公钥ID：</span>
            </template>
            <el-input v-model="formDialog.rsaSerialNo" maxlength="64" show-word-limit placeholder="请输入RSA证书序列号"></el-input>
          </el-form-item>
<!--          <el-form-item label="商户p12文件：" prop="certP12File_List">-->
<!--            <UploadFileCustom ref="uploadFileCustomRef" acceptType=".p12" :classFileType="2" v-model:fileArray="formDialog.certP12File_List">-->
<!--              <template #tip_content>-->
<!--                <div class="alter_text">请选择后缀为.p12类型的文件。</div>-->
<!--              </template>-->
<!--            </UploadFileCustom>-->
<!--          </el-form-item>-->
<!--          <el-form-item label="商户cert文件：" prop="certFile_List">-->
<!--            <UploadFileCustom ref="uploadFileCustomRef" acceptType=".pem" :classFileType="2" v-model:fileArray="formDialog.certFile_List">-->
<!--              <template #tip_content>-->
<!--                <div class="alter_text">请选择后缀为.pem类型的文件。</div>-->
<!--              </template>-->
<!--            </UploadFileCustom>-->
<!--          </el-form-item>-->
          <el-form-item label="商户key文件：" prop="keyPemFile_List">
            <UploadFileCustom ref="uploadFileCustomRef" acceptType=".pem" :classFileType="2" v-model:fileArray="formDialog.keyPemFile_List">
              <template #tip_content>
                <div class="alter_text">请选择后缀为.pem类型的文件。</div>
              </template>
            </UploadFileCustom>
          </el-form-item>
          <el-form-item label="商户公钥文件：" prop="pubKeyFile_List" v-if="formDialog.apiType === 2">
            <UploadFileCustom ref="uploadFileCustomRef" acceptType=".pem" :classFileType="2" v-model:fileArray="formDialog.pubKeyFile_List">
              <template #tip_content>
                <div class="alter_text">请选择后缀为.pem类型的文件。</div>
              </template>
            </UploadFileCustom>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>
<script>
import {ElMessage} from "element-plus";
import {notCharmap, onlyNum} from '@/utils/validate';
import {saveTenantAccount} from "@/api/tenantManagement/merchantConfig";
import UploadFileCustom from "@/components/component/UploadFileCustom.vue";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddWhiteListDialog",
  components: {UploadFileCustom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新增商户"
    },
    activeEditInfo: {
      type: Object,
      default: () => {
        return {}
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateMchName = (rule, value, callback) => {
      if (!value || !notCharmap(value)) {
        callback(new Error("请输入正确的商户名称"));
      } else {
        callback();
      }
    };

    const validateMchId = (rule, value, callback) => {
      if(that.formDialog.platformType === 1){
        if (value && !onlyNum(value)) {
          callback(new Error("请输入正确的商户号"));
        } else if(!value) {
          callback(new Error("请输入商户号"));
        } else if(value.length < 8 || value.length > 10) {
          callback(new Error("支持商户号输入8~10位数字"));
        } else {
          callback();
        }
      } else {
        if(!value){
          callback(new Error("请输入正确的账户id"));
        } else {
          callback();
        }
      }
    };

    const validateMchKey = (rule, value, callback) => {
      if (!value || !notCharmap(value)) {
        callback(new Error("请输入正确的商户密钥"));
      } else {
        callback();
      }
    };

    const validateMchType = (rule, value, callback) => {
      if (!value || !notCharmap(value)) {
        callback(new Error("请选择商户类型"));
      } else {
        callback();
      }
    };

    const validateApiV3Key = (rule, value, callback) => {
      if (!value || !notCharmap(value)) {
        callback(new Error("请输入正确的APIv3密钥"));
      } else {
        callback();
      }
    };

    const validateSerialNo = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入证书序列号"));
      } else {
        callback();
      }
    };

    const validateFileList = (rule, value, callback) => {
      if(!that.formDialog.id){
        if (!value || !value.length) {
          callback(new Error("请上传配置文件"));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      dialog_visible: props.isVisible,
      formDialog: { platformType: 1,apiType: 1 },
      platformTypeArray: [{ id: 1, name: "微信平台" }],  // ,{ id: 2, name: "支付宝平台" }
      mchTypeArray: [{ id: 1, name: "商户号" },{ id: 2, name: "个人openid" }],
      apiTypeArray: [{ id: 1, name: "平台证书" },{ id: 2, name: "微信支付公钥" }],
      rules: {
        mchName: [{required: true, trigger: "change", validator: validateMchName }],
        mchId: [{required: true, trigger: "change", validator: validateMchId }],
        // mchKey: [{required: true, trigger: "change", validator: validateMchKey }],
        // mchType: [{required: true, trigger: "change", validator: validateMchType }],
        apiV3Key: [{required: true, trigger: "change", validator: validateApiV3Key}],
        serialNo: [{required: true, trigger: "change", validator: validateSerialNo }],
        rsaSerialNo: [{required: true, trigger: "change", validator: validateSerialNo }],
        // certP12File_List: [{required: true, trigger: "change", validator: validateFileList}],
        // certFile_List: [{required: true, trigger: "change", validator: validateFileList}],
        keyPemFile_List: [{required: true, trigger: "change", validator: validateFileList}],
        pubKeyFile_List: [{required: true, trigger: "change", validator: validateFileList}],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formData = new FormData();
          for (let key in that.formDialog){
            if(key.indexOf('File_List') !== -1){
              let key_arr = key.split("_");
              if(that.formDialog[key] && that.formDialog[key].length){
                for (let i = 0; i < that.formDialog[key].length; i++) {
                  if (that.formDialog[key][i].raw){
                    formData.append(key_arr[0], that.formDialog[key][i].raw);
                  }
                }
              }
            } else {
              formData.append(key, that.formDialog[key]);
            }
          }

          saveTenantAccount(formData).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const initParamConfigFun = () => {
      that.formDialog = Object.assign({},that.formDialog,props.activeEditInfo);
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