<template>
	<view class="container">
		<order-search-from ref="orderSearchFromRef" pageType="orderlist" @headerFormEvent="pagingReload"></order-search-from>
		<view class="container_content">
			<z-paging ref="pagingRef" :fixed="false" :auto="false" v-model="dataList" @query="findMyOrderListByPage">
				<!-- 设置自己的emptyView组件，非必须。空数据时会自动展示空数据组件，不需要自己处理 -->
				<sm-null-data slot="empty" title="当前暂无订单"></sm-null-data>
				<view class="order_list">
					<order-list-card v-for="(item, index) in dataList" :key="index" :orderInfo="item" :orderType="defaultFromData.orderType"></order-list-card>
				</view>
			</z-paging>
		</view>
	</view>
</template>

<script>
	import { queryMyOrderListByPage } from "@/secondPackage/api/index.js";
	import OrderListCard from "@/secondPackage/components/orderlist/OrderListCard.vue";
	import OrderSearchFrom from "@/secondPackage/components/occupyOrderList/OrderSearchFrom.vue";
	
	export default {
		name: "orderlist",
		components:{ OrderSearchFrom,OrderListCard },
		data() {
			return {
				dataList: [],
				selectFromData:{ },
				defaultFromData:{
					orderType: 1, //订单类型 1-充电订单 2-放电订单
					orderLogoType: 3, //订单标识类型 1-预约 2-订单 3-全部类型
				},
			}
		},
		onLoad(options) {
			if(options.orderType)this.defaultFromData.orderType = options.orderType;
			this.pagingReload();
		},
		methods: {
			pagingReload() {
				let selectFromData = JSON.parse(JSON.stringify(this.$refs.orderSearchFromRef.selectFromData));
				if(selectFromData.endDate)selectFromData.endTime = selectFromData.endDate + " 23:59:59";
				if(selectFromData.startDate)selectFromData.startTime = selectFromData.startDate + " 00:00:00";
				this.selectFromData = JSON.parse(JSON.stringify(selectFromData));
				delete this.selectFromData.startDate;
				delete this.selectFromData.endDate;
				this.$refs.pagingRef.reload();
			},
			findMyOrderListByPage(pageNo, pageSize){
				let defaultFromData = JSON.parse(JSON.stringify(this.defaultFromData));
				defaultFromData.orderType = defaultFromData.orderType - 1; //订单类型 0-充电订单 1-放电订单
				queryMyOrderListByPage({page: pageNo, size: pageSize,...this.selectFromData,...defaultFromData }).then(res => {
					let orderList = res.data.items;
					if(orderList && orderList.length){
						for(let i = 0;i < orderList.length;i++){
							orderList[i].chargeMode = this.defaultFromData.orderType;
						}
					}
					//请勿在网络请求回调中给dataList赋值！！只需要调用complete就可以了
					this.$refs.pagingRef.complete(orderList);
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