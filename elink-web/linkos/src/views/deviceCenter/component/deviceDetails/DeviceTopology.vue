<template>
  <div class="content_body" :style="{ height: contentMainMaxHeight + 'px'}">
    <TabBorderCard :tabsCardArray="tabsCardArray" v-model:tabsCardIndex="tabsCardIndex">
      <template #content>
        <div class="component_list" v-loading="listLoading">
          <component :is="tabsCardIndex" :key="tabsCardIndex" :list="list" @changeEvent="queryDeviceNodeListById"></component>
        </div>
      </template>
    </TabBorderCard>
  </div>
</template>

<script lang="ts">
import { useAppStore } from '@/stores/index';

import { TopologyMap,TopologyList} from "./component";
import {findDeviceNodeListById} from "@/api/deviceCenter/deviceList";
import {computed, onMounted, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "DeviceTopology",
  components:{ TopologyMap,TopologyList },
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {

    const appStore = useAppStore();
    const contentMainMaxHeight = computed(() => {
      return appStore.contentMainMaxHeight;
    });

    const that = reactive({
      list: [],
      listLoading: false, // 表格加载
      tabsCardIndex: "TopologyMap",
      tabsCardArray: [
        {id: "TopologyMap", name: "拓扑图"},
        {id: "TopologyList", name: "列   表"}
      ],
    })

    const queryDeviceNodeListById = ()=>{
      that.listLoading = true;
      findDeviceNodeListById({ deviceId: props.activeDeviceId }).then(res=>{
        that.list = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    onMounted(()=>{
      queryDeviceNodeListById();
    })

    return {...toRefs(that), queryDeviceNodeListById, contentMainMaxHeight }
  }
})
</script>

<style lang="scss" scoped>
.content_body {
  padding: 16px 12px;
  display: flex;
  flex-direction: column;

  .component_list{
    height: 100%;
    padding: 12px 16px;
    box-sizing: border-box;
  }
}
</style>
