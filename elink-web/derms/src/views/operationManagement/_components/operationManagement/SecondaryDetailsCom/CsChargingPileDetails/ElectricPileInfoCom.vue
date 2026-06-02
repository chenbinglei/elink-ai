<template>
<div class="electricPileInfoCom">
  <template v-for="(item,index) in list" :key="index">
    <template v-if="(item.fieldName === 'extendedAttr' && item.children&&item.children.length) || item.fieldName !== 'extendedAttr'">
      <title-view :title="item.name">
        <template #headerRight>
          <template v-if="item.fieldName === 'extendedAttr'">
            <el-button class="blackFontButtons" :icon="Edit" @click="clickOperateBut(item.fieldName)">编辑</el-button>
          </template>
        </template>
        <template #content>
          <div class="content_list" v-loading="listLoading">
            <template v-if="item.fieldName !== 'chargingGun' && item.fieldName !== 'equipmentPhotos'">
              <el-row :gutter="12" class="content_list">
                <template v-for="(child,i) in item.children" :key="i">
                  <el-col :sm="12" :lg="8" :xl="8" class="info_li">
                    <div class="flex_li_left">{{ child.reaName }}：</div>
                    <div class="flex_li_right textTwo">
                      <span v-if="child.filterName">{{ $filters[child.filterName](returnDataInfo[child.fieldName]) }}</span>
                      <span v-else>{{ $filters.moreData(extendedAttributeFun(child)) }}</span>
                      <span v-if="child.unit" class="unit">{{ child.unit }}</span>
                    </div>
                  </el-col>
                </template>
              </el-row>
            </template>
            <template v-else>
              <template v-if=" item.fieldName === 'chargingGun'">
                <el-table :data="returnDataInfo.gunDetailList" v-loading="listLoading" :max-height="tableMaxHeight">
                  <el-table-column align="center" label="枪编号" fixed min-width="120">
                    <template #default="{ row }">{{ $filters.moreData(row.gunCode) }}</template>
                  </el-table-column>
                  <el-table-column align="center" label="枪名称" fixed min-width="120">
                    <template #default="{ row }">{{ $filters.moreData(row.gunName) }}</template>
                  </el-table-column>
                  <el-table-column align="center" label="枪型号" min-width="240" show-overflow-tooltip>
                    <template #default="{ row }">{{ $filters.pileGunModel(row.type) }}</template>
                  </el-table-column>
                  <el-table-column align="center" label="额定功率（kW）" min-width="210">
                    <template #default="{ row }">{{ $filters.moreData(row.ratedPower) }}</template>
                  </el-table-column>
                  <el-table-column align="center" label="车位号" min-width="210">
                    <template #default="{ row }">{{ $filters.moreData(row.parkNo) }}</template>
                  </el-table-column>
                  <el-table-column align="center" label="接口类型" min-width="210">
                    <template #default="{ row }">{{ $filters.nationalStandard(row.nationalStandard) }}</template>
                  </el-table-column>
                  <el-table-column align="center" label="操作" fixed="right" min-width="210">
                    <template #default="{ row }">
                      <div class="table_operate_class">
                        <el-link type="primary" :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
                <div style="height: 24px;"></div>
              </template>
              <template v-if=" item.fieldName ===  'equipmentPhotos'">
                <div class="realisticView" v-loading="listLoading">
                  <template v-if="returnDataInfo.imagePaths && returnDataInfo.imagePaths.length">
                    <template v-for="(itam,indax) in returnDataInfo.imagePaths" :key="indax">
                      <div class="image_item_li">
                        <Viewer ref="viewerRef" :imageArray="returnDataInfo.imagePaths" :activeImage="itam"></Viewer>
                      </div>
                    </template>
                  </template>
                  <null-data v-else words="暂未上传设备照片"></null-data>
                </div>
              </template>
            </template>
          </div>
        </template>
      </title-view>
    </template>
  </template>

  <add-device-dialog v-if="addDeviceVisible" v-model:isVisible="addDeviceVisible" :activeEditInfo="activeEditInfo" @changeEvent="initParamConfigFun" />
  <AddChargingGunDialog v-if="addChargingGunVisible" v-model:isVisible="addChargingGunVisible" :titleName="titleName" :formDialog="activeEditInfo" @changeEvent="initParamConfigFun"/>
