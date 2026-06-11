package com.sunmax.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.zip.CRC32;

@Slf4j
public class SunMaxUtil {

    /**
     * 根据起始时间和结束时间 计算时区
     */
    public static List<Integer> getTimeFrameNum(long startT, long endT) {
        List<Integer> re = new ArrayList<>();
        long startMoment = 0;//启动时刻的秒
        long endMoment = 0;//结束时刻的秒
        long zeroTime = getZeroTime();
        if (startT < zeroTime) {//跨天了
            startMoment = startT - (zeroTime - (3600 * 24));//起始时间跨天
            endMoment = endT - (zeroTime - (3600 * 24));
        } else {
            startMoment = startT - zeroTime;
            endMoment = endT - zeroTime;
        }
        long timeFrame = endMoment - startMoment; //充电时间
        int index = (int) startMoment / 3600;//一个小时是一个时段
        re.add(index);
        while (timeFrame / 3600 > 0) {
            index++;
            if (index == 24) { //跨天的时候  重新从0开始
                index = 0;
            }
            re.add(index);
            timeFrame = timeFrame - 3600;
        }
        return re;
    }

    /**
     * 获取当前时间 零点零时零分零秒
     */
    public static long getZeroTime() {
//        long current = System.currentTimeMillis();//当前时间毫秒数
//        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
//        return zero / 1000 + 8 * 3600;//转换成秒 加8小时
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toEpochSecond(ZoneOffset.of("+8")) + 8 * 3600; //转换成秒 加8小时
    }

