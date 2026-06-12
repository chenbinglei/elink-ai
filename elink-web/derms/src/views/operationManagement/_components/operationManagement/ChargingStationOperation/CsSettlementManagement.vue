<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="站点名称：">
              <el-select v-model="formInline.siteId" filterable clearable placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="所属区域：">
              <el-col :span="8" style="padding: 0">
                <el-select v-model="formInline.areaType" placeholder="请选择" @change="formInline.areaValue = ''">
                  <el-option v-for="(item,index) in areaTypeArray" :key="index" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-col>
              <el-col :span="16" style="padding: 0">
                <el-select v-model="formInline.areaValue" filterable clearable placeholder="请选择">
                  <template v-for="(item,index) in (formInline.areaType === 1 ? provinceArray : cityArray)" :key="index">
                    <el-option :label="item.name" :value="item.name"></el-option>
                  </template>
                </el-select>
              </el-col>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="运营商：">
              <el-select v-model="formInline.tenantId" filterable clearable placeholder="请选择运营商">
                <el-option v-for="item in tenantIdArray" :key="item.id" :label="item.tenantName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button class="blackFontButtons" :icon="RefreshRight" @click="clickResetForm">重置</el-button>
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
            <template #default="{ row }">{{ $filters.moreData(row.siteName)}}</template>
          </el-table-column>
          <el-table-column label="运营商" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.tenantName)}}</template>
          </el-table-column>
          <el-table-column label="所在城市">
            <template #default="{ row }">
              <span>{{ $filters.moreData(row.province) }}</span>
              <span>/</span>
              <span>{{ $filters.moreData(row.city) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="收款账户" show-overflow-tooltip>
            <template #default="{ row }">
              <template v-if="row.recMchId">
                <span class="platformName">微信：</span>
                <span class="recMchName">{{ $filters.moreData(row.recMchName) }}</span>
                <span class="recMchId">（{{ $filters.moreData(row.recMchId) }}）</span>
              </template>
              <template v-else><span class="no_class_text">未配置</span></template>
            </template>
          </el-table-column>
          <el-table-column label="付款账户" show-overflow-tooltip>
            <template #default="{ row }">
              <template v-if="row.payMchId">
                <span class="platformName">微信：</span>
                <span class="payMchName">{{ $filters.moreData(row.payMchName) }}</span>
                <span class="payMchId">（{{ $filters.moreData(row.payMchId) }}）</span>
              </template>
              <template v-else><span class="no_class_text">未配置</span></template>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="210" fixed="right">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" @click="clickOperateBut(1, row)">设置</el-link>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
      </div>
    </div>

    <SettlementSettingDialog v-if="settlementSettingVisible" v-model:isVisible="settlementSettingVisible" :activeSiteId="activeSiteId" @changeEvent="listArray('refresh')" />
  </div>
</template>

<script>
import { useAppStore } from '@/stores/index';

import pinyin from "tiny-pinyin";
import {ElMessage} from "element-plus";
import {area_type_array} from "@/utils/setVariate";
import {RefreshRight,Search} from '@element-plus/icons-vue';
import {computed, onMounted, reactive, ref, toRefs, defineComponent, nextTick} from "vue";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import SettlementSettingDialog from "./CsSettlementManagement/SettlementSettingDialog.vue";
import {querySiteAccountList,findTenantListByUserId} from "@/api/operationManagement/CsSettlementManagement";

export default defineComponent({
  name: "CsSettlementManagement",
  components: {SettlementSettingDialog},
  setup(){

    const appStore = useAppStore();
    const userInfo = computed(() => {
      return appStore.userInfo;
    });
    const tableRef = ref(null);

    const that = reactive({
      Search,
      RefreshRight,
      oldFormInline: {},
      formInline: {areaType: 1},

      cityArray: [],
      siteIdArray: [],
      provinceArray: [],
      tenantIdArray: [],
      areaTypeArray: area_type_array,

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      activeSiteId: "",
      settlementSettingVisible: false,
    });

    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      querySiteAccountList({page: that.currentPage, size: that.pageNum, ...formInline}).then(async res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
        await nextTick();
         if(tableRef.value){
          tableRef.value.setScrollTop(0);
        }
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.totalNumber = 0;
        that.list = [];
      });
    };

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    };

    const clickOperateBut = (operateType, row) => {
      if(operateType === 1){
        that.activeSiteId = row.siteId;
        that.settlementSettingVisible = true;
      }
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({scenarioTypes: "3", timer: new Date() }).then(res => {
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
        that.siteIdArray = JSON.parse(JSON.stringify(list));
        that.cityArray = JSON.parse(JSON.stringify(cityArray));
        that.provinceArray = JSON.parse(JSON.stringify(provinceArray));
        // that.regionArray = setTreeData([...provinceArray, ...cityArray]);
      });
    };

    const queryTenantListByUserId = ()=>{
      findTenantListByUserId({ timer: new Date() }).then(res=>{
        that.tenantIdArray = res.data;
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
      queryTenantListByUserId();
      listArray();
    });

    return {...toRefs(that), clickResetForm, setTableMaxHeight, tableContentRef, tablePaginationRef, clickOperateBut, querySiteBasicInfoByTenantId,
      listArray, tableRef, queryTenantListByUserId};
  }
});
</script>

<style scoped lang="scss">
.tableContent{
  padding: 12px;
  box-sizing: border-box;

  .no_class_text{
    color: rgba(255,255,255,.6);
  }
}
</style>