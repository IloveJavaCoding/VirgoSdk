package com.nepalese.virgosdk.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nepalese on 2020/11/18 12:04
 * @usage 正则匹配，筛选数据
 */
public class RegexUtil {

    /**
     *  输入手机号码格式是否正确
     */
    public static boolean matchPhoneNumber(String mobile) {
        if (mobile != null && !mobile.equals("")) {
            Pattern p = Pattern.compile("^1[34578]\\d{9}$");
            Matcher m = p.matcher(mobile);
            return m.matches();
        } else {
            return false;
        }
    }

    /**
     * 提取文本内所有url
     * @param content
     * @return
     */
    public static List<String> matchNumsUrl(String content) {
        String url_regex = "(((http|ftp|https)://)|[[A-Za-z0-9_]\\-_]+(\\.[[A-Za-z0-9_]\\-_]+))+([[A-Za-z0-9_]\\-.,@?^=%&amp;:/~+#]*[[A-Za-z0-9_]\\-@?^=%&amp;/~+#])?";
        String num_regex = "((\\+86)?(086)?(-)?(\\d){5,12})|((\\d{3,4}-)(\\d{3,4}-)?(\\d{4,8}))|((\\d){5,12})";
        Pattern pattern = Pattern.compile(num_regex + "|" + url_regex);
        Matcher matcher = pattern.matcher(content);
        List<String> match = new ArrayList<>();

        while(matcher.find()) {
            match.add(matcher.group());
        }

        return match;
    }

    /**
     * 匹配QQ号码是否合法
     * @param qq
     * @return
     */
    public static boolean matcherQQ(String qq) {
        Pattern p = Pattern.compile("[1-9]\\d{5,}");//5位以上
        Matcher m = p.matcher(qq);
        return m.matches();
    }

    /**
     * 自定义密码限制：
     * 1. 必须包含大小写字母和数字的组合
     * 2. 开头为大写
     * 3. 不能使用特殊字符
     * 4. 长度大于6
     * @param password
     * @return
     */
    public static boolean matchPassword(String password){
        Pattern p = Pattern.compile("^([A-Z])+([a-z])*(\\d)*.{6,}$");
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * IP地址的规则是： (1~255).(0~255).(0~255).(0~255)
     * 1、只包含3个点号；
     * 2、第一个段的数据为1~255，其它段的数据为0~255；
     * 3、除了数字和.号不能出现其它的字符
     */
    public static boolean isLegalIP(String ip){
        if (ip == null) {
            return false;
        }

        if (CountPoint(ip) != 3) {
            return false;
        }

        char[] arr = ip.toCharArray();
        int num = 0;

        if (arr[0] == '0') {
            return false;
        }

        for (char c : arr) {
            if (Character.isDigit(c)) {
                num = num * 10 + c - '0';
            } else if (c != '.' || num > 255) {
                return false;
            } else {
                num = 0;
            }
        }
        //最后一个点分十进制需要额外小心
        return num <= 255;
    }

    //计算点号个数
    private static int CountPoint(String ip){
        int count = 0;
        for (int i=0; i<ip.length(); i++){
            if (ip.charAt(i) == '.'){
                count++;
            }
        }
        return count;
    }
}