<template>
  <div class="operationLogDialog">
    <div class="dialog_main">
      <div class="dialog_main-node" ref="flowContainer">
        <div class="top-row">
          <div class="flow-node-circle node-same" ref="firstCircleNode"></div>

          <!-- 创建巡检任务节点 -->
          <div class="flow-node-Rectangle node-same" ref="createTaskNode"
            :class="{ 'selected-node': selectedNode, 'selected-node-no': !selectedNode, seleectTrue: selectType === '1' }">
            <div v-if="selectedNode" class="salc-center"></div>
            <div class="node-label">创建巡检任务</div>
          </div>

          <div class="flow-node-Rectangle node-same" ref="startInspectionNode" @click="selectedNode && handleClick('1',startInspection)"
            :class="{ 'selected-node': selectedNode, 'selected-node-no': !selectedNode, seleectTrue: selectType === '2' }">
            <div v-if="selectedNode" class="salc-center">{{startInspection===inspectionTotal?'全部':startInspection}}</div>
            <div class="node-label">启动巡检</div>
          </div>

          <!-- 旋转加号节点 -->
          <div class="flow-node-roate" ref="rotatePlusNode">
            <el-icon>
              <CloseBold size="15" />
            </el-icon>
          </div>

          <div class="flow-node-Rectangle node-same" ref="siteInspectionNode" @click="selectedNode && handleClick('2',OnsiteInspeciton)"
            :class="{ 'selected-node': selectedNode, 'selected-node-no': !selectedNode, seleectTrue: selectType === '3' }">
            <div v-if="selectedNode" class="salc-center">{{OnsiteInspeciton===inspectionTotal?'全部':OnsiteInspeciton}}</div>
            <div class="node-label">现场巡检</div>
          </div>

          <div class="flow-node-Rectangle node-same" ref="inspectionResultNode" @click="selectedNode && handleClick('3',resultInspection)"
            :class="{ 'selected-node': selectedNode, 'selected-node-no': !selectedNode, seleectTrue: selectType === '4' }">
            <div v-if="selectedNode" class="salc-center">{{resultInspection===inspectionTotal?'全部':resultInspection}}</div>
            <div class="node-label">巡检结果确认</div>
          </div>
        </div>
        <!-- 第二行节点（最后一个圆形节点） -->
        <div class="bottom-row">

          <div class="flow-node-circle node-same" ref="bottomCircleNode"></div>
          <div class="flow-node-roate bottom-rotate" ref="bottomRotateNode">
            <el-icon>
              <CloseBold size="15" />
            </el-icon>
          </div>
          <div class="flow-node-circle-none" ref="bottomCircleNode5"></div>
        </div>
      </div>
    </div>
    <selectUsers ref="selectUsersRef" :nodeType="nodeType" :isUserVisible="dialog_visible" @saveUserDialog="saveUserDialog" :selectUserLength="selectUserLength"
      @saveConfirmUserDialog="saveConfirmUserDialog"></selectUsers>

  </div>
</template>

<script setup>
import { CloseBold } from '@element-plus/icons-vue'
import selectUsers from './selectUsers.vue'
import { ref, onMounted, nextTick, onUnmounted, watch, computed } from 'vue'
import { getInspectionUserList, saveInspectionUser } from "@/api/assetManagement/inspection";
import SystemsetController from "@/api/system/index";
import { useStore } from "vuex";
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  selectedNode: {
    type: Boolean,
    default: false
  },
  selectType: {
    type: String,
    default: ''
  }

})
const dialog_visible = ref(false)
const selectUsersRef = ref(null)


// 所有节点引用
const firstCircleNode = ref(null)
const createTaskNode = ref(null)
const startInspectionNode = ref(null)
const rotatePlusNode = ref(null)
const siteInspectionNode = ref(null)
const inspectionResultNode = ref(null)
const bottomCircleNode = ref(null)
const bottomCircleNode5 = ref(null)
const bottomRotateNode = ref(null)
const flowContainer = ref(null)
const nodeType = ref(null)
const startInspection = ref(0) //启动巡检
const OnsiteInspeciton = ref(0) // 现场巡检
const resultInspection = ref(0) // 巡检结果确认
const selectUserLength = ref(0)
const inspectionTotal = ref(localStorage.getItem('userListLength'))

