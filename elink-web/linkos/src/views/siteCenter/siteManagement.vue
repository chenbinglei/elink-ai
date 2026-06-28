<template>
  <div class="app-container">
    <div v-resize="setTableMaxHeight" class="app-container-right">
      <div ref="headerFormRef" class="header-form">
        <el-form :model="formInline" inline>
          <el-form-item>
            <el-input v-model="formInline.keyword" class="selectAndInput" clearable placeholder="请输入关键词">
              <template #prefix>
                <el-select v-model="formInline.keywordType" placeholder="请选择">
                  <el-option v-for="item in keywordTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button :icon="Search" class="whiteFontButtons" @click="listArray('resetPage')">查询</el-button>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button :disabled="isAddButtonClick" :icon="CirclePlus" class="whiteFontButtons" @click="clickAddButFun">添加站点</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="tableContent">
        <div class="tableCenter" ref="tableCenterRef">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column label="序号" type="index" width="80" fixed="left">
              <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column label="站点名称" show-overflow-tooltip fixed="left">
              <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
            </el-table-column>
            <el-table-column label="站点ID" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.id) }}</template>
            </el-table-column>
            <el-table-column label="能源场景">
              <template #default="{ row }">
                <template v-if="row.scenarioTypes">
                  <el-tooltip v-if="row.scenarioTypes.indexOf('1') !== -1" content="光伏" effect="dark" placement="top-start">
                    <span class="iconfont icon-guangfu"></span>
                  </el-tooltip>
                  <el-tooltip v-if="row.scenarioTypes.indexOf('2') !== -1" content="储能" effect="dark" placement="top-start">
                    <span class="iconfont icon-chuneng"></span>
                  </el-tooltip>
                  <el-tooltip v-if="row.scenarioTypes.indexOf('3') !== -1" content="电桩" effect="dark" placement="top-start">
                    <span class="iconfont icon-dianzhuang"></span>
                  </el-tooltip>
                  <el-tooltip v-if="row.scenarioTypes.indexOf('4') !== -1" content="用能" effect="dark" placement="top-start">
                    <span class="iconfont icon-dianwang"></span>
                  </el-tooltip>
                  <el-tooltip v-if="row.scenarioTypes.indexOf('5') !== -1" content="变配电" effect="dark" placement="top-start">
                    <span class="iconfont icon-bianyaqi"></span>
                  </el-tooltip>
                  <el-tooltip v-if="row.scenarioTypes.indexOf('6') !== -1" content="换电" effect="dark" placement="top-start">
                    <span class="iconfont icon-huandianzhan"></span>
                  </el-tooltip>
                </template>
                <template v-else>{{ $filters.moreData(row.scenarioTypes) }}</template>
              </template>
            </el-table-column>
            <el-table-column label="所属租户">
              <template #default="{ row }">{{ $filters.moreData(row.tenantName) }}</template>
            </el-table-column>
            <el-table-column label="状态">
              <template #default="{ row }">{{ $filters.siteStatus(row.siteStatus) }}</template>
            </el-table-column>
            <el-table-column label="描述" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.siteDescribe) }}</template>
            </el-table-column>
            <el-table-column label="创建时间" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="最后更新" show-overflow-tooltip>
              <template #default="{ row }">
                <span>{{ $filters.moreData(row.updateName) }}</span>
                <span>，</span>
                <span>{{ $filters.moreData(row.updateTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperateButFun(1, row)">查看</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" type="danger" @click="clickOperateButFun(2, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
        </div>
      </div>
    </div>

    <CreateSiteDialog v-if="createSiteVisible" v-model:isVisible="createSiteVisible" :titleName="titleName" @changeEvent="listArray('resetPage')"></CreateSiteDialog>
  </div>
</template>

<script lang="ts">
import { useTagsViewStore } from '@/stores/index';

import {useRoute, useRouter} from "vue-router";
import {ElMessage, ElMessageBox} from "element-plus";
import {CirclePlus, Search} from "@element-plus/icons-vue";
import {CreateSiteDialog} from "@/views/siteCenter/component";
import {operateButtonIsClick,queryUserAuthorityIsHaveFun} from "@/utils";
import {computed, reactive, ref, toRefs, defineComponent, onMounted} from "vue";
import {deleteSiteInfoById, querySiteListByPage} from "@/api/siteCenter/siteManagement";

export default defineComponent({
  name: "siteManagement",
  components: {CreateSiteDialog},
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  setup(props) {

    const tagsViewStore = useTagsViewStore();
    const route = useRoute();
    const vueRouter = useRouter();
    const isAddButtonClick = computed(() => {
      return operateButtonIsClick('/device/siteInfo/saveOrUpdateSiteInfo')
    })

    const that = reactive({
      Search,
      CirclePlus,
      formInline: {keywordType: 1},
      keywordTypeArray: [{id: 1, name: "站点名称"}, {id: 2, name: "站点ID"}, {id: 3, name: "业主单位"}],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      titleName: "添加站点",
      createSiteVisible: false,
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      querySiteListByPage({ page: that.currentPage, size: that.pageNum, ...that.formInline}).then(res => {
        that.totalNumber = res.data.totalSize;
        that.list = res.data.items;
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.list = [];
        that.totalNumber = 0;
      })
    }

    const clickAddButFun = () => {
      that.titleName = "添加站点";
      that.createSiteVisible = true;
    }

    const clickOperateButFun = (index, row) => {
      if(index === 1){
        const routeName = "/siteCenter/stationDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if(!isAuthority){
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return
        }

        vueRouter.push({ path: routeName, query: { id: row.id } });
        tagsViewStore.addBackButViews({ id: row.id,backRouteName: route.path,showButRoute: routeName });
      }

      if(index === 2){
        ElMessageBox.confirm(`您确定要删除（<span class="deleteName">${row.siteName}</span>）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteSiteInfoById({ id: row.id }).then(()=> {
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
          ElMessage({ type:"success",showClose: true,message:"操作成功！" });
        }).catch(() => {
          console.log("取消删除");
        });
      }
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    onMounted(() => {
      listArray();
    })

    return {...toRefs(that), listArray, headerFormRef, setTableMaxHeight, clickAddButFun, clickOperateButFun, isAddButtonClick}
  }
})
</script>

<style lang="scss" scoped>
.iconfont {
  margin-right: 8px;
  &:last-child { margin-right: 0; }
}

.header-form {
  :deep(.el-select) {
    min-width: 160px;
    .el-select__wrapper {
      width: 180px;
      min-height: 32px;
    }
    .el-select__selected-item.el-select__placeholder {
      z-index: 1 !important;
      position: absolute !important;
      opacity: 1 !important;
      &.is-transparent { opacity: 1 !important; }
      span { color: #a8abb2 !important; font-size: 14px !important; }
    }
  }
  .el-input {
    :deep(.el-select) {
      min-width: 120px;
      .el-select__wrapper { width: 140px; }
    }
  }
}
</style>