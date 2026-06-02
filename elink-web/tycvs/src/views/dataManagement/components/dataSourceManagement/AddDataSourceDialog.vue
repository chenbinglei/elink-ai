<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="820" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main scrollbarStyle">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">

          <el-form-item label="名称：" prop="name">
            <el-input v-model="formDialog.name" placeholder="请输入名称" type="text" maxlength="32" show-word-limit></el-input>
          </el-form-item>

          <el-form-item label="通信方式：" prop="type">
            <el-select v-model="formDialog.type" placeholder="请选择通信方式">
              <el-option v-for="item in socketTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>

          <template v-if="formDialog.type">
            <el-form-item label="URL地址：" prop="url">
              <el-input type="textarea" v-model="formDialog.url" :placeholder=" formDialog.type === 2 ? 'http(s)://' : 'ws(s)://' "></el-input>
            </el-form-item>

            <template v-if="formDialog.type <= 2">
              <template v-if="formDialog.type === 2">
                <el-form-item label="请求方式：" prop="dynamicField.requestMethod">
                  <el-select v-model="formDialog.dynamicField.requestMethod" placeholder="请选择请求方式">
                    <el-option v-for="item in requestMethodArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                  </el-select>
                </el-form-item>
              </template>
              <el-form-item label="参数：">
                <el-table :data="formDialog.requestKey" :max-height="tableMaxHeight">
                  <el-table-column label="序号" type="index" width="60"></el-table-column>
                  <el-table-column align="center" label="Name">
                    <template #default="{ row }">
                      <el-input v-model="row.keyText" placeholder="请输入Name" type="text"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" label="Key">
                    <template #default="{ row }">
                      <el-input v-model="row.keyName" placeholder="请输入Key" type="text"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" label="是否必填" width="90">
                    <template #default="{ row }">
                      <el-checkbox v-model="row.required" label="必填"></el-checkbox>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" label="描述" min-width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.describe" type="textarea" placeholder="请输入描述" maxlength="100" :rows="2"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" width="90">
                    <template #header>
                      <div class="flex-jc-ai-center pointer" @click="clickTableOperateBut('add')">
                        <el-icon size="21" color="#1F74E2"><CirclePlusFilled /></el-icon>
                      </div>
                    </template>
                    <template #default="{ $index }">
                      <div class="flex-jc-ai-center pointer" @click="clickTableOperateBut('delete',$index)">
                        <el-icon size="21" color="#FF2626"><RemoveFilled /></el-icon>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>

              </el-form-item>
              <template v-if="formDialog.type === 2">
                <el-form-item label="请求间隔(s)：" prop="dynamicField.requestInterval">
                  <el-input-number v-model="formDialog.dynamicField.requestInterval" controls-position="right" min="30" placeholder="请输入请求间隔" />
                </el-form-item>
                <el-form-item label="请求头：">
                  <div class="requestHeader">
                    <CodeEditor v-model:value="formDialog.dynamicField.requestHeader" :placeholder="placeholder"></CodeEditor>
                  </div>
                </el-form-item>
              </template>
            </template>

            <template v-if="formDialog.type === 3">
              <el-form-item label="用户名：" prop="dynamicField.userName">
                <el-input v-model="formDialog.dynamicField.userName" placeholder="请输入用户名" type="text"></el-input>
              </el-form-item>
              <el-form-item label="密码：" prop="dynamicField.password">
                <el-input v-model="formDialog.dynamicField.password" placeholder="请输入密码" type="password" show-password></el-input>
              </el-form-item>
              <el-form-item label="Topics：" prop="dynamicField.topics">
                <el-input v-model="formDialog.dynamicField.topics" placeholder="多个topic以英文逗号隔开" type="text"></el-input>
              </el-form-item>
            </template>
          </template>
          <el-form-item label="描述：">
            <el-input v-model="formDialog.description" :rows="3" type="textarea" placeholder="请输入描述"/>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import CodeEditor from "@/components/component/CodeEditor.vue";
