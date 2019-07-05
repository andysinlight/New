package com.qennnsad.aknkaksd.hello;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author wWX407408  at 2017/9/15  18:13
 * @des SharedPreferences 工具类
 */
public class SpUtil {

    public static final String CONFIG = "mysetting";
    private static SharedPreferences msp;
    private static Context context;

    public static void init(Context c) {
        context = c;
    }

    public static SharedPreferences getPreferences() {
        return getPreferences(CONFIG);
    }

    public static SharedPreferences getPreferences(String name) {
        if (msp == null) {
            msp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        return msp;
    }

    /**
     * 保存布尔值到sp中
     *
     * @param key
     * @param value
     * @return
     */
    public static void putBoolean(String key, boolean value) {
        SharedPreferences sp = getPreferences(CONFIG);
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * @param key
     * @param defvalue
     * @return
     */
    public static boolean getBoolean(String key, boolean defvalue) {
        SharedPreferences sp = getPreferences(CONFIG);
        return sp.getBoolean(key, defvalue);
    }

    /**
     * 保存字符串到sp中
     *
     * @param key
     * @param value
     * @return
     */
    public static void putString(String key, String value) {
        SharedPreferences sp = getPreferences(CONFIG);
        sp.edit().putString(key, value).apply();
    }

    /**
     * 获取sp中的字符串
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return getString(key, "");
    }

    /**
     * 获取sp中的字符串
     *
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(String key, String defValue) {
        SharedPreferences sp = getPreferences(CONFIG);
        return sp.getString(key, defValue);
    }

    public static void putInt(String key, int value) {
        SharedPreferences sp = getPreferences(CONFIG);
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defValue) {
        SharedPreferences sp = getPreferences(CONFIG);
        return sp.getInt(key, defValue);
    }

}
