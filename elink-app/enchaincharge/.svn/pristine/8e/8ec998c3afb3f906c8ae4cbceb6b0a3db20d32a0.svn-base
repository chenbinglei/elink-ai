<template>
	<view class="loadingRoute">
		<view class="bigRadius"></view>
		<view class="minRadius minRadius1"></view>
		<view class="minRadius minRadius2"></view>
	</view>
</template>

<script></script>

<style scoped lang="scss">
	.loadingRoute {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		border: 9rpx solid rgba(255, 255, 255, 0.49);
		border-left: 9rpx solid #476AE2;
		animation: rotate 5s infinite linear;
		position: relative;

		.bigRadius {
			width: 32rpx;
			height: 32rpx;
			border-radius: 50%;
			background-color: #476AE2;
			position: absolute;
			left: 32rpx;
			top: 12rpx;
		}

		.minRadius {
			z-index: 100;
			width: 10rpx;
			height: 10rpx;
			border-radius: 50%;
			background-color: #476AE2;
			position: absolute;
		}

		.minRadius1 {
			left: 42rpx;
			top: 234rpx;
		}

		.minRadius2 {
			left: 58rpx;
			top: 244rpx;
		}

		@keyframes rotate {
			100% {
				transform: rotate(360deg);
			}
		}
	}
</style>