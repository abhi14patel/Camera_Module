package com.module.profilelib.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharedPrefrence
{
    public static SharedPreferences myPrefs;
    public static SharedPreferences.Editor prefsEditor;
    public static SharedPrefrence myObj;

    public void setIntValue(String Tag, int value)
    {
        prefsEditor.putInt(Tag, value);
        prefsEditor.apply();
    }

    public int getIntValue(String Tag)
    {
        return myPrefs.getInt(Tag, 0);

    }

    public void setValue(String Tag, String token)
    {
        prefsEditor.putString(Tag, token);
        prefsEditor.commit();
    }

    public String getValue(String Tag)
    {
        return myPrefs.getString(Tag, "default");
    }

    public static SharedPrefrence getInstance(Context ctx)
    {
        if (myObj == null)
        {
            myObj = new SharedPrefrence();
            myPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            prefsEditor = myPrefs.edit();
        }
        return myObj;
    }

    public void clearPreferences()
    {
        prefsEditor.clear();
        prefsEditor.commit();
    }
}
