<template>
  <div class="wrap ">
    <div class="h-32px pb-15px box-content flex justify-between">
      <div class="app_top_left">
        <div class="app_top_left_title">选择场站</div>
        <el-select v-model="siteId" placeholder="请选择站点" filterable clearable @change="handleSelectChange">
          <el-option :label="item.siteName" :value="item.id" v-for="item in siteList" :key="item.id" />
        </el-select>
      </div>
      <div>
        <el-button type="primary" @click="ApplicationSite">应用到其他站点</el-button>
      </div>
    </div>
    <div class="content w-full pb-15px box-content flex justify-between">
      <div class="left-content-tree w-1/5">
        <div class="tree-tip">
          <img alt="" class="list_icon" src="@/assets/image/table_icon.png" />
          <span class="text">电价策略</span>
        </div>
        <el-tree class="tree" :data="priceList" default-expand-all :props="defaultProps" node-key="id"
          :expand-on-click-node="false" ref="treeRef">
          <template #default="{ node, data }">
            <div class="custom-tree-node">
              <span class="tree-node-label" @click="handleNodeClick(node, data)">{{ node.label }}</span>
              <div v-if="node.level === 1" @click="addpricesDialog('新增', node.data)">新增</div>
              <div class="tree-node-level" v-if="node.level === 2">
                <span @click="addpricesDialog('编辑', node.data)">编辑</span>
                <span @click="addpricesDialog('复制', node.data)">复制</span>
                <span @click="deleteElectConfig(node.data)">删除</span>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
      <div class="tree-right w-4/5">
        <div style="height: 100%;width: 100%;" v-if="PriceInformation.id">
          <div class="tree-tip">
            <div class="tree-tip-left">
              <img alt="" class="list_icon" src="@/assets/image/table_icon.png" />
              <span class="text">电价信息</span>
            </div>
            <div style="cursor: pointer;">
              <span class="text-button" @click="addpricesDialog('编辑', PriceInformation)">编辑</span>
              <span class="text-button" @click="deleteElectConfig(PriceInformation)">删除</span>
            </div>
          </div>
          <div class="tree-message">
            <div>策略名称：{{ PriceInformation?.strategyName }}</div>
            <div class="tree-message-content">生效时间：{{ PriceInformation?.startDate }} 至 {{ PriceInformation?.endDate }}
            </div>

          </div>
          <div class="tree-chart">
            <div ref="chartDom" style="width:100%; height:100%"></div>
          </div>
        </div>
        <div v-else style="width: 100%; height: 100%; display: flex; align-items: center; justify-content: center;">
          <el-empty :image="emptyImg" description="暂无数据" />
        </div>
      </div>
    </div>
    <addpricesStrategy :isVisible="addpricesStrategyRef" :siteId="siteId" :moduleType="moduleType" :title="title"
      :activePricingId="activePricingId" @close="changeEvent" />

    <ApplyToOtherSitesDialog v-if="applyToOtherSitesVisible" v-model:isVisible="applyToOtherSitesVisible"
      :siteId="siteId" :activePricingId="activePricingId" :priceList="priceList" />
    <!-- <el-dialog v-model="dialogFormVisible" title="复制电价策略" width="40%" @close="dialogcopyVisible">
      <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px" style="margin-top: 20px;">
        <el-form-item label="策略名称：" prop="strategyName">
          <el-input style="max-width: 300px" v-model="formDialog.strategyName" placeholder="请输入策略名称" type="text"
            max="32" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer mt-12px justify-end">
          <el-button @click="dialogcopyVisible()">取消</el-button>
          <el-button type="primary" @click="clickConfirmBut()">确定</el-button>
        </div>
      </template>
    </el-dialog> -->
  </div>

</template>

<script setup>
import addpricesStrategy from '@/views/asset/electricityPrice/components/addpricesStrategy.vue'
import ApplyToOtherSitesDialog from "./components/ApplyToOtherSitesDialog.vue";
import { ref, onMounted, nextTick } from "vue";
import * as echarts from "echarts";
import { ElMessage, ElMessageBox } from "element-plus";
import emptyImg from "@/assets/image/empty.png";
import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";
import { queryElectConfigList, findElectConfigById, deleteAllElectConfigByIds, saveElectConfig } from "@/api/assetManagement/electricityPrice";
const activePricingId = ref('');
const siteList = ref([]);
const siteId = ref('');
const priceList = ref([
  {
    label: '电网电价',
    value: '1',
    id: "1",
    children: [],
  },
  {
    label: '光伏上网电价',
    value: '2',
    children: [],
    id: "2",
  },
  {
    label: '光伏消纳电价',
    value: '3',
    children: [],
    id: "3",
  },
  {
    label: '储能售电电价',
    value: '4',
    children: [],
    id: "4",
  },
  {
    label: '储能购电电价',
    value: '5',
    children: [],
    id: "5",
  },
  {
    label: '电桩售电电价',
    value: '6',
    children: [],
    id: "6",
  },
  {
    label: '电桩购电电价',
    value: '7',
    children: [],
    id: "7",
  },
]);
const formDialogRef = ref();
const formDialog = ref({
  strategyName: ''
})
const treeRef = ref(null);
const defaultProps = {
  children: 'children',
  label: (node) => {
    return node.label || node.strategyName; // 优先用 label，没有则用 start
  }
}
const PriceInformation = ref({}) //电价信息

