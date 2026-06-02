<template>
  <div class="PowerStatistics">
    <div class="title">
      单位：元
    </div>
    <div class="topology">
      <svg class="connections" width="100%" height="100%">
        <defs>
          <!-- V 型箭头 -->
          <marker id="arrow-v" markerWidth="10" markerHeight="10" refX="5" refY="5" orient="auto"
            markerUnits="strokeWidth">
            <path d="M0,0 L5,5 L0,10 Z" fill="#0183C9" />
          </marker>
        </defs>
        <g class="lines">
          <!-- 横线--第一组连接线 -->
          <line x1="15%" y1="20%" x2="40%" y2="20%" class="connection-line" />
          <line x1="65%" y1="20%" x2="90%" y2="20%" class="connection-line" />
          <!-- 竖线--第一组连接线 -->
          <line x1="15%" y1="15%" x2="15%" y2="20%" class="connection-line" />
          <line x1="40%" y1="15%" x2="40%" y2="20%" class="connection-line" />
          <line x1="65%" y1="15%" x2="65%" y2="20%" class="connection-line" />
          <line x1="90%" y1="15%" x2="90%" y2="20%" class="connection-line" />


          <line x1="28%" y1="20%" x2="28%" y2="26%" class="connection-line" marker-end="url(#arrow-v)" />
          <line x1="78%" y1="20%" x2="78%" y2="26%" class="connection-line" marker-end="url(#arrow-v)" />
          <!-- 横线--第二组连接线 -->
          <line x1="28%" y1="26%" x2="28%" y2="40%" class="connection-line" />
          <line x1="78%" y1="26%" x2="78%" y2="40%" class="connection-line" />


          <line x1="68%" y1="40%" x2="78%" y2="40%" class="connection-line" />
          <line x1="28%" y1="40%" x2="38%" y2="40%" class="connection-line" />

          <!-- 竖线--第二组连接线 -->
          <!-- 竖向连线 -->
          <line x1="18%" y1="50%" x2="18%" y2="65%" class="connection-line" />
          <line x1="88%" y1="50%" x2="88%" y2="65%" class="connection-line" />
          <line x1="68%" y1="40%" x2="68%" y2="65%" class="connection-line" />
          <line x1="38%" y1="40%" x2="38%" y2="65%" class="connection-line" />
          <!-- 竖线--总收益连接线 -->
          <line x1="18%" y1="65%" x2="88%" y2="65%" class="connection-line" />
          <line x1="52%" y1="30%" x2="52%" y2="80%" class="connection-line" marker-end="url(#arrow-v)" />
        </g>
      </svg>

      <!-- 第一行节点 -->
      <div class="data-group group-1">
        <div class="node income row-1 col-1">
          <div class="node-title">充电收入</div>
          <div class="node-value" style="color: #FF0000;">
            <!-- {{ formatValue(topologyList.chargeCost) || 0.00 }} -->
            <span v-if="topologyList.chargeCost !== 0">+</span>{{ topologyList.chargeCost || 0.00 }}
          </div>
        </div>
        <div class="node cost row-1 col-2">
          <div class="node-title">充电购电成本</div>
          <div class="node-value" style="color: #06E5C0;">
            <!-- {{ formatValue(topologyList.chargePurchaseCost) || 0.00 }} -->
            <span v-if="topologyList.chargePurchaseCost !== 0">-</span>{{ topologyList.chargePurchaseCost || 0.00 }}
          </div>
        </div>
        <div class="node income row-1 col-3">
          <div class="node-title">V2G售电收入</div>
          <div class="node-value" style="color: #FF0000;">
            <!-- {{ formatValue(topologyList.dischargeSaleCost) || 0.00 }} -->
            <span v-if="topologyList.dischargeSaleCost !== 0">+</span>{{ topologyList.dischargeSaleCost || 0.00 }}
          </div>
        </div>
        <div class="node cost row-1 col-4">
          <div class="node-title">V2G购电成本</div>
          <div class="node-value" style="color: #06E5C0;">
            <!-- {{ formatValue(topologyList.dischargePurchaseCost) || 0.00 }} -->
            <span v-if="topologyList.dischargePurchaseCost !== 0">-</span>{{ topologyList.dischargePurchaseCost || 0.00 }}
          </div>
        </div>
      </div>

      <!-- 第二行节点 -->
      <div class="data-group group-2">
        <div class="node profit row-2 col-1">
          <div class="node-title">充电收益</div>
          <div class="node-value" :style="{ color: getColor(topologyList.chargeIncome) }">
            {{ formatValue(topologyList.chargeIncome) || 0.00 }}

          </div>
        </div>
        <div class="node profit row-2 col-2">
          <div class="node-title">运营补贴</div>
          <div class="node-value" style="color: #FF0000;">
            <!-- {{ formatValue(topologyList.operateSubsidy) || 0.00 }} -->
            <span v-if="topologyList.operateSubsidy !== 0">+</span>{{ topologyList.operateSubsidy || 0.00 }}

          </div>
        </div>
        <div class="node profit row-2 col-3">
          <div class="node-title">放电收益</div>
          <div class="node-value" :style="{ color: getColor(topologyList.dischargeIncome) }">
            {{ formatValue(topologyList.dischargeIncome) || 0.00 }}
          </div>
        </div>
      </div>

      <!-- 第三行节点 -->
      <div class="data-group group-3">
        <div class="node cost row-3 col-1">
          <div class="node-title">场地租金</div>
          <div class="node-value" style="color: #06E5C0;">
            <!-- {{ formatValue(topologyList.siteRent)|| 0.00  }} -->
            <span v-if="topologyList.siteRent !== 0">-</span>{{ topologyList.siteRent || 0.00 }}

          </div>
        </div>
        <div class="node cost row-3 col-2">
          <div class="node-title">运营成本</div>
          <div class="node-value" style="color: #06E5C0;">
            <!-- {{ formatValue(topologyList.operateCost)|| 0.00  }} -->
            <span v-if="topologyList.operateCost !== 0">-</span>{{ topologyList.operateCost || 0.00 }}
          </div>
        </div>
        <div class="node cost row-3 col-3">
          <div class="node-title">运维成本</div>
          <div class="node-value" style="color: #06E5C0;">
            <!-- {{ formatValue(topologyList.maintainCost)|| 0.00  }} -->
            <span v-if="topologyList.maintainCost !== 0">-</span>{{ topologyList.maintainCost || 0.00 }}
          </div>
        </div>
      </div>

      <!-- 第四行节点 -->
      <div class="data-group group-4">
        <div class="node total row-4 center">
          <div class="node-title">总收益</div>
          <div class="node-value" :style="{ color: getColor(topologyList.totalIncome) }">
            {{ formatValue(topologyList.totalIncome) || 0.00 }}
          </div>
        </div>
      </div>
    </div>


  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted, onUnmounted } from "vue";

