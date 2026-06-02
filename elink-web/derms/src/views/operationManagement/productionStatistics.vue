<template>
  <!-- 储能运营-->
  <div class="app-container">
    <div class="app-container-right">
      <template v-if="routeMenuList && routeMenuList.length">
        <div class="content_border content_top">
          <Tabs :tabsArray="routeMenuList" v-model:tabsIndex="componentName" label="label"></Tabs>
          <div style="width: 20%;">
            <el-select v-model="siteId" placeholder="请选择站点" filterable clearable @change="handleSelectChange">
              <el-option :label="item.siteName" :value="item.id" v-for="item in siteList" :key="item.id" />
            </el-select>
          </div>
        </div>
        <component :is="componentName" :componentName="componentName" :siteArray="siteArray" :siteId="siteId"></component>

      </template>
      <template v-else><null-data words="请联系管理员开放权限！！！"></null-data></template>
    </div>
  </div>
</template>


<script>
import { getLeftTreeDataFun } from "@/utils";
import {
  defineComponent,
  getCurrentInstance,
  onMounted,
  reactive,
  toRefs,
} from "vue";
import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";
import energyChargingDischarging from "./_components/operationManagement/EnergyStorageOperation/EnergyStorageStatistics/energyChargingDischarging.vue";
import energyStorageProfit from "./_components/operationManagement/EnergyStorageOperation/EnergyStorageStatistics/energyStorageProfit.vue";
import countStorageRevenue from "./_components/operationManagement/EnergyStorageOperation/EnergyStorageStatistics/countStorageRevenue.vue";
import { findSiteInfoByUserId } from "@/api/operationManagement/CsStationManagement";

import { useRoute, useRouter } from 'vue-router';
export default defineComponent({
  name: "productionStatistics",
  props: {
    routeInfo: {
      type: Object,
      default: () => {
        return {};
      },
    },
  },
  components: {
    energyChargingDischarging,
    energyStorageProfit,
    countStorageRevenue,

  },
  setup (props) {
    const route = useRoute();
    const { emit } = getCurrentInstance();

    const that = reactive({
      componentName: "",
      routeMenuList: [],
      siteId: "",
      siteList: [],
      siteArray: {},
    });

    const changEvent = (data) => {
      emit("changEvent", data);
    };
    const findRouteMenuListFun = () => {
      if (props.routeInfo.childComponentName)
        that.componentName = props.routeInfo.childComponentName;
      that.routeMenuList = getLeftTreeDataFun(
        "/operationManagement/productionStatistics",
        0
      );
    };
    // 查询站点下拉列表
    const getSiteList = async () => {
      const res = await findSiteInfoByUserId({ scenarioTypes: "2", timer: new Date() });
      that.siteList = res.data
      that.siteId = res.data[0].id;
      that.siteArray = res.data[0];
    };
    const handleSelectChange = (val) => {
      that.siteList.forEach(item => {
        if (item.id === val) {
          that.siteArray= item;
        }
      })
      that.siteId = val;
    };
    onMounted(() => {
      findRouteMenuListFun();

      getSiteList();
    });

    return { ...toRefs(that), findRouteMenuListFun, changEvent, getSiteList, handleSelectChange };
  },
});
</script>

<style lang="scss" scoped>
.app-container {
  position: relative;
  flex-direction: column;

  .content_body {
    width: 100%;
    height: 96%;
    display: flex;
    align-items: center;

    .content_body_left {
      width: 300px;
      margin-right: 10px;
    }

    .content_body_right {
      flex: 1;
      width: 2px;
      height: 100%;
      flex-basis: auto;


      .content_class {
        width: 100%;
        height: 100%;
      }

      .secondary_details {
        width: 100%;
        height: 100%;
        z-index: 100;
        border-radius: 6px;
        transition: all 0.28s;
      }
    }

    .content_border {
      height: 100%;
      width: 100%;


    }
  }
}

.content_border {
  /* margin-top: 10px; */
  padding: 10px;
  display: flex;
}
</style>