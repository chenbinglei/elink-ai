<template>
	<view class="container">

		<discharge-wallet ref="dischargeWalletRef"></discharge-wallet>
		<view class="content_operate">
			<view class="content_body_top operate">
				<view class="line"></view>
				<view class="text">常用功能</view>
			</view>
			<operate-list ref="operateListRef"></operate-list>
		</view>


		<!-- 实时监控 -->
		<view class="content_body">
			<view class="content_body_top">
				<view class="line"></view>
				<view class="text">实时监控</view>
			</view>
			<view class="content_list">
				<ongoing-orders ref="ongoingOrdersRef" :startMode="2"></ongoing-orders>
			</view>
		</view>

	</view>
</template>

<script>
import OngoingOrders from "@/pages/charging/components/OngoingOrders.vue";
import OperateList from "@/fourthPackage/components/v2gDischarge/OperateList.vue";
import DischargeWallet from "@/fourthPackage/components/v2gDischarge/DischargeWallet.vue";

export default {
	name: "v2gDischarge",
	components: { OngoingOrders, DischargeWallet, OperateList },
	onShow () {
		this.$refs.ongoingOrdersRef.connectSocketInit();
		this.$refs.dischargeWalletRef.queryAppMemberBagAccountList();
	},
	onHide () {
		this.$refs.ongoingOrdersRef.closeWebSocket();
	},
	data () {
		return {

		}
	},
	methods: {

	},
	beforeDestroy () {
		console.log("页面销毁前！！！");
		this.$refs.ongoingOrdersRef.closeWebSocket();
	},
}
</script>

<style scoped lang="scss">
.operate {
	padding: 24rpx 32rpx 0 32rpx;
	margin-bottom: 0rpx !important;
}

.content_body {
	flex: 1;
	height: 2rpx;
	display: flex;
	flex-direction: column;
	box-sizing: border-box;
	padding: 24rpx 32rpx 0 32rpx;

	.content_list {
		flex: 1;
		height: 2rpx;
	}
}

.content_body_top {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;

	.line {
		width: 6rpx;
		height: 24rpx;
		margin-right: 10rpx;
		background-color: #476AE2;
	}

	.text {
		font-weight: 600;
		font-size: 32rpx;
		color: rgba(0, 0, 0, 0.8);
	}
}
</style>