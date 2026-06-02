<template>
  <div class="app-container">
    <div class="app-container-right">
      <div class="header-form content_border">
        <el-form :model="activeDeviceInfo" inline>
          <el-row :gutter="16">
            <el-col :md="8" :sm="12" :xl="6" :xs="24">
              <el-form-item label="巡检任务名称：">
                <el-input v-model="activeDeviceInfo.taskName" placeholder="请输入巡检项名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :md="8" :sm="12" :xl="6" :xs="24">
              <el-form-item label="开始时间：">
                <el-date-picker v-model="activeDeviceInfo.dataTime" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts" clearable
                  value-format="YYYY-MM-DD" format="YYYY-MM-DD" end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :sm="12" :xl="6" :xs="24">
              <el-form-item label="状态：">
                <el-select v-model="activeDeviceInfo.taskStatus" placeholder="全部">
                  <el-option v-for="item in task_status_array" :key="item.id" :label="item.name" :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :md="8" :sm="12" :xl="6" :xs="24">
              <el-form-item>
                <el-button :icon="Search" type="primary" @click="queryList">查询</el-button>
                <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
              </el-form-item>
            </el-col>

          </el-row>
        </el-form>
      </div>

      <div class="tableContent content_border" ref="tableContentRef">
        <TableHeaderTitle title="" :iconShow="false">
          <template #content>
            <div class="table_top_content">
              <el-button @click="clickOperateBut(1)" type="primary">
                <span>新增</span>
              </el-button>
              <el-button @click="clickOperateBut(2)">
                <span>删除</span>
              </el-button>
              <el-button @click="clickOperateBut(3)" type="primary">
                <span>节点人员设置</span>
              </el-button>
            </div>
          </template>
        </TableHeaderTitle>
        <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
          <el-table v-loading="listLoading" :data="InvoiceList" :max-height="tableMaxHeight" @selection-change="handleSelectionChange"
            class="custom-expand-table" @expand-change="handleExpandChange" ref="tableRef" :row-key="rowKey">
            <!-- 展开行 - 放置在选择框之前 -->
            <el-table-column type="expand" width="80">
              <template #default="scope">
                <div class="expand-content">
                  <div v-if="scope.row.expandData?.length > 0" class="map-wrapper">
                    <div :id="'map-container-' + scope.row.id" class="map-container" v-loading="scope.row.mapLoading"></div>
                    <div class="map-table" ref="tableContentRef" v-resize="setTableMaxHeights">
                      <el-table :data="scope.row.expandData || []" border stripe :max-height="tableMaxHeights">
                        <el-table-column label="场站名称" prop="siteName" align="center"></el-table-column>
                        <el-table-column label="巡检结果" prop="status" align="center">
                          <template #default="scope">
                            <span v-if="scope.row.status === 1">未分配</span>
                            <span v-if="scope.row.status === 2">未开始</span>
                            <span v-if="scope.row.status === 3">巡检中</span>
                            <span v-if="scope.row.status === 4">待验收</span>
                            <span v-if="scope.row.status === 5">完结</span>
                          </template>
                        </el-table-column>
                        <el-table-column label="结束时间" prop="finishTime" align="center">
                          <template #default="{ row }">{{ $filters.moreData(row.finishTime) }}</template>
                        </el-table-column>
                        <el-table-column label="异常数量" prop="exceptionNum" align="center">
                          <template #default="{ row }">
                            <span class="err-num">{{ $filters.moreData(row.exceptionNum) }}</span>
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                  </div>
                  <div v-else class="no-location">
                    <i class="iconfont icon-no-location"></i>
                    <p>无经纬度信息</p>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column type="selection" width="55" :selectable="(row) => row.taskStatus !== 5"></el-table-column>
            <el-table-column prop="taskName" label="巡检任务名称" show-overflow-tooltip>
              <template #default="{ row }">
                {{ $filters.moreData(row.taskName) }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="开始时间" show-overflow-tooltip>
              <template #default="{ row }">
                {{ $filters.moreData(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="updateTime" label="完成时间" show-overflow-tooltip>
              <template #default="{ row }">
                {{ $filters.moreData(row.updateTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="taskStatus" label="任务状态" show-overflow-tooltip>
              <template #default="scope">
                <span v-if="scope.row.taskStatus === 1">未分配</span>
                <span v-if="scope.row.taskStatus === 2">未开始</span>
                <span v-if="scope.row.taskStatus === 3">巡检中</span>
                <span v-if="scope.row.taskStatus === 4">待验收</span>
                <span v-if="scope.row.taskStatus === 5">完结</span>
              </template>
            </el-table-column>
            <el-table-column prop="userName" label="当前处理人" show-overflow-tooltip>
              <template #default="{ row }">
                {{ $filters.moreData(row.userName) }}
              </template>
            </el-table-column>
            <el-table-column prop="exceptionNum" label="异常数量" show-overflow-tooltip>
              <template #default="{ row }">
                {{ $filters.moreData(row.exceptionNum) }}
              </template>
            </el-table-column>

            <el-table-column label="操作" align="center">
              <template #default="scope">
                <div class="table_operate_class">
                  <el-link :underline="'never'" @click="clickDetails(scope.row,'详情')">详情</el-link>
                  <el-link :underline="'never'" v-if="scope.row.taskStatus === 1&&scope.row.userId === userId" @click="clickDetails(scope.row)">执行</el-link>
                  <el-link :underline="'never'" v-if="scope.row.taskStatus === 4&&scope.row.userId === userId" @click="clickDetails(scope.row)">验收</el-link>
                </div>
              </template>

            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination" ref="tablePaginationRef">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="queryList" />
        </div>
      </div>

    </div>
    <!-- 新增 -->
    <addTaskDialog v-if="addTaskVisible" v-model:isVisible="addTaskVisible" @saveDialog="saveDialog" @queryList="queryList" />
    <nodePersonDialog v-if="nodePersonVisible" v-model:isVisible="nodePersonVisible" @saveDialog="saveDialog" />
    <!-- 详情 -->
    <detailsDialog :inspectionType="inspectionType" :inspectionArray="inspectionArray" v-if="detailsVisible" v-model:isVisible="detailsVisible"
      @saveDialog="saveDialog" :titleValue="titleValue" />

  </div>
</template>

<script setup>
import { queryInspectionTaskList, deleteInspectionTaskByIds, findInspectionTaskDetailById } from "@/api/assetManagement/inspection";
import { RefreshRight, Search } from '@element-plus/icons-vue';
import { task_status_array } from "@/utils/setVariate";
import addTaskDialog from "./components/addTaskDialog.vue";
import nodePersonDialog from "./components/nodePersonDialog.vue";
import SystemsetController from "@/api/system/index";
import { ElMessage, ElMessageBox } from 'element-plus';
import { pickerOptionsGthanAcTime } from "@/utils/dateTime";
import { ref, watch, onMounted, nextTick, computed } from "vue";
import detailsDialog from "./components/detailsDialog.vue";

import AMapLoader from '@amap/amap-jsapi-loader';
import { useStore } from "vuex";

// 分页
const currentPage = ref(1)
const pageNum = ref(10)
const totalNumber = ref(0)
const userId = ref(null)
// 表格数据
const InvoiceList = ref([])
const inspectionArray = ref({})
const inspectionType = ref(null)
const userListLength = ref(null)
const store = useStore();
const titleValue = ref('')
const userInfo = computed(() => {
  return store.state.app.userInfo;
});
const tableMaxHeights = ref(300);

// 高德地图实例存储
const mapInstances = ref(new Map())
// AMapLoader 实例（单例）
let AMapInstance = null;

// 查询条件
const pickerOptions = ref(pickerOptionsGthanAcTime())
const activeDeviceInfo = ref({
  taskName: null,
  dataTime: null,
  taskStatus: null,
})
// 计算出表格最大高度
const tableContentRef = ref(null);
const tablePaginationRef = ref(null);
const tableMaxHeight = ref(300);
const setTableMaxHeight = () => {
  let tableContentHeight = tableContentRef.value.offsetHeight - 98;
  tableMaxHeight.value = tableContentHeight - tablePaginationRef.value.offsetHeight;
};

// 预加载高德地图
const preloadAMap = async () => {
  if (!AMapInstance) {
    try {
      AMapInstance = await AMapLoader.load({
        key: '78acf8560d11d6ee253952bc7206bf53',
        version: '2.0',
        plugins: ['AMap.Marker', 'AMap.InfoWindow', 'AMap.ToolBar']
      });
      console.log('高德地图预加载成功');
    } catch (error) {
      console.error('高德地图预加载失败:', error);
    }
  }
  return AMapInstance;
};

// 查询
const queryList = () => {
  let obj = {
    ...activeDeviceInfo.value,
    page: currentPage.value,
    size: pageNum.value,
    tenantId: userInfo.value.tenantId,
    startDate: activeDeviceInfo.value.dataTime ? activeDeviceInfo.value.dataTime[0] : null,
    endDate: activeDeviceInfo.value.dataTime ? activeDeviceInfo.value.dataTime[1] : null
  };
  delete obj.dataTime;

  queryInspectionTaskList(obj).then(async res => {
    // 为每行数据添加扩展字段
    InvoiceList.value = (res.data.items || []).map(item => ({
      ...item,
      expandData: [], // 展开数据
      mapLoading: false, // 地图加载状态
      mapInitialized: false // 是否已初始化地图
    }));
    totalNumber.value = res.data.totalSize;
    localStorage.setItem('userListLength', res.data.totalSize);
    await nextTick();
    if (tableRef.value) {
      tableRef.value.setScrollTop(0);
    }
  }).catch((error) => {
    if (error && error.code === 88886) return;
    totalNumber.value = 0;
    InvoiceList.value = [];
  });
}

const selectList = ref([])
// 多选选中的数量
const handleSelectionChange = (val) => {
  selectList.value = val.map(item => item.id)
}
// 重置
const clickResetForm = () => {
  activeDeviceInfo.value = {}
  currentPage.value = 1
  pageNum.value = 10
  activeDeviceInfo.value.dataTime = ''
  queryList()
}

// 行唯一标识
const rowKey = (row) => {
  return row.id;
}

const handleExpandChange = async (row, expandedRows) => {
  const isExpanded = expandedRows.includes(row);

  if (isExpanded) {
    // 行已展开，加载数据并初始化地图
    console.log('展开行:', row.id);

    // 设置加载状态
    row.mapLoading = true;

    try {
      // 加载详情数据
      const res = await findInspectionTaskDetailById({ id: row.id });
      const siteList = res.data.inspectionSiteList || [];

      // 更新行的展开数据
      row.expandData = siteList;

      // 等待DOM更新
      await nextTick();

      // 初始化地图
      await initMap(row);

    } catch (error) {
      console.error('加载详情数据失败:', error);
      row.expandData = [];
    } finally {
      row.mapLoading = false;
    }
  } else {
    // 行收起，销毁地图
    destroyMap(row.id);
  }
};

const destroyMap = (id) => {
  const map = mapInstances.value.get(id);
  if (map) {
    map.clearMap();
    map.destroy();
    mapInstances.value.delete(id);

    // 重置行的地图状态
    const row = InvoiceList.value.find(item => item.id === id);
    if (row) {
      row.mapInitialized = false;
    }
  }
};

const listLoading = ref(false)

// 初始化
onMounted(async () => {
  // 预加载高德地图
  await preloadAMap();

  // 查询列表
  queryList();

  // 初始化表格高度
  setTimeout(() => {
    setTableMaxHeight();
  }, 100);
  getUserListByPage()
  console.log(userInfo.value.id, 'userInfo.value.id')
  userId.value = userInfo.value.id
});
const getUserListByPage = () => {
  SystemsetController.findUserListByPage({
    page: 1,
    size: 1000,
    tenantId: userInfo.value.tenantId,
  }).then((res) => {
  });
}

const initMap = async (row) => {
  const containerId = `map-container-${row.id}`;

  // 如果已经初始化过，先销毁
  if (row.mapInitialized) {
    destroyMap(row.id);
  }

  // 确保有坐标数据
  const coords = (row.expandData || [])
    .filter(item => item.longitude && item.latitude)
    .map(item => [
      Number(item.longitude),
      Number(item.latitude)
    ]);

  console.log(`行${row.id}坐标数量:`, coords.length);

  if (coords.length === 0) {
    console.warn(`行${row.id}没有有效的坐标数据`);
    return;
  }

  // 等待容器渲染完成
  await waitForContainer(containerId);

  // 获取AMap实例
  const AMap = await preloadAMap();
  if (!AMap) {
    console.error('AMap未加载成功');
    return;
  }

  try {
    // 再次确认容器存在
    const container = document.getElementById(containerId);
    if (!container) {
      throw new Error(`地图容器 ${containerId} 不存在`);
    }

    // 设置容器样式
    container.style.width = '100%';
    container.style.height = '400px';
    container.style.display = 'block';

    // 初始化地图
    const map = new AMap.Map(containerId, {
      zoom: coords.length === 1 ? 8 : 7,
      center: coords[0],
      viewMode: '2D',
      resizeEnable: true
    });

    // 添加工具条
    map.plugin(['AMap.ToolBar'], () => {
      map.addControl(new AMap.ToolBar({
        position: 'RT'
      }));
    });

    // 添加标记点
    const markers = [];
    coords.forEach(coord => {
      const marker = new AMap.Marker({
        position: coord,
        map: map
      });
      markers.push(marker);
    });

    // 如果有多个点，调整视图
    if (coords.length > 1) {
      map.setFitView(markers);
    }

    // 保存地图实例
    mapInstances.value.set(row.id, map);
    row.mapInitialized = true;

    console.log(`行${row.id}地图初始化成功`);

  } catch (error) {
    console.error(`行${row.id}地图初始化失败:`, error);
  }
};

// 等待容器渲染完成
const waitForContainer = (containerId, maxRetries = 10) => {
  return new Promise((resolve, reject) => {
    let retries = 0;

    const checkContainer = () => {
      const container = document.getElementById(containerId);

      if (container && container.offsetHeight > 0) {
        resolve(true);
      } else if (retries < maxRetries) {
        retries++;
        setTimeout(checkContainer, 100);
      } else {
        reject(new Error(`容器 ${containerId} 未找到或没有尺寸`));
      }
    };

    checkContainer();
  });
};

const addTaskVisible = ref(false);
const tableRef = ref(null);
const nodePersonVisible = ref(false);
// 详情
const detailsVisible = ref(false);

const clickDetails = (row, value) => {
  detailsVisible.value = true;
  inspectionType.value = row.taskStatus;
  inspectionArray.value = row;
  titleValue.value = value
}

const setTableMaxHeights = () => {
  let tableContentHeight = tableContentRef.value.offsetHeight;
  tableMaxHeights.value = tableContentHeight;
};

// 新增/删除/节点人员设置
const clickOperateBut = (type) => {
  // 新增
  if (type === 1) {
    addTaskVisible.value = true;
  } else if (type === 2) {
    // 删除
    if (selectList.value.length === 0) {
      ElMessage({ type: "warning", message: "请选择要删除的行", showClose: true });
      return;
    } else {
      ElMessageBox.confirm(
        `确认删除选中的${selectList.value.length}条巡检项配置吗？`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      ).then(() => {
        deleteInspectionTaskByIds({ ids: selectList.value }).then(res => {
          if (res.success) {
            ElMessage({ type: "success", message: "删除成功", showClose: true });
            queryList();
          }
        });
      }).catch(() => {
        ElMessage({ type: "info", message: "已取消删除", showClose: true });
        tableRef.value?.clearSelection();
      });
    }
  } else if (type === 3) {
    // 节点人员设置
    nodePersonVisible.value = true;
  }
}

const saveDialog = () => {
  detailsVisible.value = false;
  addTaskVisible.value = false;
  nodePersonVisible.value = false;
  queryList();
}
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;
}

// 自定义展开行样式
.custom-expand-table {
  ::v-deep .el-table__expand-icon {
    transform: none !important;
    line-height: 16px;
    -webkit-transform: none !important;

    .el-icon {
      display: none;
    }

    &::before {
      font-family: "iconfont" !important;
      font-size: 16px;
      font-style: normal;
      -webkit-font-smoothing: antialiased;
      -moz-osx-font-smoothing: grayscale;
      content: "\e604";
      transition: all 0.3s ease;
    }

    &.el-table__expand-icon--expanded::before {
      content: "\e603";
      transform: none !important;
    }

    &:hover::before {
      transform: scale(1.1);
    }
  }
}

.expand-content {
  .map-wrapper {
    position: relative;

    .map-table {
      position: absolute;
      width: 40%;
      z-index: 9;
      opacity: 0.8;
      right: 1%;
      top: 2%;
      overflow-y: auto;
      background: white;
      border-radius: 4px;
    }

    .err-num {
      color: rgba(59, 170, 245, 1);
    }

    .map-container {
      width: 100%;
      height: 400px;
      border-radius: 6px;
      border: 1px solid #e4e7ed;
    }
  }

  .no-location {
    text-align: center;
    padding: 60px 0;
    color: #909399;

    .iconfont {
      font-size: 48px;
      margin-bottom: 12px;
      display: block;
      opacity: 0.6;
    }

    p {
      margin: 0;
      font-size: 14px;
    }
  }
}

:deep(.amap-info-window) {
  padding: 8px;

  h4 {
    margin: 0 0 8px 0;
    color: #333;
    font-size: 14px;
  }

  p {
    margin: 4px 0;
    color: #666;
    font-size: 12px;
  }
}
.table_operate_class{
  gap:10px;
}
</style>