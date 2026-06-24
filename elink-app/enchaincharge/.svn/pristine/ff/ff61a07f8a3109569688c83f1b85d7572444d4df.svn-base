<template>
	<view class="startWaitingPopUp">

		<!-- 启动中 -->
		<view class="starting" v-if="startStatus == 1">
			<view class="starting_loading">
				<view class="loading"><loading-route></loading-route></view>
				<view class="content">
					<view class="top_text" v-if="secondNumber > 0">
						<view class="number">{{ secondNumber }}</view>
						<view class="text">秒</view>
					</view>
					<view class="bottom_text" v-else>
						<text>{{ secondNumber > 0 ? '倒计时' : '请稍等' }}</text>
						<text class="dotting"></text>
					</view>
				</view>
			</view>
			<view class="starting_bottom">
				<view class="alter_text">
					<text>{{ powerWay == 2 ? "正在预约中" : "正在启动中" }}</text>
					<text class="dotting"></text>
				</view>
				<view class="alter_text">请您耐心等待，勿拔枪</view>
			</view>
		</view>

		<view class="successAndError" v-else>
			<view class="image" :class="[startStatus == 2 ? 'success' : 'error']">
				<text v-if="startStatus == 2" class="iconfont icon-duihao"></text>
				<text v-else class="iconfont icon-point"></text>
			</view>
			<view class="bottomText">
				<view class="title_text">
					<text v-if="powerWay == 2">{{ startStatus == 2 ? '预约成功' : '预约失败' }}</text>
					<text v-else>{{ startStatus == 2 ? '启动成功' : '启动失败' }}</text>
				</view>
				<view class="title_bottom">
					<text v-if="startStatus == 2">
						<text v-if="powerWay == 2">自动进入预约状态</text>
						<text v-else>{{ '自动进入充电状态' | textValue(startMode) }}</text>
					</text>
					<text v-else>请重新检查设备,稍后重试!</text>
				</view>
			</view>
		</view>

	</view>
</template>

<script>
import { mapState } from 'vuex';
import LoadingRoute from "./LoadingRoute.vue";
import { appStartWebSocket } from '@/firstPackage/api/index.js';

