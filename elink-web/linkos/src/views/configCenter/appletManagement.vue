<template>
  <div class="app-container">
    <div class="app-container-right" v-resize="setTableMaxHeight">
      <div ref="headerFormRef" class="header-form">
        <el-form :model="formInline" inline>
          <el-form-item label="关键字：">
            <el-input v-model="formInline.keyword" class="input-with-select" clearable placeholder="请输入小程序名称或ID">
              <template #append>
                <el-button :icon="Search" @click="listArray"></el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button class="whiteFontButtons" :icon="CirclePlus" @click="clickAddButtonFun">新增小程序</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="tableContent">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column align="center" label="序号" type="index" width="80"></el-table-column>
            <el-table-column align="center" label="小程序名称">
              <template #default="{ row }">{{ $filters.moreData(row.appletName)}}</template>
            </el-table-column>
            <el-table-column align="center" label="小程序ID">
              <template #default="{ row }">{{ $filters.moreData(row.appletCode)}}</template>
            </el-table-column>
            <el-table-column align="center" label="小程序类型">
              <template #default="{ row }">{{ $filters.appletType(row.appletType)}}</template>
            </el-table-column>
            <el-table-column align="center" label="联系人电话">
              <template #default="{ row }">{{ $filters.moreData(row.phone)}}</template>
            </el-table-column>
            <el-table-column align="center" label="创建信息" show-overflow-tooltip>
              <template #default="{ row }">
                <span>{{ $filters.moreData(row.createName)}}</span>
                <span>，</span>
                <span>{{ $filters.moreData(row.createTime)}}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="最后修改信息" show-overflow-tooltip>
              <template #default="{ row }">
                <span>{{ $filters.moreData(row.updateName)}}</span>
                <span>，</span>
                <span>{{ $filters.moreData(row.updateTime)}}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="240">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link type="danger" :underline="false" @click="clickOperateBut(2, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
      </div>
    </div>

    <AddAppletDialog v-if="addAppletVisible" v-model:isVisible="addAppletVisible" :titleName="titleName" :activeAppletId="activeAppletId" @changeEvent="listArray" />
  </div>
</template>
<script lang="ts">
import {ElMessage, ElMessageBox} from "element-plus";
import { CirclePlus,Search } from '@element-plus/icons-vue';
import {AddAppletDialog} from "@/views/configCenter/component";
import {queryAppletList} from "@/api/configCenter/appletManagement";
import {reactive, toRefs, onMounted, ref, defineComponent} from "vue";

export default defineComponent({
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  name: "appletManagement",
  components:{AddAppletDialog},
  setup(props) {

    const that = reactive({
      Search,
      CirclePlus,
      formInline: {},

      list: [],
      listLoading: false,
      tableMaxHeight: 300,

      activeAppletId: "",
      titleName: "新增小程序",
      addAppletVisible: false,
    })

    // 查询小程序列表
    const listArray = ()=>{
      that.listLoading = true;
      queryAppletList({ ...that.formInline }).then(res=>{
        that.list = res.data;
        that.listLoading = false;
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886)return
        that.list = [];
      });
    }

    const clickAddButtonFun = ()=>{
      that.activeAppletId = "";
      that.titleName = "新增小程序";
      that.addAppletVisible = true;
    }

    const clickOperateBut = (operateType,row)=>{

      if(operateType === 1){
        that.titleName = "编辑小程序";
        that.activeAppletId = row.id;
        that.addAppletVisible = true;
      }

      if(operateType === 2){
        ElMessageBox.confirm(`确定删除小程序（<span class="deleteName">${ row.appletName }</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              // updateSiteStatusById({ siteId: row.id,siteStatus: siteStatus }).then(()=>{
                done();
                instance.confirmButtonLoading = false;
              // }).catch(() => {
              //   instance.confirmButtonText = '确定';
                // instance.confirmButtonLoading = false;
              // });
            } else {
              done();
            }
          }
        }).then(() => {
          listArray("refresh");
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight;
    }

    onMounted(()=>{
      listArray();
    })

    return { ...toRefs(that), headerFormRef, setTableMaxHeight, clickOperateBut, clickAddButtonFun, listArray }
  }
})
</script>
<style lang="scss" scoped>
.content_top{
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}
</style>