<template>
	<view :class="['switch', sizeClass]" :style="{ background: check ? switchColor : closeColor }" @click="clickSwitch">
		<view class="block" :style="{ background: check ? blockColor : switchColor }" :class="check ? 'blockRight' : 'blockleft'"></view>
	</view>
</template>

<script>
export default {
	name: 'switch',
	props: {
		checked: {
			type: [Boolean,Number],
			default: false
		},
		switchColor: {
			type: String,
			default: '#476AE2'
		},
		// 关闭背景颜色
		closeColor: {
			type: String,
			default: '#E2E2E2'
		},
		blockColor: {
			type: String,
			default: '#ffffff'
		},
		sizeClass: {
			type: String,
			default: ''
		}
	},
	data() {
		return {
			check: this.checked
		};
	},
	watch: {
		checked: {
			handler(val) {
				this.check = val;
			}
		}
	},
	methods: {
		clickSwitch() {
			this.check = !this.check;
			this.$emit('change', this.check);
		}
	}
};
</script>

<style scoped lang="scss">
.switch {
	width: 140rpx;
	height: 48rpx;
	border-radius: 10rpx;
	padding: 0 4rpx;
	box-sizing: border-box;
	display: flex;
	align-items: center;

	.block {
		width: 64rpx;
		height: 40rpx;
		border-radius: 10rpx;
		transition: all 0.28s;
	}

	.blockleft {
		transform: translateX(0rpx);
	}

	.blockRight {
		transform: translateX(68rpx);
	}
}

.small {
	width: 66rpx;
	height: 22rpx;

	.block {
		width: 30rpx;
		height: 18rpx;
	}

	.blockRight {
		transform: translateX(30rpx);
	}
}
</style>
