<template>
  <div class="content_body" v-resize="setTableMaxHeight">
    <div class="header-form" ref="headerFormRef">
      <el-form :model="formInline" inline>
        <el-form-item label="关键字：">
          <el-input v-model="formInline.keyword" clearable placeholder="请输入关键词搜索">
            <template #append>
              <el-button :icon="Search" @click="queryGatewaySubDeviceList"></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button class="whiteFontButtons" @click="clickAssociationBut">关联设备</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="tableContent">
      <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" border stripe>
        <el-table-column align="center" label="序号" type="index" width="80"></el-table-column>
        <el-table-column align="center" label="设备名称">
          <template #default="{ row }">{{ $filters.moreData(row.deviceName)}}</template>
        </el-table-column>
        <el-table-column align="center" label="设备序列号">
          <template #default="{ row }">{{ $filters.moreData(row.deviceNumber)}}</template>
        </el-table-column>
        <el-table-column align="center" label="设备ID">
          <template #default="{ row }">{{ $filters.moreData(row.deviceId)}}</template>
        </el-table-column>
        <el-table-column align="center" label="状态">
          <template #default="{ row }">
            <span class="deviceStatus" :class="'deviceStatus' + row.txStatus">{{ $filters.deviceStatus(row.txStatus) }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作">
          <template #default="{ row }">
            <el-link :underline="false" @click="clickOperateBut(1, row)">移出</el-link>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <AssociatedDevicesDialog v-if="associatedDevicesVisible" v-model:isVisible="associatedDevicesVisible" :activeDeviceId="activeDeviceId" @changeEvent="queryGatewaySubDeviceList" />
  </div>
</template>

<script>
import { useAppStore } from '@/stores/index';

import {AssociatedDevicesDialog} from "./component";
import {ElMessage, ElMessageBox} from "element-plus";
import {CirclePlus, Delete, Refresh, Search} from "@element-plus/icons-vue";
import {computed, onMounted, reactive, ref, toRefs, defineComponent} from "vue";
import {findGatewaySubDeviceList,batchUpdateGatewaySubDevice} from "@/api/deviceCenter/deviceList";

export default defineComponent({
  name: "GatewayChildDevice",
  components: {AssociatedDevicesDialog},
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
      list: [],
      pageNum: 20,
      formInline: {},
      listLoading: false, // 表格加载
      tableMaxHeight: 300,

      associatedDevicesVisible: false,
      CirclePlus, Delete, Refresh, Search,
    })

    // 根据网关id查询网关子设备列表
    const queryGatewaySubDeviceList = ()=>{
      that.listLoading = true;
      findGatewaySubDeviceList({ gatewayId: props.activeDeviceId,...that.formInline }).then(res=>{
        that.list = res.data;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const clickAssociationBut = ()=>{
      that.associatedDevicesVisible = true;
    }

    const clickOperateBut = (operateType,row)=>{
      if(operateType === 1){
        ElMessageBox.confirm(`确定移出当前设备（<spam class="highlightText">${ row.deviceName }</spam>）吗？`, "提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "elMessageBoxWarning", showClose:false,type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在移出...';
              batchUpdateGatewaySubDevice({ gatewayId: props.activeDeviceId,type: 2,subDeviceIds:[row.deviceId] }).then(()=>{
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
        }).then(()=>{
          queryGatewaySubDeviceList();
          ElMessage({ type: "success", showClose: true, message: "操作成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = contentMainMaxHeight.value - headerFormHeight;
    }

    onMounted(()=>{
      queryGatewaySubDeviceList();
    })

    return {...toRefs(that), setTableMaxHeight, queryGatewaySubDeviceList, contentMainMaxHeight, headerFormRef, clickAssociationBut, clickOperateBut}

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

    .deviceStatus{
      color: #979797;
    }
    .deviceStatus1{
      color: #199D7C;
    }
    .deviceStatus2{
      color: #FF9C02;
    }
    .deviceStatus3{
      color: #F3615B;
    }
    .deviceStatus88{
      color: #A1A1A1;
    }
  }
}
</style>