    /**
     * 将Hex String转换为Byte数组
     *
     * @param hexString the hex string
     * @return the byte [ ]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (StringUtil.isEmpty(hexString)) {
            return null;
        }
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index > hexString.length() - 1) {
                return byteArray;
            }
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }

    /**
     * 将Hex String转换为Byte数组
     *
     * @param hexString the hex string
     * @return the byte [ ] 长度
     */
    public static byte[] hexStringToBytes(String hexString, Integer length) {
        final byte[] byteArray = new byte[length];
        int hexlen = 2*length;
        int strLen = hexString.length();
        if (StringUtil.isEmpty(hexString)) {
            return byteArray;
        }
        if(strLen < hexlen){//长度不够 后面补零
            for(int i = 0; i < hexlen-strLen; i++){
                hexString+="0";
            }
        }else if(strLen > hexlen){//长度多了 截断
            hexString = hexString.substring(0,hexlen);
        }
        hexString = hexString.toLowerCase();
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index > hexString.length() - 1) {
                return byteArray;
            }
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }

    /**
     * byte[]转char[]
     *
     * @param bytes
     * @return
     */
    public static char[] getChars(byte[] bytes) {
        Charset cs = StandardCharsets.UTF_8;
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    /**
     * 获得校验码
     * crc32校验
     */
    public static long getCrc32(byte[] bytes) {
        long crc = 0;
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        crc = crc32.getValue();
        return crc;
    }

    /**
     * 获得校验码
     * crc32校验
     */
    public static byte[] getCrc32(byte[] bytes, int len) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return intToByte((int) crc32.getValue(), len);
    }


    /**
     * 解析byte消息代码  低字节在前 高字节在后
     *
     * @param l 低字节
     * @param h 高字节
     * @return re 返回整形数
     */
    public static Integer parseMsgToInt(byte l, byte h) {
        int re = l & 0xFF;
        re |= ((h << 8) & 0xFF00);
        return re;
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param str    16进制格式的字符串
     * @param maxLen 允许设置最大长度
     *               若比最大允许的字符短，最后一个字节以‘\0’结束
     * @return 转换后的字节数组
     **/
    public static byte[] stringToByteArray(String str, Integer maxLen) {
        final byte[] byteArray = new byte[maxLen];
        if (StringUtil.isEmpty(str)) {
            return byteArray;
        }
        //throw new IllegalArgumentException("this str must not be empty");

        if (str.length() < maxLen) {
            //str = str + "\0"; //添加结束符
            char[] chars = str.toCharArray();
            for (int i = 0; i < str.length(); i++) {
                byteArray[i] = (byte) chars[i];
            }
        } else {
            char[] chars = str.toCharArray();
            for (int i = 0; i < maxLen; i++) {
                byteArray[i] = (byte) chars[i];
            }
        }
        return byteArray;
    }

    /**
     * 整型转化为byte数组
     *
     * @param data   整形数
     * @param length 指定字节数 1-4
     * @return
     */
    public static byte[] intToByte(int data, int length) {
        byte[] abyte = new byte[length];
        for (int i = 0; i < length; i++) {
            abyte[i] = (byte) (((0xff << (8 * i)) & data) >> (i * 8));
        }
        return abyte;
    }

    /**
     * byte数组转化为整型
     * 低位在前
     *
     * @param bytes
     * @return
     */
    public static int byteToInt(byte[] bytes) {
        if (bytes.length == 0) {
            return 0;
        } else if (bytes.length == 1) {
            int re = bytes[0] & 0xFF;
            return re;
        } else if (bytes.length == 2) {
            int re = bytes[0] & 0xFF;
            re |= ((bytes[1] << 8) & 0xFF00);
            return re;
        } else if (bytes.length == 3) {
            int re = bytes[0] & 0xFF;
            re |= ((bytes[1] << 8) & 0xFF00);
            re |= ((bytes[2] << 16) & 0xFF0000);
            return re;
        } else { //大于四字节的 只取四个四个字节
            int re = bytes[0] & 0xFF;
            re |= ((bytes[1] << 8) & 0xFF00);
            re |= ((bytes[2] << 16) & 0xFF0000);
            re |= ((bytes[3] << 24) & 0xFF000000);
            return re;
        }
    }

    /**
     * byte数组转化为整型  有符号数去符号
     * 低位在前
     *
     * @param bytes
     * @return
     */
    public static int byteToUnsignedInt(byte[] bytes) {
        if (bytes.length == 0) {
            return 0;
        } else if (bytes.length == 1) {
            char d = (char) (bytes[0] & 0xFF);
            int re = Math.abs(d);
            return re;
        } else if (bytes.length == 2) {
            short d = (short) ((bytes[0] & 0xFF) | ((bytes[1] << 8) & 0xFF00));
            int re = Math.abs(d);
            return re;
        } else if (bytes.length == 3) {
            int re = bytes[0] & 0xFF;
            re |= ((bytes[1] << 8) & 0xFF00);
            re |= ((bytes[2] << 16) & 0xFF0000);
            return re;
        } else { //大于四字节的 只取四个四个字节
            int d = (bytes[0] & 0xFF) | ((bytes[1] << 8) & 0xFF00) | ((bytes[2] << 16) & 0xFF0000) | ((bytes[3] << 24) & 0xFF000000);
            int re = Math.abs(d);
            return re;
        }
    }

    /**
     * 大端模式
     * 字节数组转换成整形数据  高字节在前  低字节在后
     *
     * @param bytes
     */
    public static Integer byteToIntBig(byte[] bytes) {
        if (bytes.length == 0) {
            return null;
        } else if (bytes.length == 1) {
            int re = bytes[0] & 0xFF;
            return re;
        } else if (bytes.length == 2) {
            int re = bytes[1] & 0xFF;
            re |= ((bytes[0] << 8) & 0xFF00);
            return re;
        } else if (bytes.length == 3) {
            int re = bytes[2] & 0xFF;
            re |= ((bytes[1] << 8) & 0xFF00);
            re |= ((bytes[0] << 16) & 0xFF0000);
            return re;
        } else { //大于四字节的 只取四个四个字节
            int re = bytes[3] & 0xFF;
            re |= ((bytes[2] << 8) & 0xFF00);
            re |= ((bytes[1] << 16) & 0xFF0000);
            re |= ((bytes[0] << 24) & 0xFF000000);
            return re;
        }
    }

    public static String toHexString(byte[] byteArray, char ch) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            if ((b & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & b)).append(ch);
        }
        return hexString.toString().toLowerCase();
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray 需要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            if ((b & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & b));
        }
        return hexString.toString().toLowerCase();
    }

    /**
     * 字节转成16进制表示格式的字符串
     *
     * @param b 需要转换的字节
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(byte b) {
        final StringBuilder hexString = new StringBuilder();
        if ((b & 0xff) < 0x10)//0~F前面不零
            hexString.append("0");
        hexString.append(Integer.toHexString(0xFF & b));
        return hexString.toString().toLowerCase();
    }

    /**
     * 字节数据  时间转换成 yyyy-MM-dd HH:mm:ss
     */
    public static String toDateString(byte[] byteArray) {
        String timeStr = null;
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");
        if (byteArray.length != 8)//协议格式固定时间八个字节
            return "1992-01-01 01:59:59";
        //年
        String year = toHexString(byteArray[0]) + toHexString(byteArray[1]);
        String mounth = toHexString(byteArray[2]);
        String day = toHexString(byteArray[3]);
        String h = toHexString(byteArray[4]);
        String m = toHexString(byteArray[5]);
        String s = toHexString(byteArray[6]);
        timeStr = year + "-" + mounth + "-" + day + " " + h + ":" + m + ":" + s;
        return timeStr;
    }

    /**
     * 固定长度字节数组转换成字符串  去掉\0
     */
    public static String toString(byte[] bytes) {
        String temp = new String(bytes);
        String re = temp.replace("\0", "");
        return re;
    }

    /**
     * 截取字节数组
     *
     * @param byteArray  原字节数组
     * @param startIndex 起始节点
     * @param length     字节长度
     * @return 字节数组
     */
    public static byte[] getSubByte(byte[] byteArray, int startIndex, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(byteArray, 0 + startIndex, bytes, 0, length);
        return bytes;
    }

    /**
     * 获取当期系统时间戳  精确到秒
     */
    public static long getSysTime() {
        long current = System.currentTimeMillis();//当前时间毫秒数 utc
        return current / 1000 + 8 * 3600;

    }

    /**
     * 获取当期系统时间戳  精确到秒
     */
    public static long getUnixTime() {
        long current = System.currentTimeMillis();//当前时间毫秒数 utc
        return current / 1000 ;

    }

    /**
     * 时间日期转时间戳秒 +8个小时
     */
    public static long dateTime8ToTimeStamp(String dateTime) {
        return DateUtil.strToLocalDateTime(dateTime).toInstant(ZoneOffset.of("+0")).toEpochMilli() / 1000;
    }

    /**
     * 时间日期转时间戳秒
     */
    public static long dateTime0ToTimeStamp(String dateTime) {
        return DateUtil.strToLocalDateTime(dateTime).toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000;
    }

    /**
     * UTC时间s 转换成时间字符串 yyyy-mm-dd hh:mm:ss
     */
    public static String timeStamp0Date(long seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(seconds * 1000));
    }

    /**
     * UTC时间s 转换成时间字符串 yyyy-mm-dd hh:mm:ss
     * 减8个小时
     */
    public static String timeStamp8Date(long seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        seconds = seconds - 8 * 3600;
        return sdf.format(new Date(seconds * 1000));
    }

    /**
     * 获取起始时间时区 计算时区
     */
    public static Integer getTimeFrameStart(long startT) {
        long startMoment = 0;//启动时刻的秒
        long zeroTime = getZeroTime();
        if (startT < zeroTime) {//跨天了
            startMoment = startT - (zeroTime - (3600 * 24) * ((zeroTime - startT) / (3600 * 24) + 1));//起始时间跨天
        } else {
            startMoment = startT - zeroTime;
        }
        int index = (int) startMoment / 1800;//半个小时是一个时段
        return index;
    }

    /**
     * 获取系统当前时间
     */
    public static Date getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = null;
        try {
            currentTime = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return currentTime;
    }

    /**
     * 生成随机字节数组
     *
     * @param length 长度
     * @return
     */
    //得到数组内容从0到log-1的随机数组
    public static byte[] getRandomByteArray(int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            int random = (int) (100 * Math.random());
            result[i] = (byte) random;
        }
        return result;
    }

    private static String toHexUtil(int n) {
        String rt = "";
        switch (n) {
            case 10:
                rt += "A";
                break;
            case 11:
                rt += "B";
                break;
            case 12:
                rt += "C";
                break;
            case 13:
                rt += "D";
                break;
            case 14:
                rt += "E";
                break;
            case 15:
                rt += "F";
                break;
            default:
                rt += n;
        }
        return rt;
    }

    public static String toHex(int n) {
        StringBuilder sb = new StringBuilder();
        if (n / 16 == 0) {
            return toHexUtil(n);
        } else {
            String t = toHex(n / 16);
            int nn = n % 16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    /**
     * 字符串转化为Ascii码
     *
     * @param str
     * @return
     */
    public static String stringToAscii(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        for (byte b : bs) sb.append(toHex(b));
        return sb.toString();
    }

    public static Map<String, Object> createMessageRecordMap(byte[] msg, String direction, Integer cmd) {
        Map<String, Object> map = new HashMap<>();
        String str = SunMaxUtil.toHexString(msg, ' ');
        map.put("content", str);
        map.put("type", direction);
        map.put("cmd", cmd);
        return map;
    }

    /**
     * 将秒转换成时分秒格式字符串 hh:mm:ss
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0) {
            return "00:00:00";
        } else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    /**
     * 将时间分转换成时分格式 hh:mm:ss
     */

    public static String minToTime(int time) {
        String timeStr;
        int min;
        int hour;
        if (time < 0) {
            return "00:00";
        } else {
            hour = time / 60;
            if (hour > 99) {
                return "99:59";
            } else {
                min = time % 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(min);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 时间转换成字符串
     */
    public static String getStringDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * @param @param  i
     * @param @return
     * @return byte[]
     * @throws
     * @Title: intToByteArray
     * @Description: int 转换成 byte数组
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * @param @param  date
     * @param @return
     * @return byte[]
     * @throws
     * @Title: date2HByte
     * @Description:CP56Time2a转换成 时间
     */
    public static Date acwByte2Hdate(byte[] dataByte) {
        int year = ((dataByte[1] << 8) & 0xff00) + (dataByte[0] & 0xff);
        int month = dataByte[2] & 0x1F;
        int day = dataByte[3] & 0x1F;
        int hour = dataByte[4] & 0x1F;
        int minute = dataByte[5] & 0x3F;
        int second = dataByte[6] & 0x3F;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }

    public static LocalDateTime convertToCP56Time2a(byte[] bytes) {
        int milliseconds1 = bytes[0] < 0 ? 256 + bytes[0] : bytes[0];
        int milliseconds2 = bytes[1] < 0 ? 256 + bytes[1] : bytes[1];
        int milliseconds = milliseconds1 + milliseconds2 * 256;
        // 位于 0011 1111
        int minutes = bytes[2] & 0x3f;
        // 位于 0001 1111
        int hours = bytes[3] & 0x1f;
        // 位于 0000 1111
        int days = bytes[4] & 0x1f;
        // 位于 0000 1111
        int months = bytes[5] & 0x0f;
        // 位于 0111 1111
        int years = bytes[6] & 0x7f;
        final Calendar aTime = Calendar.getInstance();
        aTime.set(Calendar.MILLISECOND, milliseconds);
        aTime.set(Calendar.MINUTE, minutes);
        aTime.set(Calendar.HOUR_OF_DAY, hours);
        aTime.set(Calendar.DAY_OF_MONTH, days);
        aTime.set(Calendar.MONTH, months - 1);
        aTime.set(Calendar.YEAR, years + 2000);
        return aTime.getTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * @param @param  date
     * @param @return
     * @return byte[]
     * @throws
     * @Title: date2HByte
     * @Description: 日期转换成 CP56Time2a
     */
    public static byte[] date2Hbyte(Date date) {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 毫秒需要转换成两个字节其中 低位在前高位在后
        // 先转换成short
        int millisecond = calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);

        // 默认的高位在前
        byte[] millisecondByte = intToByteArray(millisecond);
        bOutput.write(millisecondByte[3]);
        bOutput.write(millisecondByte[2]);

        // 分钟 只占6个比特位 需要把前两位置为零
        bOutput.write((byte) calendar.get(Calendar.MINUTE));
        // 小时需要把前三位置零
        bOutput.write((byte) calendar.get(Calendar.HOUR_OF_DAY));
        // 星期日的时候 week 是0
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == Calendar.SUNDAY) {
            week = 7;
        } else {
            week--;
        }
        // 前三个字节是 星期 因此需要将星期向左移5位  后五个字节是日期  需要将两个数字相加 相加之前需要先将前三位置零
        bOutput.write((byte) (week << 5) + (calendar.get(Calendar.DAY_OF_MONTH)));
        // 前四字节置零
        bOutput.write((byte) ((byte) calendar.get(Calendar.MONTH) + 1));
        bOutput.write((byte) (calendar.get(Calendar.YEAR) - 2000));
        return bOutput.toByteArray();
    }


    /**
     * @param @param  date
     * @param @return
     * @return byte[]
     * @throws
     * @Title: date2HByte
     * @Description:CP56Time2a转换成 时间
     */
    public static Date byte2Hdate(byte[] dataByte) {
        int year = (dataByte[6] & 0x7F) + 2000;
        int month = dataByte[5] & 0x0F;
        int day = dataByte[4] & 0x1F;
        int hour = dataByte[3] & 0x1F;
        int minute = dataByte[2] & 0x3F;
        int second = dataByte[1] > 0 ? dataByte[1] : (dataByte[1] & 0xff);
        int millisecond = dataByte[0] > 0 ? dataByte[0] : (dataByte[0] & 0xff);
        millisecond = (second << 8) + millisecond;
        second = millisecond / 1000;
        millisecond = millisecond % 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    /**
     * 将source中与target中类型和名称相同的属性值赋值给对应的entity的属性，并返回target
     *
     * @param source          源对象
     * @param target          ⽬标对象
     * @param ignoreNullField 是否忽略空值
     */
    public static void copyProperties(Object source, Object target, Boolean ignoreNullField) {
        List<Map<String, Object>> sourceFields = getFieldInfo(source);
        if (StringUtil.isEmpty(sourceFields)) {
            return;
        }
        for (Map sourceFieldMap : sourceFields) {
            try {
                Field f = target.getClass().getDeclaredField(sourceFieldMap.get("name").toString());
                //源对象属性值为空或属性类型不⼀致则返回继续下⼀条
                if (ignoreNullField && StringUtil.isEmpty(sourceFieldMap.get("value")) ||
                        !sourceFieldMap.get("type").equals(f.getType().toString())) {
                    continue;
                }
                f.setAccessible(true);
                f.set(target, sourceFieldMap.get("value"));
            } catch (Exception ex) {
//                log.error("获取字段属性报错", ex);
                //查看其⽗类属性
                try {
                    Field superField = target.getClass().getSuperclass()
                            .getDeclaredField(sourceFieldMap.get("name").toString());
                    superField.setAccessible(true);
                    superField.set(target, sourceFieldMap.get("value"));
                } catch (Exception e1) {
//                    log.error("CustomBeanUtil类属性复制错误", e1);
                }
            }
        }
    }
    /**
     * 根据属性名获取属性值
     */
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("根据属性名获取属性值报错", e);
            return null;
        }
    }

    /**
     * 获取属性类型(type)，属性名(name)，属性值(value)的map组成的list
     */
    private static List<Map<String, Object>> getFieldInfo(Object o) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (StringUtil.isEmpty(o)) {
            return null;
        }
        List<Field> fields = new ArrayList<>(Arrays.asList(o.getClass().getDeclaredFields()));        //如果存在⽗类，获取⽗类的属性值，类型，名称并添加到⼀起
        Class sc = o.getClass().getSuperclass();
        if (sc != null) {
            fields.addAll(Arrays.asList(sc.getDeclaredFields()));
        }
        for (Field field : fields) {
            Map<String, Object> infoMap = new HashMap<>();
            infoMap.put("type", field.getType().toString());
            infoMap.put("name", field.getName());
            infoMap.put("value", getFieldValueByName(field.getName(), o));
            list.add(infoMap);
        }
        return list;
    }

    /**
     * MD5 32位小写加密
     */
    public static byte[] encryptMd532(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            //默认小写，在tostring后加toUpperCase()即为大写加密
            encryptStr = hexValue.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return (stringToByteArray(encryptStr,32)) ;
    }

    /**
     *@brief  字节转换成科大智能时间
     *@author xt
     *@date 2023/1/4 15:40
     *@param
     *@return
     */
    public static LocalDateTime  byte2Kdzndate(byte[] dataByte) {
        int year = ((dataByte[5] & 0xF0)>>4)*10 + (dataByte[5] & 0x0F) + 2000;
        int month = ((dataByte[4] & 0x10)>>4)*10 + (dataByte[4] & 0x0F);
        int day = ((dataByte[3] & 0xF0)>>4)*10 + (dataByte[3] & 0x0F);
        int hour = ((dataByte[2] & 0xF0)>>4)*10 + (dataByte[2] & 0x0F);
        int minute = ((dataByte[1] & 0xF0)>>4)*10 + (dataByte[1] & 0x0F);
        int second = ((dataByte[0] & 0xF0)>>4)*10 + (dataByte[0] & 0x0F);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        Date date = calendar.getTime();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    /**
     *@brief 本地日期转换成字节
     *@author xt
     *@date 2023/1/4 18:14
     *@param
     *@return
     */
    public static byte[]  kdzndate2byte(LocalDateTime dateTime) {
        byte[] bytes = new byte[6];
        int year = dateTime.getYear()-2000;
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        int week = dateTime.getDayOfWeek().getValue();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();

        bytes[0] = (byte)((((second/10)<<4)&0xF0)+((second%10)&0x0F));
        bytes[1] = (byte)((((minute/10)<<4)&0xF0)+((minute%10)&0x0F));
        bytes[2] = (byte)((((hour/10)<<4)&0xF0)+((hour%10)&0x0F));
        bytes[3] = (byte)((((day/10)<<4)&0xF0)+((day%10)&0x0F));
        bytes[4] = (byte)(((week<<5)&0xE0)+(((month/10)<<4)&0x10)+((month%10)&0x0F));
        bytes[5] = (byte)((((year/10)<<4)&0xF0)+((year%10)&0x0F));
        return bytes;
    }

}
