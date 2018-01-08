package com.dfl.toolkit.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by felix.dai on 16/8/9.
 */
public class ViewHolder implements IViewHolder {

    @NonNull
    private View view;

    private Object tag;

    protected ViewHolder(@NonNull View view) {
        this.view = view;
    }

    @Override
    @NonNull
    public View getView() {
        return view;
    }

    @NonNull
    public Context getContext() {
        return view.getContext();
    }

    public <T extends View> T findViewById(int id) {
        return (T) view.findViewById(id);
    }

    public void setVisibility(int visibility) {
        view.setVisibility(visibility);
    }

    public void setOnClickListener(View.OnClickListener l) {
        view.setOnClickListener(l);
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }
}
