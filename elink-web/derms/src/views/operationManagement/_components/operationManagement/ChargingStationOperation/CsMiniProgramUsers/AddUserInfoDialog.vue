<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="平台类型" prop="platformType">
            <el-select v-model="formDialog.platformType" placeholder="请选择平台类型" :disabled="formDialog.id">
              <el-option v-for="item in platformTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="用户昵称" prop="nickName">
            <el-input v-model="formDialog.nickName" placeholder="请输入用户昵称" maxlength="24" show-word-limit/>
          </el-form-item>
          <el-form-item label="手机号码" prop="phoneNum">
            <el-input v-model="formDialog.phoneNum" placeholder="请输入手机号码" maxlength="11" show-word-limit/>
          </el-form-item>
          <el-form-item label="用户分组" prop="groupId">
            <el-select v-model="formDialog.groupId" filterable placeholder="请选择用户分组">
              <el-option v-for="item in groupIdArray" :key="item.id" :label="item.groupName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="邮箱" prop="mailbox">
            <el-input v-model="formDialog.mailbox" placeholder="请输入邮箱"/>
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="formDialog.refer" type="textarea" :rows="3" placeholder="请输入描述" maxlength="200" show-word-limit/>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {pay_plat_form_array} from "@/utils/setVariate";
import {isValidEmail, mobile, someCharmap} from "@/utils/validate";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";
import {saveOrUpdateAppletUser,queryAllUserGroupList} from "@/api/operationManagement/CsMiniProgramUsers";

export default defineComponent({
  name:"AddUserInfoDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加用户"
    },
    activeEditDataInfo: {
      type: Object,
      default: ()=>{
        return { };
      }
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const validatePlatformType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择平台类型"));
      } else {
        callback();
      }
    };

    const validateNickName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的用户昵称"));
      } else {
        callback();
      }
    };

    const validatePhoneNum = (rule, value, callback) => {
      if (!value || !mobile(value)) {
        callback(new Error("请输入正确的手机号码"));
      } else {
        callback();
      }
    };

    const validateGroupId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择用户分组"));
      } else {
        callback();
      }
    };

    const validateMailbox = (rule, value, callback) => {
      if (value && !isValidEmail(value)) {
        callback(new Error("请输入正确的邮箱"));
      } else {
        callback();
      }
    };

    const that = reactive({
      groupIdArray: [],
      listLoading: false,
      formDialog: { userState: 1 },
      dialog_visible: props.isVisible,
      platformTypeArray: pay_plat_form_array,
      rules:{
        mailbox: [{required: false, trigger: "change", validator: validateMailbox}],
        groupId: [{required: true, trigger: "change", validator: validateGroupId }],
        nickName: [{required: true, trigger: "change", validator: validateNickName }],
        phoneNum: [{required: true, trigger: "change", validator: validatePhoneNum }],
        platformType: [{required: true, trigger: "change", validator: validatePlatformType }],
      }
    });

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          saveOrUpdateAppletUser({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const initParamConfigFun = ()=>{
      that.formDialog = Object.assign({},that.formDialog,props.activeEditDataInfo);
      // console.log(that.formDialog);
    };

    // 查询全部用户分组列表
    const findAllUserGroupList = ()=>{
      queryAllUserGroupList({ timer: new Date() }).then(res=>{
        that.groupIdArray = res.data;
      });
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      initParamConfigFun();
      findAllUserGroupList();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, initParamConfigFun, findAllUserGroupList, formDialogRef};
  }
});
</script>

<style scoped lang="scss">

</style>