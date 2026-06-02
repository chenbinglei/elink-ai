<template>
  <div class="app-container-right">
    <div ref="headerFormRef" class="header-form">
      <div class="flex ai-center">
        <el-button :disabled="isAddButtonClick" :icon="Plus" class="whiteFontButtons" @click="clickAddButFun">添加能源系统</el-button>
      </div>
    </div>
    <div class="tableContent scrollbarStyle" v-loading="listLoading">
      <template v-if="list && list.length">
        <template v-for="(item,index) in list" :key="index">
          <TitleView :title="item.systemName">
            <template #headerRight>
              <el-button class="whiteFontButtons" :icon="Edit" @click="clickItemButton(1,item)">编辑</el-button>
              <el-button class="blackFontButtons" :icon="Delete" @click="clickItemButton(2,item)">删除</el-button>
            </template>
            <template #content>
              <div class="content_list">
                <el-row :gutter="12" class="content_list">
                  <el-col :sm="12" :lg="8" :xl="8" class="info_li">
                    <div class="flex_li_left">资产id：</div>
                    <div class="flex_li_right">{{ $filters.moreData(item.id) }}</div>
                  </el-col>
                  <el-col :sm="12" :lg="8" :xl="8" class="info_li">
                    <div class="flex_li_left">所属模型：</div>
                    <div class="flex_li_right">{{ $filters.moreData(item.modelName) }}</div>
                  </el-col>
                  <template v-if="item.reaList && item.reaList.length">
                    <template v-for="(child,i) in item.reaList" :key="i">
                      <el-col :sm="12" :lg="8" :xl="8" class="info_li">
                        <div class="flex_li_left">{{ child.reaName }}：</div>
                        <div class="flex_li_right">
                          <span>{{ $filters.moreData(extendedAttributeFun(item.readwriteObject[child.fieldName],child)) }}</span>
                          <span class="unit" v-if="child.unit">{{ child.unit }}</span>
                        </div>
                      </el-col>
                    </template>
                  </template>
                </el-row>
              </div>
            </template>
          </TitleView>
        </template>
      </template>
      <template v-else><null-data words="未配置能源信息"></null-data></template>
    </div>

    <AddEnergyInfoDialog v-if="addEnergyInfoVisible" v-model:isVisible="addEnergyInfoVisible" :titleName="titleName" :scenarioTypes="returnDataInfo.scenarioTypes"
                         :activeEditInfo="activeEditInfo" @changeEvent="querySiteInfoById"></AddEnergyInfoDialog>
  </div>
</template>

<script>
import {operateButtonIsClick} from "@/utils";
import {ElMessage, ElMessageBox} from "element-plus";
import {setTimerSplitTallyFun} from "@/utils/dateTime";
import {Plus, Edit, Delete} from "@element-plus/icons-vue";
import {onMounted, reactive, toRefs, defineComponent, computed} from "vue";
import AddEnergyInfoDialog from "./EnergyInformation/AddEnergyInfoDialog.vue";
import {deleteSiteScenarioTypeById, findSiteInfoById} from "@/api/siteCenter/stationDetails";

