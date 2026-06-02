<template>

  <div class="container">
    <el-button class="reset-button" @click="resetCanvas">重置</el-button>

    <el-empty class="empty-container" :image="emptyImg" v-if="treeData.length == '0'" description="暂无数据" />
    <div class="topology-canvas" v-else v-dragBox ref="canvas"
      :style="{ right: scrollLeft + 'px', bottom: scrollTop + 'px' }">
      <svg class="lines-container">
        <line v-for="connection in connections" :key="connection.id" :x1="connection.x1" :y1="connection.y1"
          :x2="connection.x2" :y2="connection.y2" stroke="#026F91" stroke-width="1" marker-end="url(#arrowhead)"
          stroke-dasharray="5,5" class="glowing-line" />
      </svg>
      <div v-for="node in treeData.filter((n) => n.nodeType !== 1)" :key="node.id"
        :style="nodeStyle(JSON.parse(node.pageExtend))" :class="{ active: node.active }" class="node">
        <div class="node-content">
          <img :src="getNodeImage(node)" alt="节点图标" />
        </div>
        <div class="nodeTypeName" :class="{
          nodeTypeNameActive:
            node.nodeType == 6 || node.nodeType == 8 || node.nodeType == 10,
        }">
          <div v-if="node.nodeType == 10" style="font-weight: bold;overflow: hidden;">
            <!-- <span>充电桩</span>
            <span>({{ node.dataNum1 }})</span>|| -->
            <span>充电枪</span>
            <span>({{ node.dataNum2 }})</span>
          </div>
          <!-- <div v-if="node.nodeType == 10" style="font-weight: bold; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
            <el-tooltip :content="`${node.nodeName}（${node.dataNum1}）|| 充电枪（${node.dataNum2}）`" placement="top">
              <span>
                {{ node.nodeName }}（{{ node.dataNum1 }}）|| 充电枪（{{ node.dataNum2 }}）
              </span>
            </el-tooltip>
          </div> -->

          <div v-else>
            <span style="font-weight: bold">{{ node.nodeName }}</span>
            <span v-if="
              node.nodeType == 6 || node.nodeType == 8 || node.nodeType == 10
            ">（{{ node.dataNum1 }}）</span>
          </div>
        </div>
        <div class="nodeTypeNumber" v-if="node.nodeType == '7'">
          <div>
            容量（kWp）：<span class="styleNode">{{
              JSON.parse(node.reaObject).photoRl
            }}</span>
          </div>
        </div>
        <div class="nodeTypeNumber">
          <!-- 上 -->
          <div v-for="(item, index) in node?.siteTopItemList.filter(
            (i) => i.showType === 1 && i.positionType === 1
          )" :style="{ top: '-90px', marginTop: `${index * -30}px` }" :key="item.id" class="typeItem move-up">
            <div style="text-align: right; white-space: nowrap">
              <el-tooltip :content="item.showName" effect="dark" placement="bottom">
                {{ truncateText(item.showName) }}
              </el-tooltip>
            </div>
            ：
            <span style="color: #01a1ff">
              {{ item.dataValue ? item.dataValue : "--" }}
            </span>
          </div>
          <!-- 下 -->
          <div v-for="(item, index) in node?.siteTopItemList.filter(
            (i) => i.showType === 1 && i.positionType === 2
          )" :key="item.id" class="typeItem move-down" :style="{ marginTop: `${index * 20}px` }">
            <div style="text-align: center; white-space: nowrap">
              <el-tooltip :content="item.showName" effect="dark" placement="bottom">
                {{ truncateText(item.showName) }}
              </el-tooltip>
            </div>
            ：
            <span style="color: #01a1ff">
              {{ item.dataValue ? item.dataValue : "--" }}
            </span>
          </div>
          <!-- 左 -->
          <div v-for="(item, index) in node?.siteTopItemList.filter(
            (i) => i.showType === 1 && i.positionType === 3
          )" :style="{ top: '-70px', marginTop: `${index * 30}px` }" :key="item.id" class="typeItem move-left">
            <div style="text-align: right; white-space: nowrap">
              <el-tooltip :content="item.showName" effect="dark" placement="bottom">
                {{ truncateText(item.showName) }}
              </el-tooltip>
            </div>
            ：
            <span style="color: #01a1ff">
              {{ item.dataValue ? item.dataValue : "--" }}
            </span>
          </div>
          <!--右  -->
          <div v-for="(item, index) in node?.siteTopItemList.filter(
            (i) => i.showType === 1 && i.positionType === 4
          )" :style="{ top: '-70px', marginTop: `${index * 30}px` }" :key="item.id" class="typeItem move-right">
            <div style="text-align: right; white-space: nowrap">
              <el-tooltip :content="item.showName" effect="dark" placement="bottom">
                {{ truncateText(item.showName) }}
              </el-tooltip>
            </div>
            ：
            <span style="color: #01a1ff">
              {{ item.dataValue ? item.dataValue : "--" }}
            </span>
          </div>
        </div>
      </div>

    </div>

  </div>
