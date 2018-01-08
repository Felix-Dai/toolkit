package com.dfl.toolkit.viewholder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dfl.toolkit.viewholder.IGenerator;
import com.dfl.toolkit.viewholder.IViewHolder;
import com.dfl.toolkit.viewholder.ItemBean;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by felix.dai on 16/8/9.
 */
public abstract class HolderAdapter<T, VH extends IViewHolder> extends CommonAdapter<T> {

    private Context context;
    private LayoutInflater inflater;

    private List<IGenerator<?>> generatorList = new LinkedList<>();

    public HolderAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IGenerator<? extends VH> generator = getGenerator(getItemViewType(position));
        VH viewHolder = convertView == null ?
                generator.generate(inflater, parent) :
                generator.generate(convertView);
        adapt(viewHolder, getItem(position), position);
        return viewHolder.getView();
    }

    @Override
    public void notifyDataSetChanged() {
        updateItemViewType();
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        updateItemViewType();
        super.notifyDataSetInvalidated();
    }

    private void updateItemViewType() {
        generatorList.clear();
        for (int i = 0, c = getCount(); i < c; i++) {
            IGenerator<?> generator = getGenerator(i);
            if (generatorList.indexOf(generator) < 0) {
                generatorList.add(generator);
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return generatorList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return generatorList.indexOf(getGenerator(position));
    }

    protected abstract IGenerator<? extends VH> getGenerator(int position);

    protected abstract void adapt(VH viewHolder, T data, int position);

    public abstract static class Simple<T, VH extends IViewHolder> extends HolderAdapter<T, VH> {

        private IGenerator<VH> generator;

        public Simple(Context context, IGenerator<VH> generator) {
            super(context);
            this.generator = generator;
        }

        @Override
        protected IGenerator<VH> getGenerator(int position) {
            return generator;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }
    }

    public static class Bean extends HolderAdapter<ItemBean<?>, IViewHolder> {

        public Bean(Context context) {
            super(context);
        }

        @Override
        protected IGenerator<? extends IViewHolder> getGenerator(int position) {
            return getItem(position).getGenerator();
        }

        @Override
        protected void adapt(IViewHolder viewHolder, ItemBean<?> data, int position) {
            showOn(viewHolder, data, position);
        }

        private static <VH extends IViewHolder> void showOn(IViewHolder viewHolder, ItemBean<VH> data, int position) {
            data.showOn(data.getGenerator().generate(viewHolder.getView()), position);
        }
    }
}
