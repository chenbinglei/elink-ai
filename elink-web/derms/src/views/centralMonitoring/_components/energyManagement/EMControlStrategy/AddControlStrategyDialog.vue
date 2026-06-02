<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="620" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="策略名称：" prop="strategyName">
            <el-input v-model="formDialog.strategyName" maxlength="50" placeholder="请输入策略名称" show-word-limit/>
          </el-form-item>
          <el-form-item label="策略模板：" prop="templateId">
            <el-select v-model="formDialog.templateId" clearable placeholder="请选择策略模板">
              <template v-for="item in templateIdArray" :key="item.id">
                <template v-if="activeDeviceInfo.strategyType !== 3 || (activeDeviceInfo.strategyType === 3 && item.templateName.indexOf('云平台') !== -1)">
                  <el-option :label="item.templateName" :value="item.id"></el-option>
                </template>
              </template>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {someCharmap} from "@/utils/validate";
import {saveStrategy,queryTemplateList} from "@/api/centralMonitoring/energyManagement";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";

export default defineComponent({
  name: "AddControlStrategyDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加控制策略"
    },
    // 当前站点id
    activeSiteId: {
      type: [String, Number],
      default: ""
    },
    // 当前设备信息
    activeDeviceInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const validateStrategyName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的策略名称"));
      } else {
        callback();
      }
    };

    const validateTemplateId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择策略模板"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      templateIdArray: [],
      dialog_visible: props.isVisible,
      rules:{
        strategyName: [{ required: true, trigger: "change", validator: validateStrategyName }],
        templateId: [{ required: true, trigger: "change", validator: validateTemplateId }],
      }
    });

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          saveStrategy({siteId: props.activeSiteId,...props.activeDeviceInfo,...formDialog}).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    // 查询策略模板列表数据
    const findTemplateList = ()=>{
      queryTemplateList({page: 1,size: 1000,timer: new Date()}).then(res=>{
        that.templateIdArray = res.data.items;
      });
    };

    const initParamConfigFun = ()=>{};

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      findTemplateList();
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, initParamConfigFun, formDialogRef, findTemplateList};

  }
});
</script>

<style scoped lang="scss">

</style>