const addpricesStrategyRef = ref(false);
const applyToOtherSitesVisible = ref(false); //应用到其他站点
const moduleType = ref('');
const chartDom = ref(null); // 用于存储 DOM 元素
let myChart = null; // 存储 ECharts 实例
const dialogFormVisible = ref(false)
const copyList = ref({})
onMounted(() => {
  getSiteList();

});
const copyNode = (node) => {
  copyList.value = node;
  dialogFormVisible.value = true;
}
// 验证规则
const rules = {
  strategyName: [
    { required: true, message: '请输入策略名称', trigger: 'blur' },
    { max: 32, message: '最多输入32个字符', trigger: 'blur' }
  ]
};
const clickConfirmBut = () => {
  formDialogRef.value.validate((valid) => {
    if (valid) {
      let obj = {
        ...copyList.value,
        strategyName: formDialog.value.strategyName,
        electTimeFrames: copyList.value.electTimeFrameList,
      }
      delete obj.id;
      saveElectConfig(obj).then(() => {
        dialogcopyVisible()
        ElMessage({ type: "success", showClose: true, message: "复制成功！" });
        getqueryElectConfigList()
      });
    }
  });
}
const dialogcopyVisible = () => {
  dialogFormVisible.value = false;
  formDialogRef.value?.resetFields(); // 重置表单和验证
};
// 初始结构（用于重置）
const initialPriceList = JSON.parse(JSON.stringify(priceList.value));
// 获取电价策略配置列表
const getqueryElectConfigList = () => {
  queryElectConfigList({ siteId: siteId.value }).then(res => {
    priceList.value = JSON.parse(JSON.stringify(initialPriceList));
    const list = mapJsonToPriceList(res.data, priceList.value);
    priceList.value = list;
    if (PriceInformation.value !== null) {
      const firstNonEmptyChild = list.find(item => item.children && item.children.length > 0);
      if (firstNonEmptyChild) {
        PriceInformation.value = firstNonEmptyChild.children[0]
        treeRef.value.setCurrentKey(PriceInformation.value.id); // 设置默认选中
        getOptionList(firstNonEmptyChild.children[0].electTimeFrameList);
      }
      else {
        PriceInformation.value = {}
        getOptionList([])
      }
    }

  });
};
function mapJsonToPriceList (jsonData, val) {
  const result = JSON.parse(JSON.stringify(val));

  for (const key in jsonData) {
    const matched = result.find(item => item.value === key);

    if (matched) {
      matched.children = jsonData[key];
    }
  }

  return result; // 返回处理后的结果
}

const getfindElectConfigById = (id) => {
  if (id) {
    findElectConfigById({ id: id }).then(res => {
      PriceInformation.value = res.data
      getOptionList(res.data.electTimeFrameList);
    })
  }
}
const handleNodeClick = (node) => {

  if (node.level == '2') {
    getfindElectConfigById(node.data.id)
  }
}
const changeEvent = (val) => {
  addpricesStrategyRef.value = false;
  getqueryElectConfigList();
  if (val != undefined) {
    getfindElectConfigById(val)
  }
}


