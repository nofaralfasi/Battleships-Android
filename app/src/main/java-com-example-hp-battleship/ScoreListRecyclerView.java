package com.example.hp.battleship;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreListRecyclerView extends RecyclerView.Adapter<ScoreListRecyclerView.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(ScoreRecord item);
    }

    private ArrayList<ScoreRecord> mRecordList;
    private final OnItemClickListener listener;


    public ScoreListRecyclerView(ArrayList<ScoreRecord> recordList, OnItemClickListener listener) {
        mRecordList = recordList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = mRecordList.get(position);
        holder.mName.setText(mRecordList.get(position).getName());
        holder.mScore.setText(String.valueOf(mRecordList.get(position).getScore()));

        holder.bind(mRecordList.get(position),listener);
    }

    @Override
    public int getItemCount() {
        if (mRecordList==null)
            return 0;
        return mRecordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public final View mView;
        public final TextView mName;
        public final TextView mScore;
        public ScoreRecord mItem;

        public ViewHolder(View view ) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.score_name_id);
            mScore = (TextView) view.findViewById(R.id.score_id);
        }

        public void bind(final ScoreRecord item , final OnItemClickListener listener){

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
