import { ref, onMounted, onActivated, onDeactivated, computed } from "vue";
import { useRoute } from "vue-router";
import { useStore } from "vuex";
import SystemsetController from "@/api/system/index";
import { ElMessage } from "element-plus";
import expireStateView from "./expireStateView.vue";
import alarmStatusView from "./alarmStatusView.vue";
export default function useTable(otherParams) {
  const route = useRoute();
  const store = useStore();
  const tableRef = ref(null);
  const columns = ref([
    {
      title: "用户账号",
      key: "userAccount"
    },
    {
      title: "姓名",
      key: "fullName"
    },
    {
      title: "手机号",
      key: "phone"
    },
    {
      title: "所属组织",
      key: "organName"
    },
    {
      title: "角色",
      key: "groupName",
      width: 232,
    },
    {
      title: "用户状态",
      key: "userState",
      width: 150,
      formatter: (state) => state == 1 ? '正常' : '关闭',
      render: alarmStatusView
    },
    {
      title: "到期日",
      key: "expireDate",  
      width: 150,
      render: expireStateView,
      otherProp: 'expireState'
    },
    {
      title: "操作",
      key: "operate",
      list: [
        { name: "编辑", type: 'edit',disabledProp: 'isDefaultAdmin',disabledLabel: 1},
        { name: "删除", type: 'delete' ,disabledProp: 'isDefaultAdmin',disabledLabel: 1}
      ],
      width: 300,
    }
  ]);
  const clearData = () => {
    tableRef.value.clearData();
  };
  const tiggerSearch = () => {
    tableRef.value.loadTableData();
  };
  const tiggerExport = () => {
    tableRef.value.exportTable();
  };
  const onSearch = async ({ currentPage, pageSize}) => {
    let { tenantId, userId } = JSON.parse(localStorage.getItem("USER_INFO"));
    return await SystemsetController.findUserListByPage({
      page: currentPage || 1,
      size: pageSize || 10,
      userId,
      tenantId,
      ...otherParams
    }, Date.now());
  };
  const onReset = () => {
    tableRef.value.loadTableData();
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
    clearData
  };
}
