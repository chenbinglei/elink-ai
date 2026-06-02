<template>
  <div class="app-container-right">
    <TableHeaderTitle title="" :iconShow="false">
      <template #content>
        <div class="table_top_content">
          <el-button @click="clickReset()" type="primary">
            <span>复位</span>
          </el-button>
        </div>
      </template>
    </TableHeaderTitle>
    <div ref="tableContentRef" v-resize="setTableMaxHeight" class="tableContent">
      <!-- <el-button :icon="Plus">复位</el-button> -->
      <el-table v-loading="listLoading" :data="gunDetailList" :max-height="tableMaxHeight">
        <el-table-column align="center" fixed label="枪编号" min-width="120">
          <template #default="{ row }">{{ $filters.moreData(row.gunCode) }}</template>
        </el-table-column>
        <el-table-column align="center" fixed label="枪名称" min-width="120">
          <template #default="{ row }">{{ $filters.moreData(row.gunName) }}</template>
        </el-table-column>
        <el-table-column align="center" label="状态" min-width="140">
          <template #default="{ row }">
            <span class="gun_status" :class="'gun_status' + row.gunWorkState">{{ $filters.gunWorkState(row.gunWorkState)
            }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="已充/放电量（度）" min-width="210">
          <template #default="{ row }">
            <span v-if="row.gunStatus === 2 || row.gunStatus === 5">{{ $filters.moreData(row.totalQt) }}</span>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="已充/放时长（分）" min-width="210">
          <template #default="{ row }">
            <span v-if="row.gunStatus === 2 || row.gunStatus === 5">{{ $filters.moreData(row.runTime) }}</span>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="SOC（%）" min-width="120">
          <template #default="{ row }">
            <span v-if="row.gunStatus === 2 || row.gunStatus === 5">{{ $filters.moreData(row.batterySoc) }}</span>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="电压（V）" min-width="120">
          <template #default="{ row }">
            <span v-if="row.gunStatus === 2 || row.gunStatus === 5">{{ $filters.moreData(row.outVolt) }}</span>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="电流（A）" min-width="120">
          <template #default="{ row }">
            <span v-if="row.gunStatus === 2 || row.gunStatus === 5">{{ $filters.moreData(row.outCurrent) }}</span>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="功率（kW）" min-width="120">
          <template #default="{ row }">
            <span v-if="row.gunStatus === 2 || row.gunStatus === 5">{{ $filters.moreData(row.outPower) }}</span>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" min-width="210">
          <template #default="{ row }">
            <div class="table_operate_class flex-jc-ai-center">
              <el-link :underline="false" @click="clickOperateBut(1, row)">订单</el-link>
              <span class="split_line">|</span>
              <el-dropdown>
                <template #default>
                  <el-button size="small" plain :icon="MoreFilled"></el-button>
                </template>
                <template #dropdown>
                  <el-dropdown-menu>
                    <template v-for="(item, index) in operateButList" :key="index">
                      <el-dropdown-item @click="clickOperateBut(2, row, item.id)"
                        :disabled="((item.id === 1 || item.id === 2) && row.gunWorkState !== 4) ||
                          (item.id === 3 && row.gunWorkState !== 1 && row.gunWorkState !== 2) ||
                          (item.id === 4 && row.gunWorkState !== 1 && row.gunWorkState !== 2 && row.gunWorkState !== 8)">
                        <span>{{ item.name }}</span>
                      </el-dropdown-item>
                    </template>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <EndChargingDialog v-if="endChargingVisible" v-model:isVisible="endChargingVisible"
      :returnDataInfo="returnDataInfo" />
    <PowerControlDialog v-if="powerControlVisible" v-model:isVisible="powerControlVisible"
      :returnDataInfo="returnDataInfo" />
    <StartDisAndChargingDialog v-if="startDisAndChargingVisible" v-model:isVisible="startDisAndChargingVisible"
      :returnDataInfo="returnDataInfo" :operateType="operateType" />
    <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" title="复位"
      closeOnClickModal disabledLoading width="480" @confirm="clickConfirmBut" @cancel="clickCancelBut">
      <template v-slot:content>
        <el-form ref="formRef" :model="formInline" :rules="rules">
          <el-form-item label="固件类型：" prop="firmwareType" class="form_item_class">
            <el-select v-model="formInline.firmwareType" placeholder="请选择">
              <el-option v-for="item in pileFirmwareType" :key="item.name" :label="item.value"
                :value="item.name"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </template>
    </Dialog>
  </div>
</template>

<script>
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
import { MoreFilled } from '@element-plus/icons-vue';
import { queryUserAuthorityIsHaveFun } from "@/utils";
import { pileRealWebSocket } from "@/api/websocket/webSocket";
import { pileReset } from "@/api/operationManagement/CsPileGunRunningStatus";
import EndChargingDialog from "./RealTimeStatusCom/EndChargingDialog.vue";
import PowerControlDialog from "./RealTimeStatusCom/PowerControlDialog.vue";
import StartDisAndChargingDialog from "./RealTimeStatusCom/StartDisAndChargingDialog.vue";
import { computed, defineComponent, getCurrentInstance, onMounted, reactive, ref, toRefs, onUnmounted } from "vue";

export default defineComponent({
  name: "RealTimeStatusCom",
  components: { EndChargingDialog, PowerControlDialog, StartDisAndChargingDialog },
  props: {
    routeInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup (props) {

    const store = useStore();
    const { emit } = getCurrentInstance();

    const userInfo = computed(() => {
      return store.state.app.userInfo;
    });

    const that = reactive({
      MoreFilled,
      gunDetailList: [],
      listLoading: false,
      tableMaxHeight: 280,

      websocketNum: 1,
      pileMonitorWs: null,

      operateType: 1,
      returnDataInfo: {},
      endChargingVisible: false,
      powerControlVisible: false,
      startDisAndChargingVisible: false,
      dialog_visible: false,
      formInline: { firmwareType: "" },
      operateButList: [{ name: "启动充电", id: 1 }, { name: "启动放电", id: 2 }, { name: "功率控制", id: 3 }, { name: "结束订单", id: 4 }],
      // 电桩固件类型
      pileFirmwareType: [
        { name: 1, value: 'V2G_1.0 TCP 控制板' },
        { name: 2, value: 'V2G_2.0 TCP 控制板' },
        { name: 3, value: 'V2G_3.0 TCP 控制板' },
        { name: 4, value: 'V2G_4.0 TPU 控制板' },
        { name: 5, value: 'V2G_4.0 CCU 控制板' },
        { name: 6, value: 'V2G_6.0 TCP 控制板' },
        { name: 7, value: 'V2G_7.0 TPU 控制板' },
        { name: 8, value: 'V2G_8.0 CCU 控制板' },
        { name: 9, value: 'V2G_9.0 TCP_BOOT 控制板' },
        { name: 10, value: 'V2G_10.0 TPU_BOOT 控制板' },
        { name: 11, value: 'V2G_11.0 CCU_BOOT 控制板' }


      ],
      rules: {
        firmwareType: [{ required: true, message: "请选择固件类型", trigger: "blur" }]
      }
    });

    //初始化 initWebSocket
    const initWebSocket = () => {
      if (typeof WebSocket === 'undefined') {
        ElMessage({ type: "error", showClose: true, message: "您的浏览器不支持websocket" });
        return;
      }

      that.listLoading = true;
      let wsUrl = pileRealWebSocket({
        ...props.routeInfo,
        userId: userInfo.value.userId,
      });

      that.pileMonitorWs = new WebSocket(wsUrl);
      that.pileMonitorWs.onopen = webSocketOpen;
      that.pileMonitorWs.onerror = webSocketError;
      that.pileMonitorWs.onclose = webSocketClose;
      that.pileMonitorWs.onmessage = webSocketMessage;
    };
    const formRef = ref(null)
    const clickConfirmBut = () => {
      formRef.value.validate(valid => {
        if (valid) {
          let obj = {
            firmwareType: that.formInline.firmwareType,
            pileCode: props.routeInfo.pileCode,
          };

          pileReset(obj).then(res => {
            that.dialog_visible = false;
          }).catch(err => {
            // that.dialog_visible = false;
          });

        } else {
          return false;
        }
      });

    };
    const clickCancelBut = () => {
      that.dialog_visible = false;
      formRef.value.resetFields();
    };
    const webSocketMessage = (message) => {
      try {
        let data = JSON.parse(message.data);
        if (typeof data === "object") {
          that.gunDetailList = data.gunRealModelList;
        }
      } catch (e) {
        //TODO handle the exception
        console.log(e);
      }
    };

    const webSocketOpen = () => {
      that.websocketNum = 0;
      setTimeout(() => {
        that.listLoading = false;
        console.log('枪桩设备 实时数据连接成功');
      }, 500);
    };

    const webSocketError = () => {
      // 连接建立失败重连
      that.websocketNum++;
      if (that.websocketNum <= 5) {
        // 延迟三秒在重新进行连接
        setTimeout(() => {
          initWebSocket();
        }, 3000);
      } else {
        that.listLoading = false;
        console.log('枪桩设备 ----> 连接失败!!!');
      }
    };

    // 断开 websocket
    const webSocketClose = (e) => {
      console.log('枪桩设备 ----> 断开连接!!!');
    };

    //关闭websocket
    const closeWebSocketFun = () => {
      that.websocketNum = 11;
      console.log('枪桩设备 ----> 准备断开连接!!!');
      if (that.pileMonitorWs) that.pileMonitorWs.close();
    };
    const clickReset = () => {
      that.dialog_visible = true;
    }
    const clickOperateBut = (operateType, row, butType) => {
      if (operateType === 1) {
        let routeName = "/operationManagement/CsChargingRecord";
        if (row.gunWorkState === 2) routeName = "/operationManagement/CsDisChargingRecord";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if (!isAuthority) {
          ElMessage({ type: "warning", showClose: true, message: "请联系管理员打开对应权限！" });
          return;
        }

        let componentName = row.gunWorkState === 2 ? 'CsDisChargingRecord' : 'CsChargingRecord';
        emit("changEvent", { keywordType: "5", keyword: props.routeInfo.pileCode, operateType: "switchComponents", componentName: componentName });
        store.dispatch("updateSecondaryVisible", false);
      }

      if (operateType === 2) {
        that.operateType = butType;
        that.returnDataInfo = JSON.parse(JSON.stringify(row));
        that.returnDataInfo.pileCode = props.routeInfo.pileCode;

        if (butType === 4) that.endChargingVisible = true;
        if (butType === 3) that.powerControlVisible = true;
        if (butType === 1 || butType === 2) that.startDisAndChargingVisible = true;
      }
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const setTableMaxHeight = () => {
      that.tableMaxHeight = tableContentRef.value.offsetHeight;
    };

    onMounted(() => {
      initWebSocket();
    });

    onUnmounted(() => {
      closeWebSocketFun(); //关闭websock
    });

    return {
      ...toRefs(that), setTableMaxHeight, tableContentRef, clickOperateBut, userInfo, initWebSocket, webSocketClose, webSocketError, webSocketOpen,
      webSocketMessage, closeWebSocketFun, clickReset, clickConfirmBut,formRef,clickCancelBut
    };
  }
});
</script>

<style lang="scss" scoped>
.gun_status {
  color: #E66AE3;
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 4px;
  box-sizing: border-box;
  background: rgba(230, 106, 227, .1);
}

.gun_status1 {
  // color: #41CB4A;
  color: #2B70DF;
  background: rgba(65, 203, 74, .1);
}

.gun_status2 {
  // color: #FF9C02;
  color: #41CB4A;
  background: rgba(255, 156, 2, .1);
}

.gun_status3 {
  // color: #007FEB;
  color: #00d1ff;
  background: rgba(0, 127, 235, .1);
}

.gun_status4 {
  color: #EDA300;
  background: rgba(237, 163, 0, .1);
}

.gun_status5 {
  color: #FF1515;
  background: rgba(255, 21, 21, .1);
}

.gun_status6 {
  color: #666666;
  background: rgba(102, 102, 102, .1);
}

.gun_status8 {
  color: #00B3EB;
  background: rgba(0, 179, 235, .1);
}
</style>