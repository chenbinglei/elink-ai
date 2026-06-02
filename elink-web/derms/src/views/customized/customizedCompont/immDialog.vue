<template>
  <el-dialog v-model="visible" :title="detailList.label" width="70%" style="margin-top: 1%;" class="imm-dialog"
    @opened="onDialogOpen">
    <div style="width:99%;display: flex;justify-content: end;">
      <div class="vs-right-button">
        <div class="vs-right-button-item" @click="selectTabs(item.name)" :class="{ active: item.name === tabsActive }"
          v-for="item in tabs" :key="item.id">
          {{ item.name }}
        </div>
      </div>
    </div>
    <div class="dialog-body" v-if="detailList.label === '注塑机MA1600V'">
      <div class="immImg">
        <div class="immImg-title">{{ detailList.label }}</div>
        <img src="@/assets/customized/MA5300V0000_iSpt.png" class="img-imm" alt="">
        <img src="@/assets/customized/immIcon.png" class="immIcon" alt="">

      </div>

      <div class="immType">

        <div class="immTable">
          <!-- 普通表格：合模单元 / 其他 -->
          <div class="table-container" ref="tableContentRef" v-resize="setTableMaxHeight" v-if="tabsActive !== '注射单元'">
            <el-table :data="currentTableData" ref="tableRef" style="table-layout: fixed" border stripe
              :max-height="tableMaxHeight">
              <el-table-column label="参数名称" prop="name" align="center" />
              <el-table-column label="参数值" prop="value" align="center" />
              <el-table-column label="单位" prop="unit" align="center" />
            </el-table>
          </div>

          <!-- ABC三列：注射单元 -->
          <div class="table-container" ref="tableContentRef" v-resize="setTableMaxHeight" v-else>
            <el-table :data="currentTableData" ref="tableRef" style="table-layout: fixed" border stripe
              :max-height="tableMaxHeight" :header-cell-style="{ background: '#0b3a6b', color: '#fff' }"
              :cell-style="{ color: '#fff' }">
              <el-table-column label="参数名称" prop="name" align="center" min-width="180" />
              <el-table-column label="A" align="center" min-width="100">
                <template #default="{ row }">
                  {{ typeof row.value === 'object' ? row.value.A : row.value }}
                </template>
              </el-table-column>
              <el-table-column label="B" align="center" min-width="100">
                <template #default="{ row }">
                  {{ typeof row.value === 'object' ? row.value.B : '-' }}
                </template>
              </el-table-column>
              <el-table-column label="C" align="center" min-width="100">
                <template #default="{ row }">
                  {{ typeof row.value === 'object' ? row.value.C : '-' }}
                </template>
              </el-table-column>
              <el-table-column label="单位" prop="unit" align="center" min-width="80" />
            </el-table>
          </div>
        </div>
      </div>
    </div>
    <div class="dialog-body" v-if="detailList.label === '注塑机JU5500V'">
      <div class="immImg">
        <div class="immImg-title">{{ detailList.label }}</div>
        <img src="@/assets/customized/JU5500V0000_iSpt.png" class="img-imm" alt="">
        <img src="@/assets/customized/immIcon.png" class="immIcon" alt="">

      </div>

      <div class="immType">

        <div class="immTable">
          <!-- 普通表格：合模单元 / 其他 -->
          <div class="table-container" ref="tableContentRef" v-resize="setTableMaxHeight" v-if="tabsActive == '合模单元'">
            <el-table :data="currentTableData" ref="tableRef" style="table-layout: fixed" border stripe
              :max-height="tableMaxHeight">
              <el-table-column label="参数名称" prop="name" align="center" />
              <el-table-column label="参数值" prop="value" align="center" />
              <el-table-column label="单位" prop="unit" align="center" />
            </el-table>
          </div>

          <!-- ABC三列：注射单元 -->
          <div class="table-container" ref="tableContentRef" v-resize="setTableMaxHeight" v-if="tabsActive == '其他'">
            <el-table :data="currentTableData" ref="tableRef" style="table-layout: fixed" border stripe
              :max-height="tableMaxHeight" :header-cell-style="{ background: '#0b3a6b', color: '#fff' }"
              :cell-style="{ color: '#fff' }">
              <el-table-column label="参数名称" prop="name" align="center" min-width="180" />
              <el-table-column label="2250" align="center" min-width="100">
                <template #default="{ row }">
                  {{ typeof row.value === 'object' ? row.value['2250'] : row.value }}
                </template>
              </el-table-column>
              <el-table-column label="3450" align="center" min-width="100">
                <template #default="{ row }">
                  {{ typeof row.value === 'object' ? row.value['3450'] : '-' }}
                </template>
              </el-table-column>
              <el-table-column label="4650" align="center" min-width="100">
                <template #default="{ row }">
                  {{ typeof row.value === 'object' ? row.value['4650'] : '-' }}
                </template>
              </el-table-column>
              <el-table-column label="单位" prop="unit" align="center" min-width="80" />
            </el-table>
          </div>
          <!-- 注射单元 -->
          <div class="table-container" ref="tableContentRef" v-resize="setTableMaxHeight" v-if="tabsActive == '注射单元'">
            <!-- 多级表头表格 → 和你给的示例结构完全一致 -->
            <el-table :data="currentTableData" style="width:100%" border stripe :max-height="tableMaxHeight"
              :header-cell-style="{ background: '#0b3a6b', color: '#fff' }" :cell-style="{ color: '#fff' }">
              <!-- 左侧固定 -->
              <el-table-column prop="name" label="参数名称" align="center" min-width="160"
                :cell-style="{ verticalAlign: 'middle' }" fixed="left" />
              <el-table-column prop="unit" label="单位" align="center" min-width="80"
                :cell-style="{ verticalAlign: 'middle' }" />

              <!-- 2250 型号 -->
              <el-table-column label="2250" align="center">
                <el-table-column label="规格" align="center">
                  <el-table-column label="A" align="center">
                    <template #default="{ row }">{{ row.value['2250-A'] || '-' }}</template>
                  </el-table-column>
                  <el-table-column label="B" align="center">
                    <template #default="{ row }">{{ row.value['2250-B'] || '-' }}</template>
                  </el-table-column>
                  <el-table-column label="C" align="center">
                    <template #default="{ row }">{{ row.value['2250-C'] || '-' }}</template>
                  </el-table-column>
                </el-table-column>
              </el-table-column>

              <!-- 3450 型号 -->
              <el-table-column label="3450" align="center">
                <el-table-column label="规格" align="center">
                  <el-table-column label="A" align="center">
                    <template #default="{ row }">{{ row.value['3450-A'] || '-' }}</template>
                  </el-table-column>
                  <el-table-column label="B" align="center">
                    <template #default="{ row }">{{ row.value['3450-B'] || '-' }}</template>
                  </el-table-column>
                  <el-table-column label="C" align="center">
                    <template #default="{ row }">{{ row.value['3450-C'] || '-' }}</template>
                  </el-table-column>
                </el-table-column>
              </el-table-column>

              <!-- 4550 型号 -->
              <el-table-column label="4550" align="center">
                <el-table-column label="规格" align="center">
                  <el-table-column label="A" align="center">
                    <template #default="{ row }">{{ row.value['4550-A'] || '-' }}</template>
                  </el-table-column>
                  <el-table-column label="B" align="center">
                    <template #default="{ row }">{{ row.value['4550-B'] || '-' }}</template>
                  </el-table-column>
                  <el-table-column label="C" align="center">
                    <template #default="{ row }">{{ row.value['4550-C'] || '-' }}</template>
                  </el-table-column>
                </el-table-column>
              </el-table-column>

            </el-table>
          </div>
        </div>
      </div>
    </div>
    <div class="dialog-body" v-if="detailList.label === '注塑机VE1500V'">
      <div class="immImg">
        <div class="immImg-title">{{ detailList.label }}</div>
        <img src="@/assets/customized/VE600V0000_iSpt.png" class="img-imm" alt="">
        <img src="@/assets/customized/immIcon.png" class="immIcon" alt="">




      </div>

      <div class="immType">

        <div class="immTable">
          <!-- 普通表格：合模单元 / 其他 -->
          <div class="table-container" ref="tableContentRef" v-resize="setTableMaxHeight" v-if="tabsActive == '合模单元'">
            <el-table :data="currentTableData" ref="tableRef" style="table-layout: fixed" border stripe
              :max-height="tableMaxHeight" :header-cell-style="{ background: '#0b3a6b', color: '#fff' }">
              <el-table-column label="参数名称" prop="name" align="center" />
              <el-table-column label="参数值" prop="value" align="center" />
              <el-table-column label="单位" prop="unit" align="center" />
            </el-table>
          </div>

          <!-- ABC三列：注射单元 -->
          <div class="table-container" ref="tableContentRef" v-resize="setTableMaxHeight" v-if="tabsActive == '注射单元'">
            <el-table :data="currentTableData" ref="tableRef" style="width: 100%; table-layout: fixed" border stripe
              :max-height="tableMaxHeight" :header-cell-style="{ background: '#0b3a6b', color: '#fff' }"
              :cell-style="{ color: '#fff' }">
              <!-- 左侧固定列：参数名称 + 单位 -->
              <el-table-column prop="name" label="参数名称" align="center" min-width="180" fixed="left"
                :cell-style="{ verticalAlign: 'middle' }" />
              <el-table-column prop="unit" label="单位" align="center" min-width="80" fixed="left"
                :cell-style="{ verticalAlign: 'middle' }" width="100" />


              <!-- 210 型号分组 -->
              <el-table-column label="210" align="center" min-width="320">
                <el-table-column label="AA" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['210-AA'] || row.value['210'] || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="A" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['210-A'] || row.value['210'] || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="B" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['210-B'] || row.value['210'] || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="C" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['210-C'] || row.value['210'] || '-' }}
                  </template>
                </el-table-column>
              </el-table-column>

              <!-- 300 型号分组 -->
              <el-table-column label="300" align="center" min-width="320">
                <el-table-column label="AA" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['300-AA'] || row.value['300'] || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="A" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['300-A'] || row.value['300'] || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="B" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['300-B'] || row.value['300'] || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="C" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['300-C'] || row.value['300'] || '-' }}
                  </template>
                </el-table-column>
              </el-table-column>

              <!-- 430 型号分组 -->
              <el-table-column label="430" align="center" min-width="240">
                <el-table-column label="A" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['430-A'] || row.value['430'] || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="B" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['430-B'] || row.value['430'] || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="C" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['430-C'] || row.value['430'] || '-' }}
                  </template>
                </el-table-column>
              </el-table-column>

              <!-- 640 型号分组 -->
              <el-table-column label="640" align="center" min-width="240">
                <el-table-column label="A" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['640-A'] || row.value['640'] || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="B" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['640-B'] || row.value['640'] || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="C" align="center" min-width="80">
                  <template #default="{ row }">
                    {{ row.value['640-C'] || row.value['640'] || '-' }}
                  </template>
                </el-table-column>
              </el-table-column>
            </el-table>
          </div>
          <!-- 其他：完全按图片表头（210 / 300 / 430 / 640 + 标准/高速/超高速）-->
          <div class="table-container" ref="tableContentRef" v-resize="setTableMaxHeight" v-if="tabsActive == '其他'">
            <el-table :data="currentTableData" ref="tableRef" style="width:100%; table-layout:fixed" border stripe
              :max-height="tableMaxHeight" :header-cell-style="{ background: '#0b3a6b', color: '#fff' }"
              :cell-style="{ color: '#fff', textAlign: 'center', whiteSpace: 'pre-line' }" :span-method="spanMethod">
              <el-table-column prop="name" label="参数名称" align="center" min-width="180" fixed="left" />
              <el-table-column prop="unit" label="单位" align="center" min-width="80" fixed="left" />

              <!-- 210 -->
              <el-table-column label="210" align="center">
                <el-table-column label="AA" align="center">
                  <template #default="{ row }">
                    <!-- {{ row.value?.['210-AA'] || row.value?.['210'] || '-' }} -->
                    <div :style="{ whiteSpace: 'pre-line', textAlign: 'center' }">
                      {{ row.value?.['210-AA'] || row.value?.['210'] || '-' }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="A" align="center">
                  <template #default="{ row }">{{ row.value?.['210-A'] || '-' }}</template>
                </el-table-column>
                <el-table-column label="B" align="center">
                  <template #default="{ row }">{{ row.value?.['210-B'] || '-' }}</template>
                </el-table-column>
                <el-table-column label="C" align="center">
                  <template #default="{ row }">{{ row.value?.['210-C'] || '-' }}</template>
                </el-table-column>
              </el-table-column>

              <!-- 300 -->
              <el-table-column label="300" align="center">
                <el-table-column label="AA" align="center">
                  <template #default="{ row }">

                    <div :style="{ whiteSpace: 'pre-line', textAlign: 'center' }">
                      {{ row.value?.['300-AA'] || row.value?.['300'] || '-' }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="A" align="center">
                  <template #default="{ row }">{{ row.value?.['300-A'] || '-' }}</template>
                </el-table-column>
                <el-table-column label="B" align="center">
                  <template #default="{ row }">{{ row.value?.['300-B'] || '-' }}</template>
                </el-table-column>
                <el-table-column label="C" align="center">
                  <template #default="{ row }">{{ row.value?.['300-C'] || '-' }}</template>
                </el-table-column>
              </el-table-column>

              <!-- 430 -->
              <el-table-column label="430" align="center">
                <el-table-column label="A" align="center">
                  <template #default="{ row }">

                    <div :style="{ whiteSpace: 'pre-line', textAlign: 'center' }">
                      {{ row.value?.['430-A'] || row.value?.['430'] || '-' }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="B" align="center">
                  <template #default="{ row }">-</template>
                </el-table-column>
                <el-table-column label="C" align="center">
                  <template #default="{ row }">-</template>
                </el-table-column>
              </el-table-column>

              <!-- 640 -->
              <el-table-column label="640" align="center">
                <el-table-column label="A" align="center">
                  <template #default="{ row }">

                    <div :style="{ whiteSpace: 'pre-line', textAlign: 'center' }">
                      {{ row.value?.['640-A'] || row.value?.['640'] || '-' }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="B" align="center">
                  <template #default="{ row }">-</template>
                </el-table-column>
                <el-table-column label="C" align="center">
                  <template #default="{ row }">-</template>
                </el-table-column>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed, nextTick } from 'vue'
import { setTooltipList2 } from './centerTooltip.js'

const detailList = ref([])
const tabsActive = ref('合模单元')
const tableList = ref([])
const tableMaxHeight = ref(0)
const tableContentRef = ref(null)
const tableRef = ref(null)

const props = defineProps({
  immList: { type: Array, default: () => [] },
  isVisible: { type: Boolean, default: false },
})

const tabs = ref([
  { name: "合模单元", id: '1' },
  { name: "注射单元", id: '2' },
  { name: "其他", id: '3' },
])
// 需要合并的行：总电器容量、外形、机器重量、料斗
const mergeRows = [
  '总电器容量',
  '外形(L×W×H)',
  '机器重量',
  '料斗容积(选配)',
  '电热功率'
]


const spanMethod = ({ row, rowIndex, columnIndex, col }) => {
  // 只处理需要合并的行
  if (!mergeRows.includes(row.name)) return { rowspan: 1, colspan: 1 }
  if (row.name === "电热功率") {
    // 430 组：列10~12 合并
    if (columnIndex >= 10 && columnIndex <= 12) {
      return columnIndex === 10
        ? { rowspan: 1, colspan: 3 }
        : { rowspan: 0, colspan: 0 }
    }

    // 640 组：列13~15 合并
    if (columnIndex >= 13 && columnIndex <= 15) {
      return columnIndex === 13
        ? { rowspan: 1, colspan: 3 }
        : { rowspan: 0, colspan: 0 }
    }
  } else {
    // 左侧列不合并
    if (columnIndex < 2) return { rowspan: 1, colspan: 1 }

    // 210 组：列 2~5 合并
    if (columnIndex >= 2 && columnIndex <= 5) {
      return columnIndex === 2
        ? { rowspan: 1, colspan: 4 }
        : { rowspan: 0, colspan: 0 }
    }

    // 300 组：列 6~9 合并
    if (columnIndex >= 6 && columnIndex <= 9) {
      return columnIndex === 6
        ? { rowspan: 1, colspan: 4 }
        : { rowspan: 0, colspan: 0 }
    }

    // 430 组：列10~12 合并
    if (columnIndex >= 10 && columnIndex <= 12) {
      return columnIndex === 10
        ? { rowspan: 1, colspan: 3 }
        : { rowspan: 0, colspan: 0 }
    }

    // 640 组：列13~15 合并
    if (columnIndex >= 13 && columnIndex <= 15) {
      return columnIndex === 13
        ? { rowspan: 1, colspan: 3 }
        : { rowspan: 0, colspan: 0 }
    }
  }


  return { rowspan: 1, colspan: 1 }
}
const emit = defineEmits(['close'])
const visible = ref(false)

// 打开弹窗时重新加载数据 + 刷新表格
const onDialogOpen = async () => {
  await nextTick()
  getImmList()
  setTableMaxHeight()
}

const selectTabs = (id) => {
  console.log(id,'****')

  tabsActive.value = id
  nextTick(() => {
    setTableMaxHeight()
  })
}

const getImmList = () => {
  if (!detailList.value?.label) return
  const data = setTooltipList2()[detailList.value.label]
  tableList.value = data || []
}

const currentTableData = computed(() => {
  const arr = tableList.value?.filter(item => item.name === tabsActive.value)
  return arr?.[0]?.children || []
})

const setTableMaxHeight = () => {
  nextTick(() => {
    if (tableContentRef.value) {
      tableMaxHeight.value = tableContentRef.value.offsetHeight - 10
    }
  })
}

watch(() => props.immList, (val) => {
  if (val) {
    detailList.value = val
  }
})

watch(() => props.isVisible, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  if (!val) {
    emit('close')
    tabsActive.value = '合模单元'
  }
})
</script>

<style lang="scss" scoped>
.imm-dialog {
  .vs-right-button {
    margin: 20px 20px 0 20px;
    box-sizing: border-box;
    display: inline-flex;
    align-items: center;
    border-radius: 4px 4px 4px 4px;
    color: #ffffff;
    border: 1px solid #0071cc;

    font-family: Microsoft YaHei, Microsoft YaHei;
    font-weight: bold;

    .vs-right-button-item {
      padding: 5px 10px;
      font-size: 14px;
      line-height: normal;
      cursor: pointer;
      z-index: 10;
    }

    .active {
      background: linear-gradient(180deg,
          #54a1df 0%,
          #005599 82.42%,
          #40a5fe 100%);
    }
  }



  .dialog-body {
    padding: 20px;
    box-sizing: border-box;
    text-align: center;
    color: #fff;
    display: flex;
    align-items: center;
    height: 70vh;
    gap: 3%;

    .immImg {
      width: 52%;
      height: 100%;
      flex-direction: column;
      /* 竖向排列 */
      align-items: center;
      /* 水平居中 */
      justify-content: center;
      /* 垂直居中（如果容器有高度） */
      position: relative;

      .immImg-title {
        font-weight: 400;
        font-size: 20px;
        margin: 10px auto;
        width: 50%;
        height: 8%;
        color: #00FFFF;
        display: flex;
        align-items: center;
        justify-content: center;

        font-family: YouSheBiaoTiHei, YouSheBiaoTiHei;
        background: url(@/assets/customized/immTitle.png) no-repeat;
        background-size: 100% 100%;

      }

      .img-imm {
        width: 90%;
        margin-top: 5%;
        position: absolute;
         z-index:10;
      }

      .immIcon {
        position: absolute;
        z-index:9;
        top: 67%;
        left: 47%;
        transform: translate(-50%, -50%);
      }

      img {}


    }

    .immType {
      width: 45%;
      height: 100%;
      color: #fff;
      padding: 0 20px;

      .immType-tabs {
        display: flex;
        justify-content: center;
        align-items: center;

        .immType-item {
          font-size: 18px;
          border-bottom: 1px solid #fff;
          cursor: pointer;
          padding: 10px;
        }

        .active {
          color: #00ffff;
          border-bottom: 1px solid #00ffff;
        }
      }

      .immTable {
        margin-top: 20px;
        height: calc(100% - 60px);
        display: flex;
        flex-direction: column;

        .table-container {
          flex: 1;
          overflow: hidden;
        }
      }
    }
  }
}
</style>