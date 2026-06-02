<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="680" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form :model="form_dialog" :rules="rules" ref="formDialogRef" label-width="90px">
          <el-form-item label="原密码:">
            <el-input type="password" v-model="form_dialog.operatePassword" disabled></el-input>
          </el-form-item>
          <el-form-item label="新密码:" prop="newPassword">
            <el-input type="password" v-model="form_dialog.newPassword" maxlength="4" show-password placeholder="请输入新密码"></el-input>
          </el-form-item>
          <el-form-item label="重复密码:" prop="repeatPassword">
            <el-input type="password" v-model="form_dialog.repeatPassword" :disabled="!form_dialog.newPassword" maxlength="4" show-password placeholder="请再次输入新密码"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {num1to9999, onlyNum} from "@/utils/validate";
import {updateSiteSetUp} from "@/api/siteCenter/stationDetails";
import {getCurrentInstance, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "OperationPasswordDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: ""
    },
    formDialog: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {

    const validateNewPassword = (rule, value, callback) => {
      if (!onlyNum(value)) {
        callback(new Error("请输入正确的密码"));
      } else {
        callback();
      }
    };

    const validateRepeatPassword = (rule, value, callback) => {
      if (value !== that.form_dialog.newPassword) {
        callback(new Error("两次输入密码不一致"));
      } else {
        callback();
      }
    };

    const {emit} = getCurrentInstance();

    const that = reactive({
      listLoading: false,
      titleName: "修改密码",
      form_dialog: props.formDialog,
      dialog_visible: props.isVisible,
      rules: {
        newPassword: [{required: true, trigger: "change", validator: validateNewPassword }],
        repeatPassword: [{required: true, trigger: "change", validator: validateRepeatPassword }],
      },
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          let form_dialog = JSON.parse(JSON.stringify(that.form_dialog));
          form_dialog.operatePassword = form_dialog.newPassword;
          updateSiteSetUp(form_dialog).then(() => {
            emit("changEvent");
            that.dialog_visible = false;
            ElMessage({type: "success", showClose: true, message: "保存成功！"});
          })
        }
      });
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return { ...toRefs(that), watchVisible, watchDialogVisible, saveDialog, formDialogRef }
  }
})
</script>

<style scoped lang="scss">
.maxHeight {
  width: 100%;
  max-height: 260px;
  padding-right: 8px;
  overflow: auto;
  cursor: move;

  .text-center{
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .iconRedColor {
    color: #FF0000;
  }
}

.iconfont {
  display: block;

  &::before {
    margin-right: 5px;
  }
}
</style>
