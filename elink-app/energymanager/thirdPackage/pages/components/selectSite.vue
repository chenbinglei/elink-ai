<template>
  <view class="containers">
    <u--input placeholder="请输入场站名称" suffixIcon="search" border="surround" v-model="siteName" @change="handleSearch" class="rounded-input"></u--input>

    <view class="total">
      共有{{ totalSites }}个场站
    </view>

    <view class="site-list">
      <view class="site-list-item" v-for="(item, index) in siteList" :key="item.id || index">
        <!-- 公司选择区域 -->
        <view class="company-header" @click="toggleCompany(item)">

          <view class="icon iconfont">&#xe61d;</view>
          <view class="site-list-item-name">{{ item.name }}</view>
          <view class="checkbox" :class="{
            checked: isCompanyAllSelected(item),
            partial: isCompanyPartiallySelected(item)
          }">
            <view v-if="isCompanyAllSelected(item)" class="checkbox-inner"></view>
            <view v-else-if="isCompanyPartiallySelected(item)" class="checkbox-partial"></view>
          </view>
        </view>

        <view class="site-list-item-detail">
          <u-checkbox-group v-model="selectedSiteIds" iconPlacement="right" placement="column" @change="handleCheckboxChange">
            <view v-for="(child, index) in item.children" :key="child.id || child.name" :class="{ 'no-border-bottom': index !== item.children.length - 1 }"
              class="site-item-list">
              <u-checkbox :name="child.id" :labelDisabled="true">
                <!-- 自定义 label 内容 -->
                <template #label>
                  <view class="site-item" @click="toggleSite(child)">
                    <view class="icon iconfont">&#xe60c;</view>
                    <view class="site-info">
                      <view class="siteName">{{ child.name }}</view>

                    </view>
                  </view>
                </template>
              </u-checkbox>
              <view class="siteAddress">{{ child.location?.address }}</view>
            </view>
          </u-checkbox-group>
        </view>

      </view>
    </view>
    <view class="site-button">
      <u-button @click="goSelectSite" class="btn-sure">确定</u-button>
      <u-button @click="btncancel" class="btn-cancel">取消</u-button>
    </view>
  </view>
</template>

<script setup>
import { onLoad } from '@dcloudio/uni-app'
import { getTenantSiteList } from '@/api/homePage.js'
import { ref, computed, watch, nextTick } from 'vue'

const siteName = ref('')
const siteList = ref([])
const allSites = ref([])
const source = ref('')

const selectedSiteIds = ref([]) // 存储选中的站点ID

// 计算总站点数
const totalSites = computed(() => {
  if (!Array.isArray(siteList.value)) {
    return 0
  }
  return siteList.value.reduce((total, company) => total + (company.children || []).length, 0)
})


// 检查公司是否全选
const isCompanyAllSelected = (company) => {
  if (!company || !company.children || company.children.length === 0) return false
  return company.children.every(child => selectedSiteIds.value.includes(child.id))
}

// 检查公司是否部分选中
const isCompanyPartiallySelected = (company) => {
  if (!company || !company.children || company.children.length === 0) return false
  const hasSelected = company.children.some(child => selectedSiteIds.value.includes(child.id))
  const allSelected = isCompanyAllSelected(company)
  return hasSelected && !allSelected
}
// 切换单个站点选择状态
const toggleSite = (site) => {
  const index = selectedSiteIds.value.indexOf(site.id)
  if (index > -1) {
    // 如果已选中，则移除
    selectedSiteIds.value.splice(index, 1)
  } else {
    // 如果未选中，则添加
    selectedSiteIds.value.push(site.id)
  }

  // 为了响应式更新
  selectedSiteIds.value = [...selectedSiteIds.value]
}

