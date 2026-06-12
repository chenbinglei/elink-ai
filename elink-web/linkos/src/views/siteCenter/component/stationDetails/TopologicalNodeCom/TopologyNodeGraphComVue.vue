<template>
  <div class="topology-container">
    <div class="topology-canvas" ref="canvas" v-dragBox>
      <svg class="lines-container">
        <defs>
          <linearGradient v-for="connection in connections" :id="'gradient-' + connection.id"
            :key="'grad-' + connection.id" gradientUnits="userSpaceOnUse" :x1="connection.x1" :y1="connection.y1"
            :x2="connection.x2" :y2="connection.y2">

            <stop offset="0%" stop-color="#FFFFFF" />
            <!-- 浅灰色 -->
            <stop offset="30%" stop-color="#EAEAEA" />
            <!-- 紫色 -->
            <stop offset="50%" stop-color="#A296E3" />
            <!-- 浅灰色 -->
            <stop offset="70%" stop-color="#EAEAEA" />
            <!-- 白色 -->
            <stop offset="100%" stop-color="#FFFFFF" />

          </linearGradient>
        </defs>

        <line v-for="connection in connections" :key="connection.id" :x1="connection.x1" :y1="connection.y1"
          :x2="connection.x2" :y2="connection.y2" :stroke="'url(#gradient-' + connection.id + ')'" stroke-width="3"
          marker-end="url(#arrowhead)" />
      </svg>

      <div v-for="node in internalNodes" :key="node.id" :style="nodeStyle(JSON.parse(node.pageExtend))"
        :class="{ active: node.active }" class="node">
        <div class="node-content" @click="handleClickNode(node)">
          <img :src="getNodeImage(node)" alt="节点图标" />
        </div>
         <div class="nodeTypeName">{{ node.nodeName }}</div>


        <div v-if="node.active" @click="deleteNode(node)">
          <div class="connector L-R">
            <el-icon>
              <RemoveFilled />
            </el-icon>
          </div>
          <div class="connector left" @click.stop="addNode(node, 'left')">
            <el-icon style="color: #0080FF;">
              <CirclePlus />
            </el-icon>
          </div>
          <div class="connector bottom" @click.stop=" addNode(node, 'bottom')">
            <el-icon style="color: #0080FF;">
              <CirclePlus />
            </el-icon>
          </div>
          <div class="connector right" @click.stop="addNode(node, 'right')">
            <el-icon style="color: #0080FF;">
              <CirclePlus />
            </el-icon>
          </div>
        </div>

      </div>
    </div>

    <svg style="display: none;">
      <defs>
        <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
          <polygon points="0 0, 10 3.5, 0 7" fill="#666" />
        </marker>
      </defs>
    </svg>
    <AddTopologyNodeDialog v-if="addTopologyNodeVisible" ref="addDialog" :topNodeId="topNodeId"
      v-model:isVisible="addTopologyNodeVisible" :siteRecordsId="siteRecordsId" :titleName="titleName"
      :activeParentNodeInfo="activeParentNodeInfo" :newNode="newNodeList" @changeEvent="queryTopoNodeListBySiteId">
    </AddTopologyNodeDialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue';
import { CirclePlus, RemoveFilled } from '@element-plus/icons-vue';
import { saveSiteTopNode } from "@/api/siteCenter/stationDetails";
import { ElMessage, ElMessageBox } from 'element-plus';
import AddTopologyNodeDialog from './AddTopologyNodeDialogVue.vue'
import { deleteTopNodeInfoById } from "@/api/siteCenter/stationDetails";
import { useAppStore } from '@/stores/index';

const props = defineProps({
  nodes: {
    type: Array,
    default: () => []
  },
  siteRecordsId: {
    type: [String, Number],
    default: ""
  },
});
const appStore = useAppStore();