const handleClick = (val, value) => {
  dialog_visible.value = true
  nodeType.value = val
  selectUserLength.value = value

}
const saveUserDialog = () => {
  dialog_visible.value = false

}
const saveConfirmUserDialog = () => {
  dialog_visible.value = false
  getQuerySelectUserList()
}
// 防抖函数
const debounce = (func, wait) => {
  let timeout;
  return function executedFunction (...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

// 计算基于容器高度的动态值
const getDynamicHeight = (containerHeight) => {
  return containerHeight * 0.15; // 使用容器高度的15%作为向上延伸的高度
}


// 绘制连接线
const drawConnectionLine = () => {
  nextTick(() => {
    const container = flowContainer.value
    if (!container) return

    // 清除之前的连接线和箭头
    const existingElements = container.querySelectorAll('.custom-connection-line, .line-arrow')
    existingElements.forEach(el => el.remove())

    const containerRect = container.getBoundingClientRect()
    const containerHeight = containerRect.height

    // 绘制第一行节点之间的连接线
    const nodes = [
      firstCircleNode.value,
      createTaskNode.value,
      startInspectionNode.value,
      rotatePlusNode.value,
      siteInspectionNode.value,
      inspectionResultNode.value,
    ]

    // 绘制节点之间的连接线
    for (let i = 0; i < nodes.length - 1; i++) {
      const startNode = nodes[i]
      const endNode = nodes[i + 1]

      if (!startNode || !endNode) continue

      const startRect = startNode.getBoundingClientRect()
      const endRect = endNode.getBoundingClientRect()

      // 计算相对位置
      const startX = startRect.right - containerRect.left
      const startY = startRect.top + startRect.height / 2 - containerRect.top
      const endX = endRect.left - containerRect.left
      const endY = endRect.top + endRect.height / 2 - containerRect.top

      // 计算线的长度和角度
      const length = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2))
      const angle = Math.atan2(endY - startY, endX - startX) * 180 / Math.PI

      // 创建连接线
      const connectionLine = document.createElement('div')
      connectionLine.className = 'custom-connection-line'

      // 设置连接线样式
      connectionLine.style.cssText = ` 
        position: absolute;
        left: ${startX}px;
        top: ${startY}px;
        width: ${length}px;
        height: 1px;
        background: #ffffff;
        transform-origin: 0 0;
        transform: rotate(${angle}deg);
        z-index: 1;
      `

      // 添加箭头
      const arrow = document.createElement('div')
      arrow.className = 'line-arrow'
      arrow.style.cssText = `
        position: absolute;
        left: ${startX + length - 4}px;
        top: ${startY - 3}px;
        width: 0;
        height: 0;
        border-left: 4px solid #fff;
        border-top: 4px solid transparent;
        border-bottom: 4px solid transparent;
        transform: rotate(${angle}deg);
        transform-origin: center;
        z-index: 2;
      `

      container.appendChild(connectionLine)
      container.appendChild(arrow)
    }

    // 绘制从rotatePlusNode向上延伸至createTaskNode上方的连接线
    if (rotatePlusNode.value && createTaskNode.value) {
      const rotateRect = rotatePlusNode.value.getBoundingClientRect()
      const createTaskRect = createTaskNode.value.getBoundingClientRect()

      // 使用动态计算的高度
      const upHeight = getDynamicHeight(containerHeight)

      // 向上延伸的竖线
      const upLine = document.createElement('div')
      upLine.className = 'custom-connection-line'
      upLine.style.cssText = `
        position: absolute;
        left: ${rotateRect.left + rotateRect.width / 2 - containerRect.left}px;
        top: ${rotateRect.top - containerRect.top - upHeight}px;
        width: 1px;
        height: ${upHeight}px;
        background: #ffffff;
        z-index: 1;
      `
      container.appendChild(upLine)

      // 计算水平线的长度
      const horizontalLength = Math.abs(
        (rotateRect.left + rotateRect.width / 2) -
        (createTaskRect.left + createTaskRect.width / 2)
      )

      // 水平线
      const horizontalLine = document.createElement('div')
      horizontalLine.className = 'custom-connection-line'
      horizontalLine.style.cssText = `
        position: absolute;
        left: ${Math.min(
        rotateRect.left + rotateRect.width / 2,
        createTaskRect.left + createTaskRect.width / 2
      ) - containerRect.left}px;
        top: ${rotateRect.top - containerRect.top - upHeight}px;
        width: ${horizontalLength}px;
        height: 1px;
        background: #ffffff;
        z-index: 1;
      `
      container.appendChild(horizontalLine)

      // 计算向下延伸的实际高度（从水平线到createTaskNode顶部）
      const downHeight = (createTaskRect.top - containerRect.top) - (rotateRect.top - containerRect.top - upHeight)

      // 向下延伸的竖线 - 修复高度计算
      const downLine = document.createElement('div')
      downLine.className = 'custom-connection-line'
      downLine.style.cssText = `
        position: absolute;
        left: ${createTaskRect.left + createTaskRect.width / 2 - containerRect.left}px;
        top: ${rotateRect.top - containerRect.top - upHeight}px;
        width: 1px;
        height: ${downHeight}px;
        background: #ffffff;
        z-index: 1;
      `
      container.appendChild(downLine)

      // 添加向下箭头
      const downArrow = document.createElement('div')
      downArrow.className = 'line-arrow'
      downArrow.style.cssText = `
        position: absolute;
        left: ${createTaskRect.left + createTaskRect.width / 2 - containerRect.left - 3}px;
        top: ${createTaskRect.top - containerRect.top - 4}px;
        width: 0;
        height: 0;
        border-top: 4px solid #fff;
        border-left: 4px solid transparent;
        border-right: 4px solid transparent;
        z-index: 2;
      `
      container.appendChild(downArrow)
    }

    // 绘制现场巡检节点到底部圆形节点的连接线
    if (siteInspectionNode.value && bottomCircleNode.value) {
      const rotateRect = siteInspectionNode.value.getBoundingClientRect()
      const bottomRect = bottomCircleNode.value.getBoundingClientRect()

      // 竖线
      const verticalLine = document.createElement('div')
      const vStartX = rotateRect.left + rotateRect.width / 2 - containerRect.left
      const vStartY = rotateRect.bottom - containerRect.top
      const vEndY = bottomRect.top - containerRect.top
      const vLength = vEndY - vStartY

      verticalLine.className = 'custom-connection-line vertical-line'
      verticalLine.style.cssText = `
        position: absolute;
        left: ${vStartX + 2}px;
        top: ${vStartY}px;
        width: 1px;
        height: ${vLength}px;
        background: #ffffff;
        z-index: 1;
      `
      container.appendChild(verticalLine)

      // 竖线箭头
      const verticalArrow = document.createElement('div')
      verticalArrow.className = 'line-arrow vertical-arrow'
      verticalArrow.style.cssText = `
        position: absolute;
        left: ${vStartX - 1}px;
        top: ${vStartY}px;
        width: 0;
        height: 0;
        border-bottom: 4px solid #fff;
        border-left: 4px solid transparent;
        border-right: 4px solid transparent;
        z-index: 2;
      `
      container.appendChild(verticalArrow)
    }

    // 绘制巡检结果节点到底部节点的连接线
    if (inspectionResultNode.value && bottomCircleNode5.value) {
      const rotateRect = inspectionResultNode.value.getBoundingClientRect()
      const bottomRect = bottomCircleNode5.value.getBoundingClientRect()

      // 竖线
      const verticalLine = document.createElement('div')
      const vStartX = rotateRect.left + rotateRect.width / 2 - containerRect.left
      const vStartY = rotateRect.bottom - containerRect.top
      const vEndY = bottomRect.top - containerRect.top
      const vLength = vEndY - vStartY

      verticalLine.className = 'custom-connection-line vertical-line'
      verticalLine.style.cssText = `
        position: absolute;
        left: ${vStartX}px;
        top: ${vStartY}px;
        width: 1px;
        height: ${vLength}px;
        background: #ffffff;
        z-index: 1;
      `
      container.appendChild(verticalLine)
    }

    // 绘制第二行节点之间的连接线
    const secondRowNodes = [
      bottomCircleNode.value,
      bottomRotateNode.value,
      bottomCircleNode5.value,
    ]

    // 绘制节点之间的连接线
    for (let i = 0; i < secondRowNodes.length - 1; i++) {
      const startNode = secondRowNodes[i]
      const endNode = secondRowNodes[i + 1]

      if (!startNode || !endNode) continue

      const startRect = startNode.getBoundingClientRect()
      const endRect = endNode.getBoundingClientRect()

      // 计算相对位置
      const startX = startRect.right - containerRect.left
      const startY = startRect.top + startRect.height / 2 - containerRect.top
      const endX = endRect.left - containerRect.left
      const endY = endRect.top + endRect.height / 2 - containerRect.top

      // 计算线的长度和角度
      const length = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2))
      const angle = Math.atan2(endY - startY, endX - startX) * 180 / Math.PI

      // 创建连接线
      const connectionLine = document.createElement('div')
      connectionLine.className = 'custom-connection-line'

      // 设置连接线样式
      connectionLine.style.cssText = ` 
        position: absolute;
        left: ${startX}px;
        top: ${startY}px;
        width: ${length}px;
        height: 1px;
        background: #ffffff;
        transform-origin: 0 0;
        transform: rotate(${angle}deg);
        z-index: 1;
      `

      // 添加箭头
      const arrow = document.createElement('div')
      arrow.className = 'line-arrow'

      arrow.style.cssText = `
        position: absolute;
        left: ${startX}px;
        top: ${startY - 3}px;
        width: 0;
        height: 0;
        border-right: 4px solid #fff;
        border-top: 4px solid transparent;
        border-bottom: 4px solid transparent;
        transform: rotate(${angle}deg);
        transform-origin: center;
        z-index: 2;
      `

      container.appendChild(connectionLine)
      container.appendChild(arrow)
    }
  })
}

