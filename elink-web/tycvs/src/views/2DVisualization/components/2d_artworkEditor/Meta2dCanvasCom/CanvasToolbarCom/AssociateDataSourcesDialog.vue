<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="60vw" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="名称：" prop="name">
            <el-input v-model="formDialog.name" type="text" placeholder="请输入名称"/>
          </el-form-item>
          <el-form-item label="选择数据源：" prop="dataSourceId">
            <el-select v-model="formDialog.dataSourceId" placeholder="请选择数据源" @change="changeDataSourceIdFun">
              <el-option v-for="item in dataSourceArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="URL地址：">
            <el-input v-model="formDialog.url" disabled :rows="3" type="textarea" placeholder="URL地址"/>
          </el-form-item>
          <el-form-item label="参数：" v-if="formDialog.requestKey && formDialog.requestKey.length">
            <el-table :data="formDialog.requestKey" :max-height="tableMaxHeight">
              <el-table-column label="Name" width="120" show-overflow-tooltip>
                <template #default="{ row }">{{ $filters.moreData(row.keyText) }}</template>
              </el-table-column>
              <el-table-column label="Key" width="120" show-overflow-tooltip>
                <template #default="{ row }">{{ $filters.moreData(row.keyName) }}</template>
              </el-table-column>
              <el-table-column label="required" width="90">
                <template #default="{ row }">{{ row.required ? '必填' : '--' }}</template>
              </el-table-column>
              <el-table-column label="说明" width="140" show-overflow-tooltip>
                <template #default="{ row }">{{ $filters.moreData(row.describe) }}
                </template>
              </el-table-column>
              <el-table-column label="value">
                <template #default="{ row }">
                  <el-input v-model="row.value" placeholder="请输入值" type="textarea" @change="formDialog.connectionTest = 0">
                    <template #suffix>
                      <template v-if="row.describe">
                        <el-tooltip effect="dark" placement="top">
                          <el-icon><QuestionFilled /></el-icon>
                          <template #content>{{ row.describe }}</template>
                        </el-tooltip>
                      </template>
                    </template>
                  </el-input>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item label="连接测试：" prop="connectionTest">
            <div class="flex-ai-center">
              <span class="connectionTest" :class="'connectionTest' + formDialog.connectionTest">
                {{ formDialog.connectionTest ? '通过' : '未通过' }}
              </span>
              <el-button class="whiteFontButtons" :loading="connectionTestStatus" :disabled="connectionTestStatus" @click="clickConnectionTestFun">连接测试</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>
<script lang="ts">
import {ElMessage} from "element-plus";
import {QuestionFilled} from '@element-plus/icons-vue';
import {queryDataSourceList} from "@/api/dataManagement/dataSourceManagement";
import {checkDataSource, relationDataSource} from "@/api/2DVisualization/2d_artworkEditor";
import {defineComponent, getCurrentInstance, onMounted, reactive, ref, toRefs, watch} from "vue";

export default defineComponent({
  name: "AssociateDataSourcesDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "关联数据源"
    },
    activeEditInfo: {
      type: Object,
      default: ()=>{
        return {}
      }
    }
  },
  components:{QuestionFilled},
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateName = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入名称"));
      } else {
        callback();
      }
    };

    const validateDataSourceId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择数据源"));
      } else {
        callback();
      }
    };

    const validateConnectionTest = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请进行连接测试"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      dialog_visible: props.isVisible,

      dataSourceArray: [],
      tableMaxHeight: 320,
      connectionTestStatus: false,
      rules: {
        name: [{required: true, trigger: "change", validator: validateName }],
        dataSourceId: [{required: true, trigger: "change", validator: validateDataSourceId }],
        connectionTest: [{required: true, trigger: "change", validator: validateConnectionTest }],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          // 图模关联数据源
          relationDataSource({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 校验数据源是否调通
    const clickConnectionTestFun = ()=>{

      if(!that.formDialog.dataSourceId){
        ElMessage({ type: "warning", message: "请先选择数据源", showClose: true });
        return
      }

      let requestValue = {};
      let formDialog = JSON.parse(JSON.stringify(that.formDialog));
      if(formDialog.requestKey && formDialog.requestKey.length){
        for(let i = 0;i < formDialog.requestKey.length;i++){
          if(formDialog.requestKey[i].required && !formDialog.requestKey[i].value){
            ElMessage({ type: "warning", message: `请填写${ formDialog.requestKey[i].keyName }的值`, showClose: true });
            return
          }

          let itemValue = formDialog.requestKey[i].value;
          if(formDialog.requestType !== 2 && !itemValue) itemValue = 'undefined';
          requestValue[formDialog.requestKey[i].keyName] = itemValue;
        }
      }

      that.connectionTestStatus = true;
      checkDataSource({ dataSourceId: that.formDialog.dataSourceId,requestValue: requestValue }).then(res=>{
        that.formDialog.requestValue = requestValue;
        that.formDialog.responseValue = res.data ? res.data : {};
        that.formDialog.connectionTest = 1;
        that.connectionTestStatus = false;
      }).catch(()=>{
        that.formDialog.responseValue = {};
        that.connectionTestStatus = false;
      })
    }

    const changeDataSourceIdFun = ()=>{
      let findItem = that.dataSourceArray.find(item=> item.id === that.formDialog.dataSourceId);
      if(findItem){
        that.formDialog.url = findItem.url;
        that.formDialog.requestType = findItem.type;
        that.formDialog.requestKey = findItem.requestKey;
      }
      that.formDialog.connectionTest = 0;

      if(that.formDialog.requestKey && that.formDialog.requestKey.length){
        for(let i = 0;i < that.formDialog.requestKey.length;i++){
          that.formDialog.requestKey[i].value = that.formDialog.requestValue[that.formDialog.requestKey[i].keyName]
        }
      }
    }

    // 查询数据源管理列表
    const initParamConfigFun = () => {
      let formDialog = Object.assign({}, that.formDialog, props.activeEditInfo);
      formDialog.requestValue = formDialog.requestValue ? JSON.parse(formDialog.requestValue) : {};
      that.formDialog = JSON.parse(JSON.stringify(formDialog));
      queryDataSourceList({ timer: new Date() }).then(res=>{
        let list = res.data ? res.data : [];
        list.forEach(item=>{
          if(item.requestKey)item.requestKey = JSON.parse(item.requestKey);
        })
        that.dataSourceArray = JSON.parse(JSON.stringify(list));
        changeDataSourceIdFun();
      })
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef, changeDataSourceIdFun,
      clickConnectionTestFun}

  }
})

</script>

<style lang="scss" scoped>
.connectionTest{
  color: #666666;
  margin-right: 10px;
}

.connectionTest1{
  color: #55a532;
}
</style>