// 获取场站列表
const getSiteList = async () => {
  const res = await findSiteListByUserId({});
  siteList.value = res.data
  siteId.value = res.data[0].id;
  getqueryElectConfigList();

};
const title = ref('')
// 打开弹窗
const addpricesDialog = (type, node) => {
  moduleType.value = node.moduleType ? node.moduleType : node.value;
  activePricingId.value = type == '新增' ? '' : node.id;
  title.value = type;
  addpricesStrategyRef.value = true;
}
// 应用到其他站点
const ApplicationSite = () => {
  applyToOtherSitesVisible.value = true;
}
const destroyChart = () => {
  if (myChart && typeof myChart.dispose === 'function') {
    myChart.dispose();
    myChart = null;
  }
}
// 删除电价策略
const deleteElectConfig = (value) => {
  ElMessageBox.confirm(
    `您确定要取消当前电价策略“<span class="highlightText">${value.strategyName}</span>”吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true,
      beforeClose: (action, instance, done) => {
        if (action === 'confirm') {
          // 点击“确定”时执行
          instance.confirmButtonLoading = true;
          instance.confirmButtonText = '正在删除...';

          deleteAllElectConfigByIds({ ids: [value.id] }).then(res => {
            if (res.code === 20000) {
              ElMessage.success('删除成功');
              // if (value.id === PriceInformation.value.id) {
              //   PriceInformation.value = {};
              // }
              getqueryElectConfigList();
            } else {
              ElMessage.error('删除失败');
            }
          }).catch(() => {
            ElMessage.error('网络错误');
          }).finally(() => {
            instance.confirmButtonLoading = false;
            instance.confirmButtonText = '确定';
            done(); // 执行关闭
          });
        } else {
          // 点击“取消”或关闭弹窗
          done();
        }
      },
    }
  ).catch(() => {
    ElMessage({
      type: 'info',
      message: '取消删除',
    });
  });
};
const getOptionList = (data) => {
  destroyChart();
  if (data?.length === 0) {
    return;
  }

  // 生成 48 个 30 分钟时间段（00:00-00:29, 00:30-00:59, ..., 23:30-23:59）
  const thirtyMinutes = Array.from({ length: 48 }, (_, i) => {
    const hour = Math.floor(i / 2);
    const minute = i % 2 === 0 ? '00' : '30';
    const start = `${String(hour).padStart(2, '0')}:${minute}`;
    const end = `${String(hour).padStart(2, '0')}:${minute === '00' ? '29' : '59'}`;
    return `${start}-${end}`;
  });

  // 时间转分钟（用于数据匹配）
  function timeToMinutes (timeStr) {
    const [h, m] = timeStr.split(':').map(Number);
    return h * 60 + m;
  }

  // 定义 periodType 到中文的映射
  const periodTypeMap = {
    1: '尖',
    2: '峰',
    3: '平',
    4: '谷',
    5: '深',
    6: '全天'
  };

  // 构建每个时间段的 bar 系列
  const series = data?.map(item => {
    const startMin = timeToMinutes(item.startTime);
    const endMin = timeToMinutes(item.endTime);

    const data = thirtyMinutes.map((time, index) => {
      const [start, end] = time.split('-');
      const startMinTime = timeToMinutes(start);
      const endMinTime = timeToMinutes(end);

      return (startMinTime >= startMin && endMinTime <= endMin) ? item.electMoney : null;
    });

    const firstNonEmptyIndex = data.findIndex(d => d !== null);
    const lastNonEmptyIndex = data.findLastIndex(d => d !== null);
    const midIndex = Math.floor((firstNonEmptyIndex + lastNonEmptyIndex) / 2);

    let color = '#3399ff';
    if (item.periodType === 1) {
      color = '#FB6868';
    } else if (item.periodType === 2) {
      color = '#FD9449';
    } else if (item.periodType === 3) {
      color = '#56ADF7';
    } else if (item.periodType === 4) {
      color = '#6DCF36';
    } else if (item.periodType === 5) {
      color = '#36CFC2';
    }else{
      color = '#3399ff';

    }

    return {
      name: periodTypeMap[item.periodType] || '未知时段',
      type: 'bar',
      data: data,
      barWidth: '103%',
      barGap: '-100%',
      // label: {
      //   show: (params) => {
      //     return params.dataIndex === midIndex;
      //   },
      //   position: 'top',
      //   formatter: (params) => {
      //     if (params.dataIndex !== midIndex) return '';
      //     return params.value.toFixed(2);


      //   },
      //   color: color,
      //   fontSize: 15,
      //   fontWeight: '320',
      //   padding: [2, 4],
      // },
      itemStyle: { color },
      emphasis: {
        itemStyle: {
          color: null,
          opacity: 1
        }
      },
      blur: {
        itemStyle: {
          color: null,
          opacity: 1
        }
      },
      select: {
        disabled: true
      },
    };
  });

  // 提取所有唯一的 periodType
  const uniquePeriodTypes = [...new Set(data.map(item => item.periodType))];
  // ${validParams[0].seriesName}
  const formatter = (params) => {
    const validParams = params.filter(p => p.value !== undefined && p.value !== null);
    let htmlText = `<div class='custom-tooltip-style'>
      <div class='custom-tooltip-title'>${validParams[0].axisValueLabel}</div>
     <div class='custom-tooltip-content'>
       <div class='custom-radio' style='background-color:${validParams[0].color};'></div>
      <div class="custom-title">价格 :</div>
      <div class="custom-tooltip-value">${validParams[0].value.toFixed(4)} 元 / 度</div></div>
      </div>`;
    return htmlText;
  }


  // 映射为中文名称
  const legendData = uniquePeriodTypes.map(type => periodTypeMap[type] || '未知时段');

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'none' // 去掉引导线
      },
      className: "custom-tooltip-box",
      formatter: formatter,
    },
    legend: {
      right: '10%',
      textStyle: {
        color: '#fff',
        fontSize: 14
      }
    },
    xAxis: {
      type: 'category',
      data: thirtyMinutes,
      axisLabel: {
        formatter: (value, index) => {
          if (index === 47) {
            return '23:30-23:59'; // 最后一个点显示为完整格式
          }
          return value; // 显示完整的时段
        },
        interval: 4,
        // rotate: 45,
        // margin: 20
      },
      axisTick: {
        show: false,
        interval: 1
      },
      splitLine: {
        show: false
      }
    },
    yAxis: {
      splitLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.3)',
          width: 1,
          type: 'solid'
        }
      }
    },
    series: series
  };

  nextTick(() => {
    if (!chartDom.value) return;

    myChart = echarts.init(chartDom.value);
    myChart.setOption(option);
  });
};
// 选择场站
const handleSelectChange = (val) => {
  getqueryElectConfigList()
}

</script>

<style lang="scss" scoped>
.wrap {
  box-sizing: border-box;
  padding: 10px 24px;
  height: calc(100vh - 130px);

  
  .app_top_left {
    width: 20%;
    display: flex;
    align-items: center;
    color: #fff;

    .app_top_left_title {
      white-space: nowrap;
      /* 禁止换行 */
      margin-right: 10px;
    }
  }

  .content {
    height: calc(100% - 50px);
    
    .left-content-tree {
      box-sizing: border-box;
      padding: 20px;
      border: 1PX solid rgb(44, 74, 98);
      font-size: 18px;
      color: rgba(255, 255, 255, 0.7);
      overflow: auto;

      .tree-tip {
        display: flex;
        align-items: center;

      }

      .list_icon {
        width: 32px;
        margin-right: 5px;
      }

      .tree {
        margin-top: 10px;



        .custom-tree-node {
          width: 100%;
          display: flex;
          justify-content: space-between;
          color: rgb(11, 190, 255);

          .tree-node-label {
            width: 85%;
            color: rgba(255, 255, 255, 0.7);
          }

          .tree-node-level {


            span {
              margin-left: 10px;
            }
          }

        }
      }
    }

    .tree-right {
      box-sizing: border-box;
      padding: 20px;
      border: 1PX solid rgb(44, 74, 98);
      font-size: 16px;
      color: rgba(255, 255, 255, 0.7);
      margin-left: 20px;

      .tree-tip {
        display: flex;
        justify-content: space-between;

        .tree-tip-left {
          display: flex;
          align-items: center;
        }

        .list_icon {
          width: 32px;
          margin-right: 5px;
        }

        .text-button {
          margin-left: 30px;
          color: rgb(11, 190, 255);
        }
      }

      .tree-message {
        margin-top: 26px;
        display: flex;

        .tree-message-content {
          margin-left: 10%;
        }
      }

      .tree-chart {
        margin-top: 26px;
        width: 100%;
        height: 88%;
        // background-color: #fff;
      }
    }
  }
}

::v-deep .el-tree-node {
  margin-top: 5px;
}

::v-deep .el-tree-node__content {
  padding-left: 0 !important;
}

.tree ::v-deep .el-tree-node.is-current>.el-tree-node__content {
  background-color: rgba(64, 158, 255, .2) !important;
  color: #fff !important;
}

:deep(.custom-tooltip-box) {
  padding: 0 !important;
  border: none !important;
  background-color: transparent !important;

  // 给子盒子自定义样式
  .custom-tooltip-style {

    background: rgba(0, 47, 78, 0.9);
    box-shadow: inset 0px 0px 8px 1px #00ccff;
    border-radius: 3px 3px 3px 3px;
    padding: 5px 15px;

    .custom-tooltip-title {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }

    .custom-tooltip-content {
      display: flex;
      align-items: center;
    }

    .custom-radio {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      display: inline-block;
      margin-right: 5px;
      box-sizing: border-box;

    }

    .custom-title {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }

    .custom-tooltip-value {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      margin-left: 20px;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }
  }
}
</style>