</template>

<script setup>
import TBJD from "@/assets/image/station-details/TBJD.png";
import DWJD from "@/assets/image/station-details/DWJD.png";
import BYQ from "@/assets/image/station-details/BYQ.png";
import GKJD from "@/assets/image/station-details/GWJD.png";
import JLJD from "@/assets/image/station-details/JLJD.png";
import NBQ from "@/assets/image/station-details/NBQJD.png";
import GF from "@/assets/image/station-details/GFZJ.png";
import CNG from "@/assets/image/station-details/CNGJD.png";
import FHJD from "@/assets/image/station-details/FHJD.png";
import CDZ from "@/assets/image/station-details/CDZ.png";
import KGJD from "@/assets/image/station-details/KGJD.png";
import DDQC from "@/assets/image/station-details/DDQC.png";
import emptyImg from "@/assets/image/empty.png";
import HDZJD from "@/assets/image/station-details/HDZJD.png";
import { ref, computed, onMounted, nextTick, inject, watch } from "vue";
import { findSiteTopDataListBySiteId } from "@/api/monitoringCenter/monitoringCenter";
import { useRoute } from "vue-router";
const siteId = inject("siteId");
const route = useRoute();
const canvas = ref(null);

// 模拟数据
const treeData = ref([]);
const nodeType = ref([
  { id: 1, name: "拓扑点", image: TBJD },
  { id: 2, name: "电网", image: DWJD },
  { id: 3, name: "变压器", image: BYQ },
  { id: 4, name: "关口点", image: GKJD },
  { id: 5, name: "计量点", image: JLJD },
  { id: 6, name: "逆变器", image: NBQ },
  { id: 7, name: "光伏组件", image: GF },
  { id: 8, name: "储能柜", image: CNG },
  { id: 9, name: "负荷", image: FHJD },
  { id: 10, name: "充电桩", image: CDZ },
  { id: 11, name: "开关", image: KGJD },
  { id: 12, name: "车辆", image: DDQC },
  { id: 13, name: "换电站", image: HDZJD },
]);
const scrollLeft = ref(null);
const scrollTop = ref(null);
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
// 保存初始的变换状态
const initialTransform = ref({
  left: 0,
  top: 0,
  transform: '',
  transformOrigin: ''
});
const resetCanvas = () => {
  if (!canvas.value) return;
  console.log('resetCanvas', initialTransform.value);
  // 平滑过渡效果
  canvas.value.style.transition = 'all 0.3s ease';

  // 恢复到初始状态
  canvas.value.style.right = initialTransform.value.left + 'px';
  canvas.value.style.left = '';
  canvas.value.style.top = '';

  canvas.value.style.bottom = initialTransform.value.top + 'px';
  canvas.value.style.transform = initialTransform.value.transform;
  canvas.value.style.transformOrigin = initialTransform.value.transformOrigin;

  // 过渡结束后移除过渡效果
  setTimeout(() => {
    if (canvas.value) {
      canvas.value.style.transition = '';
    }
  }, 300);

};

