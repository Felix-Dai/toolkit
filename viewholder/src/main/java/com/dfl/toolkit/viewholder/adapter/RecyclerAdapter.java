package com.dfl.toolkit.viewholder.adapter;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dfl.toolkit.viewholder.IGenerator;
import com.dfl.toolkit.viewholder.IViewHolder;
import com.dfl.toolkit.viewholder.ItemBean;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public interface Diff<T> {
        boolean areItemsTheSame(T oldItem, T newItem, int oldItemPosition, int newItemPosition);
        boolean areContentsTheSame(T oldItem, T newItem, int oldItemPosition, int newItemPosition);
    }

    private static final Diff<Object> DEF_DIFF = new Diff<Object>() {
        @Override
        public boolean areItemsTheSame(Object oldItem, Object newItem, int oldItemPosition, int newItemPosition) {
            return oldItem == null ? newItem == null : oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(Object oldItem, Object newItem, int oldItemPosition, int newItemPosition) {
            return false;
        }
    };

    private static class InternalCallback<T> extends DiffUtil.Callback {
        private List<? extends T> oldItems;
        private List<? extends T> newItems;
        private Diff<? super T> callback;

        public InternalCallback(Diff<? super T> callback, List<? extends T> oldItems, List<? extends T> newItems) {
            this.oldItems = oldItems;
            this.newItems = newItems;
            this.callback = callback;
        }

        @Override
        public int getOldListSize() {
            return oldItems == null ? 0 : oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return newItems == null ? 0 : newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            T oldItem = oldItems.get(oldItemPosition);
            T newItem = newItems.get(newItemPosition);
            return callback.areItemsTheSame(oldItem, newItem, oldItemPosition, newItemPosition);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            T oldItem = oldItems.get(oldItemPosition);
            T newItem = newItems.get(newItemPosition);
            return callback.areContentsTheSame(oldItem, newItem, oldItemPosition, newItemPosition);
        }
    }

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
    public void setDataWithDiff(List<? extends T> data) {
        setDataWithDiff(data, DEF_DIFF);
    }

    @MainThread
    public void setDataWithDiff(List<? extends T> data, Diff<? super T> callback) {
        assertMainThread();
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new InternalCallback<>(callback, this.data, data));
        this.data.clear();
        if (data != null) {
            this.data.addAll(data);
        }
        diffResult.dispatchUpdatesTo(this);
    }

    @MainThread
    public void addData(List<? extends T> data) {
        insert(getItemCount(), data);
    }

    @NonNull
    @MainThread
    protected List<T> getData() {
        assertMainThread();
        return data;
    }

    @NonNull
    @MainThread
    public List<T> getDataCopy() {
        return new ArrayList<>(data);
    }

    @MainThread
    public void change(int position, T data) {
        assertMainThread();
        if (data == null) {
            return;
        }
        if (position < 0 || position >= this.data.size()) {
            return;
        }
        this.data.set(position, data);
        notifyItemChanged(position);
    }

    @MainThread
    public void insert(int position, T data) {
        assertMainThread();
        if (data == null) {
            return;
        }
        if (position < 0 || position > this.data.size()) {
            return;
        }
        this.data.add(position, data);
        notifyItemInserted(position);
    }

    @MainThread
    public void insert(int position, List<? extends T> data) {
        assertMainThread();
        if (data == null || data.size() == 0) {
            return;
        }
        if (position < 0 || position > this.data.size()) {
            return;
        }
        this.data.addAll(position, data);
        notifyItemRangeInserted(position, data.size());
    }

    @MainThread
    public void remove(int position) {
        assertMainThread();
        if (position < 0 || position >= this.data.size()) {
            return;
        }
        this.data.remove(position);
        notifyItemRemoved(position);
    }

    @MainThread
    public void remove(int position, int count) {
        assertMainThread();
        if (count <= 0) {
            return;
        }
        if (position < 0 || position >= this.data.size()) {
            return;
        }
        count = position + count > this.data.size() ? this.data.size() - position : count;
        for (int i = position, c = position + count < data.size() ? position + count : data.size(); i < c; i++) {
            this.data.remove(position);
        }
        notifyItemRangeRemoved(position, count);
    }

    @MainThread
    public void remove(T data) {
        assertMainThread();
        if (data == null) {
            return;
        }
        int position = this.data.indexOf(data);
        if (position < 0) {
            return;
        }
        this.data.remove(position);
        notifyItemRemoved(position);
    }

    @MainThread
    public void remove(List<? extends T> data) {
        assertMainThread();
        if (data == null || data.size() == 0) {
            return;
        }
        for (T d : data) {
            remove(d);
        }
    }

    @MainThread
    public void move(T data, int position) {
        move(this.data.indexOf(data), position);
    }

    @MainThread
    public void move(int srcPos, int dstPos) {
        assertMainThread();
        if (srcPos == dstPos) {
            return;
        }
        if (srcPos < 0 || srcPos >= data.size()) {
            return;
        }
        if (dstPos < 0 || dstPos >= data.size()) {
            return;
        }
        data.add(dstPos, data.remove(srcPos));
        notifyItemMoved(srcPos, dstPos);
    }

    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    abstract public static class Gen<T, VH extends RecyclerView.ViewHolder> extends RecyclerAdapter<T, VH> {

        private List<IGenerator<? extends VH>> generatorList = new ArrayList<>();

        private Context context;
        private LayoutInflater inflater;

        public Gen(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        public Context getContext() {
            return context;
        }

        public LayoutInflater getInflater() {
            return inflater;
        }

        public abstract IGenerator<? extends VH> getGenerator(int position);

        @Override
        public int getItemViewType(int position) {
            IGenerator<? extends VH> generator = getGenerator(position);
            int index = generatorList.indexOf(generator);
            if (index >= 0) {
                return index;
            }
            generatorList.add(generator);
            return generatorList.size() - 1;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return generatorList.get(viewType).generate(getInflater(), parent);
        }

        protected abstract void adapt(VH viewHolder, T data, int position);

        @Override
        public void onBindViewHolder(VH holder, int position) {
            adapt(holder, getItem(position), position);
        }

        abstract public static class Simple<T, VH extends RecyclerView.ViewHolder> extends Gen<T, VH> {

            private IGenerator<? extends VH> generator;

            public Simple(Context context, IGenerator<? extends VH> generator) {
                super(context);
                this.generator = generator;
                super.generatorList.add(generator);
            }

            @Override
            public IGenerator<? extends VH> getGenerator(int position) {
                return generator;
            }

            @Override
            public final int getItemViewType(int position) {
                return 0;
            }
        }

        public static class Bean extends Gen<ItemBean<? extends RecyclerView.ViewHolder>, RecyclerView.ViewHolder> {

            public Bean(Context context) {
                super(context);
            }

            @Override
            public IGenerator<? extends RecyclerView.ViewHolder> getGenerator(int position) {
                return getItem(position).getGenerator();
            }

            @Override
            protected void adapt(RecyclerView.ViewHolder viewHolder, ItemBean<? extends RecyclerView.ViewHolder> data, int position) {
                showOn(viewHolder, data, position);
            }

            private static <VH extends IViewHolder> void showOn(RecyclerView.ViewHolder viewHolder, ItemBean<VH> data, int position) {
                data.showOn(data.getGenerator().generate(viewHolder.itemView), position);
            }
        }
    }

    public static class WrapHolder<VH extends IViewHolder> extends RecyclerView.ViewHolder {

        private VH wrapped;

        public WrapHolder(VH viewHolder) {
            super(viewHolder.getView());
            wrapped = viewHolder;
        }

        public VH getWrapped() {
            return wrapped;
        }
    }

    abstract public static class Wrap<T, VH extends IViewHolder> extends RecyclerAdapter<T, WrapHolder<VH>> {

        private List<IGenerator<? extends VH>> generatorList = new ArrayList<>();

        private Context context;
        private LayoutInflater inflater;

        public Wrap(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        public Context getContext() {
            return context;
        }

        public LayoutInflater getInflater() {
            return inflater;
        }

        public abstract IGenerator<? extends VH> getGenerator(int position);

        @Override
        public int getItemViewType(int position) {
            IGenerator<? extends VH> generator = getGenerator(position);
            int index = generatorList.indexOf(generator);
            if (index >= 0) {
                return index;
            }
            generatorList.add(generator);
            return generatorList.size() - 1;
        }

        @Override
        public WrapHolder<VH> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WrapHolder<>(generatorList.get(viewType).generate(getInflater(), parent));
        }

        protected abstract void adapt(VH viewHolder, T data, int position);

        @Override
        public void onBindViewHolder(WrapHolder<VH> holder, int position) {
            adapt(holder.getWrapped(), getItem(position), position);
        }

        abstract public static class Simple<T, VH extends IViewHolder> extends Wrap<T, VH> {

            private IGenerator<? extends VH> generator;

            public Simple(Context context, IGenerator<? extends VH> generator) {
                super(context);
                this.generator = generator;
                super.generatorList.add(generator);
            }

            @Override
            public IGenerator<? extends VH> getGenerator(int position) {
                return generator;
            }

            @Override
            public final int getItemViewType(int position) {
                return 0;
            }
        }

        public static class Bean extends Wrap<ItemBean<?>, IViewHolder> {

            public Bean(Context context) {
                super(context);
            }

            @Override
            protected void adapt(IViewHolder viewHolder, ItemBean<?> data, int position) {
                showOn(viewHolder, data, position);
            }

            @Override
            public IGenerator<? extends IViewHolder> getGenerator(int position) {
                return getItem(position).getGenerator();
            }

            private static <VH extends IViewHolder> void showOn(IViewHolder holder, ItemBean<VH> data, int position) {
                data.showOn(data.getGenerator().generate(holder.getView()), position);
            }
        }
    }

    private void assertMainThread() {
        assert Looper.getMainLooper() == Looper.myLooper();
    }
}
