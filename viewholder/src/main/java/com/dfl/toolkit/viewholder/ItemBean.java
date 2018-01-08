package com.dfl.toolkit.viewholder;

/**
 * Created by felix.dai on 2017/12/6.
 */

public interface ItemBean<VH extends IViewHolder> {
    IGenerator<VH> getGenerator();
    void showOn(VH viewHolder, int position);
}
