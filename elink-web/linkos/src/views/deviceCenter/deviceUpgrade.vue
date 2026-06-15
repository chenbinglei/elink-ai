<template>
  <div class="app-container">
    <div v-resize="setTableMaxHeight" class="app-container-right">
      <div ref="headerFormRef" class="header-form">
        <el-form :model="formInline" inline>
          <el-form-item label="关键词：">
            <el-input v-model="formInline.keyword" clearable placeholder="请输入任务名称、目标版本"></el-input>
          </el-form-item>
          <el-form-item label="电站名称：">
            <el-input v-model="formInline.siteName" clearable placeholder="请输入电站名称"></el-input>
          </el-form-item>
          <el-form-item label="升级时间：">
            <el-date-picker v-model="formInline.pickerDate" type="datetimerange" format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss" :disabled-date="pickerOptions.disabledDate" range-separator="~"
              start-placeholder="开始时间" end-placeholder="结束时间" style="width: 290px;" />
          </el-form-item>
          <el-form-item label="SN号：">
            <el-input v-model="formInline.deviceNumber" clearable placeholder="请输入设备序列号"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button :icon="Search" class="whiteFontButtons" @click="listArray('resetPage')">查询</el-button>
            <el-button :icon="Refresh" class="blackFontButtons" @click="clickResetForm">重置</el-button>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button :disabled="isAddButtonClick" :icon="Plus" class="whiteFontButtons"
              @click="clickAddButFun">创建任务</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="tableContent">
        <div ref="tableCenterRef" class="tableCenter">
          <el-table v-loading="listLoading" :data="list" :expand-row-keys="expandRowKeysArray" :row-key="getRowKeys"
            :max-height="tableMaxHeight" @expand-change="expandChangeFun">
            <el-table-column type="expand" fixed="left">
              <template #default="{ row }">
                <DeviceTaskRecordListCom ref="deviceTaskRecordListComRef" :taskId="row.id"
                  :deviceUpdateWsList="deviceUpdateWsList" />
              </template>
            </el-table-column>
            <el-table-column fixed="left" label="序号" type="index" width="80">
              <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column fixed="left" label="任务名称" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.taskName) }}</template>
            </el-table-column>
            <el-table-column label="状态">
              <template #default="{ row }">
                <span class="taskStatus" :class="'taskStatus_' + row.taskStatus">{{
                  $filters.taskUpgradeStatus(row.taskStatus) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="设备类型">
              <template #default="{ row }">{{ $filters.moreData(row.typeName) }}</template>
            </el-table-column>
            <el-table-column label="固件类型" show-overflow-tooltip>
              <template #default="{ row }">
                <template v-if="row.typeId >= 28 && row.typeId <= 30">{{ $filters.pileFirmwareType(row.firmwareType)
                }}</template>
                <template v-else>{{ $filters.moreData(row.firmwareType) }}</template>
              </template>
            </el-table-column>
            <el-table-column label="目标版本">
              <template #default="{ row }">{{ $filters.moreData(row.targetVersion) }}</template>
            </el-table-column>
            <el-table-column label="任务描述" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.taskDesc) }}</template>
            </el-table-column>
            <el-table-column label="创建信息" show-overflow-tooltip>
              <template #default="{ row }">
                <span>{{ $filters.moreData(row.createName) }}</span>
                <span>，</span>
                <span>{{ $filters.moreData(row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作">
              <template #default="{ row }">
                <template v-if="row.taskStatus === 3">
                  <el-link :underline="false" type="danger" @click="clickOperateButFun(1, row)">删除</el-link>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber"
            @pageChange="listArray" />
        </div>
      </div>
    </div>

    <CreateUpgradeTaskDialog v-if="createUpgradeTaskVisible" v-model:isVisible="createUpgradeTaskVisible"
      @changeEvent="listArray('resetPage')"></CreateUpgradeTaskDialog>
  </div>
</template>

<script lang="ts">
import { useAppStore } from '@/stores/index';

import { operateButtonIsClick } from "@/utils";
import { ElMessage, ElMessageBox } from "element-plus";
import { pickerOptionsGthanAcTime } from "@/utils/dateTime";
import { Plus, Refresh, Search } from "@element-plus/icons-vue";
import { deviceUpdateWebSocket } from "@/api/websocket/webSocket";
import { deleteDeviceTaskById, queryDeviceTaskList } from "@/api/deviceCenter/deviceUpgrade";
import { CreateUpgradeTaskDialog, DeviceTaskRecordListCom } from "@/views/deviceCenter/component";
import { computed, reactive, ref, toRefs, defineComponent, onMounted, onUnmounted, nextTick, onActivated, onDeactivated } from "vue";

export default defineComponent({
  name: "deviceUpgrade",
  components: { CreateUpgradeTaskDialog, DeviceTaskRecordListCom },
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  setup (props) {

    const appStore = useAppStore();
    const userInfo = computed(() => {
      return appStore.userInfo
    });

    const isAddButtonClick = computed(() => {
      return operateButtonIsClick('/device/deviceTask/createDeviceTask')
    })

    const that = reactive({
      Plus,
      Search,
      Refresh,
      formInline: {},
      oldFormInline: {},
      deviceAssetTypeList: [],
      pickerOptions: pickerOptionsGthanAcTime(),

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
      expandRowKeysArray: [],
      createUpgradeTaskVisible: false,

      websocket: null,
      websocketNum: 1,
      webSocketTimer: null,
      deviceUpdateWsList: [],
    })
    const deviceTaskRecordListComRef = ref();
    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (formInline.pickerDate) {
        formInline.endTime = formInline.pickerDate[1];
        formInline.startTime = formInline.pickerDate[0];
        delete formInline.pickerDate;
      }

      queryDeviceTaskList({ page: that.currentPage, size: that.pageNum, ...formInline }).then(res => {
        that.totalNumber = res.data.totalSize;
        that.list = res.data.items;
        that.listLoading = false;

        nextTick(() => {
          // 默认打开第一项
          if (that.list && that.list.length) {
            that.expandRowKeysArray = [that.list[0].id];
            // 主动调用子组件的 findDeviceTaskRecordList
             console.log("主动调用子组件的 findDeviceTaskRecordList", deviceTaskRecordListComRef.value)
            if (deviceTaskRecordListComRef.value) {
               deviceTaskRecordListComRef.value.findDeviceTaskRecordList();
             
            }
          }
        })
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return
        that.totalNumber = 0;
        that.list = [];
      })
    }

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    }

    const clickAddButFun = () => {
      that.createUpgradeTaskVisible = true;
    }

    // 展开行触发 (只展开当前行
    const expandChangeFun = (expandedRows) => {
      that.expandRowKeysArray = [expandedRows.id];
    }

    const clickOperateButFun = (index, row) => {
      if (index === 1) {
        ElMessageBox.confirm(`您确定要删除当前任务（<span class="deleteName">${row.taskName}</span>）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning', closeOnClickModal: false,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteDeviceTaskById({ id: row.id, type: 1 }).then(() => {
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
          ElMessage({ type: "success", showClose: true, message: "操作成功！" });
        }).catch(() => {
          console.log("取消删除");
        });
      }
    }

    //初始化 initWebSocket
    const initWebSocket = () => {
      if (typeof WebSocket === 'undefined') {
        ElMessage({ type: "error", showClose: true, message: "您的浏览器不支持websocket" });
        return
      }

      // that.listLoading = true;
      let wsUrl = deviceUpdateWebSocket({ userId: userInfo.value.userId });
      that.websocket = new WebSocket(wsUrl);
      that.websocket.onopen = webSocketOpen;
      that.websocket.onerror = webSocketError;
      that.websocket.onclose = webSocketClose;
      that.websocket.onmessage = webSocketMessage;
    }

    const webSocketMessage = (message) => {
      try {
        let data = JSON.parse(message.data);
        if (typeof data === "object") {
          // console.log(data);
          that.deviceUpdateWsList = data ?? [];

          // 更新任务状态
          if (that.list && that.list.length) {
            for (let i = 0; i < that.list.length; i++) {
              let findItem = that.deviceUpdateWsList.find(item => item.taskId === that.list[i].id);
              if (findItem) that.list[i].taskStatus = findItem.taskStatus;
            }
          }

        }
      } catch (e) {
        //TODO handle the exception
        console.log(e);
      }
    }

    const webSocketOpen = () => {
      that.websocketNum = 0;
      // that.listLoading = false;
      console.log('设备升级进度 实时数据连接成功');
      that.webSocketTimer = setInterval(() => {
        that.websocket.send("ping");
      }, 30000)
    }

    const webSocketError = () => {
      // 连接建立失败重连
      that.websocketNum++
      if (that.websocketNum <= 5) {
        // 延迟三秒在重新进行连接
        setTimeout(() => {
          initWebSocket();
        }, 3000);
      } else {
        // that.listLoading = false;
        console.log('设备升级进度 ----> 连接失败!!!');
      }
    };

    // 断开 websocket
    const webSocketClose = (e) => {
      console.log('设备升级进度 ----> 断开连接!!!');
    };

    //关闭websocket
    const closeWebSocketFun = () => {
      that.websocketNum = 11;
      that.deviceUpdateWsList = [];
      clearInterval(that.webSocketTimer);
      console.log('设备升级进度 ----> 准备断开连接!!!');
      if (that.websocket) that.websocket.close();
    }

    const getRowKeys = (row) => {
      return row.id
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      // initWebSocket();
      listArray();
    })

    onActivated(() => {
      initWebSocket();
    })

    onUnmounted(() => {
      closeWebSocketFun(); //关闭websock
    });

    onDeactivated(() => {
      closeWebSocketFun(); //关闭websock
    })

    return {
      ...toRefs(that), isAddButtonClick, headerFormRef, setTableMaxHeight, clickResetForm, listArray, clickAddButFun, clickOperateButFun, initWebSocket,
      webSocketMessage, webSocketOpen, webSocketError, webSocketClose, closeWebSocketFun, getRowKeys,deviceTaskRecordListComRef, expandChangeFun
    }
  }
})
</script>
<style lang="scss" scoped>
.taskStatus_1 {
  color: #00BD55FF;
}

.taskStatus_2 {
  color: #2b66feff;
}

.taskStatus_3 {
  color: #b9b9b9ff;
}
</style>