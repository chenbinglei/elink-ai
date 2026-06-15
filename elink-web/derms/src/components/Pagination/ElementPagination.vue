<template>
  <div class="pagination">
    <div class="pagination_left">
      <el-dropdown v-if="typeArray && typeArray.length">
        <el-button
          v-for="(item, index) in paginationButArray"
          :key="index"
          @click="batchHandle(item.buttonType)"
        >
          <el-icon v-if="item.buttonIcon">
            <component :is="item.buttonIcon"></component>
          </el-icon>
          <span>{{ item.buttonName }}</span>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="(item, index) in typeArray"
              :key="index"
              @click="typeSelect(item.id)"
              >{{ item.name }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <template v-else>
        <el-button
          v-for="(item, index) in paginationButArray"
          :key="index"
          @click="batchHandle(item.buttonType)"
        >
          <el-icon v-if="item.buttonIcon">
            <component :is="item.buttonIcon"></component>
          </el-icon>
          <span>{{ item.buttonName }}</span>
        </el-button>
      </template>
    </div>
    <div class="pagination_right">
      <el-pagination
        :currentPage="current_page"
        :page-size="page_size"
        :background="background"
        :disabled="disabled"
        :layout="layout"
        :page-sizes="page_sizes"
        :total="totalNumber"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>
<script lang="ts">
import {
  getCurrentInstance,
  defineComponent,
  toRefs,
  reactive,
  watch,
} from "vue";
import {
  CirclePlus,
  Delete,
  Folder,
  EditPen,
  Download,
  Upload,
  Refresh,
  SwitchButton,
} from "@element-plus/icons-vue";

export default defineComponent({
  name: "ElementPagination",
  components: {
    CirclePlus,
    Delete,
    Folder,
    EditPen,
    Download,
    Upload,
    Refresh,
    SwitchButton,
  },
  props: {
    totalNumber: {
      type: [Number, String],
      default: 0,
    },
    pageSize: {
      type: [Number, String],
      default: 20,
    },
    currentPage: {
      type: [Number, String],
      default: 1,
    },
    background: {
      type: Boolean,
      default: true,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    layout: {
      type: String,
      default: "total, prev, pager, next, sizes, jumper",
    },
    sizeArray: {
      type: Array,
      default: () => [10, 20, 50, 100, 500],
    },
    small: {
      type: Boolean,
      default: true,
    },
    //按钮列表
    paginationButArray: {
      type: Array,
      default: () => [],
    },
    //类型
    typeArray: {
      type: Array,
      default: () => [],
    },
  },
  emits: ["update:pageSize", "update:currentPage"],
  setup(props) {
    const { emit } = getCurrentInstance();
    const that = reactive({
      page_size: props.pageSize,
      totalNumber: props.totalNumber,
      current_page: props.currentPage,
      page_sizes: props.sizeArray,
    });

    //切换分页
    const handleCurrentChange = (val) => {
      that.current_page = val;
      emit("update:currentPage", that.current_page);
      emit("pageChange");
    };

    const handleSizeChange = (val) => {
      // that.page_size = val;
      emit("update:currentPage", 1);
      emit("update:pageSize", val);
      emit("pageChange");
    };

    const watchPagination = watch(
      [() => props.pageSize, () => props.currentPage, () => props.totalNumber],
      ([newPageSize, newCurrentPage, newTotalNumber]) => {
        that.page_size = newPageSize;
        that.totalNumber = newTotalNumber;
        that.current_page = newCurrentPage;
      }
    );

    // 批量操作 按钮
    const batchHandle = (batchHandleType) => {
      emit("paginationFunction", { type: batchHandleType });
    };

    // 类型选择
    const typeSelect = (type) => {
      emit("paginationFunction", { type: "typeSelect", typeId: type });
    };

    return {
      ...toRefs(that),
      handleCurrentChange,
      batchHandle,
      watchPagination,
      typeSelect,
      handleSizeChange,
    };
  },
});
</script>
<style lang="scss">
.pagination {
  width: 100%;
  display: flex;
  align-items: center;
  // padding-top: 20px;

  justify-content: space-between;

  .pagination_left {
    .el-button {
      color: #1f74e2;
      border: 1px solid #1f74e2;
    }
  }

  .el-pagination {
    --el-text-color-primary: #ffffff;

    .btn-prev,
    .btn-next {
      // border-radius: 4px;
       background: #001c33;
    }

    .el-pager {
      .number,
      .more {
        // border-radius: 4px;
        background: #001c33;
      }
    }
  }
}
.pagination_right{
  margin-right: 10px;
}
</style>
