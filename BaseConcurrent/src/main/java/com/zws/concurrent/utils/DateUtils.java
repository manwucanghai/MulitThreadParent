package com.zws.concurrent.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhengws
 * @date 2019-12-04 14:51
 */
public class DateUtils {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");

    public static String getNow() {
        return sdf.format(new Date());
    }
}
