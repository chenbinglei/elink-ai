<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="模板名称：" prop="templateName">
            <el-input v-model="formDialog.templateName" maxlength="50" placeholder="请输入模板名称" show-word-limit/>
          </el-form-item>
          <el-form-item label="策略类型：" prop="strategyType">
            <el-select v-model="formDialog.strategyType" clearable placeholder="请选择策略类型">
              <el-option v-for="item in strategyTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="策略说明：">
            <UploadFileCustom v-model:fileArray="formDialog.explainFile_List" :fileSize="20" acceptType=".md" :classFileType="2">
              <template #tip_content>
                <span>上传.md格式，文件大小不超过20M。</span>
              </template>
            </UploadFileCustom>
          </el-form-item>
          <el-form-item label="配置文件：" prop="configFile_List">
            <UploadFileCustom v-model:fileArray="formDialog.configFile_List" :fileSize="10" acceptType=".json" :classFileType="2">
              <template #tip_content>
                <span>上传.json格式，文件大小不超过10M。</span>
              </template>
            </UploadFileCustom>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import { ElMessage } from "element-plus";
import { someCharmap } from "@/utils/validate";
import {strategy_type_array} from "@/utils/setVariate";
import UploadFileCustom from "@/components/uploadFileCom/UploadFileCustom.vue";
import {findTemplateById, saveOrUpdateTemplate} from "@/api/centralMonitoring/energyManagement";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";

export default defineComponent({
  name:"AddStrategyTemDialog",
  components:{UploadFileCustom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "创建策略模板"
    },
    templateId:{
      type: [String,Number],
      default: ""
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const validateTemplateName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的策略模板名称"));
      } else {
        callback();
      }
    };

    const validateStrategyType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择策略类型"));
      } else {
        callback();
      }
    };

    const validateConfigFile = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请上传配置文件"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      dialog_visible: props.isVisible,
      strategyTypeArray: strategy_type_array,
      rules:{
        templateName: [{ required: true, trigger: "change", validator: validateTemplateName }],
        strategyType: [{ required: true, trigger: "change", validator: validateStrategyType }],
        configFile_List: [{ required: true, trigger: "change", validator: validateConfigFile }],
      }
    });

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formData = new FormData();
          for (let key in that.formDialog){
            if(key.indexOf('File_List') !== -1){
              let key_arr = key.split("_");
              if(that.formDialog[key] && that.formDialog[key].length){
                for (let i = 0; i < that.formDialog[key].length; i++) {
                  if (that.formDialog[key][i].raw) formData.append(key_arr[0], that.formDialog[key][i].raw);
                }
              }
            } else {
              formData.append(key, that.formDialog[key]);
            }
          }

          saveOrUpdateTemplate(formData).then(()=>{
            that.dialog_visible = false;
            emit("changeEvent",{ type:"listArray" });
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const initParamConfigFun = ()=>{
      if(props.templateId){
        that.listLoading = true;
        findTemplateById({ id: props.templateId }).then(res=>{
          let returnDataInfo = res.data ? res.data : {};
          if(returnDataInfo.configName) returnDataInfo.configFile_List = [{ name: returnDataInfo.configName }];
          if(returnDataInfo.explainName) returnDataInfo.explainFile_List = [{ name: returnDataInfo.explainName }];
          that.formDialog = JSON.parse(JSON.stringify(returnDataInfo));
          that.listLoading = false;
        }).catch(()=>{
          that.listLoading = false;
        });
      }
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, initParamConfigFun, formDialogRef};

  }
});
</script>

<style scoped lang="scss">

</style>