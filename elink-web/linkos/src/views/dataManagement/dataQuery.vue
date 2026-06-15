<template>
  <div class="app-container">
    <HandleMenus :isShowHeader="false" :handleMenuArray="handleMenuArray" @handleMenuEvent="handleMenuEvent"></HandleMenus>

    <div class="app-container-right" v-resize="setTableMaxHeight">
      <div class="header-form" ref="headerFormRef">
        <el-form :model="formInline" inline>
          <el-form-item label="时间：">
            <el-date-picker v-model="formInline.pickerDate" type="datetimerange" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm"
                            @visibleChange="pickerOptions.visibleChange" @calendar-change="pickerOptions.calendarChange" :shortcuts="pickerOptions.shortcuts"
                            :disabled-date="pickerOptions.disabledDate" :disabled-hours="pickerOptions.disabledHours" :disabled-minutes="pickerOptions.disabledMinutes"
                            range-separator="~" start-placeholder="开始时间" end-placeholder="结束时间"  style="width: 290px;"/>
          </el-form-item>
          <el-form-item label="数据：">
            <el-cascader v-model="formInline.dataSelect" :options="mappingArray" :props="defaultProps" clearable placeholder="请选择" node-key="id"
                         :render-after-expand="false" :max-collapse-tags="1" collapse-tags collapse-tags-tooltip filterable style="width: 280px"
                         @change="dataSelectChangeFun"></el-cascader>
          </el-form-item>
          <el-form-item label="数据索引：">
            <el-input-number v-model="formInline.index" :min="0" :disabled="isIndexDisabled" controls-position="right"></el-input-number>
          </el-form-item>
          <el-form-item>
            <el-button class="whiteFontButtons" :icon="Search" @click="listArray">查询</el-button>
            <el-button class="blackFontButtons" :icon="RefreshRight" @click="resetForm">重置</el-button>
          </el-form-item>
          <el-form-item>
            <el-button class="blackFontButtons" :icon="Folder" @click="clickExportBut">导出</el-button>
            <div style="margin-left: 12px">
              <TabBackground v-model:tabsIndex="componentName" :tabsArray="tabsArray"></TabBackground>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <div class="tableContent">
        <template v-if="dataInfoList && dataInfoList.length">
          <component ref="componentRef" :is="componentName" :xaxisList="xaxisList" :dataInfoList="dataInfoList" v-model:listLoading="listLoading"
                     :tableMaxHeight="tableMaxHeight"></component>
        </template>
        <div class="null_data" v-else><null-data></null-data></div>
      </div>

    </div>
  </div>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {setTreeData, treeToArray} from "@/utils";
import {findDataQueryList} from "@/api/dataManagement/dataQuery";
import {Search,RefreshRight,Folder} from '@element-plus/icons-vue';
import {getSiteDeviceTreeList} from "@/api/deviceCenter/deviceList";
import {onActivated, reactive, ref, toRefs, defineComponent} from "vue";
import {DataQueryCharts, DataQueryTable} from "@/views/dataManagement/components";
import {getNowDateAll, getNowDateMin, pickerOptionsGthanAcTime} from "@/utils/dateTime";
import {findComputeNodeAndFunctionListById, findSiteDeviceDataById} from "@/api/dataManagement/nodeManagement";

