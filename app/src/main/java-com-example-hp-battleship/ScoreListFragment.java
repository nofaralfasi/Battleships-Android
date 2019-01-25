package com.example.hp.battleship;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ScoreListFragment extends Fragment {
    private ArrayList<ScoreRecord> mAllRecords;
    private String mTableName;
    private DataBaseHelper mHandler;

    public ScoreListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = DataBaseHelper.getInstance(getActivity().getApplicationContext());

        Bundle bundle = this.getArguments();
        mTableName = bundle.getString(ScoresListActivity.TABLE_NAME_KEY);

        synchronized (mHandler) {
            mAllRecords = mHandler.getAllRecordsFromTable(mTableName);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_list, container, false);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new ScoreListRecyclerView(mAllRecords, new ScoreListRecyclerView.OnItemClickListener() {

                @Override
                public void onItemClick(ScoreRecord item) {
                    ScoreRecord hs = new ScoreRecord(item.getId(),item.getName(),item.getScore(),item.getLatitude(),item.getLongtitude());
                    ((ScoresListActivity)getActivity()).getRecordFromClickedList(hs);
                }
            }));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
