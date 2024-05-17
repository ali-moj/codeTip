package com.jvpars.codetip.utils;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.Tuple;
import java.lang.reflect.Field;
import java.math.BigInteger;

@Slf4j
public abstract class TupleConverter {

    public static Long getLong(Tuple tuple, String key) {
        BigInteger value = (BigInteger) tuple.get(key);
        return value != null ? value.longValue() : null;
    }

    public static Integer getInteger(Tuple tuple, String key) {
        return (Integer) tuple.get(key);
    }

    public static Boolean getBoolean(Tuple tuple, String key) {
        return (Boolean) tuple.get(key);
    }

    public static String getString(Tuple tuple, String key) {
        return (String) tuple.get(key);
    }



}
