<template>
	<view class="container">
		<view class="siteAndPile backgroundColor">
			<view class="siteName">{{ orderDetailInfo.siteName | moreData }}</view>
			<view class="pile_info">
				<view class="pileCode">桩编号：{{ orderDetailInfo.pileCode | moreData }}</view>
				<view class="gunCode">枪编号：{{ orderDetailInfo.gunCode | moreData }}</view>
			</view>
		</view>
		<view class="card_list backgroundColor">
			<view class="card_title"><view class="card_title_text">计费规则</view></view>
			<view class="line"></view>
			<OccupyPrice ref="OccupyPriceRef" :priceInfo="orderDetailInfo"></OccupyPrice>
		</view>
		<view class="card_list backgroundColor">
			<view class="card_title">
				<view class="card_title_left">
					<view class="card_title_text">消费/元</view>
					<view class="card_title_number">{{ orderDetailInfo.paidMoney | moneyTwoNum }} 元</view>
				</view>
				<view class="card_title_right">已结算</view>
			</view>
			<view class="line"></view>
			<view class="card_li">
				<view class="card_li_left">订单编号：</view>
				<view class="card_li_right">
					<text class="twoShowText">{{ orderDetailInfo.orderNum | moreData }}</text>
					<view class="copy_but" @click.stop="clickCopyBut">复制</view>
				</view>
			</view>
			<view class="card_li">
				<view class="card_li_left">开始时间：</view>
				<view class="card_li_right">{{ orderDetailInfo.startTime | moreData }}</view>
			</view>
			<view class="card_li">
				<view class="card_li_left">结束时间：</view>
				<view class="card_li_right">{{ orderDetailInfo.endTime | moreData }}</view>
			</view>
			<view class="card_li">
				<view class="card_li_left">订单时长：</view>
				<view class="card_li_right">{{ setDurationFun(orderDetailInfo.duration) | moreData }}</view>
			</view>
			<view class="card_li">
				<view class="card_li_left">支付时间：</view>
				<view class="card_li_right">{{ orderDetailInfo.payTime | moreData }}</view>
			</view>
			<view class="card_li">
				<view class="card_li_left">订单金额：</view>
				<view class="card_li_right">{{ orderDetailInfo.orderMoney | moneyTwoNum }} 元</view>
			</view>
		</view>
	</view>
</template>

<script>
import { findAppOrderHolderById } from '@/secondPackage/api/index.js';
import OccupyPrice from '@/secondPackage/components/occupyOrderList/OccupyPrice.vue';

export default {
	name: 'occupyDetails',
	components: { OccupyPrice },
	data() {
		return {
			id: '',
			orderDetailInfo: {},
			listloading: true //接口加载状态
		};
	},
	onLoad(options) {
		this.id = options.id;
		this.queryAppOrderHolderById();
	},
	methods: {
		queryAppOrderHolderById() {
			this.listloading = true;
			findAppOrderHolderById({ id: this.id })
				.then(res => {
					this.orderDetailInfo = res.data;
					this.listloading = false;
				})
				.catch(err => {
					this.listloading = false;
				});
		},
		clickCopyBut() {
			uni.setClipboardData({
				data: this.orderDetailInfo.orderNum,
				success: function() {
					console.log('success');
				}
			});
		},
		setDurationFun(duration) {
			let durationStr = '';
			if (duration) {
				let durationArr = duration.split(':');
				durationStr = `${durationArr[0] * 1}小时${durationArr[1] * 1}分${durationArr[2] * 1}秒`;
			}
			return durationStr;
		}
	}
};
</script>

<style scoped lang="scss">
.container {
	padding: 28rpx 32rpx 10rpx 32rpx;
	box-sizing: border-box;
	
	.siteAndPile {
		padding: 30rpx 32rpx 44rpx 32rpx;
		box-sizing: border-box;
		.siteName {
			font-size: 34rpx;
			font-weight: 600;
			color: #030303;
			margin-bottom: 16rpx;
		}
		.pile_info {
			display: flex;
			align-items: center;
			font-size: 24rpx;
			color: #9c9ea0;

			.gunCode {
				margin-left: 30rpx;
			}
		}
	}

	.card_list {
		padding: 0 32rpx 32rpx 32rpx;
		box-sizing: border-box;

		.card_title {
			height: 88rpx;
			display: flex;
			align-items: center;
			justify-content: space-between;
			color: #030303;
			font-size: 28rpx;

			.card_title_left {
				flex: 1;
				display: flex;
				font-weight: 600;
				align-items: center;

				.card_title_text {
					margin-right: 10rpx;
				}
				
				.card_title_alter {
					font-size: 24rpx;
					color: #2f2f2f;
				}
			}

			.card_title_right {
				color: #a1a1a1;
				font-size: 24rpx;

				.settlementState2 {
					color: #f3615b;
					font-weight: 600;
				}

				.settlementState3 {
					color: #1ec89e;
					font-weight: 600;
				}
			}
		}

		.line {
			height: 2rpx;
			background: #ebebeb;
			border-radius: 10rpx;
			margin-bottom: 28rpx;
		}

		.card_li {
			display: flex;
			align-items: center;
			margin-bottom: 24rpx;

			.card_li_left {
				font-size: 24rpx;
				color: rgba(0,0,0,0.4);
				white-space: nowrap;
				margin-right: 16rpx;
			}

			.card_li_right {
				display: flex;
				align-items: center;
				font-size: 24rpx;
				color: rgba(0,0,0,0.9);
				
				.twoShowText {
					flex: 1;
					-webkit-line-clamp: 1;
				}
				
				.copy_but {
					color: #476ae2;
					margin-left: 12rpx;
					padding: 4rpx 12rpx;
					border-radius: 6rpx;
					border: 2rpx solid #476ae2;
				}
			}
		}
	}

	.backgroundColor {
		background: #ffffff;
		border-radius: 10rpx;
		margin-bottom: 18rpx;
	}
}
</style>
