<template>
  <div class="app-container-right">
    <div class="header-form" style="padding: 0;">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :md="6" :sm="12" :xl="6" :xs="24">
            <el-form-item>
              <el-input v-model="formInline.keyword" clearable placeholder="请输入关键字"></el-input>
            </el-form-item>
          </el-col>
          <el-col :md="6" :sm="12" :xl="6" :xs="24">
            <el-form-item label="策略类型：">
              <el-select v-model="formInline.strategyType" clearable placeholder="请选择策略类型">
                <el-option v-for="item in strategyTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div ref="tableContentRef" class="tableContent content_border">
      <div class="flex-ai-center" style="padding: 6px 0;">
        <img src="@/assets/image/celue-icon.png" width="20" /><span style="color: #fff" class="ml-2">策略模板</span>
      </div>
      <div class="flex jc-end mt-3 mb-3"><el-button :icon="Plus" @click="clickOperateBut(5)">创建策略模板</el-button></div>
      <div ref="tableCenterRef" v-resize="setTableMaxHeight" class="tableCenter">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" ref="tableRef" stripe>
          <el-table-column fixed="left" label="序号" type="index" width="80">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column fixed="left" label="模板名称" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.templateName) }}</template>
          </el-table-column>
          <el-table-column label="策略类型">
            <template #default="{ row }">{{ $filters.strategyType(row.strategyType) }}</template>
          </el-table-column>
          <el-table-column label="策略说明" show-overflow-tooltip>
            <template #default="{ row }">
              <div v-if="row.explainName" class="textClass" @click="clickOperateBut(3,row)">
                <span>{{ $filters.moreData(row.explainName) }}</span>
              </div>
              <div v-else>{{ $filters.moreData(row.explainName) }}</div>
            </template>
          </el-table-column>
          <el-table-column label="配置文件" show-overflow-tooltip>
            <template #default="{ row }">
              <div v-if="row.configName" class="textClass" @click="clickOperateBut(4,row)">
                <span>{{ $filters.moreData(row.configName) }}</span>
              </div>
              <div v-else>{{ $filters.moreData(row.configName) }}</div>
            </template>
          </el-table-column>
          <el-table-column label="创建信息" show-overflow-tooltip>
            <template #default="{ row }">
              <span>{{ $filters.moreData(row.createName) }}</span>，
              <span>{{ $filters.moreData(row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="最后修改信息" show-overflow-tooltip>
            <template #default="{ row }">
              <span>{{ $filters.moreData(row.updateName) }}</span>，
              <span>{{ $filters.moreData(row.updateTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="120">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(2, row)">删除</el-link>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div ref="tablePaginationRef" class="tablePagination">
        <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray" />
      </div>
    </div>
    <AddStrategyTemDialog v-if="addStrategyTemVisible" v-model:isVisible="addStrategyTemVisible" :titleName="titleName" :templateId="templateId"
      @changeEvent="listArray('refresh')" />
    <StrategyFileView v-if="strategyFileViewVisible" v-model:isVisible="strategyFileViewVisible" :titleName="titleName" :templateId="templateId"
      :templateType="templateType" />
  </div>
</template>

<script lang="ts">
import { ElMessage, ElMessageBox } from "element-plus";
import { strategy_type_array } from "@/utils/setVariate";
import { RefreshRight, Search, Plus } from '@element-plus/icons-vue';
import { reactive, defineComponent, toRefs, onMounted, ref, nextTick } from "vue";
import StrategyFileView from "./EMStrategyTemplate/StrategyFileView.vue";
import AddStrategyTemDialog from "./EMStrategyTemplate/AddStrategyTemDialog.vue";
import { deleteTemplateById, queryTemplateList } from "@/api/centralMonitoring/energyManagement";

export default defineComponent({
  name: "EMStrategyTemplate",
  components: { AddStrategyTemDialog, StrategyFileView },
  setup () {
    const that = reactive({
      Plus,
      Search,
      RefreshRight,
      formInline: {},
      oldFormInline: {},
      strategyTypeArray: strategy_type_array,

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      templateId: "",
      templateType: 1,
      titleName: "创建策略模板",
      addStrategyTemVisible: false,
      strategyFileViewVisible: false,
    });
    const tableRef = ref(null);
    const listArray = async (operateType) => {
      try {
        that.listLoading = true;
        const formInline = JSON.parse(JSON.stringify(that.formInline));

        if (operateType === "resetPage" || operateType === "refresh") {
          that.currentPage = 1;
        }

        const res = await queryTemplateList({
          page: that.currentPage,
          size: that.pageNum,
          ...formInline
        });

        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;

        await nextTick();

        if (tableRef.value) {
          tableRef.value.setScrollTop(0);
        }

        if (operateType === "resetPage") {
          ElMessage.success("重置成功");
        }
      } catch (error) {
        that.listLoading = false;
        if (error && error.code !== 88886) {
          that.totalNumber = 0;
          that.list = [];
          ElMessage.error("请求失败，请重试！");
        }
      }
    };
    // const listArray =  (operateType) => {
    //   that.listLoading = true;
    //   let formInline = JSON.parse(JSON.stringify(that.formInline));
    //   if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
    //   queryTemplateList({ page: that.currentPage, size: that.pageNum, ...formInline }).then(res => {
    //     that.listLoading = false;
    //     that.list = res.data.items;
    //     that.totalNumber = res.data.totalSize;
    //     await nextTick();
    //     if (tableRef.value) {
    //       tableRef.value.setScrollTop(0);
    //     }
    //     if (operateType === "resetPage") ElMessage({ type: "success", message: "重置成功", showClose: true });
    //   }).catch((error) => {
    //     that.listLoading = false;
    //     if (error && error.code === 88886) return;
    //     that.totalNumber = 0;
    //     that.list = [];
    //   });
    // };

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    };

    const clickOperateBut = (operateType, row) => {
      if (operateType === 1) {
        that.templateId = row.id;
        that.titleName = "编辑策略模板";
        that.addStrategyTemVisible = true;
      }

      if (operateType === 2) {
        ElMessageBox.confirm(`确定删除策略模板（<span class="deleteName">${row.templateName}</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteTemplateById({ id: row.id }).then(() => {
                done();
                instance.confirmButtonLoading = false;
              }).catch(() => {
                instance.confirmButtonText = '确定';
                instance.confirmButtonLoading = false;
              });
            } else {
              done();
            }
          }
        }).then(() => {
          listArray("refresh");
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }

      if (operateType === 3) {
        that.templateType = 1;
        that.templateId = row.id;
        that.titleName = "策略说明";
        that.strategyFileViewVisible = true;
      }

      if (operateType === 4) {
        that.templateType = 2;
        that.templateId = row.id;
        that.titleName = "配置文件";
        that.strategyFileViewVisible = true;
      }

      if (operateType === 5) {
        that.templateId = "";
        that.titleName = "创建策略模板";
        that.addStrategyTemVisible = true;
      }
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 98;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      listArray();
    });

    return { ...toRefs(that), tableContentRef, tableRef, tablePaginationRef, setTableMaxHeight, clickResetForm, clickOperateBut, listArray };
  }
});
</script>

<style lang="scss" scoped>
.app-container-right {
  box-sizing: border-box;
  background: url("@/assets/image/station-details/sta-detail-bg.png") no-repeat;
  background-size: 100% 100%;
}
.tableContent {
  background: url("@/assets/image/celue-bac.png") no-repeat;
  background-size: 100% 100%;
  box-sizing: border-box;
  border: none;
  padding: 0 24px;

  .textClass {
    color: #079ceb;
    font-size: 14px;
    cursor: pointer;
    text-decoration: underline;
  }
}
</style>