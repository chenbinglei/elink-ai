<template>
  <div class="content_table" ref="tableContentRef" v-resize="setTableMaxHeight">
    <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
      <template #empty><null-data words="暂无数据"></null-data></template>
      <el-table-column label="序号" type="index" width="80"></el-table-column>
      <el-table-column label="生效时间">
        <template #default="{ row }">{{ $filters.moreData(row.takeTime)}}</template>
      </el-table-column>
      <el-table-column label="设备">
        <template #default="{ row }">{{ $filters.pileDeviceType(row.deviceType)}}</template>
      </el-table-column>
      <el-table-column label="价格状态">
        <template #default="{ row }">
          <div class="priceState" :class="'priceState' + row.priceState">
            <span class="text">{{ $filters.priceState(row.priceState)}}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <div class="table_operate_class">
            <el-link :underline="false" @click="clickOperateBut(1, row)">
              <span class="iconfont icon-dingdanguanli"></span>
              <span>详情</span>
            </el-link>
            <span class="split_line">|</span>
            <el-link :underline="false" @click="clickOperateBut(3, row)">
              <span class="iconfont icon-fuzhi"></span>
              <span>复制时段</span>
            </el-link>
            <template v-if="row.priceState === 2 && authority === 2">
              <span class="split_line">|</span>
              <el-link type="danger" :underline="false" @click="clickOperateBut(2, row)">取消</el-link>
            </template>
            <template v-if="row.priceState === 1 && authority === 2">
              <span class="split_line">|</span>
              <el-link type="danger" :underline="false" @click="clickOperateBut(4, row)">删除</el-link>
            </template>

            <span class="split_line">|</span>
            <el-link :underline="false" @click="clickOperateBut(5, row)">应用到其他站点</el-link>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <SetPriceDialog v-if="setPriceVisible" v-model:isVisible="setPriceVisible" :chargingType="chargingType" :siteId="siteId"
                    :activePricingId="activePricingId" @changeEvent="queryFixPriceRecordList"></SetPriceDialog>
    <PricingDetailsDialog v-if="pricingDetailsVisible" v-model:isVisible="pricingDetailsVisible" :activePricingId="activePricingId" />
    <ApplyToOtherSitesDialog v-if="applyToOtherSitesVisible" v-model:isVisible="applyToOtherSitesVisible" :siteId="siteId" :activePricingId="activePricingId" />
  </div>
</template>

<script lang="ts">
import SetPriceDialog from "./SetPriceDialog.vue";
import {ElMessage, ElMessageBox} from "element-plus";
import PricingDetailsDialog from "./PricingDetailsDialog.vue";
import ApplyToOtherSitesDialog from "./ApplyToOtherSitesDialog.vue";
import {watch, ref, reactive, toRefs, onMounted, defineComponent} from "vue";
import {deletePriceInfoById, findFixPriceRecordList, updatePriceStateById} from "@/api/operationManagement/CsStationDetails";

export default defineComponent({
  name: "PricingRecords",
  components:{SetPriceDialog, PricingDetailsDialog, ApplyToOtherSitesDialog},
  props: {
    // 1： 充   2；放
    chargingType: {
      type: Number,
      default: 1
    },
    siteId: {
      type: [Number,String],
      default: ""
    },
    //权限 1-只读 2-读写
    authority:{
      type: Number,
      default: 1
    },
  },
  setup(props) {

    const that = reactive({
      list:[],
      listLoading: false,
      tableMaxHeight: 280,

      activePricingId: "",
      setPriceVisible: false,
      pricingDetailsVisible: false,
      applyToOtherSitesVisible: false,
    });

    // 根据站点id查询定价记录
    const queryFixPriceRecordList = ()=>{
      that.listLoading = true;
      findFixPriceRecordList({ priceType: props.chargingType,siteId: props.siteId, type: "PricingRecords" }).then(res=>{
        that.list = res.data.fixPriceRecordList;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const clickOperateBut = (operateType, row) => {

      if(operateType === 1){
        that.activePricingId = row.id;
        that.pricingDetailsVisible = true;
      }

      if(operateType === 2){
        ElMessageBox.confirm(`您确定要取消当前计费策略（<span class="highlightText">${row.takeTime}</span>）吗？`, "提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在取消...';
              deletePriceInfoById({ pirceId: row.id }).then(() => {
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
          queryFixPriceRecordList();
          ElMessage({type: "success", showClose: true, message: "操作成功！"});
        }).catch(() => {
          console.log("取消删除");
        });
      }

      if(operateType === 3){
        that.activePricingId = row.id;
        that.setPriceVisible = true;
      }

      if(operateType === 4){
        ElMessageBox.confirm(`您确定要删除当前计费策略（<span class="highlightText">${row.takeTime}</span>）吗？`, "提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              updatePriceStateById({ pirceId: row.id,priceState: 3 }).then(() => {
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
          queryFixPriceRecordList();
          ElMessage({type: "success", showClose: true, message: "操作成功！"});
        }).catch(() => {
          console.log("取消删除");
        });
      }

      if(operateType === 5){
        that.activePricingId = row.id;
        that.applyToOtherSitesVisible = true;
      }
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const setTableMaxHeight = ()=>{
      that.tableMaxHeight = tableContentRef.value.offsetHeight;
    };

    const watchSiteId = watch(()=>props.siteId,(newSite)=>{
      queryFixPriceRecordList();
    },{ deep:true });

    onMounted(()=>{
      queryFixPriceRecordList();
    });

    return {...toRefs(that),tableContentRef,setTableMaxHeight,queryFixPriceRecordList,watchSiteId, clickOperateBut };
  }
});
</script>
<style lang="scss" scoped>
.content_table{
  height: 100%;

  .iconfont{
    margin-right: 4px;
  }

  .priceState{
    width: fit-content;
    height: 32px;
    padding: 0 16px;
    line-height: 32px;
    border-radius: 8px;
    background: rgba(136, 136, 136, .2);

    .text{
      color: #888888;
      font-size: 14px;
    }
  }
  .priceState1{
    background: rgba(65, 203, 74, .2);
    .text{
      color: #41CB4A;
    }
  }
  .priceState2{
    background: rgba(255, 156, 2, .2);
    .text{
      color: #FF9C02;
    }
  }
}
</style>