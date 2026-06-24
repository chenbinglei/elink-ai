<template>
	<view class="dischargeWallet">
		<template v-if="walletList && walletList.length">
			<swiper class="content_body" circular :autoplay="autoplay" :indicator-dots="indicatorDots"
				:indicator-color="indicatorColor" :indicator-active-color="indicatorActiveColor">
				<swiper-item v-for="(item, index) in walletList" :key="index">
					<view class="content_li">
						<view class="content_top">
							<view class="content_top_left twoShowText">放电钱包</view>
							<view class="content_top_right">
								<text class="active_index">{{ index + 1 }}</text>
								<text class="text">/</text>
								<text class="assemble">{{ walletList.length }}</text>
							</view>
						</view>
						<view class="content_center">
							<view class="top_left">
								<text class="iconfont icon-qianbao"></text>
							</view>
							<view class="top_right">
								<view class="text_top">{{ item.mchName | numberNull }}</view>
								<view style="display: flex;align-items: center;justify-content: space-between;">
									<view>
										<view class="text_bottom">{{ item.balance | moneyTwoNum }}</view>
										<view class="text_center">
											<text>可用余额（元）</text>
										</view>
									</view>
									<view>
										<view class="text_bottom-free">{{ item.freezeBalance | moneyTwoNum }}</view>
										<view class="text_center">
											<text>冻结金额（元）</text>
										</view>
									</view>
								</view>
							</view>

							<!-- <view class="caidan_class" @click.stop="clickLookButFun(item)">
								<text class="iconfont icon-zc-tishi"></text>
							</view> -->
						</view>
						<view class="content_bottom">
							<view class="content_flex" @click="clickWithdrawalBut(item)">余额提现</view>
							<view class="content_flex" @click="clickDetailBut(item)">交易明细</view>

						</view>
					</view>
				</swiper-item>
			</swiper>
		</template>
		<template v-else>
			<view class="content_body">
				<sm-null-data title="暂无放电钱包"></sm-null-data>
			</view>
		</template>

		<sm-popup ref="smPopupRef" :footerVisible="false" title="可使用场站">
			<template v-slot:content>
				<view class="content_list">
					<view class="content_li" v-for="(item, index) in siteNameList" :key="index">
						<text class="siteName twoShowText">{{ item }}</text>
					</view>
				</view>
			</template>
		</sm-popup>
	</view>
</template>

<script>
import { findAppletDisWalletListById } from "@/fourthPackage/api/index.js"

export default {
	name: "DischargeWallet",
	options: { styleIsolation: 'shared' }, //解决/deep/不生效**
	data () {
		return {
			walletList: [
				// {
				// 	balance: 1000,
				// 	accountId: 1,
				// 	mchName: "测试放电钱包"

				// }
			],
			siteNameList: [],
			autoplay: false, //是否自动切换
			indicatorDots: false, //是否显示面板指示点
			indicatorColor: "rgba(0,0,0,0.46)", //指示点颜色
			indicatorActiveColor: "#1F74E2", //当前选中的指示点颜色
		}
	},
	methods: {
		// 查询会员放电钱包账户列表
		queryAppMemberBagAccountList () {
			findAppletDisWalletListById({}).then(res => {
				let list = res.data ? res.data : [];
				this.walletList = JSON.parse(JSON.stringify(list));
			})
		},
		clickDetailBut (item) {
			uni.navigateTo({
				url: `/fourthPackage/pages/transactionDetails?accountId=${item.id}&&mchName=${item.mchName}`

			});
		},
		clickWithdrawalBut (item) {
			console.log(item,'item')
			uni.navigateTo({
				url: `/thirdPackage/Wallet/walletMoney?disWalletId=${item.id}&&money=${item.balance}&&mchName=${item.mchName}&&mchId=${item.mchId}`

			});
			// uni.showToast({
			// 	icon: "none",
			// 	title: "该功能暂未开放！"
			// });
		},
		clickLookButFun (item) {
			this.siteNameList = item.siteNameList;
			this.$refs.smPopupRef.openPopupFun();
		}
	}
}
</script>

<style scoped lang="scss">
.dischargeWallet {
	padding: 20rpx 32rpx 0 32rpx;
	box-sizing: border-box;

	.content_body {
		height: 412rpx;
		border-radius: 12rpx;
		background-color: #FFFFFF;

		.content_li {
			padding: 24rpx 32rpx 0 32rpx;
			box-sizing: border-box;

			.content_top {
				margin-bottom: 12rpx;
				display: flex;
				align-items: center;
				justify-content: space-between;

				.content_top_left {
					font-size: 32rpx;
					font-weight: bold;
					-webkit-line-clamp: 1;
				}

				.content_top_right {
					color: #ACACAC;
					font-size: 24rpx;
					margin-left: 12rpx;

					.active_index {
						color: #242424;
						font-size: 28rpx;
						font-weight: bold;
					}
				}
			}

			.content_center {
				position: relative;
				padding: 24rpx 28rpx;
				// background-color: #F3FAFF;
				box-sizing: border-box;
				display: flex;
				align-items: center;

				.top_left {
					margin-right: 82rpx;

					.iconfont {
						color: #008CFF;
						font-size: 58rpx;
					}
				}

				.top_right {
					flex: 1;

					.text_top {
						font-size: 32rpx;
					}

					.text_center {
						color: #828282;
						font-size: 32rpx;
						margin: 12rpx 0;
					}

					.text_bottom-free {
						margin-top: 38rpx;
						color: #FB0B0B;
						text-align: center;
						font-size: 48rpx;
					}

					.text_bottom {
						font-size: 48rpx;
						color: #476AE2;
						text-align: center;
						font-weight: bold;
						margin-top: 38rpx;
					}
				}

				.caidan_class {
					position: absolute;
					right: 24rpx;
					top: 24rpx;

					.iconfont {
						font-size: 100rpx;
						color: rgba(0, 0, 0, 0.46);
					}
				}
			}

			.content_bottom {
				padding: 24rpx 0;
				display: flex;
				align-items: center;
				border-top: 1px solid rgba(0, 0, 0, 0.1);

				.content_flex {
					flex: 1;
					font-size: 30rpx;
					text-align: center;
					border-right: 1px solid #E6E6E6;
					box-sizing: border-box;
					color: rgba(72, 106, 226, 1);

					&:last-child {
						border-right: none;
					}
				}
			}
		}

		/deep/ .nullDataImage {
			width: 200rpx;
			height: 200rpx;
		}
	}


	.content_list {
		max-height: 380rpx;
		padding: 24rpx 32rpx;
		box-sizing: border-box;
		overflow-y: auto;

		.content_li {
			padding: 12rpx 0;

			.siteName {
				font-size: 28rpx;
				-webkit-line-clamp: 1;
			}
		}
	}
}
</style>