import {CirclePlusFilled,RemoveFilled} from '@element-plus/icons-vue';
import {isValidUsername, serviceAddressVerification} from '@/utils/validate';
import {saveDataSource,findDataSourceById} from "@/api/dataManagement/dataSourceManagement";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddDataSourceDialog",
  components:{CodeEditor,CirclePlusFilled,RemoveFilled},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加数据源"
    },
    dataSourceId: {
      type: [String,Number],
      default: ""
    },
  },
  emits:["update:isVisible","changeEvent"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateName = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入名称"));
      } else {
        callback();
      }
    };

    const validateType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择通信方式"));
      } else {
        callback();
      }
    };

    const validateUrl = (rule, value, callback) => {
      let url_list = value.split("://");
      if(!url_list || url_list.length !== 2){
        callback(new Error("请输入正确的URL地址"));
      } else {
        let url_prefix = url_list[0];
        let findItem = that.urlPrefixList.find(item => item === url_prefix);
        if(findItem){
            if (!serviceAddressVerification(url_list[1])) {
              callback(new Error("请输入正确的URL地址"));
            } else {
              callback();
            }
        } else {
          callback(new Error("请输入正确的URL地址"));
        }
      }
    };

    const validateUserName = (rule, value, callback) => {
      if (!value || !isValidUsername(value)) {
        callback(new Error("请输入正确的用户名"));
      } else {
        callback();
      }
    };

    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入密码"));
      } else {
        callback();
      }
    };

    const validateTopics = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入Topics"));
      } else {
        callback();
      }
    };

    const validateRequestMethod = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择请求方式"));
      } else {
        callback();
      }
    };

    const validateRequestInterval = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入请求间隔"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      tableMaxHeight: 180,
      dialog_visible: props.isVisible,
      formDialog: { dynamicField: {} },
      placeholder: "{\"Content-Type\": \"multipart/form-data\"}",
      requestMethodArray: [{id: "GET", name: "GET"}, {id: "POST", name: "POST"}],
      socketTypeArray: [{id: 1, name: "websocket"}, {id: 2, name: "http"}, {id: 3, name: "mqtt"}],
      urlPrefixList: ["ws","wss","http","https"],
          // {type:1,name:"ws://"},{type:1,name:"wss://"},{type:2,name:"http://"},{type:2,name:"https://"},{type:3,name:"ws://"},{type:3,name:"wss://"}],

      rules: {
        type: [{required: true, trigger: "change", validator: validateType }],
        name: [{required: true, trigger: "change", validator: validateName }],
        url: [{required: true, trigger: "change", validator: validateUrl }],
        "dynamicField.requestMethod": [{required: true, trigger: "change", validator: validateRequestMethod }],
        "dynamicField.requestInterval": [{required: true, trigger: "change", validator: validateRequestInterval}],
        "dynamicField.userName": [{required: true, trigger: "change", validator: validateUserName }],
        "dynamicField.password": [{required: true, trigger: "change", validator: validatePassword }],
        "dynamicField.topics": [{required: true, trigger: "change", validator: validateTopics }],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          // console.log(formDialog);
          // 新建或编辑数据源
          saveDataSource({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const clickTableOperateBut = (operateType,index)=>{

      if(operateType === "add"){
        if(!that.formDialog.requestKey) that.formDialog.requestKey = [];
        that.formDialog.requestKey.push({});
      }

      if(operateType === "delete"){
        that.formDialog.requestKey.splice(index,1);
      }
    }

    const initParamConfigFun = () => {
      that.listLoading = true;
      findDataSourceById({ id: props.dataSourceId }).then(res=>{
        let formDialog = res.data ? res.data : {};
        if(formDialog.requestKey) formDialog.requestKey = JSON.parse(formDialog.requestKey);
        if(formDialog.dynamicField) formDialog.dynamicField = JSON.parse(formDialog.dynamicField);
        that.formDialog = JSON.parse(JSON.stringify(formDialog));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      if(props.dataSourceId) initParamConfigFun();
    })

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef, clickTableOperateBut}
  }
})
</script>

<style lang="scss" scoped>
.dialog-main{
  max-height: 600px;
  overflow-y: auto;

  .requestHeader{
    width: 100%;
    height: 140px;

    :deep(.cm-editor){
      height: 100%;
    }
  }
}
</style>