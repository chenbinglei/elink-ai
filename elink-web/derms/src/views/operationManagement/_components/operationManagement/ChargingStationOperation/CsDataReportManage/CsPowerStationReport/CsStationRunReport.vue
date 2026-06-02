<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="选择站点：">
              <el-select v-model="formInline.siteIds" clearable filterable multiple collapse-tags max-collapse-tags="1" placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
              </el-select>
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
                <el-select v-model="formInline.area" filterable clearable placeholder="请选择">
                  <template v-for="(item,index) in (formInline.areaType === 1 ? provinceArray : cityArray)" :key="index">
                    <el-option :label="item.name" :value="item.name"></el-option>
                  </template>
                </el-select>
              </el-col>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="16" :lg="12" :xl="12">
            <el-form-item label="开始时间：">
              <el-date-picker v-model="formInline.startAlsoDate" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts" :clearable="false"
                              value-format="YYYY-MM-DD" format="YYYY-MM-DD" end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="商户：">
              <el-select v-model="formInline.accountId" clearable placeholder="全部">
                <el-option v-for="item in accountArray" :key="item.id" :label="item.mchName" :value="item.id"></el-option>
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

    <div class="tableContent content_border" ref="tableContentRef" style="">
      <RunTableHeaderCom :formInline="formInline" :siteAllIds="siteAllIds" />
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table :data="list" stripe border v-loading="listLoading" :max-height="tableMaxHeight" ref="tableRef">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="站点名称" fixed="left" show-overflow-tooltip min-width="200">
            <template #default="{ row }">{{ $filters.moreData(row.siteName)}}</template>
          </el-table-column>
          <el-table-column label="所在城市" min-width="160">
            <template #default="{ row }">
              <span>{{ $filters.moreData(row.province) }}</span>
              <span>/</span>
              <span>{{ $filters.moreData(row.city) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="商户" show-overflow-tooltip min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.accountName) }}</template>
          </el-table-column>
          <el-table-column min-width="160">
            <template #header>
              <div class="flex-ai-center">
                <span class="header_text">时间利用率（%）</span>
                <div class="sort_class" @click="clickTableSortFun('timeUtilizeSort')">
                  <span v-if="formInline.timeUtilizeSort" class="iconfont icon-paixu-daoxu"></span>
                  <span v-else class="iconfont icon-paixu-zhengxu"></span>
                </div>
              </div>
            </template>
            <template #default="{ row }">{{ $filters.moreData(row.timeUtilize) }}</template>
          </el-table-column>
          <el-table-column min-width="160">
            <template #header>
              <div class="flex-ai-center">
                <span class="header_text">功率利用率（%）</span>
                <div class="sort_class" @click="clickTableSortFun('powerUtilizeSort')">
                  <span v-if="formInline.powerUtilizeSort" class="iconfont icon-paixu-daoxu"></span>
                  <span v-else class="iconfont icon-paixu-zhengxu"></span>
                </div>
              </div>
            </template>
            <template #default="{ row }">{{ $filters.moreData(row.powerUtilize) }}</template>
          </el-table-column>
          <el-table-column min-width="160">
            <template #header>
              <div class="flex-ai-center">
                <span class="header_text">枪均充电量（度）</span>
                <div class="sort_class" @click="clickTableSortFun('gunAvgQtSort')">
                  <span v-if="formInline.gunAvgQtSort" class="iconfont icon-paixu-daoxu"></span>
                  <span v-else class="iconfont icon-paixu-zhengxu"></span>
                </div>
              </div>
            </template>
            <template #default="{ row }">{{ $filters.moreData(row.gunAvgQt) }}</template>
          </el-table-column>
          <el-table-column label="桩平均在线时长" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.pileAvgDuration) }}</template>
          </el-table-column>
          <el-table-column label="设备在线率" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.onlineRate) }}</template>
          </el-table-column>
          <el-table-column label="电桩告警次数" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.alarmNum) }}</template>
          </el-table-column>
          <el-table-column label="启动失败次数" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.startFailNum ) }}</template>
          </el-table-column>
          <el-table-column label="异常订单数量" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.abnormalOrderNum) }}</template>
          </el-table-column>
          <el-table-column label="订单异常率" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.abnormalOrderRate) }}</template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
      </div>
    </div>
  </div>
</template>

