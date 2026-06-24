<template>
	<view class="detailListCard">
		<view class="time_title">{{ detailInfo.dataTime }}</view>
		<view class="content_body">
			<template v-for="(item,index) in detailInfo.children">
				<view class="content_list" :key="index">
					<view class="content_li">
						<view class="content_li_top twoShowText">{{ item.siteName | numberNull }}</view>
						<view class="content_li_center">
							<view class="li_top_left uni-flex-item">
								<text class="tradeType">{{ item.tradeType | disWalletTradeType }}</text>
							    <text>-</text>
								<text class="type">{{ item.detailType | detailType }}</text>
							</view>
							<view class="li_top_right" :class="'detailType' + item.detailType">
								<text>{{ item.detailType == 2 ? "-" : "+" }}</text>
								<text>{{ item.tradeMoney | moneyTwoNum }}</text>
							</view>
						</view>
						<view class="content_li_bottom">
							<view class="twoShowText">{{ item.createTime | numberNull }}</view>
							<view class="li_bottom_right">
								<text class="">余额：</text>
								<text class="number">{{ item.remainMoney | moneyTwoNum }}</text>
							</view>
						</view>
					</view>
				</view>
			</template>
		</view>
	</view>
</template>

<script>
	export default {
		name: "DetailListCard",
		props: {
			detailInfo: {
				type: Object,
				default: () => {
					return {}
				}
			},
		},
		data() {
			return {

			}
		},
		methods: {

		}
	}
</script>

<style scoped lang="scss">
	.detailListCard {
		margin-bottom: 24rpx;

		.time_title {
			font-weight: bold;
			font-size: 32rpx;
			color: rgba(0, 0, 0, 0.8);
		}

		.content_body {
			margin-top: 8rpx;
			background: #FFFFFF;

			.content_list {
				padding: 0 32rpx;
				box-sizing: border-box;

				.content_li {
					padding: 16rpx 0;
					border-bottom: 2rpx solid #E6E6E6;
					box-sizing: border-box;
					
					.content_li_top{
						font-size: 28rpx;
						font-weight: bold;
						color: rgba(0,0,0,0.8);
						-webkit-line-clamp: 1;
						margin-bottom: 10rpx;
					}

					.content_li_center,
					.content_li_bottom {
						display: flex;
						align-items: center;
						justify-content: space-between;
					}

					.content_li_center {
						margin-bottom: 8rpx;
						
						.li_top_left{
							font-size: 28rpx;					
							font-weight: 500;
							color: rgba(0,0,0,0.8);
						}
						
						.li_top_right{
							color: rgba(0,0,0,0.8);
						}

						.detailType1 {
							color: #FF7C22;
						}
					}

					.content_li_bottom {
						font-size: 24rpx;
						
						.twoShowText {
							flex: 1;
							color: #B9B9B9;
							margin-right: 8rpx;
							-webkit-line-clamp: 1;
						}
						
						.li_bottom_right{
							color: rgba(0,0,0,0.8);
						}
					}
				}
				
				&:last-child {
					.content_li{
						border-bottom: none;
					}
				}
			}
		}
	}
</style>