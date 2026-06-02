<template>
  <Dialog v-model:isVisible="dialog_visible" :listloading="listLoading" :manualEnterClose="false" :title="titleName" disabledloading width="780" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="小程序名称：" prop="appletName">
            <el-input v-model="formDialog.appletName" maxlength="32" placeholder="请输入小程序名称" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="小程序ID：" prop="appletCode">
            <el-input v-model="formDialog.appletCode" placeholder="请输入小程序ID" type="text"></el-input>
          </el-form-item>
          <el-form-item label="小程序类型：" prop="appletType">
            <el-select v-model="formDialog.appletType" placeholder="请选择小程序类型">
              <el-option v-for="item in appletTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="小程序密钥：" prop="appletSecret">
            <el-input v-model="formDialog.appletSecret" placeholder="请输入小程序密钥" type="text"></el-input>
          </el-form-item>
          <el-form-item label="联系电话：" prop="phone">
            <el-input v-model="formDialog.phone" placeholder="请输入联系电话" type="text"></el-input>
          </el-form-item>
          <el-form-item label="联系邮箱：">
            <el-table :data="formDialog.email" :max-height="tableMaxHeight">
              <el-table-column align="center" label="序号" type="index" width="60"></el-table-column>
              <el-table-column align="center" label="拥有人" width="125">
                <template #default="{ row }">
                  <el-input v-model="row.name" placeholder="请输入" type="text"></el-input>
                </template>
              </el-table-column>
              <el-table-column align="center" label="邮箱">
                <template #default="{ row }">
                  <el-input v-model="row.email" placeholder="请输入" type="text"></el-input>
                </template>
              </el-table-column>
              <el-table-column align="center" width="90">
                <template #header>
                  <div class="pointer" @click="clickTableEmail(1)">
                    <el-icon color="#007FEB" size="24" ><CirclePlusFilled /></el-icon>
                  </div>
                </template>
                <template #default="{ row,$index }">
                  <div class="pointer" @click="clickTableEmail(2,$index)">
                    <el-icon color="#FF1515" size="24" ><RemoveFilled /></el-icon>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item label="关联企业：">
            <el-select v-model="formDialog.tenantIds" multiple filterable placeholder="请选择关联企业">
              <el-option v-for="item in tenantIdArray" :key="item.id" :label="item.tenantName" :value="item.id"></el-option>
            </el-select>
            <div class="alter_text">注：如果不选择关联企业，则小程序会展示所有企业下的站点。</div>
          </el-form-item>
          <el-form-item label="小程序Logo：" prop="appletLogoArray">
            <UploadPicturesCom v-model:fileArray="formDialog.appletLogoArray"></UploadPicturesCom>
          </el-form-item>
          <el-collapse>
            <el-collapse-item title="关联公众号信息" name="officialAccount">
              <el-form-item label="公众号名称：">
                <el-input v-model="formDialog.tencentName" maxlength="32" placeholder="请输入公众号名称" show-word-limit type="text"></el-input>
              </el-form-item>
              <el-form-item label="公众号ID：">
                <el-input v-model="formDialog.tencentCode" placeholder="请输入公众号ID" type="text"></el-input>
              </el-form-item>
              <el-form-item label="公众号密钥：">
                <el-input v-model="formDialog.tencentSecret" placeholder="请输入公众号密钥" type="text"></el-input>
              </el-form-item>
              <el-form-item label="公众号二维码：">
                <UploadPicturesCom v-model:fileArray="formDialog.tencentImageArray"></UploadPicturesCom>
              </el-form-item>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>
<script>
import {ElMessage} from "element-plus";
import {someCharmap} from "@/utils/validate";
import {CirclePlusFilled, RemoveFilled} from '@element-plus/icons-vue';
import {findTenantInfoByPage} from "@/api/tenantManagement/tenantTabulation";
import UploadPicturesCom from "@/components/component/UploadPicturesCom.vue";
import {findAppletDetailById, saveApplet} from "@/api/configCenter/appletManagement";
import {defineComponent, getCurrentInstance, onMounted, reactive, ref, toRefs, watch} from "vue";