const props = defineProps({
  IncomeDate: {
    type: Object,
    default: () => {
      return {};
    }
  },
});
const formatValue = (value) => {
  const num = Number(value);
  if (isNaN(num)) return '';
  return num > 0 ? `+${num}` : `${num}`;
};

const getColor = (value) => {
  const num = Number(value);
  return num > 0 ? '#FF0000' : '#06E5C0';
};

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


}

.topology {
  position: relative;
  width: 100%;
  height: calc(100% - 50px);

  box-sizing: border-box;

  .data-group {
    display: flex;
    align-items: center;
    justify-content: space-around;
    width: 100%;
    position: absolute;
    z-index: 2;

    &.group-1 {
      top: 8%;
    }

    &.group-2 {
      top: 26%;

      .col-1 {
        margin-left: 18%;
      }

      .col-2 {
        margin-left: 4%;
      }

      .col-3 {
        margin-right: 12%;
      }

    }

    &.group-3 {
      top: 45%;
    }

    &.group-4 {
      top: 80%;

      .node {
        width: 24%;
      }
    }
  }




  .node {
    width: 18%;
    padding: 5px 0;
    margin-left: 5%;
    box-sizing: border-box;
    border: 1px solid #0183C9;
    border-radius: 10px;
    background-color: #113050;
    text-align: center;

    .node-title {
      font-size: 14px;
      font-weight: bold;
      color: #B7C0CA;
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      text-align: center;
      white-space: nowrap;
    }

    .node-value {
      font-size: 14px;
      font-weight: bold;
      text-align: center;
      margin-top: 5px;
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