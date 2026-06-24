<template>
	<view class="container">
		<order-search-from ref="orderSearchFromRef" @headerFormEvent="pagingReload"></order-search-from>
		<view class="container_content">
			<z-paging ref="pagingRef" :fixed="false" :auto="false" v-model="dataList" @query="findAppOrderHolderList">
				<!-- 设置自己的emptyView组件，非必须。空数据时会自动展示空数据组件，不需要自己处理 -->
				<sm-null-data slot="empty" title="当前暂无订单"></sm-null-data>
				<view class="order_list">
					<template v-for="(item, index) in dataList">
						<order-list-card :key="index" :orderInfo="item" @changeEvent="pagingReload"></order-list-card>
					</template>
				</view>
			</z-paging>
		</view>
	</view>
</template>

<script>
	import { queryAppOrderHolderList } from "@/secondPackage/api/index.js";
	import OrderListCard from "@/secondPackage/components/occupyOrderList/OrderListCard.vue";
	import OrderSearchFrom from "@/secondPackage/components/occupyOrderList/OrderSearchFrom.vue";
	
	export default {
		name: "occupyOrderList",
		components:{ OrderSearchFrom,OrderListCard },
		data() {
			return {
				dataList: [],
				selectFromData:{ },
			}
		},
		onLoad() {
			this.pagingReload();
		},
		methods: {
			pagingReload() {
				this.selectFromData = this.$refs.orderSearchFromRef.selectFromData;
				this.$refs.pagingRef.reload();
			},
			findAppOrderHolderList(pageNo, pageSize){
				queryAppOrderHolderList({page: pageNo, size: pageSize,...this.selectFromData }).then(res => {
					//请勿在网络请求回调中给dataList赋值！！只需要调用complete就可以了
					let dataList = res.data.items ? res.data.items : [];
					this.$refs.pagingRef.complete(dataList);
				}).catch(res => {
					//如果请求失败写this.$refs.paging.complete(false)，会自动展示错误页面
					//注意，每次都需要在catch中写这句话很麻烦，z-paging提供了方案可以全局统一处理
					//在底层的网络请求抛出异常时，写uni.$emit('z-paging-error-emit');即可
					this.$refs.pagingRef.complete(false);
				});
			}
		}
	}
</script>

<style scoped lang="scss">
	.container_content{
		display: initial;
		
		.order_list{
			width: 100%;
			padding: 24rpx 32rpx;
			box-sizing: border-box;
		}
	}
</style>