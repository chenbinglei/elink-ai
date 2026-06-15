<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading"
    :title="titleName" width="620px" :confirmVisible="isFunctionType !== 3"
    :cancelText="isFunctionType === 3 ? '关闭' : '取消'" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formdialog" :rules="rules" label-width="130px"
          :disabled="isFunctionType === 3">
          <el-form-item label="设备类型11：" v-if="isFunctionType !== 4">
            <el-tree-select class="leftArrowClass" v-model="formdialog.typeId" :indent="0" :data="handleMenuArray"
              :props="treeProps" filterable default-expand-all :render-after-expand="false"
              @change="queryPileRealFieldList" />
          </el-form-item>
          <el-form-item label="功能名称：" prop="functionName">
            <el-input v-model="formdialog.functionName" maxlength="32" placeholder="请输入功能名称" show-word-limit type="text"
              :disabled="isFunctionType === 4"></el-input>
          </el-form-item>
          <el-form-item label="功能标识：" prop="functionLogo">
            <el-input v-model="formdialog.functionLogo" maxlength="32" placeholder="请输入功能标识" show-word-limit type="text"
              :disabled="!!activeFunctionId && !isClickCopy"></el-input>
          </el-form-item>
          <el-form-item label="功能类型：" prop="functionType" v-if="isFunctionType !== 4">
            <el-select v-model="formdialog.functionType" clearable placeholder="请选择功能类型">
              <el-option v-for="item in functionTypeArray" :key="item.id" :label="item.name"
                :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="数据类型：" prop="dataType">
            <el-select v-model="formdialog.dataType" clearable :disabled="formdialog.id && isFunctionType !== 4"
              placeholder="请选择数据类型" @change="changeDataType">
              <el-option v-for="item in dataTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>

          <!--          充电桩需要绑定 关联协议-->
          <template v-if="formdialog.typeId >= 28 && formdialog.typeId <= 30 && isFunctionType !== 4">
            <el-form-item label="关联协议：" prop="fieldCode">
              <el-select v-model="formdialog.fieldCode" placeholder="请选择关联协议"
                :disabled="!formdialog.dataType || formdialog.id">
                <el-option v-for="item in fieldCodeArray" :key="item.fieldCode" :label="item.fieldName"
                  :value="item.fieldCode"></el-option>
              </el-select>
            </el-form-item>
          </template>

          <template v-if="formdialog.dataType === 8">
            <!--            <el-form-item label="元素数量：" prop="elementNum">-->
            <!--              <el-input-number v-model="formdialog.dataObject.elementNum" :min="1" :max="512" controls-position="right"/>-->
            <!--            </el-form-item>-->
            <el-form-item label="元素类型：" prop="elementType">
              <el-select v-model="formdialog.dataObject.elementType" clearable placeholder="请选择数据类型">
                <template v-for="item in dataTypeArray" :key="item.id">
                  <el-option v-if="!item.type" :label="item.name" :value="item.id"></el-option>
                </template>
              </el-select>
            </el-form-item>
          </template>

          <template v-if="formdialog.dataType < 5 || formdialog.dataObject?.elementType < 5">
            <el-form-item label="精度："
              v-if="formdialog.dataType === 3 || formdialog.dataType === 4 || (formdialog.dataType === 8 && (formdialog.dataObject?.elementType === 3 || formdialog.dataObject?.elementType === 4))">
              <el-select v-model="formdialog.accuracy" clearable placeholder="请选择精度">
                <el-option v-for="item in accuracyArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="单位：">
              <el-input v-model="formdialog.unit" type="text" placeholder="请输入单位" maxlength="16" show-word-limit />
            </el-form-item>
          </template>

          <template v-if="formdialog.dataType === 5">
            <el-form-item label="枚举项：" prop="enumArray">
              <div class="alterText">最多 {{ maxNumArrayLength }}项</div>
              <el-table :data="formdialog.dataObject.enumArray" max-height="180" border stripe>
                <el-table-column align="center" label="值">
                  <template #default="{ row }">
                    <!--                    <el-input v-model="row.id" placeholder="请填写值" type="text"></el-input>-->
                    <el-input-number v-model="row.id" controls-position="right" placeholder="请填写值"></el-input-number>
                  </template>
                </el-table-column>
                <el-table-column align="center" label="枚举值">
                  <template #default="{ row }">
                    <el-input v-model="row.name" placeholder="请填写枚举值" type="text"></el-input>
                  </template>
                </el-table-column>
                <el-table-column align="center" width="50">
                  <template #header>
                    <span class="iconfont icon-treeAdd pointer" style="color:#1F74E2" @click="clickTableAddBut"></span>
                  </template>
                  <template #default="{ row, $index }">
                    <span class="iconfont icon-shanchuxiajimokuai pointer" style="color:#EC2020"
                      @click="clickTableDeleteBut($index)"></span>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </template>

          <template v-if="
            (formdialog.dataType === 1 ||
              formdialog.dataType === 2 ||
              formdialog.dataType === 3 ||
              formdialog.dataType === 4 ||
              formdialog.dataType === 8 ||
              formdialog.dataType === 9) &&
            (
              formdialog.dataType !== 8 ||
              (formdialog.dataType === 8 &&
                [1, 2, 3, 4].includes(formdialog.dataObject.elementType))
            )
          ">
            <el-form-item label="取值范围：" prop="valueRange">
              <div style="display: flex; gap: 10px;">
                <el-input-number v-model="formdialog.valueRange.minValue" controls-position="right"
                  placeholder="最小值"></el-input-number>
                ~
                <el-input-number v-model="formdialog.valueRange.maxValue" controls-position="right"
                  placeholder="最大值"></el-input-number>
              </div>
            </el-form-item>
          </template>
          <template v-if="formdialog.dataType === 6">
            <el-form-item label="布尔值：" required>
              <el-col :span="24">
                <el-form-item label="true：" label-width="60" prop="trueValue">
                  <el-input v-model="formdialog.dataObject.trueValue" type="text" placeholder="请输入true值" maxlength="100"
                    show-word-limit />
                </el-form-item>
              </el-col>
              <el-col class="text-center" :span="2">
                <div class="" style="height: 16px"></div>
              </el-col>
              <el-col :span="24">
                <el-form-item label="false：" label-width="60" prop="falseValue">
                  <el-input v-model="formdialog.dataObject.falseValue" type="text" placeholder="请输入false值"
                    maxlength="100" show-word-limit />
                </el-form-item>
              </el-col>
            </el-form-item>
          </template>


          <template
            v-if="formdialog.dataType === 9 || (formdialog.dataType === 8 && formdialog.dataObject.elementType === 9)">
            <el-form-item label="时间格式：" prop="timeFormat">
              <el-select v-model="formdialog.dataObject.timeFormat" clearable placeholder="请选择时间格式" style="width: 100%">
                <el-option v-for="item in timeFormatArray" :key="item.id" :label="item.name"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </template>

          <el-form-item label="描述：" v-if="isFunctionType !== 4">
            <el-input v-model="formdialog.functionDesc" :rows="3" type="textarea" placeholder="请输入描述" maxlength="100"
              show-word-limit />
          </el-form-item>

        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import { setTreeData } from "@/utils";
