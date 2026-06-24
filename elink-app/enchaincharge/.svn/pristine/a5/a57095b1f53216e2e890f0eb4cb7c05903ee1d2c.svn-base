<template>
	<view class="occupyPrice">
		<!-- 占用计费详情 -->
		<view class="list">
			
			<view class="list_li">
				<view class="li_left">限免时长：</view>
				<view class="li_right ">
					<template v-if="formInline.isTimeFree == 1">
						<view class="text">{{ formInline.freeTime | moreData }}</view>
						<view class="unit">小时</view>
					</template>
					<view class="text" v-else>无限免时长</view>
				</view>
			</view>

			<view class="list_li">
				<view class="li_left">跳费时长：</view>
				<view class="li_right">
					<view class="text">{{ formInline.skipFreeTime | moreData }}</view>
					<view class="unit">分钟</view>
				</view>
			</view>
			<view class="list_li">
				<view class="li_left">封顶金额：</view>
				<view class="li_right">
					<view class="text">{{ formInline.maxMoney | moneyTwoNum }}</view>
					<view class="unit">元</view>
				</view>
			</view>
			
			<view class="list_li" v-if="formInline.isTimeFree == 1">
				<view class="li_left">循环计费：</view>
				<view class="li_right">
					<view class="text">{{ formInline.isCyclicBilling | enableOrNot }}</view>
					<template v-if="formInline.isCyclicBilling == 1">
						<view class="text">，</view>
						<view class="text">{{ formInline.billingCdm | billingCdmType }}</view>
					</template>
				</view>
			</view>
			<view class="list_li">
				<view class="li_left">单价：</view>
				<view class="li_right">
					<view class="text">{{ formInline.price | moneyTwoNum }}</view>
					<view class="unit">元/h</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'OccupyPrice',
	props: {
		priceInfo: {
			type: Object,
			default: () => {
				return {};
			}
		}
	},
	watch: {
		priceInfo: {
			deep: true,
			handler(newVal) {
				this.formInline = JSON.parse(JSON.stringify(this.priceInfo));
			}
		}
	},
	data() {
		return {
			formInline: {}
		};
	}
};
</script>

<style scoped lang="scss">
.occupyPrice {
	width: 100%;

	.list {
		background: #ffffff;
		border-radius: 10rpx;
		box-sizing: border-box;

		.list_li {
			display: flex;
			align-items: center;
			margin-bottom: 28rpx;

			.li_left {
				font-size: 24rpx;
				color: rgba(0,0,0,0.4);
			}
			
			.li_right {
				flex: 1;
				font-size: 24rpx;
				color: rgba(0,0,0,0.9);
				display: flex;
				align-items: center;
				justify-content: flex-end;

				.unit {
					margin-left: 4rpx;
				}
			}
		}
		.list_li:last-child {
			margin-bottom: 0;
		}
	}
}
</style>
