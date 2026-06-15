<template>
  <div class="content_body bg_color_class">
    <div class="content_top" v-if="authority === 2">
      <el-button type="primary" @click="clickSetPriceBut">
        <span class="iconfont icon-jine"></span>
        <span>设置定价</span>
      </el-button>
    </div>
    <div class="content_list scrollbarStyle">
      <template v-for="(item,index) in list" :key="index">
        <title-view :title="item.name">
          <template #headerRight>
            <div class="right_button" v-if="authority === 2">
              <el-button class="blackFontButtons" @click="clickOperateBut(1,item.fieldName)">
                <span class="iconfont icon-bianji"></span>
                <span>修改</span>
              </el-button>
              <el-button class="blackFontButtons" @click="clickOperateBut(2,item.fieldName)">
                <span class="iconfont icon-shanchu"></span>
                <span>删除费率</span>
              </el-button>
            </div>
          </template>
          <template #content>
            <div class="content_table" v-loading="listLoading">
              <el-table :data="returnDataInfo[item.fieldName]" :max-height="tableMaxHeight">
                <el-table-column label="收费时段">
                  <template #default="{ row }">00:00～23:59</template>
                </el-table-column>
                <el-table-column label="免占桩时长（分钟）">
                  <template #default="{ row }">{{ $filters.moreData(row.avoidDuration) }}</template>
                </el-table-column>
                <el-table-column label="收费标准">
                  <template #default="{ row }">
                    <template v-if="row.configType === 1">
                      <span>固定价格：</span>
                      <span>{{ $filters.moreData(row.configPriceInfoList[0].chargePrice) }}</span>
                      <span>元</span>
                    </template>
                    <template v-if="row.configType === 2">
                      <template v-for="(item,index) in row.configPriceInfoList" :key="index">
                        <span>超时大于等于{{ item.timeoutDuration }}分钟，</span>
                        <span v-if="item.chargeType === 1">{{ $filters.moreData(item.chargePrice) }}元/分钟；</span>
                        <span v-else>固定金额{{ $filters.moreData(item.chargePrice) }}元</span>
                      </template>
                    </template>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </title-view>
      </template>
    </div>

    <SetPriceDialog v-if="setPriceVisible" v-model:isVisible="setPriceVisible" :siteId="siteId" :activeFormDialog="activeFormDialog" @changeEvent="queryOccupyPilePriceInfoById" />
  </div>
</template>
<script lang="ts">
import {ElMessage, ElMessageBox} from "element-plus";
import SetPriceDialog from "./CsOccupyingPilePrice/SetPriceDialog.vue";
import {onMounted, reactive, toRefs, watch, defineComponent} from "vue";
import {deleteOccupyPilePriceById, findOccupyPilePriceInfoById} from "@/api/operationManagement/CsStationDetails";

export default defineComponent({
  name: "CsOccupyingPilePrice",
  components: {SetPriceDialog},
  props: {
    siteId: {
      type: [Number, String],
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
      returnDataInfo: {},
      listLoading: false,
      tableMaxHeight: 380,
      list: [
        {name: '交流计费', fieldName: 'acPriceInfoDataArray',englishName:"acPriceInfoData"},
        {name: '直流计费', fieldName: 'dcPriceInfoDataArray',englishName:"dcPriceInfoData"},
      ],

      activeFormDialog:{},
      setPriceVisible: false,
    });

    // 根据站点id查询占桩价格信息
    const queryOccupyPilePriceInfoById = () => {
      that.listLoading = true;
      findOccupyPilePriceInfoById({siteId: props.siteId}).then(res => {
        let returnDataInfo = res.data ? res.data : {};
        if(returnDataInfo.acPriceInfoData) {
          returnDataInfo.acPriceInfoDataArray = [returnDataInfo.acPriceInfoData];
          if(returnDataInfo.acPriceInfoData.configPriceInfoStr){
            returnDataInfo.acPriceInfoData.configPriceInfoStr = JSON.parse(returnDataInfo.acPriceInfoData.configPriceInfoStr);
          }
        }
        if(returnDataInfo.dcPriceInfoData){
          returnDataInfo.dcPriceInfoDataArray = [returnDataInfo.dcPriceInfoData];
          if(returnDataInfo.dcPriceInfoData.configPriceInfoStr){
            returnDataInfo.dcPriceInfoData.configPriceInfoStr = JSON.parse(returnDataInfo.dcPriceInfoData.configPriceInfoStr);
          }
        }
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const clickSetPriceBut = ()=>{
      that.activeFormDialog = {};
      that.setPriceVisible = true;
    };

    const clickOperateBut = (operateType, fieldName)=>{

      if(operateType === 1){
        let findItem = that.list.find(item => item.fieldName === fieldName);

        if(!that.returnDataInfo[findItem.englishName]){
          ElMessage({type: "error", showClose: true, message: "请先添加计费策略！"});
          return;
        }

        that.activeFormDialog = that.returnDataInfo[findItem.englishName];
        that.setPriceVisible = true;
      }

      if(operateType === 2){
        let findItem = that.list.find(item => item.fieldName === fieldName);
        if(!that.returnDataInfo[findItem.englishName]){
          ElMessage({type: "error", showClose: true, message: "请先添加计费策略！"});
          return;
        }

        ElMessageBox.confirm(`您确定要删除当前计费策略（<span class="highlightText">${ findItem.name }</span>>）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteOccupyPilePriceById({ pirceId: that.returnDataInfo[findItem.englishName].id }).then(() => {
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
          queryOccupyPilePriceInfoById();
          ElMessage({type: "success", showClose: true, message: "操作成功！"});
        }).catch(() => {
          console.log("取消删除");
        });
      }
    };

    const watchSiteId = watch(() => props.siteId, (newSite) => {
      queryOccupyPilePriceInfoById();
    }, {deep: true});

    onMounted(() => {
      queryOccupyPilePriceInfoById();
    });

    return {...toRefs(that), queryOccupyPilePriceInfoById, watchSiteId, clickSetPriceBut, clickOperateBut};
  }
});
</script>

<style lang="scss" scoped>
.content_body {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 12px;
  box-sizing: border-box;

  .content_top {
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }

  .content_list {
    flex: 1;
    overflow-y: auto;
    padding-top: 16px;
    box-sizing: border-box;

    .content_table{
      margin-bottom: 24px;
    }
  }

  .iconfont {
    margin-right: 4px;
  }
}
</style>