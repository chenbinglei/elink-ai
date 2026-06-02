<template>
  <div class="AmendPassword flex jc-center ai-center">
    <el-form label-position="right" label-width="100px" ref="formRefline" :model="formInline" :rules="rules" style="width: 360px">
      <el-form-item label="原始密码:" prop="originalPassword">
        <el-input type="text" v-model="formInline.originalPassword" placeholder="请输入原始密码" />
      </el-form-item>
      <el-form-item label="新密码:" prop="newPassword">
        <el-input type="password" show-password v-model="formInline.newPassword" placeholder="请输入新密码" />
      </el-form-item>
      <el-form-item label="确认密码:" prop="confirmPassword">
        <el-input type="password" show-password v-model="formInline.confirmPassword" placeholder="请输入确认密码" />
      </el-form-item>
      <el-form-item>
        <div class="but-group">
          <el-button @click="closeDialoig">取消</el-button>
          <el-button type="primary" :loading="loading" @click="saveDialoig">确认</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {nullToDelete} from "@/utils";
import { ElMessage } from "element-plus";
import { isvalidPassword } from "@/utils/validate";
import {reactive, toRefs, ref, onMounted} from "vue";
import {updateUserPassword} from "@/api/personalCenter/personalCenter";
export default {
  name: "AmendPassword",
  setup() {

    const validateOriginalPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入原密码"));
      } else {
        callback();
      }
    };

    const validateNewPassword = (rule, value, callback) => {
      if (!isvalidPassword(value)) {
        callback(new Error("请输入正确的新密码"));
      } else {
        if (that.formInline.originalPassword == that.formInline.newPassword) {
          callback(new Error("原密码不能与新密码一致"));
        } else {
          callback();
        }
      }
    };

    const validateConfirmPassword = (rule, value, callback) => {
      if (!isvalidPassword(value)) {
        callback(new Error("请填写新密码"));
      } else {
        if (that.formInline.newPassword != that.formInline.confirmPassword) {
          callback(new Error("两次密码输入不一致"));
        } else {
          callback();
        }
      }
    };

    const that = reactive({
      loading: false,
      formInline: {
        originalPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
      rules:{
        originalPassword: [{ required: true, trigger: "change", validator: validateOriginalPassword, }],
        newPassword: [{ required: true, trigger: "change", validator: validateNewPassword }],
        confirmPassword: [{ required: true, trigger: "change", validator: validateConfirmPassword, }],
      }
    });

    const getUserInfo = () => {
      // 修改完成个人信息，同步更新  userInfo
      let userInfoObj = JSON.parse(localStorage.getItem('USER_INFO'));
      that.formInline.phone = userInfoObj.phone;
      that.formInline.userAccount = userInfoObj.userAccount;
    };

    const formRefline = ref(null);
    const saveDialoig = () => {
      formRefline.value.validate((valid) => {
        if (valid) {
          that.loading = true;
          updateUserPassword({ password: that.formInline.confirmPassword, phone: that.formInline.phone, userAccount: that.formInline.userAccount }).then((result) => {
              closeDialoig();
              that.loading = false;
              formRefline.value.clearValidate();
              ElMessage({ type: "success", message: "密码修改成功！", showClose: true, });
            }).catch((err) => {
              that.loading = false;
            });
        }
      });
    };

    const closeDialoig = () => {
      for (let key in that.formInline) that.formInline[key] = "";
    };

    onMounted (() => {
      getUserInfo();
    });

    return { ...toRefs(that), closeDialoig, saveDialoig, formRefline, getUserInfo };
  },
};
</script>

<style scoped lang="scss">
.AmendPassword {
  flex: 1;

  .but-group {
    margin: auto;
  }
}
</style>
