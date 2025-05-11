package com.example.reforyapp.Adapter;

import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.example.reforyapp.R;
import com.example.reforyapp.RoomDataBase.MyData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final List<MyData> dataList;
    private final Set<Integer> selectedIds = new HashSet<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MyData data);
    }

    public RecyclerViewAdapter(List<MyData> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvItemName, tvItemCount, tvItemTime;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.item_checkBox);
            tvItemName = itemView.findViewById(R.id.item_text_name);
            tvItemCount = itemView.findViewById(R.id.item_text_count);
            tvItemTime = itemView.findViewById(R.id.item_text_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyData item = dataList.get(position);

        holder.tvItemName.setText(item.getName());

        holder.tvItemCount.setText("X" + item.getCount());
        if(item.getCount().isEmpty()) holder.tvItemCount.setText("");

        holder.tvItemTime.setText(item.getTime());

        holder.checkBox.setChecked(selectedIds.contains(item.getId()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedIds.add(item.getId());
            } else {
                selectedIds.remove(item.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public Set<Integer> getSelectedIds() {
        return selectedIds;
    }

    public void clearSelections() {
        selectedIds.clear();
        notifyDataSetChanged();
    }
}