// --------------------------------
const nodeStyle = (styleObj) => {
  return {
    left: styleObj.x + "px",
    top: styleObj.y + "px",
    width: 40 + "px",
    height: 40 + "px",
  };
};
const truncateText = (str) => {
  return str.length > 20 ? str.slice(0, 20) + "..." : str;
};
const fetchData = () => {
  return findSiteTopDataListBySiteId({ siteId: siteId.value }).then((res) => {
    treeData.value = res.data;
    return res.data; // 可选：返回数据用于后续处理
  });
};

// 计算所有连线
const connections = computed(() => {
  const result = [];
  // 遍历所有节点
  treeData.value.forEach((node) => {
    const nodeData = JSON.parse(node.pageExtend);
    const {
      x: nodeX,
      y: nodeY,
      width: nodeWidth,
      height: nodeHeight,
    } = nodeData;
    // 如果该节点有 parentId，则查找父节点
    if (nodeData.parentId) {
      const parentNode = treeData.value.find((n) => n.id === nodeData.parentId);
      if (parentNode) {
        const parentData = JSON.parse(parentNode.pageExtend);
        const {
          x: parentX,
          y: parentY,
          width: parentWidth,
          height: parentHeight,
        } = parentData;
        let x1, y1, x2, y2;
        if (parentX < nodeX) {
          // 左侧连接

          x1 = nodeX + 28;
          y1 = nodeY - 5 + nodeHeight / 2;
          x2 = parentX - 28 + parentWidth;
          y2 = parentY - 5 + parentHeight / 2;
        } else if (parentX > nodeX) {
          // 右侧连接
          x1 = nodeX - 28 + nodeWidth;
          y1 = nodeY - 5 + nodeHeight / 2;
          x2 = parentX + 28;
          y2 = parentY - 5 + parentHeight / 2;
        } else {
          // 上下连接
          x1 = parentX - 5 + parentWidth / 2; // 父节点中心 X
          y1 = parentY - 28 + parentHeight; // 父节点底部向下 20px
          x2 = nodeX - 5 + nodeWidth / 2; // 子节点中心 X
          y2 = nodeY + 28; // 子节点顶部
        }
        result.push({
          id: `conn-${node.id}-${nodeData.parentId}`,
          x1,
          y1,
          x2,
          y2,
        });
      }
    }
  });

  return result;
});

// 过滤出所有 siteTopItemList 中 showType === 1 的项
const filteredItems = computed(() => {
  return treeData.value
    .flatMap((node) => node.siteTopItemList)
    .filter((item) => item.showType === 1);
});
const getNodeImage = (node) => {
  const type = nodeType.value.find((item) => item.id === node.nodeType);
  return type.image;
};
// 判断某个 node 是否有 positionType 为 1/3/4 的项
const hasTopItem = (node) => {
  return node.siteTopItemList.some(
    (item) => item.showType === 1 && item.positionType === 1
  );
};

const hasLeftItem = (node) => {
  return node.siteTopItemList.some(
    (item) => item.showType === 1 && item.positionType === 3
  );
};

const hasRightItem = (node) => {
  return node.siteTopItemList.some(
    (item) => item.showType === 1 && item.positionType === 4
  );
};

