package com.okuloj.nhacchill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.SongViewHolder> {

    private final List<Song> songList = new ArrayList<>();
    private OnItemClickListener listener;

    public HomeAdapter(OnItemClickListener onItemClickListener) {
        listener = onItemClickListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bindData(position, listener);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewTitleItem);
        }

        public void bindData(int position, OnItemClickListener listener) {
            textView.setText(songList.get(position).getTitle());

            itemView.setOnClickListener(v -> listener.onItemClick(position));
        }

    }

    public void updateData(List<Song> list) {
        songList.clear();
        songList.addAll(list);
        notifyItemRangeChanged(0, list.size());
    }
}
