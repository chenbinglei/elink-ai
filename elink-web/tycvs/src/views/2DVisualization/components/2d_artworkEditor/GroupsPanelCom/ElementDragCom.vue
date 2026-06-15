<template>
  <el-row class="elementDragCom">
    <template v-if="graphical_list && graphical_list.length">
      <template v-for="(item,index) in graphical_list" :key="index">
        <template v-if="item.type === 1">
          <lx-collapse ref="lxCollapseRef" :title="item.fieldNameCn || item.name">
            <template #content>
              <template v-if="item.children && item.children.length">
                <el-col :span="item.type === 1 ? 24 : 8">
                  <ElementDragCom v-if="item.type === 1" :list="item.children"></ElementDragCom>
                  <template v-else>
                    <DragElementCom v-for="(graph_t,graph_i) in item.children" :key="graph_i" :graphInfo="graph_t"></DragElementCom>
                  </template>
                </el-col>
              </template>
              <template v-else><null-data></null-data></template>
            </template>
          </lx-collapse>
        </template>
        <el-col v-else :span="8"><DragElementCom :graphInfo="item"></DragElementCom></el-col>
      </template>
    </template>
  </el-row>
</template>

<script lang="ts">
import DragElementCom from "./DragElementCom.vue";
import {LxCollapse} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, watch, ref} from "vue";

export default defineComponent({
  name: 'ElementDragCom',
  components: {LxCollapse, DragElementCom},
  props: {
    list: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {

    const lxCollapseRef = ref([]);
    const that = reactive({
      draggable: true,
      graphical_list: [],
    })

    // 折叠打开列表
    const clickCollapseFun = () => {
      that.collapseStatus = !that.collapseStatus;
      if (lxCollapseRef && lxCollapseRef.value) {
        for (let i = 0; i < lxCollapseRef.value.length; i++) {
          lxCollapseRef.value[i].isCollapse = that.collapseStatus;
        }
      }
    }

    const watchList = watch(() => props.list, (newList) => {
      that.graphical_list = JSON.parse(JSON.stringify(newList));
    }, {deep: true, immediate: true})

    return {...toRefs(that), clickCollapseFun, lxCollapseRef, watchList}
  }
})
</script>

<style lang="scss" scoped>
:deep(.nullData) {
  .noCartImages {
    max-width: 100px !important;
  }

  .textFont {
    color: var(--color);
    font-size: 12px;
  }
}
</style>