export default defineComponent({
  name: "EnergyInformation",
  components: {AddEnergyInfoDialog},
  props: {
    siteRecordsId: {
      type: [String, Number],
      default: ""
    }
  },
  setup(props) {

    const isAddButtonClick = computed(() => {
      return operateButtonIsClick('/device/siteInfo/saveOrUpdateSiteScenarioType')
    })

    const that = reactive({
      Plus,
      Edit,
      Delete,
      list: [],
      returnDataInfo: {},
      listLoading: false,

      activeEditInfo: {},
      titleName: "新增能源系统",
      addEnergyInfoVisible: false
    })

    const querySiteInfoById = () => {
      that.listLoading = true;
      findSiteInfoById({id: props.siteRecordsId, timer: new Date()}).then(res => {
        let returnDataInfo = res.data ? res.data : {};
        let siteScenarioTypeDtos =  returnDataInfo?.siteScenarioTypeDtos ?? [];
        for(let i = 0;i < siteScenarioTypeDtos.length;i++){
          if(siteScenarioTypeDtos[i].readwriteObject){
            siteScenarioTypeDtos[i].readwriteObject = JSON.parse(siteScenarioTypeDtos[i].readwriteObject);
          }
          if(siteScenarioTypeDtos[i].reaList && siteScenarioTypeDtos[i].reaList.length){
            siteScenarioTypeDtos[i].reaList.forEach(item=>{
              if(item.extraValue) item.extraValue = JSON.parse(item.extraValue);
            })
          }
        }
        that.list = JSON.parse(JSON.stringify(siteScenarioTypeDtos));
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      })
    }

    const clickAddButFun = () => {
      that.titleName = "新增能源系统";
      that.activeEditInfo = { siteId: props.siteRecordsId };
      that.addEnergyInfoVisible = true;
    }

    const clickItemButton = (index, row)=>{
      if(index === 1){
        that.titleName = "新增能源系统";
        let activeEditInfo = JSON.parse(JSON.stringify({ siteId: props.siteRecordsId,...row })) ;
        delete activeEditInfo.reaList;
        that.activeEditInfo = JSON.parse(JSON.stringify(activeEditInfo));
        that.addEnergyInfoVisible = true;
      }

      if(index === 2){
        ElMessageBox.confirm(`您确定要删除（<span class="deleteName">${row.systemName}</span>）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteSiteScenarioTypeById({ id: row.id }).then(()=> {
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
          querySiteInfoById();
          ElMessage({ type:"success",showClose: true,message:"操作成功！" });
        }).catch(() => {
          console.log("取消删除");
        });
      }
    }

    // 扩展属性值处理
    const extendedAttributeFun = (fieldValue, fieldInfo) => {
      let new_field_value = fieldValue;

      if (fieldInfo?.extraValue) {

        //  扩展属性 ----》 下拉选择
        if (fieldInfo.reaType === 3) {
          if (fieldInfo.extraValue?.enumArray && fieldInfo.extraValue?.enumArray.length) {
            let findItem = fieldInfo.extraValue.enumArray.find(item => item.id === fieldValue);
            if (findItem) new_field_value = findItem.name;
          }
        }

        //  扩展属性 ----》 开关
        if (fieldInfo.reaType === 5) {
          new_field_value = fieldValue ? fieldInfo.extraValue.trueValue : fieldInfo.extraValue.falseValue;
        }

        //  扩展属性 ----》 时间
        if (fieldInfo.reaType === 6) {
          new_field_value = setTimerSplitTallyFun(fieldValue, fieldInfo.extraValue?.splitTally, fieldInfo.extraValue?.timeFormat);
        }
      }
      return new_field_value
    }

    onMounted(() => {
      querySiteInfoById();
    })

    return {...toRefs(that), querySiteInfoById, clickItemButton, extendedAttributeFun, isAddButtonClick, clickAddButFun}
  }
})
</script>


<style lang="scss" scoped>
.app-container-right{
  padding-top: 12px;
  box-sizing: border-box;

  .header-form{
    padding: 0;
  }

  .tableContent{
    border-top: none;

    .el-row{
      width: 100%;
      margin-bottom: 11px;

      .info_li {
        display: flex;
        align-items: center;
        margin-bottom: 18px;

        .flex_li_left {
          color: #666666;
          font-size: 14px;
        }

        .flex_li_right {
          color: #121C3F;
          font-size: 14px;
          display: flex;
          align-items: center;

          .unit{
            margin-left: 2px;
            font-weight: bold;
          }

          .copy {
            color: #1F74E2;
            cursor: pointer;
            margin-left: 12px;
          }
        }
      }
    }
  }
}
</style>