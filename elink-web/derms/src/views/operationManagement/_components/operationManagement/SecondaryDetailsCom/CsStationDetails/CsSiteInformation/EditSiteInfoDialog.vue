<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="720" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="站点名称：" prop="siteName">
            <el-input v-model="formDialog.siteName" maxlength="32" placeholder="请输入站点名称" show-word-limit/>
          </el-form-item>
          <el-form-item label="站点状态：" prop="siteStatus">
            <el-select v-model="formDialog.siteStatus" placeholder="请选择站点状态">
              <el-option v-for="(item,index) in siteStatusArray" :key="index" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="站点描述：">
            <el-input type="textarea" v-model="formDialog.siteDescribe" :rows="3" maxlength="200" placeholder="请输入站点描述" show-word-limit/>
          </el-form-item>
        </el-form>

        <div class="scrollbarStyle content_list">
          <title-view title="电站属性">
            <template #content>
              <ModelDynamicFieldCom ref="modelDynamicFieldComRef" :modelFieldArray="modelFieldArray"></ModelDynamicFieldCom>
            </template>
          </title-view>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {commonCharName} from "@/utils/validate";
import {site_status_array} from "@/utils/setVariate";
import {findSiteInfoById,saveOrUpdateSiteInfo} from "@/api/operationManagement/CsStationDetails";
import {getCurrentInstance, reactive, ref, toRefs, watch, defineComponent, onMounted, nextTick} from "vue";
import ModelDynamicFieldCom from "@/views/operationManagement/_components/operationManagement/SecondaryDetailsCom/ModelDynamicFieldCom.vue";

export default defineComponent({
  name: "EditSiteInfoDialog",
  components:{ModelDynamicFieldCom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    siteId:{
      type: [Number,String],
      default:""
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();

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

    const that = reactive({
      formDialog: {},// 动态字段列表
      listLoading: false,
      titleName: "编辑站点信息",
      dialog_visible: props.isVisible,

      modelFieldArray: [],
      siteStatusArray: site_status_array, //
      rules: {
        siteName: [{required: true, trigger: "change", validator: validateSiteName}],
        siteStatus: [{required: true, trigger: "change", validator: validateSiteStatus }],
      }
    });

    const formDialogRef = ref(null);
    const modelDynamicFieldComRef = ref([]);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          modelDynamicFieldComRef.value.formSubmitFun().then(res=>{
            let formDialog = JSON.parse(JSON.stringify(that.formDialog));
            if(formDialog.scenarioTypes) formDialog.scenarioTypes = formDialog.scenarioTypes.join(',');
            // 新增或编辑站点数据
            saveOrUpdateSiteInfo({ ...formDialog,siteReadwriteObject: res.readwriteObject}).then(()=>{
              emit("changeEvent");
              that.dialog_visible = false;
              ElMessage({ type: "success", showClose: true, message: "操作成功！" });
            }).catch(()=>{
              that.listLoading = false;
            });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const initParamConfigFun = ()=>{
      that.listLoading = true;
      // 根据站点id查询基本详情数据
      findSiteInfoById({id: props.siteId,timer: new Date(),pageName: "EditSiteInfoDialog"}).then(res=>{
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

        that.modelFieldArray = JSON.parse(JSON.stringify(returnDataInfo.siteReaList));
        nextTick(()=>{
          if(returnDataInfo.siteReadwriteObject){
            modelDynamicFieldComRef.value.formDialog = JSON.parse(returnDataInfo.siteReadwriteObject);
            // console.log(modelDynamicFieldComRef.value.formDialog);
          }

          // console.log(that.modelFieldArray);
          delete returnDataInfo.siteReaList;
          delete returnDataInfo.siteReadwriteObject;
          that.formDialog = JSON.parse(JSON.stringify(returnDataInfo));
          that.listLoading = false;
        });
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, modelDynamicFieldComRef};

  }
});
</script>

<style scoped lang="scss">
.content_list{
  max-height: 520px;
  overflow-y: auto;
}
</style>