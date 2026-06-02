<template>
  <div class="w-full h-full wrap">
    <div class="h-32px pb-15px pt-15px box-content flex-center-between">

      <el-form :model="searchForm" label-width="auto" :inline="true">
        <el-form-item style="margin-bottom: 0">
          <el-input v-model="searchForm.groupName" placeholder="输入关键字查询" clearable />
        </el-form-item>
        <el-form-item style="margin-bottom: 0">
          <el-button :icon="Search" @click="tiggerSearch()">查询</el-button>
        </el-form-item>
      </el-form>
      <div>
        <el-button @click="isVisible = true" :icon="CirclePlus">新增角色</el-button>
        <el-button @click="deleteRole(1)" :icon="Delete">批量删除</el-button>
      </div>
    </div>
    <!-- 表格 -->
    <div class="flex-1 overflow-hidden" style="height: calc(100% - 70px)">


      <SearchTable ref="tableRef" :fetch-table-data="onSearch" :export-config="exportConfig" :columns="columns"
        @operationBtn="operationBtn" :selection="true" @selectionChange="(val) => (selectionList = val)"
        pagination-way="back">
      </SearchTable>
    </div>
    <!-- 弹框 新增&编辑-->
    <el-dialog v-model="isVisible" width="30%" align-center>
      <template #header>
        <el-icon color="#fff" size="16">
          <CirclePlus />
        </el-icon><span style="color: #fff" class="ml-1">{{
          activeRow.id ? "编辑角色" : "新增角色"
        }}</span>
      </template>
      <el-form :model="form" label-width="auto" class="p-15px">
        <el-form-item label="角色名称:" required>
          <el-input v-model="form.groupName" placeholder="输入角色名称" />
        </el-form-item>
        <el-form-item label="描述:" style="margin-bottom: 0">
          <el-input v-model="form.refer" placeholder="输入角色描述" type="textarea" :rows="3" maxlength="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer mt-12px justify-end">
          <el-button @click="isVisible = false">取消</el-button>
          <el-button type="primary" @click="addRole()">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 弹框 权限配置-->
    <permissionCon v-show="isVisiblePermissionCon" :visible="isVisiblePermissionCon"
      @close="isVisiblePermissionCon = false" :groupId="activeRow.id"></permissionCon>

    <managementRole v-show="isVisibleManagementRole" :visible="isVisibleManagementRole" :selectPerson="activeRow"
      @closeDialog="closeDialog"></managementRole>
    <!-- <permissionCon
      :visible="isVisiblePermissionCon"
      @close="isVisiblePermissionCon = false"
      :groupId="activeRow.id"
    ></permissionCon> -->
    <!-- 弹框 管理人员-->
    <!-- <managementRole
      :visible="isVisibleManagementRole"
      :selectPerson="activeRow"
      @closeDialog="closeDialog"
    ></managementRole> -->
    <!-- 提示弹框 -->
    <!-- <Dialog v-model="isDeleteVisible" @cancel="isDeleteVisible = false" @confirm="deleteRole()" width="20%" title="提示">
      <template #content>是否要删除当前角色？</template>
    </Dialog> -->
  </div>
</template>
<script setup>
import Dialog from "@/components/Dialog/index.vue";
import { CirclePlus, Search, Delete } from "@element-plus/icons-vue";
import SearchTable from "@/components/table/index.vue";
import useSearchBar from "./useSearchBar";
import useTable from "./useTable";
import { ref, watch } from "vue";
import managementRole from "./components/managementRole.vue";
import permissionCon from "./components/permissionCon.vue";
import SystemsetController from "@/api/system/index";
import { ElMessage } from "element-plus";
import { ElMessageBox } from "element-plus";
const form = ref({ groupName: "", refer: "" });
const searchForm = ref({ groupName: "" });
const {
  tableRef,
  columns,
  exportConfig,
  tiggerSearch,
  tiggerExport,
  onSearch,
  onReset,
} = useTable(searchForm.value);
const selectionList = ref([]);
const isVisible = ref(false);
const isVisibleManagementRole = ref(false);
const isVisiblePermissionCon = ref(false);
const isDeleteVisible = ref(false);
const activeRow = ref({});
// 添加 编辑 角色
const addRole = () => {
  let { tenantId } = JSON.parse(localStorage.getItem("USER_INFO"));
  let obj = { ...form.value, tenantId, id: activeRow.value.id };
  SystemsetController.saveOrUpdateUserGroup(obj).then((res) => {
    isVisible.value = false;
    ElMessage({ message: "操作成功", type: "success" });
    onReset();
  });
};
// 清空数据
watch(isVisible, (val) => {
  if (!val) {
    activeRow.value = {};
    form.value = { groupName: "", refer: "" };
  }
});
// 删除角色
const deleteRole = (all) => {
  if (all && selectionList.value.length == 0)
    return ElMessage({ message: "请选择要删除的角色", type: "warning" });
  else {
    ElMessageBox.confirm("确定删除当前角色？")
      .then(() => {
        SystemsetController.deleteUserGroupById({
          groupId: all
            ? selectionList.value.map((item) => item.id).join(",")
            : activeRow.value.id,
        }).then((res) => {
          ElMessage({ message: "操作成功", type: "success" });
          activeRow.value = {};
          onReset();
        });
      })
      .catch(() => { });
  }
};
// 关闭修改人员页面
const closeDialog = (reset) => {
  isVisibleManagementRole.value = false;
  if (reset) onReset();
};
// 操作项
const operationBtn = ({ row, type }) => {
  activeRow.value = row;
  if (type == "edit") {
    isVisible.value = true;
    Object.assign(form.value, row);
  } else if (type == "delete") {
    // isDeleteVisible.value = true
    deleteRole();
  } else if (type == "managementRole") {
    isVisibleManagementRole.value = true;
  } else {
    isVisiblePermissionCon.value = true;
  }
};
</script>
<style lang="scss" scoped>
.wrap {
  box-sizing: border-box;
  background: url("@/assets/image/station-details/sta-detail-bg.png") no-repeat;
  background-size: 100% 100%;
  width: 100%;
  // height: auto;
  padding: 0 24px;
  // display: flex;
  // flex-direction: column;
}
</style>