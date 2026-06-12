import { ref, onMounted, onActivated, onDeactivated, computed } from "vue";
import { useRoute } from "vue-router";

import SystemsetController from "@/api/system/index";
import { ElMessage } from "element-plus";
export default function useTable(formData) {
  const route = useRoute();
  
  const tableRef = ref(null);
  const columns = ref([
    {
      title: "角色名称",
      key: "groupName",
      width: 159,
    },
    {
      title: "人数",
      key: "peopleNumber",
      width: 232,
    },
    {
      title: "描述",
      key: "refer",
      width: 159
    },
    {
      title: "操作",
      key: "operate",
      list: [
        { name: "管理人员", type: 'managementRole' },
        { name: "权限配置", type: 'permissionCon' },
        { name: "编辑", type: 'edit' },
        { name: "删除", type: 'delete' }
      ],
      width: 253,
    }
  ]);

  const tiggerSearch = () => {
    tableRef.value.loadTableData();
  };
  const tiggerExport = () => {
    tableRef.value.exportTable();
  };
  const onSearch = async ({ currentPage, pageSize }) => {
    let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
    return await SystemsetController.findUserGroupListByPage({
      page: currentPage || 1,
      size: pageSize || 10,
      userId,
      tenantId,
      ...formData
    }, Date.now());
  };
  const onReset = () => {
    tableRef.value.resetTable();
  };
  // 初始化查询
  onMounted(() => {
    tiggerSearch();
  });
  onActivated(() => {
    tiggerSearch();
  });
  return {
    tableRef,
    columns,
    tiggerSearch,
    tiggerExport,
    onSearch,
    onReset,
  };
}
