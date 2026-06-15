<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="920" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="设备名称：" prop="deviceName">
                <el-input v-model="formDialog.deviceName" maxlength="32" placeholder="请输入设备名称" show-word-limit :disabled="editingType === 2"/>
              </el-form-item>
            </el-col>

            <el-col :span="12" v-if="!editingType">
              <el-form-item label="设备分类：" prop="typeId">
                <el-tree-select v-model="formDialog.typeId" :data="handleMenuArray" :indent="0" :props="treeProps" :render-after-expand="false"
                                class="leftArrowClass" default-expand-all filterable @change="queryModelNameListByTypeId" />
              </el-form-item>
            </el-col>

            <!--            只有充电设备才有设备序列号-->
            <!--            v-if="(editingType === 1 || !editingType) && (formDialog.typeId >= 28 && formDialog.typeId <= 30)"-->
            <el-col :span="12" >
              <el-form-item label="设备序列号：" prop="deviceNumber">
                <el-input v-model="formDialog.deviceNumber" placeholder="请输入设备序列号"/>
              </el-form-item>
            </el-col>

            <el-col :span="12" v-if="!editingType">
              <el-form-item label="选择模型：" prop="modelId">
                <el-select v-model="formDialog.modelId" :disabled="!formDialog.typeId" placeholder="请选择选择模型">
                  <el-option v-for="item in modelArray" :key="item.id" :label="item.modelName" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>

            <template v-if="editingType === 1 || !editingType">
              <el-col :span="12">
                <el-form-item label="接入类型：" prop="accessType">
                  <el-select v-model="formDialog.accessType" placeholder="请选择接入类型">
                    <el-option v-for="item in accessTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="运营状态：" prop="operateStatus">
                  <el-select v-model="formDialog.operateStatus" placeholder="请选择运营状态">
                    <el-option v-for="item in operateStatusArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所属站点：" prop="siteId">
                  <el-select v-model="formDialog.siteId" placeholder="请选择所属站点" @change="changeSiteFun">
                    <el-option v-for="item in siteArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="父级节点：">
                  <el-tree-select v-model="formDialog.parentId" :data="parentNodeArray" :indent="0" :props="treeProps1" :render-after-expand="false"
                                  clearable class="leftArrowClass" check-strictly default-expand-all filterable :disabled="!formDialog.siteId" />
                </el-form-item>
              </el-col>
            </template>

            <template v-if="editingType === 1 || !editingType">
              <el-col :span="24">
                <el-form-item label="设备描述：">
                  <el-input type="textarea" v-model="formDialog.deviceDesc" :rows="3" maxlength="200" placeholder="请输入设备描述" show-word-limit/>
                </el-form-item>
              </el-col>
            </template>
          </el-row>
        </el-form>
        <div class="maxHeightClass scrollbarStyle" v-if="formDialog.modelId">
          <TitleView title="扩展属性">
            <template #content>
              <ModelDynamicFieldCom ref="modelDynamicFieldComRef" :sourceType="3" :newEditStatus="!!editingType" :modelId="formDialog.modelId"></ModelDynamicFieldCom>
            </template>
          </TitleView>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {setTreeData} from "@/utils";
import {ElMessage} from "element-plus";
import {commonCharName} from "@/utils/validate";
import {Delete, Plus, Location} from '@element-plus/icons-vue';
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import {findSiteInfoListByUserId} from "@/api/siteCenter/siteManagement";
import ModelDynamicFieldCom from "@/views/siteCenter/component/ModelDynamicFieldCom.vue";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent, nextTick} from "vue";
import {findDeviceAssetList, findDeviceBasicInfoById, getModelNameListByTypeId, saveDevice} from "@/api/deviceCenter/deviceList";

