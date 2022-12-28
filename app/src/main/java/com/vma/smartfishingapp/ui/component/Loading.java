package com.vma.smartfishingapp.ui.component;

import android.app.ProgressDialog;
import android.content.Context;

public class Loading {
    private static ProgressDialog g_progressDialog;

    public static void showLoading(Context p_cContext, String msg)  {
        cancelLoading();
        g_progressDialog = new ProgressDialog(p_cContext);
        g_progressDialog.setTitle(null);
        g_progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        g_progressDialog.setIndeterminate(true);
        g_progressDialog.setMessage(msg);
        g_progressDialog.setCancelable(false);
        if (g_progressDialog != null){
            g_progressDialog.show();
        }
    }

    public static void cancelLoading()
    {
        if (g_progressDialog != null)
        {
            g_progressDialog.cancel();
            g_progressDialog = null;
        }
    }
}
