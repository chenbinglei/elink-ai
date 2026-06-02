<template>
  <div class="content_body bg_color_class scrollbarStyle">
    <template v-for="(item,index) in list" :key="index">
      <title-view :title="item.name">
        <template #headerRight>
          <template v-if="item.isEditBut && authority === 2">
            <el-button class="blackFontButtons" :icon="Edit" @click="clickEditButton(item.fieldName)">编辑</el-button>
          </template>
        </template>
        <template #content>
          <div class="content_list"  v-loading="listLoading">
            <template v-if="item.fieldName !== 'RealisticView'">
              <el-row :gutter="12" class="content_list">
                <template v-for="(child,i) in item.children" :key="i">
                  <el-col :sm="12" :lg="8" :xl="8" class="info_li">
                    <div class="flex_li_left">{{ child.reaName }}：</div>
                    <div class="flex_li_right">
                      <span v-if="child.filterName">{{ $filters[child.filterName](returnDataInfo[child.fieldName]) }}</span>
                      <span v-else>{{ $filters.moreData(extendedAttributeFun(child)) }}</span>
                      <span class="unit" v-if="child.unit">{{ child.unit }}</span>
                    </div>
                  </el-col>
                </template>
              </el-row>
            </template>
            <template v-else>
              <div class="realisticView">
                <template v-if="returnDataInfo.imagePath && returnDataInfo.imagePath.length">
                  <template v-for="(itam,indax) in returnDataInfo.imagePath" :key="indax">
                    <div class="image_item_li">
                      <Viewer ref="viewerRef" :imageArray="returnDataInfo.imagePath" :activeImage="itam"></Viewer>
                      <div class="preview flex-jc-ai-center" @click="clickPreviewBut(indax)">
                        <el-icon size="18" color="#FFFFFF"><Search /></el-icon>
                      </div>
                    </div>
                  </template>
                </template>
                <null-data v-else words="暂未上传实景图"></null-data>
              </div>
            </template>
          </div>
        </template>
      </title-view>
    </template>

    <EditSiteInfoDialog v-if="editSiteInfoVisible" v-model:isVisible="editSiteInfoVisible" :siteId="siteId" @changeEvent="querySiteBasicInfoById"/>
    <RealisticViewDialog v-if="realisticViewVisible" v-model:isVisible="realisticViewVisible" :returnDataInfo="returnDataInfo" @changeEvent="querySiteBasicInfoById" />
  </div>
</template>
<script>
import { Edit,Search } from '@element-plus/icons-vue';
import Viewer from "@/components/component/Viewer.vue";
import {setTimerSplitTallyFun} from "@/utils/dateTime";
import {findSiteInfoById} from "@/api/operationManagement/CsStationDetails";
import EditSiteInfoDialog from "./CsSiteInformation/EditSiteInfoDialog.vue";
import RealisticViewDialog from "./CsSiteInformation/RealisticViewDialog.vue";
import {onMounted, reactive, toRefs, watch, ref, defineComponent} from "vue";

