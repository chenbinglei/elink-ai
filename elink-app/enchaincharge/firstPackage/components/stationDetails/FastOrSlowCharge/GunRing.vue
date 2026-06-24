<template>
	<view :class="['GunRing', !isBackground ? 'noBackground' : '', 'gun_status' + gunWorkState]">
		<!-- 充电中，放电中 展示 -->
		<template v-if="gunWorkState === 1 || gunWorkState === 2">
			<view class="circle_left ab" :style="renderLeftRate"></view>
			<view class="circle_right ab" :style="renderRightRate"></view>
			<view class="circle_text gunText">
				<view class="text_top">{{ batterySOC ? batterySOC : 0 }}%</view>
				<view class="text_bottom">{{ gunWorkState | gunWorkState }}</view>
			</view>
		</template>
		<!-- 其余状态展示 -->
		<template v-else>
			<text class="gunText">{{ gunWorkState | gunWorkState }}</text>
		</template>
	</view>
</template>

<script>
export default {
	name: 'GunRing',
	props: {
		// 当前枪状态
		gunWorkState: {
			type: [String, Number],
			default: ''
		},
		// 当前电池soc
		batterySOC: {
			type: [String, Number],
			default: 0
		},
		// 是否有背景色
		isBackground: {
			type: Boolean,
			default: true
		}
	},
	data() {
		return {
			// batterySOC: 30,
			renderLeftRate: 'transform: rotate(0deg);',
			renderRightRate: 'transform: rotate(0deg);'
		};
	},
	watch: {
		batterySOC: {
			deep: true,
			immediate: true,
			handler(newVal, oldVal) {
				this.setRenderRateFun();
			}
		}
	},
	methods: {
		setRenderRateFun() {
			let batterySOC = this.batterySOC ? this.batterySOC : 0;
			
			if (batterySOC >= 50) {
				this.renderLeftRate = 'transform: rotate(' + 3.6 * (batterySOC - 50) + 'deg);';
			}

			if (batterySOC < 50) {
				this.renderRightRate = 'transform: rotate(' + 3.6 * batterySOC + 'deg);';
			} else {
				this.renderRightRate = `transform: rotate(0deg);border-color: ${this.gunWorkState === 2 ? 'rgba(235, 230, 133, 1)' : 'rgba(26, 150, 223, 1)'};`;
			}
		}
	}
};
</script>

<style scoped lang="scss">
.GunRing {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
	box-sizing: border-box;
	background: rgba(219, 217, 217, 0.5);
	border: 2rpx solid rgba(219, 217, 217, 0.5);

	.gunText {
		color: #979797;
		font-size: 20rpx;
		font-weight: 600;
	}

	.ab {
		position: absolute;
		left: 0;
		right: 0;
		top: 0;
		bottom: 0;
		margin: auto;
	}

	.circle_left {
		border-radius: 50%;
		transition: all 0.5s;
		border: 4rpx solid #fcebe0;
		clip: rect(0, 50rpx, 100rpx, 0);
	}

	.circle_right {
		border-radius: 50%;
		transition: all 0.5s;
		border: 4rpx solid #fcebe0;
		clip: rect(0, 100rpx, 100rpx, 50rpx);
	}

	.circle_text {
		width: 100%;
		height: 100%;
		color: #ec8d4a;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
}

// 充放电隐藏边框
.gun_status1 {
	// border: none;
	background: none;
	box-shadow: inset 0 0 0 4rpx rgba(26, 150, 223, 1);

	// .circle_left {
	// 	border-color: rgba(26, 150, 223, 1);
	// }
	// .circle_right {
	// 	border-color: rgba(26, 150, 223, 1);
	// }

	.circle_text {
		color: rgba(26, 150, 223, 1);
	}
}

.gun_status2 {
	background: none;
	box-shadow: inset 0 0 0 4rpx rgba(235, 230, 133, 1);

	// .circle_left {
	// 	border-color: rgba(235, 230, 133, 1);
	// }
	// .circle_right {
	// 	border-color: rgba(235, 230, 133, 1);
	// }

	.circle_text {
		color: rgba(235, 230, 133, 1);
	}
}

.gun_status3 {
	border-color: #199D7C;
	background: rgba(25,157,124,0);
	.gunText {
		color: #199D7C;
	}
}

.gun_status4 {
	border-color: rgba(255, 125, 30, 1);
	background: rgba(25, 157, 124, 0);
	.gunText {
		color: rgba(255, 125, 30, 1);
	}
}

.gun_status5 {
	border-color: #F3615B;
	background: rgba(243,97,91,0.06);
	.gunText {
		color: #F3615B;
	}
}

.gun_status9 {
	border-color: #D66312;
	background: rgba(214,99,18,0.06);
	.gunText {
		color: #D66312;
	}
}

.noBackground {
	background: none !important;
}
</style>
