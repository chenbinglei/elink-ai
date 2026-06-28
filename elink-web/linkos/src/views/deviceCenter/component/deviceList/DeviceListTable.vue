<template>
  <div class="app-container-right" v-resize="setTableMaxHeight">
    <div class="header-form" ref="headerFormRef">
      <el-form :model="formInline" inline>
        <el-form-item label="关键字：">
          <el-input v-model="formInline.keyword" clearable placeholder="请输入关键词搜索"></el-input>
        </el-form-item>
        <el-form-item label="通信状态：">
          <el-select v-model="formInline.txStatus" clearable placeholder="请选择通信状态">
            <el-option v-for="item in deviceStatusArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="接入类型：">
          <el-select v-model="formInline.accessType" clearable placeholder="请选择接入类型">
            <el-option v-for="item in accessTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备类型：">
          <el-tree-select class="leftArrowClass" v-model="formInline.typeId" :indent="0" :data="deviceTypeList" :props="treeProps"
                          filterable default-expand-all :render-after-expand="false" placeholder="请选择设备类型"/>
        </el-form-item>
        <el-form-item label="告警状态：">
          <el-select v-model="formInline.alarmStatus" clearable placeholder="请选择告警状态">
            <el-option v-for="item in alarmStatusArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button class="whiteFontButtons" :icon="Search" @click="listArray('refresh')">查询</el-button>
          <el-button class="blackFontButtons" :icon="Refresh" @click="clickResetForm">重置</el-button>
        </el-form-item>
        <el-form-item>
          <el-button :icon="Folder" class="whiteFontButtons" :disabled="isBatchAddButtonClick"  @click="clickAddBut(2)">文件批量添加</el-button>
          <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="clickAddBut(1)">添加设备</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div v-loading="listLoading" class="tableContent">
      <div ref="tableCenterRef" class="tableCenter">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" border stripe>
          <el-table-column align="center" label="序号" type="index" width="80">
            <template #default="{$index}">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="设备名称" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.deviceName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="所属模型">
            <template #default="{ row }">
              <div class="flex-jc-ai-center modelName">
                <div class="logoPath">
                  <el-image :src="row.logoPath ? row.logoPath : ''">
                    <template #error>
                      <div class="image-slot">
                        <el-icon><Picture/></el-icon>
                      </div>
                    </template>
                  </el-image>
                </div>
                <el-tooltip effect="dark" placement="top-start">
                  <div class="textTwo">{{ $filters.moreData(row.modelName) }}</div>
                  <template #content>{{ $filters.moreData(row.modelName) }}</template>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="center" label="设备序列号" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.deviceNumber) }}</template>
          </el-table-column>
          <el-table-column align="center" label="通信状态">
            <template #default="{ row }">
              <span class="txStatus" :class="'txStatus' + row.txStatus">
                <span>{{ $filters.deviceStatus(row.txStatus) }}</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="设备类型" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.typeName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="接入类型">
            <template #default="{ row }">{{ $filters.accessType(row.accessType) }}</template>
          </el-table-column>
          <el-table-column align="center" label="所属站点">
            <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="告警状态">
            <template #default="{ row }">
              <span class="alarmStatus" :class="'alarmStatus' + row.alarmStatus">
                <span>{{ $filters.alarmStatus(row.alarmStatus) }}</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template #default="{ row }">
              <el-link :underline="false" @click="clickOperateBut(1, row)">详情</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" type="danger" @click="clickOperateBut(2, row)">删除</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="paginationRef">
        <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
      </div>
    </div>

    <add-device-dialog v-if="addDeviceVisible" v-model:isVisible="addDeviceVisible" :titleName="titleName" :activeSiteId="activeSiteId" @changeEvent="listArray('refresh')" />
    <batch-add-device-dialog v-if="batchAddDeviceVisible" v-model:isVisible="batchAddDeviceVisible" :titleName="titleName" :activeSiteId="activeSiteId" @changeEvent="listArray('refresh')" />

  </div>
</template>

<script lang="ts">
import { useTagsViewStore } from '@/stores/index';

import {useRouter,useRoute} from "vue-router";
import AddDeviceDialog from "./AddDeviceDialog";
import BatchAddDeviceDialog from "./BatchAddDeviceDialog";
import {ElMessage, ElMessageBox, ElSwitch} from "element-plus";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import {deleteDeviceById, queryDeviceList} from "@/api/deviceCenter/deviceList";
import {CirclePlus, Folder, Picture, Search, Refresh} from "@element-plus/icons-vue";
import {reactive, toRefs, ref, watch, h, computed, onMounted, defineComponent} from "vue";
import {clickCopyValue, operateButtonIsClick, queryUserAuthorityIsHaveFun, setTreeData} from "@/utils";

