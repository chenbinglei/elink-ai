<template>
	<view class="popup">
		<uni-popup ref="popupRef" :type="popupType" :safeArea="safeArea" :isMaskClick="isMaskClick">
			<view class="content" :class="{ contentBorderClass: popupType === 'bottom' }" :style="{ backgroundColor:backgroundColor }">
				<view class="content_top" v-if="headerVisible">
					<template v-if="popupShowType === 'rightTopClose'">
						<view class="top_left"></view>
						<view class="top_center">
							<text v-if="titleType === 'defaultTitle'" class="defaultTitle">{{ title }}</text>
							<text class="text" v-else>
								<text>筛选(</text>
								<text class="textNumber">{{ textNumber }}</text>
								<text>)</text>
							</text>
						</view>
						<view class="top_right" @click="clickCloseIcon">
							<text class="iconfont icon-chahao"></text>
						</view>
					</template>
					<template v-else>
						<view class="buttons cancelBut" @click="clickClose">取消</view>
						<view class="buttons enterBut" @click="clickEnter">确定</view>
					</template>
				</view>
				<view class="content_center"><slot name="content"></slot></view>
				<view class="content_bottom" v-if="footerSlot"><slot name="footer"></slot></view>
				<view class="content_bottom" v-if="popupShowType === 'rightTopClose' && footerVisible">
					<view class="buttons cancelBut" @click="clickClose">{{ cancelText }}</view>
					<view class="buttons enterBut" @click="clickEnter">确定</view>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
export default {
	name: 'sm-popup',
	props: {
		// 弹框类型
		popupShowType: {
			type: String,
			default: 'rightTopClose' //rightTopClose:  右上角带有关闭按钮   rightTopEnter： 右上角确定按钮
		},
		// 底部按钮是否显示
		footerVisible: {
			type: Boolean,
			default: true
		},
		// 头部是否显示
		headerVisible: {
			type: Boolean,
			default: true
		},
		// 底部插槽是否显示
		footerSlot: {
			type: Boolean,
			default: false
		},
		// 标题类型
		titleType: {
			type: String,
			default: 'defaultTitle' //titleNumber
		},
		// 标题
		title: {
			type: String,
			default: '' //titleNumber
		},
		isMaskClick: {
			type: Boolean,
			default: true
		},
		// 点击确定是否关闭弹框
		enterClosePopup: {
			type: Boolean,
			default: true
		},
		// 点击取消是否关闭弹框
		cancelClosePopup: {
			type: Boolean,
			default: true
		},
		// 总筛选数量
		textNumber: {
			type: [String, Number],
			default: 0 //titleNumber
		},
		// 弹框打开位置
		popupType: {
			type: String,
			default: 'bottom'
		},
		// 背景颜色
		backgroundColor: {
			type: String,
			default: '#ffffff'
		},
		// 取消按钮文字
		cancelText: {
			type: String,
			default: '取消'
		},
	},
	data() {
		return {
			safeArea: false,
		};
	},
	methods: {
		openPopupFun() {
			this.$refs.popupRef.open();
		},
		closePopupFun() {
			this.$refs.popupRef.close();
		},
		//  点击确定
		clickEnter() {
			if (this.enterClosePopup) this.$refs.popupRef.close();
			this.$emit('popupEvent', { operationType: 'enter' });
		},
		//  点击关闭
		clickClose() {
			if(this.cancelClosePopup)this.$refs.popupRef.close();
			this.$emit('popupEvent', { operationType: 'cancel' });
		},
		clickCloseIcon(){
			this.$refs.popupRef.close();
			this.$emit('popupEvent', { operationType: 'closeIcon' });
		}
	}
};
</script>
<style lang="scss" scoped>
.content {
	border-radius: 16rpx;
	box-sizing: border-box;

	.content_top {
		height: 96rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 32rpx;
		border-bottom: 2rpx solid #f5f5f7;
		box-sizing: border-box;
		.top_center {
			color: #2a2a2a;
			font-size: 32rpx;

			.textNumber {
				color: #107be9;
				font-weight: 500;
			}
		}

		.buttons {
			font-size: 32rpx;
			color: #989898;
		}
		.enterBut {
			color: #107be9;
		}
	}

	.content_center {
		width: 100%;
	}
	.content_bottom {
		display: flex;
		align-items: center;
		box-sizing: border-box;
		border-top: 2rpx solid #f5f5f7;
		padding: 24rpx 32rpx 0 32rpx;

		.buttons {
			height: 80rpx;
			font-size: 28rpx;
			border-radius: 12rpx;
			text-align: center;
			line-height: 80rpx;
		}

		.cancelBut {
			width: 200rpx;
			color: #107be9;
			margin-right: 24rpx;
			border: 2rpx solid #107be9;
		}
		.enterBut {
			flex: 1;
			color: #ffffff;
			background: #107be9;
		}
	}
}
.contentBorderClass{
	padding-bottom: 32rpx;
	border-radius: 24rpx 24rpx 0px 0px;
}
</style>
