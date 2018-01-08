package com.dfl.toolkit.viewholder;

import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;

public interface IDialogHolder extends OnCancelListener, OnDismissListener, OnShowListener {
    void show();
    void dismiss();
}
