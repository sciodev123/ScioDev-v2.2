package com.redbluekey.sciodev.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage  {

    private static final String AUTH_TOKEN = "AUTH_TOKEN";
    private static final String USERNAME = "USERNAME";
    private static final String AUTH_PREF = "AUTH_PREF";

    public static void saveAuthData(Activity activity, final String authToken, final String username) {
        SharedPreferences sharedPref = activity.getSharedPreferences(AUTH_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AUTH_TOKEN, authToken);
        editor.putString(USERNAME, username);
        editor.apply();
    }

    public static String[] getAuthData(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(AUTH_PREF, Context.MODE_PRIVATE);
        String token = sharedPref.getString(AUTH_TOKEN, "");
        String username = sharedPref.getString(USERNAME, "");

        return new String[]{token, username};
    }

    public static void clearData(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(AUTH_PREF, Context.MODE_PRIVATE);
        sharedPref.edit().clear().apply();
    }
}
