<template>
	<view class="nullData">
			<!-- 空数据展示页面 -->
		<image class="nullDataImage" :src="getStaticFilePath(imageName)" ></image>
		<text class="text">{{ title }}</text>
	</view>
</template>

<script>
export default {
	name: 'sm-null-data',
	props: {
		title: {
			type: String,
			default: '暂无数据'
		},
		imageName: {
			type: String,
			default: 'nullDataImage.png'
		},
	},
	data() {
		return {
			
		};
	}
};
</script>

<style scoped lang="scss">
.nullData {
	width: 100%;
	height: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;

	.nullDataImage {
		width: 404rpx;
		height: 404rpx;
	}
	
	.text {
		color: #828282;
		font-size: 24rpx;
		margin-top: 12rpx;
	}
}
</style>
