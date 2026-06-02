<template>
  <div class="app-container fd-column">
    <div class="content_top">
      <Tabs :tabsArray="tabs_list" v-model:tabsIndex="componentName" isRightSlot>
        <template v-slot:rightButtonSlot>
          <template v-if="isEditCompany">
            <template v-if="componentName === 'CompanyInfo'"><el-button class="whiteFontButtons" @click="buttonClickFun">编 辑</el-button></template>
            <template v-if="componentName === 'Organization'">
              <el-button :icon="CirclePlus" class="whiteFontButtons" @click="buttonClickFun">新增组织</el-button>
            </template>
            <template v-if="componentName === 'ApplicationAuth'">
              <el-button class="whiteFontButtons" @click="buttonClickFun('allSave')">保存设置</el-button>
            </template>
            <template v-if="componentName === 'AssetAuthorization' ">
              <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="!isOperateStatus" @click="buttonClickFun">添加站点</el-button>
            </template>
            <template v-if="componentName === 'MerchantConfig'">
              <el-button :icon="CirclePlus" class="whiteFontButtons" @click="buttonClickFun">新增商户</el-button>
            </template>
          </template>
        </template>
      </Tabs>
    </div>
    <div class="app-container-right scrollbarStyle">
      <component :is="componentName" ref="componentRef" :tenantId="tenantId" :sourceType="sourceType" :isEditCompany="isEditCompany" @changEvent="changEvent"></component>
    </div>
  </div>
</template>

<script>
import CompanyInfo from "./CompanyInfo";
import Organization from "./Organization";
import MerchantConfig from "./MerchantConfig";
import ApplicationAuth from "./ApplicationAuth";
import {CirclePlus} from "@element-plus/icons-vue";
import AssetAuthorization from "./AssetAuthorization";
import {reactive, ref, toRefs, defineComponent, onMounted} from "vue";

export default defineComponent({
  name: "CompanyPublicComponent",
  components: { CompanyInfo, Organization, ApplicationAuth, AssetAuthorization, MerchantConfig },
  props:{
    tenantId: {
      type:[String,Number],
      default: ""
    },
    isEditCompany:{
      type: Boolean,
      default: false
    },
    // 1： 租户管理详情  2： 企业信息
    sourceType:{
      type: Number,
      default: 1
    }
  },
  setup(props) {

    const componentRef = ref(null);
    const that = reactive({
      CirclePlus,
      isOperateStatus: false, // 页面是否可操作 （企业信息的 资产授权按钮）
      componentName: "CompanyInfo",
      tabs_list: [
        {name: "企业概况", id: "CompanyInfo"},
        {name: "组织架构", id: "Organization"},
        {name: "应用授权", id: "ApplicationAuth"},
        {name: "资产授权", id: "AssetAuthorization"},
        {name: "账户信息", id: "MerchantConfig"},
      ]
    });

    // 按钮操作
    const buttonClickFun = (type) => {
      componentRef.value.clickButtonFun(type);
    }

    const changEvent = (data)=>{
      if(data.type === "AssetAuthorization"){
        that.isOperateStatus = data.isOperateStatus;
      }
    }

    onMounted(()=>{})

    return {...toRefs(that), buttonClickFun , componentRef, changEvent}
  },
})
</script>

<style lang="scss" scoped>
.content_top{
  box-sizing: border-box;

  :deep(.tabs_list){
    padding: 0 !important;
  }
}
</style>
