<template>
	<view class="container"><ourLoading isFullScreen active text="loading..." /></view>
</template>

<script>
import ourLoading from '@/components/our-loading/our-loading.vue';
export default {
	name: 'welcome',
	components: { ourLoading },
	data() {
		return {};
	},
	onShow() {
		this.initStorageSync();
	},
	methods: {
		initStorageSync() {
			// 进入App 重新赋值登录信息
			let userInfo = uni.getStorageSync('USER_INFO');
			this.$store.dispatch('alterUserInfo', userInfo);
			uni.switchTab({ url: '/pages/station/index' });
		}
	}
};
</script>

<style></style>
