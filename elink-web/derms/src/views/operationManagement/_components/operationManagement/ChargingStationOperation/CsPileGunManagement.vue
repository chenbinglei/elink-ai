<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="关键词：">
              <el-input v-model="formInline.pileName" clearable placeholder="请输入桩名称/编号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="选择站点：">
              <el-select v-model="formInline.siteId" collapse-tags multiple filterable clearable placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="所属区域：">
              <el-col :span="8" style="padding: 0">
                <el-select v-model="formInline.areaType" placeholder="请选择" @change="formInline.area = ''">
                  <el-option v-for="(item, index) in areaTypeArray" :key="index" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-col>
              <el-col :span="16" style="padding: 0">
                <el-select v-model="formInline.area" filterable clearable placeholder="请选择">
                  <template v-for="(item, index) in (formInline.areaType === 1 ? provinceArray : cityArray)" :key="index">
                    <el-option :label="item.name" :value="item.name"></el-option>
                  </template>
                </el-select>
              </el-col>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="桩状态：">
              <el-select v-model="formInline.workStatus" clearable placeholder="全部">
                <el-option v-for="item in workStatusArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="枪状态：">
              <el-select v-model="formInline.gunWorkState" clearable placeholder="全部">
                <el-option v-for="item in gunWorkStateArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="生产厂家：">
              <el-input v-model="formInline.manufacturersName" clearable placeholder="请输入生产厂家"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="桩类型：">
              <el-select v-model="formInline.typeId" filterable clearable placeholder="全部">
                <el-option v-for="item in typeIdArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
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
      <TableHeaderTitle title="桩枪管理"></TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight" ref="tableRef">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column fixed label="桩编号/名称" min-width="180">
            <template #default="{ row }">
              <div class="operateUnitName textTwo">{{ $filters.moreData(row.deviceNumber) }}</div>
              <div class="siteName textTwo">{{ $filters.moreData(row.deviceName) }}</div>
            </template>
          </el-table-column>
          <el-table-column label="运营商/站点" min-width="180">
            <template #default="{ row }">
              <div class="operateUnitName textTwo">{{ $filters.moreData(row.operateName) }}</div>
              <div class="siteName textTwo">{{ $filters.moreData(row.siteName) }}</div>
            </template>
          </el-table-column>
          <el-table-column label="类型">
            <template #default="{ row }">
              <div class="pileType" :class="'pileType' + row.typeId">
                <span>{{ $filters.pileType(row.typeId) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="额定功率(kW)" min-width="120">
            <template #default="{ row }">{{ $filters.moreData(row.ratedPower) }}</template>
          </el-table-column>
          <el-table-column label="桩状态">
            <template #default="{ row }">
              <div class="workStatus" :class="'workStatus' + row.workStatus">
                <span>{{ $filters.deviceStatus(row.workStatus) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="枪状态" min-width="320">
            <template #default="{ row }">
              <GunStatusInfoCard :gunStateInfoList="row.gunStateInfoList"></GunStatusInfoCard>
            </template>
          </el-table-column>
          <el-table-column label="厂商" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.manufacturer) }}</template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="180">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" @click="clickOperateBut(1, row)">详情</el-link>
                <span class="split_line">|</span>
                <el-dropdown>
                  <span class="el-dropdown-link">
                    <el-link>更多</el-link>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item><el-link :underline="false" @click="clickOperateBut(2, row)">查看二维码</el-link></el-dropdown-item>
                      <el-dropdown-item><el-link :underline="false" @click="clickOperateBut(4, row)">下发二维码</el-link></el-dropdown-item>
                      <el-dropdown-item> <el-link :underline="false" @click="clickOperateBut(3, row)">{{ row.operateStatus === 1 ? '检修' : '恢复'
                  }}</el-link></el-dropdown-item>

                    </el-dropdown-menu>
                  </template>
                </el-dropdown>

                <!-- <el-link :underline="false" @click="clickOperateBut(2, row)">二维码</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(3, row)">{{ row.operateStatus === 1 ? '检修' : '恢复'
                  }}</el-link> -->
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray" />
      </div>
    </div>
    <el-dialog v-model="is_visible" :close-on-click-modal="closeOnClickModal" :custom-class="customClass" :show-close="showClose" title="下发二维码" width="40%"
      @close="handleClose">
      <div v-loading="listLoading && !disabledLoading" class="dialog_content">
        <el-form :model="formData" :rules="rules" ref="formRef" label-width="150" class="dialog_form" scroll-to-error>
          <el-form-item label="二维码地址" prop="qrCodeUrl">
            <el-input type="textarea" v-model="formData.qrCodeUrl" placeholder="请输入二维码地址" clearable></el-input>
          </el-form-item>
          <div style="font-size: 12px; color: #acc; text-align: center; margin-left: 90px;margin-bottom: 20px;">
            参考样式:https://sdccs.sunmaxxtech.com/#/applet?mc=nlec&prot=ykc&No=</div>

        </el-form>
        <div style="text-align: right;">
          <el-button type="primary" @click="issueClick()">下发</el-button>
          <el-button type="primary" @click="is_visible = false">取消</el-button>
        </div>
      </div>
    </el-dialog>
    <ChargingPileQrCodeDialog v-if="chargingPileQrCodeVisible" v-model:isVisible="chargingPileQrCodeVisible" :activePileId="activePileId">
    </ChargingPileQrCodeDialog>
  </div>
