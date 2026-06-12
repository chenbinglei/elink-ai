<template>
  <div class="app-container-right">

    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="站点名称：">
              <el-input v-model="formInline.siteName" clearable placeholder="请输入关键字"></el-input>
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
            <el-form-item label="光伏类型：">
              <el-select v-model="formInline.pvType" clearable placeholder="全部">
                <el-option v-for="item in pvTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="消纳方式：">
              <el-select v-model="formInline.consumMode" filterable clearable placeholder="全部">
                <el-option v-for="item in consumModeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="并网等级：">
              <el-select v-model="formInline.tiedGrade" filterable clearable placeholder="全部">
                <el-option v-for="item in tiedGradeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
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
      <TableHeaderTitle title="站点列表">
        <template #content>
          <div class="table_top_content">
            <el-button :icon="Setting" @click="clickOperateBut(2)">列表显示项</el-button>
          </div>
        </template>
      </TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="站点名称" fixed="left" min-width="210" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
          </el-table-column>
          <template v-for="item in tableAllFieldList" :key="item.key">
            <template v-if="tableShowFieldList.indexOf(item.key) !== -1">
              <el-table-column :label="item.name" :min-width="item.minWidth" show-overflow-tooltip>
                <template #default="{ row }">
                  <div class="table_content_class" :class="[item.className,item.className + row[item.key]]">
                    <template v-if="item.componentName">
                      <component :is="item.componentName" :configInfo="item" :returnDataInfo="row"></component>
                    </template>
                    <template v-else>
                      <span class="value">{{ $filters[item.filterName ? item.filterName : 'moreData'](row[item.key]) }}</span>
                      <span v-if="item.unit">{{ item.unit }}</span>
                    </template>
                  </div>
                </template>
              </el-table-column>
            </template>
          </template>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" @click="clickOperateBut(1, row)">详情</el-link>
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

    <TableFieldControlDialog v-if="tableFieldControlVisible" v-model:isVisible="tableFieldControlVisible" :tableAllFieldList="tableAllFieldList"
                             :tableFieldStorageName="tableFieldStorageName" @changeEvent="queryTableFieldListFun" />
  </div>
</template>

<script>
import pinyin from "tiny-pinyin";
import {ElMessage, ElMessageBox} from "element-plus";
import {RefreshRight, Search, Setting} from "@element-plus/icons-vue";
import {onMounted, reactive, ref, toRefs, defineComponent} from "vue";
import SiteCityNameCom from "./PvStationManagement/SiteCityNameCom.vue";
import DayQtAndHoursCom from "./PvStationManagement/DayQtAndHoursCom.vue";
import PowerCurveChartCom from "./PvStationManagement/PowerCurveChartCom.vue";
import {findPvSiteListByPage} from "@/api/operationManagement/PvOperationsAnalysis";
import {findSiteInfoByUserId, updateSiteStatusById} from "@/api/operationManagement/CsStationManagement";
import {area_type_array, consum_mode_array, pv_type_array, site_status_all_array, tied_grade_array} from "@/utils/setVariate";
import TableFieldControlDialog from "@/views/operationManagement/_components/operationManagement/PublicComponents/TableFieldControlDialog.vue";

export default defineComponent({
  name: "PvStationManagement",
  components: {TableFieldControlDialog, PowerCurveChartCom, SiteCityNameCom, DayQtAndHoursCom },
  setup() {
    const that = reactive({
      Search,
      Setting,
      RefreshRight,
      cityArray: [],
      provinceArray: [],
      oldFormInline: {},
      formInline: {areaType: 1},
      pvTypeArray: pv_type_array,
      areaTypeArray: area_type_array,
      tiedGradeArray: tied_grade_array,
      consumModeArray: consum_mode_array,
      siteStatusArray: site_status_all_array,

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      tableShowFieldList: [], // 表格展示字段
      tableFieldControlVisible: false,
      tableFieldStorageName: 'PvStationManagement_TableFieldList',
      tableAllFieldList: [
        { name: "资产状态", key: "siteStatus", minWidth: 120,filterName: "siteStatus", className: 'siteStatus'},
        { name: "所在城市", key: 'siteCityName', componentName: "SiteCityNameCom", minWidth: 150},
        { name: "光伏类型", key: 'pvType', filterName: "pvType", minWidth: 120},
        { name: "消纳方式", key: 'consumMode', filterName: "consumMode", minWidth: 120},
        { name: "实时功率曲线", key: 'realPowerList', componentName: "PowerCurveChartCom", minWidth: 280},
        { name: "实时功率归一化", key: 'powerAtOne', minWidth: 150, unit: '%'},
        { name: "今日发电", key: 'dayQt', componentName: "DayQtAndHoursCom", minWidth: 150},
        { name: "装机容量（kWp）", key: 'pvCapacity', minWidth: 180},
        { name: "投产时间", key: 'officialRunTime', minWidth: 120},
        { name: "并网等级", key: 'tiedGrade', filterName: "tiedGrade", minWidth: 120},
      ]
    });

    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findPvSiteListByPage({page: that.currentPage, size: that.pageNum, ...formInline}).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
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
        ElMessage({type: "warning", showClose: true, message: "暂未开放！"});
        // const routeName = "/operationManagement/CsStationDetails";
        // const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        // if(!isAuthority){
        //   ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
        //   return
        // }
        //
        // operationManagementStore.updateSecondaryInfo({ subTitle: row.siteName, id: row.id, componentName: "CsStationDetails", backComponentName: "CsStationManagement"});
        // operationManagementStore.updateSecondaryVisible(true);
      }

      if (operateType === 2) that.tableFieldControlVisible = true;

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
      findSiteInfoByUserId({scenarioTypes: 1,timer: new Date()}).then(res => {
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

    // 查询表格展示字段列表
    const queryTableFieldListFun = ()=>{
      let tableShowFieldList = [];
      let storageFieldList = localStorage.getItem(that.tableFieldStorageName);
      // console.log(storageFieldList);
      if(!storageFieldList){
        for(let i = 0;i < that.tableAllFieldList.length;i++){
          tableShowFieldList.push(that.tableAllFieldList[i].key);
        }
      }
      that.tableShowFieldList = storageFieldList ? JSON.parse(storageFieldList) : tableShowFieldList;
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
      queryTableFieldListFun();
      listArray();
    });

    return {...toRefs(that), clickResetForm, setTableMaxHeight, tableContentRef, tablePaginationRef, clickOperateBut, querySiteBasicInfoByTenantId,
      listArray, queryTableFieldListFun};
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
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