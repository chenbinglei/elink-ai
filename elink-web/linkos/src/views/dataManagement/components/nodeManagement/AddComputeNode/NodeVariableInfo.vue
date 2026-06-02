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
          <!-- 使用独立的key强制重新渲染 -->
          <el-cascader 
            :key="`cascader_${$index}_${cascaderKey}`"
            v-model="row.sourceId" 
            :options="getCascaderOptions($index)" 
            :props="cascaderProps" 
            clearable 
            placeholder="请选择" 
            filterable 
            style="width: 100%;"
            @change="(data)=>changeMappingValue(data,$index)"
          />
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

<script>
import { treeToArray } from "@/utils";
import { ElMessage } from "element-plus";
import { someCharmap } from "@/utils/validate";
import { reactive, ref, toRefs, defineComponent, onMounted, onUnmounted, nextTick,watch } from "vue";
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
      // 不再直接使用mappingArray，改用rowOptionsMap
      varTableList: [],
      tableMaxHeight: 300,
      defaultValueArray: [{ id: 0, name: "0" }, { id: null, name: "NULL" }],
    });

    // 核心修复1：为每一行维护独立的options
    const rowOptionsMap = ref(new Map());
    // 核心修复2：缓存已加载的节点数据
    const loadedNodesCache = ref(new Map());
    // 核心修复3：标记正在加载中的节点
    const loadingNodes = ref(new Set());
    // 强制刷新级联选择器的key
    const cascaderKey = ref(0);

    // 级联选择器配置
    const cascaderProps = reactive({
      lazy: true,
      value: "id",
      label: 'name',
      children: 'children',
      lazyLoad: (node, resolve) => cascaderLazyLoad(node, resolve),
      isLeaf: (node) => node.leaf || !node.children || node.children.length === 0
    });

    // 获取指定行的options
    const getCascaderOptions = (rowIndex) => {
      if (!rowOptionsMap.value.has(rowIndex)) {
        // 初始化基础选项
        rowOptionsMap.value.set(rowIndex, [
          { id: "gnd", name: "功能点", leaf: false, children: [] },
          { id: "jd", name: "计算节点", leaf: false, children: [] }
        ]);
      }
      return rowOptionsMap.value.get(rowIndex);
    };

    // 核心修复：懒加载函数
    const cascaderLazyLoad = async (node, resolve) => {
      const { level, data, pathValues } = node;
      
      // 生成唯一节点标识
      const nodeKey = `${level}_${data?.id || 'root'}_${pathValues?.join('_') || ''}`;
      
      // 调试日志
      console.log(`[LazyLoad] 节点: ${nodeKey}, 层级: ${level}, 数据:`, data);

      // 1. 检查是否正在加载
      if (loadingNodes.value.has(nodeKey)) {
        console.log(`[LazyLoad] 节点正在加载中，跳过: ${nodeKey}`);
        resolve([]);
        return;
      }

      // 2. 检查缓存
      if (loadedNodesCache.value.has(nodeKey)) {
        console.log(`[LazyLoad] 使用缓存: ${nodeKey}`);
        const cachedData = loadedNodesCache.value.get(nodeKey);
        
        // 更新当前行的options
        if (pathValues && pathValues.length > 0) {
          await updateRowOptions(node, cachedData);
        }
        
        resolve(cachedData);
        return;
      }

      // 3. 标记为加载中
      loadingNodes.value.add(nodeKey);
      
      try {
        let result = [];

        // 一级节点：功能点/计算节点
        if (level === 0) {
          result = [
            { id: "gnd", name: "功能点", leaf: false, children: [] },
            { id: "jd", name: "计算节点", leaf: false, children: [] }
          ];
        }
        
        // 二级节点：设备列表（仅站点类型）
        else if (props.exampleType === 2 && level === 1) {
          const res = await findSiteDeviceDataById({ 
            siteId: props.recordId, 
            level: level, 
            timer: new Date() 
          });
          
          let devices = res.data || [];
          
          if (devices.length === 0) {
            data.leaf = true;
            result = [];
          } else {
            // 处理设备数据
            result = devices.map(item => ({
              ...item,
              name: item.deviceName,
              parentId: data.id,
              leaf: false,
              children: []
            }));
            
            // 添加"当前站点"选项
            result.unshift({ 
              name: "当前站点", 
              id: props.recordId, 
              parentId: data.id, 
              leaf: false,
              children: []
            });
          }
        }
        
        // 三级节点：功能点/计算节点
        else if (level === 2 || (props.exampleType === 1 && level === 1)) {
          const searchType = pathValues?.[0] === "gnd" ? "gnd" : "jd";
          const requestData = { 
            deviceId: props.exampleType === 2 ? data.id : props.recordId, 
            level: level, 
            timer: new Date() 
          };
          
          const res = await findComputeNodeAndFunctionListById(requestData, searchType);
          let nodes = res.data || [];
          
          if (nodes.length === 0) {
            data.leaf = true;
            result = [];
          } else {
            result = nodes.map(item => {
              const nodeItem = {
                ...item,
                leaf: true,
                name: item.functionName || item.nodeName,
                originId: item.id,
                dataType: item.dataType,
                parentId: data.id
              };
              
              if (searchType === "gnd") nodeItem.id = item.functionLogo;
              if (searchType === "jd") nodeItem.id = item.storageId;
              
              return nodeItem;
            });
          }
        } else {
          data.leaf = true;
          result = [];
        }

        // 存入缓存
        if (result.length > 0 || level === 0) {
          loadedNodesCache.value.set(nodeKey, result);
        }

        // 更新当前行的options
        if (pathValues && pathValues.length > 0 && result.length > 0) {
          await updateRowOptions(node, result);
        }

        console.log(`[LazyLoad] 加载完成: ${nodeKey}`, result);
        resolve(result);

      } catch (error) {
        console.error('[LazyLoad] 加载失败:', error);
        resolve([]);
      } finally {
        // 延迟移除加载标记
        setTimeout(() => {
          loadingNodes.value.delete(nodeKey);
        }, 500);
      }
    };

    // 更新指定行的options
    const updateRowOptions = async (node, childrenData) => {
      const { pathValues } = node;
      
      // 找到当前是第几行
      let targetRowIndex = -1;
      for (let i = 0; i < that.varTableList.length; i++) {
        const row = that.varTableList[i];
        if (row._temp_pathValues && JSON.stringify(row._temp_pathValues) === JSON.stringify(pathValues.slice(0, -1))) {
          targetRowIndex = i;
          break;
        }
      }

      if (targetRowIndex === -1) return;

      const rowOptions = rowOptionsMap.value.get(targetRowIndex);
      if (!rowOptions) return;

      // 根据pathValues找到要更新的节点
      let targetNode = null;
      let currentLevel = rowOptions;

      for (let i = 0; i < pathValues.length; i++) {
        const pathId = pathValues[i];
        const found = currentLevel.find(item => item.id === pathId);
        if (found) {
          targetNode = found;
          currentLevel = found.children || [];
        } else {
          break;
        }
      }

      if (targetNode) {
        targetNode.children = childrenData;
        // 强制刷新级联选择器
        cascaderKey.value++;
        await nextTick();
      }
    };

    // 来源发生改变执行
    const changeMappingValue = (data, index) => {
      let isIndexDisabled = 0;
      let sourceCode = "", paramType = "";
      
      if (data && data.length) {
        // 保存pathValues到行的临时属性
        that.varTableList[index]._temp_pathValues = data;
        
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
        // 从缓存中查找数据类型
        for (let [key, value] of loadedNodesCache.value) {
          const found = value.find(item => item.id === dataId || item.originId === dataId);
          if (found && found.dataType === 8) {
            isIndexDisabled = 1;
            break;
          }
        }
      }

      that.varTableList[index].paramType = paramType;
      that.varTableList[index].sourceCode = sourceCode;
      that.varTableList[index].isIndexDisabled = isIndexDisabled;
    }

    const clickTableAddBut = () => {
      if (!that.varTableList) that.varTableList = [];
      
      // 新增行时初始化数据
      const newRow = { 
        defaultValue: null,
        sourceId: [],
        isIndexDisabled: 0,
        indexNum: undefined,
        paramName: '',
        minValue: undefined,
        maxValue: undefined
      };
      
      that.varTableList.push(newRow);
      
      // 为新行创建独立的options
      const newIndex = that.varTableList.length - 1;
      rowOptionsMap.value.set(newIndex, [
        { id: "gnd", name: "功能点", leaf: false, children: [] },
        { id: "jd", name: "计算节点", leaf: false, children: [] }
      ]);
      
      // 强制刷新
      cascaderKey.value++;
    }

    const clickTableDeleteBut = (index) => {
      that.varTableList.splice(index, 1);
      // 删除对应的options
      rowOptionsMap.value.delete(index);
      
      // 重新索引后面的行
      const newMap = new Map();
      that.varTableList.forEach((_, idx) => {
        if (rowOptionsMap.value.has(idx + 1)) {
          newMap.set(idx, rowOptionsMap.value.get(idx + 1));
        }
      });
      rowOptionsMap.value = newMap;
      
      cascaderKey.value++;
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

          if (!varTableList[i].sourceId || varTableList[i].sourceId.length === 0) {
            ElMessage({ type: "error", showClose: true, message: `请选择${i + 1}行的数据来源` });
            return
          }

          let findIndex = paramNameArray.findIndex(item => item === varTableList[i].paramName);
          if (findIndex !== -1) {
            ElMessage({ type: "error", showClose: true, message: `第${i + 1}行的变量名称与${findIndex + 1}行重复了` });
            return
          }

          paramNameArray.push(varTableList[i].paramName);
        }
      }

      return varTableList
    }

    const handleDragStart = (e, data) => {
      const target = document.createElement('span');
      target.className = "dragDomClass";
      target.innerHTML = data;

      let dataHtml = '';
      if (data) dataHtml = `<span class="clickable-text mceNonEditable variable_class">${data}</span>`;
      e.dataTransfer.setData('text/html', dataHtml); 
      e.target.classList.add('draggingClass'); 
      e.dataTransfer.setDragImage(target, 0, 0); 
    }

    const handleDragEnd = (e, data) => {
      e.target.classList.remove('draggingClass'); 
    }

    const headerClass = (obj) => {
      if (obj.columnIndex > 0 && obj.columnIndex <= 2) return 'headerClass';
    }

    // 初始化表格高度
    const tableCenterRef = ref(null);
    const setTableMaxHeight = () => {
      that.tableMaxHeight = tableCenterRef.value.offsetHeight;
    }

    // 监听props变化，重置状态
    watch(() => props.recordId, () => {
      // 清空缓存
      rowOptionsMap.value.clear();
      loadedNodesCache.value.clear();
      loadingNodes.value.clear();
      that.varTableList = [];
      cascaderKey.value++;
    });

    // 组件卸载时清理
    onUnmounted(() => {
      rowOptionsMap.value.clear();
      loadedNodesCache.value.clear();
      loadingNodes.value.clear();
    });

    return {
      ...toRefs(that),
      tableCenterRef,
      setTableMaxHeight,
      clickTableAddBut,
      clickTableDeleteBut,
      returnVarTableListFun,
      changeMappingValue,
      handleDragEnd,
      handleDragStart,
      headerClass,
      cascaderProps,
      getCascaderOptions,
      cascaderKey
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