</div>
</template>

<script>
import {Edit} from '@element-plus/icons-vue';
import Viewer from "@/components/component/Viewer.vue";
import {setTimerSplitTallyFun} from "@/utils/dateTime";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import AddDeviceDialog from "./ElectricPileInfoCom/AddDeviceDialog.vue";
import AddChargingGunDialog from "./ElectricPileInfoCom/AddChargingGunDialog.vue";
import {findPileDetailById} from "@/api/operationManagement/CsPileGunRunningStatus";

export default defineComponent({
  name: "ElectricPileInfoCom",
  components:{Viewer,AddChargingGunDialog,AddDeviceDialog},
  props: {
    routeInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props){

    const that = reactive({
      Edit,
      listLoading: false,
      returnDataInfo: {},
      tableMaxHeight: 280,

      list: [
        {
          name: '基本信息',
          fieldName: "basicInfo",
          children: [
            {reaName: "桩编号", fieldName: "deviceNumber", unit: ""},
            {reaName: "桩名称", fieldName: "deviceName", unit: ""},
            {reaName: "所属电站", fieldName: "siteName", filterName: "", unit: ""},
            {reaName: "接入类型", fieldName: "accessType", filterName: "accessType"},
            {reaName: "运营状态", fieldName: "operateStatus", filterName: "operateStatus"},
            {reaName: "设备类型", fieldName: "typeId", filterName: "pileType"},
            {reaName: "设备描述", fieldName: "deviceDesc"},
          ]
        },
        {
          name: '扩展属性',
          fieldName: "extendedAttr",
          children: []
        },
        {name: '充电枪', fieldName: "chargingGun"},
        {name: '设备照片', fieldName: "equipmentPhotos"},
      ],

      activeEditInfo: {},
      titleName: "添加充电枪",
      addDeviceVisible: false,
      addChargingGunVisible: false,
    });

    // 根据id查询电桩详情信息
    const initParamConfigFun = () => {
      that.listLoading = true;
      findPileDetailById({ id: props.routeInfo.id }).then(res=>{
        let returnDataInfo = res.data ? res.data : {};

        // 处理扩展属性返回值
        if(returnDataInfo.readwriteObject){
          returnDataInfo.readwriteObject = JSON.parse(returnDataInfo.readwriteObject);
          returnDataInfo = Object.assign({},returnDataInfo,returnDataInfo.readwriteObject);
        }
        
        if(returnDataInfo.imagePaths)returnDataInfo.imagePaths = returnDataInfo.imagePaths.split(',');

        // 扩展属性处理
        if(returnDataInfo.deviceReaList && returnDataInfo.deviceReaList.length){
          returnDataInfo.newDeviceReaList = JSON.parse(JSON.stringify(returnDataInfo.deviceReaList));

          for(let i = 0;i < returnDataInfo.deviceReaList.length;i++){
            if(returnDataInfo.deviceReaList[i].extraValue){
              returnDataInfo.deviceReaList[i].extraValue = JSON.parse(returnDataInfo.deviceReaList[i].extraValue);
            }

            // 位置信息处理
            if(returnDataInfo.deviceReaList[i].reaType === 4){
              let activeDataInfo = JSON.parse(JSON.stringify(returnDataInfo.deviceReaList[i]));
              returnDataInfo.deviceReaList[i].fieldName = returnDataInfo.deviceReaList[i].fieldName + ".longitudeAndLatitude";

              let active_obj = JSON.parse(JSON.stringify(activeDataInfo));
              returnDataInfo.deviceReaList.splice(i + 1,0,{...active_obj,reaName: activeDataInfo.reaName + "-具体地址",fieldName: activeDataInfo.fieldName + ".address"});
              returnDataInfo.deviceReaList.splice(i + 1,0,{...active_obj,reaName: activeDataInfo.reaName + "-所在城市",fieldName: activeDataInfo.fieldName + ".city"});
              i+=2;
            }
          }
          // console.log(returnDataInfo.deviceReaList);
          let findIndex = that.list.findIndex(item=> item.fieldName === "extendedAttr");
          that.list[findIndex].children = JSON.parse(JSON.stringify(returnDataInfo.deviceReaList));
        }

        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const clickOperateBut = (operateType,row)=>{
      if(operateType === 1){
        that.titleName = "编辑充电枪";
        that.activeEditInfo = JSON.parse(JSON.stringify(row));
        that.addChargingGunVisible = true;
      }

      if(operateType === "extendedAttr"){
        that.activeEditInfo = JSON.parse(JSON.stringify({
          id: props.routeInfo.id,
          deviceName: that.returnDataInfo.deviceName,
          deviceNumber: that.returnDataInfo.deviceNumber,
          deviceReaList: that.returnDataInfo.newDeviceReaList,
          readwriteObject: that.returnDataInfo.readwriteObject,
        }));
        // console.log(that.activeEditInfo);
        that.addDeviceVisible = true;
      }
    };

    // 扩展属性值处理
    const extendedAttributeFun = (fieldInfo)=>{
      let fieldNameArray = fieldInfo.fieldName.split('.');
      let fieldValue = that.returnDataInfo[fieldNameArray[0]];
      if(fieldNameArray.length >= 2){
        for(let i = 1;i < fieldNameArray.length;i++){
          if(fieldValue) fieldValue = fieldValue[fieldNameArray[i]];
        }
      }
      // console.log(fieldValue);

      if(fieldInfo.extraValue){
        //  扩展属性 ----》 下拉选择
        if(fieldInfo.reaType === 3){
          if(fieldInfo.extraValue?.enumArray && fieldInfo.extraValue?.enumArray.length){
            // 多选
            if(fieldInfo.extraValue.multiple){
              // console.log(fieldValue);
              // console.log(fieldInfo.extraValue.enumArray);
              if(fieldValue && fieldValue.length){
                for(let i = 0;i < fieldValue.length;i++){
                  let findItem = fieldInfo.extraValue.enumArray.find(item=> item.id === fieldValue[i]);
                  if(findItem) fieldValue[i] = findItem.name;
                }
                fieldValue = fieldValue.join('，');
              }
            } else {
              let findItem = fieldInfo.extraValue.enumArray.find(item=> item.id === fieldValue);
              if(findItem) fieldValue = findItem.name;
            }
          }
        }

        //  扩展属性 ----》 开关
        if(fieldInfo.reaType === 5){
          fieldValue = fieldValue ? fieldInfo.extraValue.trueValue : fieldInfo.extraValue.falseValue;
        }

        //  扩展属性 ----》 时间
        if(fieldInfo.reaType === 6){
          fieldValue = setTimerSplitTallyFun(fieldValue,fieldInfo.extraValue?.splitTally,fieldInfo.extraValue?.timeFormat);
        }
      }

      return fieldValue;
    };

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), initParamConfigFun, clickOperateBut, extendedAttributeFun};

  }
});

</script>

<style scoped lang="scss">
.el-row {
  width: 100%;
  margin-bottom: 11px;

  .info_li {
    display: flex;
    align-items: center;
    margin-bottom: 24px;

    .flex_li_left {
      color: #D3ECFB;
      font-size: 14px;
      white-space: nowrap;
    }

    .flex_li_right {
      color: #D3ECFB;
      font-size: 14px;
      display: flex;
      align-items: center;

      .unit{
        margin-left: 2px;
      }
    }
  }
}

.realisticView {
  display: flex;
  flex-wrap: wrap;
  align-items: center;

  .image_item_li {
    width: 100px;
    height: 100px;
    overflow: hidden;
    margin-right: 12px;
    border-radius: 8px;
    box-sizing: border-box;
    border: 1px solid #EAEEF1;
    position: relative;

    &:last-child {
      margin-right: 0;
    }
  }
}
.blackFontButtons{
  margin-right: 15px;
}
</style>