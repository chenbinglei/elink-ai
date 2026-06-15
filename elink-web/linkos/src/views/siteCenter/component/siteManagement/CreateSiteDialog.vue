<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="920" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="站点名称：" prop="siteName">
                <el-input v-model="formDialog.siteName" maxlength="32" placeholder="请输入站点名称" show-word-limit/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="站点编码：" prop="siteCode">
                <el-input v-model="formDialog.siteCode" placeholder="请输入站点编码"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="站点状态：" prop="siteStatus">
                <el-select v-model="formDialog.siteStatus" placeholder="请选择站点状态">
                  <el-option v-for="(item,index) in siteStatusArray" :key="index" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <!--            <template v-if="!activeSiteId"></template>-->
            <el-col :span="12">
              <el-form-item label="运营单位：" prop="operatorId">
                <el-select v-model="formDialog.operatorId" placeholder="请选择运营单位">
                  <el-option v-for="(item,index) in tenantIdArray" :key="index" :label="item.tenantName" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="产权单位：" prop="propertyId">
                <el-select v-model="formDialog.propertyId" placeholder="请选择产权单位">
                  <el-option v-for="(item,index) in tenantIdArray" :key="index" :label="item.tenantName" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="选择模型：" prop="siteModelId">
                <el-select v-model="formDialog.siteModelId" placeholder="请选择选择模型" :disabled="!!activeSiteId" @change="changeModelIdFun()">
                  <el-option v-for="item in modelArray" :key="item.id" :label="item.modelName" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="!activeSiteId">
              <el-form-item label="能源场景：">
                <el-select v-model="formDialog.scenarioTypes" filterable multiple collapse-tags placeholder="请选择能源场景" @change="changeScenarioTypesFun">
                  <el-option v-for="(item,index) in scenarioTypesArray" :key="index" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <!--            只有管理员才可以选择-->
            <el-col :span="12" v-if="userInfo.userRole === 1">
              <el-form-item label="所属租户：" prop="tenantId">
                <el-select v-model="formDialog.tenantId" filterable placeholder="请选择所属租户">
                  <el-option v-for="(item,index) in tenantIdArray" :key="index" :label="item.tenantName" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <template v-for="(item,index) in formDialog.siteDynamicFieldList" :key="index">
              <el-col :span="12" v-if="item.scenarioType">
                <el-form-item :label="queryScenarioTypeTextFun(item.scenarioType,2) + '：'" :prop="'siteDynamicFieldList.' + index + '.modelId'"
                              :rules="{required: true,message: `请选择${ queryScenarioTypeTextFun(item.scenarioType,2) }`,trigger: 'change'}">
                  <el-select v-model="item.modelId" filterable placeholder="请选择">
                    <el-option v-for="(item,index) in scenarioTypeConfig['modelArray' + item.scenarioType]" :key="index" :label="item.modelName" :value="item.id"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </template>
            <el-col :span="24">
              <el-form-item label="站点描述：">
                <el-input type="textarea" v-model="formDialog.siteDescribe" :rows="3" maxlength="200" placeholder="请输入站点描述" show-word-limit/>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div class="scrollbarStyle content_list">
          <template v-for="(item,index) in formDialog.siteDynamicFieldList" :key="index">
            <TitleView :title="queryScenarioTypeTextFun(item.scenarioType)">
              <template #content>
                <ModelDynamicFieldCom ref="modelDynamicFieldComRef" :newEditStatus="!!activeSiteId" :modelId="item.modelId" :scenarioType="item.scenarioType"></ModelDynamicFieldCom>
              </template>
            </TitleView>
          </template>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import { useAppStore } from '@/stores/index';

import {ElMessage} from "element-plus";
import {commonCharName} from "@/utils/validate";
import {site_status_array} from "@/utils/setVariate";
import {findSiteInfoById} from "@/api/siteCenter/stationDetails";
import {saveOrUpdateSiteInfo} from "@/api/siteCenter/siteManagement";
import {getModelNameListByTypeId} from "@/api/deviceCenter/deviceList";
import {findTenantInfoByPage} from "@/api/tenantManagement/tenantTabulation";
import ModelDynamicFieldCom from "@/views/siteCenter/component/ModelDynamicFieldCom.vue";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent, nextTick, computed} from "vue";

