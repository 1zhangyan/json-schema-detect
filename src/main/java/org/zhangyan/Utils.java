package org.zhangyan;

import java.util.Collection;

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

    public static boolean isEmpty(Collection collection) {
        if (null == collection || collection.isEmpty()) {
            return true;
        }
        return false;
    }
}
