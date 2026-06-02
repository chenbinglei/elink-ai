<template>
  <div class="w-full h-full">
    <!-- 弹框 查看-->
    <el-dialog
      v-model="isVisibleWarning"
      width="40%"
      align-center
      @close="closeDialog"
      title="管理人员"
    >
      <div class="dialog-warning-content p-24px flex-jc-ai-center">
        <el-transfer
          v-model="selectValue"
          filterable
          :props="{ key: 'id', label: 'fullName' }"
          :titles="['未选', '已选']"
          :data="personList"
        />
      </div>
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
import { ref, watch, defineEmits, defineProps } from "vue";
import SystemsetController from "@/api/system/index";
import { ElMessage } from "element-plus";
const isVisibleWarning = ref(false);
const selectValue = ref([]);
const personList = ref([]);
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  selectPerson: {
    type: Object,
    default: () => [],
  },
});
const emit = defineEmits(["closeDialog"]);
// 关闭弹框
const closeDialog = (data) => {
  emit("closeDialog",data);
  isVisibleWarning.value = false
};
// 添加成员
const addRoleUser = () => {
  if (selectValue.value?.length) {
    SystemsetController.updateUserByGroupId({
      userIds: selectValue?.value.join(","),
      groupId: props.selectPerson?.id,
    }).then((res) => {
      ElMessage({ message: "操作成功", type: "success" });
      closeDialog(true);
    });
  } else ElMessage({ message: "请先选择人员，再添加", type: "warning" });
};
// 查询可添加人员
const findUserListByTenantId = () => {
  let { tenantId } = JSON.parse(localStorage.getItem("USER_INFO"));
  SystemsetController.findUserListByTenantId({
    tenantId,
  }).then((res) => {
    personList.value = res.data;
  });
};
// 弹框
watch(
  () => props.visible,
  (newVisible) => {
    isVisibleWarning.value = newVisible;
    findUserListByTenantId();
  }
);
// 列表人员
watch(
  () => props.selectPerson,
  (newVisible) => {
    selectValue.value = props.selectPerson?.userDtoList?.map((item) => item.id);
  },
  { deep: true }
);
</script>