// 创建防抖版本的绘制函数
const debouncedDrawConnectionLine = debounce(drawConnectionLine, 250);

// 暴露方法给父组件
defineExpose({
  drawConnectionLine
})
const getQuerySelectUserList = async () => {
  let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
  getInspectionUserList({ tenantId }).then((res) => {
    res.data.forEach(item => {
      if (item.type == 1) {
        startInspection.value = item.userNum ? item.userNum : 0
      } else if (item.type == 2) {
        OnsiteInspeciton.value = item.userNum ? item.userNum : 0
      } else if (item.type == 3) {
        resultInspection.value = item.userNum ? item.userNum : 0
      }
    })
  });
  // SystemsetController.findUserListByPage({
  //   page: 1,
  //   size: 10,
  //   userId,
  //   tenantId,
  // }).then((res) => { 
  //   inspectionTotal.value=res.data.totalSize
  // });
}
// 初始化
onMounted(async () => {
  // 等待DOM完全渲染
  await nextTick()
  drawConnectionLine()
  if (props.selectedNode) {
    getQuerySelectUserList()
  }

})

// 监听窗口大小变化 - 使用防抖版本
window.addEventListener('resize', debouncedDrawConnectionLine)

// 组件卸载时移除监听器
onUnmounted(() => {
  window.removeEventListener('resize', debouncedDrawConnectionLine);
})

