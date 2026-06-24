<template>
	<view :class="['sm-checkbox', isChecked ? 'checkbox' : '']" @click.stop="clickCheckbox">
		<text class="text frontText" v-if="lable && lableSeat">{{ lable }}</text>

		<template v-if="type === 'checkbox'">
			<view class="checkbox_con">
				<template v-if="isChecked">
					<image class="checked_yes" src="@/static/image/checked_yes.png"></image>
				</template>
			</view>
		</template>
		<template v-if="type === 'radio'">
			<view class="radio_con">
				<template v-if="isChecked">
					<image class="checked_yes" src="@/static/image/radio_yes.png"></image>
				</template>
			</view>
		</template>

		<text class="text nextText" v-if="lable && !lableSeat">{{ lable | textValue(startMode) }}</text>
	</view>
</template>

<script>
export default {
	name: 'sm-checkbox',
	props: {
		type: {
			type: String,
			default: 'checkbox' //checkbox:多选框   radio： 单选框
		},
		checked: {
			type: [Boolean, Object, Number],
			default: false
		},
		lable: {
			type: String,
			default: ''
		},
		// false 后面  true 前面
		lableSeat: {
			type: Boolean,
			default: false
		},
		isClick: {
			type: Boolean,
			default: false
		},
		// 是否返回当前对象数据
		isReturnObject: {
			type: Boolean,
			default: false
		},
		activeInfo: {
			type: Object,
			default: ()=>{
				return {}
			}
		},
		startMode:{
			type:[Number,String],
			default: 1
		},
	},
	data() {
		return {
			isChecked: false
		};
	},
	watch: {
		checked: {
			deep: true,
			immediate: true,
			handler(newVal, oldVal) {
				this.isChecked = this.checked;
				// console.log(this.isChecked);
			}
		}
	},
	methods: {
		clickCheckbox() {
			if(this.isClick){
				if(this.isReturnObject){
					this.$emit('change', this.activeInfo);
				} else {
					this.isChecked = !this.isChecked;
					this.$emit('change', this.isChecked);
				}
			}
		}
	}
};
</script>

<style lang="scss" scoped>
.sm-checkbox {
	display: flex;
	align-items: center;
	justify-content: space-between;

	.checkbox_con {
		width: 36rpx;
		height: 36rpx;
		border: 2rpx solid #d3d4de;
		box-sizing: border-box;
		border-radius: 8rpx;
		position: relative;

		.checked_yes {
			width: 100%;
			height: 100%;
			transition: all 0.28s;
			position: absolute;
			left: 0;
			top: 0;
		}
	}

	.radio_con {
		width: 32rpx;
		height: 32rpx;
		border: 2rpx solid #d3d4de;
		box-sizing: border-box;
		border-radius: 50%;
		position: relative;

		.checked_yes {
			width: 100%;
			height: 100%;
			transition: all 0.28s;
			position: absolute;
			left: 0;
			top: 0;
		}
	}

	.text {
		color: #2a2a2a;
		font-size: 28rpx;
	}

	.frontText {
		margin-right: 24rpx;
	}

	.nextText {
		margin-left: 24rpx;
	}
}

.checkbox {
	.text {
		color: #107be9;
		font-weight: 500;
	}
}
</style>