// 获取所有 .node 中的 .typeItem.move-down 的 margin-top 最大值
function getMaxMoveDownMarginTop () {
  const nodes = document.querySelectorAll(".node");
  if (!nodes.length) return null;

  let maxMarginTop = 0;

  nodes.forEach((node) => {
    const moveDownItems = node.querySelectorAll(".typeItem.move-down");
    moveDownItems.forEach((item) => {
      const marginTop = parseFloat(item.style.marginTop);
      if (!isNaN(marginTop)) {
        maxMarginTop = Math.max(maxMarginTop, marginTop);
      }
    });
  });

  return maxMarginTop * 2 + 20;
}
// 获取所有 .node 中的 .typeItem.move-down 的 margin-top 最小值
function getMaxMoveUpMarginTop () {
  const nodes = document.querySelectorAll(".node");
  if (!nodes.length) return null;

  let maxMarginTop = 0;

  nodes.forEach((node) => {
    const moveDownItems = node.querySelectorAll(".typeItem.move-up");
    moveDownItems.forEach((item) => {
      const marginTop = parseFloat(item.style.marginTop);
      if (!isNaN(marginTop)) {
        maxMarginTop = Math.min(maxMarginTop, marginTop);
      }
    });
  });

  return maxMarginTop * 2 - 20;
}
watch(
  siteId,
  () => {
    fetchData().then(() => {
      if (canvas.value) {
        nextTick(() => {
          requestAnimationFrame(() => {
            const nodes = document.querySelectorAll(".node");
            if (nodes.length === 0) return;

            const positions = Array.from(nodes).map((node) => ({
              x: parseFloat(node.style.left),
              y: parseFloat(node.style.top),
            }));
            const maxMarginTop = getMaxMoveDownMarginTop();
            const maxMarginbottom = getMaxMoveUpMarginTop();
            const minX = Math.min(...positions.map((p) => p.x)) - 300;
            const maxX = Math.max(...positions.map((p) => p.x)) + 200;
            const minY =
              Math.min(...positions.map((p) => p.y)) + maxMarginbottom;
            const maxY =
              Math.max(...positions.map((p) => p.y)) + 70 + maxMarginTop;

            const width = maxX - minX;
            const height = maxY - minY;

            const containerWidth = canvas.value.clientWidth;
            const containerHeight = canvas.value.clientHeight;
            // 判断是否需要缩放
            const scaleX = containerWidth / width;
            const scaleY = containerHeight / height;
            const scale = Math.min(scaleX, scaleY);
            if (scale < 1) {
              const originalCenterX = (minX + maxX) / 2;
              const originalCenterY = (minY + maxY) / 2;

              const scaledCenterX = originalCenterX * scale;
              const scaledCenterY = originalCenterY * scale;

              const canvasCenterX = containerWidth / 2;
              const canvasCenterY = containerHeight / 2;

              const offsetX = canvasCenterX - scaledCenterX;
              const offsetY = canvasCenterY - scaledCenterY;
              initialTransform.value = {
                left: 0,
                top: 0,
                transform: `scale(${scale}) translate(${offsetX}px, ${offsetY}px)`,
                transformOrigin: "25% 0"
              };
              canvas.value.style.transform = `scale(${scale}) translate(${offsetX}px, ${offsetY}px)`;
              canvas.value.style.transformOrigin = "25% 0";
            } else {
              // 内容不超出，直接居中
              scrollLeft.value = (minX + maxX) / 2 - containerWidth / 2;
              scrollTop.value = (minY + maxY) / 2 - containerHeight / 2;
              canvas.value.scrollLeft = scrollLeft;
              canvas.value.scrollTop = scrollTop;
              // 保存初始状态
              initialTransform.value = {
                left: scrollLeft,
                top: scrollTop,
                transform: '',
                transformOrigin: ''
              };
            }
          });
        });
      }
    });
  },
  { immdiate: true }
);
const getfetchData = () => {
  fetchData().then(() => {
    nextTick(() => {
      requestAnimationFrame(() => {
        const nodes = document.querySelectorAll(".node");
        if (nodes.length === 0) return;

        const positions = Array.from(nodes).map((node) => ({
          x: parseFloat(node.style.left),
          y: parseFloat(node.style.top),
        }));
        const maxMarginTop = getMaxMoveDownMarginTop();
        const maxMarginbottom = getMaxMoveUpMarginTop();
        const minX = Math.min(...positions.map((p) => p.x)) - 300;
        const maxX = Math.max(...positions.map((p) => p.x)) + 200;
        const minY = Math.min(...positions.map((p) => p.y)) + maxMarginbottom;
        const maxY = Math.max(...positions.map((p) => p.y)) + 70 + maxMarginTop;

        const width = maxX - minX;
        const height = maxY - minY;

        const containerWidth = canvas.value.clientWidth;
        const containerHeight = canvas.value.clientHeight;
        // 判断是否需要缩放
        const scaleX = containerWidth / width;
        const scaleY = containerHeight / height;
        const scale = Math.min(scaleX, scaleY);
        if (scale < 1) {
          const originalCenterX = (minX + maxX) / 2;
          const originalCenterY = (minY + maxY) / 2;

          const scaledCenterX = originalCenterX * scale;
          const scaledCenterY = originalCenterY * scale;

          const canvasCenterX = containerWidth / 2;
          const canvasCenterY = containerHeight / 2;

          const offsetX = canvasCenterX - scaledCenterX;
          const offsetY = canvasCenterY - scaledCenterY;

          canvas.value.style.transform = `scale(${scale}) translate(${offsetX}px, ${offsetY}px)`;
          canvas.value.style.transformOrigin = "25% 0";
          initialTransform.value = {
            left: 0,
            top: 0,
            transform: `scale(${scale}) translate(${offsetX}px, ${offsetY}px)`,
            transformOrigin: "25% 0"
          };
        } else {
          // 内容不超出，直接居中
          scrollLeft.value = (minX + maxX) / 2 - containerWidth / 2;
          scrollTop.value = (minY + maxY) / 2 - containerHeight / 2;
          canvas.value.scrollLeft = scrollLeft;
          canvas.value.scrollTop = scrollTop;
          // 保存初始状态
          initialTransform.value = {
            left: scrollLeft,
            top: scrollTop,
            transform: '',
            transformOrigin: ''
          };
        }
      });
    });
  });
};
onMounted(() => {
  // 假设你已经通过 fetchData 获取了 treeData 数据
  getfetchData();
});
</script>

