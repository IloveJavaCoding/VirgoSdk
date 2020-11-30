package com.nepalese.virgosdk.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nepalese on 2020/11/18 12:04
 * @usage 正则匹配，筛选数据
 */
public class MatchUtil {

    //输入手机号码格式是否正确
    public static boolean matchPhoneNumber(String mobile) {
        if (mobile != null && !mobile.equals("")) {
            Pattern p = Pattern.compile("^1[34578]\\d{9}$");
            Matcher m = p.matcher(mobile);
            return m.matches();
        } else {
            return false;
        }
    }

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

    public static boolean matcherQQ(String qq) {
        Pattern p = Pattern.compile("[1-9]\\d{5,}");//5位以上
        Matcher m = p.matcher(qq);
        return m.matches();
    }

    //必须包含大小写字母和数字的组合且开头为大写，不能使用特殊字符，长度大于6
    public static boolean matchPassword(String password){
        Pattern p = Pattern.compile("^([A-Z])+([a-z])*(\\d)*.{6,}$");
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
