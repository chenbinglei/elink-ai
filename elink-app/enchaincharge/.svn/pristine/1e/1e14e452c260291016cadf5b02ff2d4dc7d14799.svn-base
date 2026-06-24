<template>
	<view class="UseInfoAndPhoto">
		<view class="userInfo" @click="clickFun">
			<view class="photo">
				<template v-if="userInfo && userInfo.image">
					<image class="image" :src="userInfo.image"></image>
				</template>
				<view v-else class="null_image"></view>
			</view>
			<view class="info_all">
				<template v-if="userInfo">
					<view class="userName">{{ userInfo.nickName }}</view>
					<view class="phone">{{ userInfo.mobile | phoneFourRep }}</view>
				</template>
				<view class="up_text" v-else>点击登录</view>
			</view>
		</view>
		<view v-if="userInfo.nickName" class="editIcon" @click="clickFun">
			<text class="iconfont icon-bianji"></text>
		</view>
	</view>
</template>

<script>
import { mapState } from 'vuex';
export default {
	name: 'UseInfoAndPhoto',
	computed: {
		...mapState(['userInfo'])
	},
	data() {
		return {
			
		};
	},
	methods: {
		clickFun() {
			// 查看用户是否登录
			if(this.$lockUserIfLogin(2)){
				uni.navigateTo({
					url: '/thirdPackage/personalData/index'
				});
			}
		}
	}
};
</script>

<style lang="scss" scoped>
.UseInfoAndPhoto {
	display: flex;
	justify-content: space-between;
	padding: 12rpx 0;
	
	.userInfo {
		display: flex;
		
		.photo {
			width: 100rpx;
			height: 100rpx;
			overflow: hidden;
			border-radius: 50%;
			background-color: #FFFFFF;
			margin-right: 32rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			
			.null_image{
				width: 94rpx;
				height: 94rpx;
				border-radius: 50%;
				background-color: #F4F5F9;
			}
			
			.image {
				width: 100%;
				height: 100%;
			}
		}
		
		.info_all{
			display: flex;
			flex-direction: column;
			justify-content: space-around;
			
			.userName {
				font-size: 32rpx;
				color: #000000;
			}
			.phone {
				font-size: 24rpx;
				color: #989898;
			}
			.up_text {
				font-size: 36rpx;
			}
		}
	}

	.editIcon {
		display: flex;
		// align-items: center;
		
		.icon-bianji1 {
			font-size: 42px;
		}
	}
}
</style>
