<template>
  <div class="w-full h-full">
    <!-- 弹框 查看-->
    <el-dialog v-model="isVisibleWarning" width="40%" align-center @close="closeDialog" title="管理人员">
      <template #header>
        <el-icon color="#fff" size="16">
          <CirclePlus />
        </el-icon><span style="color: #fff" class="ml-1">{{
          activeRow.id ? "编辑用户" : "新增用户"
        }}</span>
      </template>
      <el-form :model="addUserForm" label-width="auto" class="p-15px" :rules="rules" ref="formRef">
        <el-form-item label="所属组织" v-model="addUserForm.organId" prop="organId">
          <el-tree-select v-model="addUserForm.organId" :data="handleMenuArray" check-strictly
            :props="handleMenuTreeProps" />
        </el-form-item>
        <el-form-item label="角色" prop="groupId">
          <el-select v-model="addUserForm.groupId" placeholder="请选择角色">
            <el-option :label="item.groupName" :value="item.id" v-for="item in roleList" :key="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户账号:" prop="userAccount">
          <el-input v-model="addUserForm.userAccount" placeholder="请输入用户账号" type="text" />
        </el-form-item>
        <el-form-item label="密码:" prop="password">
          <el-input v-model="addUserForm.password" placeholder="请输入密码" type="password" autocomplete="new-password" />
        </el-form-item>
        <el-form-item label="确认密码:" prop="repeatPassword">
          <el-input v-model="addUserForm.repeatPassword" placeholder="请确认密码" type="password" />
        </el-form-item>
        <el-form-item label="姓名:" prop="fullName">
          <el-input v-model="addUserForm.fullName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="电话:" prop="phone">
          <el-input v-model="addUserForm.phone" placeholder="请输入电话" type="number" />
        </el-form-item>
        <el-form-item label="状态:" prop="userState">
          <el-switch v-model="addUserForm.userState" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="到期时间:" prop="expireDate">
          <el-date-picker v-model="addUserForm.expireDate" type="date" placeholder="请输入到期时间" value-format="YYYY-MM-DD"
            style="width: 100%;padding-left: 0;" />
        </el-form-item>
        <el-form-item label="头像:" prop="imageFile">
          <el-upload action="#" :auto-upload="false" :on-change="handleFileChange" :file-list="fileList"
            :show-file-list="false" :limit="1" accept=".jpg, .jpeg, .png, .JPG, .JPEG" :on-exceed="handleExceed">
            <img v-if="fileList.length" :src="fileList[0].url" class="avatar" width="100" height="100" />
            <img v-else-if="activeRow.userProfile" :src="activeRow.userProfile" class="avatar" width="100"
              height="100" />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer mt-12px justify-end">
          <el-button @click="closeDialog()">取消</el-button>
          <el-button type="primary" @click="addRoleUser()">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, watch, defineEmits, defineProps, onMounted } from "vue";
import SystemsetController from "@/api/system/index";
import { ElMessage } from "element-plus";
import { mobile } from "@/utils/validate";
const isVisibleWarning = ref(false);
const formRef = ref(null);
const roleList = ref([]);
const handleMenuTreeProps = ref({
  value: "id",
  label: "organName",
  children: "children",
});
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  activeRow: {
    type: Object,
    default: () => { },
  },
  handleMenuArray: {
    type: Object,
    default: () => [],
  },
});
const fileList = ref([])
const handleFileChange = (file, files) => {
  if (files.length) {
    fileList.value = files.map(f => {
      if (!f.url && f.raw) {
        f.url = URL.createObjectURL(f.raw)
      }
      return f
    })
  }
}
const handleExceed = () => {
  fileList.value = []
};
// 表单数据
const addUserForm = ref({
  userState: "",
  groupId: "",
  organId: "",
  userAccount: "",
  password: "",
  repeatPassword: "",
  fullName: "",
  phone: "",
  expireDate: "",
  userRole: 2
});
const basisValid = {
  required: true,
  message: "请输入",
  trigger: "blur",
};
const phoneBasisValid = (rule, value, callback) => {
  if (!mobile(value)) {
    callback(new Error('请填写正确的手机号码'))
  } else callback()
};
const repeatPasswordValid = (rule, value, callback) => {
  if (value == '') {
    callback(new Error('请确认密码'))
  } else if (addUserForm.value.password != addUserForm.value.repeatPassword) {
    callback(new Error('两次密码不一致'))
  } else callback()
};
const rules = ref({
  groupId: [basisValid],
  userAccount: [basisValid],
  password: [basisValid],
  repeatPassword: [{ validator: repeatPasswordValid, trigger: 'blur' }],
  fullName: [basisValid],
  phone: [{ validator: phoneBasisValid, trigger: 'blur' }],
  typeId: [basisValid],
});
const emit = defineEmits(["close"]);
// 关闭弹框
const closeDialog = (type) => {
  emit("close", type);
};
// 添加用户
const addRoleUser = () => {
  let { tenantId } = JSON.parse(
    localStorage.getItem("USER_INFO")
  );
  formRef.value.validate((valid) => {
    if (valid) {
      let formData = new FormData();
      let formInline = JSON.parse(JSON.stringify(addUserForm.value));
      for (let key in formInline) formData.append(key, formInline[key]);      //字段参数
      formData.delete('imageFile');
      formData.append("imageFile", fileList.value[0]?.raw);         //用户更改了头像
      formData.append("tenantId", tenantId);
      SystemsetController.saveOrUpdateUserInfo(formData).then((res) => {
        ElMessage({ message: "操作成功", type: "success" });
        closeDialog('onreset');
      });
    } else {
      ElMessage({ type: "error", showClose: true, message: "表单校验失败" });
      return false;
    }
  });
};
// 角色列表
const findUserGroupListByPage = () => {
  let { tenantId, userId } = JSON.parse(
    localStorage.getItem("USER_INFO")
  );
  SystemsetController.findUserGroupListByPage({
    page: 1,
    size: 9999,
    userId,
    tenantId,
  }).then((res) => {
    roleList.value = res.data.items;
  });
};
// 弹框
watch(
  () => props.visible,
  (newVisible) => {
    isVisibleWarning.value = props.visible;
  }
);
// 编辑数据
watch(
  () => props.activeRow,
  (newVisible) => {
    console.log(props.activeRow, props.activeRow.userRole, '编辑用户', JSON.parse(JSON.stringify(props.activeRow)))
    // addUserForm.value = props.activeRow.userRole ? JSON.parse(JSON.stringify(props.activeRow)) : {
    //   userState: "",
    //   groupId: "",
    //   organId: "",
    //   userAccount: "",
    //   password: "",
    //   repeatPassword: "",
    //   fullName: "",
    //   phone: "",
    //   expireDate: "",
    //   userRole: 2
    // }
    addUserForm.value = props.activeRow.userRole !== undefined
      ? { ...props.activeRow }
      : {
        userState: "",
        groupId: "",
        organId: "",
        userAccount: "",
        password: "",
        repeatPassword: "",
        fullName: "",
        phone: "",
        expireDate: "",
        userRole: 2
      }
    fileList.value = []
    if (props.activeRow.password) addUserForm.value.repeatPassword = props.activeRow.password;
  },
  { deep: true }
);
onMounted(() => {
  findUserGroupListByPage()
})
</script>
<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  border: 1px solid #165398;
  width: 100px;
  height: 100px;
  text-align: center;
  border-radius: 10px;
}
</style>