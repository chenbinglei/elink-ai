<template>
  <div class="company-info-wrap">
    <!-- 头部 -->
    <div class="company-head flex-ai-center mb-24px" v-if="companyInfo">
      <div class="flex-jc-ai-center logo">
        <img
          :src="companyInfo.logo"
          alt=""
          width="50"
          height="50"
          v-if="companyInfo.logo"
        />
      </div>
      <div class="flex-ai-center ml-5 info">
        <div>
          <p class="mb-16px">{{ companyInfo.tenantName }}</p>
          <p>id：{{ companyInfo.id }}</p>
        </div>
        <div class="pl-5 account">
          <p>企业账号</p>
          <p>{{ companyInfo.superAccount }}</p>
        </div>
        <div class="pl-5 account user">
          <p>主管理员</p>
          <p>{{ companyInfo.phone }}</p>
        </div>
      </div>
    </div>
    <!-- 组织加上场站 -->
    <div class="company-inner">
      <div
        :class="activeMenu == index ? 'handleMenus' : 'handleMenus_active'"
        @click="activeMenu = index"
        v-for="(item, index) in handleMenusArray"
        :key="index"
      >
        <span
          :class="
            activeMenu == index ? 'handleMenus_span' : 'handleMenus_span_active'
          "
          >{{ item }}</span
        >
      </div>
      <div class="flex mt-24px" v-if="activeMenu == 0" style="height: 100%;">
        <div class="company-inner-left">
          <h3>组织架构</h3>
          <HandleMenus
            ref="handleMenusRef"
            :handleMenuArray="handleMenuArray"
            :treeProps="handleMenuTreeProps"
            customTreeClass="leftArrowClass"
            dataRenewIsFun
            defaultExpandAll
            :isSearchInput="false"
            :isShowHeader="false"
            class="content_body_left"
            :expandOnClickNode="false"
            :moreOperate="true"
            @more="handleMenuMoreEvent"
            @handleMenuEvent="handleMenuEvent"
          />
        </div>
        <div class="company-inner-right flex-1 pl-5">
          <div class="flex jc-end mb-5">
            <el-button
              @click="openSiteDialog()"
              :icon="CirclePlus"
              :disabled="!parentId"
              >添加站点</el-button
            >
          </div>
          <el-table
            class="el-table-company"
            :data="tableData"
            @row-click="(row) => handleRowClick(row, index)"
            v-loading="loading"
            ref="tableRef"
          >
            <el-table-column
              prop="siteName"
              label="站点名称"
              :column-key="1"
              align="center"
            />
            <el-table-column
              prop="siteName"
              label="权限"
              :column-key="1"
              align="center"
            >
              <template #default="scope">
                <el-radio-group v-model="scope.row.authority" class="ml-4">
                  <el-radio
                    :label="1"
                    size="small"
                    @change="updateSite(scope.row)"
                    :disabled="!parentId"
                    >只读</el-radio
                  >
                  <el-radio
                    :label="2"
                    size="small"
                    @change="updateSite(scope.row)"
                    :disabled="!parentId"
                    >读写</el-radio
                  >
                </el-radio-group>
              </template>
            </el-table-column>
            <el-table-column
              prop="siteName"
              label="操作"
              :column-key="1"
              align="center"
            >
              <template #default="scope">
                <span
                  @click="deleteSite(scope.row.id)"
                  class="pointer blue-num"
                  style="font-size: 14px"
                  v-if="parentId"
                  >删除</span
                >
                <span v-else style="font-size: 14px; color: #ccc">删除</span>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-if="pagination.total > 0"
            :current-page="pagination.currentPage"
            class="mt-12px justify-end paginationCurrentPage"
            :page-size="pagination.pageSize"
            :total="pagination.total"
            @size-change="handleSizeChange"
            :page-sizes="pageSizes"
            @current-change="handleCurrentChange"
            layout="total, sizes, prev, pager, next, jumper"
          />
        </div>
      </div>
      <!-- 弹框 权限配置-->
      <permissionCon v-else></permissionCon>
    </div>
    <!-- 新增站点 -->
    <Dialog
      v-model="isSiteVisible"
      @cancel="isSiteVisible = false"
      @confirm="handleConfirm"
      width="30%"
      title="添加站点"
    >
      <template v-slot:content>
        <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
          <el-form-item label="场站" prop="siteId" required>
            <el-select v-model="form.siteId" placeholder="选择场站" multiple>
              <el-option
                :label="item.siteName"
                :value="item.id"
                v-for="item in siteList"
                :key="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="权限">
            <el-radio-group v-model="form.authority">
              <el-radio :label="1" size="small">只读</el-radio>
              <el-radio :label="2" size="small">读写</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </template>
    </Dialog>
    <!-- 组织 -->
   <div v-if="sOrganVisible">
     <addOran
      :visible="isOrganVisible"
      :activeRow="activeRow"
      :handleMenuArray="handleMenuArray"
      @close="closeOrgan()"
    ></addOran>
   </div>
  </div>
