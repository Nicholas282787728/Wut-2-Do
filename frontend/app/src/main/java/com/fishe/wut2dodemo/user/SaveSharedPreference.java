package com.fishe.wut2dodemo.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    static final String PREF_USER_NAME= "username";
    static final String PREF_PASS_WORD= "password";
    static final String isLogIn = "false";



    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static void setPassWord(Context ctx, String password)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PASS_WORD, password);
        editor.commit();
    }

    public static String getPassWord(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_PASS_WORD, "");
    }

    public static void login(Context ctx, String state){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(isLogIn,state);
        editor.commit();

    }

    public static void logout(Context ctx, String state){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(isLogIn,state);
        editor.commit();

    }
    public static String getIsLogIn(Context ctx)
    {
        return getSharedPreferences(ctx).getString(isLogIn, "");
    }
}
