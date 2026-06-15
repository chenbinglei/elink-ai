<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="520" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="上传文件：" prop="fileList">
            <UploadFileCustom ref="uploadFileCustomRef" v-model:fileArray="formDialog.fileList" :acceptType="acceptType">
              <template #tip_content>
                <div class="el-upload__tip">
                  <span style="margin-right: 4px">格式为.xlsx 最大2MB</span>
                  <el-link type="primary" :underline="false" @click="downloadTemplate">模板下载</el-link>
                </div>
              </template>
            </UploadFileCustom>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {useRoute} from "vue-router";
import {ElMessage} from "element-plus";
import {readWorkbookFromLocalFile} from "@/utils";
import {variableTypeArray} from "@/utils/publicParam";
import {exportCustomExcel} from "@/common/exportExcel";
import UploadFileCustom from "@/components/component/UploadFileCustom.vue";
import {findGraphVariableByGraphId,saveAllGraphVariable} from "@/api/2DVisualization/2d_artworkEditor";
import {reactive, toRefs, watch, getCurrentInstance, defineComponent, onMounted, ref} from 'vue';

export default defineComponent({
  name: 'ImportVariablesDialog',
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
  },
  components:{UploadFileCustom},
  setup(props) {
    const route = useRoute();
    const {emit} = getCurrentInstance();

    const validateFileList = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请上传变量文件"));
      } else {
        callback();
      }
    };

    const that = reactive({
      list: [],
      formDialog: {},
      listLoading: false,
      titleName: "导入变量",
      acceptType: ".xlsx",
      activeGraphId: route.query.id, // 当前图模id
      dialog_visible: props.isVisible,
      rules: {
        fileList: [{required: true, trigger: "change", validator: validateFileList }],
      }
    })

    // 根据图模id查询关联变量数据
    const queryGraphVariableByGraphId = () => {
      that.listLoading = true;
      findGraphVariableByGraphId({ graphId: that.activeGraphId,tiemr: new Date() }).then(res => {
        that.list = res.data;
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.list = [];
      })
    }

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
         readWorkbookFromLocalFile(that.formDialog.fileList[0].raw).then(res=>{
           // console.log(res);
           let graphVariableChangeVos = [];
           let var_list = res.body && res.body.length ? res.body[0] : [];
           if(var_list && var_list.length) var_list.shift(); // 删除第一列数据
           // console.log(var_list);
           var_list.forEach(item=>{
             if(item.name){
               let findItem_a = that.list.find(ts=> ts.name === item.name);
               let findItem_b = graphVariableChangeVos.find(ts=> ts.name === item.name);
               if(!findItem_a && !findItem_b){
                 graphVariableChangeVos.push({graphId: that.activeGraphId,name: item.name, type: item.type, description: item.description});
               }
             }
           })

           // console.log(graphVariableChangeVos);
           // graphVariableChangeVos = [
           //   {graphId: that.activeGraphId,name:"list_008",type:"String",description:"请查看上传文件数据"}
           // ]
           if(!graphVariableChangeVos || !graphVariableChangeVos.length){
             ElMessage({type: 'error', showClose: true, message: '请仔细查看上传文件数据！'});
             that.listLoading = false;
             return
           }

           // 批量编辑图模变量数据
           saveAllGraphVariable({graphVariableChangeVos: graphVariableChangeVos}).then(()=>{
             emit("changeEvent");
             that.dialog_visible = false;
             ElMessage({type: "success", message: "操作成功", showClose: true});
           }).catch(()=>{
             that.listLoading = false;
           })

         }).catch(()=>{
           that.listLoading = false;
         })
        }
      })
    }

    const downloadTemplate = ()=>{
      let dateList = [];
      let tableHeader = [
        {width: 20, key: "name", name: "变量名"},
        {width: 20, key: "type", name: "类型",reaType: 3,options: variableTypeArray},
        // {width: 35, key: "graphSourceId", name: "关联数据源Id"},
        // {width: 35, key: "graphSourceName", name: "关联数据源"},
        // {width: 35, key: "dataObject", name: "数据对象"},
        // {width: 35, key: "dataPoint", name: "数据点"},
        // {width: 15, key: "dataPointIndex", name: "数据点下标",reaType: 1,options:{ minValue: 0,maxValue: 9999999 } },
        {width: 20, key: "description", name: "描述"},
      ];

      exportCustomExcel(tableHeader, dateList,`导入变量模板`,"Sheet1",{
        isInsertEmptyData: true,  // 是否生成空数据
        totalNumber: 500, // 生成空数据的总条数
        isLock: false, // 是否锁定该文件
      });
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(()=>{
      queryGraphVariableByGraphId();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, clickConfirmBut, queryGraphVariableByGraphId, downloadTemplate}
  }
})
</script>

<style lang="scss" scoped>

</style>