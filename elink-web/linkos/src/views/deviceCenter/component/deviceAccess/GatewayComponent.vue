<template>
  <TitleView title="通道信息" isContentHeight>
    <template #headerRight>
      <template v-if="!deviceInfo.txStatus">
        <el-button class="blackFontButtons" @click="clickRegisterBut(1)">注册设备</el-button>
      </template>
      <template v-else>
        <el-button class="blackFontButtons" @click="clickRegisterBut(2)">注销设备</el-button>
      </template>
      <el-button class="whiteFontButtons" :icon="CirclePlus" :disabled="isAddButtonClick" @click="clickAddChannel">新建通道</el-button>
    </template>
    <template #content>
      <div class="gatewayComponent" v-loading="listLoading">
        <template v-if="deviceChannelList && deviceChannelList.length">
          <TabActiveBorder ref="tabActiveBorderRef" :tabsArray="deviceChannelList" v-model:tabsIndex="activeChannelId" @changeEvent="changeEvent"></TabActiveBorder>
          <GatewayChannelComponent :activeDeviceId="activeDeviceId" :activeChannelInfo="activeChannelInfo" @changeEvent="queryChannelInfoListByDeviceId"></GatewayChannelComponent>
          <PointNumberConfig :activeDeviceId="activeDeviceId" :activeDeviceName="deviceInfo.deviceName" :activeChannelName="activeChannelName"
                             :activeChannelId="activeChannelId" @changeEvent="changeEvent"></PointNumberConfig>
        </template>
        <template v-else>
          <div class="flex-jc-ai-center maxHeight">
            <null-data words="未配置通道"></null-data>
          </div>
        </template>

        <AddChannelDialog v-if="addChannelVisible" v-model:isVisible="addChannelVisible" :titleName="titleName" :activeDeviceId="activeDeviceId" :channelInfo="channelInfo"
                          :editStatus="editStatus" @changeEvent="queryChannelInfoListByDeviceId"></AddChannelDialog>
      </div>
    </template>
  </TitleView>
</template>

<script>
import {operateButtonIsClick} from "@/utils";
import AddChannelDialog from "./AddChannelDialog";
import {CirclePlus} from '@element-plus/icons-vue';
import PointNumberConfig from "./PointNumberConfig";
import {ElMessage, ElMessageBox} from "element-plus";
import TabActiveBorder from "@/components/Tabs/TabActiveBorder";
import GatewayChannelComponent from "./GatewayChannelComponent";
import {computed, getCurrentInstance, nextTick, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {findChannelInfoListByDeviceId, updateDeviceStatus, deleteChannelById} from "@/api/deviceCenter/deviceAccess";

export default defineComponent({
  name: "GatewayComponent",
  components:{AddChannelDialog,TabActiveBorder,GatewayChannelComponent,PointNumberConfig},
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    },
    deviceInfo: {
      type: Object,
      default: ()=>{
        return { }
      }
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();
    const tabActiveBorderRef = ref(null);

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/device/access/saveChannel')
    })

    const that = reactive({
      CirclePlus,
      channelInfo: {},
      editStatus: false,
      listLoading: false,
      titleName: "新增通道",
      activeChannelId: "",
      activeChannelName:"",
      activeChannelInfo:{},
      deviceChannelList: [],
      addChannelVisible: false,
    })

    // 根据设备id查询设备通道信息列表
    const queryChannelInfoListByDeviceId = ()=>{
      that.listLoading = true;
      findChannelInfoListByDeviceId({ deviceId:props.activeDeviceId }).then(res=>{
        that.deviceChannelList = res.data;
        if(that.deviceChannelList && that.deviceChannelList.length){
          nextTick(()=>{
            let findItem = that.deviceChannelList.find(item=> item.id === that.activeChannelId);
            if(!findItem) that.activeChannelId = that.deviceChannelList[0].id; // 查看是否有当前通道信息
            tabActiveBorderRef.value.selectIndex(that.activeChannelId);
          })
        }
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      })
    }

    const clickAddChannel = ()=>{

      if(!props.deviceInfo.txStatus){
        ElMessage({ type: "error", showClose: true, message: "请先注册当前设备！" });
        return
      }

      that.channelInfo = {};
      that.editStatus = false;
      that.titleName = "新增通道";
      that.addChannelVisible = true;
    }

    const changeEvent = (data)=>{

      if(data.type === "selectIndex"){
        that.activeChannelInfo = data;
        that.activeChannelId = data.id;
        that.activeChannelName = data.channelName;
      }

      if(data.type === "channelEdit"){
        that.editStatus = true;
        that.titleName = "编辑通道";
        that.channelInfo = JSON.parse(JSON.stringify(that.activeChannelInfo));
        that.addChannelVisible = true;
      }

      if(data.type === "clickClose"){
        ElMessageBox.confirm(`确定删除当前通道（${ data.channelName }）吗？`, "提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "elMessageBoxWarning", showClose:false,type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteChannelById({ deviceId: props.activeDeviceId,channelId: that.activeChannelId }).then(()=>{
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
          queryChannelInfoListByDeviceId();
          ElMessage({ type: "success", showClose: true, message: "操作成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    // 注册设备
    const clickRegisterBut = (updateType)=>{
      ElMessageBox.confirm(`确定<span class="highlightText">${ updateType === 1 ? '注册' : '注销' }</span>当前设备（<span class="highlightText">${ props.deviceInfo.deviceName }</span>）吗？`,
          "提示", { dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', showClose:false,closeOnClickModal: false,
        beforeClose: (action, instance, done)=>{
          if (action === 'confirm') {
            // 设备注册注销
            instance.confirmButtonLoading = true;
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
        emit("changeEvent",{ operateType: "registerDevice" });
        ElMessage({ type: "success", showClose: true, message: "操作成功！" });
      }).catch(() => {
        console.log("取消删除！");
      });
    }

    const watchDeviceId = watch(() => props.activeDeviceId, (newActiveDeviceId) => {
      queryChannelInfoListByDeviceId();
    }, {deep: true, immediate: true})

    return { ...toRefs(that), watchDeviceId, queryChannelInfoListByDeviceId, clickAddChannel, tabActiveBorderRef, changeEvent, clickRegisterBut, isAddButtonClick }
  }
})
</script>

<style scoped lang="scss">
.gatewayComponent{
  height: 100%;
  .maxHeight{
    height: 100%;
  }
}
</style>
