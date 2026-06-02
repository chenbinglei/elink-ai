<template>
  <el-dialog v-model="dialog_visible" width='50%' :manual-enter-close="false" class="select-users-dialog"
    @close="handleClose">
    <template #header>
      <TableHeaderTitle title="选择用户"></TableHeaderTitle>
    </template>
    <div class="dialog-main">
      <div class="select-users-left">
        <HandleMenus ref="handleMenusRef" :handleMenuArray="handleMenuArray" :treeProps="handleMenuTreeProps"
          :id="defaultKey" dataRenewIsFun defaultExpandAll :isSearchInput="false" :isShowHeader="false"
          class="content_body_left" :expandOnClickNode="false" @handleMenuEvent="handleMenuEvent" />
      </div>
      <div class="select-users-right">
        <el-form :model="form">
          <el-row :gutter="16">
            <el-col :md="12" :sm="12" :xl="12" :xs="12">
              <el-form-item label="用户名:" prop="fullName">
                <el-input v-model="form.fullName" placeholder="请输入用户名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :md="12" :sm="12" :xl="12" :xs="12">
              <el-form-item>
                <el-button :icon="Search" type="primary" @click="queryList">查询</el-button>
                <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
              </el-form-item>
            </el-col>
          </el-row>

        </el-form>
        <div class="tableCenter" ref="tableCenterRef">
          <el-table v-loading="listLoading" :data="InvoiceList" @selection-change="handleSelectionChange"
            class="custom-expand-table" @expand-change="handleExpandChange" ref="tableRef" :max-height="tableMaxHeight"
            style="height: 32vh; " :row-key="row => row.id">
            <el-table-column type="selection" width="55" reserve-selection></el-table-column>
            <el-table-column label="用户名" prop="fullName" align="center">
              <template #default="{ row }">
                <span style="color:#00A6FF;">{{ row.fullName }}</span>
              </template>
            </el-table-column>
            <el-table-column label="角色" prop="groupName" align="center"></el-table-column>
            <el-table-column label="状态" prop="typeStuts" align="center">
              <template #default="{ row }">
                <span v-if="row.typeStuts === '0'" style="color:#00A6FF;">正常</span>
                <span v-else style=" color: #F74A4A">禁用</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination" ref="tablePaginationRef">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage"
            @pageChange="querySelectUserList" />
        </div>

      </div>
    </div>
    <template #footer>
      <el-button type="primary" @click="handleSave">确定</el-button>
      <el-button @click="handleClose">取消</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, reactive } from 'vue'
import SystemsetController from "@/api/system/index";
import HandleMenus from "@/components/handleMenu/HandleMenus.vue";
import { ElMessage } from "element-plus";
import { findInspectionUserList, getInspectionUserList, saveInspectionUser } from "@/api/assetManagement/inspection";

const props = defineProps({
  isUserVisible: {
    type: Boolean,
    default: false
  },
  nodeType: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['saveUserDialog'])
// 左侧组织数据
const handleMenuArray = ref([]);
const handleMenuTreeProps = ref({
  value: "id",
  label: "organName",
  children: "children",
});
const defaultKey = ref('')
const selectUser = ref(null)
const form = ref({
  fullName: '',
})
const InvoiceList = ref([])
const nodeTypeList = ref([])
// 分页
const currentPage = ref(1)
const pageNum = ref(10)
const totalNumber = ref(0)
const tableRef = ref(null)
// 点击左侧组织架构
const handleMenuEvent = (item) => {
  // form.value.organId = item?.id || form.value.organId
  querySelectUserList()
  // tiggerSearch()
};
const querySelectUserList = async () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  getInspectionUserList({ tenantId }).then((res) => {
    nodeTypeList.value = res.data.filter(item => item.type == String(props.nodeType));
  });
  SystemsetController.findUserListByPage({
    page: currentPage.value,
    size: pageNum.value,
    userId,
    tenantId,
    ...form.value
  }).then((res) => {
    const list = res.data.items
    totalNumber.value = res.data.totalSize
    // const ids = JSON.parse(nodeTypeList.value?.userIds || '[]');
    const ids = nodeTypeList.value.length > 0
      ? JSON.parse(nodeTypeList.value[0].userIds)
      : [];

    InvoiceList.value = list.map(item => {
      const match = ids.find(n => n === item.id);
      return {
        ...item,
        typeStuts: match ? '0' : '--'
      };
    });
    ids.forEach(id => {
      console.log('row', id);
      const row = InvoiceList.value.find(item => item.id === id);
      if (row) {
        tableRef.value.toggleRowSelection(row, true);
      }
    });
  });
};
// 获取选中的用户名称
const handleSelectionChange = (val) => {
  selectUser.value = val.map(item => item.id)
}
const handleSave = () => {
  // if (selectUser.value.length === 0) return message.error('请选择用户')
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  // if (nodeTypeList.value.id)
  let obj = {
    userIds: selectUser.value,
    type: props.nodeType,
    tenantId,
  }
  if (nodeTypeList.value[0]?.id) {
    obj.id = nodeTypeList.value[0].id;
  }
  console.log(nodeTypeList.value,obj,'obj');
  saveInspectionUser(obj).then(res => {
    ElMessage({ type: "success", showClose: true, message: "操作成功！" });
    handleClose()

  })
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
    console.log(res.data);
    defaultKey.value = res.data.find(item => !item.parentId)?.id
    form.value.organId = defaultKey.value
    handleMenuArray.value = buildTree(res.data);
  });
};
// 整理左侧组织数据
function buildTree (items) {
  // 创建一个哈希表来存储所有节点
  const itemMap = {};
  items.forEach((item) => {
    // 根据是否有父节点设置不同的图标
    const icon = item.parentId ? 'iconfont icon-kehu' : 'iconfont icon-gongsi';

    itemMap[item.id] = {
      ...item,
      children: [],
      iconName: icon // 添加图标属性

    };
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

// 修复：确保 dialog_visible 响应 props 的变化
const dialog_visible = ref(props.isUserVisible)

// 修复：监听 isUserVisible 的变化
watch(() => props.isUserVisible, (newVal) => {
  dialog_visible.value = newVal
  if (newVal) {
    findOrganStructureListByTenantId()
  }
})

// 修复：监听 dialog_visible 的变化并通知父组件
watch(dialog_visible, (newVal) => {
  if (!newVal) {
    emit('saveUserDialog')
  }
})

const handleClose = () => {
  //清除表格选中状态
  tableRef.value?.clearSelection()
  dialog_visible.value = false

}
</script>

<style lang="scss" scoped>
.select-users-dialog {
  .dialog-main {
    padding: 20px;
    display: flex;

    .select-users-left {
      width: 30%;
      padding: 10px;
      border: 1px solid rgba(255, 255, 255, 0.15);
    }

    .select-users-right {
      flex: 1;
      padding: 10px;
    }


  }
}

.handleMenu {
  border-right: none;
}
</style>