export default defineComponent({
  name: "DeviceListTable",
  components:{Picture, AddDeviceDialog,BatchAddDeviceDialog},
  props: {
    activeSiteId: {
      type: [String, Number],
      default: ""
    },
    // 全部站点is
    handleMenuIds:{
      type: Array,
      default: ()=> []
    },
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup(props) {

    const tagsViewStore = useTagsViewStore();
    const route = useRoute();
    const vueRouter = useRouter();
    const deleteLogoRef = ref(false);

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/device/device/saveDevice')
    })

    const isBatchAddButtonClick = computed(()=>{
      return operateButtonIsClick('/device/device/batchInsertDevice')
    })

    const that = reactive({
      list: [],
      pageNum: 20,
      formInline: {},
      currentPage: 1,
      totalNumber: 0,
      oldFormInline: {},
      listLoading: false, // 表格加载
      tableMaxHeight: 300,
      CirclePlus, Folder, Search, Refresh,
      treeProps:{value: 'id', label: 'typeName', children: 'children'},

      titleName: "添加设备",
      addDeviceVisible: false,
      batchAddDeviceVisible: false,

      deviceTypeList: [],
      alarmStatusArray: [{id: 1, name: "无告警 "}, {id: 2, name: "有告警"}],
      accessTypeArray: [{id: 1, name: "直连设备 "}, {id: 2, name: "网关设备"}, {id: 3, name: "网关子设备"}],
      deviceStatusArray: [{id: 0, name: "未注册 "}, {id: 1, name: "在线"}, {id: 2, name: "故障"}, {id: 88, name: "离线"}],
    })

    // 查询模型标准功能列表
    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      formInline.siteIds = props.activeSiteId ? [props.activeSiteId] : props.handleMenuIds;

      if(!formInline.siteIds || !formInline.siteIds.length){
        that.listLoading = false;
        that.totalNumber = 0;
        that.list = [];
      }

      queryDeviceList({ page: that.currentPage, size: that.pageNum, ...formInline }).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if(operateType === "resetPage")ElMessage({ type: "success", message: "重置成功", showClose: true });
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886) return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const clickAddBut = (tabsIndex)=>{
      if(tabsIndex === 1){
        that.titleName = "添加设备";
        that.addDeviceVisible = true;
      }

      if(tabsIndex === 2){
        that.titleName = "批量添加设备";
        that.batchAddDeviceVisible = true;
      }
    }

    const clickOperateBut = (operate,row)=>{

      if(operate === 1){
        const routeName = "/deviceCenter/deviceDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if(!isAuthority){
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return
        }

        vueRouter.push({ path: routeName, query: { id: row.id,typeId: row.typeId,subTitle: row.deviceName } });
        tagsViewStore.addBackButViews({ id: row.id, backRouteName: route.path, showButRoute: routeName });
      }

      if(operate === 2){
        ElMessageBox({
          type: 'warning',
          title: "删除提示",
          showCancelButton: true,
          cancelButtonText: '取消',
          confirmButtonText: '确定',
          closeOnClickModal: false,
          customClass: "deleteMsgBoxClass",
          message: ()=> h('div',null,[
            h('div', null, [
              h('span', null, '确定删除'),
              h('span', { style: 'color: #FF7B7B;font-weight: bold' }, row.deviceName),
              h('span', null, '吗？'),
            ]),
            h('div', { style: 'display: flex;align-items: center' }, [
              h('span', null, '删除关联数据：'),
              h(ElSwitch,{
                modelValue: deleteLogoRef.value,
                'onUpdate:modelValue': (val) => {
                  deleteLogoRef.value = val;
                }
              })
            ]),
          ]),
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteDeviceById({ id: row.id,deleteLogo: deleteLogoRef.value }).then(()=>{
                done();
                instance.confirmButtonLoading = false;
              }).catch(() => {
                instance.confirmButtonText = '确定';
                instance.confirmButtonLoading = false;
              });
            } else {
              done();
            }
          }
        }).then(()=>{
          listArray("refresh");
          deleteLogoRef.value = false;
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    const clickResetForm = ()=>{
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    }

    // 复制
    const clickCopyBut = (row,fieldName)=>{
      clickCopyValue(row[fieldName]);
    }

    // 获取资产分类列表
    const queryAssetTypeList = ()=>{
      getAssetTypeList({ ids: [3],timer: new Date(),pageName: "DeviceListTable" }).then(res=>{
        let assetTypeList = res.data ? res.data : [];
        that.deviceTypeList = setTreeData(assetTypeList);
      })
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 150;
    }

    const watchActiveSiteId = watch(() => props.activeSiteId, (newActiveSiteId) => {
      listArray('refresh');
    }, { deep: true })

    onMounted(()=>{
      queryAssetTypeList();
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
    })

    return {...toRefs(that), headerFormRef, setTableMaxHeight, watchActiveSiteId, listArray, clickAddBut, clickOperateBut, deleteLogoRef, clickCopyBut,
      isAddButtonClick,isBatchAddButtonClick, clickResetForm, queryAssetTypeList}
  }
})
</script>

<style lang="scss" scoped>
// 设备列表筛选区域下拉框宽度调整
.header-form {
  :deep(.el-select) {
    .el-select__wrapper {
      width: 210px;
    }
  }
}

.txStatus,.alarmStatus{
  color: #666666;
}

.txStatus1{
  color: #41CB4A;
}
.txStatus2{
  color: #FF9C02;
}
.txStatus3,.alarmStatus2{
  color: #FF1515;
}
.modelName{

  .logoPath{
    width: 35px;
    height: 35px;

    .el-image {
      width: 100%;
      height: 100%;
      border-radius: 4px;
      border: 1px solid #DBDBDD;
      box-sizing: border-box;

      .image-slot {
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #DBDBDD;
        font-size: 32px;
        color: #F5F5F5;
      }
    }
  }

  .textTwo{
    flex: 1;
    margin-left: 4px;
    -webkit-line-clamp: 1;
  }
}
</style>