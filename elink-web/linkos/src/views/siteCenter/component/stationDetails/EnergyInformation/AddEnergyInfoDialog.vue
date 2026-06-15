<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="890" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-row :gutter="10">
            <el-col :span="24">
              <el-form-item label="能源系统名称：" prop="systemName">
                <el-input v-model="formDialog.systemName" maxlength="32" placeholder="请输入能源系统名称" show-word-limit/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="能源场景：" prop="scenarioType">
                <el-select v-model="formDialog.scenarioType" placeholder="请选择能源场景" @change="changeScenarioTypesFun">
<!--                    :disabled="scenarioTypes.indexOf(item.id) !== -1"-->
                  <el-option v-for="(item,index) in scenarioTypesArray" :key="index" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="选择模型：" prop="modelId">
                <el-select v-model="formDialog.modelId" placeholder="请选择模型" :disabled="!formDialog.scenarioType">
                  <el-option v-for="item in modelArray" :key="item.id" :label="item.modelName" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <template v-if="formDialog.modelId">
          <div class="scrollbarStyle content_list">
            <ModelDynamicFieldCom ref="modelDynamicFieldComRef" :sourceType="2" :modelId="formDialog.modelId" :scenarioType="formDialog.scenarioType"></ModelDynamicFieldCom>
          </div>
        </template>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {commonCharName} from "@/utils/validate";
import {getModelNameListByTypeId} from "@/api/deviceCenter/deviceList";
import {saveOrUpdateSiteScenarioType} from "@/api/siteCenter/stationDetails";
import ModelDynamicFieldCom from "@/views/siteCenter/component/ModelDynamicFieldCom.vue";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent, nextTick} from "vue";

export default defineComponent({
  name:"AddEnergyInfoDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加站点"
    },
    // 已添加的能源类型
    scenarioTypes: {
      type: String,
      default: ""
    },
    activeEditInfo:{
      type: Object,
      default: ()=>{
        return { }
      }
    }
  },
  components:{ModelDynamicFieldCom},
  setup(props){
    const {emit} = getCurrentInstance();

    const validateSystemName = (rule, value, callback) => {
      if (!value || !commonCharName(value)) {
        callback(new Error("请输入正确的站点名称"));
      } else {
        callback();
      }
    };

    const validateScenarioType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择能源场景"));
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

    const that = reactive({
      formDialog: {},
      listLoading: false,
      dialog_visible: props.isVisible,

      modelArray: [],
      scenarioTypesArray: [
        {id: 1, name: "光伏",typeId: 4},
        {id: 2, name: "储能",typeId: 5},
        {id: 3, name: "电桩",typeId: 6},
        {id: 4, name: "用能",typeId: 7},
        {id: 5, name: "配电",typeId: 61},
        {id: 6, name: "换电",typeId: 68}
      ],

      rules: {
        systemName: [{required: true, trigger: "change", validator: validateSystemName }],
        scenarioType: [{required: true, trigger: "change", validator: validateScenarioType }],
        modelId: [{required: true, trigger: "change", validator: validateModelId }],
      }
    })

    const formDialogRef = ref(null);
    const modelDynamicFieldComRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          modelDynamicFieldComRef.value.formSubmitFun().then(res=>{
            // 新增或编辑站点能源场景信息数据
            let formDialog = JSON.parse(JSON.stringify(that.formDialog));
            saveOrUpdateSiteScenarioType({...formDialog,readwriteObject: res.readwriteObject }).then(()=>{
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

    // 选择 能源场景
    const changeScenarioTypesFun = ()=>{
      that.formDialog.modelId = "";
      queryModelNameListByTypeId();
    }

    // 根据分类id获取模型名称列表
    const queryModelNameListByTypeId = ()=>{
      let findItem = that.scenarioTypesArray.find(item => item.id === that.formDialog.scenarioType);
      getModelNameListByTypeId({ scenarioType: that.formDialog.scenarioType,typeId: findItem?.typeId, timer: new Date() }).then(res=>{
        let modelArray = res.data ? res.data : [];
        that.modelArray = JSON.parse(JSON.stringify(modelArray));
      })
    }

    const initParamConfigFun = () => {
      let formDialog = Object.assign({},props.activeEditInfo,that.formDialog);
      that.formDialog = JSON.parse(JSON.stringify(formDialog));

      // 编辑能源信息
      if(formDialog.id){
        queryModelNameListByTypeId();  // 根据分类id获取模型名称列表
        delete that.formDialog.readwriteObject;

        nextTick(()=>{
          if(formDialog.readwriteObject){
            modelDynamicFieldComRef.value.formDialog = JSON.parse(JSON.stringify(formDialog.readwriteObject));
          }
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

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, queryModelNameListByTypeId, changeScenarioTypesFun,
      modelDynamicFieldComRef}

  }
})
</script>

<style scoped lang="scss">
.content_list{
  max-height: 620px;
  overflow-y: auto;
}

.el-form-item:last-child {
  margin-bottom: 18px !important;
}
</style>