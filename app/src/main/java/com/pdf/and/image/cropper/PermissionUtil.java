package com.pdf.and.image.cropper;

import android.content.Context;
import android.content.SharedPreferences;


public class PermissionUtil {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PermissionUtil(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.permission_preferences), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void updatePermissionPreferences(String permission)
    {
        if ("storage".equals(permission)) {
            editor.putBoolean(context.getString(R.string.permission_storage), true);
            editor.commit();
        }
    }
    public boolean checkPermissionPreference(String permission)
    {
        boolean isShown=false;
        if ("storage".equals(permission)) {
            isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_storage), false);
        }
        return isShown;
    }
    public void updateUserPreferences()
    {
        editor.putBoolean(context.getString(R.string.user_first_time), true);
        editor.commit();
    }
    public boolean checkUserPreference()
    {
         return  sharedPreferences.getBoolean(context.getString(R.string.user_first_time),false);
    }

}