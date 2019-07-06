package com.module.profilelib.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.module.profilelib.R;

import java.io.File;

public class ProjectUtils
{
    private static AlertDialog dialog;

    public static void showDialog(Context context, String title, String msg,
                                  DialogInterface.OnClickListener OK,
                                  DialogInterface.OnClickListener cancel, boolean isCancelable)
    {
        AlertDialog dialog = null;

        if (title == null)
            title = context.getResources().getString(R.string.app_name);

        if (OK == null)
            OK = new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                    hideDialog();
                }
            };

        if (cancel == null)
            cancel = new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                    hideDialog();
                }
            };

        if (dialog == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton("OK", OK);
            builder.setNegativeButton("Cancel", cancel);
            dialog = builder.create();
            dialog.setCancelable(isCancelable);
        }

        try
        {
            dialog.show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Static method to hide the dialog if visible
     */
    public static void hideDialog()
    {
        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
            dialog.cancel();
            dialog = null;
        }
    }


    public static boolean hasMultiplePermissionInManifest(Activity activity, int requestCode, String[] permissionName)
    {
        for (String s:permissionName)
        {
            if (ContextCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED)
            {
                // Some permissions are not granted, ask the user.
                ActivityCompat.requestPermissions(activity, permissionName, requestCode);
                return false;
            }
        }

        return true;
    }

    public static boolean hasPermissionInManifest(Activity activity, int requestCode, String permissionName)
    {
        if (ContextCompat.checkSelfPermission(activity, permissionName)
                != PackageManager.PERMISSION_GRANTED)
        {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{permissionName}, requestCode);
        }
        else
        {
            return true;
        }
        return false;
    }
}