export default defineComponent({
  name: "AddAppletDialog",
  components: {UploadPicturesCom,CirclePlusFilled, RemoveFilled},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "创建场景"
    },
    activeAppletId: {
      type: [String, Number],
      default: ""
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();

    const validateAppletName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的小程序名称"));
      } else {
        callback();
      }
    };

    const validateAppletCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入小程序ID"));
      } else {
        callback();
      }
    };

    const validateAppletType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择小程序类型"));
      } else {
        callback();
      }
    };

    const validateAppletSecret = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入小程序密钥"));
      } else {
        callback();
      }
    };

    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入联系电话"));
      } else {
        callback();
      }
    };

    const validateAppletLogoArray = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请上传小程序Logo"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      tableMaxHeight: 280,
      dialog_visible: props.isVisible,

      tenantIdArray: [],
      appletTypeArray: [{id: 1, name: '微信'}, {id: 2, name: "支付宝"}],
      rules: {
        phone: [{required: true, trigger: "change", validator: validatePhone}],
        appletName: [{required: true, trigger: "change", validator: validateAppletName}],
        appletCode: [{required: true, trigger: "change", validator: validateAppletCode}],
        appletType: [{required: true, trigger: "change", validator: validateAppletType}],
        appletSecret: [{required: true, trigger: "change", validator: validateAppletSecret}],
        appletLogoArray: [{required: true, trigger: "change", validator: validateAppletLogoArray}],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {

          that.listLoading = true;
          let formData = new FormData();

          if (that.formDialog.appletLogoArray && that.formDialog.appletLogoArray.length) {
            for (let i = 0; i < that.formDialog.appletLogoArray.length; i++) {
              if (that.formDialog.appletLogoArray[i].raw) {
                formData.append("appletLogo", that.formDialog.appletLogoArray[i].raw);
              }
            }
          }

          if (that.formDialog.tencentImageArray && that.formDialog.tencentImageArray.length) {
            for (let i = 0; i < that.formDialog.tencentImageArray.length; i++) {
              if (that.formDialog.tencentImageArray[i].raw) {
                formData.append("tencentImage", that.formDialog.tencentImageArray[i].raw);
              }
            }
          }

          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          delete formDialog.appletLogoArray
          delete formDialog.tencentImageArray

          for (let key in formDialog){
            if(Array.isArray(formDialog[key]))formDialog[key] = JSON.stringify(formDialog[key]);
            formData.append(key, formDialog[key]);
          }

          saveApplet(formData).then(() => {
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({type: "success", showClose: true, message: "操作成功！"});
          }).catch(() => {
            that.listLoading = false;
          })
        }
      })
    }

    // 分页查询租户信息
    const queryTenantInfoByPage = () => {
      findTenantInfoByPage({page: 1, size: 0,timer: new Date() }).then(res => {
        that.tenantIdArray = res.data ? res.data : [];
      })
    }

    const clickTableEmail = (operateType,index)=>{

      if(operateType === 1){
        if(!that.formDialog.email)that.formDialog.email = [];
        that.formDialog.email.push({});
      }

      if(operateType === 2){
        that.formDialog.email.splice(index,1);
      }
    }

    // 初始化参数配置
    const initParamConfigFun = () => {
      // console.log(props.formDialog);

      if (props.activeAppletId) {
        findAppletDetailById({id: props.activeAppletId}).then(res => {
          let formDialog = res.data ? res.data : {};

          if (formDialog.tencentImage)formDialog.tencentImageArray = [{url: formDialog.tencentImage}];
          delete formDialog.tencentImage

          if (formDialog.appletLogo)formDialog.appletLogoArray = [{url: formDialog.appletLogo}];
          delete formDialog.appletLogo

          if (formDialog.email) formDialog.email = JSON.parse(formDialog.email);
          if (formDialog.tenantIds) formDialog.tenantIds = JSON.parse(formDialog.tenantIds);
          for (let key in formDialog)if(!formDialog[key])formDialog[key] = null;

          that.formDialog = JSON.parse(JSON.stringify(formDialog));
          // console.log(that.formDialog)
        })
      }
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
      queryTenantInfoByPage();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, queryTenantInfoByPage, clickTableEmail}
  }
})
</script>

<style lang="scss" scoped>
.alter_text{
  color: #666666;
  font-size: 12px;
}
</style>