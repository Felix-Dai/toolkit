package com.dfl.toolkit.viewholder;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;


public abstract class DialogHolder extends ViewHolder implements IDialogHolder {

    private Dialog dialog;

    protected DialogHolder(View view) {
        super(view);
    }

    @Override
    public void onShow(DialogInterface dialog) {}

    @Override
    public void onCancel(DialogInterface dialog) {}

    @Override
    public void onDismiss(DialogInterface dialog) {
        this.dialog = null;
        if (getView().getParent() != null) {
            ((ViewGroup) getView().getParent()).removeView(getView());
        }
    }

    @Override
    public void show() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = onCreateDialog(getContext());
        dialog.setOnCancelListener(this);
        dialog.setOnDismissListener(this);
        dialog.setOnShowListener(this);
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    protected abstract Dialog onCreateDialog(Context context);
}
