<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="580px" @confirm="clickConfirm">
    <template v-slot:content>
      <div class="content_body">
        <el-form :model="formdialog" :rules="rules" ref="formDialogRef" label-width="100px">
          <el-form-item label="变量类型:" prop="varType">
            <el-radio-group v-model="formdialog.varType" :disabled="formdialog.id" @change="formdialog.dataSource = ''">
              <el-radio v-for="item in varTypeArray" :key="item.id" :label="item.id">{{ item.name }}</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="变量名称:" prop="varName">
            <el-input type="text" v-model="formdialog.varName" placeholder="请输入变量名称"></el-input>
          </el-form-item>

          <el-form-item label="变量标识:" prop="varCode">
            <el-input type="text" v-model="formdialog.varCode" :disabled="formdialog.id" placeholder="请输入变量标识"></el-input>
          </el-form-item>

          <el-form-item label="数据来源:" prop="dataSource">
            <el-select v-model="formdialog.dataSource" placeholder="请选择数据来源">
              <template v-for="item in dataSourceArray" :key="item.id">
                <el-option :disabled="formdialog.varType === 2 && item.id === 2" :label="item.name" :value="item.id"></el-option>
              </template>
            </el-select>
          </el-form-item>

          <el-form-item label="描述:">
            <el-input type="textarea" v-model="formdialog.remark" rows="3" maxlength="100" show-word-limit placeholder="请输入描述"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import { useStore } from "vuex";
import {ElMessage} from "element-plus";
import {notCharmap, someCharmap} from "@/utils/validate";
import {saveOrUpdateSystemVariable} from "@/api/dataManagement/systemVariables";
import {getCurrentInstance, reactive, toRefs, watch, ref, computed, defineComponent} from "vue";

export default defineComponent({
  name: "AddVariableDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: ""
    },
    activeVarInfo: {
      type: Object,
      default: ()=>{
        return { }
      }
    },
  },
  setup(props) {
    const store = useStore();
    const formDialogRef = ref(null);
    const { emit } = getCurrentInstance();

    const userInfo = computed(() => {
      return store.state.app.userInfo
    });

    const validateVarName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的变量名称"));
      } else {
        callback();
      }
    };

    const validateVarCode = (rule, value, callback) => {
      if (!value || !notCharmap(value)) {
        callback(new Error("请输入正确的变量标识"));
      } else {
        callback();
      }
    };

    const validateVarType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择变量类型"));
      } else {
        callback();
      }
    };

    const validateDataSource = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择数据来源"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      formdialog: props.activeVarInfo,
      dialog_visible: props.isVisible,
      varTypeArray: [{id:2,name:"场站变量"},{id:1,name:"设备变量"}],
      dataSourceArray: [{id:1,name:"计算节点"},{id:2,name:"模型功能点"}],
      rules: {
        varName: [{ required: true, trigger: "change", validator: validateVarName }],
        varCode: [{ required: true, trigger: "change", validator: validateVarCode }],
        varType: [{ required: true, trigger: "change", validator: validateVarType }],
        dataSource: [{ required: true, trigger: "change", validator: validateDataSource }],
      }
    });

    const clickConfirm = ()=>{
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          saveOrUpdateSystemVariable({ ...that.formdialog }).then(()=>{
            that.dialog_visible = false;
            emit("changEvent", { type: "listArray" });
            ElMessage({type: "success", showClose: true, message: "操作成功！"});
          }).catch((e)=>{
            that.listLoading = false;
          })
        }
      });
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return { ...toRefs(that), watchVisible, watchDialogVisible,clickConfirm,formDialogRef,userInfo }
  }
})
</script>

<style scoped>

</style>
