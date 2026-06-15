<template>
  <Dialog v-model:isVisible="dialog_visible" :footerVisible="false" closeOnClickModal title="节点日志" width="50vw">
    <template v-slot:content>
      <div v-loading="listLoading" class="content_body">

        <div class="content_body_top">
          <el-form :inline="true" :model="formInline">
            <el-form-item label="时间：">
              <el-date-picker v-model="formInline.queryDate" :disabled-date="pickerOptions.disabledDate" format="YYYY-MM-DD" placeholder="请选择时间"
                              type="date" value-format="YYYY-MM-DD"/>
            </el-form-item>
            <el-form-item label="类型：">
              <el-select v-model="formInline.queryType" clearable placeholder="请选择类型">
                <el-option v-for="item in queryTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button class="whiteFontButtons" @click="listArray">查询</el-button>
              <el-button class="blackFontButtons" @click="resetForm">重置</el-button>

              <el-dropdown style="margin-left: 10px">
                <el-button class="blackFontButtons">
                  <span>清除日志</span>
                  <el-icon><arrow-down/></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-item @click="clickClearLog(1)">清空当天</el-dropdown-item>
                  <el-dropdown-item @click="clickClearLog(2)">清空全部</el-dropdown-item>
                </template>
              </el-dropdown>
            </el-form-item>
          </el-form>
        </div>
        <div class="content_body_bottom">
          <div class="tableCenter">
            <el-table :data="list" :max-height="tableMaxHeight">
              <el-table-column align="center" label="序号" width="80">
                <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
              </el-table-column>
              <el-table-column align="center" label="类型" width="120">
                <template #default="{ row }">{{ $filters.nodeLogType(row.queryType) }}</template>
              </el-table-column>
              <el-table-column align="center" label="级别" width="120">
                <template #default="{ row }">{{ $filters.moreData(row.logLevel) }}</template>
              </el-table-column>
              <el-table-column align="center" label="记录时间">
                <template #default="{ row }">{{ $filters.moreData(row.logTime) }}</template>
              </el-table-column>
              <el-table-column align="center" label="日志信息" show-overflow-tooltip>
                <template #default="{ row }">{{ $filters.moreData(row.logInfo) }}</template>
              </el-table-column>
            </el-table>
          </div>
          <div class="tablePagination">
            <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
          </div>
        </div>

      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {ArrowDown} from '@element-plus/icons-vue';
import {getNowDate, pickerOptionsGthanAcTime} from "@/utils/dateTime";
import {getCurrentInstance, onMounted, reactive, toRefs, watch} from "vue";
import {findNodeLogInfoListByPage, removeNodeLogInfo} from "@/api/dataManagement/nodeManagement";

export default {
  name: "ComputeNodeLog",
  components: {ArrowDown},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    // 当前编辑的节点id
    computeNodeId: {
      type: [String, Number],
      default: ""
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      oldFormInline: {},
      formInline: {queryDate: getNowDate()},

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 320,
      dialog_visible: props.isVisible,

      pickerOptions: pickerOptionsGthanAcTime(),
      queryTypeArray: [{id: "undefined", name: "全部"}, {id: 1, name: "定时任务"}, {id: 2, name: "数据补录"}],
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findNodeLogInfoListByPage({...that.formInline, page: that.currentPage, size: that.pageNum, nodeId: props.computeNodeId}).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return
        that.list = [];
        that.totalNumber = 0;
      });
    }

    const resetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    }

    const clickClearLog = (removeType) => {
      removeNodeLogInfo({removeDate: that.formInline.queryDate, queryType: Number(that.formInline.queryType) ? that.formInline.queryType : '',
        nodeId: props.computeNodeId, removeType: removeType}).then(() => {
        listArray("refresh");
        ElMessage({type: "success", message: "删除成功!", showClose: true});
      }).catch(() => {
      });
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      listArray();
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, listArray, clickClearLog, resetForm}
  }
}
</script>

<style lang="scss" scoped>
.content_body {
  .content_body_top {
  }

  .content_body_bottom {
    margin-top: 12px;

    .tablePagination {
      margin-top: 12px;
    }
  }
}
</style>
