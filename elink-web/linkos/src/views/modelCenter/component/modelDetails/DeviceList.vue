<template>
  <div class="content_table" v-resize="setTableMaxHeight">

    <div class="header-form" ref="headerFormRef">
      <el-form :model="formInline" inline>
        <el-form-item label="关键字：">
          <el-input v-model="formInline.keyword" class="input-with-select" clearable placeholder="请输入关键词搜索">
            <template #append>
              <el-button :icon="Search" @click="listArray('resetPage')"></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button :icon="View" class="whiteFontButtons" @click="clickDisplaySet">显示设置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-loading="listLoading" class="tableContent">
      <div class="tableCenter">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" border stripe>
          <el-table-column align="center" label="序号" type="index" width="80" fixed>
            <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>

          <template v-for="(item,index) in fieldDataList" :key="index">
            <el-table-column align="center" :label="item.functionName" min-width="210">
              <template #default="{ row }">{{ $filters.moreData(customDataFilterFun(item,row)) }}</template>
            </el-table-column>
          </template>

        </el-table>
      </div>
      <div class="tablePagination">
        <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
      </div>
    </div>

    <display-setting-table v-if="displaySettingVisible" v-model:isVisible="displaySettingVisible" :activeModelId="activeModelId"
                           :fieldDataList="fieldDataList" @changeEvent="listArray('resetPage')"></display-setting-table>

  </div>
</template>

<script>
import {DisplaySettingTable} from "./component";
import {onMounted, reactive, ref, toRefs} from "vue";
import {View, Delete, Refresh, Search} from "@element-plus/icons-vue";
import {findModelDeviceListByModelId} from "@/api/modelCenter/modelManagement";

export default {
  name: "DeviceList",
  components:{DisplaySettingTable},
  props: {
    activeModelId: {
      type: [String, Number],
      default: ''
    },
    componentMaxHeight: {
      type: [String, Number],
      default: 320
    }
  },
  setup(props) {

    const that = reactive({
      list: [],
      pageNum: 20,
      formInline: {},
      currentPage: 1,
      totalNumber: 0,
      listLoading: false, // 表格加载
      tableMaxHeight: 300,
      View, Delete, Refresh, Search,

      fieldDataList: [], // 表格渲染的静态字段
      displaySettingVisible: false,
    })

    const listArray = (reset)=>{
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      findModelDeviceListByModelId({ modelId: props.activeModelId, page: that.currentPage, size: that.pageNum,...that.formInline }).then(res=>{
        that.totalNumber = res.data.deviceDataPage.totalSize;
        that.fieldDataList = res.data.fieldDataList;
        that.list = res.data.deviceDataPage.items;
        that.listLoading = false;
      }).catch(err => {
        that.listLoading = false;
        if (err && err.code === 88886)return
        that.fieldDataList = [];
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const clickDisplaySet = ()=>{
      that.displaySettingVisible = true;
    }

    const customDataFilterFun = (item,row)=>{
      if(item.dataObject){
        let dataObject = eval('('+item.dataObject+')');
        if(dataObject && dataObject.length){
          let findItem = dataObject.find(el => el.id === row[item.functionLogo]);
          if(findItem) return findItem.name
        }
      }
      return row[item.functionLogo]
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.componentMaxHeight - headerFormHeight - 110;
    }

    onMounted(()=>{
      listArray();
    })

    return {...toRefs(that), listArray, clickDisplaySet, headerFormRef, setTableMaxHeight, customDataFilterFun }
  }
}
</script>

<style scoped lang="scss">
.content_table{
  height: 100%;
  display: flex;
  flex-direction: column;

  .header-form,.tableContent{
    padding: 0;
    border-top: none;
  }
}
</style>
