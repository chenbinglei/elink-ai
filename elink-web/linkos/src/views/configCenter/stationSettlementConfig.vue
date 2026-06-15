<template>
  <div class="app-container">
    <div v-resize="setTableMaxHeight" class="app-container-right">
      <div ref="headerFormRef" class="header-form">
        <el-form :model="formInline" inline>
          <el-form-item label="站点名称:">
            <el-select v-model="formInline.siteId" filterable placeholder="请选择">
              <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="区域:">
            <el-select v-model="formInline.area" placeholder="请选择">
              <el-option-group v-for="group in regionArray" :key="group.id" :label="group.name">
                <el-option v-for="(item,index) in group.children" :key="index" :label="item.name" :value="item.name"></el-option>
              </el-option-group>
            </el-select>
          </el-form-item>
          <el-form-item label="运营商：">
            <el-select v-model="formInline.operateUnitId" clearable filterable placeholder="请选择运营商">
              <el-option v-for="item in operateUnitArray" :key="item.id" :label="item.tenantName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button :icon="Search" class="whiteFontButtons" @click="listArray">查询</el-button>
            <el-button :icon="RefreshRight" class="blackFontButtons" @click="clickResetBut">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="tableContent">
        <div ref="tableCenterRef" class="tableCenter">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column label="运营商/站点名称">
              <template #default="{ row }">
                <div class="operateUnitName textTwo">{{ $filters.moreData(row.operateUnitName) }}</div>
                <div class="siteName textTwo">{{ $filters.moreData(row.siteName) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="所在城市">
              <template #default="{ row }">
                <span>{{ $filters.moreData(row.province) }}</span>
                <span>/</span>
                <span>{{ $filters.moreData(row.city) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="微信收款账户"></el-table-column>
            <el-table-column label="微信付款账户"></el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperateBut(1, row)">结算账户配置</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
        </div>
      </div>
    </div>

    <AccountConfigDialog v-if="accountConfigVisible" v-model:isVisible="accountConfigVisible" :activeSiteId="activeSiteId"></AccountConfigDialog>
  </div>
</template>
<script lang="ts">
import pinyin from "js-pinyin";
import {setTreeData} from "@/utils";
import {RefreshRight, Search} from "@element-plus/icons-vue";
import {computed, onMounted, reactive, ref, toRefs} from "vue";
import {AccountConfigDialog} from "@/views/configCenter/component";
import {findSiteBasicInfoByTenantId,findOperatorInfoByTenantId} from "@/api/configCenter/stationSettlementConfig";

export default {
  name: "stationSettlementConfig",
  components: {AccountConfigDialog},
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  setup(props) {

    const that = reactive({
      Search,
      RefreshRight,
      oldFormInline: {},
      formInline: {siteId: ""},

      siteIdArray: [],
      regionArray: [],
      operateUnitArray: [],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      activeSiteId: "",
      accountConfigVisible: false,
    })

    const listArray = (operateType) => {
      // that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      // findPlatformInfoListByPage({ page: that.currentPage, size: that.pageNum, ...that.formInline}).then(res => {
      //   that.totalNumber = res.data.totalSize;
      //   that.list = res.data.items;
      //   that.listLoading = false;
      // }).catch((error) => {
      //   that.listLoading = false;
      //   if (error && error.code === 88886)return
      //   that.list = [];
      //   that.totalNumber = 0;
      // })
    }

    const clickResetBut = () => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      listArray('resetPage');
    }

    const clickOperateBut = (operateType,row)=>{
      if(operateType === 1){
        that.activeSiteId = row.id;
        that.accountConfigVisible = true;
      }
    }

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteBasicInfoByTenantId({}).then(res => {
        let list = res.data ? res.data : [];
        let provinceArray = [], cityArray = [];
        for (let i = 0; i < list.length; i++) {
          //省份处理
          if (list[i].province) {
            let province_id = pinyin.getFullChars(list[i].province);

            let findProvince = provinceArray.find(item => item.id === province_id);
            if (!findProvince) provinceArray.push({name: list[i].province, id: province_id});

            // 市区处理
            let findCity = cityArray.find(item => item.name === list[i].city);
            if (!findCity) cityArray.push({name: list[i].city, parentId: province_id});
          }
        }
        // console.log(provinceArray);
        // console.log(cityArray);
        list.unshift({id: "", siteName: "全部"});
        that.siteIdArray = JSON.parse(JSON.stringify(list));
        that.regionArray = setTreeData([...provinceArray, ...cityArray]);
      })
    }

    // 查询运营商下拉列表
    const queryOperatorInfoByTenantId = () => {
      findOperatorInfoByTenantId({ timer: new Date() }).then(res => {
        let list = res.data ? res.data : [];
        list.unshift({id: undefined, tenantName: "全部"});
        that.operateUnitArray = JSON.parse(JSON.stringify(list));
      })
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
      queryOperatorInfoByTenantId();
      listArray();
    })

    return {...toRefs(that), headerFormRef, setTableMaxHeight, clickResetBut, listArray, querySiteBasicInfoByTenantId,queryOperatorInfoByTenantId,
      clickOperateBut}
  }
}
</script>
<style lang="scss" scoped>

</style>