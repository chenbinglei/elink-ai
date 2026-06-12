<template>
  <div class="content_body" v-resize="setTableMaxHeight">
    <div class="header-form" ref="headerFormRef">
      <el-form :model="formInline" inline>
        <el-form-item label="时间：">
          <el-date-picker v-model="pickerDate" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts" end-placeholder="结束时间"
                          range-separator="-" start-placeholder="开始时间" type="daterange" unlink-panels value-format="YYYY-MM-DD"/>
        </el-form-item>
        <el-form-item label="事件类型：">
          <el-select v-model="formInline.eventLevel" clearable placeholder="请选择接入类型"  @change="listArray('resetPage')">
            <el-option v-for="item in eventLevelArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button :icon="Search" class="whiteFontButtons" @click="listArray('resetPage')">查询</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div v-loading="listLoading" class="tableContent">
      <div ref="tableCenterRef" class="tableCenter">
        <el-table :data="list" :max-height="tableMaxHeight" border stripe>
          <el-table-column align="center" label="序号" type="index" width="80">
            <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="事件名称">
            <template #default="{ row }">{{ $filters.moreData(row.eventName)}}</template>
          </el-table-column>
          <el-table-column align="center" label="事件类型">
            <template #default="{ row }">{{ $filters.functionSourceType(row.type)}}</template>
          </el-table-column>
          <el-table-column align="center" label="事件来源" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.functionNames)}}</template>
          </el-table-column>
          <el-table-column align="center" label="告警级别">
            <template #default="{ row }">{{ $filters.eventLevel(row.eventLevel)}}</template>
          </el-table-column>
          <el-table-column align="center" label="上报时间" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
          </el-table-column>
          <el-table-column align="center" label="状态">
            <template #default="{ row }">
              <span class="eventStatus" :class="'eventStatus' + row.eventStatus">{{ $filters.eventStatus(row.eventStatus) }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="恢复时间">
            <template #default="{ row }">
              <template v-if="row.eventStatus">{{ $filters.moreData(row.updateTime) }} </template>
              <template v-else>--</template>
            </template>
          </el-table-column>
<!--          <el-table-column align="center" label="恢复时间">-->
<!--            <template #default="{ row }">-->
<!--              <template v-if="row.eventStatus === 1">{{ $filters.moreData(row.updateTime) }}</template>-->
<!--              <template v-else>-->
<!--                <template v-if="row.isAllow === 1">-->
<!--                  <el-link :underline="false" @click="clickOperateBut(1, row)">解除</el-link>-->
<!--                </template>-->
<!--                <template v-else>事件不可手动解除</template>-->
<!--              </template>-->
<!--            </template>-->
<!--          </el-table-column>-->
        </el-table>
      </div>
      <div class="tablePagination">
        <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
      </div>
    </div>
  </div>
</template>

<script>
import { useAppStore } from '@/stores/index';

import {ElMessage, ElMessageBox} from "element-plus";
import {event_level_array} from "@/utils/setVariate";
import {CirclePlus, Delete, Refresh, Search} from "@element-plus/icons-vue";
import {pickerOptionsSixMonth, getDaysFromCurrentTime} from "@/utils/dateTime";
import {computed, onMounted, reactive, ref, toRefs, defineComponent} from "vue";
import {findDeviceEventList, updateDeviceEventStatusById} from "@/api/deviceCenter/deviceList";

export default defineComponent({
  name: "DeviceEvent",
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {

    const appStore = useAppStore();
    const contentMainMaxHeight = computed(() => {
      return appStore.contentMainMaxHeight;
    });

    const that = reactive({
      Delete,
      Search,
      Refresh,
      CirclePlus,
      list: [],
      pageNum: 20,
      formInline: {},
      currentPage: 1,
      totalNumber: 0,
      listLoading: false, // 表格加载
      tableMaxHeight: 300,
      eventLevelArray: event_level_array,
      pickerOptions: pickerOptionsSixMonth(),
      pickerDate: [getDaysFromCurrentTime(-30),getDaysFromCurrentTime()],
    })

    // 查询模型标准功能列表
    const listArray = (reset) => {
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if(that.pickerDate){
        formInline.startDate = that.pickerDate[0];
        formInline.endDate = that.pickerDate[1];
      }
      findDeviceEventList({page: that.currentPage, size: that.pageNum, deviceId: props.activeDeviceId, ...formInline}).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886) return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const clickOperateBut = (operate,row)=>{
      if(operate === 1){
        ElMessageBox.confirm(`确定解除（<span class="deleteName">${ row.eventName }</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "deleteMsgBoxClass", showClose:false,type: 'warning',
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在解除...';
              updateDeviceEventStatusById({ id: row.id }).then(()=>{
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
          listArray("resetPage");
          ElMessage({ type: "success", showClose: true, message: "解除成功！" });
        }).catch(() => {
          console.log("取消解除！");
        });
      }
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = contentMainMaxHeight.value - headerFormHeight - 148;
    }

    onMounted(()=>{
      listArray();
    })

    return {...toRefs(that), headerFormRef, contentMainMaxHeight, setTableMaxHeight, listArray, clickOperateBut}
  }
})
</script>

<style lang="scss" scoped>
.content_body{
  height: 100%;
  padding: 16px 12px 0 12px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  .tableContent{
    padding: 0 !important;

    .eventStatus0{
      color: #FF1515;
    }
  }
}
</style>
