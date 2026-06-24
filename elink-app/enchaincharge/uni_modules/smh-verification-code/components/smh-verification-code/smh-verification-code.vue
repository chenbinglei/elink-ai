<template>
	<view class="containe">
		<view class="code" @click="changeStatus">
			<view v-for="(item, index) in unit" class="verification_code" :class="{ active: pwd1[index] }">
				<view class="line" v-if="pwd1.length === index"></view>
				<view class="value">{{ pwd1[index] }}</view>
			</view>
		</view>
		<view class="input">
			<input class="uni-input" type="number" :maxlength="unit" @input="change1" v-model="pwd" v-show="true" :focus="inputStatus" />
		</view>
	</view>
</template>

<script>
export default {
	props: {
		unit: {
			type: Number,
			default: 6
		},
		focus: {
			type: Boolean,
			default: false
		},
		ontime: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			pwd: '',
			pwd1: [],
			inputStatus: this.focus,
		};
	},
	methods: {
		changeStatus() {
			this.inputStatus = !this.inputStatus;
		},
		change1(e) {
			this.pwd1 = String(e.target.value).split('');
			if (this.ontime) {
				this.$emit('change', e.target.value);
			} else {
				if (this.pwd1.length == this.unit) {
					this.$emit('change', e.target.value);
				}
			}
		}
	}
};
</script>

<style lang="scss" scoped>
.containe {
	.code {
		width: 100%;
		display: flex;
		justify-content: space-between;

		.verification_code {
			width: 88rpx;
			height: 100rpx;
			border-radius: 6rpx;
			background: rgba(245, 245, 245, 0.6);

			display: flex;
			align-items: center;
			justify-content: center;
			
			.line {
				width: 4rpx;
				height: 60rpx;
				background: #476AE2;
				border-radius: 2rpx;
				animation: twinkling 1s infinite ease;
			}
		}

		.active {
			color: #28292a;
			font-size: 64rpx;
			font-weight: 600;
			background: #f5f5f5;
		}
	}

	.input {
		width: 0;
		height: 0;
		overflow: hidden;
	}
	
	
	
	@keyframes twinkling {
		0% {
			opacity: 0.2;
		}
		50% {
			opacity: 0.8;
		}
		100% {
			opacity: 0.2;
		}
	}
}
</style>
