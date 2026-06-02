import {ElMessage} from "element-plus";
import store from "@/store/index.js";

//普通数据组成树形结构  调用时，字段名以字符串的形式传参，如treeData(source, 'id', 'parentId', 'children')
export function setTreeData(data = []) {
    let cloneData = JSON.parse(JSON.stringify(data)) // 对源数据深度克隆
    return cloneData.filter((father) => { //循环所有项
        let branchArr = cloneData.filter((child) => {
            return father.id === child.parentId //返回每一项的子级数组
        });
        if (branchArr.length > 0) {
            father.children = branchArr; //如果存在子级，则给父级添加一个children属性，并赋值
            father.childrenNum = branchArr.length; // 数量
        }
        return !father.parentId || father.parentId === "0" || father.parentId === "null" //返回第一层
    }) //返回树形数据
}


/*
* 根据条件删除对应的数据
*  tree：树形结构数据
*  key : 树形属性
*  value : 值
* */
export function deleteTreeArray(tree = [], value = "", key = "id") {
    return tree.filter(item => {
        return item[key] !== value
    }).map(item => {
        item = Object.assign({}, item)
        if (item.children) {
            item.children = deleteTreeArray(item.children, value, key)
        }
        return item
    })
}

// 把树形结构数据扁平化
export function treeToArray(tree) {
    let res = []
    for (const item of tree) {
        const {children, childrenList, ...i} = item;
        if (children && children.length || childrenList && childrenList.length ) {
            res = res.concat(treeToArray(children || childrenList))
        }
        res.push(i)
    }
    return res
}

/*
* data  扁平化站点数据
*  pushAlllist :  是否向列表中注册顶级全部选项
* isDisabledAll :  是否全部禁选
* label: 对应label展示字段
* */

// 列表排列
export function getDataListFun(data = [], pushAlllist = false, isDisabledAll = false, label = "") {
    let dataArray = [];
    let allList = [{id: "allList", label: "全部", parentId: 0, number: data.length }];

    data.forEach(element => {
        if (JSON.stringify(dataArray).indexOf(element.id) === -1) {
            dataArray.push({
                number: 1,
                id: element.id,
                label: element[label],
                disabled: isDisabledAll,
                parentId: pushAlllist ? "allList" : 0,
            });
        } else {
            // 计算各自子数量
            let findIndex = dataArray.findIndex(item => item.id === element.id);
            if (findIndex !== -1) dataArray[findIndex].number += 1;
        }
    });

    let allCarList = [...dataArray];
    if (pushAlllist && allCarList.length) allCarList = [...allCarList, ...allList];
    return setTreeData(allCarList);
}

/*
* 搜索 树形结构列表数据
* analysis: 解构对象中某个数据
*  */
export function selectTreeData(value, key, arr, analysis) {
    let newArr = [];
    arr.forEach(element => {
        // 把对象中某个值合并到主对象中
        if (analysis) element = Object.assign({}, element, element[analysis]);
        if (element[key] && element[key].indexOf(value) > -1) { // 判断条件
            newArr.push(element);
        } else {
            if (element.children && element.children.length > 0) {
                let resData = selectTreeData(value, key, element.children, analysis);
                if (resData && resData.length > 0) {
                    let obj = {...element, children: resData};
                    newArr.push(obj);
                }
            }
        }
    });
    return newArr;
}