// 切换公司所有站点选择状态
const toggleCompany = (company) => {
  if (!company || !company.children || company.children.length === 0) return

  const allSelected = isCompanyAllSelected(company)
  const companySiteIds = company.children.map(child => child.id)

  if (allSelected) {
    // 如果全选，则取消全选
    selectedSiteIds.value = selectedSiteIds.value.filter(id => !companySiteIds.includes(id))
  } else {
    // 如果不是全选，则添加所有未选中的站点
    const newSelected = [...selectedSiteIds.value]
    companySiteIds.forEach(id => {
      if (!newSelected.includes(id)) {
        newSelected.push(id)
      }
    })
    selectedSiteIds.value = newSelected
  }
  nextTick(() => {
    handleCheckboxChange(selectedSiteIds.value)
  })
}
const getScenarioTypes = () => {
  const selectedSites = siteList.value.flatMap(company =>
    company.children.filter(child => selectedSiteIds.value.includes(child.id))
  );
  const allScenarioTypes = selectedSites
    .filter(item => item.scenarioTypes && typeof item.scenarioTypes === 'string')
    .flatMap(item => {
      // 如果是逗号分隔的字符串，拆分成数字数组
      return item.scenarioTypes.split(',').map(Number);
    });
  const uniqueScenarioTypes = Array.from(new Set(allScenarioTypes));
  return uniqueScenarioTypes
}
// 获取选中站点中最早的 createTime
const getEarliestCreateTime = () => {
  // 1. 根据 selectedSiteIds 查找对应的站点对象
  const selectedSites = siteList.value.flatMap(company =>
    company.children.filter(child => selectedSiteIds.value.includes(child.id))
  );
  // 2. 提取 createTime 并过滤掉无效值
  const createTimes = selectedSites
    .map(site => site.createTime)
    .filter(time => time !== undefined && time !== null);

  // 3. 如果没有选中任何站点，返回 null 或空字符串
  if (createTimes.length === 0) return null;

  // 4. 找出最早的时间（最小值）
  return createTimes.reduce((earliest, current) => {
    return new Date(current) < new Date(earliest) ? current : earliest;
  });
};
// 处理复选框组变化
const handleCheckboxChange = (value) => {
  // 可以在这里处理额外的逻辑
}


// 搜索处理
const handleSearch = (val) => {

  if (val == '') {
    // 如果没有输入，显示完整数据
    siteList.value = buildTree(allSites.value)
    return
  } else {
    const arr = buildTree(allSites.value)
    const root = arr[0]
    let obj = arr[0].children.filter(item => {
      return item.name.toLowerCase().includes(val.toLowerCase())
    })
    let newSite = {
      ...root,
      children: obj
    }
    siteList.value[0] = newSite
  }

}

// 构建树形结构
function buildTree (data) {
  const map = {}
  const root = []

  data.forEach(item => {
    // 解析 location 字符串
    let parsedLocation = null
    if (item.location && typeof item.location === 'string') {
      try {
        parsedLocation = JSON.parse(item.location)
      } catch (e) {
        console.error('解析 location 失败:', e)
        parsedLocation = { address: item.location }
      }
    }

    // 创建节点并添加 children
    map[item.id] = {
      ...item,
      location: parsedLocation,
      children: []
    }
  })

  // 构建树
  data.forEach(item => {
    if (item.parentId === '0') {
      root.push(map[item.id])
    } else {
      if (map[item.parentId]) {
        map[item.parentId].children.push(map[item.id])
      }
    }
  })

  return root
}

onLoad((options) => {
  source.value = options.source
  let parsedData = null;

  if (options && typeof options.selectedSite === 'string') {
    try {
      const encoded = options.selectedSite;
      const decoded = decodeURIComponent(encoded);
      parsedData = JSON.parse(decoded);
    } catch (e) {
      console.error('JSON 解析失败:', e);
    }
  }
  if (parsedData && parsedData.length > 0) {
    selectedSiteIds.value = parsedData
  }

  getSiteList();
});
const getSiteList = () => {
  const userId = uni.getStorageSync('USER_ID')
  let obj = {
    userId: userId
  }
  getTenantSiteList(obj).then(res => {
    allSites.value = res.data
    siteList.value = buildTree(res.data)
    const allScenarioTypes = res.data
      .filter(item => item.scenarioTypes && typeof item.scenarioTypes === 'string')
      .flatMap(item => {
        // 如果是逗号分隔的字符串，拆分成数字数组
        return item.scenarioTypes.split(',').map(Number);
      });
    // 去重
    const uniqueScenarioTypes = Array.from(new Set(allScenarioTypes));

  })
}
const goSelectSite = () => {
  const pages = getCurrentPages(); // 获取页面栈
  const prevPage = pages[pages.length - 2]; // 上一页
  if (selectedSiteIds.value.length > 0) {
    if (prevPage) {
      const earliestTime = getEarliestCreateTime();
      const scenarioTypes = getScenarioTypes()
      uni.setStorageSync('EARLIEST_CREATE_TIME', earliestTime)
      //  uni.setStorageSync('SCENARIO_TYPES', scenarioTypes)
      // 向上一页传递数据
      uni.$emit(`selectSiteEvent_${source.value}`, selectedSiteIds.value,scenarioTypes);
      // 返回上一页
      uni.navigateBack();
    } else {
      console.error('没有上一页');
    }
  } else {
    uni.showToast({
      title: '请选择站点',
      icon: 'none'
    })
  }

}
const btncancel = () => {
  selectedSiteIds.value = []
}

