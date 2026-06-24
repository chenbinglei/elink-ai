export function calcNumberFun(num1 = 0, num2 = 0, calcStr) {
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
export function isNumber(value) {
    return typeof value === 'number' && !isNaN(value);
}