import { ElMessage } from "element-plus";
import { getAssetTypeList } from "@/api/modelCenter/modelManagement";
import { someCharmap, validateStartLowerCaseAndCompose } from "@/utils/validate";
import { getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent } from "vue";
import { findFunctionDetailById, getPileRealFieldList, saveFunction } from "@/api/modelCenter/standardFunction";

export default defineComponent({
  name: "AddStandardFunDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加标准功能点"
    },
    activeDeviceTypeId: {
      type: [Number, String],
      default: ""
    },
    activeFunctionId: {
      type: [Number, String],
      default: ""
    },
    // 1：新增  2： 编辑  3: 查看详情  4: 修改数据定义
    isFunctionType: {
      type: Number,
      default: 1
    },
    // 修改数据定义 使用
    activeFunctionInfo: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 是否点击复制
    isClickCopy: {
      type: Boolean,
      default: false
    }
  },
  setup (props) {

    const { emit } = getCurrentInstance();

    const validateFunctionName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的功能名称"));
      } else {
        callback();
      }
    };

    const validateFunctionLogo = (rule, value, callback) => {
      if (!value || !validateStartLowerCaseAndCompose(value)) {
        callback(new Error("请输入正确的功能标识"));
      } else {
        callback();
      }
    };

    const validateFunctionType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择功能类型"));
      } else {
        callback();
      }
    };

    const validateFieldCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择关联协议"));
      } else {
        callback();
      }
    };

    const validateDataType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择数据类型"));
      } else {
        callback();
      }
    };

    const validateEnumArray = (rule, value, callback) => {
      if (!that.formdialog.dataObject.enumArray || !that.formdialog.dataObject.enumArray.length) {
        callback(new Error("请添加枚举项"));
      } else {
        callback();
      }
    };

    const validateTrueValue = (rule, value, callback) => {
      if (!that.formdialog.dataObject.trueValue) {
        callback(new Error("请填写true值"));
      } else {
        callback();
      }
    };

    const validateFalseValue = (rule, value, callback) => {
      if (!that.formdialog.dataObject.falseValue) {
        callback(new Error("请填写false值"));
      } else {
        callback();
      }
    };

    const validateTimeFormat = (rule, value, callback) => {
      if (!that.formdialog.dataObject.timeFormat) {
        callback(new Error("请选择时间格式"));
      } else {
        callback();
      }
    };

    const validateElementNum = (rule, value, callback) => {
      if (!that.formdialog.dataObject.elementNum) {
        callback(new Error("请填写元素数量"));
      } else {
        callback();
      }
    };

    const validateElementType = (rule, value, callback) => {
      if (!that.formdialog.dataObject.elementType) {
        callback(new Error("请选择元素类型"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formdialog: {
        dataObject: {},  // 数据对象
        typeId: props.activeDeviceTypeId,
        valueRange: {
        
        }
      },

      fieldCodeArray: [],
      listLoading: false,
      handleMenuArray: [],
      oldHandleMenuArray: [],
      dialog_visible: props.isVisible,
      treeProps: { value: 'id', label: 'typeName', children: 'children' },

      maxNumArrayLength: 100, // 枚举值最大条数
      timeFormatArray: [{ id: 1, name: "UTC时间戳（毫秒级）" }],
      accuracyArray: [{ id: 1, name: "1" }, { id: 2, name: "0.1" }, { id: 3, name: "0.01" }, { id: 4, name: "0.001" }, { id: 5, name: "0.0001" }, { id: 6, name: "0.00001" }],
      functionTypeArray: [{ id: 1, name: "遥测" }, { id: 2, name: "遥信" }, { id: 3, name: "遥脉" }, { id: 4, name: "遥控" }, { id: 5, name: "遥调" }],

      dataTypeArray: [
        { id: 1, name: "int32(整数型)" },
        { id: 2, name: "int64(长整数型)" },
        { id: 3, name: "float(单精度浮点型)" },
        { id: 4, name: "double(双精度浮点型)" },
        { id: 5, name: "enum(枚举)", type: "element" },
        { id: 6, name: "bool(布尔)", type: "element" },
        { id: 8, name: "array(数组)", type: "element" },
        { id: 9, name: "date(时间)" }
      ],
      rules: {
        functionName: [{ required: true, trigger: "change", validator: validateFunctionName }],
        functionLogo: [{ required: true, trigger: "change", validator: validateFunctionLogo }],
        functionType: [{ required: true, trigger: "change", validator: validateFunctionType }],
        dataType: [{ required: true, trigger: "change", validator: validateDataType }],
        enumArray: [{ required: true, trigger: "change", validator: validateEnumArray }],
        trueValue: [{ required: true, trigger: "change", validator: validateTrueValue }],
        falseValue: [{ required: true, trigger: "change", validator: validateFalseValue }],
        timeFormat: [{ required: true, trigger: "change", validator: validateTimeFormat }],
        elementNum: [{ required: true, trigger: "change", validator: validateElementNum }],
        elementType: [{ required: true, trigger: "change", validator: validateElementType }],
        fieldCode: [{ required: true, trigger: "change", validator: validateFieldCode }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formdialog));
          if (formDialog.typeId >= 28 && formDialog.typeId <= 30) {
            let findItem = that.fieldCodeArray.find(item => item.fieldCode === formDialog.fieldCode);
            formDialog.fieldType = findItem.fieldType;
          }
          console.log(formDialog);
          // 新增编辑模型标准功能
          saveFunction({ ...formDialog }, props.isFunctionType).then(() => {
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(() => {
            that.listLoading = false;
          })
        }
      })
    }

    const changeDataType = () => {
      queryPileRealFieldList();
      that.formdialog.dataObject = {};
      that.formdialog.fieldCode = "";
      that.formdialog.fieldType = "";
    }

    // 枚举值添加数据
    const clickTableAddBut = () => {
      if (!that.formdialog.dataObject.enumArray) that.formdialog.dataObject.enumArray = [];
      if (that.formdialog.dataObject.enumArray.length < that.maxNumArrayLength) {
        that.formdialog.dataObject.enumArray.push({});
      } else {
        ElMessage({ type: "error", showClose: true, message: "最多只能添加100项！" });
      }
    }

    // 枚举值删除数据
    const clickTableDeleteBut = (index) => {
      that.formdialog.dataObject.enumArray.splice(index, 1);
    }

    // 初始化参数配置
    const initParamConfigFun = () => {


      if (props.activeFunctionId && props.isFunctionType <= 2) {
        
        // 根据主键id查询标准功能详情
        that.listLoading = true;
        findFunctionDetailById({ id: props.activeFunctionId }).then(res => {
          let formDialog = res.data ? res.data : {};
          if (formDialog.dataObject) formDialog.dataObject = JSON.parse(formDialog.dataObject);
          formDialog.valueRange = JSON.parse(formDialog.valueRange || '{}');
          

          // for(let key in formDialog)if(!formDialog[key]) formDialog[key] = "";
          that.formdialog = JSON.parse(JSON.stringify(formDialog));
          queryPileRealFieldList();
          // 如果为复制，删除对应的id
          if (props.isClickCopy) delete that.formdialog.id;
          that.listLoading = false;
        }).catch(() => {
          that.listLoading = false;
        })
      }

      if (props.isFunctionType >= 3) {
        let formDialog = JSON.parse(JSON.stringify(props.activeFunctionInfo));
        if (formDialog.dataObject) formDialog.dataObject = JSON.parse(formDialog.dataObject);
          formDialog.valueRange = JSON.parse(formDialog.valueRange || '{}');
        that.formdialog = JSON.parse(JSON.stringify(formDialog));
      }
    }

    // 获取资产分类列表
    const queryAssetTypeList = () => {
      getAssetTypeList({ timer: new Date(), pageName: "AddStandardFunDialog" }).then(res => {
        let assetTypeList = res.data ? res.data : [];
        that.handleMenuArray = setTreeData(assetTypeList);
        that.oldHandleMenuArray = JSON.parse(JSON.stringify(assetTypeList));
      })
    }

    // 获取电桩协议字段列表
    const queryPileRealFieldList = () => {
      if (that.formdialog.typeId > 30 || that.formdialog.typeId < 28) return
      getPileRealFieldList({ timer: new Date(), dataType: that.formdialog.dataType }).then(res => {
        that.fieldCodeArray = res.data;
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
      if (props.isFunctionType !== 4) queryAssetTypeList();
    })

    return {
      ...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, queryAssetTypeList, clickTableAddBut, clickTableDeleteBut,
      queryPileRealFieldList, changeDataType
    }
  }
})
</script>

<style scoped lang="scss">
.alterText {
  font-size: 12px;
  color: #DBDBDD;
}
</style>