export default defineComponent({
  name: "AddDeviceDialog",
  components: {ModelDynamicFieldCom, Plus, Delete, Location},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "创建模型"
    },
    activeSiteId: {
      type: [String, Number],
      default: ""
    },
    // 当前设备id
    activeDeviceId: {
      type: [Number, String],
      default: ""
    },
    // 编辑类型  0：新建  1: 基本类型  2： 扩展属性某一条
    editingType: {
      type: Number,
      default: 0
    },
    // 编辑字段
    fieldEditName: {
      type: String,
      default: ""
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

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

    const validateSiteId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择所属站点"));
      } else {
        callback();
      }
    };

    const validateDeviceName = (rule, value, callback) => {
      if (!value || !commonCharName(value)) {
        callback(new Error("请输入正确的设备名称"));
      } else {
        callback();
      }
    };

    const validateDeviceNumber = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入设备序列号"));
      } else {
        callback();
      }
    };

    const validateAccessType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择接入类型"));
      } else {
        callback();
      }
    };

    const validateOperateStatus = (rule, value, callback) => {
      if (!value && value !== 0) {
        callback(new Error("请选择运营状态"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      dialog_visible: props.isVisible,
      formDialog: { siteId: props.activeSiteId },
      treeProps1: {value: 'id', label: 'name', children: 'children'},
      treeProps: {value: 'id', label: 'typeName', children: 'children'},

      siteArray: [],
      modelArray: [],
      handleMenuArray: [],
      parentNodeArray: [],
      accessTypeArray: [{id: 1,name: "直连设备"},{id: 2,name: "网关设备"},{id: 3,name: "网关子设备"}],
      operateStatusArray: [{id: 0,name: "未知"},{id: 1,name: "投运"},{id: 2,name: "检修"},{id: 3,name: "退役"}],

      rules: {
        typeId: [{required: true, trigger: "change", validator: validateTypeId }],
        modelId: [{required: true, trigger: "change", validator: validateModelId }],
        siteId: [{required: true, trigger: "change", validator: validateSiteId }],
        deviceName: [{required: true, trigger: "change", validator: validateDeviceName }],
        // deviceNumber: [{required: true, trigger: "change", validator: validateDeviceNumber }],
        accessType: [{required: true, trigger: "change", validator: validateAccessType }],
        operateStatus: [{required: true, trigger: "change", validator: validateOperateStatus }],
      }
    })

    const formDialogRef = ref(null);
    const modelDynamicFieldComRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          // 新增编辑设备数据
          that.listLoading = true;
          modelDynamicFieldComRef.value.formSubmitFun().then(res=>{
            let formDialog = JSON.parse(JSON.stringify(that.formDialog));
            let readwriteObject = res.readwriteObject ? res.readwriteObject : {};
            saveDevice({ ...formDialog,readwriteObject: readwriteObject }).then(()=>{
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

    const initParamConfigFun = () => {
      if(props.editingType){
        that.listLoading = true;
        findDeviceBasicInfoById({ deviceId: props.activeDeviceId,timer: new Date() }).then(res=>{
          let formDialog = {};
          let deviceInfo = res.data ? res.data : {};
          delete deviceInfo.reaMap;

          for(let key in deviceInfo){
            if(deviceInfo[key] || deviceInfo[key] === 0){
              try {
                let obj = JSON.parse(deviceInfo[key]);
                if(typeof obj === "object") for(let el in obj) formDialog[el] = obj[el];
              } catch (e) {
                // console.log(e);
              }
              formDialog[key] = deviceInfo[key];
            }
          }
          // console.log(formDialog);
          that.formDialog = JSON.parse(JSON.stringify(formDialog));
          queryDeviceAssetList();

          nextTick(()=>{
            if(formDialog.readwriteObject){
              modelDynamicFieldComRef.value.formDialog = JSON.parse(formDialog.readwriteObject);
              // console.log(modelDynamicFieldComRef.value[i].formDialog);
            }
            that.listLoading = false;
          })
        }).catch(()=>{
          that.listLoading = false;
        })
      } else {
        queryAssetTypeList();
        queryDeviceAssetList();
      }
    }

    // 获取资产分类列表
    const queryAssetTypeList = ()=>{
      getAssetTypeList({ ids: [3], timer: new Date(),pageName: "AddDeviceDialog" }).then(res=>{
        let assetTypeList = res.data ? res.data : [];
        that.handleMenuArray = setTreeData(assetTypeList);
      })
    }

    // 根据分类id获取模型名称列表
    const queryModelNameListByTypeId = ()=>{
      getModelNameListByTypeId({ typeId: that.formDialog.typeId }).then(res=>{
        that.modelArray = res.data ? res.data : [];
        that.formDialog.modelId = "";
      }).catch(()=>{
        that.modelArray = [];
        that.formDialog.modelId = "";
      })
    }

    // 根据用户id查询站点列表信息
    const querySiteInfoListByUserId = ()=>{
      findSiteInfoListByUserId({ timer: new Date() }).then(res=>{
        that.siteArray = res.data ? res.data : [];
      })
    }

    const changeSiteFun = ()=>{
      that.formDialog.parentId = "";
      queryDeviceAssetList();
    }

    // 根据站点id查询设备资产父节点数据
    const queryDeviceAssetList = ()=>{
      if(!that.formDialog.siteId) return
      findDeviceAssetList({ siteId: that.formDialog.siteId }).then(res=>{
        let parentNodeArray = res.data ? res.data : [];
        parentNodeArray.forEach(item=>{
          if (item.parentId === that.formDialog.siteId) item.parentId = "";
        })
        that.parentNodeArray = setTreeData(parentNodeArray);
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
      querySiteInfoListByUserId();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, queryAssetTypeList, queryModelNameListByTypeId,
     querySiteInfoListByUserId, queryDeviceAssetList, changeSiteFun, modelDynamicFieldComRef}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item:last-child {
  margin-bottom: 18px !important;
}

.maxHeightClass{
  max-height: 580px;
  overflow-y: auto;
}
</style>
