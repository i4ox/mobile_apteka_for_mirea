package ru.artlkv.aptekaformirea;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.StringDef;

public class SettingsManager {
    private static final String SETTINGS_NAME = "main_preferences";
    private static SettingsManager sSharedPrefs;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private boolean bulkUpdate = false;
    @StringDef({Key.IS_FIRST_TIME, Key.IS_USER_DETAILS_PRESENT})
    public @interface Keys {}

    public static class Key {
        public static final String IS_FIRST_TIME = "is_first_time";
        public static final String IS_USER_DETAILS_PRESENT = "is_user_details_present";

        public static final String HAS_INTRO_SHOWN = "has_intro_shown";

        public static final String HAS_SHOWN_ORDER_LIST_TUTORIAL = "has_shown_order_list_tutorial";

        public static final String HAS_SHOWN_NEW_ORDER_TUTORIAL = "has_shown_new_order_tutorial";

        public static final String FCM_REG_ID = "fcm_registration_id";

    }

    private SettingsManager(Context context) {
        mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }


    public static SettingsManager getInstance(Context context) {
        if (sSharedPrefs == null) {
            sSharedPrefs = new SettingsManager(context.getApplicationContext());
        }
        return sSharedPrefs;
    }

    public static SettingsManager getInstance() {
        if (sSharedPrefs != null) {
            return sSharedPrefs;
        }

        throw new IllegalArgumentException("Should use getInstance(Context) at least once before using this method.");
    }

    public void put(String key, String val) {
        doEdit();
        mEditor.putString(key, val);
        doCommit();
    }

    public void put(String key, int val) {
        doEdit();
        mEditor.putInt(key, val);
        doCommit();
    }

    public void put(String key, boolean val) {
        doEdit();
        mEditor.putBoolean(key, val);
        doCommit();
    }

    public void put(String key, float val) {
        doEdit();
        mEditor.putFloat(key, val);
        doCommit();
    }

    public void put(String key, double val) {
        doEdit();
        mEditor.putString(key, String.valueOf(val));
        doCommit();
    }

    public void put(String key, long val) {
        doEdit();
        mEditor.putLong(key, val);
        doCommit();
    }

    public String getString(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }

    public String getString(String key) {
        return mPref.getString(key, null);
    }

    public int getInt(String key) {
        return mPref.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return mPref.getInt(key, defaultValue);
    }

    public long getLong(String key) {
        return mPref.getLong(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        return mPref.getLong(key, defaultValue);
    }

    public float getFloat(String key) {
        return mPref.getFloat(key, 0);
    }

    public float getFloat(String key, float defaultValue) {
        return mPref.getFloat(key, defaultValue);
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(mPref.getString(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPref.getBoolean(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return mPref.getBoolean(key, false);
    }

    /**
     * Remove keys from SharedPreferences.
     *
     * @param keys The name of the key(s) to be removed.
     */
    public void remove(String... keys) {
        doEdit();
        for (String key : keys) {
            mEditor.remove(key);
        }
        doCommit();
    }

    /**
     * Remove all keys from SharedPreferences.
     */
    public void clear() {
        doEdit();
        mEditor.clear();
        doCommit();
    }

    public void edit() {
        bulkUpdate = true;
        mEditor = mPref.edit();
    }

    public void commit() {
        bulkUpdate = false;
        mEditor.commit();
        mEditor = null;
    }

    private void doEdit() {
        if (!bulkUpdate && mEditor == null) {
            mEditor = mPref.edit();
        }
    }

    private void doCommit() {
        if (!bulkUpdate && mEditor != null) {
            mEditor.commit();
            mEditor = null;
        }
    }

}
