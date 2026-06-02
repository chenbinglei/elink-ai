<template>
  <div class="container">
    <el-dialog v-model="dialog_visible" width='50%' :manual-enter-close="false" class="select-users-dialog" @close="handleClose">
      <template #header>
        <TableHeaderTitle title="选择用户"></TableHeaderTitle>
      </template>
      <div class="dialog-main">
        <div class="w-full h-full wrap flex">
          <div class="orgInner">
            <h3>组织架构</h3>
            <HandleMenus ref="handleMenusRef" :handleMenuArray="handleMenuArray" :treeProps="handleMenuTreeProps" customTreeClass="leftArrowClass"
              :id="defaultKey" dataRenewIsFun defaultExpandAll :isSearchInput="false" :isShowHeader="false" class="content_body_left" :expandOnClickNode="false"
              @handleMenuEvent="handleMenuEvent" />
          </div>
          <div class="flex-1 pl-5">
            <div class="h-32px pb-15px pt-15px box-content flex-center-between">

              <el-form :model="form" label-width="auto" :inline="true">
                <el-form-item label="用户名:" prop="fullName">
                  <el-input v-model="form.fullName" placeholder="请输入用户名称"></el-input>
                </el-form-item>
              </el-form>
              <div>
                <el-button :icon="Search" type="primary" @click="queryList">查询</el-button>
                <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
              </div>
            </div>
            <!-- 表格 -->
            <div class="flex-1 overflow-hidden" style="height:32vh">
              <el-table v-loading="listLoading" :data="InvoiceList" @selection-change="handleSelectionChange" class="custom-expand-table"
                @expand-change="handleExpandChange" ref="tableRef" :row-key="row => row.id" :max-height="tableMaxHeight" style="height: 32vh; ">
                <el-table-column type="selection" width="55" reserve-selection></el-table-column>
                <el-table-column label="用户名" prop="fullName" align="center">
                  <template #default="{ row }">
                    <span style="color:#00A6FF;">{{ row.fullName }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="角色" prop="groupName" align="center"></el-table-column>
                <el-table-column label="状态" prop="userState" align="center">
                  <template #default="{ row }">
                    <span v-if="row.userState === 1" style="color:#00A6FF;">正常</span>
                    <span v-else style=" color: #F74A4A">禁用</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div class="tablePagination" ref="tablePaginationRef">
              <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="querySelectUserList" />
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="handleSave">确定</el-button>
        <el-button @click="handleClose">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="setup">
import HandleMenus from "@/components/handleMenu/HandleMenus.vue";
import SystemsetController from "@/api/system/index";
import { ref, watch, reactive, onMounted } from 'vue'
import { ElMessage } from "element-plus";
import { findInspectionUserList, getInspectionUserList, saveInspectionUser } from "@/api/assetManagement/inspection";
const emit = defineEmits(['saveUserDialog', 'saveConfirmUserDialog'])
const props = defineProps({
  isUserVisible: {
    type: Boolean,
    default: false
  },
  nodeType: {
    type: String,
    default: ''
  }
  ,
  selectUserLength: {
    type: Number,
    default: 0
  }
})
// 搜索数据
const form = ref({
  keyword: "",
  organId: "",
  userState: ''
});

// 分页
const currentPage = ref(1)
const pageNum = ref(10)
const totalNumber = ref(0)
const dialog_visible = ref(false)
const hasBeenCalled = ref(false)
// 表格数据
const InvoiceList = ref([])
const nodeTypeList = ref([])
// 左侧组织数据
const handleMenuArray = ref([]);
const defaultKey = ref('')
const handleMenuTreeProps = ref({
  value: "id",
  label: "organName",
  children: "children",
});
const tableRef = ref(null)
const selectUser = ref(null)
// 整理左侧组织数据
function buildTree (items) {
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
// 获取选中的用户名称
const handleSelectionChange = (val) => {
  selectUser.value = val.map(item => item.id)
}
// 点击左侧组织架构
const handleMenuEvent = (item) => {
  form.value.organId = item?.id || form.value.organId
  querySelectUserList()
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
    querySelectUserList()
  });
};
const querySelectUserList = async () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  SystemsetController.findUserListByPage({
    page: currentPage.value,
    size: pageNum.value,
    userId,
    tenantId,
    ...form.value
  }).then((res) => {
    totalNumber.value = res.data.totalSize
    InvoiceList.value = res.data.items
  });
};
const queryInspectionUserList = async () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  getInspectionUserList({ tenantId }).then((res) => {
    const arr = res.data.filter(item => item.type == String(props.nodeType));
    nodeTypeList.value = arr
    const ids = arr.length > 0
      ? JSON.parse(arr[arr.length - 1].userIds)
      : [];
    ids.forEach(id => {
      const row = InvoiceList.value.find(item => item.id === id);
      if (row) {
        tableRef.value.toggleRowSelection(row, true);
      }
    })
  });
}
const handleSave = () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  let obj = {
    userIds: selectUser.value,
    type: props.nodeType,
    tenantId,
  }
  if (nodeTypeList.value[0]?.id) {
    obj.id = nodeTypeList.value[0].id;
  }
  saveInspectionUser(obj).then(res => {
    ElMessage({ type: "success", showClose: true, message: "操作成功！" });
    confirmClose()
  })
};
const confirmClose = () => {
  tableRef.value?.clearSelection()
  dialog_visible.value = false
  emit('saveConfirmUserDialog')
}
const clickResetForm = () => {
  form.value = {
    fullName: ''
  }
  querySelectUserList()
}
// 搜索-前端过滤 但是数据量大的时候失效
const queryList = () => {
  if (!form.value.fullName) {
    querySelectUserList()
    return
  }
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  SystemsetController.findUserListByPage({
    page: 1,
    size: 99,
    userId,
    tenantId,
  }).then((res) => {
    const keyword = form.value.fullName.trim().toLowerCase();
    InvoiceList.value = res.data.items.filter(item =>
      item.fullName.toLowerCase().includes(keyword)
    );
    totalNumber.value = InvoiceList.value.length
  });

}
const handleClose = () => {
  //清除表格选中状态
  tableRef.value?.clearSelection()
  dialog_visible.value = false
  emit('saveUserDialog')
}
onMounted(() => {
  findOrganStructureListByTenantId();
});

watch(() => props.isUserVisible, (newVal) => {
  dialog_visible.value = newVal
  if (newVal) {
    setTimeout(() => {
      queryInspectionUserList()
    }, 500)
  }
})

</script>

<style lang="scss" scoped>
.dialog-main {
  padding: 20px;
  display: flex;
}
</style>