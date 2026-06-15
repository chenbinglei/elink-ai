<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName"
          disabledLoading width="680" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="变量名：" prop="name">
            <el-input v-model="formDialog.name" placeholder="请输入变量名" type="text"/>
          </el-form-item>
          <el-form-item label="类型：">
            <el-select v-model="formDialog.type" placeholder="请选择类型" clearable @change="changeGraphSourceId({ isClearDataPointId: true })">
              <el-option v-for="item in varTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关联数据源：" prop="graphSourceId">
            <el-select v-model="formDialog.graphSourceId" placeholder="请选择关联数据源" @change="changeGraphSourceId({ isClearDataPointId: true })">
              <el-option v-for="item in graphSourceIdArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关联数据：">
            <el-col :span="formDialog.type === 'ArrayList' || formDialog.type === 'ArrayString' ? 11 : 24">
              <el-form-item prop="dataPointId">
                <el-tree-select class="leftArrowClass" v-model="formDialog.dataPointId" :indent="0" :data="dataPointList" :props="treeProps" check-strictly
                                filterable default-expand-all :render-after-expand="false" placeholder="请选择关联数据" @change="dataPointIdChangeFun"/>
              </el-form-item>
            </el-col>
            <template v-if="formDialog.type === 'ArrayList' || formDialog.type === 'ArrayString'">
              <el-col :span="2">
                <div class="flex-jc-ai-center">-</div>
              </el-col>
              <el-col :span="11">
                <el-form-item prop="dataPointIndex">
                  <el-input-number v-model="formDialog.dataPointIndex" controls-position="right" placeholder="从0开始"></el-input-number>
                </el-form-item>
              </el-col>
            </template>
          </el-form-item>
          <el-form-item label="描述：">
            <el-input v-model="formDialog.description" :rows="3" placeholder="请输入描述" maxlength="100" type="textarea" show-word-limit/>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {letterNumLine, someCharMap} from "@/utils/validate";
import {setTreeData, treeToArray} from "@/utils";
import {variableTypeArray} from "@/utils/publicParam";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {findGraphSourceByGraphId, saveGraphVariable} from "@/api/2DVisualization/2d_artworkEditor";

export default defineComponent({
  name: "AddVariableDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新建变量"
    },
    activeEditInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateName = (rule, value, callback) => {
      if (!value || !someCharMap(value)) {
        callback(new Error("请输入正确的变量名"));
      } else {
        callback();
      }
    };

    const validateType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择类型"));
      } else {
        callback();
      }
    };

    const validateGraphSourceId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择数据源"));
      } else {
        callback();
      }
    };

    const validateDataPointId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择关联数据"));
      } else {
        callback();
      }
    };

    // const validateDataPointIndex = (rule, value, callback) => {
    //   if (!value && value !== 0) {
    //     callback(new Error("请输入点位"));
    //   } else {
    //     callback();
    //   }
    // };

    const that = reactive({
      listLoading: false,
      dialog_visible: props.isVisible,

      formDialog: {},
      dataPointList: [],
      tableMaxHeight: 320,
      oldDataPointList: [],
      graphSourceIdArray: [],
      varTypeArray: variableTypeArray,
      treeProps:{value: 'id', label: 'chName', children: 'children'},

      rules: {
        name: [{required: true, trigger: "change", validator: validateName }],
        type: [{required: true, trigger: "change", validator: validateType }],
        dataPointId: [{required: true, trigger: "change", validator: validateDataPointId }],
        graphSourceId: [{required: true, trigger: "change", validator: validateGraphSourceId }],
        // dataPointIndex: [{required: true, trigger: "change", validator: validateDataPointIndex }],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          if(formDialog.dataPointId){
            let dataPointIdList = formDialog.dataPointId.split('/');
            formDialog.dataObject = dataPointIdList;
            formDialog.dataPoint = dataPointIdList[dataPointIdList.length - 1];
          }

          for(let key in formDialog) if(!formDialog[key] && formDialog[key]!== 0)formDialog[key] = "";

          // 新增或编辑图模变量数据
          saveGraphVariable({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({type: "success", message: "操作成功", showClose: true});
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 数据源选择发生改变
    const changeGraphSourceId = (data = { isClearDataPointId: true })=>{
      // dataSourceId
      let dataPointList = [];
      let findItem = that.graphSourceIdArray.find(item=> item.id === that.formDialog.graphSourceId);
      // console.log(findItem);

      if(findItem){
        dataPointList = JSON.parse(JSON.stringify(findItem.responseValue ? findItem.responseValue : []));
        // console.log(responseValue);
        if(dataPointList && dataPointList.length){
          dataPointList.forEach(item=>{
            if( that.formDialog.type && item.fieldType !== that.formDialog.type){
              item.disabled = true;
            }
          })
        }
      }

      if(data.isClearDataPointId){
        that.formDialog.dataPointId = "";
        that.formDialog.dataPointIndex = "";
      }
      that.oldDataPointList = JSON.parse(JSON.stringify(dataPointList));
      that.dataPointList = JSON.parse(JSON.stringify(setTreeData(dataPointList)));
      // console.log(that.dataPointList);
    }

    // 获取当前选择的数据类型, 设置数据类型
    const dataPointIdChangeFun = ()=>{
      // console.log(that.oldDataPointList);
      // console.log(that.formDialog.dataPointId);
      let findItem = that.oldDataPointList.find(item => item.id === that.formDialog.dataPointId);
      that.formDialog.type = findItem?.fieldType ?? "";
    }

    const initParamConfigFun = () => {
      let formDialog = Object.assign({}, that.formDialog, props.activeEditInfo);
      if(formDialog.dataObject){
        formDialog.dataObject = JSON.parse(formDialog.dataObject);
        formDialog.dataPointId = formDialog.dataObject.join('/');
      }

      that.formDialog = JSON.parse(JSON.stringify(formDialog));
      queryGraphSourceByGraphId();
    }

    // 根据图模id查询关联数据源数据
    const queryGraphSourceByGraphId = () => {
      findGraphSourceByGraphId({graphId: that.formDialog.graphId}).then(res => {
        let graphSourceIdArray = res.data ? res.data : [];
        graphSourceIdArray.forEach(item=>{
          if(item.responseValue){
            let responseValue = JSON.parse(item.responseValue);
            item.responseValue = treeToArray(responseDataSetIdFun(responseValue));
          }
        })

        // console.log(graphSourceIdArray);
        that.graphSourceIdArray = graphSourceIdArray;
        changeGraphSourceId({ isClearDataPointId: false }); // 初始化执行一次
      })
    }

    const responseDataSetIdFun = (list = [],parentObj = {})=>{
      if(list && list.length){
        for (let i = 0; i < list.length;i++){
          // console.log(list[i]);
          list[i].parentId = parentObj.id;
          list[i].id = `${ parentObj.id ? parentObj.id + '/' : '' }${ list[i].enName }`;

          if(list[i].children && list[i].children.length){
            list[i].children = responseDataSetIdFun(list[i].children,list[i]);
          }
        }
      }
      return list
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

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef, queryGraphSourceByGraphId,
      changeGraphSourceId, responseDataSetIdFun, dataPointIdChangeFun}
  }
})

</script>

<style lang="scss" scoped>

</style>