<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="form_dialog" :rules="rules" label-width="130px">
          <el-form-item label="租户名称：" prop="tenantName">
            <el-input v-model="form_dialog.tenantName" maxlength="32" show-word-limit placeholder="请输入租户名称"/>
          </el-form-item>
          <el-form-item label="状态：">
            <el-switch v-model="form_dialog.tenantState" :active-value="1" :inactive-value="0"/>
          </el-form-item>
          <el-form-item label="管理员账号：" prop="superAccount">
            <el-input v-model="form_dialog.superAccount" maxlength="32" show-word-limit placeholder="请输入管理员账号"/>
          </el-form-item>
          <el-form-item label="密码：" prop="password">
            <el-input v-model="form_dialog.password" placeholder="请输入密码" type="password" show-password/>
          </el-form-item>
          <el-form-item label="请重复密码：" prop="repeatPassword">
            <el-input v-model="form_dialog.repeatPassword" placeholder="请输入密码" type="password" show-password/>
          </el-form-item>
          <template v-if="isDetails">
            <el-form-item label="地址：" prop="address">
              <el-input v-model="form_dialog.address" maxlength="50" show-word-limit placeholder="请输入地址"/>
            </el-form-item>
            <el-form-item label="组织机构代码：" prop="organizationCode">
              <el-input v-model="form_dialog.organizationCode" maxlength="50" show-word-limit placeholder="请输入组织机构代码"/>
            </el-form-item>
            <el-form-item label="描述：" prop="refer">
              <el-input v-model="form_dialog.refer" maxlength="16" show-word-limit placeholder="请输入描述"/>
            </el-form-item>
          </template>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {useStore} from "vuex";
import {ElMessage} from "element-plus";
import {Delete, Plus} from "@element-plus/icons-vue";
import {commonCharName, isvalidPassword, someCharmap} from "@/utils/validate";
import {saveOrUpdateTenantInfo} from "@/api/tenantManagement/tenantTabulation";
import {computed, getCurrentInstance, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddTenantDialog",
  components:{Plus,Delete},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新增租户"
    },
    // 是否为详情展示
    isDetails: {
      type: Boolean,
      default: false
    },
    formDialog: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {

    const store = useStore();
    const {emit} = getCurrentInstance();

    const userInfo = computed(() => {
      return store.state.app.userInfo;
    });

    const validateTenantName = (rule, value, callback) => {
      if(!value || !commonCharName(value)){
        callback(new Error("请输入正确的分组名称"));
      } else {
        callback();
      }
    };
    const validateSuperAccount = (rule, value, callback) => {
      if(!value || !someCharmap(value)){
        callback(new Error("请输入正确的管理员账号"));
      } else {
        callback();
      }
    };
    const validatePassword = (rule, value, callback) => {
      if(!value || !isvalidPassword(value)){
        callback(new Error("请输入正确的密码"));
      } else {
        callback();
      }
    };
    const validateRepeatPassword = (rule, value, callback) => {
      if(!value || !isvalidPassword(value)) {
        callback(new Error("请输入正确的密码"));
      } else if(that.form_dialog.password !== that.form_dialog.repeatPassword) {
        callback(new Error("两次密码不一致"));
      } else {
        callback();
      }
    };
    const that = reactive({
      form_dialog: {...props.formDialog},
      dialog_visible: props.isVisible,
      listLoading: false,

      rules: {
        tenantName: [{ required: true, trigger: "change", validator: validateTenantName }],
        superAccount: [{ required: true, trigger: "change", validator: validateSuperAccount }],
        password: [{ required: true, trigger: "change", validator: validatePassword }],
        repeatPassword: [{ required: true, trigger: "change", validator: validateRepeatPassword }]
      },
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let form_dialog = JSON.parse(JSON.stringify(that.form_dialog));
          if(props.isDetails){
            form_dialog.updateUserName = userInfo.value.fullName;
          } else {
            form_dialog.createUserName = userInfo.value.fullName;
          }
          saveOrUpdateTenantInfo({...form_dialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return { ...toRefs(that),watchVisible,watchDialogVisible,formDialogRef,saveDialog }
  }
})
</script>

<style scoped>

</style>
