<template>
	<view class="container">
		<view class="company-user">
			<view class="top_left">
				<text class="iconfont icon-qianbao"></text>
				{{ mchName }}
				<!-- {{}} -->
			</view>
			<detail-search-from ref="detailSearchFromRef" @headerFormEvent="pagingReload"></detail-search-from>
		</view>
		<view v-if="walletList.length === 0">
			<sm-null-data slot="empty" title="当前暂无明细"></sm-null-data>
		</view>
		<view v-else>
			<view v-for="(item, index) in walletList" :key="index" class="content_card">
				<view class="content_card_body">
					<view class="timer">{{ item.date }}</view>
					<view class="card_body_bottom">
						<view class="card_body_bottom_li">
							<text class="text">支出</text>
							<text class="number">-{{ item.outcomeMoney | moneyTwoNum }}</text>
							<text class="unit">元</text>
						</view>
						<view class="card_body_bottom_li">
							<text class="text">收入</text>
							<text class="number">+{{ item.incomeMoney | moneyTwoNum }}</text>
							<text class="unit">元</text>
						</view>
					</view>
				</view>
				<view class="content_card_border" v-for="items in item.tradeDetailList" :key="items.id">
					<view class="content_card_list_li top">
						<view>{{ items.tradeType == 1 ? 'V2G收益存入' : '余额提现' }}</view>
						<view :style="{ color: items.tradeType == 1 ? '#FB0B0B' : '#000000' }">{{ items.tradeType == 1 ? '+' : '-'
							}}{{ items.tradeMoney| moneyTwoNum  }}</view>
					</view>
					<view class="content_card_list_li bottom">
						<view>{{ items.createTime }}</view>
						<view>余额 {{ items.tradeBalance| moneyTwoNum  }}</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import {
	queryAppletTradeList
} from "@/fourthPackage/api/index.js"
import DetailListCard from "@/fourthPackage/components/transactionDetails/DetailListCard.vue";
import DetailSearchFrom from "@/fourthPackage/components/transactionDetails/DetailSearchFrom.vue";

export default {
	name: "transactionDetails",
	components: {
		DetailSearchFrom,
		DetailListCard
	},
	data () {
		return {
			list: [],
			dataList: [],
			accountId: "",
			walletList: [],
			// expendMoney: 0,
			// incomeMoney: 0,
			selectFromData: {},
			mchName: "",
			// date:""

		}
	},
	onLoad (options) {
		this.accountId = options.accountId;
		this.mchName = options.mchName;
		this.findMyDetailListByPage()
	},
	methods: {
		pagingReload () {
			console.log('pagingReload');
			let selectFromData = JSON.parse(JSON.stringify(this.$refs.detailSearchFromRef.selectFromData));
			if (selectFromData.endDate) selectFromData.endTime = selectFromData.endDate;
			if (selectFromData.startDate) selectFromData.startTime = selectFromData.startDate;
			this.selectFromData = JSON.parse(JSON.stringify(selectFromData));
			console.log(this.selectFromData);
			delete this.selectFromData.startDate;
			delete this.selectFromData.endDate;
			this.findMyDetailListByPage()
		},
		findMyDetailListByPage () {
			let data = {
				disWalletId: this.accountId,
				...this.selectFromData
			}
			queryAppletTradeList(data).then(res => {
				this.walletList = res.data
			})
		},
	}
}
</script>

<style scoped lang="scss">
.container {
	.company-user {
		background-color: #fff;

		.top_left {
			height: 98rpx;
			color: #000000;
			display: flex;
			align-items: center;
			padding: 48rpx;
			box-sizing: border-box;

			.iconfont {
				color: #008CFF;
				font-size: 58rpx;
				margin-right: 50rpx;
			}
		}

	}

	.content_card {
		padding: 12rpx 24rpx;
		box-sizing: border-box;

		.content_card_body {
			border-radius: 8rpx;
			padding: 20rpx 32rpx;
			box-sizing: border-box;
			display: flex;
			justify-content: space-between;
			align-items: center;

			.card_body_top {
				display: flex;
				align-items: center;

				.timer {
					font-size: 28rpx;
					font-weight: bold;
				}

				.text {
					margin: 0 12rpx;
				}
			}

			.card_body_bottom {
				display: flex;
				align-items: center;
				justify-content: space-between;
				width: 60%;

				.card_body_bottom_li {
					font-size: 24rpx;

					.text {
						margin-right: 8rpx;
					}

					.number {
						font-size: 28rpx;
						font-weight: bold;
						margin-right: 4rpx;
					}
				}
			}
		}

		.content_card_border {
			background-color: #fff;
			padding: 45rpx 24rpx;
			border-bottom: 1rpx solid rgba(0, 0, 0, 0.1);

			.content_card_list_li {
				display: flex;
				justify-content: space-between;
			}

			.top {
				font-weight: 550;
				font-size: 36rpx;
				color: rgba(0, 0, 0, 1);

			}

			.bottom {
				margin-top: 42rpx;

				font-size: 28rpx;
				color: rgba(0, 0, 0, .5);
			}
		}
	}

	.container_content {
		display: initial;

		.detail_list {
			width: 100%;
			padding: 24rpx 32rpx;
			box-sizing: border-box;
		}
	}
}
</style>