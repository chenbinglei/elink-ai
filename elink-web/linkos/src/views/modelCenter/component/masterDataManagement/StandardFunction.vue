<template>
  <div class="app-container-right">
    <div v-loading="listLoading" class="tableContent" style="border-top: none">
      <div class="tableCenter">
        <el-table :data="list" :max-height="contentHeight" border stripe>
          <el-table-column align="center" label="序号" type="index" width="80">
            <template #default="{$index}">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="功能名称" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.functionName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="标识符">
            <template #default="{ row }">{{ $filters.moreData(row.functionLogo) }}</template>
          </el-table-column>
          <el-table-column align="center" label="数据类型">
            <template #default="{ row }">{{ $filters.dataType(row.dataType)}}</template>
          </el-table-column>
          <el-table-column align="center" label="数据值定义" show-overflow-tooltip>
            <template #default="{ row }">
              <data-value-definition :dataType="row.dataType" :dataObject="row.dataObject"></data-value-definition>
            </template>
          </el-table-column>
          <el-table-column align="center" label="功能类型">
            <template #header>
              <div class="table_header flex-jc-ai-center">
                <span class="table_header_text">功能类型</span>
                <FilterDropDown :objectData="functionTypeFilter" :formInline="form_inline" @changEvent="changEvent"></FilterDropDown>
              </div>
            </template>
            <template #default="{ row }">{{ $filters.functionType(row.functionType)}}</template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template #default="{ row }">
              <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" @click="clickOperateBut(2, row)">复制</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" type="danger" @click="clickOperateBut(3, row)">删除</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination">
        <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
      </div>
    </div>

    <AddStandardFunDialog v-if="addStandardFunVisible" v-model:isVisible="addStandardFunVisible" :titleName="titleName" :activeDeviceTypeId="activeDeviceTypeId"
                          :isClickCopy="isClickCopy" :activeFunctionId="activeFunctionId" @changeEvent="listArray('resetPage')"></AddStandardFunDialog>
  </div>
</template>

<script>
import {ElMessage, ElMessageBox, ElSwitch} from "element-plus";
import {FilterDropDown} from "@/components/FromFilterComponent";
import {DataValueDefinition,AddStandardFunDialog} from "./component/index";
import {h, reactive, toRefs, ref, watch, defineComponent, onMounted} from "vue";
import {deleteFunctionById, queryFunctionList} from "@/api/modelCenter/standardFunction";

export default defineComponent({
  name: "StandardFunctionTable",
  components:{AddStandardFunDialog,DataValueDefinition,FilterDropDown},
  props: {
    activeDeviceTypeId: {
      type: [Number, String],
      default: ""
    },
    contentHeight:{
      type: Number,
      default: 380
    },
    formInline:{
      type: Object,
      default:()=>{
        return {}
      }
    }
  },
  setup(props) {

    const deleteLogoRef = ref(false);

    const that = reactive({
      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false, // 表格加载
      functionTypeFilter:{
        title: "功能类型",
        fieldName: "functionType",
        list: [{id: 1, name: "遥测"}, {id: 2, name: "遥信"}, {id: 3, name: "遥脉"}, {id: 4, name: "遥控"}, {id: 5, name: "遥调"}]
      },
      form_inline: {},
      old_form_inline: {},
      isClickCopy: false,
      activeFunctionId: "",
      titleName: "添加标准功能点",
      addStandardFunVisible: false,
    })

    // 查询模型标准功能列表
    const listArray = (reset) => {
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      let form_inline = JSON.parse(JSON.stringify(that.form_inline));
      if(form_inline.functionType)form_inline.functionType = form_inline.functionType.join(',');
      queryFunctionList({ page: that.currentPage, size: that.pageNum, typeId: props.activeDeviceTypeId, ...props.formInline,...form_inline }).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886) return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const changEvent = (data)=>{
      // console.log(data);
      if(data.type === "FilterDropDown"){
        that.form_inline[data.fieldName] = data.value;
        // console.log(that.form_inline);
        listArray("resetPage");
      }
    }

    const clickAddButtonFun = ()=>{
      that.isClickCopy = false;
      that.activeFunctionId = "";
      that.titleName = "添加标准功能点";
      that.addStandardFunVisible = true;
    }

    const clickOperateBut = (operate,row)=>{

      if(operate === 1){
        that.isClickCopy = false;
        that.activeFunctionId = row.id;
        that.titleName = "编辑标准功能点";
        that.addStandardFunVisible = true;
      }

      if(operate === 2){
        that.isClickCopy = true;
        that.activeFunctionId = row.id;
        that.titleName = "添加标准功能点";
        that.addStandardFunVisible = true;
      }

      if(operate === 3){
        ElMessageBox({
          type: 'warning',
          title: "删除提示",
          showCancelButton: true,
          cancelButtonText: '取消',
          confirmButtonText: '确定',
          customClass: "deleteMsgBoxClass",
          message: ()=> h('div',null,[
            h('div', null, [
              h('span', null, '确定删除'),
              h('span', { style: 'color: #FF7B7B;font-weight: bold' }, row.functionName),
              h('span', null, '吗？'),
            ]),
            h('div', { style: 'display: flex;align-items: center' }, [
              h('span', null, '删除关联数据：'),
              h(ElSwitch,{
                modelValue: deleteLogoRef.value,
                'onUpdate:modelValue': (val) => {
                  deleteLogoRef.value = val;
                }
              })
            ]),
          ]),
          beforeClose: (action, instance, done)=> {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteFunctionById({ id: row.id,deleteLogo: deleteLogoRef.value }).then(()=>{
                done();
                instance.confirmButtonLoading = false;
              }).catch(() => {
                instance.confirmButtonText = '确定';
                instance.confirmButtonLoading = false;
              });
            } else {
              done();
            }
          }
        }).then(()=>{
          listArray("resetPage");
          deleteLogoRef.value = false;
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    const watchActiveSortId = watch(() => props.activeDeviceTypeId, (newActiveSortId) => {
      that.form_inline = JSON.parse(JSON.stringify(that.old_form_inline));
      listArray('resetPage');
    }, { deep: true })

    onMounted(() => {
      that.old_form_inline = JSON.parse(JSON.stringify(that.form_inline));
      if(props.activeDeviceTypeId) listArray();
    })

    return {...toRefs(that), watchActiveSortId, listArray, clickAddButtonFun, clickOperateBut, deleteLogoRef, changEvent }
  }
})
</script>

<style scoped lang="scss">
</style>
