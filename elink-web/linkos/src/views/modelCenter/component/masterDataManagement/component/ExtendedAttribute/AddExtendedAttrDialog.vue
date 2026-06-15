<template>
  <Dialog v-model:isVisible="dialog_visible" :disabledLoading="!isDetails" :manualEnterClose="isDetails" :listLoading="listLoading" :title="titleName" width="580px"
          :confirmVisible="!isDetails" :cancelText="isDetails?'关闭':'取消'" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px" :disabled="isDetails">
          <el-form-item label="设备类型：">
            <el-tree-select class="leftArrowClass" v-model="formDialog.typeId" :indent="0" :data="handleMenuArray" :props="treeProps"
                            filterable default-expand-all :render-after-expand="false" />
          </el-form-item>

          <el-form-item label="属性名称：" prop="reaName">
            <el-input v-model="formDialog.reaName" maxlength="32" placeholder="请输入属性名称" show-word-limit type="text"></el-input>
          </el-form-item>

          <el-form-item label="英文名称：" prop="fieldName">
            <el-input v-model="formDialog.fieldName" maxlength="32" placeholder="请输入英文名称" show-word-limit type="text"></el-input>
          </el-form-item>

          <el-form-item label="属性类型：" prop="reaType">
            <el-select v-model="formDialog.reaType" clearable placeholder="请选择属性类型" @change="changeReaTypeFun">
              <el-option v-for="item in reaTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="是否必填：" prop="required">
            <el-select v-model="formDialog.required" placeholder="请选择是否必填">
              <el-option v-for="item in requiredArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>

          <template v-if="formDialog.reaType === 1">
            <el-form-item label="取值范围：" required>
              <el-col :span="11">
                <el-form-item prop="minValue">
                  <el-input-number v-model="formDialog.extraValue.minValue" controls-position="right"/>
                </el-form-item>
              </el-col>
              <el-col class="text-align" :span="2"><span class="text-gray">~</span></el-col>
              <el-col :span="11">
                <el-form-item prop="maxValue">
                  <el-input-number v-model="formDialog.extraValue.maxValue" controls-position="right"/>
                </el-form-item>
              </el-col>
            </el-form-item>
            <el-form-item label="单位：">
              <el-input v-model="formDialog.unit" type="text" placeholder="请输入单位" maxlength="16" show-word-limit/>
            </el-form-item>
          </template>

          <template v-if="formDialog.reaType === 3">
            <el-form-item label="是否多选：">
              <el-select v-model="formDialog.extraValue.multiple" clearable placeholder="请选择">
                <el-option v-for="item in requiredArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="选项：" prop="enumArray">
              <el-table :data="formDialog.extraValue.enumArray" max-height="180" border stripe>
                <el-table-column align="center" label="值">
                  <template #default="{ row }">
                    <el-input v-model="row.id" placeholder="请填写值" type="text"></el-input>
                  </template>
                </el-table-column>
                <el-table-column align="center" label="名称">
                  <template #default="{ row }">
                    <el-input v-model="row.name" placeholder="请填写枚举值" type="text"></el-input>
                  </template>
                </el-table-column>
                <el-table-column align="center" width="50">
                  <template #header>
                    <span class="iconfont icon-treeAdd pointer" style="color:#1F74E2" @click="clickTableAddBut"></span>
                  </template>
                  <template #default="{ row,$index }">
                    <span class="iconfont icon-shanchuxiajimokuai pointer" style="color:#EC2020" @click="clickTableDeleteBut($index)"></span>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </template>

          <template v-if="formDialog.reaType === 4">
            <el-form-item label="坐标系：" prop="coordinateSystem">
              <el-select v-model="formDialog.extraValue.coordinateSystem" clearable placeholder="请选择坐标系">
                <el-option v-for="item in coordinateSystemArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </template>

          <template v-if="formDialog.reaType === 5">
            <el-form-item label="true显示文字：">
              <el-input v-model="formDialog.extraValue.trueValue" type="text" placeholder="请输入true显示文字" maxlength="100" show-word-limit/>
            </el-form-item>

            <el-form-item label="false显示文字：">
              <el-input v-model="formDialog.extraValue.falseValue" type="text" placeholder="请输入false显示文字" maxlength="100" show-word-limit/>
            </el-form-item>
          </template>

          <template v-if="formDialog.reaType === 6">
            <el-form-item label="分隔符：" prop="splitTally">
              <el-select v-model="formDialog.extraValue.splitTally" placeholder="请选择分隔符">
                <el-option v-for="item in splitTallyArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="时间格式：" prop="timeFormat">
              <el-select v-model="formDialog.extraValue.timeFormat" placeholder="请选择时间格式">
                <el-option v-for="item in timeFormatArray" :key="item" :label="item" :value="item"></el-option>
              </el-select>
            </el-form-item>
          </template>

        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {setTreeData} from "@/utils";