const canvas = ref(null);
const scale = ref(1); // 初始缩放比例为 1
const emit = defineEmits(['update:nodes']);
const titleName = ref('节点管理');
const addTopologyNodeVisible = ref(false);
const newNodeList = ref(null);
const siteRecordsId = ref(null)
const activeParentNodeInfo = ref(null)
const topNodeId = ref(null)
// 创建响应式节点副本
const internalNodes = ref(
  props.nodes.map(node => ({
    ...node,
    width: 60,   // 设置默认宽度
    height: 60    // 设置默认高度
  }))
);
const nodeType = ref([
  { id: 1, name: '拓扑点', image: require("@/assets/image/TPJD.png") },
  { id: 2, name: '电网', image: require("@/assets/image/powerGrid.png") },
  { id: 3, name: '变压器', image: require("@/assets/image/BYQ.png") },
  { id: 4, name: '关口点', image: require("@/assets/image/GKJD.png") },
  { id: 5, name: '计量点', image: require("@/assets/image/JLJD.png") },
  { id: 6, name: '逆变器', image: require("@/assets/image/NBQ.png") },
  { id: 7, name: '光伏组件', image: require("@/assets/image/GF.png") },
  { id: 8, name: '储能柜', image: require("@/assets/image/CNG.png") },
  { id: 9, name: '负荷', image: require("@/assets/image/FHJD.png") },
  { id: 10, name: '充电桩', image: require("@/assets/image/CDZ.png") },
  { id: 11, name: '开关', image: require("@/assets/image/KGJD.png") },
  { id: 12, name: '车辆', image: require("@/assets/image/CL.png") },
  { id: 13, name: '换电站', image: require("@/assets/image/HDZJD.png") }
]);
let nodeIdCounter = internalNodes.value.length > 0
  ? Math.max(...internalNodes.value.map(n => n.id)) + 1
  : 1;
// 监听 props 变化
watch(() => props.nodes, (newNodes) => {
  internalNodes.value = [...newNodes];
  nodeIdCounter = internalNodes.value.length > 0
    ? Math.max(...internalNodes.value.map(n => n.id)) + 1
    : 1;
});

// 更新父组件
const updateNodes = () => {

  emit('update:nodes', [...internalNodes.value]);
};

// 计算所有连线
const connections = computed(() => {
  const result = [];
  // 遍历所有节点
  internalNodes.value.forEach(node => {
    const nodeData = JSON.parse(node.pageExtend);
    const { x: nodeX, y: nodeY, width: nodeWidth, height: nodeHeight } = nodeData;
    // 如果该节点有 parentId，则查找父节点
    if (nodeData.parentId) {
      const parentNode = internalNodes.value.find(n => n.id === nodeData.parentId);
      if (parentNode) {
        const parentData = JSON.parse(parentNode.pageExtend);
        const { x: parentX, y: parentY, width: parentWidth, height: parentHeight } = parentData;
        let x1, y1, x2, y2;
        if (parentX < nodeX) { // 左侧连接
          x1 = nodeX;
          y1 = nodeY + nodeHeight / 2;
          x2 = parentX + parentWidth;
          y2 = parentY + parentHeight / 2;
        } else if (parentX > nodeX) { // 右侧连接
          x1 = nodeX + nodeWidth;
          y1 = nodeY + nodeHeight / 2;
          x2 = parentX;
          y2 = parentY + parentHeight / 2;
        } else { // 上下连接
          x1 = parentX + parentWidth / 2;       // 父节点中心 X
          y1 = parentY + parentHeight + 20;     // 父节点底部向下 20px
          x2 = nodeX + nodeWidth / 2;           // 子节点中心 X
          y2 = nodeY;                           // 子节点顶部
        }
        result.push({
          id: `conn-${node.id}-${nodeData.parentId}`,
          x1, y1, x2, y2
        });
      }
    }
  });

  return result;
});

