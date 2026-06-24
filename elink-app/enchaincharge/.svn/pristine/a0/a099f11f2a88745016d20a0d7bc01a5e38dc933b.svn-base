<template>
	<uni-popup ref="smModalInput" type="center">
		<view class="sm-modal-input">
			<view class="modal_centent">
				<view class="modal_top">
					<view class="title">{{ mergeInfo.title }}</view>
					<view class="center_input" v-if="mergeInfo.editable">
						<uni-easyinput
							:type="mergeInfo.inputType"
							class="uni-input"
							v-model="content"
							:placeholder="mergeInfo.placeholderText"
							:inputBorder="false"
							:styles="inputStyle"
							:maxlength="mergeInfo.inputlength"
						></uni-easyinput>
					</view>
				</view>

				<view class="bottom_but">
					<view class="button cancel" v-if="mergeInfo.showCancel" :style="{ color: mergeInfo.cancelColor }" @click="cancelBut">{{ mergeInfo.cancelText }}</view>
					<view class="button enter" :style="{ color: mergeInfo.confirmColor }" @click="enterBut">{{ mergeInfo.confirmText }}</view>
				</view>
			</view>
		</view>
	</uni-popup>
</template>

<script>
export default {
	name: 'SmModalInput',
	props: {
		modalInfo: {
			type: Object,
			default: () => {
				return {};
			}
		}
	},
	watch: {
		modalInfo: {
			deep: true,
			handler(val, old) {
				this.setPageDataFun();
			}
		}
	},
	data() {
		return {
			defaultInfo: {
				editable: false, //是否显示输入框
				placeholderText: '', //显示输入框时的提示文本
				showCancel: true, //是否显示取消按钮，默认为 true
				cancelText: '取消', //取消按钮的文字
				cancelColor: '#000000', //取消按钮的文字颜色
				confirmText: '确定', //确定按钮的文字
				confirmColor: '#1AA787', //确定按钮的文字颜色

				inputType: 'text',
				inputlength: 1000, // 输入框可输入长度
				inputContent: '', // 默认输入框内容
				enterClosePopup: false // 确定是否关闭弹框
			},
			mergeInfo: {},
			content: '',
			inputStyle: {
				height: '88rpx',
				color: '#454545',
				borderColor: '#FFFFFF',
				backgroundColor: '#ECEAEA',
				borderRadius: '10rpx',
			}
		};
	},
	methods: {
		openModalInput() {
			this.$refs.smModalInput.open('center');
		},
		// 取消
		cancelBut() {
			this.$refs.smModalInput.close();
			this.$emit('modalEvent', { type: 'cancel' });
		},
		enterBut() {
			if (this.mergeInfo.enterClosePopup) this.$refs.smModalInput.close();
			this.$emit('modalEvent', { type: 'enter', content: this.content });
		},
		// 设置组件配置参数
		setPageDataFun() {
			let mergeInfo = Object.assign({}, this.defaultInfo, this.modalInfo);
			this.inputStyle.height = mergeInfo.inputType === 'textarea' ? '180rpx' : '88rpx';
			this.mergeInfo = JSON.parse(JSON.stringify(mergeInfo));
			this.content = this.mergeInfo.inputContent;
		},
		closeModalPopup() {
			this.$refs.smModalInput.close();
		}
	}
};
</script>

<style scoped lang="scss">
.sm-modal-input {
	width: 85vw;
	
	.modal_centent {
		width: 100%;
		background: #ffffff;
		border-radius: 12rpx;

		.modal_top {
			padding: 48rpx 40rpx 36rpx 40rpx;
			box-sizing: border-box;

			.title {
				text-align: center;
				margin-bottom: 24rpx;
				font-size: 30rpx;
				font-weight: bold;
				color: #000000;
			}

			.center_input {
				max-height: 180rpx;
				overflow-y: auto;
			}
		}

		.bottom_but {
			border-top: 2rpx solid #eeeef0;
			display: flex;

			.button {
				flex: 1;
				height: 98rpx;
				text-align: center;
				line-height: 98rpx;
				font-size: 30rpx;
				font-weight: 500;
			}
			.cancel {
				border-right: 2rpx solid #eeeef0;
			}
		}
	}
}
</style>