</template>

<script>
import { useOperationManagementStore } from '@/stores/index';

import pinyin from "tiny-pinyin";
import { queryUserAuthorityIsHaveFun } from "@/utils";
import { ElMessage, ElMessageBox } from "element-plus";
import { pickerOptionsGthanAcTime } from "@/utils/dateTime";
import { RefreshRight, Search } from '@element-plus/icons-vue';
import { onMounted, reactive, ref, toRefs, defineComponent, nextTick } from "vue";
import { findSiteInfoByUserId } from "@/api/operationManagement/CsStationManagement";
import { GunStatusInfoCard, ChargingPileQrCodeDialog } from "./CsPileGunManagement/index";
import { findPileListByPage, updateDeviceOperateStatus, pileSetQr } from "@/api/operationManagement/CsPileGunManagement";
import { area_type_array, gun_work_state_array, pile_status_array, pile_type_array } from "@/utils/setVariate";

export default defineComponent({
  name: "CsPileGunManagement",
  components: { GunStatusInfoCard, ChargingPileQrCodeDialog },
  setup () {

    const operationManagementStore = useOperationManagementStore();
    const that = reactive({
      Search,
      RefreshRight,
      oldFormInline: {},
      formInline: { areaType: 1 },

      cityArray: [],
      siteIdArray: [],
      provinceArray: [],
      typeIdArray: pile_type_array,
      areaTypeArray: area_type_array,
      workStatusArray: pile_status_array,
      gunWorkStateArray: gun_work_state_array,
      pickerOptions: pickerOptionsGthanAcTime(),
      alarmStatusArray: [{ id: 0, name: "未修复" }, { id: 1, name: "已修复" }],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      activePileId: "",
      chargingPileQrCodeVisible: false,
      is_visible: false,
      pileCode: "",
      formData: {
        deviceNumber: ""
      },
      rules: {
        qrCodeUrl: [{ required: true, message: "请输入二维码地址", trigger: "blur" }],
      },
    });
    const formRef = ref()
    const tableRef = ref(null);
    const listArray = (operateType) => {
      that.listLoading = true;
      let obj = {
        ...that.formInline,
        siteId: String(that.formInline.siteId), // 转为字符串
      };
      let formInline = JSON.parse(JSON.stringify(obj));
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findPileListByPage({ page: that.currentPage, size: that.pageNum, ...formInline }).then(async res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({ type: "success", message: "重置成功", showClose: true });
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

      if (operateType === 1) {
        const routeName = "/operationManagement/CsChargingPileDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if (!isAuthority) {
          ElMessage({ type: "warning", showClose: true, message: "请联系管理员打开对应权限！" });
          return;
        }

        operationManagementStore.updateSecondaryInfo({
          subTitle: `设备详情 - ${row.deviceName}`, id: row.id, pileCode: row.deviceNumber,
          componentName: "CsChargingPileDetails", backComponentName: "CsPileGunManagement"
        });
        operationManagementStore.updateSecondaryVisible(true);
      }

      if (operateType === 2) {
        that.activePileId = row.id;
        that.chargingPileQrCodeVisible = true;
      }

      if (operateType === 3) {
        let operateStatus = row.operateStatus === 1 ? 2 : 1;
        let operateStatusText = row.operateStatus === 1 ? '检修' : '恢复';
        ElMessageBox.confirm(`确定${operateStatusText}（<span class="highlightText">${row.deviceName}</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在操作...';
              updateDeviceOperateStatus({ deviceId: row.id, operateStatus: operateStatus }).then(() => {
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
          ElMessage({ type: "success", showClose: true, message: "操作成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
      if (operateType === 4) {
        that.pileCode = row.deviceNumber
        that.is_visible = true
      }
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({ scenarioTypes: "3", timer: new Date() }).then(res => {
        let list = res.data ? res.data : [];
        let provinceArray = [], cityArray = [];
        for (let i = 0; i < list.length; i++) {

          // 读写类型字段
          if (list[i].siteReadwriteObject) list[i].siteReadwriteObject = JSON.parse(list[i].siteReadwriteObject);
          let location_info = list[i]?.siteReadwriteObject?.location ?? {};

          //省份处理
          if (location_info.province) {
            let province_id = pinyin.convertToPinyin(location_info.province);

            let findProvince = provinceArray.find(item => item.id === province_id);
            if (!findProvince) provinceArray.push({ name: location_info.province, id: province_id });

            // 市区处理
            let findCity = cityArray.find(item => item.name === location_info.city);
            if (!findCity) cityArray.push({ name: location_info.city, parentId: province_id });
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

    // 下发
    const issueClick = () => {
      formRef.value.validate((valid, fields) => {
        if (valid) {
          pileSetQr({ pileCode: that.pileCode, qrStr: that.formData.qrCodeUrl }).then(() => {
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
            handleClose();
          }).catch(() => {
            ElMessage({ type: "error", showClose: true, message: "操作失败！" });
          });
        }
      })
    }
    const handleClose = () => {
      that.is_visible = false
      formRef.value.resetFields()
    }
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

    return { ...toRefs(that),tableRef, clickResetForm, setTableMaxHeight, tableContentRef, tablePaginationRef, listArray, querySiteBasicInfoByTenantId, clickOperateBut, formRef, issueClick, handleClose };
  }
});
</script>

<style scoped lang="scss">
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  .operateUnitName {
    color: #ffffff;
    font-size: 14px;
    font-weight: bold;
    -webkit-line-clamp: 1;
  }

  .siteName {
    font-size: 14px;
    margin-top: 12px;
    -webkit-line-clamp: 1;
    color: rgba(255, 255, 255, 0.8);
  }

  .workStatus {
    width: fit-content;
    padding: 0 16px;
    height: 32px;
    color: #c7c7c7;
    font-size: 14px;
    border-radius: 8px;
    background: #4abeff4d;
    line-height: 32px;
  }

  .workStatus1 {
    color: #41cb4a;
    background: rgba(65, 203, 74, 0.2);
  }

  .workStatus2 {
    color: #ff9c02;
    background: rgba(255, 156, 2, 0.2);
  }

  .workStatus3 {
    color: #ff2b2b;
    background: rgba(255, 43, 43, 0.2);
  }
}
</style>