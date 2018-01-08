package com.dfl.toolkit.viewholder;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

/**
 * Created by felix.dai on 16/8/9.
 */
public abstract class Generator<VH extends IViewHolder> implements IGenerator<VH> {

    private static SparseArray<IViewHolder> getMap(View view) {
        SparseArray<IViewHolder> map;
        map = (SparseArray<IViewHolder>) view.getTag(R.id.view_holder_map);
        if (map == null) {
            map = new SparseArray<>();
            view.setTag(R.id.view_holder_map, map);
        }
        return map;
    }

    private static <VH> VH findHolder(View view, Class<VH> clazz) {
        SparseArray<IViewHolder> map = getMap(view);
        return (VH) map.get(clazz.hashCode());
    }

    public static void setHolder(View view, IViewHolder viewHolder) {
        SparseArray<IViewHolder> map = getMap(view);
        map.put(viewHolder.getClass().hashCode(), viewHolder);
    }

    private Class<VH> holderClass;
    private int layoutId;

    public Generator(Class<VH> holderClass, int layoutId) {
        this.holderClass = holderClass;
        this.layoutId = layoutId;
    }

    public Class<VH> getHolderClass() {
        return holderClass;
    }

    public String getHolderName() {
        return holderClass.getName();
    }

    @Override
    public VH generate(View view) {
        VH viewHolder = findHolder(view, holderClass);
        if (viewHolder == null) {
            viewHolder = create(view);
            setHolder(view, viewHolder);
        }
        return viewHolder;
    }

    @Override
    public VH generate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(layoutId, parent, false);
        VH viewHolder = create(view);
        setHolder(view, viewHolder);
        return viewHolder;
    }

    protected abstract VH create(View view);

    public static class RFL<VH extends IViewHolder> extends Generator<VH> {

        public RFL(Class<VH> holderClass, int layoutId) {
            super(holderClass, layoutId);
        }

        @Override
        protected VH create(View view) {
            Class<VH> holderClass = getHolderClass();
            try {
                Constructor<VH> constructor = holderClass.getDeclaredConstructor(View.class);
                constructor.setAccessible(true);
                return constructor.newInstance(view);
            } catch (Exception e) {
                throw new RuntimeException("View Holder init fail: " + holderClass.getName(), e);
            }
        }
    }
}
