<template>
  <div class="p-14px pt-0">
    <div class="flex mb-5 jc-end">
      <el-button disabled @click="saveTenantApplyEmpowerInfo()"
        >保存设置</el-button
      >
    </div>
    <div class="flex">
      <div class="w-70 pr-10px">
        <!-- <el-input
          v-model="input3"
          size="small"
          placeholder="请输入关键字"
          :prefix-icon="Search"
          class="mb-3"
        /> -->
        <div
          class="flex-ai-center company-list p-10px pointer"
          :class="activeId == item.id ? 'active' : ''"
          v-for="(item, index) in companyList"
          :key="index"
          @click="findTenantApplyEmpowerInfoById(item.id)"
        >
          <img src="@/assets/image/chongdian_icon.png" alt="" width="20" />
          <span class="ml-2">{{ item.productName }}</span>
        </div>
      </div>
      <div class="flex-1">
        <el-table
          ref="tableRef"
          :data="tableData"
          row-key="id"
          border
          v-loading="loading"
          :tree-props="{ children: 'children' }"
          style="height: 60vh"
        >
          <el-table-column prop="permissionName" label="权限名称" />
          <el-table-column prop="permissionType" label="类型">
            <template #default="scope">{{
              scope.row.permissionType == 1 ? "页面" : "控件"
            }}</template>
          </el-table-column>
          <el-table-column label="操作">
            <template #header>
              <el-checkbox
                disabled
                v-model="checkAll"
                size="small"
                :true-value="1"
                :false-value="2"
                @change="handleSelectAll"
                label="操作"
              />
            </template>
            <template #default="scope">
              <el-checkbox
                disabled
                v-model="scope.row.operate"
                size="small"
                :true-value="1"
                :false-value="2"
                @change="handleSelect(scope.row)"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>
<script setup>
import { Search } from "@element-plus/icons-vue";
import { ref, onMounted, nextTick } from "vue";
import moment from "moment";
import SystemsetController from "@/api/system/index";
import { ElMessage } from "element-plus";
const tableRef = ref();
const loading = ref(false);
const checkAll = ref(false);
const activeId = ref("");
const companyList = ref([]);
const tableData = ref([]);
// 选择
const handleSelect = (row) => {
  const selectChildren = (node) => {
    if (node.children) {
      node.children.forEach((child) => {
        child.operate = child.operate == 1 ? 2 : 1;
        selectChildren(child);
      });
    }
  };
  selectChildren(row);
};
// 选择所有
const handleSelectAll = (type) => {
  const toggleAllChildren = (nodes) => {
    nodes.forEach((node) => {
      node.operate = type;
      if (node.children) {
        toggleAllChildren(node.children);
      }
    });
  };
  toggleAllChildren(tableData.value);
};
// 查询平台
const queryProductList = () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  SystemsetController.queryProductList({
    userId,
    tenantId,
  }).then((res) => {
    companyList.value = res.data;
    if (res.data.length) findTenantApplyEmpowerInfoById(res.data[0]?.id);
  });
};
// 构建树
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
// 查询平台下面的产品
const findTenantApplyEmpowerInfoById = (moduleId) => {
  loading.value = true;
  activeId.value = moduleId;
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  SystemsetController.findTenantApplyEmpowerInfoById({
    timer: moment().format(),
    moduleId,
    userId,
    tenantId,
  }).then((res) => {
    tableData.value = buildTree(res.data);
    checkAll.value = res.data.find((item) => item.operate == 2) ? false : true;
    loading.value = false;
  });
};
// 扁平化
const flattenTree = (tree) => {
  let result = [];
  tree.forEach((item) => {
    const { children, ...reset } = item;
    if (children.length) result = result.concat(flattenTree(children));
    result.push(reset);
  });
  return result;
};
// 修改权限
const saveTenantApplyEmpowerInfo = () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  let tenantApplyVos = flattenTree(tableData.value)
    .filter((item) => item.operate == 1)
    .map((item) => {
      return {
        permissionId: item.id,
        operate: item.operate,
      };
    });
  SystemsetController.saveTenantApplyEmpowerInfo({
    tenantApplyVos,
    moduleId: activeId.value,
    userId,
    tenantId,
  }).then((res) => {
    ElMessage({ message: "操作成功", type: "success" });
  });
};
onMounted(() => {
  queryProductList();
});
</script>
<style lang="scss" scoped>
.company-list {
  color: #fff;
}
.company-list:hover,
.active {
  background: rgba(8, 71, 104, 0.42);
  color: #fff;
}
</style>