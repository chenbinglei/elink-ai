<template>
	<uni-popup ref="selectAddress" type="bottom">
		<view class="selectAddress">
			<view class="address_top">请选择所在地区</view>
			<view class="address_center">
				<view class="steps_li" v-for="(item,index) in stepslist" :key="index" :class="{ activeClass: activeIndex >= index }" @click="clickSteps(index)">
					<view class="steps_left">
						<view class="line"></view>
						<view class="radio"></view>
					</view>
					<view class="steps_text" :class="{ activeText: activeIndex == index}">{{ item.title }}</view>
				</view>
			</view>
			<view class="address_bottom">
				<view class="select_title">{{ selectTitle }}</view>
				<scroll-view class="scroll_view" scroll-y="true">
					<view class="scroll_view_li" v-for="(item, index) in scrollViewArray" :key="index" @click="clickScrollView(item)"
					:class="{ activeClass: selectData.resideProvince == item.id || selectData.resideCity == item.id || selectData.resideArea == item.id }">
						<view class="text">{{ item.name }}</view>
						<template v-if="activeIndex <= 0 && selectData.resideProvince == item.id">
							<uni-icons type="checkmarkempty" size="20" color="#1EC89E"></uni-icons>
						</template>
					</view>
				</scroll-view>
			</view>
		</view>
	</uni-popup>
</template>

<script>
import { queryProvinceData, queryCityDataByProvinceId, queryAreaDataByCityId } from '@/thirdPackage/api/index.js';
export default {
	name: 'SelectAddress',
	data() {
		return {
			selectData: {},
			activeIndex: 0,
			selectTitle: '选择省份/地区',
			scrollViewArray: [], // 页面展示的
			
			stepslist:[{title: '请选择省份/地区'}],
			
			activeIndexId:''
		};
	},
	created() {
		// this.getProvinceData();
	},
	methods: {
		openPopup(selectData) {
			this.selectData = selectData;
			
			if(this.selectData.resideProvince){
				this.stepslist = [
					{ title: this.selectData.provinceName,id:this.selectData.resideProvince },
					{ title: this.selectData.cityName,id:this.selectData.resideCity },
				];
				
				if(this.selectData.resideArea){
					this.stepslist.push({ title: this.selectData.areaName,id:this.selectData.resideArea });
				}
				this.activeIndex = this.stepslist.length - 1;
				this.activeIndexId = this.stepslist[this.activeIndex - 1].id; // 当前的选择的id  省或者市
			}
		
			this.setPageTextFun();
			this.$refs.selectAddress.open('bottom');
		},
		clickScrollView(item){
			// 点击选择当前的，设置当前
			this.$set(this.stepslist,this.activeIndex,{ id: item.id,title: item.name })
			this.stepslist.splice(this.activeIndex + 1 );
			
			//设置完成，跳转下一级
			if(this.activeIndex >= 2){
				console.log(this.stepslist);
				
				let perAddress = {
					provinceName: this.stepslist[0].title,
					resideProvince: this.stepslist[0].id,
					
					resideCity: this.stepslist[1].id,
					cityName: this.stepslist[1].title,
					
					resideArea: this.stepslist[2].id,
					areaName: this.stepslist[2].title,
				}
				
				this.$emit("modalEvent",{ type:'enter',...perAddress });
				this.$refs.selectAddress.close();
				return
			}
			
			let activeIndex = this.activeIndex + 1;
			this.clickSteps(activeIndex);
		},
		clickSteps(activeIndex){
			this.activeIndex = activeIndex;
			this.activeIndexId = this.stepslist[this.activeIndex - 1]&&this.stepslist[this.activeIndex - 1].id;
			this.setPageTextFun();  
		},
		setPageTextFun(){
			if(this.activeIndex == 0){
				this.selectTitle = '请选择省份/地区';
				this.getProvinceData();
			}
			
			if(this.activeIndex == 1){
				this.selectTitle = '选择城市';
				if(!this.stepslist[this.activeIndex]){
					this.stepslist[this.activeIndex] = { title: '请选择城市' };
				}
				this.getCityDataByProvinceId();
			}
			
			if(this.activeIndex == 2){
				this.selectTitle = '选择区/县';
				if(!this.stepslist[this.activeIndex]){
					this.stepslist[this.activeIndex] = { title: '请选择区/县' };
				}
				this.getAreaDataByCityId();
			}
		},
		
		// 查询全国省份列表
		getProvinceData() {
			queryProvinceData({}).then(res => {
				res.data.forEach(item => {
					item.id = item.provinceId;
					item.name = item.provinceName;
				});
				
				this.scrollViewArray = res.data;
			});
		},
		// 根据全国省编码id查询下面市级数据
		getCityDataByProvinceId(){
			queryCityDataByProvinceId({ provinceId: this.activeIndexId }).then(res => {
				res.data.forEach(item => {
					item.id = item.cityId;
					item.name = item.cityName;
				});
				
				this.scrollViewArray = res.data;
			});
		},
		// 根据全国市编码id查询下面区县级数据
		getAreaDataByCityId() {
			queryAreaDataByCityId({ cityId: this.activeIndexId }).then(res => {
				res.data.forEach(item => {
					item.id = item.areaId;
					item.name = item.areaName;
				});
				
				this.scrollViewArray = res.data;
			});
		}
	}
};
</script>

<style scoped lang="scss">
.selectAddress {
	width: 100%;
	background-color: #ffffff;
	padding: 28rpx 24rpx 20rpx 24rpx;
	border-radius: 48rpx 48rpx 0 0;
	box-sizing: border-box;

	.address_top {
		margin-bottom: 20rpx;
		font-size: 32rpx;
		font-weight: bold;
		text-align: center;
	}

	.address_center {
		margin-bottom: 20rpx;
		padding-bottom: 20rpx;
		border-bottom: 2rpx solid #efeeee;
		
		.steps_li{
			display: flex;
			
			.steps_left{
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: flex-end;
				
				.radio{
					width: 6rpx;
					height: 6rpx;
					border-radius: 50%;
					border: 1px solid #efeeee;
				}
				.line{
					flex: 1;
					width: 2rpx;
					background-color: #efeeee;
				}
			}
			.steps_text{
				font-size: 24rpx;
				margin-left: 20rpx;
				padding-top: 30rpx;
			}
		}
		
		.activeClass{
			.radio{
				border-color: #1EC89E;
			}
			
			.radio,.line{
				background-color: #1EC89E !important;
			}
		}
		
		.activeText{
			color: #1EC89E;
		}
			
		.steps_li:first-child{
			.steps_left{
				.line{
					display: none;
				}
			}
		}
	}

	.address_bottom {
		.select_title {
			font-size: 28rpx;
			font-weight: bold;
			margin-bottom: 20rpx;
		}

		.scroll_view {
			height: 380rpx;
			overflow-y: auto;

			.scroll_view_li {
				font-size: 24rpx;
				display: flex;
				align-items: center;
				margin-bottom: 24rpx;

				.text {
					flex: 1;
					padding: 0 20rpx;
					box-sizing: border-box;
				}
			}
			
			.activeClass{
				color: #1EC89E;
			}
		}
	}
}
</style>
