package com.jvpars.codetip.utils;


import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;


@Slf4j
public abstract class MyArgUtils {

    public static String  convertEpochToPersian(long epoch){
        return new java.sql.Date(epoch).toString();

    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static long  nowEpoch(){
        return System.currentTimeMillis();
    }


    public static String safeUrlString(String str){
        if(str==null) return "undef";
        if(str.length()==0) return  "undef";
        return str.replaceAll("[^a-zA-Z0-9\\.\\-\\_\\&\\'\\)\\(\\{\\}\\[\\]]", "").replaceAll("/","").replaceAll("\\.","").replaceAll("\\&","");
    }

    public static String toPrettyURL(String string) {
        return Normalizer.normalize(string.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

    }

    public static String toPrettyURLWithPersianScape(String string) {
        return Normalizer.normalize(string.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-");
    }


    public static BigDecimal toCurrency(String str) {
        str= str.replaceAll("[^\\d.]", "");
        return new BigDecimal(str);

    }

    public static String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }



}

