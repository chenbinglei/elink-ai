import { ref, onMounted, computed, onActivated } from "vue";
import { transformTreeData } from "@/utils/transform";
import { isNull } from "@/utils/validate";
import SystemMonitorController from "@/api/together/systemMonitor";
import useSiteId from "@/views/monitor/_hooks/useSiteId";

//暂时未开发的设备类型
const excludeTypeIds = [];

/**
 * @typedef {Object} TreeSwitchViewParams
 * @property {Object} viewMap - 设备类型id与对应视图组件的映射
 * @property {number} tabType - 页面类型id
 * @param {TreeSwitchViewParams} params
 * @returns
 */
export default function useTreeSwitchView({ viewMap, tabType = 1 }) {
  const { siteId } = useSiteId();

  const treeLoading = ref(false);
  const treeData = ref([]);
  const currentNodeKey = ref("");
  const currentNodeType = ref("");
  const currentView = computed(() => {
    
    return viewMap[currentNodeType.value] ?? viewMap["default"];
  });
  const defaultProps = {
    children: "children",
    label: "name",
    class: (data) => {
      return isNull(data.typeId) || excludeTypeIds.includes(data.typeId)
        ? "tree-node-no-click"
        : "";
    },
  };
  const handleNodeClick = (data, node) => {
    if (isNull(data.typeId) || excludeTypeIds.includes(data.typeId)) {
      node.isCurrent = false;
      const oldKey = currentNodeKey.value;
      currentNodeKey.value = "";
      requestAnimationFrame(() => {
        currentNodeKey.value = oldKey;
      });
      return;
    }
    currentNodeKey.value = data.id;
    currentNodeType.value = data.typeId;
  };

  const getTreeData = async () => {
    treeLoading.value = true;
    try {
      const { data } = await SystemMonitorController.getSystemTreeList(
        {
          siteId,
          type: tabType,
        },
        Date.now()
      );
      // 转化树数据
      data
        .filter((item) => item.levelType === 2)
        .forEach((item) => {
          item.typeId = data[0].typeId;
        });
      treeData.value = transformTreeData(data);
      currentNodeKey.value = treeData.value[0].id;
      currentNodeType.value = treeData.value[0].typeId;
    } catch (e) {
      treeData.value = [];
    } finally {
      treeLoading.value = false;
    }
  };

  onMounted(() => {
    getTreeData();
  });
  onActivated(() => {
    getTreeData();
  });

  return {
    treeLoading,
    currentNodeKey,
    treeProps: {
      data: treeData,
      nodeKey: "id",
      highlightCurrent: true,
      defaultExpandAll: true,
      expandOnClickNode: false,
      props: defaultProps,
      onNodeClick: handleNodeClick,
    },
    currentView,
    viewProps: {
      deviceId: currentNodeKey,
      deviceType: currentNodeType,
    },
  };
}
