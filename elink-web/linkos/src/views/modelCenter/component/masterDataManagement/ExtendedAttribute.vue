<template>
  <div class="app-container-right">
    <div v-loading="listLoading" class="tableContent" style="border-top: none">
      <div class="tableCenter">
        <el-table :data="list" :max-height="contentHeight" border stripe>
          <el-table-column align="center" label="序号" type="index" width="80">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="属性名称">
            <template #default="{ row }">{{ $filters.moreData(row.reaName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="英文名称">
            <template #default="{ row }">{{ $filters.moreData(row.fieldName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="类型">
            <template #header>
              <div class="table_header flex-jc-ai-center">
                <span class="table_header_text">类型</span>
                <FilterDropDown :objectData="reaTypeFilter" :formInline="form_inline" @changEvent="changEvent"></FilterDropDown>
              </div>
            </template>
            <template #default="{ row }">{{ $filters.reaType(row.reaType) }}</template>
          </el-table-column>
<!--          <el-table-column align="center" label="默认值">-->
<!--            <template #default="{ row }">{{ $filters.moreData(row.defaultValue) }}</template>-->
<!--          </el-table-column>-->
<!--          <el-table-column align="center" label="读写类型">-->
<!--            <template #header>-->
<!--              <div class="table_header flex-jc-ai-center">-->
<!--                <span class="table_header_text">读写类型</span>-->
<!--                <FilterDropDown :objectData="readWriteTypeFilter" :formInline="form_inline" @changEvent="changEvent"></FilterDropDown>-->
<!--              </div>-->
<!--            </template>-->
<!--            <template #default="{ row }">{{ $filters.readWriteType(row.readWriteType) }}</template>-->
<!--          </el-table-column>-->
          <el-table-column align="center" label="是否必填">
            <template #default="{ row }">{{ $filters.required(row.required) }}</template>
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

    <AddExtendedAttrDialog v-if="addExtendedAttrVisible" v-model:isVisible="addExtendedAttrVisible" :activeExtendedAttrId="activeExtendedAttrId"
                           :activeDeviceTypeId="activeDeviceTypeId" :isClickCopy="isClickCopy" :titleName="titleName" @changeEvent="listArray('resetPage')"></AddExtendedAttrDialog>
  </div>
</template>

<script lang="ts">
import {ElMessage, ElMessageBox} from "element-plus";
import {FilterDropDown} from "@/components/FromFilterComponent";
import {onMounted, reactive, defineComponent, toRefs, watch} from "vue";
import {AddExtendedAttrDialog, AddStandardFunDialog} from "./component/index";
import {deleteSeaById, querySeaList} from "@/api/modelCenter/extendedAttribute";

export default defineComponent({
  name: "extendedAttribute",
  components: {FilterDropDown, AddStandardFunDialog, AddExtendedAttrDialog},
  props: {
    activeDeviceTypeId: {
      type: [Number, String],
      default: ""
    },
    contentHeight: {
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

    const that = reactive({
      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      form_inline: {},
      old_form_inline: {},
      listLoading: false, // 表格加载
      readWriteTypeFilter:{title: "读写类型", fieldName: "readWriteType", list: [{id: 1, name: "只读"},{id: 2, name: "读写"}]},
      reaTypeFilter:{
        title: "类型", fieldName: "reaType",
        list: [{id: 1, name: "数值"},{id: 2, name: "文字"},{id: 3, name: "选项"},{id: 4, name: "位置"},{id: 5, name: "开关"}, {id: 6, name: "时间"},{id: 7, name: "文本"}]
      },

      isClickCopy: false,
      titleName: "添加扩展属性",
      activeExtendedAttrId: "",
      addExtendedAttrVisible: false,
    })

    // 查询模型扩展属性列表
    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      let form_inline = JSON.parse(JSON.stringify(that.form_inline));
      if(form_inline.reaType)form_inline.reaType = form_inline.reaType.join(',');
      if(form_inline.readWriteType)form_inline.readWriteType = form_inline.readWriteType.join(',');
      querySeaList({page: that.currentPage, size: that.pageNum, typeId: props.activeDeviceTypeId, ...props.formInline,...form_inline}).then(res => {
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

    const clickAddButtonFun = () => {
      that.isClickCopy = false;
      that.titleName = "添加扩展属性";
      that.activeExtendedAttrId = "";
      that.addExtendedAttrVisible = true;
    }

    const clickOperateBut = (operate, row) => {

      if (operate === 1) {
        that.isClickCopy = false;
        that.titleName = "编辑扩展属性";
        that.activeExtendedAttrId = row.id;
        that.addExtendedAttrVisible = true;
      }

      if (operate === 2) {
        that.isClickCopy = true;
        that.titleName = "添加扩展属性";
        that.activeExtendedAttrId = row.id;
        that.addExtendedAttrVisible = true;
      }

      if (operate === 3) {
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${row.reaName}</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "deleteMsgBoxClass", showClose: false, type: 'warning',
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteSeaById({id: row.id}).then(() => {
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
          listArray("resetPage");
          ElMessage({type: "success", showClose: true, message: "删除成功！"});
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

    return {...toRefs(that), listArray, clickAddButtonFun, clickOperateBut, watchActiveSortId, changEvent}
  }
})
</script>

<style scoped>

</style>
