<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="820" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="通道名称：">
            <el-input v-model="platformName" disabled type="text"></el-input>
          </el-form-item>
          <template v-if="protocolType === 1">
            <el-form-item label="数据配置：" required>
              <el-col :span="5"><span class="flex-jc-ai-center">站点</span></el-col>
              <el-col :span="1"></el-col>
              <el-col :span="5"><span class="flex-jc-ai-center">设备编号</span></el-col>
              <el-col :span="1"></el-col>
              <el-col :span="5"><span class="flex-jc-ai-center">资源编号</span></el-col>
              <el-col :span="1"></el-col>
              <el-col :span="5"><span class="flex-jc-ai-center">服务标识</span></el-col>
            </el-form-item>
          </template>
          <template v-if="protocolType === 2">
            <el-form-item label="数据配置：" required>
              <el-col :span="6">
                <div class="flex-jc-ai-center">
                  <span class="titleName" style="margin-right: 2px">站点</span>
                  <el-tooltip placement="top" trigger="click">
                    <el-icon class="pointer" size="16"><QuestionFilled /></el-icon>
                    <template #content>
                      <div class="el-alert__title">提示：如站点配置到当前通道中，则其它通道下该站点配置则失效。</div>
                    </template>
                  </el-tooltip>
                </div>
              </el-col>
              <el-col :span="1"></el-col>
              <el-col :span="6"><span class="flex-jc-ai-center">运营商ID</span></el-col>
               <el-col :span="5"><span class="flex-jc-ai-center">资源编号</span></el-col>
            </el-form-item>
          </template>
          <el-form-item prop="dataConfigList">
            <div class="scrollbarStyle content_body">
              <template v-if="formDialog.dataConfigList && formDialog.dataConfigList.length">
                <template v-for="(item,index) in formDialog.dataConfigList" :key="index">
                  <div class="content_list">
                    <el-col :span="5">
                      <el-form-item :prop="'dataConfigList.' + index + '.siteId'" :rules="{required: true,message: '请选择站点',trigger: 'change'}">
                        <el-select v-model="item.siteId" placeholder="请选择站点">
                          <el-option v-for="item in siteIdArray" :disabled="lockSiteIdDisabledFun(item.id)" :key="item.id" :label="item.name" :value="item.id"></el-option>
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="1">
                      <span class="flex-jc-ai-center">-</span>
                    </el-col>
                    <template v-if="protocolType === 1">
                      <el-col :span="5">
                        <el-form-item :prop="'dataConfigList.' + index + '.deviceSn'" :rules="{required: true,message: '请输入设备编号',trigger: 'change'}">
                          <el-input v-model="item.deviceSn" placeholder="设备编号" type="text"></el-input>
                        </el-form-item>
                      </el-col>
                      <el-col :span="1">
                        <span class="flex-jc-ai-center">-</span>
                      </el-col>
                      <el-col :span="5">
                        <el-form-item>
                          <el-input v-model="item.resourceSn" placeholder="资源编号" type="text"></el-input>
                        </el-form-item>
                      </el-col>
                      <el-col :span="1">
                        <span class="flex-jc-ai-center">-</span>
                      </el-col>
                      <el-col :span="5">
                        <el-form-item :prop="'dataConfigList.' + index + '.identifier'" :rules="{required: true,message: '请选择服务标识',trigger: 'change'}">
                          <el-select v-model="item.identifier" placeholder="服务标识" multiple collapse-tags collapse-tags-tooltip :max-collapse-tags="1">
                            <el-option v-for="item in identifierArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                          </el-select>
                        </el-form-item>
                      </el-col>
                    </template>
                    <template v-if="protocolType === 2">
                      <el-col :span="6">
                        <el-form-item :prop="'dataConfigList.' + index + '.operatorId'" :rules="{required: true,message: '请填写运营商ID',trigger: 'change'}">
                          <el-select v-model="item.operatorId" placeholder="请选择运营商ID">
                            <el-option v-for="item in equipmentOwnerArray" :key="item.operatorId" :label="item.operatorName" :value="item.operatorId"></el-option>
                          </el-select>
                        </el-form-item>
                      </el-col>
                        <el-col :span="1">
                      <span class="flex-jc-ai-center">-</span>
                    </el-col>
                      <el-col :span="6">
                        <el-form-item>
                          <el-input v-model="item.resourceSn" placeholder="资源编号" type="text"></el-input>
                        </el-form-item>
                      </el-col>
                    </template>
                    <el-col :span="1">
                      <div class="flex-jc-ai-center pointer" style="height: 100%;" @click="clickRemoveFun(index)">
                        <el-icon color="#FD393A" size="18"><RemoveFilled/></el-icon>
                      </div>
                    </el-col>
                  </div>
                </template>
              </template>
              <template v-else>
                <null-data words="暂无数据配置"></null-data>
              </template>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </template>
    <template #bottomContent>
      <el-button :disabled="listLoading" :icon="Plus" class="whiteFontButtons" @click="clickAddDataFun">添加数据</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {getSiteDeviceTreeList} from "@/api/deviceCenter/deviceList";
