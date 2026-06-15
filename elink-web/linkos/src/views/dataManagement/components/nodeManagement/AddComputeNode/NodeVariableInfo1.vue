<template>
  <div ref="tableCenterRef" v-resize="setTableMaxHeight" class="nodeVariableInfo">
    <el-table class="noSelect" :data="varTableList" :max-height="tableMaxHeight" border stripe :header-cell-class-name="headerClass">
      <el-table-column align="center" label="序号" type="index" width="80"></el-table-column>
      <el-table-column align="center" label="名称">
        <template #default="{ row }">
          <div class="draggable_class flex ai-center">
            <el-input v-model="row.paramName" placeholder="请填写" type="text"></el-input>
            <div :class="{ dragClass: row.paramName }" :draggable="true" @dragend.stop="handleDragEnd($event, row.paramName)"
              @dragstart.stop="handleDragStart($event, row.paramName)"><span class="iconfont icon-tuodong"></span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="来源">
        <template #default="{ row,$index }">
          <el-cascader v-model="row.sourceId" :options="mappingArray" :props="defaultProps" clearable placeholder="请选择" filterable style="width: 100%;"
            @change="(data)=>changeMappingValue(data,$index)" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="最小值">
        <template #default="{ row }">
          <el-input-number v-model="row.minValue" controls-position="right" placeholder="请填写" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="最大值">
        <template #default="{ row }">
          <el-input-number v-model="row.maxValue" controls-position="right" placeholder="请填写" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="索引">
        <template #default="{ row }">
          <el-input-number v-model="row.indexNum" :disabled="!row.isIndexDisabled" controls-position="right" :min="0" :max="99" placeholder="请填写" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="缺省值">
        <template #default="{ row }">
          <el-select v-model="row.defaultValue" clearable placeholder="请选择">
            <el-option v-for="item in defaultValueArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column align="center" width="50">
        <template #header>
          <span class="iconfont icon-treeAdd pointer addIconBut" @click="clickTableAddBut"></span>
        </template>
        <template #default="{ row,$index }">
          <span class="iconfont icon-shanchuxiajimokuai pointer scIconBut" @click="clickTableDeleteBut($index)"></span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts">
import { treeToArray } from "@/utils";
import { ElMessage } from "element-plus";
import { someCharmap } from "@/utils/validate";
import { reactive, ref, toRefs, defineComponent } from "vue";
import { findComputeNodeAndFunctionListById, findSiteDeviceDataById } from "@/api/dataManagement/nodeManagement";