export default {
	name: "StartWaitingPopUp",
	components: { LoadingRoute },
	options: { styleIsolation: 'shared' },//解决/deep/不生效**
	computed: {
		...mapState(['userInfo'])
	},
	props: {
		pileCode: {
			type: String,
			default: ""
		},
		gunCode: {
			type: String,
			default: ""
		},
		//1-充电  2-放电 
		startMode: {
			type: [String, Number],
			default: 1
		},
		//充电方式 1-立即充电  2-定时充电  3-自动充电
		powerWay: {
			type: [String, Number],
			default: 1
		},
	},
	data () {
		return {
			timer: null,
			startStatus: 1, // 1：启动中  2；启动成功  3：启动失败
			secondNumber: 60,
			pileTimeData: {},
			startingStatus: false, // true: 启动成功  false：启动中
			errorNumber: 1,// 连接失败次数
			maxErrorNumber: 3, // 最大失败连接次数
			socketTask: null, //socket对象
			is_open_socket: false, //socket是否开启
		}
	},
	mounted () {
		this.initParametersFun();
	},
	methods: {
		// 初始化参数
		initParametersFun () {
			this.startStatus = 1;
			this.secondNumber = 60;
			clearInterval(this.timer);
			this.setTimerFun();
		},
		// 设置启动状态
		setStartStatusFun (startStatus) {
			this.startStatus = startStatus; //启动失败
			clearInterval(this.timer);
		},
		setTimerFun () {
			clearInterval(this.timer);
			this.timer = setInterval(() => {
				this.secondNumber -= 1;

				if (this.secondNumber <= 0) {
					clearInterval(this.timer);
				}

				// 三秒之后连接webSocket
				if (this.secondNumber === 57) this.connectSocketInit();

			}, 1000);
		},
		connectSocketInit () {
			// 创建一个this.socketTask对象【发送、接收、关闭socket都由这个对象操作】
			let webSocketlink = appStartWebSocket({
				gunCode: this.gunCode,
				pileCode: this.pileCode,
				memberId: this.userInfo.memberId
			});

			uni.connectSocket({
				url: webSocketlink,
				success: res => {
					console.log('appWebSocket：创建成功');
				},
				fail: () => {
					uni.showToast({ icon: 'none', title: '实时数据创建失败！' });
					this.$emit("changEvent", { type: "pileStartError" });
				}
			});

			uni.onSocketOpen((res) => {
				console.log('WebSocket连接正常打开中...！');
				this.is_open_socket = true;
			});

			uni.onSocketError((res) => {
				uni.showToast({ icon: 'none', title: 'WebSocket连接打开失败，请检查！' });
				this.$emit("changEvent", { type: "pileStartError" });
				this.is_open_socket = false;
			});

			uni.onSocketClose((res) => {
				this.is_open_socket = false;
				console.log('WebSocket 已关闭！');
			});

			// 实时数据处理
			uni.onSocketMessage(res => {
				console.log('收到服务器内容：' + res.data);
				try {
					let data = JSON.parse(res.data);

					if (typeof data == 'object') {
						this.pileTimeData = data;
						// console.log(this.pileTimeData);

						// 启动失败
						if (!data.gunStateLogo) {
							this.startingStatus = false;
							this.closeWebSocket(); // 关闭连接
						}

						// 2: 启动成功  7: 预约成功
						if (data.gunStateLogo == 2 || data.gunStateLogo == 7) {
							this.startingStatus = true;
							this.setStartStatusFun(2);
							this.closeWebSocket(false);

							// 启动成功之后 三秒进行跳转
							setTimeout(() => {
								// 充电成功回到实时订单
								if (this.startMode == 1) {
									uni.switchTab({
										url: "/pages/charging/index",
										success: () => {
											uni.showToast({ icon: "none", title: "启动成功，请查看！" });
										}
									})
								} else {
									// 放电返回到 实时放电页面
									uni.navigateBack({ delta: 1 });
								}
							}, 3000);
						}
					}
				} catch (e) {
					//TODO handle the exception
					console.log(e);
				}
			});
		},
		//关闭socket
		closeWebSocket (isExecute = true) {
			if (this.is_open_socket) {
				uni.closeSocket({
					success: (res) => {
						this.socketTask = null;
						this.is_open_socket = false;
						// console.log('关闭成功', res);
						if (!this.startingStatus && isExecute) {
							this.errorNumber += 1;
							if (this.errorNumber <= this.maxErrorNumber) {
								setTimeout(() => {
									this.connectSocketInit();  // 重新连接
								}, 3000);
							} else {
								// 提示启动失败
								this.setStartStatusFun(3);
								setTimeout(() => {
									this.$emit("changEvent", { type: "pileStartError" });
								}, 3000);
							}
						}
					},
					fail: (err) => {
						console.log('关闭失败', err);
					}
				})
			}
		},
	},
	destroyed () {
		this.closeWebSocket(false);
		clearInterval(this.timer);
	},
}
</script>

<style lang="scss" scoped>
.startWaitingPopUp {
	width: 80vw;
	padding: 24rpx 32rpx;
	box-sizing: border-box;

	.starting {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;

		.starting_loading {
			width: 320rpx;
			height: 320rpx;
			margin-top: -160rpx;
			position: relative;

			.loading {
				width: 100%;
				height: 100%;
				padding: 32rpx;
				border-radius: 50%;
				box-sizing: border-box;
				background-color: #ffffff;
			}

			.content {
				width: 100%;
				height: 100%;
				z-index: 10;
				display: flex;
				align-items: center;
				justify-content: center;
				position: absolute;
				left: 0;
				top: 0;

				.top_text {
					display: flex;
					align-items: center;

					.number {
						color: #1F74E2;
						font-size: 62rpx;
						font-weight: bold;
					}

					.text {
						font-size: 28rpx;
						margin-left: 4rpx;
					}

				}

				.bottom_text {
					font-size: 32rpx;
					font-weight: bold;
				}
			}
		}

		.starting_bottom {
			margin-top: 4rpx;
			display: flex;
			flex-direction: column;
			justify-content: center;

			.alter_text {
				font-size: 32rpx;
				color: rgba(0, 0, 0, 0.8);
				margin-bottom: 12rpx;
				text-align: center;

				&:last-child {
					margin-bottom: 0;
				}
			}
		}
	}

	.successAndError {
		display: flex;
		flex-direction: column;
		align-items: center;

		.image {
			width: 88rpx;
			height: 88rpx;
			border-radius: 50%;
			text-align: center;
			line-height: 88rpx;

			.iconfont {
				color: #ffffff;
				font-size: 32rpx;
				font-weight: bold;
			}
		}

		.success {
			background-color: #1F74E2;
		}

		.error {
			background-color: #FF8C65;
		}

		.bottomText {
			font-size: 28rpx;
			margin-top: 32rpx;
			display: flex;
			flex-direction: column;
			align-items: center;
		}
	}
}
</style>