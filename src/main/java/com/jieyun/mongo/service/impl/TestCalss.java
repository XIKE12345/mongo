package com.jieyun.mongo.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @CalssName TestCalss
 * @Desc TODO
 * @Author huiKe
 * @email <link>754873891@qq.com </link>
 * @CreateDate 2020/3/6
 * @Version 1.0
 **/
public class TestCalss {
    private static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }


    public static void main(String[] args) throws ParseException{
         String stamp = "1583476688748";
        String s = TestCalss.stampToDate(stamp);
        System.out.println(s);
        System.out.println("===============");
        String s1 = TestCalss.dateToStamp("2020.03.01");
        System.out.println(s1);

    }
}
