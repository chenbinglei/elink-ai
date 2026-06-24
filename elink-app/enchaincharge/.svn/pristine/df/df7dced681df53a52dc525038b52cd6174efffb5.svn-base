<template>
	<!-- 相册选择二维码进行识别，返回二维码内容 -->
	<view class="canvasBox" :style="{ width: canvasW + 'px', height: canvasH + 'px' }"><canvas class="canvas" canvas-id="myCanvas"></canvas></view>
</template>

<script>
import jsQR from './jsQR.js';

export default {
	data() {
		return {
			qrcodeData: '',
			canvasW: 300,
			canvasH: 300
		};
	},
	methods: {
		getQrcode() {
			uni.chooseImage({
				count: 1,
				sourceType: ['album'],
				success: res => {
					// 二维码信息
					const data = { qrcode: '', isqrcode: true };
					// 相册图片
					const tempFilePaths = res.tempFilePaths[0];

					// 画布对象
					const ctx = uni.createCanvasContext('myCanvas', this);
					ctx.drawImage(tempFilePaths, 0, 0, this.canvasW, this.canvasH);

					ctx.draw(true, ret => {
						uni.canvasGetImageData(
							{
								canvasId: 'myCanvas',
								x: 0,
								y: 0,
								width: this.canvasW,
								height: this.canvasH,
								success: imageData => {
									data.qrcode = jsQR(imageData.data, imageData.width, imageData.height, {
										inversionAttempts: 'dontInvert'
									});

									if (data.qrcode == null) {
										data.isqrcode = false;
									}

									this.beforeAvatarUpload(data);
								}
							},
							this
						);
					});
				}
			});
		},
		//识别成功进行回调
		beforeAvatarUpload(data) {
			if (data.isqrcode) {
				this.qrcodeData = data.qrcode.data;
				this.$emit('callback', 'scanCode', this.qrcodeData);
			} else {
				uni.showToast({
					icon: 'none',
					title: '没有识别到二维码'
				});
			}
		}
	}
};
</script>

<style scoped lang="scss">
.canvasBox {
	position: fixed;
	left: 9999999px;
	top: 999999px;
	z-index: -999;

	.canvas {
		width: 100%;
		height: 100%;
	}
}
</style>
