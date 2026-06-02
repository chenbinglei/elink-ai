<template>
  <div v-loading="listLoading" class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="activeDeviceInfo" inline>
        <el-row :gutter="16">
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="控制节点：">
              <el-select v-model="activeDeviceInfo.id" filterable placeholder="请选择控制节点" @change="deviceIdChangeFun">
                <el-option v-for="item in controlIdArray" :key="item.id" :label="item.deviceName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="16" :sm="12" :xl="18" :xs="24">
            <div class="flex-ai-center jc-end header-form-right">
              <template v-if="activeStrategyId">
                <div class="updateTimeText">
                  <span>最近更新时间：</span>
                  <span class="updateTime">{{ $filters.moreData(issueStrategyTime) }}</span>
                </div>
                <el-button @click="clickOperateBut(1)">
                  <template #icon>
                    <span class="iconfont icon-duqu"></span>
                  </template>
                  <span>读取</span>
                </el-button>
                <el-button type="primary" :loading="issuedLoading" @click="clickOperateBut(2)">
                  <template #icon>
                    <span class="iconfont icon-peizhixiafa"></span>
                  </template>
                  <span>配置下发</span>
                </el-button>
              </template>
              <el-button @click="clickOperateBut(3)">
                <template #icon>
                  <span class="iconfont icon-zhuangtaijiankong"></span>
                </template>
                <span>状态监控</span>
              </el-button>
              <el-button @click="clickOperateBut(4)">
                <template #icon>
                  <span class="iconfont icon-kongzhirizhi"></span>
                </template>
                <span>控制日志</span>
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div class="tableContent" v-if="controlIdArray && controlIdArray.length">
      <StrategyControlListCom ref="strategyControlListComRef" v-model:activeStrategyId="activeStrategyId" :activeDeviceInfo="issuedDeviceInfo" :activeSiteId="activeSiteId"/>
      <StrategyParameterCom ref="strategyParameterComRef" :activeStrategyId="activeStrategyId" :activeSiteId="activeSiteId" :activeDeviceInfo="issuedDeviceInfo" @changeEvent="changeEvent"/>
    </div>
    <null-data v-else words="请先添加网关设备"></null-data>

    <ConditionMonitoringDialog v-if="conditionMonitoringVisible" v-model:isVisible="conditionMonitoringVisible" :activeSiteId="activeSiteId" />
  </div>
</template>

<script>
import {useStore} from "vuex";
import {ElMessage, ElMessageBox} from "element-plus";
import {queryUserAuthorityIsHaveFun} from "@/utils";
import StrategyParameterCom from "./StrategyParameterCom.vue";
import StrategyControlListCom from "./StrategyControlListCom.vue";
import ConditionMonitoringDialog from "./ConditionMonitoringDialog.vue";
import {computed, defineComponent, getCurrentInstance, reactive, ref, toRefs, watch} from "vue";
import {findGatewayDataBySiteId, issuedStrategy} from "@/api/centralMonitoring/energyManagement";

export default defineComponent({
  name: "StrategyControlCom",
  components: {StrategyControlListCom, StrategyParameterCom, ConditionMonitoringDialog},
  props: {
    activeSiteId: {
      type: [String, Number],
      default: ""
    }
  },
  setup(props) {

    const store = useStore();
    const {emit} = getCurrentInstance();

    const issueStrategyTime = computed(() => {
      return store.state.energyManagement.issueStrategyTime;
    });
    const strategyName = computed(() => {
      return store.state.energyManagement.strategyName;
    });

    const that = reactive({
      listLoading: false,
      controlIdArray: [],
      activeDeviceInfo: {},
      issuedLoading: false,
      activeStrategyId: "", // 当前选中的策略id

      issuedDeviceInfo: {}, // 传递子组件的设备数据
      conditionMonitoringVisible: false,
    });

    // 控制节点发生改变 执行
    const deviceIdChangeFun = () => {
      let activeDeviceId = that.activeDeviceInfo.id || that.controlIdArray[0].id;
      let findItem = that.controlIdArray.find(item => item.id === activeDeviceId);
      let activeDeviceInfo = JSON.parse(JSON.stringify(findItem ?? {}));
      that.activeDeviceInfo = JSON.parse(JSON.stringify(activeDeviceInfo));

      delete activeDeviceInfo.id;
      that.issuedDeviceInfo = JSON.parse(JSON.stringify(activeDeviceInfo));
      // console.log(that.issuedDeviceInfo);
    };

    // 根据站点id查询网关数据
    const queryGatewayDataBySiteId = () => {
      that.listLoading = true;
      findGatewayDataBySiteId({siteId: props.activeSiteId,timer: new Date()}).then(res => {
        let controlIdArray = res.data ? res.data : [];
        controlIdArray.forEach(item=>{
          item.deviceId = item.id; // 新增设备iD
          item.id = item.id || item.deviceNumber;
        });
        that.controlIdArray = JSON.parse(JSON.stringify(controlIdArray));
        that.listLoading = false;
        deviceIdChangeFun();
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const strategyParameterComRef = ref(null);
    const clickOperateBut = (operateType) => {

      if (operateType === 1) {
        strategyParameterComRef.value.queryType = 2; //2-读取数据
        strategyParameterComRef.value.queryStrategyById();
        strategyControlListComRef.value.findStrategyList();
      }

      if (operateType === 2) {
        ElMessageBox.confirm(`确定下发当前已保存的策略（<span class="highlightText">${ strategyName.value }</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          showClose:false, closeOnClickModal: false, beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              that.issuedLoading = true;
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在下发...';
              issuedStrategy({ id: that.activeStrategyId  }).then(()=>{
                done();
                instance.confirmButtonLoading = false;
              }).catch(() => {
                that.issuedLoading = false;
                instance.confirmButtonText = '确定';
                instance.confirmButtonLoading = false;
              });
            } else {
              done();
            }
          }
        }).then(() => {
          that.issuedLoading = false;
          strategyParameterComRef.value.queryStrategyById();
          strategyControlListComRef.value.findStrategyList();
          ElMessage({ type: "success", showClose: true, message: "下发成功！" });
        }).catch(() => {
          console.log("取消下发！");
        });
      }

      if (operateType === 3) {
        that.conditionMonitoringVisible = true;
      }

      if (operateType === 4) {
        const routeName = "/centralMonitoring/EMControlLogs";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if (!isAuthority) {
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return;
        }
        emit("changeEvent", {
          operateType: "switchComponents",
          componentName: "EMControlLogs",
          siteId: props.activeSiteId
        });
      }
    };

    const strategyControlListComRef = ref(null);
    const changeEvent = (data)=>{
      if(data.operateType === "deleteStrategy")strategyControlListComRef.value.findStrategyList();
    };

    const watchActiveSiteId = watch(() => props.activeSiteId, (newActiveSiteId) => {
      that.controlIdArray = [];
      that.activeDeviceInfo = {};
      if (newActiveSiteId) queryGatewayDataBySiteId();
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchActiveSiteId, queryGatewayDataBySiteId, clickOperateBut, deviceIdChangeFun, changeEvent, strategyControlListComRef,
      issueStrategyTime, strategyName, strategyParameterComRef};
  }
});
</script>

<style lang="scss" scoped>
.app-container-right {

  .header-form-right {
    width: 100%;

    .updateTimeText {
      font-size: 14px;
      color: rgba(211, 236, 251, 1);
      margin-right: 10px;
    }
  }

  .tableContent {
    flex-direction: initial;
  }
}
</style>