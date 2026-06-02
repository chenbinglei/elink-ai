<template>
  <div style="box-sizing: border-box;height: 100%;overflow: hidden;">
    <div class="w-full h-full wrap flex">
      <div class="orgInner">
        <h3>组织架构</h3>
        <HandleMenus
          ref="handleMenusRef"
          :handleMenuArray="handleMenuArray"
          :treeProps="handleMenuTreeProps"
          customTreeClass="leftArrowClass"
          :id="defaultKey"
          dataRenewIsFun
          defaultExpandAll
          :isSearchInput="false"
          :isShowHeader="false"
          class="content_body_left"
          :expandOnClickNode="false"
          @handleMenuEvent="handleMenuEvent"
        />
      </div>
      <div class="flex-1 pl-5">
        <div class="h-32px pb-15px pt-15px box-content flex-center-between">

          <el-form :model="form" label-width="auto" :inline="true">
            <el-form-item style="margin-bottom: 0">
              <el-input v-model="form.keyword" placeholder="输入关键字查询" clearable/>
            </el-form-item>
            <el-form-item style="margin-bottom: 0">
              <el-select
                v-model="form.userRole"
                placeholder="请选择角色"
                style="width: 200px"
                clearable
              >
                <el-option label="管理员" :value="1" />
                <el-option label="普通用户" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item style="margin-bottom: 0">
              <el-select
                v-model="form.userState"
                placeholder="请选择状态"
                style="width: 200px"
                clearable
              >
                <el-option label="开启" :value="1" />
                <el-option label="关闭" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item style="margin-bottom: 0;margin-right: 10px">
              <el-select
                v-model="form.expireState"
                placeholder="到期日"
                style="width: 160px"
                clearable
              >
                <el-option label="生效中" :value="1" />
                <el-option label="已过期" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item style="margin-bottom: 0">
              <el-button :icon="Search" @click="handleMenuEvent()"
                >查询</el-button
              >
            </el-form-item>
          </el-form>
          <div>
            <el-button @click="isVisible = true" :icon="CirclePlus"
              >新增用户</el-button
            >
            <el-button @click="deleteUserInfoById(1)" :icon="Delete"
              >批量删除</el-button
            >
          </div>
        </div>
        <!-- 表格 -->
        <div class="flex-1 overflow-hidden" style="height: calc(100vh - 200px)">
          <SearchTable
            ref="tableRef"
            :fetch-table-data="onSearch"
            :export-config="exportConfig"
            :columns="columns"
            @operationBtn="operationBtn"
            :selection="true"
            @selectionChange="(val) => (selectionList = val)"
            pagination-way="back"
          >
          </SearchTable>
        </div>
      </div>
    </div>
    <!-- 新增 编辑-->
    <addUser
      :visible="isVisible"
      @close="closeDialog"
      :active-row="activeRow"
      :handleMenuArray="handleMenuArray"
    ></addUser>
  </div>
</template>
<script setup>
import { CirclePlus, Search, Delete } from "@element-plus/icons-vue";
import SearchTable from "@/components/table/index.vue";
import useTable from "./useTable";
import { onMounted, ref, watch } from "vue";
import addUser from "./components/addUser.vue";
import SystemsetController from "@/api/system/index";
import HandleMenus from "@/components/handleMenu/HandleMenus.vue";
import { ElMessage } from "element-plus";
import { ElMessageBox } from 'element-plus'
// 搜索数据
const form = ref({
  keyword: "",
  organId: "",
  userState: ''
});
const defaultKey = ref('')
const {
  tableRef,
  columns,
  exportConfig,
  tiggerSearch,
  tiggerExport,
  onSearch,
  onReset,
  clearData
} = useTable(form.value);
// 左侧组织数据
const handleMenuArray = ref([]);
const handleMenuTreeProps = ref({
  value: "id",
  label: "organName",
  children: "children",
});
const selectionList = ref([]); //选中数据
const isVisible = ref(false);  // 弹窗
const activeRow = ref({});  // 选中数据
// 清空数据
watch(isVisible, (val) => {
  if (!val) {
    activeRow.value = {};
  }
});
// 整理左侧组织数据
function buildTree(items) {
  // 创建一个哈希表来存储所有节点
  const itemMap = {};
  items.forEach((item) => {
    itemMap[item.id] = { ...item, children: [] };
  });
  // 构建树结构
  const tree = [];
  items.forEach((item) => {
    if (item.parentId) {
      // 如果有父节点，将当前节点添加到父节点的children中
      if (itemMap[item.parentId]) {
        itemMap[item.parentId].children.push(itemMap[item.id]);
      }
    } else {
      // 如果没有父节点，直接添加到树的根层级
      tree.push(itemMap[item.id]);
    }
  });
  return tree;
}
// 点击左侧组织架构
const handleMenuEvent = (item) => {
  form.value.organId = item?.id || form.value.organId
  tiggerSearch()
};
// 组织架构
const findOrganStructureListByTenantId = () => {
  let { tenantId, userId } = JSON.parse(
    localStorage.getItem("USER_INFO")
  );
  SystemsetController.findOrganStructureListByTenantId({
    userId,
    tenantId,
  }).then((res) => {
    defaultKey.value = res.data.find(item => !item.parentId)?.id
    form.value.organId = defaultKey.value
    handleMenuArray.value = buildTree(res.data);
  });
};
// 删除角色
const deleteUserInfoById = (all) => {
  if (all && selectionList.value.length == 0)
    return ElMessage({ message: "请选择要删除的用户", type: "warning" });
  else {
    ElMessageBox.confirm('确定删除当前用户？')
    .then(() => {
        SystemsetController.deleteUserInfoById({
          id: all
            ? selectionList.value.map((item) => item.id).join(",")
            : activeRow.value.id,
        }).then((res) => {
          ElMessage({ message: "操作成功", type: "success" });
          activeRow.value = {};
          onReset();
        });
    })
    .catch(() => {
    })
  }
};
// 关闭弹框
const closeDialog = (type) => { 
  isVisible.value = false
  if(type) onReset()
};
// 操作项
const operationBtn = ({ row, type }) => {
  activeRow.value = row;
  if (type == "edit") {
    isVisible.value = true;
  } else if (type == "delete") {
    deleteUserInfoById();
  } else {
    isVisiblePermissionCon.value = true;
  }
};
onMounted(() => {
  findOrganStructureListByTenantId();
});
</script>
<style lang="scss" scoped>
.wrap {
  box-sizing: border-box;
  background: url("@/assets/image/station-details/sta-detail-bg.png") no-repeat;
  background-size: 100% 100%;
  width: 100%;
  // height: calc(100% - 166px);
  height: 100%;
  padding: 0 24px;
}
.orgInner {
  width: 260px;
  color: #fff;
  flex-shrink: 0;
  border-right: 1px solid rgba(189, 202, 212, 0.2);
}
.leftArrowClass {
  border: none !important;
}
</style>