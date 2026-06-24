<template>
	<view class="sm-title">
		<view class="header_title uni-flex align-center">
			<view class="title_left">
				<view class="xian"></view>
				<view class="text">{{ title }}</view>
				<view class="subtitle" v-if="subtitle">{{ subtitle }}</view>
			</view>
			<view class="title_right">
				<slot name="rightSlot"></slot>
			</view>
		</view>
		<view class="title_content">
			<slot name="content"></slot>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'sm-title',
		props: {
			title: {
				type: String,
				default: '标题'
			},
			subtitle: {
				type: String,
				default: ''
			}
		}
	};
</script>

<style lang="scss" scoped>
	.header_title {
		color: #2a2a2a;
		font-size: 28rpx;
		font-weight: 500;
		margin-bottom: 12rpx;

		.title_left {
			display: flex;
			align-items: center;

			.xian {
				width: 4rpx;
				height: 32rpx;
				background: #107be9;
				border-radius: 2rpx;
				margin-right: 16rpx;
			}

			.subtitle {
				color: #2a2a2a;
				font-size: 24rpx;
				margin-left: 4rpx;
			}
		}
	}
</style>