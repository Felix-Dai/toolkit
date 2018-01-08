package com.dfl.toolkit.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by felix.dai on 16/8/9.
 */
public interface IGenerator<VH extends IViewHolder> {
    VH generate(View view);
    VH generate(LayoutInflater inflater, ViewGroup parent);
}