<style scoped>
.container {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.reset-button {
  position: absolute;
  bottom: 20px;
  right: 20px;
  z-index: 10;


}

.empty-container {
  display: flex;
  justify-content: center;
  /* 水平居中 */
  align-items: center;
  /* 垂直居中 */
  width: 100%;
  height: 100%;
}

.topology-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.topology-canvas {
  z-index: 1;
  position: absolute;
  width: 100%;
  height: 100%;
  /* pointer-events: none; */
  /* 关键点 */
  /* overflow-x: auto; */
  /* 启用横向滚动 */
  /* overflow: scroll; */
}

.add-button {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 10;
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
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

.custom-color {
  color: white !important;
}

.node {
  position: absolute;
  /* background-color: white; */
  border: 2px solid #2583be;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: move;
  z-index: 2;
  width: 40px;
  height: 40px;
}

.node.active {
  border: 2px dashed RGBA(56, 161, 247, 0.5);
}

/* 添加在style部分 */
.node {
  transition: all 0.3s ease;
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

.glowing-line {
  stroke: #026f91;
  /* 线条颜色 */
  stroke-width: 1;
  /* 线条宽度 */
  filter: drop-shadow(2px 2px 4px rgba(170, 169, 163, 0.7));
  /* 添加发光阴影 */
  /* 添加阴影 */
}

.nodeTypeName {
  text-align: center;
  padding-top: 8px;
  font-size: 12px;
  white-space: nowrap;
  position: absolute;
  width: 110px;
  left: -40px;
}

.nodeTypeNameActive {
  color: #01a1ff;
}

.nodeTypeNumber {
  font-size: 12px;
  position: absolute;
  white-space: nowrap;
  width: 130px;
  left: -40px;
  text-align: center;
  top: 160%;
}

.typeItem {
  display: flex;
  padding-top: 5px;
  text-align: center;
  position: absolute;
  /* 必须设置为非 static */
  display: inline-block;
  /* 块级内联元素 */
  white-space: nowrap;
}

.move-up {
  top: auto;
  /* bottom: 80px; */
  /* 距离 .node 底部 10px */
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.move-left {
  /* top: auto; */
  /* top: 10px;
  bottom: 44px;
  left: -140px;
  width: 100px;
  text-align: right; */
  left: -100px;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
}

.move-right {
  right: -100px;
  text-align: left;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 100%;
}

.move-down {
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.styleNode {
  font-size: 14px;
  color: #fff;

  text-shadow: 0 0 2px #000,
    /* 黑色内阴影 */
    0 0 5px #00ffff;
  /* 青色外发光 */
}
</style>