</script>

<style lang="scss" scoped>
.operationLogDialog {
  .content {
    margin-bottom: 0 !important;
  }

  .dialog_main {
    height: 32vh;
    position: relative;

    .dialog_main-node {
      display: flex;
      flex-direction: column;
      justify-content: center;
      font-size: 12px;
      position: relative;
      height: 100%;
    }

    .top-row {
      display: flex;
      justify-content: space-around;
      align-items: center;
      width: 100%;
      position: relative;
    }

    .bottom-row {
      display: flex;
      align-items: center;
      width: 100%;
      position: relative;
      margin-top: 9%;

      .flow-node-circle {
        margin-left: 54.5%;
      }

      .flow-node-roate {
        margin-left: 13%;
      }

      .flow-node-circle-none {
        margin-left: 18%;
      }
    }

    .node-same {
      background: rgb(74, 153, 214);
      border: 2px solid rgb(255, 255, 255);
      text-align: center;
      position: relative;
      z-index: 2;
    }

    .salc-center {
      color: #00fff2;
      height: 18px;
    }

    .flow-node-roate {
      width: 20px;
      height: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1.5px solid rgb(255, 255, 255);
      transform: rotate(45deg);
      // background: rgb(74, 153, 214);
      position: relative;
      z-index: 2;

      .el-icon {
        transform: rotate(-45deg);
        color: white;
      }
    }

    .flow-node-circle {
      width: 20px;
      height: 20px;
      border-radius: 50%;
    }

    .seleectTrue {
      border: 1.5px solid #fe1c1c;
    }

    .selected-node {
      cursor: pointer;
      height: 40px;
      line-height: 18px;
    }

    .selected-node-no {
      height: 40px;
      line-height: 40px;
    }

    .flow-node-Rectangle {
      width: 90px;
      // padding: 10px;
      border-radius: 5px;

      .node-label {
        color: white;
        font-weight: 500;
        white-space: nowrap;
        width: 100%;
        text-align: center;
      }
    }
  }
}