export default defineComponent({
  name: "CsSiteInformation",
  components: {EditSiteInfoDialog,RealisticViewDialog,Viewer,Search},
  props:{
    siteId:{
      type: [Number,String],
      default:""
    },
    //权限 1-只读 2-读写
    authority:{
      type: Number,
      default: 1
    },
  },
  setup(props) {

    const viewerRef = ref([]);

    const that = reactive({
      Edit,
      listLoading: false,
      returnDataInfo: {},
      list: [
        {
          name: "基本信息",
          isEditBut: false,
          fieldName: "BasicInfo",
          children: [
            {reaName: "站点编码", fieldName: "id", filterName: "", unit: ""},
            {reaName: "站点名称", fieldName: "siteName", filterName: "", unit: ""},
            {reaName: "站点状态", fieldName: "siteStatus", filterName: "siteStatus", unit: ""},
            {reaName: "站点描述", fieldName: "siteDescribe", filterName: "", unit: ""},
          ]
        },
        {
          name: "扩展信息",
          isEditBut: true,
          fieldName: "ExtendedInfo",
          children: []
        },
        {
          name: "实景图",
          isEditBut: true,
          fieldName: "RealisticView",
        }
      ],

      realisticViewVisible: false,
      editSiteInfoVisible: false,
    });

    // 根据站点id查询基本详情数据
    const querySiteBasicInfoById = ()=>{
      that.listLoading = true;
      findSiteInfoById({ id: props.siteId }).then(res=>{
        let returnDataInfo = res.data ? res.data : {};

        // 处理扩展属性返回值
        if(returnDataInfo.siteReadwriteObject){
          returnDataInfo.siteReadwriteObject = JSON.parse(returnDataInfo.siteReadwriteObject);
          returnDataInfo = Object.assign({},returnDataInfo,returnDataInfo.siteReadwriteObject);
        }

        // 扩展属性处理
        if(returnDataInfo.siteReaList && returnDataInfo.siteReaList.length){
          for(let i = 0;i < returnDataInfo.siteReaList.length;i++){
            if(returnDataInfo.siteReaList[i].extraValue){
              returnDataInfo.siteReaList[i].extraValue = JSON.parse(returnDataInfo.siteReaList[i].extraValue);
            }

            // 位置信息处理
            if(returnDataInfo.siteReaList[i].reaType === 4){
              let activeDataInfo = JSON.parse(JSON.stringify(returnDataInfo.siteReaList[i]));
              returnDataInfo.siteReaList[i].fieldName = returnDataInfo.siteReaList[i].fieldName + ".longitudeAndLatitude";

              let active_obj = JSON.parse(JSON.stringify(activeDataInfo));
              returnDataInfo.siteReaList.splice(i + 1,0,{...active_obj,reaName: activeDataInfo.reaName + "-具体地址",fieldName: activeDataInfo.fieldName + ".address"});
              returnDataInfo.siteReaList.splice(i + 1,0,{...active_obj,reaName: activeDataInfo.reaName + "-所在城市",fieldName: activeDataInfo.fieldName + ".city"});
              i+=2;
            }
          }
          let findIndex = that.list.findIndex(item=> item.fieldName === "ExtendedInfo");
          that.list[findIndex].children = JSON.parse(JSON.stringify(returnDataInfo.siteReaList));
        }

        // 站点图片处理
        if(returnDataInfo.imagePath){
          returnDataInfo.imagePath = returnDataInfo.imagePath.split(",");
        }

        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const clickEditButton = (fieldName)=>{
      if(fieldName === "ExtendedInfo"){
        that.editSiteInfoVisible = true;
      }

      if(fieldName === "RealisticView"){
        that.realisticViewVisible = true;
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

    const clickPreviewBut = (index)=>{
      viewerRef.value[index].clickLookImage();
    };

    const watchSiteId = watch(()=>props.siteId,(newSiteId)=>{
      querySiteBasicInfoById();
    },{ deep:true });

    onMounted(()=>{
      querySiteBasicInfoById();
    });

    return {...toRefs(that),watchSiteId,querySiteBasicInfoById,clickEditButton,viewerRef,clickPreviewBut,extendedAttributeFun};
  }
});
</script>

<style lang="scss" scoped>
.content_body{
  height: 100%;
  padding: 16px;
  overflow-y: auto;

  .el-row{
    width: 100%;
    margin-bottom: 11px;

    .info_li {
      display: flex;
      align-items: center;
      margin-bottom: 24px;

      .flex_li_left {
        color: #d3ecfb;
        font-size: 14px;
      }

      .flex_li_right {
        color: #d3ecfb;
        font-size: 14px;
        display: flex;
        align-items: center;
      }
    }
  }

  .realisticView{
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .image_item_li{
      width: 100px;
      height: 100px;
      overflow: hidden;
      margin-right: 12px;
      border-radius: 8px;
      box-sizing: border-box;
      border: 1px solid #EAEEF1;
      position: relative;

      .preview{
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        border-radius: 8px;
        background: rgba(18, 28, 63, .5);
        transition: all .28s;
        display: none;
      }

      &:hover{
        cursor: pointer;
        .preview{
          display: flex;
        }
      }

      &:last-child{
        margin-right: 0;
      }
    }
  }
}
</style>