import {ElMessage} from "element-plus";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import {letterNumStartAndCompose, someCharmap} from "@/utils/validate";
import {findSeaDetailById, saveSea} from "@/api/modelCenter/extendedAttribute";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddExtendedAttrDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加扩展属性"
    },
    activeDeviceTypeId:{
      type: [Number,String],
      default: ""
    },
    activeExtendedAttrId:{
      type: [Number,String],
      default: ""
    },
    // 是否只展示详情
    isDetails:{
      type: Boolean,
      default: false
    },
    isClickCopy:{
      type: Boolean,
      default: false
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const validateReaName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的属性名称"));
      } else {
        callback();
      }
    };

    const validateFieldName = (rule, value, callback) => {
      if (!value || !letterNumStartAndCompose(value)) {
        callback(new Error("请输入正确的英文名称"));
      } else {
        callback();
      }
    };

    const validateReaType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择属性类型"));
      } else {
        callback();
      }
    };

    const validateCoordinateSystem = (rule, value, callback) => {
      if (!that.formDialog.extraValue.coordinateSystem) {
        callback(new Error("请选择坐标系"));
      } else {
        callback();
      }
    };

    const validateSplitTally = (rule, value, callback) => {
      if (!that.formDialog.extraValue.splitTally) {
        callback(new Error("请选择时间分隔符"));
      } else {
        callback();
      }
    };

    const validateTimeFormat = (rule, value, callback) => {
      if (!that.formDialog.extraValue.timeFormat) {
        callback(new Error("请选择时间格式"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      formDialog: {
        extraValue: {},
        required: false,
        typeId: props.activeDeviceTypeId
      },
      handleMenuArray: [],
      dialog_visible: props.isVisible,
      requiredArray: [{id: false, name: "否"},{id: true, name: "是"}],
      readWriteTypeArray: [{id: 1, name: "只读"},{id: 2, name: "读写"}],
      treeProps:{value: 'id', label: 'typeName', children: 'children'},
      timeFormatArray: ["yyyy-MM-dd","yyyy-MM-dd HH:mm","yyyy-MM-dd HH:mm:ss"],
      splitTallyArray: [{id: 1, name: "-"},{id: 2, name: "/"},{id: 3, name: "文字"}],
      coordinateSystemArray: [{id: 1, name: "WGS-84"},{id: 2, name: "百度地图"},{id: 3, name: "高德地图"},{id: 4, name: "腾讯地图"}],
      reaTypeArray: [{id: 1, name: "数值"},{id: 2, name: "文字"},{id: 3, name: "选项"},{id: 4, name: "位置"},{id: 5, name: "开关"}, {id: 6, name: "时间"},{id: 7, name: "文本"}],
      rules:{
        reaName:[{required: true, trigger: "change", validator: validateReaName }],
        reaType:[{required: true, trigger: "change", validator: validateReaType }],
        fieldName:[{required: true, trigger: "change", validator: validateFieldName }],
        splitTally:[{required: true, trigger: "change", validator: validateSplitTally }],
        timeFormat:[{required: true, trigger: "change", validator: validateTimeFormat }],
        coordinateSystem:[{required: true, trigger: "change", validator: validateCoordinateSystem }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          // 新增编辑模型扩展属性
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          saveSea({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 枚举值添加数据
    const clickTableAddBut = ()=>{
      if (!that.formDialog.extraValue.enumArray) that.formDialog.extraValue.enumArray = [];
      that.formDialog.extraValue.enumArray.push({ });
    }

    // 枚举值删除数据
    const clickTableDeleteBut = (index)=>{
      that.formDialog.extraValue.enumArray.splice(index, 1);
    }

    const changeReaTypeFun = ()=>{
      that.formDialog.unit = "";
      that.formDialog.extraValue = {};
    }

    // 初始化参数配置
    const initParamConfigFun = ()=>{
      // 根据主键id查询标准功能详情
      if(props.activeExtendedAttrId){
        that.listLoading = true;
        findSeaDetailById({ id: props.activeExtendedAttrId }).then(res=>{
          let formDialog = res.data ? res.data : {};
          for(let key in formDialog){
            if(formDialog[key]){
              try {
                formDialog[key] = JSON.parse(formDialog[key]);
              } catch (e) {}
            } else {
              if(typeof formDialog[key] === "string"){
                formDialog[key] = "";
              }
            }
          }
          // console.log(formDialog);
          if(formDialog.typeId) formDialog.typeId = String(formDialog.typeId);
          that.formDialog = JSON.parse(JSON.stringify(formDialog));
          // 如果为复制，删除对应的id
          if(props.isClickCopy)delete that.formDialog.id;
          that.listLoading = false;
        }).catch(()=>{
          that.listLoading = false;
        })
      }
    }

    // 获取设备类型列表
    const queryAssetTypeList = ()=>{
      getAssetTypeList({ timer: new Date(),pageName: "AddExtendedAttrDialog" }).then(res=>{
        let assetTypeList = res.data ? res.data : [];
        that.handleMenuArray = setTreeData(assetTypeList);
        that.oldHandleMenuArray = JSON.parse(JSON.stringify(assetTypeList));
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      initParamConfigFun();
      queryAssetTypeList();
    })

    return {...toRefs(that),watchVisible,watchDialogVisible,formDialogRef,saveDialog,initParamConfigFun, clickTableAddBut,clickTableDeleteBut, changeReaTypeFun, queryAssetTypeList}
  }
})
</script>

<style scoped>

</style>
