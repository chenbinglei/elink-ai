<template>
  <div class="w-full h-full">
    <!-- 弹框 查看-->
    <el-dialog
      v-model="isVisibleWarning"
      width="50%"
      align-center
      @close="closeDialog"
      title="权限配置"
    >
      <div class="flex p-14px">
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
            @click="findGroupApplyEmpowerInfoById(item.id)"
          >
            <img src="@/assets/image/chongdian_icon.png" alt="" width="20" />
            <span class="ml-2">{{ item.productName }}</span>
          </div>
        </div>
        <el-table
          ref="tableRef"
          :data="tableData"
          row-key="id"
          border
          v-loading="loading"
          style="height: 32vh"
          :tree-props="{ children: 'children' }"
        >
          <el-table-column prop="permissionName" label="权限名称" />
          <el-table-column prop="permissionType" label="类型">
            <template #default="scope">{{
              scope.row.permissionType == 1 ? "页面" : "控件"
            }}</template>
          </el-table-column>
          <el-table-column  label="操作">
            <template #header>
              <el-checkbox v-model="checkAll" size="small" :true-label="1" :false-label="2" @change="handleSelectAll" label="操作"/>
            </template>
            <template #default="scope"> 
              <el-checkbox v-model="scope.row.operate" size="small" :true-label="1" :false-label="2" @change="handleSelect(scope.row)"/>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <div class="dialog-footer mt-12px justify-end">
          <el-button @click="closeDialog()">取消</el-button>
          <el-button type="primary" @click="saveGroupApplyEmpowerInfo()">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { Search } from "@element-plus/icons-vue";
import { ref, watch, defineEmits, defineProps, nextTick } from "vue";
import moment from "moment";
import { ElMessage } from "element-plus";
import SystemsetController from "@/api/system/index";
const isVisibleWarning = ref(false);
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  groupId: {
    type: String,
    default: ''
  }
});
const tableRef = ref();
const loading = ref(false);
const activeId = ref("");
const emit = defineEmits(["close"]);
const companyList = ref([]);
const tableData = ref([]);
watch(
  () => props.visible,
  (newVisible) => {
    isVisibleWarning.value = newVisible;
    if (newVisible) queryProductList();
  }
);
// 选择
const handleSelect = (row) => {
  const selectChildren = (node) => {
    if (node.children) {
      node.children.forEach((child) => {
        child.operate = child.operate == 1 ? 2 : 1
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
      node.operate = type
      if (node.children) {
        toggleAllChildren(node.children);
      }
    });
  };
  toggleAllChildren(tableData.value);
};
// 关闭对话框
const closeDialog = () => {
  isVisibleWarning.value = false;
  emit("close");
};
// 查询平台
const queryProductList = () => {
  let { tenantId, userId } = JSON.parse(
    localStorage.getItem("USER_INFO")
  );
  SystemsetController.queryProductList({
    userId,
    tenantId,
  }).then((res) => {
    companyList.value = res.data;
    if (res.data.length) findGroupApplyEmpowerInfoById(res.data[0]?.id);
  });
};
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
const flattenTree = (tree) => {
  let result = [];
  tree.forEach(item => { 
    const { children,...reset } = item;
    if(children.length) result = result.concat(flattenTree(children));
    result.push(reset);
  });
  return result
}
// 查询平台下面的产品  
const findGroupApplyEmpowerInfoById = (moduleId) => {
  loading.value = true;
  activeId.value = moduleId;
  let { tenantId, userId } = JSON.parse(
    localStorage.getItem("USER_INFO")
  );
  SystemsetController.findGroupApplyEmpowerInfoById({
    timer: moment().format(),
    groupId: props.groupId,
    moduleId,
    userId,
    tenantId,
  }).then((res) => {
    tableData.value = buildTree(res.data);
    nextTick(() => {
      loading.value = false;
    });
  });
};
// 修改权限
const saveGroupApplyEmpowerInfo = () => { 
  let groupApplyVos = flattenTree(tableData.value).filter(item => item.operate == 1).map(item => {
    return {
      permissionId: item.id,
      operate: item.operate,
    }
  })
  let { tenantId, userId } = JSON.parse(
    localStorage.getItem("USER_INFO")
  );
  SystemsetController.saveGroupApplyEmpowerInfo({
    groupApplyVos,
    groupId: props.groupId,
    moduleId: activeId.value,
    userId,
    tenantId,
  }).then((res) => {
    ElMessage({ message: "操作成功", type: "success" });
    closeDialog()
  });
};
</script>
<style lang="scss" scoped>
.company-list:hover,
.active {
  background: rgba(8, 71, 104, 0.42);
  color: #fff;
}
</style>