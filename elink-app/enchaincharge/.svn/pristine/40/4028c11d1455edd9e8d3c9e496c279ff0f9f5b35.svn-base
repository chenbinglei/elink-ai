<template>
	<view class="PriceTable">
		<view class="table_header">
			<view class="text">时段</view>
			<template v-if="strategyType === 1">
				<view class="text">充电费用(元/度)</view>
				<view class="text">电费(元/度)</view>
				<view class="text">服务费(元/度)</view>
			</template>
			<template v-else>
				<view class="text">放电费用(元/度)</view>
			</template>
		</view>
		<scroll-view :scroll-top="scrollTop" :scroll-y="true" class="table_body">
			<template v-for="(item, index) in tableData">
				<view class="table_body_li" :class="{ activeClass: item.isCurrent }" :key="index">
					<view class="text">
						<text>{{ item.startTime | moreData }}</text>
						<text>~</text>
						<text>{{ item.endTime | moreData }}</text>
					</view>
					<template v-if="strategyType === 1">
						<view class="text">{{ item.totalCost | moneyTwoNum }}</view>
						<view class="text">{{ item.electMoney | moneyTwoNum }}</view>
						<view class="text">{{ item.serviceMoney | moneyTwoNum }}</view>
					</template>
					<template v-else>
						<view class="text">{{ item.totalCost | moneyTwoNum }}</view>
					</template>
				</view>
			</template>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		name: "PriceTable",
		props: {
			tableData: {
				type: Array,
				default: () => []
			},
			isChart: {
				type: Boolean,
				default: false
			},
			strategyType: {
				type:[Number,String],
				default: ""
			}
		},
		data() {
			return {
				scrollTop: 0,
			}
		},
		mounted() {
			this.getScrollTopFun();
		},
		methods: {
			getScrollTopFun(){
				const query = uni.createSelectorQuery().in(this);
				query.select('.activeClass').boundingClientRect(data => {
					this.scrollTop = data.top - 380;
				}).exec();
			}
		}
	}
</script>

<style scoped lang="scss">
.PriceTable  {
	width: 100%;
	height: 420rpx;
	margin-top: 24rpx;
	padding: 0 24rpx 24rpx;
	box-sizing: border-box;
	display: flex;
	flex-direction: column;
	background: #ffffff;
	border-radius: 10rpx;

	.table_header {
		display: flex;
		align-items: center;
		background: #dfe2e6;
		border-radius: 8rpx;
		border: 4rpx solid #ffffff;
		padding: 10rpx 0 14rpx 0;
		box-sizing: border-box;

		.text {
			flex: 1;
			color: #5a5d66;
			font-size: 20rpx;
			text-align: center;
		}
	}

	.table_body {
		flex: 1;
		height: 2rpx;
		// overflow-y: auto;
		margin-top: 14rpx;
		
		.table_body_li{
			display: flex;
			align-items: center;
			margin-bottom: 10rpx;
			border-radius: 8rpx;
			background: #f1f3f4;
			padding: 10rpx 0 14rpx 0;
			box-sizing: border-box;
			
			.text {
				flex: 1;
				color: #454545;
				font-size: 20rpx;
				text-align: center;
			}
			
			&:last-child{
				margin-bottom: 0;
			}
		}
		
		.activeClass{
			background: #FBF0EB;
			border: 2rpx solid #F8D5BC;
			.text {
				color: #D66312;
			}
		}
	}
}
</style>
