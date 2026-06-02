<template>
  <!-- 整个 container 可拖动 + 滚轮缩放 -->
  <div class="container" @wheel="handleWheel" @mousedown="startDrag" @mousemove="onDrag" @mouseup="stopDrag" @mouseleave="stopDrag">
    <el-button class="reset-button" @click="resetCanvas">重置</el-button>

    <el-empty class="empty-container" :image="emptyImg" v-if="treeData.length == 0" description="暂无数据" />

    <div 
      class="topology-canvas" 
      v-else 
      ref="canvas"
      :style="canvasStyle"
    >
      <svg class="lines-container">
        <line 
          v-for="connection in connections" 
          :key="connection.id" 
          :x1="connection.x1" 
          :y1="connection.y1"
          :x2="connection.x2" 
          :y2="connection.y2" 
          stroke="#026F91" 
          stroke-width="1" 
          marker-end="url(#arrowhead)"
          stroke-dasharray="5,5" 
          class="glowing-line" 
        />
      </svg>

      <div 
        v-for="node in treeData.filter((n) => n.nodeType !== 1)" 
        :key="node.id"
        :style="nodeStyle(JSON.parse(node.pageExtend))" 
        :class="{ active: node.active }" 
        class="node"
      >
        <div class="node-content">
          <img :src="getNodeImage(node)" alt="节点图标" />
        </div>
        <div class="nodeTypeName" :class="{
          nodeTypeNameActive: node.nodeType == 6 || node.nodeType == 8 || node.nodeType == 10,
        }">
          <div v-if="node.nodeType == 10" style="font-weight: bold;overflow: hidden;">
            <span>充电枪</span>
            <span>({{ node.dataNum2 }})</span>
          </div>
          <div v-else>
            <span style="font-weight: bold">{{ node.nodeName }}</span>
            <span v-if="node.nodeType == 6 || node.nodeType == 8 || node.nodeType == 10">（{{ node.dataNum1 }}）</span>
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
          <div 
            v-for="(item, index) in node?.siteTopItemList.filter(i => i.showType === 1 && i.positionType === 1)" 
            :style="{ top: '-90px', marginTop: `${index * -30}px` }" 
            :key="item.id" 
            class="typeItem move-up"
          >
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
          <div 
            v-for="(item, index) in node?.siteTopItemList.filter(i => i.showType === 1 && i.positionType === 2)" 
            :key="item.id" 
            class="typeItem move-down" 
            :style="{ marginTop: `${index * 20}px` }"
          >
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
          <div 
            v-for="(item, index) in node?.siteTopItemList.filter(i => i.showType === 1 && i.positionType === 3)" 
            :style="{ top: '-70px', marginTop: `${index * 30}px` }" 
            :key="item.id" 
            class="typeItem move-left"
          >
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
          <div 
            v-for="(item, index) in node?.siteTopItemList.filter(i => i.showType === 1 && i.positionType === 4)" 
            :style="{ top: '-70px', marginTop: `${index * 30}px` }" 
            :key="item.id" 
            class="typeItem move-right"
          >
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

// ============== 全屏自由拖拽 + 缩放核心 ==============
const scale = ref(1);
const offsetX = ref(0);
const offsetY = ref(0);
const isDragging = ref(false);

let startX = 0;
let startY = 0;
let lastOffsetX = 0;
let lastOffsetY = 0;

const initialState = {
  scale: 1,
  x: 0,
  y: 0,
};

const canvasStyle = computed(() => {
  return {
    transform: `translate(${offsetX.value}px, ${offsetY.value}px) scale(${scale.value})`,
    transformOrigin: "0 0",
  };
});

// 滚轮缩放
const handleWheel = (e) => {
  e.preventDefault();
  const delta = e.deltaY > 0 ? -0.1 : 0.1;
  const newScale = Math.max(0.2, Math.min(5, scale.value + delta));
  const rect = canvas.value.getBoundingClientRect();
  const mouseX = e.clientX - rect.left;
  const mouseY = e.clientY - rect.top;
  const ratio = newScale / scale.value;

  offsetX.value = mouseX - ratio * (mouseX - offsetX.value);
  offsetY.value = mouseY - ratio * (mouseY - offsetY.value);
  scale.value = newScale;
};

// 开始拖拽
const startDrag = (e) => {
  if (e.target.closest(".node") || e.target.closest(".reset-button")) return;
  isDragging.value = true;
  startX = e.clientX;
  startY = e.clientY;
  lastOffsetX = offsetX.value;
  lastOffsetY = offsetY.value;
};

// 拖拽中
const onDrag = (e) => {
  if (!isDragging.value) return;
  const dx = e.clientX - startX;
  const dy = e.clientY - startY;
  offsetX.value = lastOffsetX + dx;
  offsetY.value = lastOffsetY + dy;
};

// 停止拖拽
const stopDrag = () => {
  isDragging.value = false;
};

// 重置
const resetCanvas = () => {
  scale.value = initialState.scale;
  offsetX.value = initialState.x;
  offsetY.value = initialState.y;
};
// ======================================================

const nodeStyle = (styleObj) => {
  return {
    left: styleObj.x + "px",
    top: styleObj.y + "px",
    width: "40px",
    height: "40px",
  };
};