export default defineComponent({
  name: "dataQuery",
  components: { DataQueryCharts, DataQueryTable },
  props:{
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup(props) {
    const componentRef = ref(null);

    const that = reactive({
      Search,
      Folder,
      RefreshRight,
      oldFormInline: {},
      isIndexDisabled: true,
      componentName: "DataQueryCharts",
      pickerOptions: pickerOptionsGthanAcTime(),
      formInline:{ pickerDate: [getNowDateMin(-1440),getNowDateMin(0)] },
      tabsArray: [{ id: "DataQueryCharts", name: "图表" }, { id: "DataQueryTable", name: "数据" }],

      xaxisList: [],
      dataInfoList: [],
      listLoading: false,
      tableMaxHeight: 320,
      exampleType: 1, //实例类型 1-设备类型 2-站点类型
      activeSiteId: "", // 当前站点id
      activeRecordId: "",  //记录id
      activeRecordName: "",
      handleMenuArray: [],

      oldMappingArray:[{ id: "gnd",name:"功能点",children:[] }, { id: "jd",name:"计算节点",children:[] }], // 模型列表  映射
      mappingArray:[{ id: "gnd",name:"功能点",children:[] }, { id: "jd",name:"计算节点",children:[] }], // 模型列表  映射
      defaultProps: {
        lazy: true,
        value: "id",
        label: 'name',
        multiple: true, // 多选
        children: 'children',
        lazyLoad: (node, resolve)=> casCaDerLazyLoad(node, resolve)
      },
    })

    // 获取站点设备树形结构
    const querySiteDeviceTreeList = ()=>{
      getSiteDeviceTreeList({ type: 0,timer: new Date() }).then(res=>{
        let handleMenuArray = res.data ? res.data : [];
        handleMenuArray.forEach(element => {
          element.deviceName = element.name;

          if (element.type === 1) {
            element.iconName = "icon-zhandian";
          } else {
            let typeDetailText = "--";
            if(element.typeDetail === 1) typeDetailText = "直连";
            if(element.typeDetail === 2) typeDetailText = "网关";
            if(element.typeDetail === 3) typeDetailText = "子设备";
            element.name = `<span class="textTwo flex-all">${ element.name }</span><span class='typeDetailClass'>${ typeDetailText }</span>`;
          }
        });

        that.handleMenuArray = setTreeData(handleMenuArray);
      })
    }

    const handleMenuEvent = (data) => {
      // console.log(data);
      if(data.menuType === "clickTreeNode"){
        // 再次重新进入缓存页面，
        if(that.activeRecordId !== data.id){
          that.exampleType = data.type !== 1 ? 1 : 2; // 实例类型 1-设备类型 2-站点类型
          that.activeRecordName = data.deviceName;
          that.activeRecordId = data.id;
          that.mappingArray = [];
          resetFormInlineFun();
        }
      }
    }

    const resetFormInlineFun = ()=>{
      that.xaxisList = [];
      that.dataInfoList = [];
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      that.mappingArray = JSON.parse(JSON.stringify(that.oldMappingArray));
    }

    const listArray = ()=>{
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      let array = JSON.parse(JSON.stringify(treeToArray(that.mappingArray)));

      if(!formInline.dataSelect || !formInline.dataSelect.length){
        ElMessage({ type: "error", message: "请选择查询数据点", showClose: true });
        return
      }

      that.listLoading = true;
      if(formInline.pickerDate){
        formInline.endTime = formInline.pickerDate[1];
        formInline.startTime = formInline.pickerDate[0];
        delete formInline.pickerDate
      }

      formInline.nodeIds = []; // 多个计算节点唯一id
      formInline.functionLogos = []; // 多个功能点标识
      formInline.arrayFunctionLogos = []; // 多个数组类型功能点标识
      if(!formInline.index)formInline.index = 0; // 索引默认为0

      // 处理开始时间、结束时间带上时分秒
      formInline.endTime = getNowDateAll(formInline.endTime,{ isSs: false });
      formInline.startTime = getNowDateAll(formInline.startTime,{ isSs: false });

      for(let i = 0;i < formInline.dataSelect.length;i++){
        let dataLogoName = formInline.dataSelect[i][0];
        let dataId = formInline.dataSelect[i][formInline.dataSelect[i].length - 1];
        if(dataLogoName === "jd")formInline.nodeIds.push(dataId);
        if(dataLogoName === "gnd"){
          let findItem = array.find(item=> item.id === dataId);
          if(findItem.dataType === 8){
            formInline.arrayFunctionLogos.push(dataId);
          } else {
            formInline.functionLogos.push(dataId);
          }
        }
      }

      formInline.nodeIds = formInline.nodeIds.join(',');
      formInline.functionLogos = formInline.functionLogos.join(',');
      formInline.arrayFunctionLogos = formInline.arrayFunctionLogos.join(',');

      findDataQueryList({ deviceId: that.activeRecordId,...formInline, }).then(res=>{
        let returnData = res.data ? res.data : {};
        that.xaxisList = returnData.xaxisList;
        that.dataInfoList = returnData.dataInfoList;
        // that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    // 重置
    const resetForm = ()=>{
      that.isIndexDisabled = true;
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      ElMessage({ type: "success", message: "重置成功！", showClose: true });
    }

    // 点击导出按钮
    const clickExportBut = ()=>{
      if(!that.dataInfoList || !that.dataInfoList.length) {
        ElMessage({ type: "error", message: "未查询到数据，请重新查询！", showClose: true });
        return
      }
      let fileName = `${ that.activeRecordName }  ${ that.formInline.pickerDate.join("~") } 数据`;
      componentRef.value.exportSearchDataFun(fileName);
    }

    // 动态获取数据来源数据
    const casCaDerLazyLoad = (node,resolve)=>{
      const { level,data,pathValues } = node;

      let searchType = "jd",levelNum = 1;
      if(pathValues[0] === "gnd")searchType = "gnd";

      if(that.exampleType === 2 && level === 1){
        // 根据站点id查询下面设备列表
        findSiteDeviceDataById({ siteId: that.activeRecordId,timer: new Date() }).then(res=>{
          let deviceArray = res.data ? res.data : [];
          deviceArray.unshift({id: that.activeRecordId,deviceName: that.activeRecordName});
          for (let i = 0;i < deviceArray.length;i++) deviceArray[i].name = deviceArray[i].deviceName;
          resolve(deviceArray);
        })
      }

      // 层级为第二级 （站点数据的第三层） 或者  为设备时（设备第二层）
      if(level === 2 || that.exampleType === 1){
        let requestData = { deviceId: that.activeRecordId };
        if(that.exampleType === 2)requestData.deviceId = data.id;

        findComputeNodeAndFunctionListById({ ...requestData,timer: new Date() },searchType).then(res=>{
          let newComputeNodeOrMeasurementList = [];
          let computeNodeOrMeasurementList = res.data ? res.data : [];
          for(let i = 0;i < computeNodeOrMeasurementList.length;i++){
            computeNodeOrMeasurementList[i].leaf = level >= levelNum;
            computeNodeOrMeasurementList[i].newId = computeNodeOrMeasurementList[i].id;
            computeNodeOrMeasurementList[i].name = computeNodeOrMeasurementList[i].functionName || computeNodeOrMeasurementList[i].nodeName;
            if(searchType === "gnd"){
              computeNodeOrMeasurementList[i].id = computeNodeOrMeasurementList[i].functionLogo;
              if(computeNodeOrMeasurementList[i].dataType !== 5 && computeNodeOrMeasurementList[i].dataType !== 6 && computeNodeOrMeasurementList[i].dataType !== 9){
                newComputeNodeOrMeasurementList.push(computeNodeOrMeasurementList[i]);
              }
            }
            if(searchType === "jd"){
              // computeNodeOrMeasurementList[i].id = computeNodeOrMeasurementList[i].storageId;
              newComputeNodeOrMeasurementList.push(computeNodeOrMeasurementList[i]);
            }
          }
          resolve(newComputeNodeOrMeasurementList);
        })
      }
    }

    // 查询数据索引是否可填写
    const dataSelectChangeFun = ()=>{
      let isIndexDisabled = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      let array = JSON.parse(JSON.stringify(treeToArray(that.mappingArray)));
      // console.log(array);
      for(let i = 0;i < formInline.dataSelect.length;i++){
        let dataId = formInline.dataSelect[i][formInline.dataSelect[i].length - 1];
        let findItem = array.find(item=> item.id === dataId);
        if(findItem?.dataType === 8) isIndexDisabled = false;
      }
      that.isIndexDisabled = isIndexDisabled;
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight;
    }

    onActivated(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteDeviceTreeList();
    })

    return {...toRefs(that), querySiteDeviceTreeList, handleMenuEvent, listArray, resetForm, componentRef, casCaDerLazyLoad, resetFormInlineFun,headerFormRef,
      setTableMaxHeight,dataSelectChangeFun,clickExportBut}
  }
})
</script>

<style scoped lang="scss">
:deep(.el-cascader__tags){
  flex-wrap: nowrap;
}
</style>
