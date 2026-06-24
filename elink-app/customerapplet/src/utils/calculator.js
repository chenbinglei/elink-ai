class MyCalc {

    paramsObj = {};
    operator = [];

    constructor(paramsObj, operator = []) {
        this.paramsObj = paramsObj;
        this.operator = operator.filter(item => item !== "(" && item !== ")");// 左右括号做单独管理
    }

    // main
    do(aimStr = "") {
        aimStr = this.formatAimStr(aimStr);
        return this.clacAimStr(aimStr);
    }

    /**************************************
     * 计算携带括号的字符串
     * @param {String} aimStr
     * @returns {Number}
     *************************************/
    clacAimStr(aimStr = "") {
        aimStr = aimStr?.replace(/\s/g, "") || "";
        aimStr = "(" + aimStr + ")";
        let leftBracketIndexs = []; // 左括号的脚标

        for (let i = 0; i < aimStr.length; i++) {
            if (aimStr[i] === "(") {
                leftBracketIndexs.push(i);
                // console.log(leftBracketIndexs);
            } else if (aimStr[i] === ")") {
                let lastLBIS = leftBracketIndexs.pop();
                let baseStr = aimStr.slice(lastLBIS + 1, i);
                let res = this.clacBaseStr(baseStr);
                // console.log(aimStr, "(" + baseStr + ")", res);

                aimStr = aimStr.replace("(" + baseStr + ")", res)
                i = leftBracketIndexs[leftBracketIndexs.length - 1];
            }
        }

        return Number(aimStr);
    }

    /***********************************************************************
     * 根据 this.paramsObj ， 对传入的字符串做计算
     *  - 传入的字符串为最基本的字符串， 只含有运算符和数值、变量， 不含有括号
     * @param {String} aimStr
     ************************************************************************/
    clacBaseStr(aimStr = "") {
        aimStr = aimStr?.replace(/\s/g, "");
        let stack = [];

        // 数字压栈
        function addNum(num) {
            num = Number(num);
            if (stack.length > 0) {
                let op = stack.pop();
                if (op === "+" || op === "-") {
                    stack.push(op);
                }
                // 如果是乘除， 先与前一个数字做运算再压栈
                else if (op === "*" || op === "/") {
                    let preNum = Number(stack.pop());
                    num = op === "*" ? preNum * num : preNum / num;
                }
            }

            stack.push(num);
        }

        // 先计算乘除
        let isGetNum = true; // 当前是取数字还是运算符
        while (aimStr.length > 0) {
            if (isGetNum) {
                let t = this.getFirstValue(aimStr);
                let num = t.value;
                aimStr = t.str;
                addNum(num);
            } else {
                let t = this.getFirstOperator(aimStr);
                stack.push(t.value)
                aimStr = t.str;
            }
            isGetNum = !isGetNum;
        }

        // 再加减
        let isAdd = true;
        let result = 0;
        stack.forEach(item => {
            if (item === "+") {
                isAdd = true
            } else if (item === "-") {
                isAdd = false
            } else {
                result += isAdd ? +item : -item;
            }
        })

        return result;
    }

    /********************************
     * 获取第一个计算值 数值 | 变量
     * @param {String} aimStr
     * @return {Object}
     *    - value: 传入 aimStr 中截取的 第一个计算值 数值 | 变量(a)
     *    - str: 截取了 value & operator 之后的字符串
     ********************************/
    getFirstValue(aimStr = "") {
        aimStr = aimStr?.replace(/\s/g, "") || "";
        let result = {value: null, str: null,};

        let startIndex = 0; // 第一个非运算符字符的脚标（支持负数）
        for (let i = 0; i < aimStr.length; i++) {
            if (!this.operator.includes(aimStr[i])) {
                startIndex = i;
                break;
            }
        }

        let lastIndex = startIndex; // 当前需要截取的数值 | 变量的末尾脚标
        for (let i = startIndex; i < aimStr.length; i++) {
            if (this.operator.includes(aimStr[i])) {
                lastIndex = i;
                break;
            } else if (i === aimStr.length - 1) {
                lastIndex = aimStr.length;
                break;
            }
        }

        result.value = aimStr.slice(0, lastIndex);
        result.str = aimStr.slice(lastIndex);

        return result;
    }

    /********************************
     * 获取第一个有效运算符
     * @param {String} aimStr
     * @return {Object}
     *    - value: 传入 aimStr 中截取的 第一个有效运算符
     *    - str: 截取了 第一个有效运算符(operator) 之后的字符串
     ********************************/
    getFirstOperator(aimStr = "") {
        aimStr = aimStr?.replace(/\s/g, "") || "";
        let result = {value: "", str: "",};

        for (let i = 0; i < aimStr.length; i++) {
            if (this.operator.includes(aimStr[i])) {
                result.value = aimStr[i];
                result.str = aimStr.slice(i + 1);
                break;
            }
        }

        return result;
    }

    clac(num1, num2, operator) {
        num1 = Number(num1);
        num2 = Number(num2);

        if (operator === "+") {
            return num1 + num2;
        } else if (operator === "-") {
            return num1 - num2;
        } else if (operator === "*") {
            return num1 * num2;
        } else if (operator === "/") {
            return num1 / num2;
        } else return num1;
    }

    /************************************************
     * 依据 this.paramsObj, 替换字符串中的参数(被引号包裹的字符串)
     * @param {String} aimStr
     *******************************************/
    formatAimStr(aimStr = "") {
        aimStr = aimStr?.replace(/\s/g, "");
        let limitSymbol = `"`;

        for (let i = 0; i < aimStr.length; i++) {
            if (aimStr[i] === limitSymbol) {
                for (let j = i + 1; j < aimStr.length; j++) {
                    if (aimStr[j] === limitSymbol) {
                        let param = aimStr.slice(i, j + 1);
                        let value = this.paramsObj[param.replace(/"/g, "")];

                        if (value === undefined) {
                            throw new Error(`字符串中的参数 “${param}” 不存在于参数列表中!`)
                        } else {
                            aimStr = aimStr.replace(param, value)
                            // console.log(param, value);
                            break;
                        }

                    }
                }
            }
        }
        return aimStr;
    }

    /******************************************
     * 规则 正确性验证
     * @param {String} aimStr 目标规则
     * @param {Array}  paramArr 参数数组
     ******************************************/
    valid(aimStr = "", paramArr = []) {
        aimStr = aimStr?.replace(/\s/g, "") || "";
        let tempChar = String.fromCharCode(0x10f0); // 替换 aimStr 中参数的特殊字符
        let paramsReg = /".*?"/g;
        let validCharReg = `[0-9\\+\\-\\*\\/\\(\\)\\.${tempChar}]`;// 有效字符
        validCharReg = new RegExp(validCharReg)

        // 验证字符串的参数(两端携带引号的子字符串)是否在参数列表 paramArr 中
        let aimStrParams = aimStr.match(paramsReg) || [];
        for (let i = 0; i < aimStrParams.length; i++) {
            const item = aimStrParams[i];
            if (!paramArr.includes(item)) {
                throw new Error(`参数 ${item} 不存在于列表当中！`);
            }
        }
        aimStr = aimStr.replace(/".*?"/g, tempChar);


        // 验证左右括号的正确性
        let leftBracketIndexs = []; // 左括号的脚标

        for (let i = 0; i < aimStr.length; i++) {
            if (aimStr[i] === "(") {
                leftBracketIndexs.push(i);
            } else if (aimStr[i] === ")") {
                let lastLBIS = leftBracketIndexs.pop();
                if (lastLBIS === undefined) {
                    throw new Error(`括号数量不对称`);
                }
                let baseStr = aimStr.slice(lastLBIS + 1, i);

                if (baseStr.length === 2) {
                    throw new Error(`出现（）空括号错误`);
                }
            } else if (!validCharReg.test(aimStr[i])) {
                throw new Error(`“${aimStr[i]}” 不是有效字符！如果是变量请用 {""} 英文双引号引用。`);
            }
        }

        if (leftBracketIndexs.length !== 0) {
            throw new Error(`括号数量不对称`);
        }

        return true
    }
}

let paramsArr = [];
let operator = ["+", "-", "*", "/", "(", ")"];
let paramsObj = {"a": 100, "b": 10, "c": 5};
for (const key in paramsObj) paramsArr.push("\"" + key + "\"");
let mc = new MyCalc(paramsObj, operator);

export default mc
// 1. getFirstValue
// console.log(mc.getFirstValue("+999+666")); // value: '+999', str: '+666'
// console.log(mc.getFirstValue(`+"999"+666`)); //  value: '+"999"', str: '+666'
// console.log(mc.getFirstValue(`+++"999"+666`)); // value: '+++"999"', str: '+666'
// console.log(mc.getFirstValue(`+++9+666`)); // value: '+++9', str: '+666'
// console.log(mc.getFirstValue(`9+666`)); // value: '9', str: '+666'
// console.log(mc.getFirstValue(`9`)); // value: '9', str: ''
// console.log(mc.getFirstValue(``)); // //  value: "", str: ""


// 2. getFirstOperator
// console.log(mc.getFirstOperator("+999+666")); // value: '+', str: '999+666'
// console.log(mc.getFirstOperator("++999+666")); //  value: '+', str: '+999+666'
// console.log(mc.getFirstOperator("896+999+666")); // value: '+', str: '999+666'
// console.log(mc.getFirstOperator("")); //  value: "", str: ""


// 3. clacBaseStr
// console.log(mc.clacBaseStr("12+2+4+8")); // 26
// console.log(mc.clacBaseStr("12+2+4")); // 18
// console.log(mc.clacBaseStr("12+2")); // 14
// console.log(mc.clacBaseStr("12")); // 12
// console.log(mc.clacBaseStr("12+4-6-8")); // 2
// console.log(mc.clacBaseStr("5+5*5")); // 30
// console.log(mc.clacBaseStr("5*5")); // 25
// console.log(mc.clacBaseStr("5*5+5")); // 30
// console.log(mc.clacBaseStr("5*5+5*5")); // 50
// console.log(mc.clacBaseStr("5*5+5*5+5")); // 55
// console.log(mc.clacBaseStr("1*2*3*4*5")); // 120
// console.log(mc.clacBaseStr("1*2*3*4*5/4/3/2")); // 5


// 4. clacAimStr
// console.log(mc.clacAimStr(`((1+2)*2+(2+2)*3)+5`)); // 23
// console.log(mc.clacAimStr(``)); // 0
// console.log(mc.clacAimStr(`5`)); // 5
// console.log(mc.clacAimStr(`2+3*4/2`)); // 8
// console.log(mc.clacAimStr(`(2+3)*(4/2)`)); // 10
// console.log(mc.clacAimStr(`(((1+2*3)+(4+5*6)*2+3)/3)+2*5`)); // 36
// console.log(mc.clacAimStr("2*(2-1+49+(1-1+20+15*2)) - (150 + 2*10)")); // 30


// 5. formatAimStr
// console.log(mc.formatAimStr(`((("a" + 1) * 2 + ("b" + 1) * 2) + 1) * 2`)); // (((100+1)*2+(10+1)*2)+1)*2
// console.log(mc.formatAimStr(` 1 1`)); // 11


// 6. do (main)
// console.log(mc.do(`((("a" + 1) * 2 + ("b" + 1) * 2) + 1) * 2`)); // 450
// console.log(mc.do(`()`)); // 0
// console.log(mc.do(`"a"`)); // 100
// console.log(mc.do(`"a"*2`)); // 200
// console.log(mc.do(`100`)); // 100
// console.log(mc.do(`"a"*"b"`)); // 1000
// console.log(mc.do(`2*2*2+3`)); // 11
// console.log(mc.do(`2*2*2+3*2`)); // 14
// console.log(mc.do(`2+2+2+2*2*2+3*2*2+2+2`)); // 30
// console.log(mc.do(`100-10*1+10*100`)); // 1090


// 7. valid
// console.log(mc.valid(aimStr, [])); // 参数 "a" 不存在于列表当中！
// console.log(mc.valid(aimStr, paramsArr)); // true
// console.log(mc.valid(`"a" + 1 * 2 + "b" + 1`, paramsArr)); // true
// console.log(mc.valid(`("参)数1" + 1 * 2 + "b" + 1`, paramsArr)); // 参数 "参)数1" 不存在于列表当中！
// console.log(mc.valid(`出勤天数*10`, ['"出勤天数"', '"请假"', '"出勤金额"'])) // 出 不是有效字符！如果是变量请用 {""} 英文双引号引用。
// console.log(mc.valid(`"出勤天数"*10`, ['"出勤天数"', '"请假"', '"出勤金额"'])) // true