const truncateText = (str) => {
  return str.length > 20 ? str.slice(0, 20) + "..." : str;
};

const fetchData = () => {
  return findSiteTopDataListBySiteId({ siteId: siteId.value }).then((res) => {
    treeData.value = res.data;
  });
};

const connections = computed(() => {
  const result = [];
  treeData.value.forEach((node) => {
    const nodeData = JSON.parse(node.pageExtend);
    const { x: nodeX, y: nodeY, width: nodeW, height: nodeH } = nodeData;
    if (nodeData.parentId) {
      const p = treeData.value.find((n) => n.id === nodeData.parentId);
      if (p) {
        const pd = JSON.parse(p.pageExtend);
        const { x: px, y: py, width: pw, height: ph } = pd;
        let x1, y1, x2, y2;
        if (px < nodeX) {
          x1 = nodeX + 28; y1 = nodeY - 5 + nodeH / 2;
          x2 = px - 28 + pw; y2 = py - 5 + ph / 2;
        } else if (px > nodeX) {
          x1 = nodeX - 28 + nodeW; y1 = nodeY - 5 + nodeH / 2;
          x2 = px + 28; y2 = py - 5 + ph / 2;
        } else {
          x1 = px - 5 + pw / 2; y1 = py - 28 + ph;
          x2 = nodeX - 5 + nodeW / 2; y2 = nodeY + 28;
        }
        result.push({ id: `conn-${node.id}`, x1, y1, x2, y2 });
      }
    }
  });
  return result;
});

const getNodeImage = (node) => {
  const t = nodeType.value.find((item) => item.id === node.nodeType);
  return t ? t.image : "";
};

function getMaxMoveDownMarginTop() {
  let max = 0;
  document.querySelectorAll(".typeItem.move-down").forEach((it) => {
    const m = parseFloat(it.style.marginTop);
    if (!isNaN(m)) max = Math.max(max, m);
  });
  return max * 2 + 20;
}

function getMaxMoveUpMarginTop() {
  let min = 0;
  document.querySelectorAll(".typeItem.move-up").forEach((it) => {
    const m = parseFloat(it.style.marginTop);
    if (!isNaN(m)) min = Math.min(min, m);
  });
  return min * 2 - 20;
}

const getfetchData = () => {
  fetchData().then(() => {
    nextTick(() => {
      requestAnimationFrame(() => {
        const nodes = document.querySelectorAll(".node");
        if (!nodes.length) return;
        const pos = Array.from(nodes).map(n => ({
          x: parseFloat(n.style.left),
          y: parseFloat(n.style.top)
        }));
        const maxD = getMaxMoveDownMarginTop();
        const maxU = getMaxMoveUpMarginTop();
        const minX = Math.min(...pos.map(p => p.x)) - 200;
        const maxX = Math.max(...pos.map(p => p.x)) + 200;
        const minY = Math.min(...pos.map(p => p.y)) + maxU;
        const maxY = Math.max(...pos.map(p => p.y)) + 70 + maxD;
        const w = maxX - minX;
        const h = maxY - minY;
        const cw = canvas.value.clientWidth;
        const ch = canvas.value.clientHeight;
        const s = Math.min(cw / w, ch / h);

        if (s < 1) {
          const cx = (minX + maxX) / 2;
          const cy = (minY + maxY) / 2;
          const ox = cw / 2 - cx * s;
          const oy = ch / 2 - cy * s;
          initialState.scale = s;
          initialState.x = ox;
          initialState.y = oy;
          scale.value = s;
          offsetX.value = ox;
          offsetY.value = oy;
        } else {
          initialState.scale = 1;
          initialState.x = (minX + maxX) / 2 - cw / 2;
          initialState.y = (minY + maxY) / 2 - ch / 2;
          scale.value = 1;
          offsetX.value = initialState.x;
          offsetY.value = initialState.y;
        }
      });
    });
  });
};

watch(siteId, () => {
  getfetchData();
}, { immediate: true });

onMounted(() => {
  getfetchData();
});
</script>

<style scoped>
.container {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  user-select: none;
  cursor: grab;
}
.container:active {
  cursor: grabbing;
}

.reset-button {
  position: absolute;
  bottom: 20px;
  right: 20px;
  z-index: 10;
  cursor: pointer;
}

.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.topology-canvas {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1;
  will-change: transform;
}

.lines-container {
  position: absolute;
  top: 0;
  left: 0;
  pointer-events: none;
  z-index: 1;
  overflow: visible;
}

.node {
  position: absolute;
  border: 2px solid #2583be;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: move;
  z-index: 2;
  width: 40px;
  height: 40px;
  transition: all 0.3s ease;
}
.node.active {
  border: 2px dashed RGBA(56, 161, 247, 0.5);
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
  stroke-width: 1;
  filter: drop-shadow(2px 2px 4px rgba(170, 169, 163, 0.7));
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
  display: inline-block;
  white-space: nowrap;
}

.move-up {
  top: auto;
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.move-left {
  left: -110px;
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
  text-shadow: 0 0 2px #000, 0 0 5px #00ffff;
}
</style>