<template>
	<view class="container">
		<view class="container_content">
			<view class="container_flex">
				<lxy-steps class="stepslist" :activeIndex="3"></lxy-steps>
				<view class="brandList">
					<view class="brandList_left" id="brandList_left">
						<view class="brandList_li" v-for="(item, index) in carTypeList" :key="index">
							<view class="letter" :id="item.letter">{{ item.letter }}</view>
							<view class="vehicleName_list">
								<view class="vehicleName" v-for="(itam, indax) in item.children" :key="indax" @click="clickSelect(itam)">
									<text class="text">{{ itam.typeName }}</text>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import LxySteps from '@/secondPackage/components/lxy-steps.vue';
import { getCarTypeListBySeriesId } from '@/secondPackage/api/index.js';
export default {
	name: 'vehicleModel',
	components: { LxySteps },
	data() {
		return {
			brandName: '',
			brandId: '',
			seriesName: '',
			seriesId: '',

			carTypeList: []
		};
	},
	onLoad(options) {
		let data = JSON.parse(decodeURIComponent(options.item));
		this.brandId = data.brandId;
		this.brandName = data.brandName;
		this.seriesId = data.seriesId;
		this.seriesName = data.seriesName;
		this.queryCarTypeListBySeriesId();
	},
	methods: {
		queryCarTypeListBySeriesId() {
			getCarTypeListBySeriesId({ seriesId: this.seriesId }).then(res => {
				let carTypeList = JSON.parse(JSON.stringify(res.data));

				//没有车型直接调用返回方法
				if(carTypeList.length <= 0){
					this.clickSelect({ typeName:'',id:'' });
					return
				}

				this.carTypeList = JSON.parse(
					JSON.stringify([
						{
							letter: this.seriesName,
							children: carTypeList
						}
					])
				);
			});
		},
		clickSelect(itam) {
			let pages = getCurrentPages();
			let prevPage = pages[pages.length - 4];
			prevPage.$vm.bindCarData = {
				brandId: this.brandId,
				brandName: this.brandName,
				seriesName: this.seriesName,
				seriesId: this.seriesId,
				// picturePath:this.picturePath,
				typeName: itam.typeName,
				typeId: itam.id
			};
			
			if(!prevPage.$vm.totalKm && itam.totalKm)prevPage.$vm.totalKm = itam.totalKm;
			if(!prevPage.$vm.batteryCap && itam.batteryCap)prevPage.$vm.batteryCap = itam.batteryCap;
			
			//因为修改的是data里面的绑定数据，所以返回后页面数据会直接显示修改后的
			uni.navigateBack({ delta: 3 });
		}
	}
};
</script>

<style scoped lang="scss">
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
			overflow: auto;

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
	}
}
</style>
