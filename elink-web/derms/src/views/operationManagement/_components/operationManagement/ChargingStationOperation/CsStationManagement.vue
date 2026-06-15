<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="站点名称：">
              <el-input v-model="formInline.keyword" clearable placeholder="请输入关键字"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="所属区域：">
              <el-col :span="8" style="padding: 0">
                <el-select v-model="formInline.areaType" placeholder="请选择" @change="formInline.area = ''">
                  <el-option v-for="(item,index) in areaTypeArray" :key="index" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-col>
              <el-col :span="16" style="padding: 0">
                <el-select v-model="formInline.area" filterable clearable placeholder="全部">
                  <template v-for="(item,index) in (formInline.areaType === 1 ? provinceArray : cityArray)" :key="index">
                    <el-option :label="item.name" :value="item.name"></el-option>
                  </template>
                </el-select>
              </el-col>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="资产状态：">
              <el-select v-model="formInline.siteStatus" clearable placeholder="全部">
                <el-option v-for="item in siteStatusArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="全天开放：">
              <el-select v-model="formInline.openAllDay" clearable placeholder="全部">
                <el-option v-for="item in openAllDayArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="建筑场所：">
              <el-select v-model="formInline.construction" filterable clearable placeholder="全部">
                <el-option v-for="item in constructionArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="tableContent content_border" ref="tableContentRef">
      <TableHeaderTitle title="站点列表"></TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight" ref="tableRef">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="站点名称" fixed="left" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
          </el-table-column>
          <el-table-column label="资产状态">
            <template #default="{ row }">
              <div class="siteStatus" :class="'siteStatus' + row.siteStatus">
                <span>{{ $filters.siteStatus(row.siteStatus) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所在城市">
            <template #default="{ row }">
              <span>{{ $filters.moreData(row.province) }}</span>
              <span>/</span>
              <span>{{ $filters.moreData(row.city) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="详细地址" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.address) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="210" fixed="right">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" @click="clickOperateBut(1, row)">详情</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(2, row)">订单</el-link>
                <template v-if="row.siteStatus !== 1">
                  <span class="split_line">|</span>
                  <el-link :underline="false" @click="clickOperateBut(3, row)">投运</el-link>
                </template>
                <template v-if="row.siteStatus === 1 || row.siteStatus === 3">
                  <span class="split_line">|</span>
                  <el-link :underline="false" @click="clickOperateBut(5, row)">停运</el-link>
                </template>
                <template v-if="row.siteStatus === 1">
                  <span class="split_line">|</span>
                  <el-link :underline="false" @click="clickOperateBut(4, row)">检修</el-link>
                </template>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { useOperationManagementStore } from '@/stores/index';

import pinyin from "tiny-pinyin";
import {queryUserAuthorityIsHaveFun} from "@/utils";
import {ElMessage, ElMessageBox} from "element-plus";
import {RefreshRight,Search} from '@element-plus/icons-vue';
import {onMounted, reactive, ref, toRefs, defineComponent, getCurrentInstance, nextTick} from "vue";
import {area_type_array, build_site_array, site_status_all_array} from "@/utils/setVariate";
import {findSiteInfoByUserId, findSiteInfoListByPage, updateSiteStatusById} from "@/api/operationManagement/CsStationManagement";

export default defineComponent({
  name: "CsStationManagement",
  emits: ["changEvent"],
  setup() {

    const operationManagementStore = useOperationManagementStore();
    const {emit} = getCurrentInstance();

    const that = reactive({
      Search,
      RefreshRight,
      cityArray: [],
      provinceArray: [],
      oldFormInline: {},
      areaTypeArray: area_type_array,
      constructionArray: build_site_array,
      siteStatusArray: site_status_all_array,
      formInline: { areaType: 1,keywordType: 1,scenarioTypes: 3 },
      tabsArray: [{id: 1, name: "充电桩"}, {id: 2, name: "充电枪"}],
      openAllDayArray: [{id: 0, name: "否"}, {id: 1, name: "是"}],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
    });
    const tableRef = ref(null);
    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findSiteInfoListByPage({page: that.currentPage, size: that.pageNum, ...formInline}).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        nextTick(() => {
          if (tableRef.value) {
            tableRef.value.setScrollTop(0);
          }
        });
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.totalNumber = 0;
        that.list = [];
      });
    };

    const clickOperateBut = (operateType, row) => {
      if (operateType === 1) {
        const routeName = "/operationManagement/CsStationDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        console.log("isAuthority",isAuthority);
        if(!isAuthority){
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return;
        }

        operationManagementStore.updateSecondaryInfo({ subTitle: row.siteName, id: row.id, componentName: "CsStationDetails", backComponentName: "CsStationManagement"});
        operationManagementStore.updateSecondaryVisible(true);
      }

      if (operateType === 2) {
        const routeName = "/operationManagement/CsChargingRecord";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if(!isAuthority){
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return;
        }
        emit("changEvent",{ operateType: "switchComponents", componentName: "CsChargingRecord",siteId: row.id });
      }

      if (operateType === 3 || operateType === 4 || operateType === 5) {
        let siteStatus = 1;
        let highlightText = "投运";

        if(operateType === 4){
          siteStatus = 3;
          highlightText = "检修";
        }

        if(operateType === 5){
          siteStatus = 2;
          highlightText = "停运";
        }

        ElMessageBox.confirm(`确定更改站点状态为（<span class="deleteName">${ highlightText }</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在更改...';
              updateSiteStatusById({ siteId: row.id,siteStatus: siteStatus }).then(()=>{
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
          listArray("refresh");
          ElMessage({ type: "success", showClose: true, message: "更改成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    };

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({scenarioTypes: "3",timer: new Date()}).then(res => {
        let list = res.data ? res.data : [];
        let provinceArray = [], cityArray = [];
        for (let i = 0; i < list.length; i++) {

          // 读写类型字段
          if(list[i].siteReadwriteObject) list[i].siteReadwriteObject = JSON.parse(list[i].siteReadwriteObject);
          let location_info = list[i]?.siteReadwriteObject?.location ?? {};

          //省份处理
          if(location_info.province){
            let province_id = pinyin.convertToPinyin(location_info.province);

            let findProvince = provinceArray.find(item => item.id === province_id);
            if (!findProvince) provinceArray.push({name: location_info.province, id: province_id });

            // 市区处理
            let findCity = cityArray.find(item => item.name === location_info.city);
            if (!findCity) cityArray.push({name: location_info.city, parentId: province_id });
          }
        }
        // console.log(provinceArray);
        // console.log(cityArray);
        // that.siteIdArray = JSON.parse(JSON.stringify(list));
        that.cityArray = JSON.parse(JSON.stringify(cityArray));
        that.provinceArray = JSON.parse(JSON.stringify(provinceArray));
      });
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 78;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
      listArray();
    });

    return {...toRefs(that),tableRef, clickResetForm, setTableMaxHeight, tableContentRef, tablePaginationRef, clickOperateBut, querySiteBasicInfoByTenantId, listArray};
  }
});

</script>

<style lang="scss" scoped>
.tableContent{
  padding: 12px;
  box-sizing: border-box;

  .siteStatus{
    width: fit-content;
    padding: 0 16px;
    height: 32px;
    color: #C7C7C7;
    font-size: 14px;
    border-radius: 8px;
    background: #4abeff4d;
    line-height: 32px;
  }

  .siteStatus1{
    color: #41CB4A;
    background: rgba(65, 203, 74, .2);
  }
  .siteStatus2{
    color: #FF2B2B;
    background: rgba(255, 43, 43, .2);
  }
  .siteStatus3{
    color: #FF9C02;
    background: rgba(255, 156, 2, .2);
  }
}
</style>