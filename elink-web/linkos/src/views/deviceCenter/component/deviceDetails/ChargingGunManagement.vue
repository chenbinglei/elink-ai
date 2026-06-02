<template>
  <div class="content_body">
    <div class="header-form" ref="headerFormRef">
      <el-button :icon="DocumentCopy" class="blackFontButtons" @click="clickAddButFun(1)">克隆枪数据</el-button>
      <el-button :icon="CirclePlus" class="whiteFontButtons" @click="clickAddButFun(2)">添加充电枪</el-button>
    </div>
    <div  class="tableContent" v-resize="setTableMaxHeight">
      <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" border stripe>
        <el-table-column align="center" label="序号" type="index" width="80" fixed="left"></el-table-column>
        <el-table-column align="center" label="枪名称" fixed="left" width="180">
          <template #default="{ row }">{{ $filters.moreData(row.gunName) }}</template>
        </el-table-column>
        <el-table-column align="center" label="枪编号" fixed="left" width="120">
          <template #default="{ row }">{{ $filters.moreData(row.gunCode) }}</template>
        </el-table-column>
        <el-table-column align="center" label="枪型号" width="210">
          <template #default="{ row }">{{ $filters.chargingGunType(row.type) }}</template>
        </el-table-column>
        <el-table-column align="center" label="外观" show-overflow-tooltip width="210">
          <template #default="{ row }">{{ $filters.moreData(row.appearance) }}</template>
        </el-table-column>
        <el-table-column align="center" label="额定功率（kW）" width="210">
          <template #default="{ row }">{{ $filters.moreData(row.ratedPower) }}</template>
        </el-table-column>
        <el-table-column align="center" label="额定电流（A）" width="210">
          <template #default="{ row }">{{ $filters.moreData(row.ratedCurrent) }}</template>
        </el-table-column>
        <el-table-column align="center" label="额定电压上限（V）" width="210">
          <template #default="{ row }">{{ $filters.moreData(row.voltageUpperLimits) }}</template>
        </el-table-column>
        <el-table-column align="center" label="额定电压下限（V）" width="210">
          <template #default="{ row }">{{ $filters.moreData(row.voltageLowerLimits) }}</template>
        </el-table-column>
        <el-table-column align="center" label="车位号" width="210">
          <template #default="{ row }">{{ $filters.moreData(row.parkNo) }}</template>
        </el-table-column>
        <el-table-column align="center" label="国家标准" width="210">
          <template #default="{ row }">{{ $filters.nationalStandard(row.nationalStandard) }}</template>
        </el-table-column>
        <el-table-column align="center" label="防护等级" width="210">
          <template #default="{ row }">{{ $filters.moreData(row.ipGrade) }}</template>
        </el-table-column>
        <el-table-column align="center" label="辅助电源" width="210">
          <template #default="{ row }">{{ $filters.auxPower(row.auxPower) }}</template>
        </el-table-column>
        <el-table-column align="center" label="二维码解析地址" width="240" show-overflow-tooltip>
          <template #default="{ row }">{{ $filters.moreData(row.qrCodes) }}</template>
        </el-table-column>
        <el-table-column align="center" label="充电设备接口唯一码" width="210">
          <template #default="{ row }">{{ $filters.moreData(row.connectorUniqueId) }}</template>
        </el-table-column>
        <el-table-column align="center" label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
            <span class="split_line">|</span>
            <el-link :underline="false" type="danger" @click="clickOperateBut(2, row)">删除</el-link>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <CloneChargingGunDialog v-if="cloneChargingGunVisible" v-model:isVisible="cloneChargingGunVisible" :activeDeviceId="activeDeviceId"></CloneChargingGunDialog>
    <AddChargingGunDialog v-if="addChargingGunVisible" v-model:isVisible="addChargingGunVisible" :titleName="titleName" :formDialog="formDialog" @changeEvent="queryDeviceGunListByDeviceId"/>
  </div>
</template>

<script>
import {useStore} from "vuex";
import {ElMessage, ElMessageBox} from "element-plus";
import {CirclePlus,DocumentCopy} from "@element-plus/icons-vue";
import {AddChargingGunDialog,CloneChargingGunDialog} from "./component";
import {onMounted, reactive, ref, toRefs, defineComponent, computed} from "vue";
import {deleteAllDeviceGun, findDeviceGunListByDeviceId} from "@/api/deviceCenter/deviceDetails";

export default defineComponent({
  name:"ChargingGunManagement",
  components: {AddChargingGunDialog,CloneChargingGunDialog},
  props: {
    activeDeviceId: {
      type: [String, Number],
      default: ''
    }
  },
  setup(props){
    const store = useStore();
    const contentMainMaxHeight = computed(() => {
      return store.state.app.contentMainMaxHeight;
    });

    const that = reactive({
      list: [],
      CirclePlus,
      DocumentCopy,
      formInline: {},
      listLoading: false, // 表格加载
      tableMaxHeight: 300,

      formDialog: {},
      titleName: "添加充电枪",
      addChargingGunVisible: false,
      cloneChargingGunVisible: false,
    })


    // 根据模型id查询模型拓扑节点列表
    const queryDeviceGunListByDeviceId = ()=>{
      that.listLoading = true;
      findDeviceGunListByDeviceId({ deviceId: props.activeDeviceId,...that.formInline }).then(res=>{
        that.list = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const clickAddButFun = (operateType) => {
      if(operateType === 1){
        if(!that.list || !that.list.length){
          ElMessage({ type: "warning",showClose: true,message: "请先添加枪数据！" });
          return
        }
        that.cloneChargingGunVisible = true;
      }

      if(operateType === 2){
        that.titleName = "添加充电枪";
        that.formDialog = { deviceId: props.activeDeviceId };
        that.addChargingGunVisible = true;
      }
    }

    const clickOperateBut = (operate,row)=>{

      if(operate === 1){
        that.titleName = "编辑充电枪";
        that.formDialog = JSON.parse(JSON.stringify(row));
        that.addChargingGunVisible = true;
      }

      if(operate === 2){
        ElMessageBox.confirm(`您确定要删除（<span class="deleteName">${row.gunName}</span>）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteAllDeviceGun({ deviceGunIds: [row.id] }).then(()=> {
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
          queryDeviceGunListByDeviceId();
          ElMessage({ type:"success",showClose: true,message:"操作成功！" });
        }).catch(() => {
          console.log("取消删除");
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
      queryDeviceGunListByDeviceId()
    })

    return { ...toRefs(that), headerFormRef, setTableMaxHeight, clickAddButFun, queryDeviceGunListByDeviceId, clickOperateBut, contentMainMaxHeight}
  }
})
</script>

<style lang="scss" scoped>
.content_body{
  height: 100%;
  box-sizing: border-box;
  padding: 12px 12px 0 12px;
  display: flex;
  flex-direction: column;

  .header-form{
    display: flex;
    justify-content: flex-end;
    padding-bottom: 12px;
    box-sizing: border-box;
  }

  .tableContent{
    padding: 0 !important;
  }
}
</style>