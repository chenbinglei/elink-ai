<template>
	<view class="container">
		<view class="container_content">
			<view class="search-input">
				<uni-easyinput type="search" class="uni-input" v-model="brandName" placeholder="搜索品牌" prefixIcon="search" :inputBorder="false" 
				:styles="inputStyle" @input="searchBrand"></uni-easyinput>
			</view>
			<view class="container_flex">
				<lxy-steps class="stepslist"></lxy-steps>
				<view class="brandList">
					<view class="brandList_left" id="brandList_left">
						<scroll-view scroll-y="true" :style="{ height: winHeight }" :scroll-into-view="scrollTopId">
							<view class="brandList_li" v-for="(item, index) in brandList" :key="index">
								<view class="letter" :id="item.letter">{{ item.letter }}</view>
								<view class="vehicleName_list">
									<view class="vehicleName" v-for="(itam, indax) in item.children" :key="indax" @click="clickSelect(itam)">
										<text class="text">{{ itam.name }}</text>
									</view>
								</view>
							</view>
						</scroll-view>
					</view>

					<view class="brandList_right">
						<view class="letter" v-for="(item, index) in brandList" :key="index" :data-letter="item.letter" @click="clickLetter">{{ item.letter }}</view>
					</view>
				</view>
			</view>
		</view>
		
		<view class="showSlectedLetter" v-if="isShowLetter"> {{ toastShowLetter }} </view>
	</view>
</template>

<script>
import pinyin from '@/secondPackage/utils/pinyin/pinyin3.js';
import { getCarBrandList } from '@/secondPackage/api/index.js';
import LxySteps from '@/secondPackage/components/lxy-steps.vue';
export default {
	name: 'vehicleBrand',
	components: { LxySteps },
	data() {
		return {
			brandName: '',
			inputStyle: {
				height: '80rpx',
				color: '#DBD9D9',
				backgroundColor: '#FFFFFF',
				fontSize: '32rpx',
				borderRadius: '10rpx'
			},

			brandList: [],
			oldBrandList:[],
			winHeight: '0',
			scrollTopId: '',
			isShowLetter: false,
			toastShowLetter: '',//点击右侧字母显示弹框 字母
		};
	},
	onLoad() {
		this.queryCarBrandList();
	},
	methods: {
		queryCarBrandList() {
			getCarBrandList({}).then(res => {
				let brandList = JSON.parse(JSON.stringify(res.data));
				brandList.forEach(val => {
					val.code = String(val.id);
					val.name = val.brandName;
				});
				this.oldBrandList = brandList;
				let newBrandList = pinyin.paixu(brandList);
				newBrandList.sort(function(o1, o2) {
					return o1.letter.charCodeAt(0) - o2.letter.charCodeAt(0);
				});
				
				uni.createSelectorQuery().select('#brandList_left').boundingClientRect().exec((data)=>{
					let findItem = data.find(item=> item.id === "brandList_left");
					if(findItem)this.winHeight = findItem.height - 10 + 'px';
				});
				
				
				this.brandList = JSON.parse(JSON.stringify(newBrandList));
				// console.log(this.brandList);
			});
		},
		// 搜索品牌
		searchBrand(){
			let brandList = JSON.parse(JSON.stringify(this.oldBrandList));
			let showBrandList = JSON.parse(JSON.stringify(this.oldBrandList));
			if(this.brandName){
				showBrandList = [];
				for(let i = 0;i < brandList.length;i++){
					if(brandList[i].brandName.indexOf(this.brandName) !== -1){
						showBrandList.push(brandList[i]);
					}
				}
			}
			
			let newBrandList = pinyin.paixu(showBrandList);
			newBrandList.sort(function(o1, o2) {
				return o1.letter.charCodeAt(0) - o2.letter.charCodeAt(0);
			});
			
			this.brandList = JSON.parse(JSON.stringify(newBrandList));
		},
		clickLetter(e) {
			const showLetter = e.currentTarget.dataset.letter;
			this.toastShowLetter = showLetter;
			this.isShowLetter = true;
			this.scrollTopId = showLetter;

			setTimeout(() => {
				this.isShowLetter = false;
			}, 500);
		},
		clickSelect(itam){
			// console.log(itam);
			let data = {
				brandId: itam.code,
				brandName: itam.name
			}
			uni.navigateTo({
				url: `/secondPackage/vehicleManagement/vehicleSeries?item=${encodeURIComponent(JSON.stringify(data))}`
			});
		}
		
	}
};
</script>

<style scoped lang="scss">
.container_content {
	padding: 24rpx 0;
	box-sizing: border-box;
	
	.container_flex {
		background: #fff;
		margin-top: 24rpx;
			
		.stepslist {
			margin-top: 58rpx;
			margin-bottom: 60rpx;
		}
		
		.brandList {
			flex: 1;
			display: flex;
			align-items: center;
			padding: 0 32rpx;
			box-sizing: border-box;
		
			.brandList_left {
				flex: 1;
				height: 100%;
		
				.brandList_li {
					margin-bottom: 28rpx;
					
					.letter {
						font-size: 30rpx;
						color: #242424;
						margin-bottom: 14rpx;
					}
					.vehicleName_list {
						padding-left: 30rpx;
						box-sizing: border-box;
		
						.vehicleName {
							height: 88rpx;
							line-height: 88rpx;
							padding-left: 48rpx;
							box-sizing: border-box;
							border-bottom: 2rpx solid #454545;
							font-size: 28rpx;
							color: #242424;
						}
					}
				}
			}
		
			.brandList_right {
				height: 100%;
				display: flex;
				flex-direction: column;
				justify-content: center;
		
				.letter {
					font-size: 24rpx;
					color: #242424;
				}
			}
		}
	}
}

.showSlectedLetter {
	background-color: rgba(0, 0, 0, 0.5);
	color: #fff;
	display: flex;
	justify-content: center;
	align-items: center;
	position: fixed;
	top: 50%;
	left: 50%;
	margin: -100rpx;
	width: 200rpx;
	height: 200rpx;
	border-radius: 20rpx;
	font-size: 52rpx;
	z-index: 1;
}
</style>