// 监听选择变化，保存到本地存储
watch(selectedSiteIds, (newVal) => {
  try {
    // uni.setStorageSync('electedSiteIdss', newVal)
  } catch (e) {
    console.error('保存已选站点失败:', e)
  }
}, { deep: true })
</script>

<style lang="scss" scoped>
.containers {
  padding: 40rpx;
  padding-bottom: 140rpx;
  height: 100%;
  background: #f3f3f3;
}

.site-list-item-name {
  flex: 1;
  font-weight: 500;
  font-size: 28rpx;
  color: #000000;
  margin-left: 20rpx;
}

.company-header {
  display: flex;
  align-items: center;
  width: 100%;
  background: #f3f3f3;
  border-radius: 20rpx 20rpx 20rpx 20rpx;
  padding: 24rpx 22rpx;
  margin-bottom: 10rpx;

  &:active {
    opacity: 0.8;
  }

  .iconfont {
    color: #fff;
    font-size: 20rpx;
    background: linear-gradient(0deg, #58cbd2 0%, #77e7f1 100%);
    padding: 10rpx;
    border-radius: 50%;
  }
}

.company-count {
  margin-left: 10rpx;
  font-size: 24rpx;
  color: #666;
  white-space: nowrap;
}

.site-list-item {
  padding: 40rpx 20rpx 20rpx 20rpx;
  background: #fff;
}

.total {
  font-weight: 500;
  font-size: 20rpx;
  color: #a2a2a2;
  margin-top: 26rpx;
  margin-bottom: 20rpx;
}

.site-list {
  border-radius: 28rpx;
  height: 85%;
  overflow: auto;
}

.rounded-input {
  width: 100%;
  height: 80rpx;
  border-radius: 38rpx !important;
  background: #ffffff;
  padding: 0 20rpx;
}

.site-list-item-detail {
  margin: 20rpx 0;

  .site-item-list {
    padding: 0 20rpx;
    padding-bottom: 23rpx;
  }

  .no-border-bottom {
    border-bottom: 2rpx solid #dfdfdf;
  }

  .site-item {
    display: flex;
    align-items: flex-start;
    align-items: center;

    .iconfont {
      color: #fff;
      font-size: 20rpx;
      background: linear-gradient(0deg, #5482e4 0%, #82a1f3 100%);
      padding: 10rpx;
      border-radius: 50%;
    }
  }

  &:active {
    background-color: #f9f9f9;
  }

  .site-info {
    flex: 1;
    margin-left: 20rpx;
  }

  .siteName {
    font-weight: 500;
    font-size: 26rpx;
    color: #000000;
    padding: 19rpx 0;
  }

  .siteAddress {
    font-weight: 400;
    font-size: 20rpx;
    margin-left: 58rpx;
    color: #a2a2a2;
  }
}

// 复选框样式
.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid #ddd;
  border-radius: 6rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;

  &.checked {
    border-color: #2979ff;
    background-color: #2979ff;
  }

  &.partial {
    border-color: #2979ff;
    background-color: #2979ff;
  }

  .checkbox-inner {
    width: 18rpx;
    height: 18rpx;
    background-color: white;
    border-radius: 2rpx;
  }

  .checkbox-partial {
    width: 18rpx;
    height: 4rpx;
    background-color: white;
    border-radius: 1rpx;
  }
}

// 操作按钮
.action-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 20rpx 40rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 2rpx solid #f0f0f0;
  z-index: 100;

  .btn {
    padding: 16rpx 20rpx;
    border-radius: 8rpx;
    font-size: 24rpx;
    text-align: center;
    font-weight: 500;
    white-space: nowrap;

    &:active {
      opacity: 0.8;
    }

    &.select-all,
    &.select-none,
    &.invert-select {
      background: #f5f5f5;
      color: #333;
      margin-right: 10rpx;
      flex: 1;
    }

    &.confirm {
      background: #2979ff;
      color: white;
      flex: 2;
      margin-left: 10rpx;
    }
  }
}

.site-button {
  display: flex;
  justify-content: center;
  gap: 14rpx;
  font-size: 30rpx;
  font-weight: 800;

  .btn-sure {
    background: #1877ff;
    border-radius: 49rpx;
    color: #ffffff;
  }

  .btn-cancel {
    background: #e7e8ea;
    border-radius: 49rpx;
    color: #1877ff;
  }
}
</style>