// 连接线样式
.custom-connection-line {
  position: absolute;
  background: #ffffff;
  z-index: 1;
}

.line-arrow {
  position: absolute;
  z-index: 2;
}

/* 添加派发文字 - 通过CSS伪元素实现 */
.top-row::after {
  content: "派发";
  position: absolute;
  font-size: 12px;
  text-align: center;
  z-index: 3;
  color: #ffffff;
  white-space: nowrap;

  /* 定位到createTaskNode和startInspectionNode之间的连接线中心位置 */
  left: 30.5%;
  top: 33%;

  /* 确保文字在连接线上方 */
  transform: translate(-50%, -50%);
}

/* 在创建巡检任务到rotatePlusNode的上方横线添加文字"退回" */
.dialog_main-node::before {
  content: "退回";
  position: absolute;
  font-size: 12px;
  text-align: center;
  z-index: 3;
  color: #ffffff;
  white-space: nowrap;

  /* 定位到水平线的中心位置 */
  left: 38%;
  top: 10%;

  /* 确保文字在连接线上方 */
  transform: translate(-50%, -50%);
}

/* 在bottomRotateNode到siteInspectionNode添加文字'退回' */
.bottom-row::before {
  content: "退回";
  position: absolute;
  font-size: 12px;
  text-align: center;
  z-index: 3;
  color: #ffffff;
  white-space: nowrap;

  /* 定位到竖线的中心位置 */
  left: 73%;
  top: -100%;

  /* 确保文字在连接线上方 */
  transform: translate(-50%, -50%);
}

/* 在bottomRotateNode到bottomCircleNode5添加文字'确认' */
.bottom-row::after {
  content: "确认";
  position: absolute;
  font-size: 12px;
  text-align: center;
  z-index: 3;
  color: #ffffff;
  white-space: nowrap;

  /* 定位到水平线的中心位置 */
  left: 63%;
  top: 10%;

  /* 确保文字在连接线上方 */
  transform: translate(-50%, -50%);
}

// 响应式调整
@media (max-width: 768px) {
  .operationLogDialog {
    width: 90% !important;

    .dialog_main {
      .dialog_main-node {
        flex-wrap: wrap;
        gap: 10px;
      }

      .flow-node-Rectangle {
        width: 70px;
        padding: 10px;
      }
    }
  }
}
</style>