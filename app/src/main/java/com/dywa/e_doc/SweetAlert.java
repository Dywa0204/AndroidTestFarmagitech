package com.dywa.e_doc;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetAlert {
    public void success(Context context, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(message)
            .show();
    }

    public SweetAlertDialog confirm(Context context, String title, String message, String ConfirmText, String CancelText) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
            pDialog.setTitleText(title)
            .setContentText(message)
            .setConfirmText(ConfirmText)
            .setCancelButton(CancelText, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();
                }
            })
            .show();

        return pDialog;
    }

    public void error(Context context, String title, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(message)
            .show();
    }

    public SweetAlertDialog loading(Context context, String message) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(message);
        pDialog.setCancelable(false);
        pDialog.show();

        return pDialog;
    }

}
