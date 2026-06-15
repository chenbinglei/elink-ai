<template>
  <div class="secondaryDetailsCom">
    <div class="content_back_header">
      <div class="back_button flex-jc-ai-center" @click="clickBackButtonFun">
        <span class="iconfont icon-zuodanjiantou"></span>
      </div>
      <div class="back_title">
        <template v-if="secondaryDetailsInfo.componentName === 'CsStationDetails'">
          <el-select v-model="siteRecordId" filterable placeholder="请选择" @change="siteIdChangeFun">
            <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"/>
          </el-select>
        </template>
        <template v-else>{{ secondaryDetailsInfo.subTitle }}</template>
      </div>
    </div>
    <div :class="{content_details_border: isNoContentBorderList.indexOf(secondaryDetailsInfo.componentName) === -1 }" class="content_details">
      <component :is="secondaryDetailsInfo.componentName" :authority="authority" :routeInfo="secondaryDetailsInfo" :siteId="siteId" @changEvent="changEvent"></component>
    </div>
  </div>
</template>

<script lang="ts">
import { useOperationManagementStore } from '@/stores/index';

import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import {computed, defineComponent, getCurrentInstance, onMounted, reactive, toRefs} from "vue";
import {CsChargingPileDetails, CsStationDetails, CsOrderRecordDetails, CsFeedbackDetails, CsMiniProgramUserDetails} from "./SecondaryDetailsCom/index";

export default defineComponent({
  name: "SecondaryDetailsCom",
  components: {CsChargingPileDetails, CsStationDetails, CsOrderRecordDetails, CsFeedbackDetails, CsMiniProgramUserDetails},
  setup() {
    const operationManagementStore = useOperationManagementStore();
    const {emit} = getCurrentInstance();

    const secondaryDetailsInfo = computed(() => {
      console.log(operationManagementStore.secondaryDetailsInfo,'***');
      
      return operationManagementStore.secondaryDetailsInfo;
    });

    const that = reactive({
      siteId: "",  // 详情子组件使用的
      authority: 1, //权限 1-只读 2-读写
      siteIdArray: [],
      siteRecordId: "", // 下拉选择使用的
      isNoContentBorderList: ["CsFeedbackDetails", "CsMiniProgramUserDetails"]
    });

    const clickBackButtonFun = () => {
      operationManagementStore.updateSecondaryVisible(false);
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      
      findSiteInfoByUserId({scenarioTypes: "3",timer: new Date()}).then(res => {
        let list = res.data ? res.data : [];
        that.siteIdArray = JSON.parse(JSON.stringify(list));
        console.log(that.siteIdArray,'***');
        
        querySiteAuthorityFun();
      }).catch(() => {
        that.authority = 1;
      });
    };

    // 切换站点
    const siteIdChangeFun = ()=>{
      that.siteId = that.siteRecordId;
      querySiteAuthorityFun();
    };

    // 查询站点权限
    const querySiteAuthorityFun = () => {
      let authority = 1;
      let findItem = that.siteIdArray.find(item => item.id === that.siteId);
      if (findItem) authority = findItem.authority;
      that.authority = authority;
    };

    const changEvent = (data)=>{
      emit("changEvent",data);
    };

    onMounted(() => {
      // 站点详情获取 站点列表
      if (secondaryDetailsInfo.value.componentName === 'CsStationDetails') {
        that.siteRecordId = secondaryDetailsInfo.value.id;
        that.siteId = secondaryDetailsInfo.value.id;
        querySiteBasicInfoByTenantId();
      }
    });

    return {...toRefs(that), secondaryDetailsInfo, clickBackButtonFun, querySiteBasicInfoByTenantId, querySiteAuthorityFun, changEvent, siteIdChangeFun};
  }
});

</script>

<style lang="scss" scoped>
.secondaryDetailsCom {
  height: 100%;
  display: flex;
  flex-direction: column;

  .content_back_header {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    color: #ffffff;
    cursor: pointer;

    .back_button {
      width: 28px;
      height: 28px;
      color: #0085FF;
      font-size: 21px;
      border-radius: 50%;
      background: #00a3ff4d;
      margin-right: 12px;
    }

    .back_title {
      min-width: 240px;
    }
  }

  .content_details {
    flex: 1;
    height: 2px;
    overflow-y: auto;
  }

  .content_details_border {
    padding: 12px;
    border-radius: 6px;
    box-sizing: border-box;
    border: 1px solid #106ec499;
  }
}
</style>