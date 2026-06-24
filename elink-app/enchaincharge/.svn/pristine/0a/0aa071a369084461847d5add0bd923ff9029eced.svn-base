<template>
	<view class="ongoingOrders">
		<template v-if="orderList&& orderList.length">
			<template v-for="(item, index) in orderList">
				<ongoing-orders-card :key="index" :startMode="startMode" :orderInfo="item"></ongoing-orders-card>
			</template>
		</template>
		<view v-else class="null_data uni-flex flex-center align-center">
			<sm-null-data title="暂无进行中的订单"></sm-null-data>
			<view class="button" @click="goToPage('/secondPackage/orderManagement/orderlist')">
				<text>查看已完成订单</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { mapState } from 'vuex';
	import { appRealWebSocket } from "@/api/home.js";
	import OngoingOrdersCard from "./OngoingOrdersCard.vue";

	export default {
		name: "OngoingOrders",
		components:{ OngoingOrdersCard },
		props: {
			// 1: 充  2：放
			startMode: {
				type: [String, Number],
				default: 1
			},
		},
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				orderList: [],
				socketTask: null, //socket对象
				is_open_socket: false, //socket是否开启
			}
		},
		methods: {
			connectSocketInit() {
				// 创建一个this.socketTask对象【发送、接收、关闭socket都由这个对象操作】
				let webSocketlink = appRealWebSocket({
					orderType: this.startMode - 1,
					memberId: this.userInfo.memberId
				});

				uni.connectSocket({
					url: webSocketlink,
					success: res => {
						console.log('appWebSocket：连接成功');
					},
					fail: () => {
						uni.showToast({
							icon: 'none',
							title: '实时数据连接失败！'
						});
					}
				});

				uni.onSocketOpen((res) => {
					console.log('WebSocket连接正常打开中...！');
					this.is_open_socket = true;
				});

				uni.onSocketError((res) => {
					uni.showToast({
						icon: 'none',
						title: 'WebSocket连接打开失败，请检查！'
					});
					this.is_open_socket = false;
				});

				uni.onSocketClose((res) => {
					this.is_open_socket = false;
					console.log('WebSocket 已关闭！');
				});

				// 实时数据处理
				uni.onSocketMessage(res => {
					try {
						let data = JSON.parse(res.data);
						if (typeof data == 'object') {
							// console.log(data);
							this.orderList = data;
						}
					} catch (e) {
						//TODO handle the exception
						console.log(e);
					}
				});
			},
			//关闭socket
			closeWebSocket() {
				if (this.is_open_socket) {
					uni.closeSocket({
						success: (res) => {
							this.socketTask = null;
							this.is_open_socket = false;
							console.log('关闭成功', res);
						},
						fail: (err) => {
							console.log('关闭失败', err);
						}
					})
				}
			},
			goToPage(pageName) {
				// 查看用户是否登录
				if (this.$lockUserIfLogin(2)) {
					uni.navigateTo({ url: `${ pageName }?orderType=${ this.startMode }` });
				}
			}
		},
		beforeDestroy() {
			this.closeWebSocket();
		},
	}
</script>

<style scoped lang="scss">
	.ongoingOrders {
		width: 100%;
		height: 100%;
		padding-bottom: 32rpx;
		box-sizing: border-box;
		overflow-y: auto;

		.null_data {
			height: 100%;
			flex-direction: column;

			.button {
				margin-top: 14rpx;
				padding: 8px 24px;
				border-radius: 12rpx;
				box-sizing: border-box;
				background-color: #FFFFFF;
			}
		}
	}
</style>