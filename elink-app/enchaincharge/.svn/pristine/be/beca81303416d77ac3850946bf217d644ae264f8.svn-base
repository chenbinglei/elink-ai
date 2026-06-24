import vPinyin from './pinyin2.js';
var Letters =  ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U','V', 'W', 'X', 'Y', 'Z','#'];
var reg = new RegExp('[A-Z]');
export default {
	paixu: function(ary) {		
		var e = []
		e = ary
		/*按字母排序分开*/
		var list = []
		e.forEach((value,index)=>{
			try{
				if(value.name == null){
					value.name = value.code;
				}
				var pinyin=vPinyin.chineseToPinYin(value.name)[0];
				if(!reg.test(pinyin)){
					pinyin="#";
				}
				Letters.forEach((value2, index2) => {
					if (value2 == pinyin) {
						var children = {
							letter: value2,
							value: {
								initial: value2,
								name: value.name,
								brandPath:value.brandPath,
								short: vPinyin.chineseToPinYin(value.name),
								code: value.code.replace(/\s*/g,""),
							}
						}
						list = list.concat(children)
					};
				});
			}catch(e){
				//TODO handle the exception
				console.log(e)
			}
		});
		/*获取所有字母字母去掉*/
		var list2 = []
		list.forEach((value, index) => {
			var map = list[index].letter;
			list2 = list2.concat(map)
		});
		/*去掉相同的字母*/
		var newArr = [];
		for (var i = 0; i < list2.length; i++) {
			// console.log(list2[i])
			if (newArr.indexOf(list2[i]) == -1) {
				newArr.push(list2[i]);
			}
		}
		/*给数组letter赋值字母*/
		var list3 = []
		newArr.forEach((value, index) => {
			var map = {
				letter: value,
				children: []
			}
			list3 = list3.concat(map)
		})
		/*得到数组*/
		list3.forEach((value, index) => {
			list.forEach((value2, index2) => {
				if (value.letter == value2.letter) {
					var map = value2.value
					value.children = value.children.concat(map)
				}
			})
		})
		return list3;
	}
}
