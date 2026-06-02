<template>
  <div class="app-container fd-column">

    <div class="app-container-right">
      <div class="header-form">
        <el-form :model="formInline" inline @submit.prevent="listArray">
          <el-row :gutter="16">
            <el-col :md="8" :sm="12" :xl="6" :xs="24">
              <el-form-item label="关键词：" >
                <el-input v-model="formInline.name" clearable placeholder="搜索数据源名称"/>
              </el-form-item>
            </el-col>
            <el-col :md="8" :sm="12" :xl="6" :xs="24">
              <el-form-item>
                <el-button :icon="Search" class="whiteFontButtons" @click="listArray">查询</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <div v-resize="setTableMaxHeight" class="tableContent bg_color_class">
        <TableHeaderTitle title="数据源管理">
          <template #content>
            <el-button :icon="Plus" class="whiteFontButtons" @click="clickAddButFun">添加数据源</el-button>
          </template>
        </TableHeaderTitle>
        <div ref="tableCenterRef" class="tableCenter">
          <el-table v-loading="listLoading" row-key="id" :data="list" :max-height="tableMaxHeight">
            <el-table-column fixed label="序号" type="index" width="80"></el-table-column>
            <el-table-column label="数据源名称">
              <template #default="{ row }">{{ $filters.moreData(row.name) }}</template>
            </el-table-column>
            <el-table-column label="通信方式">
              <template #default="{ row }">{{ $filters.communicationType(row.type) }}</template>
            </el-table-column>
            <el-table-column label="URL" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.url) }}</template>
            </el-table-column>
            <el-table-column label="描述" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.description) }}</template>
            </el-table-column>
            <el-table-column label="更新时间">
              <template #default="{ row }">{{ $filters.moreData(row.updateTime) }}</template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <div class="table_operate_class">
                  <el-link :underline="false" type="primary" @click="clickOperateBut(1, row)">编辑</el-link>
                  <span class="split_line">|</span>
                  <el-link :underline="false" type="danger" @click="clickOperateBut(2, row)">删除</el-link>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <AddDataSourceDialog v-if="addDataSourceVisible" v-model:isVisible="addDataSourceVisible" :titleName="titleName" :dataSourceId="dataSourceId" @changeEvent="listArray" />
    </div>
  </div>
</template>

<script>
import {ElMessage, ElMessageBox} from "element-plus";
import {reactive, ref, toRefs, onMounted} from "vue";
import {Plus, Search} from "@element-plus/icons-vue";
import {AddDataSourceDialog} from "@/views/dataManagement/components";
import {deleteDataSourceByIds, queryDataSourceList} from "@/api/dataManagement/dataSourceManagement";

export default {
  name: "dataSourceManagement",
  components:{AddDataSourceDialog},
  setup() {
    const that = reactive({
      Plus,
      Search,
      formInline: {},
      oldFormInline: {},

      list: [],
      listLoading: false,
      tableMaxHeight: 300,

      dataSourceId: "",
      titleName: "添加数据源",
      addDataSourceVisible: false,
    })

    // 查询图元管理列表
    const listArray = () => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));

      queryDataSourceList({ ...formInline }).then(res => {
        let list = res.data ? res.data : [];
        that.list = JSON.parse(JSON.stringify(list));
        that.listLoading = false;
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886) return
        that.list = [];
      });
    }

    const clickAddButFun = () => {
      that.dataSourceId = "";
      that.titleName = "添加数据源";
      that.addDataSourceVisible = true;
    }

    const clickOperateBut = (operateType,row)=>{

      if(operateType === 1){
        that.dataSourceId = row.id;
        that.titleName = "编辑数据源";
        that.addDataSourceVisible = true;
      }

      if(operateType === 2){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${row.name}</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteDataSourceByIds({ ids: [row.id] }).then(()=>{
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
        }).then(() => {
          listArray();
          ElMessage({type: "success", showClose: true, message: "删除成功！"});
        }).catch(() => {
          console.log("取消删除！");
        });
      }

    }

    // 计算出表格最大高度
    const tableCenterRef = ref(null);
    const setTableMaxHeight = () => {
      that.tableMaxHeight = tableCenterRef.value.offsetHeight;
    }

    onMounted(()=>{
      listArray();
    })

    return {...toRefs(that), setTableMaxHeight, tableCenterRef, clickAddButFun, listArray, clickOperateBut}
  }
}
</script>

<style lang="scss" scoped>
</style>