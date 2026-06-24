<template>
	<view class="whiteCard" :style="{ backgroundColor: backgroundColor }" @click="clickCardlist">
		<view class="card_left">
			<view class="left_top_text">{{ leftTopText }}</view>
			<view class="left_bottom_text">{{ leftBottomText }}</view>
		</view>
		<view class="card_right"><slot name="slotRight"></slot></view>
	</view>
</template>

<script>
export default {
	name: 'Cardlist',
	props: {
		leftTopText: {
			type: String,
			default: ''
		},
		leftBottomText: {
			type: String,
			default: ''
		},
		backgroundColor: {
			type: String,
			default: 'rgba(255, 255, 255, 1)'
		}
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
.whiteCard {
	height: 128rpx;
	display: flex;
	align-items: center;
	border-radius: 12rpx;
	justify-content: space-between;
	padding: 26rpx 40rpx 24rpx 26rpx;
	box-sizing: border-box;

	.card_left {
		display: flex;
		flex-direction: column;
		align-content: space-between;
		.left_top_text {
			font-size: 28rpx;
			font-weight: 500;
			color: #454545;
		}
		.left_bottom_text {
			font-size: 24rpx;
			color: #979797;
		}
	}

	.card_right {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: flex-end;
	}
}
</style>
