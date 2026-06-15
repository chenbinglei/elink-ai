<template>
  <TitleView title="通道信息">
    <template #content>
      <div class="directConnectionComponent" v-loading="listLoading">
        <div class="table_list">
          <div class="table_list_left">协议类型</div>
          <div class="table_list_right">{{ $filters.moreData(deviceChannel.protocolType) }}</div>
        </div>
        <div class="table_list">
          <div class="table_list_left">接入协议</div>
          <div class="table_list_right">{{ $filters.moreData(deviceChannel.accessProtocol) }}</div>
        </div>
        <div class="table_list">
          <div class="table_list_left">设备状态</div>
          <div class="table_list_right">
            <span class="deviceStatus" :class="'deviceStatus' + deviceInfo.txStatus">{{ $filters.deviceStatus(deviceInfo.txStatus) }}</span>
            <el-button size="small" @click="clickStatusBut" :type="!deviceInfo.txStatus ? 'primary' : 'warning'">
              <span>{{ !deviceInfo.txStatus ? '注册' : '注销' }}</span>
            </el-button>
          </div>
        </div>
      </div>
    </template>
  </TitleView>
</template>

<script lang="ts">
import {ElMessage, ElMessageBox} from "element-plus";
import {reactive, toRefs, watch, defineComponent, getCurrentInstance} from "vue";
import {findAccessDetailByDeviceId, updateDeviceStatus} from "@/api/deviceCenter/deviceAccess";

export default defineComponent({
  name: "DirectConnectionComponent",
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();
    const that = reactive({
      deviceInfo:{},
      deviceChannel:{},
      listLoading: false,
    })

    // 根据设备id查询设备接入详情
    const queryAccessDetailByDeviceId = ()=>{
      that.listLoading = true;
      findAccessDetailByDeviceId({ deviceId:props.activeDeviceId }).then(res=>{
        that.deviceInfo = res.data;
        that.deviceChannel = res.data.dasChannel ? res.data.dasChannel : {};
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      })
    }

    const clickStatusBut = ()=>{
      ElMessageBox.confirm(`确定${ that.deviceInfo.txStatus ? '注销' : '注册' }当前设备吗？`, "提示", {
        dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
        customClass: "elMessageBoxWarning", showClose:false,type: 'warning',closeOnClickModal: false,
        beforeClose: (action, instance, done)=>{
          if (action === 'confirm') {
            // 设备注册注销
            instance.confirmButtonLoading = true;
            let updateType = that.deviceInfo.txStatus ? 2 : 1;
            updateDeviceStatus({ deviceId:props.activeDeviceId,updateType: updateType }).then(()=>{
              done();
              instance.confirmButtonLoading = false;
            }).catch(() => {
              instance.confirmButtonLoading = false;
            });
          } else {
            done();
          }
        }
      }).then(() => {
        queryAccessDetailByDeviceId();
        emit("changeEvent",{ operateType: "registerDevice" });
        ElMessage({ type: "success", showClose: true, message: "操作成功！" });
      }).catch(() => {
        console.log("取消删除！");
      });
    }

    const watchDeviceId = watch(() => props.activeDeviceId, (newActiveDeviceId) => {
      queryAccessDetailByDeviceId();
    }, {deep: true, immediate: true})

    return { ...toRefs(that), watchDeviceId, queryAccessDetailByDeviceId,clickStatusBut }
  }
})
</script>

<style scoped lang="scss">
.directConnectionComponent{
  width: 100%;
  border: 1px solid #DBDBDD;
  box-sizing: border-box;

  .table_list{
    height: 32px;
    display: flex;
    border-bottom: 1px solid #DBDBDD;

    .table_list_left{
      width: 112px;
      height: 100%;
      background: #E8F2FF;
      text-align: center;
      line-height: 32px;
      font-size: 12px;
      color: #242424;
    }

    .table_list_right{
      flex: 1;
      padding: 0 16px;
      box-sizing: border-box;
      display: flex;
      align-items: center;

      .deviceStatus{
        margin-right: 12px;
        color: #BBBBBB;
      }

      .deviceStatus1{
        color: #41CB4A;
      }

      .deviceStatus3{
        color: #FD393A;
      }
    }

    &:last-child{
      border-bottom: none;
    }
  }
}
</style>