const queryTopoNodeListBySiteId = () => {
  emit("changeEvent");
}
const nodeStyle = (styleObj) => {
  return {
    left: styleObj.x + 'px',
    top: styleObj.y + 'px',
    width: styleObj.width + 'px',
    height: styleObj.height + 'px'
  };
};
// 添加新节点
const addNode = (parentNode, direction, x, y) => {
  topNodeId.value = ''
  activeParentNodeInfo.value = parentNode
  siteRecordsId.value = parentNode?.siteId

  const spacing = 200;
  const parsedParentNode = parentNode?.pageExtend ? JSON.parse(parentNode.pageExtend) : null;
  // 检查下方是否有节点
  // const isCollision = internalNodes.value.some(item => {
  //   const itemData = JSON.parse(item.pageExtend);
  //   const targetY = parsedParentNode.y + spacing;

  //   // 判断是否在目标 y 轴附近（允许一定误差）
  //   const tolerance = 20;
  //   return (
  //     Math.abs(itemData.y - targetY) <= tolerance
  //   );
  // });
  // if (isCollision) {
  //   console.log('下方已有节点，不添加');
  //   return;
  // }
  let targetX = parsedParentNode?.x;
  let targetY = parsedParentNode?.y;
  if (direction === 'left') {
    targetX = parsedParentNode.x - spacing;
  } else if (direction === 'right') {
    targetX = parsedParentNode.x + spacing;
  } else if (direction === 'bottom') {
    targetY = parsedParentNode.y + spacing;
  }
  // 检查目标位置是否已有节点
  const isCollision = internalNodes.value.some(item => {
    const itemData = JSON.parse(item.pageExtend);
    const tolerance = 20; // 允许的误差范围

    if (direction === 'left' || direction === 'right') {
      return Math.abs(itemData.y - targetY) <= tolerance &&
        Math.abs(itemData.x - targetX) <= tolerance;
    } else if (direction === 'bottom') {
      return Math.abs(itemData.x - targetX) <= tolerance &&
        Math.abs(itemData.y - targetY) <= tolerance;
    }
    return false;
  });

  if (isCollision) {
    ElMessage({
      message: `该方向已有节点，禁止添加`,
      type: 'warning',
      duration: 2000,
    });
    return;
  }
  // 碰撞检测和空间调整
  // if (direction === 'right') {
  //   const objLineRight = connections.value.find(item => {
  //     const centerX = parsedParentNode.x + parsedParentNode.width / 2 + spacing;
  //     return (
  //       item.x1 === centerX &&
  //       ((item.y1 < parsedParentNode.y && parsedParentNode.y < item.y2) ||
  //         (item.y1 > parsedParentNode.y && parsedParentNode.y > item.y2))
  //     );
  //   });
  //   if (objLineRight) {
  //     const modifiedXNodes = [];
  //     internalNodes.value = internalNodes.value.map(item => {
  //       const itemData = JSON.parse(item.pageExtend);
  //       let updated = false;
  //       if (itemData.x > parsedParentNode.x) {
  //         itemData.x += spacing;
  //         updated = true;
  //       }

  //       if (updated) {
  //         modifiedXNodes.push({
  //           ...item,
  //           pageExtend: JSON.stringify(itemData)
  //         });
  //       }

  //       return {
  //         ...item,
  //         pageExtend: JSON.stringify(itemData)
  //       };
  //     });
  //     appStore.updateNewTopoNode(modifiedXNodes);
  //     // modifiedXNodes.forEach(node => saveDialog(node));
  //   }
  //   // 查找与父节点中间垂直位置相交的连接线
  //   const objRight = internalNodes.value.find(item => {
  //     const itemData = JSON.parse(item.pageExtend);
  //     return (
  //       itemData.x === parsedParentNode.x + spacing &&
  //       itemData.y === parsedParentNode.y
  //     );
  //   });

  //   if (objRight) {
  //     const modifiedXNodes = [];
  //     internalNodes.value = internalNodes.value.map(item => {
  //       const itemData = JSON.parse(item.pageExtend);
  //       let updated = false;
  //       if (itemData.x > parsedParentNode.x) {
  //         itemData.x += spacing;
  //         updated = true;
  //       }

  //       if (updated) {
  //         modifiedXNodes.push({
  //           ...item,
  //           pageExtend: JSON.stringify(itemData)
  //         });
  //       }
  //       return {
  //         ...item,
  //         pageExtend: JSON.stringify(itemData)
  //       };
  //     });
  //     appStore.updateNewTopoNode(modifiedXNodes);
  //     // modifiedXNodes.forEach(node => saveDialog(node));
  //   }
  // }
  // if (direction == 'left') {
  //   // 查找与父节点中间垂直位置相交的连接线（向左）
  //   const objLineLeft = connections.value.find(item => {
  //     const centerX = parsedParentNode.x + parsedParentNode.width / 2 - spacing;
  //     return (
  //       item.x1 === centerX &&
  //       ((item.y1 < parsedParentNode.y && parsedParentNode.y < item.y2) ||
  //         (item.y1 > parsedParentNode.y && parsedParentNode.y > item.y2))
  //     );
  //   });

  //   if (objLineLeft) {
  //     const modifiedXNodes = [];
  //     internalNodes.value = internalNodes.value.map(item => {
  //       const itemData = JSON.parse(item.pageExtend);
  //       let updated = false;

  //       if (itemData.x < parsedParentNode.x) {
  //         itemData.x -= spacing;
  //         updated = true;
  //       }

  //       if (updated) {
  //         modifiedXNodes.push({
  //           ...item,
  //           pageExtend: JSON.stringify(itemData)
  //         });
  //       }

  //       return {
  //         ...item,
  //         pageExtend: JSON.stringify(itemData)
  //       };
  //     });
  //     appStore.updateNewTopoNode(modifiedXNodes);
  //     // modifiedXNodes.forEach(node => saveDialog(node));
  //   }
  //   // 查找与父节点中间垂直位置相交的连接线（向左）
  //   const objLeft = internalNodes.value.find(item => {
  //     const itemData = JSON.parse(item.pageExtend);
  //     return (
  //       itemData.x === parsedParentNode.x - spacing &&
  //       itemData.y === parsedParentNode.y
  //     );
  //   });

  //   if (objLeft) {
  //     const modifiedXNodes = [];
  //     internalNodes.value = internalNodes.value.map(item => {
  //       const itemData = JSON.parse(item.pageExtend);
  //       let updated = false;

  //       if (itemData.x < parsedParentNode.x) {
  //         itemData.x -= spacing;
  //         updated = true;
  //       }

  //       if (updated) {
  //         modifiedXNodes.push({
  //           ...item,
  //           pageExtend: JSON.stringify(itemData)
  //         });
  //       }

  //       return {
  //         ...item,
  //         pageExtend: JSON.stringify(itemData)
  //       };
  //     });
  //     appStore.updateNewTopoNode(modifiedXNodes);
  //     // modifiedXNodes.forEach(node => saveDialog(node));
  //   }
  // }
  // if (direction == 'bottom') {
  //   const objLine = connections.value.find(item => {
  //     const centerY = parsedParentNode.y + parsedParentNode.height / 2 + spacing;
  //     return (
  //       item.y1 === centerY &&
  //       ((item.x1 < parsedParentNode.x && parsedParentNode.x < item.x2) ||
  //         (item.x1 > parsedParentNode.x && parsedParentNode.x > item.x2))
  //     );
  //   });

  //   if (objLine) {
  //     const modifiedYNodes = [];
  //     internalNodes.value = internalNodes.value.map(item => {
  //       const itemData = JSON.parse(item.pageExtend);
  //       let updated = false;
  //       if (itemData.y > parsedParentNode.y) {
  //         itemData.y += spacing;
  //         updated = true;
  //       }
  //       if (updated) {
  //         modifiedYNodes.push({
  //           ...item,
  //           pageExtend: JSON.stringify(itemData)
  //         });
  //       }
  //       return {
  //         ...item,
  //         pageExtend: JSON.stringify(itemData)
  //       };

  //     })
  //     console.log('updateNewTopoNode', modifiedYNodes);

  //     appStore.updateNewTopoNode(modifiedYNodes);
  //     // modifiedXNodes.forEach(node => saveDialog(node));
  //   }
  //   // 查找 x 和 y 匹配的节点
  //   const obj = internalNodes.value.find(item => {
  //     const itemData = JSON.parse(item.pageExtend);
  //     return (
  //       itemData.x === parsedParentNode.x &&
  //       itemData.y === parsedParentNode.y + spacing
  //     );
  //   });
  //   if (obj) {
  //     const modifiedYNodes = [];
  //     internalNodes.value = internalNodes.value.map(item => {
  //       const itemData = JSON.parse(item.pageExtend);
  //       let updated = false;
  //       if (itemData.y > parsedParentNode.y) {
  //         itemData.y += spacing;
  //         updated = true;
  //       }
  //       if (updated) {
  //         modifiedYNodes.push({
  //           ...item,
  //           pageExtend: JSON.stringify(itemData)
  //         });
  //       }
  //       return {
  //         ...item,
  //         pageExtend: JSON.stringify(itemData)
  //       };
  //     });
  //     appStore.updateNewTopoNode(modifiedYNodes);
  //     // modifiedXNodes.forEach(node => saveDialog(node));
  //   }
  // }


  const canvasRect = canvas.value.getBoundingClientRect();
  let xm = direction === 'initial' ? canvasRect.width / 2 - 60 : x;
  let yn = direction === 'initial' ? canvasRect.height / 6 - 60 : y;
  let newX = xm ?? 0;
  let newY = yn ?? 0;

  if (parentNode) {
    // if (isDirectionDisabled(parentNode, direction)) return;
    switch (direction) {
      case 'left':
        newX = parsedParentNode.x - spacing;
        newY = parsedParentNode.y;
        break;
      case 'bottom':
        newX = parsedParentNode.x;
        newY = parsedParentNode.y + spacing;
        break;
      case 'right':
        newX = parsedParentNode.x + spacing;
        newY = parsedParentNode.y;
        break;
    }
  }
  const newNode = {
    id: parentNode.id,
    x: newX,
    y: newY,
    width: 60,
    height: 60,
    content: '',
    children: [],
    disabledDirections: [],
    parentId: parentNode.id ? parentNode.id : "/",
    parentName: parentNode.nodeName ? parentNode.nodeName : '/'
  };


  // 建立父子关系
  if (parentNode) {
    parentNode.children = [...(parentNode.children || []), newNode.id];
    switch (direction) {
      case 'left':
        parentNode.disabledDirections = [...(parentNode.disabledDirections || []), 'left'];
        newNode.disabledDirections = ['right'];
        break;
      case 'right':
        parentNode.disabledDirections = [...(parentNode.disabledDirections || []), 'right'];
        newNode.disabledDirections = ['left'];
        break;
      case 'bottom':
        parentNode.disabledDirections = [...(parentNode.disabledDirections || []), 'bottom'];
        break;
    }
    // if (direction !== 'initial') {
    //   // 方向使用之后禁用-------
    //   const modifiedYNodes = [];
    //   // 解析 pageExtend 字符串为对象
    //   let pageExtendObj = JSON.parse(parentNode.pageExtend);
    //   // 创建一个新的 disabledDirections 数组
    //   const newDisabledArray = [
    //     ...pageExtendObj.disabledDirections,
    //     ...parentNode.disabledDirections
    //   ];
    //   // 创建新的 itemData 对象，包含合并后的 disabledDirections
    //   const itemDirections = {
    //     ...parentNode,
    //     pageExtend: JSON.stringify({
    //       ...pageExtendObj,
    //       disabledDirections: newDisabledArray
    //     })
    //   };
    //   // 将新对象推入 modifiedYNodes
    //   modifiedYNodes.push(itemDirections);
    //   appStore.updateNewTopoNode(modifiedYNodes);
    //   // ----------------
    // }

  }
  newNodeList.value = newNode
  addTopologyNodeVisible.value = true;

};
const getNodeImage = (node) => {
  const type = nodeType.value.find(item => item.id === node.nodeType);
  return type.image;
};
// 删除节点及其所有子节点
const deleteNode = async (node) => {
  ElMessageBox.confirm(`确定删除此节点？`, "提示", {
    dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        instance.confirmButtonText = '正在删除...';
        deleteTopNodeInfoById({ topNodeId: node.id }).then(() => {
          done();
          instance.confirmButtonLoading = false;
          emit("changeEvent");
        }).catch(() => {
          emit("changeEvent");
        });
      } else {
        done();
      }
    }
  }).then(() => {
    listArray("refresh");
    ElMessage({ type: "success", showClose: true, message: "删除成功！" });
  }).catch(() => {
    console.log("取消删除！");
  });

};
// 点击节点
const handleClickNode = (node) => {
  emit('handleClickNode', node);
  internalNodes.value.forEach(x => x.active = false);
  node.active = true;
  updateNodes();
};

