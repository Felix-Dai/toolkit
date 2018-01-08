package com.dfl.toolkit.viewholder.adapter;

import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix.dai on 16/8/9.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    @NonNull
    private List<T> data = new ArrayList<>();

    @MainThread
    public void setData(List<? extends T> data) {
        assertMainThread();
        this.data.clear();
        if (data != null) {
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }

    @MainThread
    public void addData(List<? extends T> data) {
        assertMainThread();
        if (data != null) {
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @MainThread
    public List<T> getDataCopy() {
        return new ArrayList<>(data);
    }

    @NonNull
    @MainThread
    protected List<T> getData() {
        assertMainThread();
        return data;
    }

    @Override
    @MainThread
    public int getCount() {
        assertMainThread();
        return data.size();
    }

    @Override
    @MainThread
    public T getItem(int position) {
        assertMainThread();
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void assertMainThread() {
        assert Looper.getMainLooper() == Looper.myLooper();
    }
}
