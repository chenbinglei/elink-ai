<template>
  <div class="w-full h-full">
    <!-- 弹框 查看-->
    <el-dialog
      v-model="isVisibleWarning"
      width="40%"
      align-center
      @close="closeDialog"
    >
      <template #header>
        <el-icon color="#fff" size="16"><CirclePlus /></el-icon
        ><span style="color: #fff" class="ml-1">{{
          activeRow.type === "editOrgan" ? "编辑组织" : "新增组织"
        }}</span>
      </template>
      <el-form
        :model="addUserForm"
        label-width="auto"
        class="p-15px"
        :rules="rules"
        ref="formRef"
      >
        <el-form-item
          label="父级组织"
          prop="parentId"
        >
          <el-select v-model="addUserForm.parentId" placeholder="请选择父级组织" :disabled="activeRow.type == 'addOrgan' ? false : (activeRow.parentId ? false : true) ">
            <el-option
              v-for="item in handleMenuArrayFlat"
              :key="item.id"
              :label="item.organName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="组织名称:"  prop="organName">
          <el-input
            v-model="addUserForm.organName"
            placeholder="请输入组织名称"
            type="text"
            max="32"
          />
        </el-form-item>
        <el-form-item label="排序:"  prop="sortNumber">
          <el-input
            v-model="addUserForm.sortNumber"
            placeholder="请输入排序"
            type="number"
          />
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
import { ref, watch, defineEmits, defineProps, computed } from "vue";
import SystemsetController from "@/api/system/index";
import { ElMessage } from "element-plus";
const isVisibleWarning = ref(false);
const formRef = ref(null);
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  activeRow: {
    type: Object,
    default: () => [],
  },
  handleMenuArray: {
    type: Object,
    default: () => [],
  },
});
// 扁平数组
const flattenTree = (tree = []) => {
  let result = [];
  tree.forEach(item => { 
    const { childrenList,...reset } = item;
    if(childrenList && childrenList.length) result = result.concat(flattenTree(childrenList));
    result.push(reset);
  });
  return result
}
// 表单
const addUserForm = ref({
  sortNumber: "",
  parentId: "",
  organName: ""
});
// 校验规则
const basisValid = {
  required: true,
  message: "请输入",
  trigger: "blur",
};
const validateParentId = (rule, value, callback) => {
  if (props.activeRow?.type == 'addOrgan' && value == '') {
    callback(new Error('请选择父级组织'))
  } else if((props.activeRow?.type == 'editOrgan' && value == '' && props.activeRow.parentId)) {
    callback(new Error('请选择父级组织'))
  }else callback()
}
const rules = ref({
  sortNumber: [basisValid],
  parentId: [{validator: validateParentId, trigger: 'change'}],
  organName: [basisValid]
});
const emit = defineEmits(["close"]);
// 关闭弹框
const closeDialog = (type) => {
  addUserForm.value = {
    sortNumber: "",
    parentId: "",
    organName: ""
  }
  emit("close",type);
};
// 添加组织
const addRoleUser = () => {
  let { tenantId, userId } = JSON.parse(
    localStorage.getItem("USER_INFO")
  );
  formRef.value.validate((valid) => {
    if (valid) {
      let obj = Object.assign({}, addUserForm.value, { tenantId, userId });
      SystemsetController.saveOrUpdateOrganStructure(obj).then((res) => {
        ElMessage({ message: "操作成功", type: "success" });
        closeDialog('onreset');
      });
    } else {
      ElMessage({ type: "error", showClose: true, message: "表单校验失败" });
      return false;
    }
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
    if(props.activeRow.type == 'addOrgan'){
      addUserForm.value.parentId = props.activeRow.parentId
    }else addUserForm.value = props.activeRow;
  },
  { deep: true }
);
// 树形结构平铺
const handleMenuArrayFlat = computed(() => { 
  return props.activeRow?.type == 'addOrgan' ? flattenTree(props.handleMenuArray) : flattenTree(props.handleMenuArray).filter(item => item.id != props.activeRow.id)
});
</script>