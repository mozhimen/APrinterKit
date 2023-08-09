package com.mozhimen.pidk_printer_dascom.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mozhimen.pidk_printer_dascom.R;

/*
 * Copyright (c) 2020 DASCOM All rights reserved.
 */


public class ProgressDialogUtil {
    private static AlertDialog mAlertDialog;

    public static void showProgressDialog(Context context, String msg) {
        if (mAlertDialog == null)
            mAlertDialog = new AlertDialog.Builder(context, R.style.MyDialogStyle).create();
        View loadView = LayoutInflater.from(context).
                inflate(R.layout.custiom_progress_dialog_view, null);
        mAlertDialog.setView(loadView);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setCancelable(false);

        TextView tvTip = loadView.findViewById(R.id.tvTip);
        tvTip.setText(msg);
        mAlertDialog.show();
    }

    public static void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
