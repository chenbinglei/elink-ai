<template>
	<view class="progress-button">

		<view class="outer_class" @touchstart="touchstartFun" @touchend="touchendFun">
			<view class="button_class">
				<view class="circle_left ab" :style="renderLeftRate"></view>
				<view class="circle_right ab" :style="renderRightRate"></view>
				<view class="circle_text">
					<view class="stop_icon">
						<text class="iconfont icon-tuichu"></text>
					</view>
				</view>
			</view>
		</view>

		<view class="text_class">长按结束</view>
	</view>
</template>

<script>
	export default {
		name: 'ProgressButton',
		data() {
			return {
				timer: null,
				schedule: 0, // 圆环进度
				stopTime: 3500, // 长按时间 ms
				intervalTimer: 0,
				renderLeftRate: 'transform: rotate(0deg);',
				renderRightRate: 'transform: rotate(0deg);'
			};
		},
		mounted() {
			this.countStopTimeFun();
		},
		methods: {
			countStopTimeFun() {
				this.intervalTimer = this.stopTime / 200;
				this.setRenderRateFun();
			},
			touchstartFun() {
				this.timer = setInterval(() => {
					if (this.schedule >= 100) {
						this.schedule = 100;
						this.touchendFun();
					} else {
						this.schedule += 2;
						this.setRenderRateFun();
					}
				}, this.intervalTimer);
			},
			touchendFun() {
				clearInterval(this.timer);
				
				if (this.schedule >= 100) {
					console.log("可以执行关闭了!!");
					this.$emit("changEvent",{ type: "clickCloseBut" });
				}
				
				this.schedule = 0;
				this.setRenderRateFun();
			},
			setRenderRateFun() {
				if(this.schedule <= 0){
					this.renderLeftRate = 'transform: rotate(0deg);';
					this.renderRightRate = 'transform: rotate(0deg);';
					return
				}
				
				if (this.schedule >= 50) {
					this.renderLeftRate = 'transform: rotate(' + 3.6 * (this.schedule - 50) + 'deg);';
				}

				if (this.schedule < 50) {
					this.renderRightRate = 'transform: rotate(' + 3.6 * this.schedule + 'deg);';
				} else {
					this.renderRightRate = `transform: rotate(0deg);border-color: #E2476A;`;
				}
			}
		}
	};
</script>

<style scoped lang="scss">
	.progress-button {

		.outer_class {
			width: 128rpx;
			height: 128rpx;
			padding: 2rpx;
			border-radius: 50%;
			box-sizing: border-box;
			border: 2rpx solid #E2476A;

			.button_class {
				width: 100%;
				height: 100%;
				position: relative;
				border-radius: 50%;
				box-shadow: inset 0 0 0 8rpx #E2476A;

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
					border: 9rpx solid #ffffff;
					clip: rect(0, 60rpx, 122rpx, 0);
				}

				.circle_right {
					border-radius: 50%;
					border: 9rpx solid #ffffff;
					clip: rect(0, 122rpx, 122rpx, 60rpx);
				}

				.circle_text {
					width: 100%;
					height: 100%;
					padding: 10rpx;
					box-sizing: border-box;

					.stop_icon {
						height: 100%;
						border-radius: 50%;
						background-color: #E2476A;
						display: flex;
						align-items: center;
						justify-content: center;

						.iconfont {
							color: #ffffff;
							font-size: 48rpx;
						}
					}
				}
			}
		}

		.text_class {
			color: #7E89B2;
			font-size: 24rpx;
			margin-top: 8rpx;
			text-align: center;
		}
	}
</style>