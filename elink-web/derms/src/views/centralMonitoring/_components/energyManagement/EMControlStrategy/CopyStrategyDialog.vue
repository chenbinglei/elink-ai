<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="580" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="控制策略：">
            <el-input v-model="strategyName" placeholder="请输入控制策略" disabled/>
          </el-form-item>
          <el-form-item label="目标站点：" prop="siteId">
            <el-select v-model="formDialog.siteId" clearable placeholder="请选择目标站点" @change="queryGatewayDataBySiteId">
              <template v-for="item in siteIdArray" :key="item.id">
                <template v-if="item.id !== formDialog.activeSiteId">
                  <el-option :label="item.siteName" :value="item.id"></el-option>
                </template>
              </template>
            </el-select>
          </el-form-item>
          <el-form-item label="控制节点：" prop="deviceId">
            <el-select v-model="formDialog.deviceId" clearable placeholder="请选择控制节点" :disabled="!formDialog.siteId">
              <el-option v-for="item in deviceIdArray" :key="item.id" :label="item.deviceName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import { useEnergyManagementStore } from '@/stores/index';

import {ElMessage} from "element-plus";
import {findSiteListByUserId} from "@/api/centralMonitoring/centralMonitoring";
import {cloneStrategy, findGatewayDataBySiteId} from "@/api/centralMonitoring/energyManagement";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted, computed} from "vue";

export default defineComponent({
  name: "CopyStrategyDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeEditInfo: {
      type: Object,
      default: ()=>{
        return { };
      }
    },
  },
  setup(props){
    const energyManagementStore = useEnergyManagementStore();
    const {emit} = getCurrentInstance();
    const strategyName = computed(() => {
      return energyManagementStore.strategyName;
    });

    const validateSiteId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择目标站点"));
      } else {
        callback();
      }
    };

    const validateDeviceId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择控制节点"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      titleName: "复制策略",
      listLoading: false,

      siteIdArray: [],
      deviceIdArray: [],
      dialog_visible: props.isVisible,
      rules:{
        siteId: [{ required: true, trigger: "change", validator: validateSiteId }],
        deviceId: [{ required: true, trigger: "change", validator: validateDeviceId }],
      }
    });

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          // that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          let findItem = that.deviceIdArray.find(item => item.id === formDialog.deviceId);
          formDialog.deviceId = findItem.deviceId ? findItem.deviceId : "";
          // console.log(formDialog);
          cloneStrategy({...formDialog}).then(()=>{
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const initParamConfigFun = ()=>{
      that.formDialog = Object.assign({},that.formDialog,props.activeEditInfo);
      querySiteListByUserId();
    };

    // 查询站点列表
    const querySiteListByUserId = ()=>{
      findSiteListByUserId({ timer: new Date() }).then(res => {
        that.siteIdArray = res.data ? res.data : [];
        that.loading = false;
      }).catch(()=>{
        that.loading = false;
      });
    };

    // 根据站点id查询网关数据
    const queryGatewayDataBySiteId = () => {
      that.formDialog.deviceId = "";
      let formDialog = JSON.parse(JSON.stringify(that.formDialog));
      findGatewayDataBySiteId({...formDialog}).then(res => {
        let deviceIdArray = res.data ? res.data : [];
        deviceIdArray.forEach(item=>{
          item.deviceId = item.id; // 新增设备iD
          item.id = item.id || item.deviceNumber;
        });
        that.deviceIdArray = JSON.parse(JSON.stringify(deviceIdArray));
      });
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, initParamConfigFun, formDialogRef, strategyName, querySiteListByUserId,
      queryGatewayDataBySiteId};

  }
});
</script>

<style scoped lang="scss">

</style>