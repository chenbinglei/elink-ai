package com.sunmax.common.util;
import lombok.extern.slf4j.Slf4j;

import com.sunmax.common.constant.FunctionqConstant;
import com.sunmax.common.util.oss.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class ConversionExpressionUtil {
    // 表达式字符合法性校验正则模式，静态常量化可以降低每次使用都要编译地消耗
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("[A-Za-z0-9\\.+-/*()= ]+");

    /**
     *@brief 一维数组混合运算表达式运算
     *@author xt
     *@date 2022/12/20 16:57
     *@param
     *@return
     */
    public static String conversionExpression(String expression,Integer dimension) {
        // 非空校验
        if (null == expression || "".equals(expression.trim())) {
            throw new IllegalArgumentException("表达式不能为空！");
        }
        // 表达式字符合法性校验
        Matcher matcher = EXPRESSION_PATTERN.matcher(expression);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("表达式含有非法字符！");
        }
        Stack<String> optStack = new Stack<>(); // 运算符栈
        Stack<String> varStack = new Stack<>(); // 变量栈 表达式中运算符的左值和右值为变量
        Stack<String> funcStack = new Stack<>(); //函数栈  表达式中函数 SUM MAX MIN
        StringBuilder curVarBuilder = new StringBuilder(16); // 当前正在读取中的数值字符追加器
        // 逐个读取字符，并根据运算符判断参与何种计算
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c != ' ') { // 空白字符直接丢弃掉
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    curVarBuilder.append(c); // 持续读取一个数值的各个字符
                } else {
                    if (curVarBuilder.length() > 0) {
                        // 如果追加器有值，说明读取完成一个函数或者变量
                        String temp = curVarBuilder.toString();
                        if(isFunc(temp)){
                            funcStack.push(temp);
                            //截取函数内的表达式
                            String subStr = expression.substring(i);
                            String subExpression = getFuncExpression(subStr);
                            Integer endIndex = getFuncExpressionEndIndex(subStr);
                            i=i+endIndex;
                            String cStr = conversionExpression(subExpression,dimension);
                            if(cStr==null){
                                return null;
                            }else { //入栈前加上函数名
                                cStr = temp+'('+cStr+')';
                                varStack.push(cStr);
                            }
                            if(i < expression.length()-1){
                                i=i+1;
                            }else if(i == expression.length()-1){
                                break;
                            }
                        }else{
                            varStack.push(temp);
                        }
                        curVarBuilder.delete(0, curVarBuilder.length());
                    }
                    c = expression.charAt(i);//更新c值
                    String curOpt = String.valueOf(c);
                    if (optStack.empty()) {
                        // 运算符栈栈顶为空则直接入栈
                        optStack.push(curOpt);
                    }else {
                        if (curOpt.equals("(")) {
                            // 当前运算符为左括号，直接入运算符栈
                            optStack.push(curOpt);
                        } else if (curOpt.equals(")")) {
                            // 当前运算符为右括号，触发括号内的字表达式进行计算
                            directCalc(optStack, varStack, true,dimension);
                        } else {
                            // 当前运算符为加减乘除之一，要与栈顶运算符比较，判断是否要进行一次二元计算
                            optStack.push(curOpt);
                        }
                    }
                }
            }
        }
        //表达式不是函数结尾，有单独的变量  不符合计算规则
        if(curVarBuilder.length() >0){
            // 如果追加器有值，说明读取完成一个函数或者变量
            String temp = curVarBuilder.toString();
            if(!isFunc(temp)){
                return null;
            }
        }
        if(!optStack.isEmpty()){
            directCalc(optStack, varStack, false,dimension);
        }
        return varStack.pop();
    }

    /**
     * 遇到右括号和等号执行的连续计算操作（递归计算）
     * @param optStack 运算符栈
     * @param varStack 数值栈
     * @param isBracket true表示为括号类型计算
     */
    public static void directCalc(Stack<String> optStack, Stack<String> varStack,
                                  boolean isBracket,Integer dimension) {

        String opt =  ""; // 当前参与计算运算符
        String str2 = ""; // 当前参与计算数值2
        String str1 = ""; // 当前参与计算数值1
        if(!varStack.isEmpty()){
            str2 = varStack.pop();
        }
        if(!varStack.isEmpty()){
            str1 = varStack.pop();
        }
        if (!"(".equals(optStack.peek())) {
            // 括号类型则遇左括号停止计算，同时将左括号从栈中移除
            opt = optStack.pop();
        }
        String str = expressionCalc(opt, str1, str2,dimension);

        // 计算结果当做操作数入栈
        varStack.push(str);

        if (isBracket) {
            if ("(".equals(optStack.peek())) {
                // 括号类型则遇左括号停止计算，同时将左括号从栈中移除
                optStack.pop();
            } else {
                directCalc(optStack, varStack, isBracket,dimension);
            }
        } else {
            if (!optStack.empty()) {
                // 等号类型只要栈中还有运算符就继续计算
                directCalc(optStack, varStack, isBracket,dimension);
            }
        }
    }

    public static String expressionCalc(String opt, String str1, String str2,Integer num) {
        StringBuilder reStr = new StringBuilder();
        Integer int1 = getExperessionDimension(str1);
        Integer int2 = getExperessionDimension(str2);
        if(int1 == 0 && int2 == 0){
            reStr = new StringBuilder(str1 + opt + str2);
        }else {
            for(int i = 0; i < num; i++){
                if(i==num-1){
                    reStr.append(convertStr(int1, i, str1)).append(opt).append(convertStr(int2, i, str2));
                }else {
                    reStr.append(convertStr(int1, i, str1)).append(opt).append(convertStr(int2, i, str2)).append(',');
                }
            }
        }
        return reStr.toString();
    }
    /**
     *@brief 根据变量类型转换字符串
     *@author xt
     *@date 2023/1/3 10:16
     *@param
     *@return
     */
    private static String convertStr(Integer type, Integer index, String srcStr){
        String reStr = "";
        if(srcStr.equals("")){
            return "";
        }
        switch (type){
            case 0:
                reStr = srcStr;
                break;
            case 1:
                reStr = srcStr+'['+index+']';
                break;
            case 2:
                List<String> strList = splitStr(srcStr);
                reStr = strList.get(index);
                break;
            default:
                break;
        }
        return reStr;

    }

    /**
     *@brief 分割计算后的字符串
     *@author xt
     *@date 2023/1/3 13:53
     *@param
     *@return
     */
    private static List<String> splitStr(String str){
        List<String> restr = new ArrayList<>();
        Stack<String> leftBracketStack = new Stack<>(); // 左括号栈
        StringBuilder curBuilder = new StringBuilder(1024); // 当前正在读取中的字符追加器
        // 逐个读取字符
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            curBuilder.append(c);
            if (c == ')') { // 如果是右括号,左括号出栈一个
                leftBracketStack.pop();
            }else if(c == '('){
                leftBracketStack.push(String.valueOf(c));
            }else if(c == ','){
                if(leftBracketStack.isEmpty()){
                    curBuilder.deleteCharAt(curBuilder.length()-1);//去掉末尾的逗号
                    restr.add(curBuilder.toString());
                    curBuilder.delete(0, curBuilder.length());
                }
            }
        }
        if(curBuilder.length()>0){
            restr.add(curBuilder.toString());
        }
        return restr;
    }

    private static Integer getExperessionDimension(String str){
        String temp   =  str.toLowerCase(Locale.ROOT);
        if(temp.length()<5){//不满足函数表达式长度
            return 1;//一维数组
        }else {
            char rightBracket = temp.charAt(temp.length()-1);
            String sub = temp.substring(0,5);
            if((sub.contains("sum") || sub.contains("max") || sub.contains("min")) && rightBracket == ')') {
                return 0;//函数表达式 结果是一个值
            }else if(temp.contains("[") && temp.contains("]")){
                return 2;//非函数的一维函数展开项
            }
        }
        return 1;

    }

    /**
     *@brief 截取函数括号内的表达式
     *@author xt
     *@date 2022/12/20 17:56
     *@param
     *@return
     */
    private static String getFuncExpression(String str) {
        // 非空校验
        if (null == str || "".equals(str.trim())) {
            throw new IllegalArgumentException("表达式不能为空！");
        }
        // 表达式字符合法性校验
        Matcher matcher = EXPRESSION_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("表达式含有非法字符！");
        }
        Stack<String> leftBracketStack = new Stack<>(); // 运算符栈
        StringBuilder curBuilder = new StringBuilder(2048); // 当前正在读取中的字符追加器
        // 逐个读取字符
        for (int i = 0; i < str.length(); i++) {
            if(i == 0 && str.charAt(i) != '('){//函数表达式必须以左括号开始
                return null;
            }
            char c = str.charAt(i);
            curBuilder.append(c);
            if (c == ')') { // 如果是右括号,左括号出栈一个
                leftBracketStack.pop();
            }else if(c == '('){
                leftBracketStack.push(String.valueOf(c));
            }
            if(leftBracketStack.empty()){
                return curBuilder.toString();
            }
        }
        return curBuilder.toString();
    }

    /**
     *@brief 获取表达式中函数体结束的位置
     *@author xt
     *@date 2022/12/21 9:46
     *@param
     *@return
     */
    private static Integer getFuncExpressionEndIndex(String str) {
        // 非空校验
        if (null == str || "".equals(str.trim())) {
            throw new IllegalArgumentException("表达式不能为空！");
        }
        // 表达式字符合法性校验
        Matcher matcher = EXPRESSION_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("表达式含有非法字符！");
        }
        Stack<String> leftBracketStack = new Stack<>(); // 运算符栈
        // 逐个读取字符
        int i = 0;
        for (; i < str.length(); i++) {
            if(i == 0 && str.charAt(i) != '('){//函数表达式必须以左括号开始
                return 0;
            }
            char c = str.charAt(i);
            if (c == ')') { // 如果是右括号,左括号出栈一个
                leftBracketStack.pop();
            }else if(c == '('){
                leftBracketStack.push(String.valueOf(c));
            }
            if(leftBracketStack.empty()){
                return i;
            }
        }
        return i;
    }

    /**
     *@brief 判断是否是函数标标识符
     *@author xt
     *@date 2022/12/20 17:24
     *@param
     *@return
     */
    private static boolean isFunc(String str){
        str=str.toLowerCase(Locale.ROOT);
        switch (str){
            case "sum":
            case "max":
            case "min":
                return true;
            default:
                return false;
        }
    }

    /**
     * 获取基础模型函数表达式里面的数据值
     */
    public static List<String> getBasicExpressionParamList(String expression) {
        List<String> resultList = Lists.newArrayList();
        String prefix = FileUtil.separator;
        StringBuilder result = new StringBuilder();
        int j = 0;
        if (StringUtil.isNotEmpty(expression)) {
            for (int i = expression.length(); i > 0; i--) {
                char charAt = expression.charAt(i - 1);
                if (charAt != ' ') {
                    if (charAt == ']') {
                        j = i;
                        prefix = String.valueOf(charAt);
                    }
                    if (charAt == '(' || charAt == ',' || charAt == '+' || charAt == '-' || charAt == '*' || charAt == '/') {
                        if (StringUtil.isNotEmpty(prefix) && prefix.contains("]")) {
                            result.append(expression, i, j);
                            resultList.add(result.toString());
                            prefix = FileUtil.separator;
                            result = new StringBuilder();
                            j = 0;
                        }
                    }
                }
            }
        }
        return resultList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 获取基础模型函数表达式里面的数据值
     */
/*    public static List<String> getInstanceExpressionParamList(String expression) {
        List<String> resultList = Lists.newArrayList();
        expression = expression.replace(" ","");
        String prefix = FileUtil.separator;
        //上一位索引
        int upperIndex = 0;

        if (StringUtil.isNotEmpty(expression)) {
            for (int i = expression.length(); i > 0; i--) {
                char charAt = expression.charAt(i - 1);
                if (charAt != ' ') {
                    if (prefix.equals(FileUtil.separator) && charAt != ')') {
                        upperIndex = i;
                        prefix = String.valueOf(charAt);
                    } else if (charAt == ')') {
                        upperIndex = i;
                        if ("+".equals(prefix) || "-".equals(prefix) || "*".equals(prefix) || "/".equals(prefix)) {
                            upperIndex = upperIndex - 1;
                        }
                    } else if (charAt == '(') {
                        String substring = expression.substring(i, upperIndex);
                        if (substring.charAt(0) == '(') {
                            substring = substring.substring(1);
                        }
                        resultList.add(substring);
                        upperIndex = 0;
                        prefix = FileUtil.separator;
                    }
                    if (charAt == '+' || charAt == '-' || charAt == '*' || charAt == '/') {
                        String substring = expression.substring(i, upperIndex);
                        if (StringUtil.isNotEmpty(substring)) {
                            if (substring.charAt(0) == '(') {
                                substring = substring.substring(1);
                            }
                            resultList.add(substring);
                            upperIndex = 0;
                            prefix = FileUtil.separator;
                        } else {
                            upperIndex = upperIndex - 1;
                        }
                    }
                }
            }
            //如果标记的上一位索引等于公式长度，说明是统计函数，直接添加
            if (upperIndex == expression.length()) {
                resultList.add(expression);
            } else {
                //公式从前向后到第一位运算符部分上面截取不到，所以单独处理
                int k = 0;
                for (int j = 0; j < expression.length(); j++) {
                    char charAt = expression.charAt(j);
                    if (charAt == '+' || charAt == '-' || charAt == '*' || charAt == '/' || charAt == '(') {
                        k = j;
                        break;
                    }
                }
                String substring = expression.substring(0, k);
                if (StringUtil.isNotEmpty(substring)) {
                    if (substring.charAt(0) == '(') {
                        substring = substring.substring(1);
                    }
                    resultList.add(substring);
                }
            }
        }
        return resultList.stream().distinct().collect(Collectors.toList());
    }*/
    public static void main(String[] args) {
        log.info("{}", getInstanceExpressionParamList("gnd@2c99698b9b076b54019b0807d7840183@active_power+gnd@2c99698b9b076b54019b080879d30184@active_power+gnd@2c99698b9b076b54019b080915d40185@active_power+gnd@2c99698b9b076b54019b080a97510186@active_power+gnd@2c99698b9b076b54019b08056b230181@active_power+gnd@2c99698b9b076b54019b0807400a0182@active_power-gnd@2c99698b9b2025a0019b26631935014c@pcs_activepower-gnd@2c99698b9b2025a0019b26639ebc014d@pcs_activepower-gnd@2c99698b9b2025a0019b266408aa014e@pcs_activepower-gnd@2c99698b9b2025a0019b2664779d014f@pcs_activepower+gnd@2c99698b9b076b54019b080f00cf018f@total_active_power"));
    }

    /**
     * 获取基础模型函数表达式里面的数据值
     */
    public static List<String> getInstanceExpressionParamList(String expression) {
        List<String> resultList = Lists.newArrayList();
        expression = expression.replace(" ","");
        //碰到“)”记录下来
        String prefix = FileUtil.separator;
        //碰到“)”并且prefix有值时，j加一，用来截取
        int hook = 0;
        //上一位索引
        int upperIndex = 0;
        if (StringUtil.isNotEmpty(expression)) {
            for (int i = expression.length(); i > 0; i--) {
                char charAt = expression.charAt(i - 1);
                if (charAt != ' ') {
                    if (charAt == ')' && prefix.equals(FileUtil.separator)) {
                        upperIndex = i;
                        prefix = ")";
                    } else if (charAt == ')' && StringUtil.isNotEmpty(prefix)){
                        hook++;
                    }
                    if (charAt == '+' || charAt == '-' || charAt == '*' || charAt == '/') {
                        //存放截取之后要保存的函数
                        String substring;
                        //存放截取之前要replace的函数
                        String substringBefore;
                        if (upperIndex > 0) {
                            substring = expression.substring(i, upperIndex - hook);
                            substringBefore = substring;
                            if (substring.charAt(0) == '(') {
                                substring = substring.substring(1);
                            } else if (substring.charAt(substring.length() - 1) == ')' && getFunConstant().stream().noneMatch(substring::contains)) {
                                substring = substring.substring(0, substring.length() - 1);
                            }
                        } else {
                            substring = expression.substring(i);
                            substringBefore = substring;
                            if (substring.charAt(0) == '(') {
                                substring = substring.substring(1);
                            }
                        }
                        String string = charAt + substringBefore;
                        //判断prefix是否有值，有的话拼接上
                        if (StringUtil.isNotEmpty(prefix) && hook > 0) {
                            string = string + prefix;
                            prefix = FileUtil.separator;
                        }
                        expression = expression.replace(string, "");
                        resultList.add(substring);
                        upperIndex = 0;
                        hook = 0;
                    }
                }
            }
            //如果标记的上一位索引等于公式长度，说明是统计函数，直接添加
            if (upperIndex == expression.length()) {
                resultList.add(expression);
            } else {
                //公式从前向后到第一位运算符部分上面截取不到，所以单独处理
                int k = 0;
                for (int j = 0; j < expression.length(); j++) {
                    char charAt = expression.charAt(j);
                    if (charAt == '+' || charAt == '-' || charAt == '*' || charAt == '/') {
                        k = j;
                        break;
                    }
                }
                String substring = expression.substring(0, k);
                if (StringUtil.isNotEmpty(substring) && substring.charAt(0) == '(') {
                    substring = substring.substring(1);
                } else if (expression.charAt(0) == '('){
                    substring = expression.substring(1);
                } else {
                    substring = expression;
                }
                resultList.add(substring);
            }
        }
        if (CollectionUtils.isNotEmpty(resultList)) {
            resultList = resultList.stream().map(s -> {
                if (s.contains(FileUtil.LEFT_BRACKET) && s.contains(FileUtil.RIGHT_BRACKET)) {
                    return s;
                } else if (s.contains(FileUtil.LEFT_BRACKET)) {
                    s = s.replace(FileUtil.LEFT_BRACKET, FileUtil.separator);
                } else if (s.contains(FileUtil.RIGHT_BRACKET)) {
                    s = s.replace(FileUtil.RIGHT_BRACKET, FileUtil.separator);
                }
                return s;
            }).collect(Collectors.toList());
        }
        return resultList.stream().distinct().collect(Collectors.toList());
    }

    public static List<String> getFunConstant() {
        Class<FunctionqConstant> functionqConstantClass = FunctionqConstant.class;
        return Arrays.stream(functionqConstantClass.getFields())
                .filter(field -> Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                .map(Field::getName).collect(Collectors.toList());
    }

    /**
     * 获取基础模型函数表达式里面的数据值
     *//*
    public static List<String> getInstanceExpressionParamList(String expression) {
        List<String> resultList = Lists.newArrayList();
        expression = expression.replace(" ","");
        String fff = "MAX(gnd@4028820e8d0cfa34018d1537c7d7000b@power)";
        //上一位索引
        int upperIndex = 0;
        if (StringUtil.isNotEmpty(expression)) {
            for (int i = expression.length(); i > 0; i--) {
                char charAt = expression.charAt(i - 1);
                if (charAt != ' ') {
                    if (charAt == ')') {
                        upperIndex = i;
                    }
                    if (charAt == '+' || charAt == '-' || charAt == '*' || charAt == '/') {
                        String substring = FileUtil.separator;
                        if (upperIndex > 0) {
                            substring = expression.substring(i, upperIndex);
                            if (substring.charAt(0) == '(') {
                                substring = substring.substring(1);
                            }
                        } else {
                            substring = expression.substring(i);
                        }
                        resultList.add(substring);
                        upperIndex = 0;
                    }
                }
            }
            //如果标记的上一位索引等于公式长度，说明是统计函数，直接添加
            if (upperIndex == expression.length()) {
                resultList.add(expression);
            } else {
                //公式从前向后到第一位运算符部分上面截取不到，所以单独处理
                int k = 0;
                for (int j = 0; j < expression.length(); j++) {
                    char charAt = expression.charAt(j);
                    if (charAt == '+' || charAt == '-' || charAt == '*' || charAt == '/') {
                        k = j;
                        break;
                    }
                }
                String substring = expression.substring(0, k);
                if (substring.charAt(0) == '(') {
                    substring = substring.substring(1);
                }
                resultList.add(substring);
            }
        }
        return resultList.stream().distinct().collect(Collectors.toList());
    }*/

}