</template>
<script setup>
import permissionCon from "./components/permissionCon.vue";
import addOran from "./components/addOran.vue";
import Dialog from "@/components/Dialog/index.vue";
import { CirclePlus } from "@element-plus/icons-vue";
import { reactive, onMounted, ref, nextTick } from "vue";
import SystemsetController from "@/api/system/index";
const handleMenusArray = ref(["资产权限", "应用权限"]);
import HandleMenus from "@/components/handleMenu/HandleMenus.vue";
import { ElMessage } from "element-plus";
import { ElMessageBox } from "element-plus";
const activeMenu = ref(0);
const isSiteVisible = ref(false);
const isOrganVisible = ref(false);
const isVisiblePermissionCon = ref(false);
const companyInfo = ref();
const activeRow = ref();
const tableData = ref([]);
const organId = ref("");
const parentId = ref("");
const formRef = ref(null);
const loading = ref(false);
const tableRef = ref(null);
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});
const form = ref({
  authority: 1,
  siteId: [],
});
const rules = ref({
  siteId: [{ required: true, message: "请选择", trigger: "change" }],
});
const siteList = ref([]);
const pageSizes = [10, 20, 30, 40, 50, 100, 200, 500];
// 保存场站
const handleConfirm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      batchSaveOrganEmpower();
    } else {
      ElMessage({ type: "error", showClose: true, message: "表单校验失败" });
      return false;
    }
  });
};
const handleSizeChange = (size) => {
  pagination.pageSize = size;
  findEmpowerListByPage();
};

const handleCurrentChange = (page) => {
  pagination.currentPage = page;
  findEmpowerListByPage();
};
const closeOrgan = (reset) => {
  isOrganVisible.value = false;
  findOrganStructureByTenantId();
};
// 点击组织的更多操作
const handleMenuMoreEvent = (item) => {
  console.log(item);
  let { type, data } = item;
  if (type == "addOrgan" || type == "editOrgan") {
    isOrganVisible.value = true;
    activeRow.value = { ...data, type };
  } else deleteOrganStructureById(data.id);
};
// 左侧组织数据
const handleMenuArray = ref([]);
const handleMenuTreeProps = ref({
  value: "id",
  label: "organName",
  children: "childrenList",
});
const handleRowClick = (row, index) => {};
// 点击左侧组织架构
const handleMenuEvent = (item) => {
  console.log(item,'组织结构');
  
  organId.value = item.id;
  parentId.value = item.parentId;
  findEmpowerListByPage();
};
// 组织架构
const findOrganStructureByTenantId = () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  SystemsetController.findOrganStructureByTenantId({
    userId,
    tenantId,
  }).then((res) => {
    handleMenuArray.value = res.data;
    organId.value = res.data.find((item) => !item.parentId).id;
    findEmpowerListByPage();
  });
};
// 查询企业详情
const findTenantDetailsById = () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  SystemsetController.findTenantDetailsById({
    tenantId,
    userId,
    id: tenantId,
  }).then((res) => {
    companyInfo.value = res.data;
  });
};
// 场站列表
const findOrganEmpowerSiteList = () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  SystemsetController.findOrganEmpowerSiteList({
    organId: organId.value,
    tenantId,
    userId,
  }).then((res) => {
    siteList.value = res.data;
  });
};
// 查询场站
const findEmpowerListByPage = () => {
  loading.value = true;
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  SystemsetController.findEmpowerListByPage({
    organId: organId.value ?? handleMenuArray.value?.[0]?.id,
    page: pagination.currentPage,
    size: pagination.pageSize,
    tenantId,
    userId,
  }).then(async res => {
    loading.value = false;
    pagination.total = res.data.totalSize;
    tableData.value = res.data.items;
    await nextTick();
    if (tableRef.value) {
      tableRef.value.setScrollTop(0);
    }
  });
};
// 添加场站
const batchSaveOrganEmpower = () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  let organEmpowerInfoStr = form.value.siteId.map((id) => {
    let obj = siteList.value.find((obj) => obj.id == id);
    return {
      siteId: id,
      organId: organId.value,
      tenantId: obj.tenantId,
      authority: form.value.authority,
    };
  });
  SystemsetController.batchSaveOrganEmpower({
    organEmpowerInfoStr,
    tenantId,
    userId,
  }).then((res) => {
    ElMessage({ message: "操作成功", type: "success" });
    findEmpowerListByPage();
  });
};
// 修改权限
const updateSite = (item) => {
  SystemsetController.updateEmpowerAuthorityById({
    authority: item.authority,
    empowerId: item.id,
    empowerType: 2,
  }).then((res) => {
    ElMessage({ message: "操作成功", type: "success" });
  });
};
// 删除场站
const deleteSite = (empowerId) => {
  ElMessageBox.confirm("确定删除当前资产授权?")
    .then(() => {
      SystemsetController.deleteEmpowerInfoById({
        empowerId,
        empowerType: 2,
      }).then((res) => {
        ElMessage({ message: "操作成功", type: "success" });
        findEmpowerListByPage();
      });
    })
    .catch(() => {});
};
// 删除组织
const deleteOrganStructureById = (organId) => {
  ElMessageBox.confirm("确定删除当前组织?")
    .then(() => {
      SystemsetController.deleteOrganStructureById({ organId }).then((res) => {
        ElMessage({ message: "操作成功", type: "success" });
        findOrganStructureByTenantId();
      });
    })
    .catch(() => {});
};
// 打开弹框
const openSiteDialog = () => {
  isSiteVisible.value = true;
  form.value = {
    authority: 1,
    siteId: [],
  };
  findOrganEmpowerSiteList();
};
onMounted(() => {
  findOrganStructureByTenantId();
  findTenantDetailsById();
});
</script>
<style scoped lang="scss">
@import "./index.scss";
</style>