package org.zhangyan;

public class Utils {

    public static boolean isValidStr(String str) {
        if (null == str || str.isEmpty()) {
            return false;
        }
        return true;
    }

    public static void validStr(String str) {
        if (!isValidStr(str)) {
            throw new RuntimeException("string is not valid!");
        }
    }
}