import {Plus, RemoveFilled,QuestionFilled} from '@element-plus/icons-vue';
import {findAllOperatorInfoList} from "@/api/configCenter/operatorManagement";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {batchUpdateDataConfig, findDataConfigByForwardId} from "@/api/configCenter/dataForwarding";

export default defineComponent({
  name: "PassagewayConfigDialog",
  components: {RemoveFilled,QuestionFilled},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    platformName: {
      type: String,
      default: ""
    },
    dataForwardId: {
      type: [String,Number],
      default: ""
    },
    // 接入协议类型 1-mqtt 2-http
    protocolType: {
      type: [String,Number],
      default: 1
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateDataConfigList = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请添加数据配置信息"));
      } else {
        callback();
      }
    };

    const that = reactive({
      Plus,
      formDialog: {},
      listLoading: false,
      titleName: "通道配置",
      dialog_visible: props.isVisible,

      siteIdArray: [],
      equipmentOwnerArray: [],
      identifierArray: [{id: "elecInfoAcq", name: "负荷用电信息"}, {id: "chgStatInfoAcq", name: "充电站信息"}, {id: "esInfoAcq", name: "储能电站信息"}],
      rules: {
        dataConfigList: [{required: true, trigger: "blur", validator: validateDataConfigList }],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          let dataConfigVos = [];
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          if(formDialog.dataConfigList && formDialog.dataConfigList.length){
            for(let i = 0;i < formDialog.dataConfigList.length;i++){
              let dynamicConfigs = JSON.parse(JSON.stringify(formDialog.dataConfigList[i]));
              delete dynamicConfigs.siteId;
              dataConfigVos.push({siteId: formDialog.dataConfigList[i].siteId, dynamicConfigs: dynamicConfigs });
            }
            delete formDialog.dataConfigList
          }

          batchUpdateDataConfig({forwardId: props.dataForwardId,dataConfigVos: dataConfigVos,...formDialog }).then(()=>{
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const clickAddDataFun = () => {
      if (!that.formDialog.dataConfigList) that.formDialog.dataConfigList = [];
      that.formDialog?.dataConfigList.push({});
    }

    const clickRemoveFun = (index) => {
      that.formDialog.dataConfigList.splice(index, 1)
    }

    // 获取站点设备树形结构
    const querySiteDeviceTreeList = ()=>{
      getSiteDeviceTreeList({ type: 1,timer: new Date() }).then(res=>{
        let handleMenuArray = res.data ? res.data : [];
        that.siteIdArray = JSON.parse(JSON.stringify(handleMenuArray));
      })
    }

    // 查看当前站点是否可选择
    const lockSiteIdDisabledFun = (siteId = "")=>{
      let disabled = false;
      let findIndex = that.formDialog.dataConfigList.findIndex(item => item.siteId === siteId);
      if(findIndex !== -1)disabled = true;
      return disabled
    }

    // 根据数据转发id查询数据配置数据
    const initParamConfigFun = () => {
      that.listLoading = true;
      findDataConfigByForwardId({ forwardId: props.dataForwardId }).then(res=>{
        let dataConfigList = [];
        let dataConfigVos = res.data ? res.data : [];
        for(let i = 0;i < dataConfigVos.length;i++){
          let dynamicConfigs = {};
          if(dataConfigVos[i].dynamicConfigs) dynamicConfigs = JSON.parse(dataConfigVos[i].dynamicConfigs);
          dataConfigList.push({ siteId: dataConfigVos[i].siteId,...dynamicConfigs });
        }
        that.formDialog.dataConfigList = JSON.parse(JSON.stringify(dataConfigList));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    // 查询所有运营商列表
    const queryAllOperatorInfoList = () =>{
      findAllOperatorInfoList({ timer: new Date() }).then(res=>{
        that.equipmentOwnerArray = res.data ? res.data : [];
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
      querySiteDeviceTreeList();
      if(props.protocolType === 2) queryAllOperatorInfoList();
    })

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef, clickAddDataFun, clickRemoveFun, querySiteDeviceTreeList,
      lockSiteIdDisabledFun, queryAllOperatorInfoList}
  }
})

</script>

<style lang="scss" scoped>
.content_body {
  width: 100%;
  max-height: 520px;
  overflow-y: auto;

  .content_list {
    width: 100%;
    display: flex;
    margin-bottom: 16px;
  }
}
</style>