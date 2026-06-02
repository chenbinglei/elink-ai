<template>
  <div class="PowerStatistics">
    <div class="title">
      单位：kWh
    </div>
    <div class="content">
      <!-- 拓扑图容器 -->
      <div class="topology-container">
        <!-- 连接线 SVG -->
        <svg class="connections" width="100%" height="100%">
          <defs>
            <!-- V 型箭头 -->
            <marker id="arrow-v" markerWidth="10" markerHeight="10" refX="5" refY="5" orient="auto"
              markerUnits="strokeWidth">
              <path d="M0,0 L5,5 L0,10 Z" fill="#0183C9" />
            </marker>
          </defs>
          <g class="lines">
            <!-- 第一组连接线 -->
            <line x1="25%" y1="16%" x2="63%" y2="16%" class="connection-line" marker-end="url(#arrow-v)" />
            <!-- 第二组连接线 -->
            <line x1="25%" y1="46%" x2="62%" y2="45%" class="connection-line" marker-end="url(#arrow-v)" />
            <!-- 组间连接线 -->
            <line x1="0%" y1="16%" x2="16%" y2="16%" class="connection-line" />
            <line x1="0%" y1="46%" x2="16%" y2="46%" class="connection-line" />
            <line x1="0%" y1="75%" x2="13%" y2="75%" class="connection-line" marker-end="url(#arrow-v)" />
            <line x1="0%" y1="16%" x2="0" y2="45%" class="connection-line" />
            <line x1="0%" y1="45%" x2="0" y2="75%" class="connection-line" />
          </g>
        </svg>

        <!-- 拓扑节点 -->
        <div class="topology-nodes">
          <!-- 第一组 -->
          <div class="data-group group-1">
            <div class="data-item forward-power node">
              <div class="node-content">
                <div class="data-label">正向电量</div>
                <div class="data-value powerNum">{{ topologyList.positiveQt || 0.00 }}</div>
              </div>
            </div>
            <div class="data-item reverse-power node">
              <div class="node-content">
                <div class="data-label">汽车充电量</div>
                <div class="data-value">{{ topologyList.pileChargeQt || 0.00 }}</div>
              </div>
            </div>
          </div>

          <!-- 第二组 -->
          <div class="data-group group-2">
            <div class="data-item forward-power node">
              <div class="node-content">
                <div class="data-label">汽车放电量</div>
                <div class="data-value">{{ topologyList.pileDischargeQt || 0.00 }}</div>
              </div>
            </div>
            <div class="data-item reverse-power node">
              <div class="node-content">
                <div class="data-label">反向电量</div>
                <div class="data-value powerNum">{{ topologyList.negativeQt || 0.00 }}</div>
              </div>
            </div>
          </div>

          <!-- 第三组 -->
          <div class="data-group group-3">
            <div class="data-item forward-power node">
              <div class="node-content">
                <div class="data-label">损耗电量</div>
                <div class="data-value powerNum">{{ topologyList.lossQt || 0.00 }}</div>
              </div>
            </div>
            <div style="width: 25%; padding: 15px 0">
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from "vue";

const props = defineProps({
  IncomeDate: {
    type: Object,
    default: () => {
      return {};
    }
  },
});

const topologyList = ref(props.IncomeDate);

watch(() => props.IncomeDate, (newValue, oldValue) => {
  topologyList.value = newValue;
  console.log(newValue, 'newValue999');
});
</script>

<style lang="scss" scoped>
.PowerStatistics {
  width: 100%;
  height: 100%;

  .title {
    font-size: 12px;
    font-weight: bold;
    color: #fff;
    font-family: Microsoft YaHei, Microsoft YaHei;
    font-weight: 300;
    text-align: center;
    margin-top: 20px;
  }

  .content {
    width: 100%;
    height: calc(100% - 30px);
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    margin-left: 5%;

    .topology-container {
      position: relative;
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
    }

    .topology-nodes {
      position: relative;
      width: 100%;
      height: 100%;
      z-index: 2;
    }

    .data-group {
      display: flex;
      align-items: center;
      justify-content: space-around;
      width: 100%;
      position: absolute;

      &.group-1 {
        top: 10%;
      }

      &.group-2 {
        top: 40%;
      }

      &.group-3 {
        top: 70%;
      }

      .node {
        width: 25%;
        padding: 10px 0;
        box-sizing: border-box;
        border: 1px solid #0183C9;
        border-radius: 10px;
        background-color: #113050;
        position: relative;
        transition: all 0.3s ease;

        // &:hover {
        //   transform: translateY(-2px);
        //   box-shadow: 0 4px 12px rgba(1, 131, 201, 0.3);
        // }
        .node-content {
          .data-label {
            font-size: 14px;
            font-weight: bold;
            color: #B7C0CA;
            font-family: Microsoft YaHei, Microsoft YaHei;
            font-weight: 400;
            text-align: center;
          }

          .data-value {
            font-size: 14px;
            font-weight: bold;
            color: #06E5C0;
            text-align: center;
            margin-top: 10px;
          }

          .powerNum {
            color: #FF0000;
          }
        }

        &.empty-node {
          opacity: 0.3;
          border: 1px dashed #0183C9;

          .data-label,
          .data-value {
            color: #666;
          }
        }
      }
    }
  }
}

/* 连接线样式 */
.connections {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.connection-line {
  stroke: #0183C9;
  stroke-width: 1;
  fill: none;


}

.group-connector {
  stroke: #0183C9;
  stroke-width: 1;
  stroke-dasharray: 5, 5;
}
</style>