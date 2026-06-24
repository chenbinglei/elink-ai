<template>
  <div class="app-container">
    <div class="content_body">
      <PreviewCanvasEditor ref="reportTemplateCanvasRef" v-model:listLoading="overlayShow"></PreviewCanvasEditor>
    </div>
    <div class="bottom_but">
      <div class="scale_buttons">
        <div class="scaleMinus scale_but" @click="clickScaleBut('scaleMinus')">-</div>
        <div class="scaleAdd scale_but" @click="clickScaleBut('scaleAdd')">+</div>
      </div>
      <div class="button" @click.stop="generatePdfFile">下载报表</div>
    </div>

    <van-overlay :show="overlayShow">
      <van-loading vertical>加载中...</van-loading>
    </van-overlay>

  </div>
</template>

<script>
import {useRoute} from "vue-router";
import $filters from "@/common/filters";
import CalcEval from "@/utils/calculator";
import {onMounted, reactive, ref, toRefs} from "vue";
import {PreviewCanvasEditor} from '@/views/ies/components';
import {findReportDataById,customRequestPort} from "@/api/ies";

export default {
  name: "reportView",
  components: {PreviewCanvasEditor},
  setup() {
    const route = useRoute();
    const reportTemplateCanvasRef = ref(null);

    const that = reactive({
      data: { }, // 请求接口数据
      storeData: {},
      staticAllVar: [], //pdf编辑器绑定的所有变量数据
      tableBindVarList: [], //pdf编辑器绑定的 表格、图表数据
      overlayShow: false,
      htmlReg: /<[^>]+>/gim,
      canvasEditorData: null, // pdf编辑器数据
      customRequestIndex: 0,
      singleTableMaxLength: 14, //单个表格最大长度为10
    })

    const queryReportDataById = () => {
      that.overlayShow = true;
      findReportDataById(that.data).then(res => {

        if(res.data){
          that.storeData = res.data.storeData ? JSON.parse(res.data.storeData) : {};
          if(res.data.fileData){
            let templateCanvas = res.data.fileData ? JSON.parse(res.data.fileData) : {};
            // console.log(templateCanvas);
            that.staticAllVar = templateCanvas.staticAllVar;
            that.canvasEditorData = templateCanvas.graphFileStr;
            that.tableBindVarList = templateCanvas.tableBindVarList;
          }
        }
        // 字段中需要被请求的数据
        customRequestListFun();

      }).catch(() => {
        that.overlayShow = false;
      })
    }

    // 字段中需要被请求的数据
    const customRequestListFun = () => {
      // const { header,main,footer } = that.canvasEditorData.data;
      if(that.storeData && that.storeData.length){
        if(that.storeData[that.customRequestIndex].fieldValue && that.storeData[that.customRequestIndex].fieldValue.dataDesc){
          let dataDesc = eval('(' + that.storeData[that.customRequestIndex].fieldValue.dataDesc + ')');
          // console.log(dataDesc);
          if (dataDesc.labelType === "select" && dataDesc.dataSource === 2) {
            dataDesc.dataSource = 1; // 重置为 1
            let data = JSON.parse(JSON.stringify(that.data));
            delete data.id;
            customRequestPort({ fieldLogo: that.storeData[that.customRequestIndex].fieldLogo,timer: new Date(),...data }, dataDesc.selectValue).then(result => {
              dataDesc.selectValue = result.data ? result.data : [];
              that.storeData[that.customRequestIndex].fieldValue.dataDesc = JSON.stringify(dataDesc);
              makeTheNextCallFun();
            }).catch(() => {
              makeTheNextCallFun();
            })
          } else {
            makeTheNextCallFun();
          }
        } else {
          makeTheNextCallFun();
        }
      } else {
        // 根据返回数据修改变量数据
        updateCanvasEditorDataFun();
      }
    }

    // 缩放
    const clickScaleBut = (operateType)=>{
      reportTemplateCanvasRef.value.setPageScaleFun(operateType);
    }

    // 回调函数
    const makeTheNextCallFun = ()=>{
      if ((that.customRequestIndex + 1) === that.storeData.length){
        updateCanvasEditorDataFun();
        return
      }
      that.customRequestIndex+=1;
      customRequestListFun();
    }

    // 根据返回数据修改变量数据
    const updateCanvasEditorDataFun = ()=>{
      // console.log(that.storeData);
      // console.log(that.canvasEditorData);
      const {data} = that.canvasEditorData;
      for (let key in data) {
        if (data[key] && data[key].length) {
          for (let i = 0; i < data[key].length; i++) {
            // console.log(data[key][i]);

            // 普通数据设置
            if (data[key][i].type === "control") {
              data[key][i].control.prefix = "\u200c"; // 去掉展示的大括号
              data[key][i].control.postfix = "\u200c"; // 去掉展示的大括号
              const {conceptId, dataType, formula, unit} = data[key][i].control;
              let valueText = queryVariableValueFun({conceptId, dataType, formula, unit});
              data[key][i].control.value = [{value: valueText}];
            }

            // 静态表格数据
            if (data[key][i].type === "table") {

              // 普通表格
              if (!data[key][i].value) {
                if (data[key][i].trList && data[key][i].trList.length) {
                  for (let tr = 0; tr < data[key][i].trList.length; tr++) {
                    if (data[key][i].trList[tr].tdList && data[key][i].trList[tr].tdList.length) {
                      for (let td = 0; td < data[key][i].trList[tr].tdList.length; td++) {
                        if (data[key][i].trList[tr].tdList[td].value && data[key][i].trList[tr].tdList[td].value.length) {
                          for (let vl = 0; vl < data[key][i].trList[tr].tdList[td].value.length; vl++) {
                            if (data[key][i].trList[tr].tdList[td].value[vl].type === "control") {
                              data[key][i].trList[tr].tdList[td].value[vl].control.prefix = "\u200c"; // 去掉展示的大括号
                              data[key][i].trList[tr].tdList[td].value[vl].control.postfix = "\u200c"; // 去掉展示的大括号
                              const {conceptId, dataType, formula, unit} = data[key][i].trList[tr].tdList[td].value[vl].control;
                              let valueText = queryVariableValueFun({conceptId, dataType, formula, unit});
                              // console.log(valueText);
                              data[key][i].trList[tr].tdList[td].value[vl].control.value = [{value: valueText}];
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }

              try {
                // 绑定数据的表格
                if (data[key][i].value) {
                  // console.log(data[key][i]);
                  let trListArray = [];
                  // console.log(that.tableBindVarList);
                  let findItem = that.tableBindVarList.find(item => item.id === data[key][i].value);
                  // 获取当前表格展示最大展示多少条数据
                  let tableMaxLength = findItem.tableMaxLength || 100;
                  // console.log(findItem);

                  let rowsTotalNum = 0; // 一共有多少条数据
                  for (let k = 0; k < findItem.bindListArray.length; k++) {
                    // 查找对应的数据
                    let bindData = {fieldLogo: findItem.bindListArray[k], countType: findItem.countType, interval: findItem.countPeriod + findItem.countPeriodType};
                    let activePelData = that.storeData.find(item => JSON.stringify(bindData) === JSON.stringify({
                      fieldLogo: item.fieldLogo, countType: item.countType, interval: item.interval
                    }));

                    if (activePelData) {
                      // 设置表格总数据量
                      if (activePelData.fieldValue && activePelData.fieldValue.length || !rowsTotalNum) {
                        rowsTotalNum = activePelData.fieldValue.length > tableMaxLength ? tableMaxLength : activePelData.fieldValue.length;
                      }
                    }
                  }
                  // console.log(rowsTotalNum);
                  // 默认
                  if (findItem && !findItem.direction) {
                    // 获取表格的头部（ 对象格式）
                    let gaugeOutfit = JSON.parse(JSON.stringify(data[key][i].trList[0]));
                    // console.log(gaugeOutfit);

                    for (let k = 0; k < findItem.bindListArray.length; k++) {
                      // 查找对应的数据
                      let bindData = {fieldLogo: findItem.bindListArray[k], countType: findItem.countType, interval: findItem.countPeriod + findItem.countPeriodType};
                      let activePelData = that.storeData.find(item => JSON.stringify(bindData) === JSON.stringify({
                        fieldLogo: item.fieldLogo, countType: item.countType, interval: item.interval
                      }));

                      if (activePelData) {
                        // console.log(activePelData);
                        for (let index = 0; index < rowsTotalNum; index++) {
                          let activeTrObj = JSON.parse(JSON.stringify(trListArray[index] ? trListArray[index] : { height: 38, tdList: [] }));

                          if (k === 0) {
                            // 增加索引
                            if (findItem.whetherOrNot) activeTrObj.tdList.push({ colspan: 1, rowspan: 1, value: [{value: index + 1}] });

                            activeTrObj.tdList.push({
                              colspan: 1, rowspan: 1,
                              value: [{ value: activePelData.fieldValue[rowsTotalNum - index - 1].dateTime }]
                            });
                          }

                          let valueText = '';
                          if(activePelData && activePelData.fieldValue)valueText = activePelData.fieldValue[rowsTotalNum - index - 1].value;
                          valueText = valueText ? valueText : '--';
                          activeTrObj.tdList.push({ colspan: 1, rowspan: 1, value: [{value: valueText + activePelData.fieldUnit}] });
                          trListArray[index] = activeTrObj;
                        }
                      }
                    }
                    // console.log(trListArray);

                    // 列表无数据展示
                    if (!trListArray.length) {
                      console.log('列表无数据');
                      trListArray.push({ height: 42, tdList: [{ colspan: gaugeOutfit.tdList.length, rowspan: 1, value: [{value: "暂无数据", rowFlex: RowFlex.CENTER}] }] });
                      data[key][i].trList = [gaugeOutfit, ...trListArray];
                    }

                    // 判断表格是否需要分表格
                    if(trListArray.length && trListArray.length > that.singleTableMaxLength){
                      // 获取列表需要分为几个表格
                      let shareTableNum = Math.ceil(trListArray.length / that.singleTableMaxLength);
                      // console.log(shareTableNum);
                      // console.log(trListArray);
                      // 把当前表格分为几个表格进行渲染
                      for(let share = 1;share <= shareTableNum;share++){
                        // console.log(share);
                        let start = (share - 1) * that.singleTableMaxLength;
                        let interceptArray = trListArray.slice(start,that.singleTableMaxLength + start);
                        // console.log(interceptArray);

                        if(share === 1){
                          data[key][i].trList = [gaugeOutfit, ...interceptArray];
                        } else {
                          // 克隆当前表格
                          let cloneActiveTable = JSON.parse(JSON.stringify(data[key][i]));
                          // console.log(cloneActiveTable);
                          // console.log(interceptArray);
                          cloneActiveTable.trList = interceptArray;
                          // 往当前表格后插入数据
                          data[key].splice(i+1,0,cloneActiveTable);
                          i++
                        }
                      }
                    } else {
                      // 不需要分表格
                      data[key][i].trList = [gaugeOutfit, ...trListArray];
                    }
                  }

                  // 纵向表格
                  if (findItem && findItem.direction && rowsTotalNum > 0) {
                    // console.log(findItem);
                    // console.log(data[key][i]);
                    // console.log(rowsTotalNum);

                    let colgroupArray = [];
                    // console.log(rowsTotalNum);
                    let tabMaxwidtd = data[key][i].width / (rowsTotalNum + 1);

                    if (data[key][i].trList && data[key][i].trList.length) {
                      for (let col = 1; col < data[key][i].trList.length; col++) {

                        if (col === 1) colgroupArray.push({width: tabMaxwidtd});
                        // 查找对应的数据
                        let bindData = {fieldLogo: findItem.bindListArray[col - 1], countType: findItem.countType, interval: findItem.countPeriod + findItem.countPeriodType};
                        let activePelData = that.storeData.find(item => JSON.stringify(bindData) === JSON.stringify({
                          fieldLogo: item.fieldLogo, countType: item.countType, interval: item.interval
                        }));

                        for (let row = 0; row < rowsTotalNum; row++) {
                          if (activePelData) {

                            let valueText = "";
                            if(activePelData && activePelData.fieldValue)valueText = activePelData.fieldValue[rowsTotalNum - row - 1].value;
                            valueText = valueText ? valueText : "--";
                            data[key][i].trList[col].tdList[row + 1] = {
                              colspan: 1,
                              rowspan: 1,
                              value: [{value: valueText + activePelData.fieldUnit }]
                            }
                          } else {
                            data[key][i].trList[col].tdList[row + 1] = {colspan: 1, rowspan: 1, value: [{value: '--'}]};
                          }

                          // 往第一列添加日期数据
                          if (col === 1) {
                            let valueDateTime = '--';
                            colgroupArray.push({ width: tabMaxwidtd });
                            if(activePelData && activePelData.fieldValue)valueDateTime = activePelData.fieldValue[rowsTotalNum - row - 1].dateTime;
                            data[key][i].trList[0].tdList[row + 1] = {
                              colspan: 1, rowspan: 1, value: [{ value: valueDateTime }]
                            }
                          }
                        }

                      }
                    }

                    if (rowsTotalNum > 0) data[key][i].colgroup = colgroupArray;
                    // console.log(data[key][i]);
                  }
                }
              } catch (e) {
                console.log(e);
              }
            }
            //以上为表格加载数据设置
          }
        }
      }
      // console.log(that.canvasEditorData);
      reportTemplateCanvasRef.value.setCanvasEditorHTML(that.canvasEditorData);
      that.overlayShow = false;
    }

    // 根据对应的 conceptId 查找接口返回值
    const queryVariableValueFun = (obj)=>{
      let valueText = "--";
      // console.log(that.storeData);
      // 默认当前变量的图元
      if(obj.dataType !== "countData"){
        let findItem = that.staticAllVar.find(item => item.id === obj.conceptId);
        // console.log(findItem);
        if(findItem){

          // 查找对应的数据
          let bindData = { fieldLogo: findItem.fieldLogo,countType: findItem.countType,interval: findItem.countPeriod + findItem.countPeriodType };
          let activePelData = that.storeData.find(item => JSON.stringify(bindData) === JSON.stringify({
            fieldLogo: item.fieldLogo,countType:item.countType,interval:item.interval
          }));

          if(activePelData){
            //判断 fieldValue 返回数据类型 根据类型进行渲染
            // console.log(typeof activePelData.fieldValue);
            if(typeof activePelData.fieldValue === "object"){
              // 数组类型
              valueText = $filters.moreData(JSON.stringify(activePelData.fieldValue));
              // console.log(activePelData.fieldValue);

              // 对象类型
              if(!Array.isArray(activePelData.fieldValue)){
                valueText = $filters.moreData(activePelData.fieldValue.dataValue);  // 默认展示数据
                // 当前值如果有描述
                if (activePelData.fieldValue.dataDesc) {
                  let dataValue = "";
                  let dataDesc = eval('(' + activePelData.fieldValue.dataDesc + ')');
                  // console.log(dataDesc);

                  try {
                    dataValue = JSON.parse(activePelData.fieldValue.dataValue);
                  } catch (e) {
                    dataValue = activePelData.fieldValue.dataValue;
                  }

                  if (dataDesc.labelType === "lable"){
                    if(dataValue) valueText = dataValue.join(',');
                  }

                  if (dataDesc.labelType === "location"){
                    if(dataValue) valueText = dataValue.address;
                  }

                  if (dataDesc.labelType === "select") {
                    // console.log(dataDesc)

                    // 静态数据
                    if (dataDesc.dataSource === 1) {
                      // console.log(dataDesc);
                      if (dataDesc.selectValue && dataDesc.selectValue.length) {
                        let valueObj = dataDesc.selectValue.find(item => String(item.id) === String(dataValue));
                        if (valueObj) valueText = valueObj.name;
                      }
                    }
                  }

                  if (dataDesc.labelType === "dateTime") {
                    if (dataValue) {
                      valueText = dataValue; // 默认直接赋值

                      //取消额外属性
                      if(!dataDesc.additionalAttr){
                        let weekTime = '';

                        // 关联星期
                        if (dataDesc.associatedWeek){
                          weekTime = `（${setWeekTime(dataValue.weekTime.sort())}）`;
                        }

                        // 年月日
                        if (dataDesc.dateTimeType === 1) {
                          valueText = dataValue.dayAndYearTime + weekTime;
                        }

                        // 时分
                        if (dataDesc.dateTimeType === 2) {
                          valueText = (dataValue.hourAndMinuteTime ? dataValue.hourAndMinuteTime.join('~') : '无限制' ) + weekTime;
                        }
                      }
                    }
                  }
                }
              }
            }

            if(typeof activePelData.fieldValue === "number" || typeof activePelData.fieldValue === "string"){
              valueText = $filters.moreData(activePelData.fieldValue);  // 默认展示数据
            }

            if(activePelData.fieldUnit)valueText = valueText + activePelData.fieldUnit;
          }
        }
      }

      // 需要计算的变量图元
      if(obj.dataType === "countData"){

        // 首先对公式进行解析
        const dom = document.createElement('div');
        dom.innerHTML = obj.formula;
        const varDomArray = dom.querySelectorAll(".clickable-text");
        // console.log(varDomArray);

        for(let j = 0;j < varDomArray.length;j++){
          let { id } = varDomArray[j].dataset;
          // console.log(id);

          let findItem = that.staticAllVar.find(item => item.id === Number(id));
          if(findItem){
            let bindData = { fieldLogo: findItem.fieldLogo, countType: findItem.countType, interval: findItem.countPeriod + findItem.countPeriodType };
            let activePelData = that.storeData.find(item => JSON.stringify(bindData) === JSON.stringify({
              fieldLogo: item.fieldLogo,countType:item.countType,interval:item.interval
            }));
            // 找到对应的值 进行修改
            if(activePelData)varDomArray[j].innerHTML = activePelData.fieldValue;
          }
        }

        // console.log(dom.innerHTML);
        const formula = dom.innerHTML.replace(that.htmlReg,""); // 把所有的标签都替换成空字符 组成公式
        // 进行数值计算
        // console.log(formula);
        valueText = String(CalcEval.clacAimStr(formula));
        if(obj.unit)valueText = valueText + obj.unit;
      }

      return valueText
    }

    const setWeekTime = (array) => {
      if (array.length === 7) return '周一至周日';
      let weekTime = [];
      for (let i = 0; i < array.length; i++) {
        let str = '';
        if (String(array[i]) === "1") str = '星期一';
        if (String(array[i]) === "2") str = '星期二';
        if (String(array[i]) === "3") str = '星期三';
        if (String(array[i]) === "4") str = '星期四';
        if (String(array[i]) === "5") str = '星期五';
        if (String(array[i]) === "6") str = '星期六';
        if (String(array[i]) === "7") str = '星期日';
        weekTime.push(str)
      }
      return weekTime.join(',')
    }

    // 下载报表
    const generatePdfFile = ()=>{
      that.overlayShow = true;
      reportTemplateCanvasRef.value.downloadPdfFileFun({ pdfName: that.data.graphName });
    }

    onMounted(() => {
      if(route.query.data){
        that.data = JSON.parse(decodeURIComponent(route.query.data));
        queryReportDataById();
      }
      document.title = "报表详情";
    })

    return { ...toRefs(that),queryReportDataById,setWeekTime,reportTemplateCanvasRef,customRequestListFun,makeTheNextCallFun,generatePdfFile,
      updateCanvasEditorDataFun,queryVariableValueFun,clickScaleBut}
  }
}
</script>

<style scoped lang="scss">
.app-container{
  font-size: 0.18rem;
  position: relative;

  .content_body{
    width: 100%;
    height: 100%;
  }

  .bottom_but{
    width: 100%;
    position: absolute;
    bottom: 0.24rem;
    left: 0;
    z-index: 10000;
    padding: 0 .32rem;
    box-sizing: border-box;

    .scale_buttons{
      margin-bottom: 0.12rem;
      display: flex;
      flex-direction: column;
      align-items: flex-end;

      .scale_but{
        width: 0.26rem;
        height: 0.26rem;
        font-size: 0.14rem;
        text-align: center;
        line-height: 0.26rem;
        border: 1px solid #ccc;
        border-radius: 0.02rem;
        margin-bottom: 0.04rem;

        &:last-child{
          margin-bottom: 0;
        }
      }
    }

    .button{
      width: 100%;
      height: .4rem;
      color: #FFFFFF;
      font-size: .14rem;
      background: #107BE9;
      border-radius: .06rem;
      text-align: center;
      line-height: .4rem;
    }
  }
}
</style>
