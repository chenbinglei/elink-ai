<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="桩编号：">
              <el-input v-model="formInline.pileCode" clearable placeholder="请输入桩编号"
                @change="formInline.gunCode = ''"></el-input>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="枪编号：">
              <el-input v-model="formInline.gunCode" :disabled="!formInline.pileCode" clearable
                placeholder="请输入枪编号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="12" :xl="12" :xs="24">
            <el-form-item label="开始时间：">
              <el-date-picker v-model="formInline.startAlsoDate" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss"
                range-separator="~" @change="pickerOptions.pickerDateChange"
                @calendar-change="pickerOptions.calendarChange" :shortcuts="pickerOptions.shortcuts"
                :disabled-date="pickerOptions.disabledDate" :disabled-hours="pickerOptions.disabledHours"
                :disabled-minutes="pickerOptions.disabledMinutes" :disabled-seconds="pickerOptions.disabledSeconds"
                start-placeholder="开始时间" end-placeholder="结束时间" />
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="协议类型：">
              <el-select v-model="formInline.protocolType" clearable filterable placeholder="全部">
                <el-option v-for="item in protocolTypeArray" :key="item.id" :label="item.name"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" class="blackFontButtons" @click="clickResetForm">重置</el-button>
              <el-button :icon="Folder" :disabled="exportLoading" :loading="exportLoading"
                @click="clickExportBut">导出</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div ref="tableContentRef" class="tableContent content_border">
      <TableHeaderTitle title="日志记录"></TableHeaderTitle>
      <div ref="tableCenterRef" v-resize="setTableMaxHeight" class="tableCenter">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" stripe ref="tableRef">
          <el-table-column fixed="left" label="序号" type="index" width="80">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column fixed="left" label="时间" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.dateTime) }}</template>
          </el-table-column>
          <el-table-column fixed="left" label="桩编号" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.pileCode) }}</template>
          </el-table-column>
          <el-table-column label="枪编号">
            <template #default="{ row }">{{ $filters.moreData(row.gunCode) }}</template>
          </el-table-column>
          <el-table-column label="类型">
            <template #default="{ row }">{{ $filters.protocolType(row.protocolType) }}</template>
          </el-table-column>
          <el-table-column label="消息ID">
            <template #default="{ row }">{{ $filters.moreData(row.cmd) }}</template>
          </el-table-column>
          <el-table-column label="发送方" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.sender) }}</template>
          </el-table-column>
          <el-table-column label="接收方" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.receiver) }}</template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="90">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" @click="clickOperateBut(1, row)">详情</el-link>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div ref="tablePaginationRef" class="tablePagination">
        <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber"
          @pageChange="listArray" />
      </div>
    </div>
    <MessageParsingDialog v-if="messageParsingVisible" v-model:isVisible="messageParsingVisible"
      :activeMsgDataInfo="activeMsgDataInfo" />
  </div>
</template>

<script lang="ts">
import { ElMessage } from "element-plus";
import { RefreshRight, Search,Folder } from '@element-plus/icons-vue';
import MessageParsingDialog from "./MessageParsingDialog.vue";
import { exportCustomExcel } from "@/common/exportExcel";
import { defineComponent, onMounted, reactive, ref, toRefs, nextTick } from "vue";
import { queryProtocolList } from "@/api/operationManagement/CsElectricPileLog";
import { getNowDateMin, pickerOptionsMinutesSecondsTimer } from "@/utils/dateTime";

export default defineComponent({
  name: "CsElectricPileLog",
  components: { MessageParsingDialog },
  setup () {

    const that = reactive({
      Search,
       Folder,
      RefreshRight,
      oldFormInline: {},
      formInline: { startAlsoDate: [getNowDateMin(-60), getNowDateMin(0)] },
      pickerOptions: pickerOptionsMinutesSecondsTimer(getNowDateMin(-60), getNowDateMin(0)),
      protocolTypeArray: [{ id: 1, name: "启动命令" }, { id: 2, name: "启动响应" }, { id: 3, name: "启动事件" }, { id: 4, name: "停止命令" }, { id: 5, name: "停止响应" },
      { id: 6, name: "停止事件" }, { id: 7, name: "记录上报" },
      { id: 8, name: "日志数据上报" }, { id: 9, name: "复位命令" }, { id: 10, name: "复位响应" },
      { id: 11, name: "设置二维码命令" }, { id: 12, name: "设置二维码响应" }
      ],
      exportLoading: false,
      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      activeMsgDataInfo: {},
      messageParsingVisible: false,
    });
    const tableRef = ref(null);
    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (formInline.startAlsoDate) {
        formInline.startTime = formInline.startAlsoDate[0];
        formInline.endTime = formInline.startAlsoDate[1];
        delete formInline.startAlsoDate;
      }

      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      queryProtocolList({ page: that.currentPage, size: that.pageNum, ...formInline }).then(async res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({ type: "success", message: "重置成功", showClose: true });
        await nextTick();
        if(tableRef.value){
          tableRef.value.setScrollTop(0);
        }
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.totalNumber = 0;
        that.list = [];
      });
    };
    const clickExportBut = () => {
      that.exportLoading = true;
      let tableHeader = [
        { width: 40, key: "dateTime", name: "时间" },
        { width: 20, key: "pileCode", name: "桩编号" },
        { width: 25, key: "gunCode", name: "枪编号" },

        { width: 20, key: "protocolType", name: "类型", filterName: 'protocolType' },
        { width: 25, key: "cmd", name: "消息ID" },
        { width: 25, key: "sender", name: "发送方" },
        { width: 20, key: "receiver", name: "接收方" },
        { width: 20, key: "content", name: `内容` },

      ];
      that.exportLoading = false;
      exportCustomExcel(tableHeader, that.list, `日志记录`);
      console.log(that.list)
    }

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      that.pickerOptions.pickerDateChange();
      listArray("resetPage");
    };

    const clickOperateBut = (operateType, row) => {
      if (operateType === 1) {
        that.activeMsgDataInfo = JSON.parse(JSON.stringify(row));
        that.messageParsingVisible = true;
      }
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 78;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      listArray();
    });

    return { ...toRefs(that), clickExportBut, clickResetForm, setTableMaxHeight, tableContentRef, tablePaginationRef, clickOperateBut, listArray, tableRef};
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;
}
</style>