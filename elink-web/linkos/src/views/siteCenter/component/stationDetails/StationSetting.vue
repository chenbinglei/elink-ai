<template>
  <div class="tableContent scrollbarStyle">
    <template v-for="(item,index) in list" :key="index">
      <TitleView :title="item.name">
        <template #headerRight>
          <el-button v-if="item.isEditBut" :icon="Edit" class="blackFontButtons" @click="clickEditButton(item.fieldName)">编辑</el-button>
<!--          <el-button v-if="item.isRefreshBut" :icon="RefreshRight" class="blackFontButtons" @click="querySiteSetUpBySiteId">刷新</el-button>-->
        </template>
        <template #content>
          <div class="content_body" v-loading="listLoading">
            <component ref="componentRef" :is="item.componentName" :returnDataInfo="returnDataInfo" @changEvent="querySiteSetUpBySiteId"></component>
          </div>
        </template>
      </TitleView>
    </template>
  </div>
</template>

<script>
import {Edit,RefreshRight} from "@element-plus/icons-vue";
import OtherSetting from "./StationSetting/OtherSetting.vue";
import SecuritySetting from "./StationSetting/SecuritySetting.vue";
import SiteAppFunConfig from "./StationSetting/SiteAppFunConfig.vue";
import PvFunctionConfig from "./StationSetting/PvFunctionConfig.vue";
import {onMounted, reactive, toRefs, defineComponent, ref} from "vue";
import {findSiteSetUpBySiteId} from "@/api/siteCenter/stationDetails";

export default defineComponent({
  name: "StationSetting",
  components: {SecuritySetting, OtherSetting, SiteAppFunConfig, PvFunctionConfig},
  props: {
    siteRecordsId: {
      type: [String, Number],
      default: ""
    }
  },
  setup(props) {

    const that = reactive({
      Edit,
      RefreshRight,
      returnDataInfo: {},
      listLoading: false,
      list: [
        {
          name: "安全设置",
          isEditBut: true,
          fieldName: "SecuritySetting",
          componentName: "SecuritySetting"
        },
        {
          name: "其他设置",
          fieldName: "OtherSetting",
          componentName: "OtherSetting"
        },
        {
          isRefreshBut: true,
          name: "站点应用功能配置",
          fieldName: "SiteAppFunConfig",
          componentName: "SiteAppFunConfig"
        },
        {
          isRefreshBut: true,
          name: "光伏功能配置",
          fieldName: "PvFunctionConfig",
          componentName: "PvFunctionConfig"
        },
      ]
    })

    const componentRef = ref(null);
    const clickEditButton = (fieldName) => {
      if (fieldName === "SecuritySetting") {
        for(let i = 0;i < componentRef.value.length;i++){
          if(componentRef.value[i].clickEditButtonFun){
            componentRef.value[i].clickEditButtonFun();
          }
        }
      }
    }

    // 根据站点id查询站点设置
    const querySiteSetUpBySiteId = () => {
      that.listLoading = true;
      findSiteSetUpBySiteId({siteId:props.siteRecordsId}).then(res => {
        let returnDataInfo = res.data ? res.data : {};
        if(!returnDataInfo.appShow) returnDataInfo.appShow = 1;
        if(!returnDataInfo.siteId) returnDataInfo.siteId = props.siteRecordsId;
        if(!returnDataInfo.operatePassword) returnDataInfo.operatePassword = "0000"; // 如果站点没有密码 默认0000
        for(let key in returnDataInfo) if(!returnDataInfo[key] && returnDataInfo[key] !== 0) delete returnDataInfo[key];
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    onMounted(() => {
      querySiteSetUpBySiteId();
    })

    return {...toRefs(that), clickEditButton, querySiteSetUpBySiteId, componentRef}
  }
})
</script>

<style lang="scss" scoped>
</style>