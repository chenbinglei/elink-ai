<template>
  <div class="app-container">
    <!-- 巡检项配置 -->
    <StationMenuListCom v-model:activeSiteId="activeSiteId" v-model:activeSiteName="activeSiteName">
    </StationMenuListCom>

    <null-data v-if="!activeSiteId" words="请先选择站点"></null-data>
    <div v-else class="app-container-right">
      <div class="header-form content_border">
        <el-form :model="activeDeviceInfo" inline>
          <el-row :gutter="16">
            <el-col :md="8" :sm="12" :xl="6" :xs="24">
              <el-form-item label="巡检项名称：">
                <el-input v-model="activeDeviceInfo.name" placeholder="请输入巡检项名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :md="16" :sm="12" :xl="18" :xs="24">
              <el-form-item>
                <el-button :icon="Search" type="primary" @click="queryList">查询</el-button>
                <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="tableContent content_border" ref="tableContentRef">
        <TableHeaderTitle title="巡检项配置">
          <template #content>
            <div class="table_top_content">
              <el-button @click="clickOperateBut(1)">
                <span>新增</span>
              </el-button>

              <el-button @click="clickOperateBut(3)">

                <span>模版下载</span>
              </el-button>
              <el-button @click="clickOperateBut(4)">
                <span>删除</span>
              </el-button>
              <el-button @click="clickOperateBut(5)">
                <span>导入</span>
              </el-button>
              <el-button @click="clickOperateBut(6)">
                <span>导出</span>
              </el-button>
            </div>
          </template>
        </TableHeaderTitle>
        <!-- <div class="flex-ai-center jc-end header-form-right">
          <el-button @click="clickOperateBut(1)">
            <span>新增</span>
          </el-button>
          <el-button @click="clickOperateBut(2)">
            <span>修改</span>
          </el-button>
          <el-button @click="clickOperateBut(3)">

            <span>模版下载</span>
          </el-button>
          <el-button @click="clickOperateBut(4)">
            <span>删除</span>
          </el-button>
          <el-button @click="clickOperateBut(5)">
            <span>导入</span>
          </el-button>
          <el-button @click="clickOperateBut(6)">
            <span>导出</span>
          </el-button>
        </div> -->
        <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
          <el-table v-loading="listLoading" :data="ConfigurationList" :max-height="tableMaxHeight"
            @selection-change="handleSelectionChange" ref="tableRef">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column prop="iconPath" label="图标" align="center" show-overflow-tooltip>

              <template #default="scope">
                <div style="display: flex;justify-content: center;align-items: center;">
                  <img v-if="!scope.row.iconPath" :src="configImg" alt="" style="width: 25px; height:auto; object-fit: cover;" />
                  <img v-else :src="scope.row.iconPath" alt="图标" style="width: 25px; height:auto; object-fit: cover;" />
                </div>
              </template>

            </el-table-column>
            <el-table-column prop="name" label="巡检项名称" align="center" show-overflow-tooltip />
            <el-table-column prop="description" label="巡检内容描述" align="center" show-overflow-tooltip>
              <template #default="scope">
                <span>{{ scope.row?.description || "--" }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" align="center" show-overflow-tooltip />
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button @click="editOperateBut(scope.row)">编辑</el-button>
              </template>

            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination" ref="tablePaginationRef">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage"
            @pageChange="queryList" />
        </div>
      </div>
    </div>
    <el-dialog v-model="dialogFormVisible" :title="title" width="30%" @close="dialogcopyVisible">
      <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="30%" style="margin-top: 20px;">
        <el-form-item label="巡检项图标：" prop="code">
          <UploadPicturesCom ref="uploadPicturesComRef" :uploadNum="1" v-model:fileArray="fileArray">
            <template #tip_content>
              <div class="alter_text">支持PNG/JPG文件，大小不超过2M</div>
            </template>
          </UploadPicturesCom>
        </el-form-item>
        <el-form-item label="巡检项名称：" prop="name">
          <el-input style="max-width: 300px" v-model="formDialog.name" placeholder="请输入巡检项名称" type="text" max="32" />
        </el-form-item>
        <el-form-item label="巡检内容描述：" prop="description">
          <el-input style="max-width: 300px" type="textarea" v-model="formDialog.description" placeholder="请输入巡检内容描述"
            max="300" maxlength="200" show-word-limit />

        </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer mt-12px justify-end">
          <el-button @click="dialogcopyVisible()">取消</el-button>
          <el-button type="primary" @click="clickConfirmBut()">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <ImportConfigDialog v-if="importComponentLibraryVisible" v-model:isVisible="importComponentLibraryVisible"
      :activeSiteId="activeSiteId" @changeEvent="queryList()" />
  </div>
</template>

<script setup>

import StationMenuListCom from "./components/station.vue";
import { queryInspectionItemList, saveInspectionItem, deleteAllInspectionItemByIds } from "@/api/assetManagement/inspection";
import { exportCustomExcel } from "@/common/exportExcel";
import { ref, watch, onMounted } from "vue";
import UploadPicturesCom from "@/components/uploadFileCom/UploadPicturesCom.vue";
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import ImportConfigDialog from "./components/ImportConfigDialog.vue";
import configImg from "@/assets/images/config.png";

const activeSiteId = ref("");
const activeSiteName = ref("");
const activeDeviceInfo = ref({
  name: "",
});
const ConfigurationList = ref([]);
const exportTableHeader = ref([
  { key: "name", name: "巡检项名称" },
  { key: "description", name: "巡检内容描述" },
  { key: "createTime", name: "创建时间" },

]);
watch(activeSiteId, (newValue) => {
  if (newValue) {
    queryList();
  }
});
const listLoading = ref(false);
const selectList = ref([]);
// 分页
const currentPage = ref(1)
const pageNum = ref(10)
const totalNumber = ref(0)
// 计算出表格最大高度
const tableContentRef = ref(null);
const tablePaginationRef = ref(null);
const tableMaxHeight = ref(300)
// 导入弹窗
const importComponentLibraryVisible = ref(false);


// 新增、修改弹窗标题
const fileArray = ref([]);

const title = ref("");
const uploadPicturesComRef = ref(null);
const dialogFormVisible = ref(false);
const formDialog = ref({
  name: '',
  description: '', // 初始化为 ''
  code: '' // 如果有 code 字段
});
const rules = ref({
  name: [
    { required: true, message: "请输入巡检项名称", trigger: "blur" },
    { max: 32, message: "请输入32个字符以内的名称", trigger: "blur" },
  ],
})
const setTableMaxHeight = () => {
  let tableContentHeight = tableContentRef.value.offsetHeight - 78;
  tableMaxHeight.value = tableContentHeight - tablePaginationRef.value.offsetHeight;
};

const handleSelectionChange = (val) => {
  selectList.value = val;
};
const queryList = () => {
  listLoading.value = true;
  let obj = {
    ...activeDeviceInfo.value,
    page: currentPage.value,
    size: pageNum.value,
    siteId: activeSiteId.value,
  };
  queryInspectionItemList(obj).then(res => {
    listLoading.value = false;
    ConfigurationList.value = res.data.items;
    totalNumber.value = res.data.totalSize;
  });

};

async function urlToFormData (url) {
  try {
    const response = await fetch(url);
    const blob = await response.blob();
    const file = new File([blob], "image.png", { type: "image/png" });

    const formData = new FormData();
    formData.append("iconFile", file);
    return { file, formData };
  } catch (err) {
    console.error("下载图片失败:", err);
    throw err;
  }
}
const editOperateBut = async (value) => {
  dialogFormVisible.value = true;
  title.value = "修改";
  formDialog.value = value;
 

  if (formDialog.value.iconPath) {
    try {
       fileArray.value = [{ url: formDialog.value.iconPath, raw: null }];
      // const { file } = await urlToFormData(formDialog.value.iconPath);
      // fileArray.value = [{ url: formDialog.value.iconPath, raw: file }];
      // console.log("fileArray.value", fileArray.value);
    } catch (err) {
      console.error("下载图标失败", err);
    }

  }
}
const tableRef = ref(null);
const clickOperateBut = (type, value) => {
  switch (type) {
    case 1:
      dialogFormVisible.value = true;
      title.value = "新增";
      break;

    case 3:
      const filteredHeaders = exportTableHeader.value.filter(
        item => item.key !== 'createTime'
      );
      exportCustomExcel(
        filteredHeaders,
        [],
        `巡检项导入模板`
      );
      break;

    case 4:
      if (selectList.value.length === 0) {
        ElMessage({ type: "warning", message: "请选择要删除的巡检项配置", showClose: true });
        return;
      }
      let ids = selectList.value.map(item => item.id);
      ElMessageBox.confirm(
        `确认删除选中的${selectList.value.length}条巡检项配置吗？`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      ).then(() => {
        // 删除逻辑
        deleteAllInspectionItemByIds({ ids: ids }).then(res => {
          if (res.success) {
            ElMessage({ type: "success", message: "删除成功", showClose: true });
            queryList();
          }
        });

      }).catch(() => {
        ElMessage({ type: "info", message: "已取消删除", showClose: true });
        tableRef.value?.clearSelection();

      });
      break;

    case 5:
      // 导入逻辑
      importComponentLibraryVisible.value = true;

      break;

    case 6:
      exportCustomExcel(
        exportTableHeader.value,
        selectList.value.length?selectList.value:ConfigurationList.value,

        `${activeSiteName.value}_巡检项`
      );
      // 清空选中行
  // tableRef.value?.clearSelection();
      break;

    default:
      break;
  }
};
const formDialogRef = ref(null);
// 关闭弹窗
const dialogcopyVisible = () => {
  formDialogRef.value?.resetFields();
  formDialog.value = {
    name: '',
    description: '',
    code: ''
  };
  formDialogRef.value?.clearValidate();
  dialogFormVisible.value = false;
  fileArray.value = [];
}
// 新增/修改 确定弹窗
const clickConfirmBut = () => {
  console.log('formDialog', formDialog.value.description)
  formDialogRef.value.validate(async (valid) => {
    if (valid) {
      let formData = new FormData();
      // 添加文件（如果存在）
      if (fileArray.value.length > 0) {
        if(!fileArray.value[0].raw){
          const {file} = await urlToFormData(fileArray.value[0].url)
          formData.append("iconFile", file);
        }else{
          formData.append("iconFile", fileArray.value[0].raw);
        }
        
      }
      formData.append("name", formDialog.value.name);
      formData.append("description", formDialog.value.description);
      formData.append("siteId", activeSiteId.value);
      if (formDialog.value.id) {
        formData.append("id", formDialog.value.id);
      }

      saveInspectionItem(formData).then(res => {
        if (res.success) {
          ElMessage({ type: "success", message: "操作成功", showClose: true });
          queryList();
          dialogcopyVisible();
        }
      });
    }
  });
}
// 重置
const clickResetForm = () => {
  activeDeviceInfo.value = {
    name: "",
  };
  currentPage.value = 1;
  pageNum.value = 10;
 queryList();

};
// onMounted(() => {
 

// });


</script>

<style lang="scss" scoped>
.app-container {
  flex-direction: initial !important;
}

.app-container-right {
  flex: 1;
  width: 2px;
  flex-basis: auto;
  display: flex;
  flex-direction: column;
  margin-left: 12px;

  .tableContent {
    padding: 12px;
    box-sizing: border-box;

    .tableCenter {
      // margin-top: 12px;

    }
  }

}
</style>