export default defineComponent({
  name: "CreateSiteDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加站点"
    },
    activeSiteId: {
      type: [String,Number],
      default: ""
    },
  },
  components:{ModelDynamicFieldCom},
  setup(props) {
    const appStore = useAppStore();
    const {emit} = getCurrentInstance();
    const userInfo = computed(() => {
      return appStore.userInfo;
    });

    const validateSiteName = (rule, value, callback) => {
      if (!value || !commonCharName(value)) {
        callback(new Error("请输入正确的站点名称"));
      } else {
        callback();
      }
    };

    const validateSiteStatus = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择站点状态"));
      } else {
        callback();
      }
    };

    const validateTenantId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择所属租户"));
      } else {
        callback();
      }
    };

    const validateTypeId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择设备分类"));
      } else {
        callback();
      }
    };

    const validateModelId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择模型"));
      } else {
        callback();
      }
    };

    const validateSiteCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择站点编码"));
      } else {
        callback();
      }
    };

    const validateOperatorId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择运营单位"));
      } else {
        callback();
      }
    };

    const validatePropertyId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择产权单位"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      scenarioTypeConfig: {},
      formDialog: { isDelete: 1 },// 动态字段列表
      dialog_visible: props.isVisible,

      modelArray: [],  // 站点模型
      tenantIdArray: [], // 所属租户
      siteStatusArray: site_status_array, //
      scenarioTypesArray: [{id: 1, name: "光伏",typeId: 4}, {id: 2, name: "储能",typeId: 5}, {id: 3, name: "电桩",typeId: 6}, {id: 4, name: "用能",typeId: 7}, {id: 5, name: "配电",typeId: 9999}, {id: 6, name: "换电",typeId: 68}],
      rules: {
        typeId: [{required: true, trigger: "change", validator: validateTypeId }],
        siteName: [{required: true, trigger: "change", validator: validateSiteName }],
        siteCode: [{required: true, trigger: "change", validator: validateSiteCode }],
        tenantId: [{required: true, trigger: "change", validator: validateTenantId }],
        siteModelId: [{required: true, trigger: "change", validator: validateModelId }],
        siteStatus: [{required: true, trigger: "change", validator: validateSiteStatus }],
        operatorId: [{required: true, trigger: "change", validator: validateOperatorId }],
        propertyId: [{required: true, trigger: "change", validator: validatePropertyId}],
      }
    })

    const formDialogRef = ref(null);
    const modelDynamicFieldComRef = ref([]);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          let promiseList = [];
          that.listLoading = true;
          if(modelDynamicFieldComRef.value && modelDynamicFieldComRef.value.length){
            for(let i = 0;i < modelDynamicFieldComRef.value.length;i++){
              let res = modelDynamicFieldComRef.value[i].formSubmitFun();
              promiseList.push(res);
            }
          }

          Promise.all(promiseList).then(siteScenarioTypeDtos=>{
            let formDialog = JSON.parse(JSON.stringify(that.formDialog));
            if(formDialog.scenarioTypes) formDialog.scenarioTypes = formDialog.scenarioTypes.join(',');
            let findIndex = siteScenarioTypeDtos.findIndex(item=> item.scenarioType === 0);
            if(findIndex !== -1){
              formDialog.siteReadwriteObject = JSON.parse(JSON.stringify(siteScenarioTypeDtos[findIndex].readwriteObject));
              siteScenarioTypeDtos.splice(findIndex,1);
            }
            delete formDialog.siteDynamicFieldList;
            let newSiteScenarioTypeDtos = [];
            // 编辑基本信息的时候，能源系统信息穿默认的
            if(props.activeSiteId){
              if(formDialog.siteScenarioTypeDtos && formDialog.siteScenarioTypeDtos.length){
                newSiteScenarioTypeDtos = JSON.parse(JSON.stringify(formDialog.siteScenarioTypeDtos));
              }
              delete formDialog.siteScenarioTypeDtos;
            }
            // 新增或编辑站点数据
            saveOrUpdateSiteInfo({ ...formDialog,siteScenarioTypeDtos: props.activeSiteId ? newSiteScenarioTypeDtos : siteScenarioTypeDtos }).then(()=>{
              emit("changeEvent");
              that.dialog_visible = false;
              ElMessage({ type: "success", showClose: true, message: "操作成功！" });
            }).catch(()=>{
              that.listLoading = false;
            })
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 选择模型发生改变  0: 基本扩展属性
    const changeModelIdFun = (config = { scenarioType: 0,typeId: 1 })=>{
      if(!that.formDialog.siteDynamicFieldList) that.formDialog.siteDynamicFieldList = [];
      let findIndex = that.formDialog.siteDynamicFieldList.findIndex(item=> item.scenarioType === config.scenarioType);
      if(config.scenarioType === 0) config.modelId = that.formDialog.siteModelId; // 扩展属性模型id
      if(findIndex === -1){
        that.formDialog.siteDynamicFieldList.push(config);
      } else {
        that.formDialog.siteDynamicFieldList[findIndex].modelId = config.modelId;
      }
      that.formDialog.siteDynamicFieldList = that.formDialog.siteDynamicFieldList.sort((a,b)=>{
        return a.scenarioType < b.scenarioType ? -1 : a.scenarioType > b.scenarioType ? 1 : 0;
      })
      // console.log(that.formDialog.siteDynamicFieldList);
    }

    // 选择 能源场景
    const changeScenarioTypesFun = ()=>{
      let scenarioTypes = [];
      if(that.formDialog.scenarioTypes)scenarioTypes = JSON.parse(JSON.stringify(that.formDialog.scenarioTypes));
      let siteDynamicFieldList = JSON.parse(JSON.stringify(that.formDialog?.siteDynamicFieldList ?? []));

      for(let i = 0;i < scenarioTypes.length;i++){
        let findIndex = siteDynamicFieldList.findIndex(item=> item.scenarioType === scenarioTypes[i]); // 查看当前是否已添加
        if(findIndex === -1){
          let findItem = that.scenarioTypesArray.find(item=> item.id === scenarioTypes[i]);
          changeModelIdFun({ scenarioType: scenarioTypes[i],typeId: findItem.typeId });
          queryModelNameListByTypeId({ scenarioType: scenarioTypes[i],typeId: findItem.typeId });
        }
      }

      // 删除已添加的能源场景
      for(let i = 0;i < siteDynamicFieldList.length;i++){
        // 过滤掉扩展属性字段
        if(siteDynamicFieldList[i].scenarioType){
          let findIndex = scenarioTypes.findIndex(item=> item === siteDynamicFieldList[i].scenarioType);
          if(findIndex === -1){
            delete that.scenarioTypeConfig['modelArray' + siteDynamicFieldList[i].scenarioType];
            that.formDialog.siteDynamicFieldList.splice(i,1);
          }
        }
      }
    }

    // 分页查询租户信息
    const queryTenantInfoByPage = () => {
      findTenantInfoByPage({page: 1, size: 0}).then(res => {
        that.tenantIdArray = res.data ? res.data : [];
      })
    }

    // 根据分类id获取模型名称列表
    const queryModelNameListByTypeId = (data = { scenarioType: 0,typeId: 1 })=>{
      getModelNameListByTypeId({ ...data, timer: new Date(), pageName: "CreateSiteDialog" }).then(res=>{
        let modelArray = res.data ? res.data : [];
        if(data.scenarioType === 0){
          that.modelArray = JSON.parse(JSON.stringify(modelArray));
        } else {
          that.scenarioTypeConfig['modelArray' + data.scenarioType] = JSON.parse(JSON.stringify(modelArray));
        }
      })
    }

    const queryScenarioTypeTextFun = (scenarioType,type = 1)=>{
      let afterText = type === 1 ? "属性" : "模型";
      switch (String(scenarioType)) {
        case "0":
          return `电站${ afterText }`;
        case "1":
          return `光伏系统${ afterText }`;
        case "2":
          return `储能系统${ afterText }`;
        case "3":
          return `电桩系统${ afterText }`;
        case "4":
          return `用能系统${ afterText }`;
        case "5":
          return `配电系统${ afterText }`;
        case "6":
          return `换电系统${ afterText }`;
        default:
          return `电站${ afterText }`;
      }
    }

    const initParamConfigFun = () => {
      queryModelNameListByTypeId(); // 根据分类id获取模型名称列表
      if(userInfo.value.userRole <= 1) queryTenantInfoByPage(); // 分页查询租户信息

      // 编辑站点基本信息
      if(props.activeSiteId){
        that.listLoading = true;
        // 根据站点id查询基本详情数据
        findSiteInfoById({id: props.activeSiteId,timer: new Date(),pageName: "CreateSiteDialog"}).then(res=>{
          let returnDataInfo = res.data ? res.data : {};
          if(returnDataInfo.scenarioTypes){
            returnDataInfo.scenarioTypes = returnDataInfo.scenarioTypes.split(',');
          }
          // 能源信息处理
          if(returnDataInfo.siteScenarioTypeDtos && returnDataInfo.siteScenarioTypeDtos.length){
            for(let i = 0;i < returnDataInfo.siteScenarioTypeDtos.length;i++){
              delete returnDataInfo.siteScenarioTypeDtos[i].reaList;
              if(returnDataInfo.siteScenarioTypeDtos[i].readwriteObject){
                returnDataInfo.siteScenarioTypeDtos[i].readwriteObject = JSON.parse(returnDataInfo.siteScenarioTypeDtos[i].readwriteObject);
              }
            }
          }
          delete returnDataInfo.siteReaList;
          that.formDialog = JSON.parse(JSON.stringify(returnDataInfo));
          // console.log(that.formDialog);
          changeModelIdFun(); // 选择模型发生改变
          nextTick(()=>{
            if(modelDynamicFieldComRef.value && modelDynamicFieldComRef.value.length){
              for(let i = 0;i < modelDynamicFieldComRef.value.length;i++){
                if(modelDynamicFieldComRef.value[i].scenarioType === 0){
                  if(returnDataInfo.siteReadwriteObject){
                    modelDynamicFieldComRef.value[i].formDialog = JSON.parse(returnDataInfo.siteReadwriteObject);
                    // console.log(modelDynamicFieldComRef.value[i].formDialog);
                  }
                }
              }
            }
            that.listLoading = false;
          })
        }).catch(()=>{
          that.listLoading = false;
        })
      }
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, queryTenantInfoByPage, queryModelNameListByTypeId,
      changeModelIdFun, queryScenarioTypeTextFun, changeScenarioTypesFun, modelDynamicFieldComRef, userInfo}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item:last-child {
  margin-bottom: 18px !important;
}

.content_list{
  max-height: 620px;
  overflow-y: auto;
}
</style>