export default defineComponent({
  name: "NodeVariableInfo",
  props: {
    //实例类型 1-设备类型  2-站点类型
    exampleType: {
      type: [String, Number],
      default: ""
    },
    recordId: {
      type: [String, Number],
      default: ""
    },
  },
  setup (props) {
    const that = reactive({
      sourceArray: [],
      mappingArray: [], // 模型列表  映射
      varTableList: [],
      tableMaxHeight: 300,
      defaultValueArray: [{ id: 0, name: "0" }, { id: null, name: "NULL" }],
      defaultProps: { lazy: true, value: "id", label: 'name', children: 'children', lazyLoad: (node, resolve) => cascaderLazyLoad(node, resolve) },
    });

    // 动态获取数据来源数据
    const cascaderLazyLoad = (node,resolve)=>{
      const { level,data,pathValues } = node;
      let searchType = "jd",levelNum = 1;
      if(pathValues[0] === "gnd")searchType = "gnd";

      if(level === 0){
        let node_array = [{ id: "gnd",name:"功能点" }, { id: "jd",name:"计算节点" }];
        resolve(node_array);
      }

      if(props.exampleType === 2 && level === 1){
        // 根据站点id查询下面设备列表
        findSiteDeviceDataById({ siteId: props.recordId,level: level,timer: new Date() }).then(res=>{
          let deviceArray = res.data ? res.data : [];
          for (let i = 0;i < deviceArray.length;i++) deviceArray[i].name = deviceArray[i].deviceName;
          deviceArray.unshift({ name: "当前站点",id: props.recordId });
          resolve(deviceArray);
        })
      }

      // 层级为第二级 （站点数据的第三层） 或者  为设备时（设备第二层）
      if(level === 2 || (props.exampleType === 1 && level === 1)){
        let requestData = { deviceId: props.recordId };
        if(props.exampleType === 2) requestData.deviceId = data.id;
        findComputeNodeAndFunctionListById({ ...requestData,level: level,timer: new Date() },searchType).then(res=>{
          let computeNodeOrMeasurementList = res.data ? res.data : [];
          for(let i = 0;i < computeNodeOrMeasurementList.length;i++){
            computeNodeOrMeasurementList[i].leaf = level >= levelNum;
            computeNodeOrMeasurementList[i].newId = computeNodeOrMeasurementList[i].id;
            computeNodeOrMeasurementList[i].name = computeNodeOrMeasurementList[i].functionName || computeNodeOrMeasurementList[i].nodeName;
            if(searchType === "gnd")computeNodeOrMeasurementList[i].id = computeNodeOrMeasurementList[i].functionLogo;
            if(searchType === "jd")computeNodeOrMeasurementList[i].id = computeNodeOrMeasurementList[i].storageId;
          }
          resolve(computeNodeOrMeasurementList);
        })
      }
    }
    // 动态获取数据来源数据
    // const cascaderLazyLoad = (node, resolve) => {
    //   const { level, data, pathValues } = node;
      
    //   let searchType = "jd", levelNum = 1;
    //   if (pathValues && pathValues[0] === "gnd") searchType = "gnd";
    //   // 1. 一级节点：功能点/计算节点（直接赋值给mappingArray）
    //   if (level === 0) {
    //     let node_array = [{ id: "gnd", name: "功能点" }, { id: "jd", name: "计算节点" }];
    //     // 核心：一级节点直接存入mappingArray
    //     that.mappingArray = JSON.parse(JSON.stringify(node_array));

    //     resolve(node_array);

    //     return; // 终止后续逻辑
    //   }

    //   // 2. 二级节点：站点/设备列表（仅站点类型 exampleType=2）
    //   if (props.exampleType === 2 && level === 1) {
      
    //     findSiteDeviceDataById({ siteId: props.recordId, level: level, timer: new Date() }).then(res => {
    //       let deviceArray = res.data ? res.data : [];
    //       for (let i = 0; i < deviceArray.length; i++) {
    //         deviceArray[i].name = deviceArray[i].deviceName;
    //         // 给子节点标记父节点id，方便后续查找
    //         deviceArray[i].parentId = data.id;
    //       }
    //       deviceArray.unshift({ name: "当前站点", id: props.recordId, parentId: data.id });
          
    //       // 核心：找到一级父节点（gnd/jd），将设备列表挂载到children
    //       const parentNode = that.mappingArray.find(item => item.id === data.id);
    //       if (parentNode) {
    //         // 深拷贝避免引用问题
    //         parentNode.children = JSON.parse(JSON.stringify(deviceArray));
    //       }

    //       resolve(deviceArray);
    //     });
    //     return; // 终止后续逻辑
    //   }

    //   // 3. 三级节点：功能点/计算节点详情
    //   // 场景1：站点类型（exampleType=2）的第三级（level=2）
    //   // 场景2：设备类型（exampleType=1）的第二级（level=1）
    //   if (level === 2 || (props.exampleType === 1 && level === 1)) {
        
    //     let requestData = { deviceId: props.recordId };
    //     if (props.exampleType === 2) requestData.deviceId = data.id;

    //     findComputeNodeAndFunctionListById({ ...requestData, level: level, timer: new Date() }, searchType).then(res => {
    //       let computeNodeOrMeasurementList = res.data ? res.data : [];
    //       for (let i = 0; i < computeNodeOrMeasurementList.length; i++) {
    //         computeNodeOrMeasurementList[i].leaf = level >= levelNum;
    //         computeNodeOrMeasurementList[i].newId = computeNodeOrMeasurementList[i].id;
    //         computeNodeOrMeasurementList[i].name = computeNodeOrMeasurementList[i].functionName || computeNodeOrMeasurementList[i].nodeName;
    //         // 保留原始id和dataType（关键：用于判断数组类型）
    //         computeNodeOrMeasurementList[i].originId = computeNodeOrMeasurementList[i].id;
    //         computeNodeOrMeasurementList[i].dataType = computeNodeOrMeasurementList[i].dataType;
    //         if (searchType === "gnd") computeNodeOrMeasurementList[i].id = computeNodeOrMeasurementList[i].functionLogo;
    //         if (searchType === "jd") computeNodeOrMeasurementList[i].id = computeNodeOrMeasurementList[i].storageId;
    //         // 标记父节点id
    //         computeNodeOrMeasurementList[i].parentId = data.id;
    //       }

    //       // 核心：根据不同类型找到父节点，挂载子节点到children
    //       let parentNode = null;
    //       if (props.exampleType === 2) {
    //         // 站点类型：一级(gnd/jd) → 二级(设备) → 三级(功能点)
    //         // 先找一级节点
    //         const level1Node = that.mappingArray.find(item => item.id === pathValues[0]);
    //         if (level1Node && level1Node.children) {
    //           // 再找二级节点（设备）
    //           parentNode = level1Node.children.find(item => item.id === pathValues[1]);
    //         }
    //       } else {
    //         // 设备类型：一级(gnd/jd) → 二级(功能点)
    //         parentNode = that.mappingArray.find(item => item.id === pathValues[0]);
    //       }

    //       // 将功能点/计算节点列表挂载到父节点的children
    //       if (parentNode) {
    //         parentNode.children = JSON.parse(JSON.stringify(computeNodeOrMeasurementList));
    //       }

    //       resolve(computeNodeOrMeasurementList);
    //     });
    //     return; // 终止后续逻辑
    //   }

    //   // 兜底：若未匹配任何层级，返回空数组
    //   resolve([]);
    // };


    // 来源发生改变执行
    const changeMappingValue = (data, index) => {
      let isIndexDisabled = 0;
      let sourceCode = "", paramType = "";
      if (data && data.length) {
        if (data[0] === "gnd") {
          paramType = 1;
          sourceCode = `${data[0]}@${data[1]}@${data[2]}`;
          if (props.exampleType === 1) sourceCode = `${data[0]}@${props.recordId}@${data[1]}`;
        }
        if (data[0] === "jd") {
          paramType = 2;
          sourceCode = data[0] + "@" + data[data.length - 1];
        }

        let dataId = data[data.length - 1];
        let array = JSON.parse(JSON.stringify(treeToArray(that.mappingArray)));
        let findItem = array.find(item => item.id === dataId);
        if (findItem && findItem.dataType === 8) isIndexDisabled = 1;
      }

      that.varTableList[index].paramType = paramType;
      that.varTableList[index].sourceCode = sourceCode;
      that.varTableList[index].isIndexDisabled = isIndexDisabled;
    }

    const clickTableAddBut = () => {
      if (!that.varTableList) that.varTableList = [];
      that.varTableList.push({ defaultValue: null });
    }

    const clickTableDeleteBut = (index) => {
      that.varTableList.splice(index, 1);
    }

    const returnVarTableListFun = () => {
      let paramNameArray = [], varTableList = [];

      if (that.varTableList && that.varTableList.length) {
        varTableList = JSON.parse(JSON.stringify(that.varTableList));
        for (let i = 0; i < varTableList.length; i++) {

          if (!varTableList[i].paramName) {
            ElMessage({ type: "error", showClose: true, message: `请填写${i + 1}行的变量名称` });
            return
          }

          if (!someCharmap(varTableList[i].paramName)) {
            ElMessage({ type: "error", showClose: true, message: `请填写正确的变量名称（${i + 1}行）` });
            return
          }

          if (!varTableList[i].sourceId) {
            ElMessage({ type: "error", showClose: true, message: `请选择${i + 1}行的数据来源` });
            return
          }

          let findIndex = paramNameArray.findIndex(item => item === varTableList[i].paramName);
          if (findIndex !== -1) {
            ElMessage({ type: "error", showClose: true, message: `第${i + 1}行的变量名称与${findIndex}行重复了` });
            return
          }

          paramNameArray.push(varTableList[i].paramName);
        }
      }

      return varTableList
    }

    const handleDragStart = (e, data) => {
      // console.log(data)
      const target = document.createElement('span');
      target.className = "dragDomClass";
      target.innerHTML = data;

      let dataHtml = '';
      if (data) dataHtml = `<span class="clickable-text mceNonEditable variable_class">${data}</span>`;
      e.dataTransfer.setData('text/html', dataHtml); // 设置数据类型为纯文本
      e.target.classList.add('draggingClass'); // 添加拖拽时的样式
      e.dataTransfer.setDragImage(target, 0, 0); // 设置拖拽时的图片
    }

    const handleDragEnd = (e, data) => {
      // console.log(data);
      e.target.classList.remove('draggingClass'); // 移除拖拽时的样式
    }

    // 表格 头部中那些带有红色 *
    const headerClass = (obj) => {
      if (obj.columnIndex > 0 && obj.columnIndex <= 2) return 'headerClass';
    }

    // 初始化表格高度
    const tableCenterRef = ref(null);
    const setTableMaxHeight = () => {
      that.tableMaxHeight = tableCenterRef.value.offsetHeight;
    }

    return {
      ...toRefs(that), tableCenterRef, setTableMaxHeight, clickTableAddBut, clickTableDeleteBut, returnVarTableListFun, cascaderLazyLoad, changeMappingValue,
      handleDragEnd, handleDragStart, headerClass
    }
  }
})
</script>

<style lang="scss" scoped>
.nodeVariableInfo {
  height: 100%;

  .addIconBut {
    font-size: 21px;
    cursor: pointer;
    color: #1f74e2;
  }

  .scIconBut {
    font-size: 22px;
    cursor: pointer;
    color: #ff2626;
  }

  .draggable_class {
    .iconfont {
      margin-left: 6px;
    }
  }
}
</style>