/* 获取字符串url参数 根据 & 符 进行截取 */
export function getParameterByName(url, symbol = "&") {
    let theRequest = {};
    if (url.indexOf("?") !== -1 && url.indexOf("=") !== -1) {
        let strs = url.split('?')[1].split(symbol);
        for (let i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest
}

// 获取url路径获取文件名称
export function getUrlFileName(url){
    const url_obj = new URL(url);
    return url_obj.pathname?.split('/').pop()
}


/**
 * css 颜色 hex 格式 转 rgba
 */
export function colorHexTurnRgba(hex, transparent) {
    if (hex[0] !== '#') return hex
    let repairLength = 9 - hex.length;
    for (let i = 0; i < repairLength; i++) hex = hex + "0";
    let r = parseInt(hex.slice(1, 3), 16)
    let g = parseInt(hex.slice(3, 5), 16)
    let b = parseInt(hex.slice(5, 7), 16)
    let a = parseInt(hex.slice(7, 9), 16)
    return 'rgba(' + r + ',' + g + ',' + b + ',' + (transparent ? transparent : a) + ')'
}

/**
 * 下载Excel文件流
 * @param {string} binaryData
 * @param {string} fileName
 *
 * api中增加 参数
 * method: 'post',
 * responseType: 'blob',
 */
export function downloadExcel(binaryData, fileName) {
    const a = document.createElement("a");
    a.download = fileName;
    a.style.display = "none";
    a.href = URL.createObjectURL(new Blob([binaryData], {
        type: 'application/vnd.ms-excel;charset=UTF-8'
    }));
    document.body.appendChild(a);
    a.click();
    URL.revokeObjectURL(a.href);
    document.body.removeChild(a);
}

//根据文件地址下载文件
export function downloadFiles(fileUrl, fileName) {
    const link = document.createElement('a');
    link.style.display = 'none';
    link.href = fileUrl;
    //download 属性不起作用，是因为不是同源
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

/*
* 点击复制
* */
export function clickCopyValue(textValue = "", isMessage = true) {
    // 模拟 输入框
    let cInput = document.createElement("input");
    cInput.value = textValue;
    // cInput.style.display = "none";
    document.body.appendChild(cInput);
    // 选取文本框内容
    cInput.select();

    // 执行浏览器复制命令
    // 复制命令会将当前选中的内容复制到剪切板中（这里就是创建的input标签）
    // Input要在正常的编辑状态下原生复制方法才会生效
    document.execCommand("copy");
    // 复制成功后再将构造的标签 移除
    document.body.removeChild(cInput);
    if (isMessage) ElMessage({type: "success", showClose: true, message: "复制成功"});
}

/*
* 获取剪切板里面的内容 (需要使用https 或者 localhost)
*  使用方式：
*      const clipboard = getClipboardContent();
*      clipboard.then(clipText=>{ console.log(clipText); })
* */
export async function getClipboardContent() {
    try {
        const clipboardData = await navigator.clipboard.readText();
        return clipboardData.trim();
    } catch (e) {
        console.error("Failed to read clipboard data: ", e);
        return "";
    }
}

/*
*  根据 当前路由名称获取子级所有的页面权限
*  routeName : 路由名称   路由英文名称
*  isContainFather: 返回是否包含父级（默认不包含） 0：不包含
*  isFilterTree : 是否过滤树形数据
* */
export function getLeftTreeDataFun(routeName, isContainFather = 0, isFilterTree = 1) {
    let treeMenuData = JSON.parse(localStorage.getItem("ALL_SIDEBAR"));
    let handleMenuArray = selectTreeData(routeName, "path", treeMenuData);
    // console.log(handleMenuArray);
    let menuArrayData = getChildrenArrayFun(routeName, isContainFather, handleMenuArray);
    // console.log(menuArrayData);

    // 不过滤树形数据
    if (!isFilterTree) return menuArrayData

    let newMenuArrayData = [];
    if (menuArrayData) newMenuArrayData = filterTreeArray(menuArrayData, 1);
    return newMenuArrayData
}


// 递归查找子级
export function getChildrenArrayFun(routeName, isContainFather = 0, handleMenuArray = []) {
    let menuArrayData = [];
    handleMenuArray.forEach(item => {
        menuArrayData = item.path === routeName ? (isContainFather ? [item] : item.children) : getChildrenArrayFun(routeName, isContainFather, item.children);
    });
    return setThreeData(menuArrayData)
}

/*
*   重新设置树形结构数据的属性值
*   配合 getLeftTreeData 方法使用
* */
export function setThreeData(ArrayData) {
    if (ArrayData && ArrayData.length) {
        for (let i = 0; i < ArrayData.length; i++) {
            if (ArrayData[i].name) {
                let nameArray = ArrayData[i].name.split('/');
                ArrayData[i].id = nameArray[nameArray.length - 1];
            }
            ArrayData[i].label = ArrayData[i].meta.title;
            if (ArrayData[i].children) setThreeData(ArrayData[i].children);
        }
    }
    return ArrayData
}

/*
* 过滤掉 隐藏的路由
*  tree：树形结构数据
*  hiddenType : 1: 默认过滤掉不用显示的路由   2：返回需要显示的路由
* */
export function filterTreeArray(tree = [], hiddenType = 1) {
    if(!tree || !tree.length)return  []
    return tree.filter(item => {
        return hiddenType === 1 ? !item.hidden : item.hidden
    }).map(item => {
        item = Object.assign({}, item)
        if (item.children) {
            item.children = filterTreeArray(item.children, hiddenType)
        }
        return item
    })
}

// 处理数据回显展示null问题
export function nullToDelete(data) {
    return data && data !== "null" || data === 0 ? data : "";
}

/**
 * 去除括号前所有字符
 * */
export function getBracketPreStr(str) {
    let index = str.indexOf('('); // 括号区分中文英文，index=5--即：获取到第一个括号的索引位置
    // 获取到第一个括号前面的所有字符
    return str.substring(0, index)
}


// 页面中添加按钮是否可以操作
export function operateButtonIsClick(requestPath) {
    // console.log(requestPath);
    let permissionList = [],authority = true;
    if(store.getters.permissionList && store.getters.permissionList.length){
        permissionList = JSON.parse(JSON.stringify(store.getters.permissionList));
    }
    let findItem = permissionList.find(item => item.url === requestPath);
    if(findItem)authority = false;
    return authority
}


// 查看用户是否用户某个权限
export function queryUserAuthorityIsHaveFun (pathName = ""){
    let isAuthority = false;
    let route_list = JSON.parse(localStorage.getItem("AUTH_ROUTER"));

    if(route_list && route_list.length){
        for(let i = 0;i < route_list.length;i++){
            if(pathName && (pathName === ('/' + route_list[i].router_path))){
                isAuthority = true;
                break
            }
        }
    }

    return isAuthority
}

// js  计算两数值不精准处理
export function calcNumberFun(num1, num2, calcStr) {
    let str1, // 转换为字符串的数字
        str2,
        ws1 = 0,// ws1，ws2 用来存储传入的num的小数点后的数字的位数
        ws2 = 0,// 赋默认值，解决当整数和小数运算时倍数计算错误导致的结果误差
        bigger,// bigger和smaller用于加，减，除法找出小的那个数字，给后面补0，解决位数不对从而造成的计算错误的问题；乘法需要将结果除两个数字的倍数之和
        smaller,// 例如：加减除法中1.001 + 2.03 ，如果不给2.03进行补0，最后会变成1001+203，数字错位导致结果错误；乘法中1.12*1.1会放大为112*11，所以结果需要除以1000才会是正确的结果，112*11/1000=1.232
        zeroCount, // 需要补充0的个数
        isExistDot1, // 传入的数字是否存在小数点
        isExistDot2,
        sum,
        beishu = 1;
    // 将数字转换为字符串
    str1 = num1.toString();
    str2 = num2.toString();
    // 是否存在小数点（判断需要计算的数字是不是包含小数）
    isExistDot1 = str1.indexOf('.') !== -1;
    isExistDot2 = str2.indexOf('.') !== -1;
    // 取小数点后面的位数
    if (isExistDot1) {
        ws1 = str1.split('.')[1].length;
    }

    if (isExistDot2) {
        ws2 = str2.split('.')[1].length;
    }
    // 如ws1 和 ws2 无默认值，如果num1 或 num2 不是小数的话则 ws1 或 ws2 的值将为 undefined
    // bigger 和 smaller 的值会和预期不符
    bigger = ws1 > ws2 ? ws1 : ws2;
    smaller = ws1 < ws2 ? ws1 : ws2;

    switch (calcStr) {
        // 加减法找出小的那个数字，给后面补0，解决位数不对从而造成的计算错误的问题
        // 例如：1.001 + 2.03 ，如果不给2.03进行补0，最后会变成1001+203，数字错位导致结果错误
        case "+":
        case "-":
        case "/":
            zeroCount = bigger - smaller;
            for (let i = 0; i < zeroCount; i++) {
                if (ws1 === smaller) {
                    str1 += "0";
                } else {
                    str2 += "0";
                }
            }
            break;
        case "*":
            // 乘法需要将结果除两个数字的倍数之和
            bigger = bigger + smaller;
            break;
        default:
            return "暂不支持的计算类型，现已支持的有加法、减法、乘法、除法";
    }

    // 去除数字中的小数点
    str1 = str1.replace('.', '');
    str2 = str2.replace('.', '');

    // 计算倍数，例如：1.001小数点后有三位，则需要乘 1000 变成 1001，变成整数后精度丢失问题则不会存在
    for (let i = 0; i < bigger; i++) {
        beishu *= 10; // 等价于beishu = beishu * 10;
    }
    num1 = parseInt(str1);
    num2 = parseInt(str2);
    // 进行最终计算并除相应倍数
    switch (calcStr) {
        case "+":
            sum = (num1 + num2) / beishu;
            break;
        case "-":
            sum = (num1 - num2) / beishu;
            break;
        case "*":
            sum = (num1 * num2) / beishu;
            break;
        case "/":
            sum = num1 / num2;
            /* 除数与被除数同时放大一定倍数，不影响结果，
            所以对数字进行放大对应倍数并进行补0操作后不用另对倍数做处理 */
            break;
        default:
            return "暂不支持的计算类型，现已支持的有加法、减法、乘法、除法";
    }

    return sum;
}