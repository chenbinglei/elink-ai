<template>
	<view class="cardlist" @click="clickCardlist">
		<!-- 左侧文字，右侧箭头 -->
		<view class="cardlist_top">
			<view class="cardlist_left">
				<slot v-if="slotleft" name="slotleft"></slot>
				<text v-else class="leftText">{{ leftText }}</text>
			</view>
			<view class="cardlist_right">
				<view class="slot">
					<slot v-if="enableSlot" name="slotRight"></slot>
					<view class="text" v-else>
						<uni-link v-if="unilink" :href="rightText" :text="rightText" :showUnderLine="false" :color='unilinkColor'></uni-link>
						<text v-else>{{ rightText }}</text>
					</view>
				</view>
				<view v-if="arrowShow" class="u-arrow u-arrow-right"></view>
			</view>
		</view>
		<view class="cardlist_bottom" v-if="bottomLine"></view>
	</view>
</template>

<script>
export default {
	name: 'Cardlist',
	props: {
		//左侧插槽
		slotleft: {
			type: Boolean,
			default: false
		},
		// 右侧插槽
		leftText: {
			type: String,
			default: ''
		},
		//右侧是否启用插槽
		enableSlot: {
			type: Boolean,
			default: false
		},
		// 右侧文字
		rightText: {
			type: String,
			default: ''
		},
		//是否显示右侧箭头
		arrowShow: {
			type: Boolean,
			default: true
		},
		// 下侧是否有下划线
		bottomLine: {
			type: Boolean,
			default: true
		},
		// 右侧文字是否为超链接
		unilink: {
			type: Boolean,
			default: false
		},
		unilinkColor: {
			type: String,
			default: '#1791FF'
		},
	},
	data() {
		return {};
	},
	methods: {
		clickCardlist() {
			this.$emit('cardEvent');
		}
	}
};
</script>
<style scoped lang="scss">
.cardlist {
	height: 108rpx;
	padding: 0 20rpx 0 40rpx;
	box-sizing: border-box;
	border-radius: 12rpx;
	margin-bottom: 20rpx;
	display: flex;
	flex-direction: column;

	.cardlist_top {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: space-between;

		.cardlist_left {
			display: flex;
			align-items: center;
			margin-right: 10rpx;

			.leftText {
				font-size: 30rpx;
				font-weight: 400;
				color: #242424;
			}
		}
		.cardlist_right {
			flex: 1;
			height: 100%;
			display: flex;
			align-items: center;

			.slot {
				flex: 1;
				height: 100%;
				display: flex;
				align-items: center;
				justify-content: flex-end;

				.text {
					font-size: 28rpx;
					font-weight: 500;
					color: #c7c7c7;
				}
			}
			.u-arrow {
				border-color: #c7c7c7;
				margin-left: 20rpx;
			}
		}
	}

	.cardlist_bottom {
		width: 100%;
		border-bottom: 2rpx solid #E9E9E9;
	}
}
</style>
