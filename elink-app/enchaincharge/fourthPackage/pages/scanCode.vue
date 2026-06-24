<template>
	<view class="container">
		
		<template v-if="isScanCode">
			<camera class="cameraClass" frame-size="large" mode="scanCode" :device-position="devicePosition"
				:flash="flash ? 'on' : 'off'" @error="cameraError" @scancode="scancode" ></camera>
			<view class="camera_view">
				<view class="camera_frame" :animation="animationData"></view>
			</view>
		</template>
		<template v-else>
			<view class="container_content">
				<view class="sancode_image">
					<image class="image" :src="getStaticFilePath('sancode.png')"></image>
				</view>
				<view class="manualInput">
					<uni-easyinput type="number" class="uni-input" :focus="isScanCode" v-model="terminalCode" placeholder="请输入终端号编号" 
					            :maxlength="32" :inputBorder="false" :styles="inputStyle"></uni-easyinput>
					<view class="enterBut" @click="$noMultipleClicks(handleConfirm)">确定</view>
				</view>
			</view>
		</template>
		
		<view class="content_bottom" :class="{ 'input_bottom' : !isScanCode}">
			<view class="camera_text" v-if="isScanCode">请将摄像头对准二维码进行扫描</view>
			<view class="bottom_buts">
				<view class="button_li" @click="clickSwitch">
					<view class="button">
						<text class="iconfont icon-jianpan-xianxing jianpan" v-if="isScanCode"></text>
						<text class="iconfont icon-saoma saoma" v-else></text>
					</view>
					<text class="text">{{ isScanCode ? '输入终端号' : '扫码' }}</text>
				</view>
				<view class="button_li" @click="clickOpenAlbum">
					<view class="button">
						<text class="iconfont icon-tupian tupian"></text>
					</view>
					<text class="text">选择图片</text>
				</view>
				<view class="button_li" v-if="isScanCode" :class="{ flash: flash }" @click="clickFlash">
					<view class="button">
						<text class="iconfont icon-shoudiantong" :class="[ flash ? 'shoudian_a' : 'shoudian']"></text>
					</view>
					<text class="text">{{ flash ? '关闭手电筒' : '打开手电筒' }}</text>
				</view>
			</view>
		</view>
		<lxy-qrcode @callback="returnValue" ref="lxyQrcodeRef"></lxy-qrcode>
	</view>
</template>
<script>
import lxyQrcode from '@/fourthPackage/components/lxy-qrcode/lxy-qrcode.vue';

export default {
	name: 'scanCode',
	components: { lxyQrcode },
	data() {
		return {
			isFang: true,
			noClick: true,
			isScanCode: true, //true:扫码  false: 手动输入终端号
			animation: uni.createAnimation({}),
			devicePosition: 'back', //前置或后置摄像头，值为front, back
			flash: false, // 闪光灯，值为 auto, on, off
			animationData: {},
			inputStyle: {
				height: '96rpx',
				color: '#454545',
				backgroundColor: '#F7F7F7',
				borderColor: '#E6E6E6',
				borderRadius: '12rpx',
				fontSize: '32rpx'
			},

			terminalCode: '' // 3102620230726002  3302620230726003
		};
	},
	onLoad(options) {
		this.donghua();
		this.startMode = options.startMode || 1;
	},
	methods: {
		// 确认事件
		handleConfirm() {
			this.returnValue('terminalCode', this.terminalCode);
		},
		//小程序端扫码成功
		scancode(e) {
			// console.log(e);
			this.returnValue('scanCode', e.detail.result);
		},
		// 返回返回结果
		returnValue(type, result) {
			if (this.isFang) {
				this.isFang = false;
				let opt = { type, result }; // 回调函数

				// 手机振动
				uni.vibrateLong({
					complete: () => {
						// this.isFang = false;
						this.$scanCodeEvent(opt);
					}
				});
			}
		},
		//用户不允许使用摄像头时触发  （小程序）
		cameraError(e) {
			// console.log(e);
			uni.showToast({ icon: 'none',title: '请打开摄像头权限才可进行扫码!' });
		},
		// 点击切换按钮
		clickSwitch() {
			this.flash = false;
			this.isScanCode = !this.isScanCode;
		},
		//打开手电筒
		clickFlash() {
			this.flash = !this.flash;
		},
		donghua() {
			let m = false; // 控制向上还是向下移动   默认执行一次
			this.animation.translateY(480).step({ duration: 3500 });
			this.animationData = this.animation.export();
			this.timer = setInterval(() => {
				if (m) {
					this.animation.translateY(480).step({ duration: 3500 });
					m = !m;
				} else {
					this.animation.translateY(120).step({ duration: 3500 });
					m = !m;
				}
				this.animationData = this.animation.export();
			}, 3500);
		},
		clickOpenAlbum() {
			this.$refs.lxyQrcodeRef.getQrcode();
		}
	}
};
</script>
<style lang="scss" scoped>
.container {
	background-color: #ffffff;
	
	.cameraClass{
		width: 100%;
		height: 100%;
		transition: all .28s;
	}
	
	.camera_view{
		width: 100%;
		height: 100%;
		position: absolute;
		left: 0;
		top: 0;
		padding: 0 78rpx;
		box-sizing: border-box;
		
		.camera_frame{
			width: 100%;
			box-shadow: 0rpx 0rpx 20rpx 5rpx #15E8AF;
		}
	}
	
	.container_content{
		padding: 100rpx 0;
		transition: all .28s;
		
		.sancode_image{
			width: 100%;
			margin-bottom: 120rpx;
			
			.image{
				width: 100%;
				height: 240rpx;
			}
		}
		
		.manualInput {
			width: 100%;
			padding: 0 32rpx;
			box-sizing: border-box;
		
			.uni-input {
				caret-color: #ffffff;
				font-size: 28rpx;
				padding: 0 16rpx;
			}
		
			.enterBut {
				height: 96rpx;
				color: #ffffff;
				font-size: 34rpx;
				border-radius: 12rpx;
				background: #476AE2;
				text-align: center;
				line-height: 96rpx;
				margin-top: 20rpx;
			}
		}
	}
	
	.content_bottom {
		width: 100%;
		padding: 0 40rpx;
		box-sizing: border-box;
		position: absolute;
		left: 0;
		bottom: 180rpx;
		z-index: 10000;
		
		.camera_text{
			color: #ffffff;
			font-size: 24rpx;
			text-align: center;
			margin-bottom: 14rpx;
		}
		
		.bottom_buts {
			padding: 0 32rpx;
			box-sizing: border-box;
			display: flex;
			justify-content: space-between;
	
			.button_li {
				display: flex;
				flex-direction: column;
				align-items: center;
				
				.text {
					color: #ffffff;
					font-size: 24rpx;
				}
				
				.button {
					width: 80rpx;
					height: 80rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					margin-bottom: 8rpx;
					border-radius: 50%;
					background: rgba(0, 0, 0, 0.4);
					
					.iconfont{
						color: #ffffff;
						font-size: 48rpx;
					}
				}
			}
			
			.flash{
				.text,.iconfont {
					color: #476AE2 !important;
				}
			}
		}
	}
	
	.input_bottom{
		.text{
			color: #242424 !important;
		}
	}
}
</style>
