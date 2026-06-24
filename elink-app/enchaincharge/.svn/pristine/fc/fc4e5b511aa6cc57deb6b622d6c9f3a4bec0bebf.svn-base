<template>
	<view class="container">
		<view class="container_content">
			<view class="container_flex">
				<view class="drivinglicense" @click="clickUploadPhotos">
					<image v-if="licensePath" :src="licensePath" ></image>
					<image v-else src="@/secondPackage/static/image/drivinglicense.png" ></image>
				</view>
				<view class="alter_text">车辆认证是将您的车辆信息与您的账户进行绑定，实现即插即充、V2G以及电池健康检测服务</view>
				<view class="button" @click="clickEnter">确认</view>
			</view>
		</view>
	</view>
</template>

<script>
import { parseCarLicense } from "@/secondPackage/api/index.js"
export default {
	name: 'drivinglicenseUpload',
	data() {
		return {
			imageFile: null,
			licensePath:null,
			// drivinglicenseInfo:null, // 行驶证信息
			
			// 正面
			// {
			//     "config_str": "null\n",         #配置字符串信息
			//     "plate_num": "沪A0M084",        #车牌号码
			//     "vehicle_type":"小型轿车",       #车辆类型
			//     "owner": "张三",                #所有人名称
			//     "use_character":"出租转非",      #使用性质
			//     "addr":"浙江省宁波市江东区丁街88弄", #地址
			//     "model":"桑塔纳牌SVW7180LE1",    #品牌型号
			//     "vin" : "LSVFF66R8C2116280",     #车辆识别代号
			//     "engine_num" : "416098",        #发动机号码
			//     "register_date":"20121127",       #注册日期
			//     "issue_date":"20130708",        #发证日期
			//     "issue_authority": "上海市公安员交通警察总队",             # 签发机关
			//     "request_id": "84701974fb983158_20160526100112",             #请求对应的唯一表示
			//     "success": true                 #识别成功与否 true/false
			// }
			
			// 反面
			// {
			//     "config_str":"{\"side\": \"back\" }",  #配置字符串信息
			//     "appproved_passenger_capacity":"5人",    #核定载人数
			//     "approved_load":"",                      #核定载质量
			//     "file_no":"530100001466",                #档案编号
			//     "gross_mass":"2000kg",                   #总质量
			//     "inspection_record":"检验有效期至2014年09月云A(01)",  #检验记录
			//     "overall_dimension":"4945x1845x1480mm",   #外廓尺寸
			//     "traction_mass":"",                       #准牵引总质量
			//     "unladen_mass":"1505kg"                   #整备质量
			//     "plate_num":"云AD8V02",                   #号牌号码
			//     "success":true,              #识别成功与否 true/false
			//     "request_id":"20180131144149_c440540b20a4dc079a10680ff60b2d2a" #请求对应的唯一表示
			// }
		};
	},
	methods: {
		clickUploadPhotos(){
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: result => {
					// console.log(result);
					this.imageFile = result.tempFilePaths.map((value, i) => {
						return {
							name: 'imageFile',
							uri: value
						};
					});
			
					this.licensePath = result.tempFilePaths[0];
				},
				fail: err => {
					console.log('chooseImage fail', err);
				}
			});
		},
		clickEnter(){
			if(!this.licensePath){
				uni.showToast({
					icon:'none',
					title:"请上传行驶证照片"
				})
				return
			}
			
			parseCarLicense(this.imageFile,{ }).then(res=>{
				if(res.data){
					let drivinglicenseInfo = res.data;
					
					let pages = getCurrentPages();
					let prevPage = pages[pages.length - 2];
					prevPage.$vm.drivinglicenseInfo = {
						vinCode: drivinglicenseInfo.vin,// VIN码
						licensePagePath: this.licensePath, // 行驶证图片路径
						plate_num: drivinglicenseInfo.plate_num,// 车牌号码
						motorNumber: drivinglicenseInfo.engine_num, // 发动机号
						registerDate: drivinglicenseInfo.register_date, // 注册日期
					}
					
					prevPage.$vm.imageFile = this.imageFile;
					prevPage.$vm.vehicleStatus = 1; // 认证成功
					// uni.navigateBack({ delta: pages.length - 2 });
					uni.navigateBack({ delta: 1 });

				} else {
					uni.showToast({
						icon:"none",
						duration:3000,
						title: res.message || "请上传正确的行驶证照片！"
					})
				}
			})
		}
	}  
};
</script>
   
<style scoped lang="scss">
.container_flex {
	background-color: #FFFFFF;
	padding: 108rpx 40rpx 20rpx 40rpx;
	box-sizing: border-box;
	
	.drivinglicense{
		width: 100%;
		padding: 0 36rpx;
		margin-bottom: 28rpx;
		box-sizing: border-box;
		
		image{
			width: 100%;
			height: 410rpx;
			border-radius: 10rpx;
		}
	}

	.alter_text {
		color: #242424;
		font-size: 30rpx;
		text-align: center;
		margin-bottom: 54rpx;
	}
	.button {
		height: 98rpx;
		background: #476AE2;
		border-radius: 12rpx;
		font-size: 36rpx;
		font-weight: 600;
		color: #FFFFFF;
		text-align: center;
		line-height: 98rpx;
	}
}
</style>
