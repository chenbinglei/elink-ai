<template>
	<view class="container">
		<view class="container_li">
			<view class="li_top">电话</view>
			<view class="li_btm">
				<view class="phone_li">
					<text class="phone">{{ appConfig.phone | moreData}}</text>
					<text class="blod_text" @click="phoneCallFun">拨打电话</text>
				</view>
			</view>
		</view>
		<template v-if="appConfig.email && appConfig.email.length">
			<view class="container_li">
				<view class="li_top">邮箱</view>
				<view class="li_btm">
					<view class="phone_li" v-for="(item,index) in appConfig.email" :key="index">
						<text class="phone">{{ item.email }}</text>
						<text class="blod_text" @click="copyValue(item.email)">复制</text>
					</view>
				</view>
			</view>
		</template>
		
		<view class="container_li" v-if="appConfig.tencentImage">
			<view class="li_top">公众号</view>
			<view class="li_btm code">
				<image class="sm_code" show-menu-by-longpress :src="appConfig.tencentImage" @click="previewImageFun"></image>
				<text class="weChat_text">长按二维码关注微信公众号</text>
			</view>
		</view>
		
	</view>
</template>

<script>
	import { mapState } from 'vuex';
	
	export default {
		name: 'contactUs',
		computed: { ...mapState(['appConfig']) },
		data() {
			return {
				
			}
		},
		methods: {
			// 点击复制
			copyValue(copyValue){
				uni.setClipboardData({ data: copyValue });
			},
			// 点击拨打电话
			phoneCallFun() {
				uni.makePhoneCall({ phoneNumber: this.appConfig.telephone });
			},
			// 长按二维码跳转公众号
			previewImageFun() {
				uni.previewImage({
					urls: this.appConfig.tencentImage,
					success: (res) => {
						console.log("success");
					}
				})
			}
		}
	}
</script>

<style scoped lang="scss">
.container {
	margin: 32rpx;
	padding: 32rpx;
	box-sizing: border-box;
	border-radius: 24rpx;
	background-color: #ffffff;

	.container_li {
		display: flex;
		flex-direction: column;
		margin-bottom: 100rpx;
		
		&:last-child {
			margin-bottom: 0;
		}
		
		.li_top {
			color: #242424;
			margin-bottom: 48rpx;
		}
		
		.li_btm {
			display: flex;
			flex-direction: column;
			
			.phone_li {
				display: flex;
				margin-bottom: 24rpx;
				
				&:last-child {
					margin-bottom: 0;
				}
				
				.phone {
					color: #989898;
					margin-right: 48rpx;
				}
				
				.blod_text {
					color: #1D5ED2;
				}
			}
		}
		
		.code {
			display: flex;
			flex-direction: column;
			align-items: center;
			
			.sm_code{
				width: 240rpx;
				height: 240rpx;
				margin-bottom: 24rpx;
			}
			
			.weChat_text{
				color: #989898;
				font-size: 24rpx;
			}
		}
	}
}
</style>