// 双击节点
// const handleDoubleClick = (node) => {
//   topNodeId.value = node.id
//   addTopologyNodeVisible.value = true;
// };
const vDragBox = (el, binding) => {
  const dragBox = el; // 获取当前元素

  // 当选中对象为输入框或者文本框不执行拖拽操作
  dragBox.onmousedown = (e) => {
    if (e.target.nodeName === 'INPUT' || e.target.nodeName === 'TEXTAREA') {
      return;
    }

    e.preventDefault(); // 防止默认行为

    // 算出鼠标相对元素的位置
    const disX = e.clientX - dragBox.offsetLeft;
    const disY = e.clientY - dragBox.offsetTop;

    document.onmousemove = (e) => {
      // 用鼠标的位置减去鼠标相对元素的位置，得到元素的位置
      e.preventDefault();
      const left = e.clientX - disX;
      const top = e.clientY - disY;

      // 移动当前元素
      dragBox.style.left = left + "px";
      dragBox.style.top = top + "px";
      // binding.value.set(left, top);
    };

    document.onmouseup = (e) => {
      e.preventDefault();
      // 鼠标弹起来的时候不再移动
      document.onmousemove = null;
      // 预防鼠标弹起来后还会循环（即预防鼠标放上去的时候还会移动）
      document.onmouseup = null;
    };
  }
};
// 通过鼠标滚动实现放大缩小
const handleWheel = (e) => {
  e.preventDefault();
  const delta = e.deltaY;
  const zoomFactor = 0.1;

  if (delta > 0) {
    scale.value = Math.max(0.5, scale.value - zoomFactor);
  } else {
    scale.value = Math.min(3, scale.value + zoomFactor);
  }

  if (canvas.value) {
    canvas.value.style.transform = `scale(${scale.value})`;
  }
};
onMounted(() => {
  if (canvas.value) {
    canvas.value.addEventListener('wheel', handleWheel);
  }
});

