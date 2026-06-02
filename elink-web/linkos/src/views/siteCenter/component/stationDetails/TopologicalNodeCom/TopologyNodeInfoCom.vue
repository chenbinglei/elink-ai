<template>
  <div class="topologyNodeInfoCom">
    <TitleView isContentHeight title="节点信息">
      <template #headerRight>
        <template v-if="activeNodeId">
          <el-button type="text" @click="clickEditNodeButFun">编辑</el-button>
        </template>
      </template>
      <template #content>
        <div v-if="activeNodeId" class="content_body">
          <div class="content_body_list">
            <div class="content_body_list_left">名称：</div>
            <div class="content_body_list_right">
              <!-- {{ $filters.moreData(returnDataInfo.nodeName) }} -->
              <el-tooltip :content="returnDataInfo.nodeName" effect="light" placement="bottom">
                {{ $filters.moreData(returnDataInfo.nodeName) }}
              </el-tooltip>
            </div>
          </div>
          <div class="content_body_list">
            <div class="content_body_list_left">类型：</div>
            <div class="content_body_list_right">
              <span class="nodeType">{{ $filters.toolNodeType(returnDataInfo.nodeType) }}</span>
            </div>
          </div>
          <template v-if="returnDataInfo.nodeType === 3">
            <div class="content_body_list">
              <div class="content_body_list_left">高压电压等级：</div>
              <div class="content_body_list_right">
                <span class="nodeType">{{ $filters.moreData(JSON.parse(returnDataInfo.reaObject).minVoltage) }}</span>
              </div>
            </div>
            <div class="content_body_list">
              <div class="content_body_list_left">低压电压等级：</div>
              <div class="content_body_list_right">
                <span class="nodeType">{{ $filters.moreData(JSON.parse(returnDataInfo.reaObject).minVoltage) }}</span>
              </div>
            </div>
          </template>
          <template
            v-if="returnDataInfo.nodeType === 4 || returnDataInfo.nodeType === 5 || returnDataInfo.nodeType === 6 || returnDataInfo.nodeType === 10 || returnDataInfo.nodeType === 11 || returnDataInfo.nodeType === 13">
            <div class="content_body_list">
              <div class="content_body_list_left">关联设备：</div>
              <!-- <div class="content_body_list_right">
                <span class="nodeType">{{returnDataInfo.deviceList?.map(item => item.deviceName).join(',')}}</span>
              </div> -->
            </div>
            <div class="content_body_list">
              <el-table :data="returnDataInfo.deviceList" border>
                <el-table-column label="类型">
                  <template #default="{ row }">{{ $filters.moreData(row.typeName) }}</template>
                </el-table-column>
                <el-table-column label="设备名称">
                  <template #default="{ row }">{{ $filters.moreData(row.deviceName) }}</template>
                </el-table-column>
                <el-table-column label="SN">
                  <template #default="{ row }">{{ $filters.moreData(row.deviceNumber) }}</template>
                </el-table-column>

              </el-table>
            </div>
          </template>
          <template v-if="returnDataInfo.nodeType === 4">
            <div class="content_body_list">
              <div class="content_body_list_left">变压器安全容量：</div>
              <div class="content_body_list_right">
                <span class="nodeType">{{ $filters.moreData(JSON.parse(returnDataInfo.reaObject).safeByq) }}</span>
              </div>
            </div>
          </template>

          <template v-if="returnDataInfo.nodeType === 7">
            <div class="content_body_list">
              <div class="content_body_list_left">光伏容量：</div>
              <div class="content_body_list_right">
                <span class="nodeType">{{ $filters.moreData(JSON.parse(returnDataInfo.reaObject).photoRl) }}</span>
              </div>
            </div>
          </template>
          <template v-if="returnDataInfo.nodeType === 8">
            <div class="content_body_list">
              <div class="content_body_list_left">多柜并机运行：</div>
              <div class="content_body_list_right">
                <span class="nodeType">{{ $filters.moreData(JSON.parse(returnDataInfo.reaObject).operation) == 1 ? '是' :
                  "否"
                }}</span>
              </div>
            </div>
            <div class="content_body_list">
              <div class="content_body_list_left">关联设备： </div>
            </div>
            <div class="content_body_list">
              <el-table :data="returnDataInfo.deviceList" border>
                <el-table-column label="类型">
                  <template #default="{ row }">{{ $filters.moreData(row.typeName) }}</template>
                </el-table-column>
                <el-table-column label="设备名称">
                  <template #default="{ row }">{{ $filters.moreData(row.deviceName) }}</template>
                </el-table-column>
                <el-table-column label="SN">
                  <template #default="{ row }">{{ $filters.moreData(row.deviceNumber) }}</template>
                </el-table-column>

              </el-table>
            </div>
          </template>
          <template v-if="returnDataInfo.nodeType === 4 ||
            returnDataInfo.nodeType === 5 ||
            returnDataInfo.nodeType === 6 ||
            returnDataInfo.nodeType === 8 ||
            returnDataInfo.nodeType === 10 ||
            returnDataInfo.nodeType === 11 ||
            returnDataInfo.nodeType === 13">
            <div class="content_body_list">
              <div class="content_body_list_left">数据项：</div>
            </div>
            <div class="content_body_list">
              <el-table :data="returnDataInfo.siteTopItemList" border>
                <el-table-column label="数据名称" show-overflow-tooltip>
                  <template #default="{ row }">{{ $filters.moreData(row.dataName) }}</template>
                </el-table-column>
                <el-table-column label="展示名称" show-overflow-tooltip>
                  <template #default="{ row }">{{ $filters.moreData(row.showName) }}</template>
                </el-table-column>
                <el-table-column label="数据展示" show-overflow-tooltip>
                  <template #default="{ row }">
                    <div style="display: flex; align-items: center;">
                      <span
                        :class="row.showType == 2 ? 'icon iconfont icon-yanjing_yincang_o' : 'iconfont icon-yanjing_xianshi_o'"
                        :style="{ color: row.showType == 1 ? '#2B66FE' : '#999' }"></span>
                      <span style="margin-left: 8px;">{{ row.showType == 1 ? '显示' : '隐藏' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="位置" show-overflow-tooltip>
                  <template #default="{ row }">{{ $filters.positionType(row.positionType) }}</template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </div>
        <null-data v-else words="暂无数据"></null-data>
      </template>
    </TitleView>
    <AddTopologyNodeDialog v-if="addTopologyNodeVisible" v-model:isVisible="addTopologyNodeVisible"
      :topNodeId="topNodeId" :siteRecordsId="siteRecordsId" :titleName="titleName"
      :activeParentNodeInfo="activeParentNodeInfo" @changeEvent="changeEventUpdata"></AddTopologyNodeDialog>

  </div>
</template>

<script>
import { reactive, toRefs, defineComponent, watch, getCurrentInstance, } from "vue";
import AddTopologyNodeDialog from "./AddTopologyNodeDialogVue.vue";
import { findTopNodeInfoById } from "@/api/siteCenter/stationDetails";

export default defineComponent({
  name: "TopologyNodeInfoCom",
  components: { AddTopologyNodeDialog },
  props: {
    siteRecordsId: {
      type: [String, Number],
      default: ""
    },
    activeNodeId: {
      type: [Number, String],
      default: ""
    }
  },
  setup (props) {
    const { emit } = getCurrentInstance();
    const that = reactive({
      list: [],
      returnDataInfo: {},
      listLoading: false,
      topNodeId: null,
      titleName: '添加节点',
      activeParentNodeInfo: {},
      addTopologyNodeVisible: false,

      gfbwd_list: [
        { name: "光伏总发电量", fieldName: "totalChargeQt" },
        { name: "尖时光伏发电量", fieldName: "jchargeQt" },
        { name: "峰时光伏发电量", fieldName: "fchargeQt" },
        { name: "平时光伏发电量", fieldName: "pchargeQt" },
        { name: "谷时光伏发电量", fieldName: "gchargeQt" },
        { name: "深谷光伏发电量", fieldName: "sgChargeQt" },
      ],
      cnbwd_list: [
        { name: "储能总充电量", fieldName: "totalChargeQt" },
        { name: "尖时储能充电量", fieldName: "jchargeQt" },
        { name: "峰时储能充电量", fieldName: "fchargeQt" },
        { name: "平时储能充电量", fieldName: "pchargeQt" },
        { name: "谷时储能充电量", fieldName: "gchargeQt" },
        { name: "深谷储能充电量", fieldName: "sgChargeQt" },
        { name: "储能总放电量", fieldName: "totalDischargeQt" },
        { name: "尖时储能放电量", fieldName: "jdischargeQt" },
        { name: "峰时储能放电量", fieldName: "fdischargeQt" },
        { name: "平时储能放电量", fieldName: "pdischargeQt" },
        { name: "谷时储能放电量", fieldName: "gdischargeQt" },
        { name: "深谷储能放电量", fieldName: "sgDischargeQt" },
      ],
      dzbwd_list: [
        { name: "电桩总充电量", fieldName: "totalChargeQt" },
        { name: "尖时电桩充电量", fieldName: "jchargeQt" },
        { name: "峰时电桩充电量", fieldName: "fchargeQt" },
        { name: "平时电桩充电量", fieldName: "pchargeQt" },
        { name: "谷时电桩充电量", fieldName: "gchargeQt" },
        { name: "深谷电桩充电量", fieldName: "sgChargeQt" },
        { name: "电桩总放电量", fieldName: "totalDischargeQt" },
        { name: "尖时电桩放电量", fieldName: "jdischargeQt" },
        { name: "峰时电桩放电量", fieldName: "fdischargeQt" },
        { name: "平时电桩放电量", fieldName: "pdischargeQt" },
        { name: "谷时电桩放电量", fieldName: "gdischargeQt" },
        { name: "深谷电桩放电量", fieldName: "sgDischargeQt" },
      ],
      qtbwd_list: [
        { name: "正向有功总电能", fieldName: "totalChargeQt" },
        { name: "尖时正向有功电能", fieldName: "jchargeQt" },
        { name: "峰时正向有功电能", fieldName: "fchargeQt" },
        { name: "平时正向有功电能", fieldName: "pchargeQt" },
        { name: "谷时正向有功电能", fieldName: "gchargeQt" },
        { name: "深谷正向有功电能", fieldName: "sgChargeQt" },
        { name: "反向有功总电能", fieldName: "totalDischargeQt" },
        { name: "尖时反向有功电能", fieldName: "jdischargeQt" },
        { name: "峰时反向有功电能", fieldName: "fdischargeQt" },
        { name: "平时反向有功电能", fieldName: "pdischargeQt" },
        { name: "谷时反向有功电能", fieldName: "gdischargeQt" },
        { name: "深谷反向有功电能", fieldName: "sgDischargeQt" },
      ]
    })

    // 根据拓扑节点id查询拓扑信息
    const queryTopoNodeInfoById = () => {
      that.listLoading = true;
      findTopNodeInfoById({ topNodeId: props.activeNodeId }).then(res => {
        let list = [];
        that.returnDataInfo = JSON.parse(JSON.stringify(res.data ?? {}));
        if (that.returnDataInfo.deviceType === 1) list = JSON.parse(JSON.stringify(that.gfbwd_list));
        if (that.returnDataInfo.deviceType === 2) list = JSON.parse(JSON.stringify(that.cnbwd_list));
        if (that.returnDataInfo.deviceType === 3) list = JSON.parse(JSON.stringify(that.dzbwd_list));
        if (that.returnDataInfo.deviceType === 4) list = JSON.parse(JSON.stringify(that.qtbwd_list));
        if (that.returnDataInfo.deviceIdList && that.returnDataInfo.deviceIdList.length) {
          that.returnDataInfo.deviceIds = that.returnDataInfo.deviceIdList;
          if (that.returnDataInfo.nodeType === 0) {
            that.returnDataInfo.deviceIds = that.returnDataInfo.deviceIdList[0];
          }
        }
        that.list = JSON.parse(JSON.stringify(list));
        that.listLoading = false;
      }).catch(() => {
        that.list = [];
        that.listLoading = false;
      })
    }

    const clickEditNodeButFun = () => {
      that.titleName = '编辑节点';
      that.activeParentNodeInfo = JSON.parse(JSON.stringify(that.returnDataInfo));
      that.topNodeId = that.returnDataInfo.id
      that.addTopologyNodeVisible = true;
    }
    const changeEventUpdata = () => {

      queryTopoNodeInfoById()
      //拓扑图进行更新
      emit("infoUpdata")
    }
    const watchActiveNodeId = watch(() => props.activeNodeId, (newActiveNodeId) => {
      if (newActiveNodeId) queryTopoNodeInfoById();
    }, { deep: true, immediate: true });

    return { ...toRefs(that), watchActiveNodeId, changeEventUpdata, queryTopoNodeInfoById, clickEditNodeButFun }
  }
})
</script>

<style lang="scss" scoped>
.topologyNodeInfoCom {
  width: 380px;
  height: 100%;
  box-sizing: border-box;
  padding: 10px 0 10px 10px;
  border-left: 1px solid #00000033;

  .content_body {
    height: 100%;
    overflow-y: auto;

    .content_body_list {
      margin-bottom: 16px;
      display: flex;
      align-items: center;

      .content_body_list_left {
        font-size: 16px;
        color: #00000080;
      }

      .content_body_list_right {
        flex: 1;
        font-size: 14px;
        color: #000000ff;
        white-space: nowrap;
        /* 禁止换行 */
        overflow: hidden;
        /* 超出部分隐藏 */
        text-overflow: ellipsis;
        /* 显示省略号 */
      }

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}
</style>