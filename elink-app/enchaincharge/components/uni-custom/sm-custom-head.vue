<template>
	<view class="customHead" :style="{ height: customHeadHeight, paddingBottom: paddingBottom }">
		<view class="content_view flex-center align-center" :style="{ paddingTop: menuInfo.top + 'px' }">
			<view v-if="arrowStatus" class="view_left flex-center align-center" @click="clickBackPage">
				<view class="u-arrow u-arrow-left" :style="{ borderColor: themeColor }"></view>
			</view>
			<view class="view_center flex-center align-center" :style="{ color: themeColor }">
				<text v-if="titleStatus">{{ title }}</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'CustomHead',
	props: {
		title: {
			type: String,
			default: ''
		},
		// 文字是否显示
		titleStatus: {
			type: Boolean,
			default: true
		},
		// 文字颜色
		themeColor: {
			type: String,
			default: '#000000'
		},
		// 返回箭头是否显示
		arrowStatus: {
			type: Boolean,
			default: true
		}
	},
	data() {
		return {
			systemInfo: {},
			menuInfo: {},
			paddingLeft: '0px',
			paddingBottom: '0px',
			customHeadHeight:"94rpx",
		};
	},
	mounted() {
		this.getDeviceInfo();
	},
	methods: {
		getDeviceInfo() {
			// #ifdef MP-WEIXIN
			// 获取设备详情
			this.systemInfo = uni.getSystemInfoSync();
			// 获取微信小程序胶囊位置信息
			this.menuInfo = uni.getMenuButtonBoundingClientRect();
			// console.log(this.systemInfo)
			// console.log(this.menuInfo)
			this.customHeadHeight = this.menuInfo.bottom * 2 + 'rpx';
			this.paddingLeft = this.systemInfo.screenWidth - this.menuInfo.right + 'px';
			this.paddingBottom = this.menuInfo.top - this.systemInfo.statusBarHeight + 'px';
			// #endif
		},
		// 返回上一页
		clickBackPage() {
			uni.navigateBack({ delta: 1 });
		}
	}
};
</script>

<style scoped lang="scss">
.customHead {
	z-index: 1000000;
	position: relative;
	box-sizing: border-box;

	.content_view {
		width: 100%;
		height: 100%;
		position: absolute;
		top: 0;left: 0;
		box-sizing: border-box;

		.view_left {
			z-index: 100;
			padding-left: 28rpx;
			position: absolute;
			left: 0;

			.u-arrow {
				width: 20rpx;
				height: 20rpx;
				border-width: 4rpx;
			}

			.backButImage {
				width: 22rpx;
				height: 40rpx;
			}
		}

		.view_center {
			flex: 1;
			font-size: 28rpx;
			font-weight: 500;
			box-sizing: border-box;
		}
	}
}
</style>