<script>
import pinyin from "tiny-pinyin";
import {ElMessage} from "element-plus";
import {area_type_array} from "@/utils/setVariate";
import {RefreshRight,Search} from '@element-plus/icons-vue';
import {reactive, toRefs, defineComponent, onMounted, ref, nextTick} from "vue";
import RunTableHeaderCom from "./CsStationRunReport/RunTableHeaderCom.vue";
import {getDaysFromCurrentTime, pickerOptionsGthanAcTime} from "@/utils/dateTime";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import {findAccountList, findSiteRunReport} from "@/api/operationManagement/CsDataReportManage";

export default defineComponent({
  name: "CsStationRunReport",
  components:{RunTableHeaderCom},
  setup() {
    const tableRef = ref(null);
    const that = reactive({
      Search,
      RefreshRight,
      siteAllIds: [],
      oldFormInline: {},
      formInline: {
        areaType: 1,
        gunAvgQtSort: 0, // 枪均电量排序 0-升序 1-降序
        timeUtilizeSort: 0, //时间利用率排序 0-升序 1-降序
        powerUtilizeSort: 0, //功率利用率排序 0-升序 1-降序
        startAlsoDate: [getDaysFromCurrentTime(-31),getDaysFromCurrentTime(-1)]
      },

      cityArray: [],
      siteIdArray: [],
      accountArray: [],
      provinceArray: [],
      areaTypeArray: area_type_array,
      pickerOptions: pickerOptionsGthanAcTime(1),

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
    });

    const tableContentRef = ref(null);
    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if(formInline.startAlsoDate){
        formInline.startTime = formInline.startAlsoDate[0] + ' 00:00:00';
        formInline.endTime = formInline.startAlsoDate[1] + ' 23:59:59';
        delete formInline.startAlsoDate;
      }

      if(!formInline.siteIds || !formInline.siteIds.length){
        if(!that.siteAllIds || !that.siteAllIds.length){
          that.list = [];
          that.totalNumber = 0;
          that.listLoading = false;
          ElMessage({type: "error", showClose: true, message: "请先选择查询的站点！"});
          return;
        }
        formInline.siteIds = JSON.parse(JSON.stringify(that.siteAllIds));
      }

      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findSiteRunReport({page: that.currentPage, size: that.pageNum, ...formInline}).then(async res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
        that.totalNumber = returnDataInfo.totalSize;
        tableContentRef.value.scrollTop = 0;
        that.list = returnDataInfo.items;
        that.listLoading = false;
        await nextTick();
        if (tableRef.value) {
          tableRef.value.setScrollTop(0);
        }
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.totalNumber = 0;
        that.list = [];
      });
    };

    // 点击表格头部筛选
    const clickTableSortFun = (fieldName)=>{
      let activeValue = that.formInline[fieldName];
      that.formInline[fieldName] = activeValue ? 0 : 1;
      listArray('refresh');
    };

    const clickResetForm = (operateType = "resetPage") => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      if(!that.siteAllIds.length) querySiteBasicInfoByTenantId();
      if(that.siteAllIds.length) listArray(operateType);
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({scenarioTypes: "3",timer: new Date()}).then(res => {
        let siteAllIds = [];
        let list = res.data ? res.data : [];
        let provinceArray = [], cityArray = [];
        for (let i = 0; i < list.length; i++) {
          siteAllIds.push(list[i].id); // 站点id

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
        that.siteAllIds = JSON.parse(JSON.stringify(siteAllIds));
        that.provinceArray = JSON.parse(JSON.stringify(provinceArray));
        if(that.siteAllIds.length) listArray(); // 有站点id 就获取报表数据
      });
    };

    // 查询商户列表
    const queryAccountList = ()=>{
      findAccountList({queryMode: 1,timer: new Date()}).then(res=>{
        that.accountArray = res.data ? res.data : [];
      });
    };

    // 计算出表格最大高度
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 78;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(()=>{
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
      queryAccountList();
    });

    return {...toRefs(that), querySiteBasicInfoByTenantId, clickResetForm, listArray, tableContentRef, queryAccountList, clickTableSortFun,
      tablePaginationRef, setTableMaxHeight, tableRef};
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  box-sizing: border-box;
  padding: 12px;
  box-sizing: border-box;
  z-index: 1;
  .sort_class{
    cursor: pointer;
    display: flex;
    align-items: center;

    .iconfont{
      color: #007FEB;
    }
  }
}
</style>