onUnmounted(() => {
  if (canvas.value) {
    canvas.value.removeEventListener('wheel', handleWheel);
  }
});
// 暴露方法给父组件调用
defineExpose({ addNode });
</script>

<style scoped>
.topology-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.topology-canvas {
  position: absolute;
  width: 100%;
  height: 100%;
  transform-origin: top left;
  transition: transform 0.2s ease;
}

.add-button {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 10;
  padding: 8px 16px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer
}



.lines-container {
  position: absolute;
  top: 0;
  left: 0;
  pointer-events: none;
  z-index: 1;
  overflow: visible;
  /* 确保超出部分可见 */
}

.node {
  position: absolute;
  background-color: white;
  border: 2px solid #D1D1D1;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: move;
  z-index: 2;
  width: 40px;
  height: 40px;
}

.node.active {
  border: 2px dashed RGBA(56, 161, 247, .5);
}

/* 添加在style部分 */
.node {
  transition: all 0.3s ease;
}

.line-extended {
  stroke: #ff7d00;
  stroke-width: 3;
  animation: linePulse 0.8s ease-in-out infinite;
}

@keyframes linePulse {
  0% {
    stroke-width: 3;
  }

  50% {
    stroke-width: 5;
  }

  100% {
    stroke-width: 3;
  }
}

.connector.highlight {
  background-color: #ff7d00;
  transform: scale(1.2);
  transition: all 0.3s ease;
}

.nodeTypeName {
  text-align: center;
  padding-top: 8px;
  font-size: 12px;
  white-space: nowrap;
  /* 禁止换行 */
  overflow: hidden;
  /* 隐藏溢出内容 */
  text-overflow: ellipsis;
  /* 显示 ... */
}

.node-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  box-sizing: border-box;
  word-break: break-all;
}

.connector {
  position: absolute;
  width: 16px;
  height: 16px;
  /* background-color: #409EFF; */
  /* border-radius: 50%; */
  cursor: pointer;
  z-index: 3;
}

.L-R {
  right: -16px;
  top: 0%;
  transform: translateY(-50%);
  background-color: transparent;

  .el-icon {
    color: #FF4A33;
  }
}

.connector.left {
  left: -9px;
  top: 50%;
  transform: translateY(-50%);
}

.connector.bottom {
  left: 50%;
  bottom: -10px;
  transform: translateX(-50%);
}

.connector.right {
  right: -10px;
  top: 50%;
  transform: translateY(-50%);
}

.connector.disabled {
  /* background-color: #ccc; */
  cursor: not-allowed;
  opacity: 0.5;
}
</style>