package com.sportus.sportus.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sportus.sportus.Adapters.EventsFragmentAdapter;
import com.sportus.sportus.R;

public class EventsFragment extends Fragment {
    public static final String KEY_EVENT_INDEX = "event_index";

    public interface OnEventSelectedInterface{
        void onListEventSelected(int index);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        OnEventSelectedInterface listener = (OnEventSelectedInterface) getActivity();
        View view = inflater.inflate(R.layout.events_list_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listEvents);
        EventsFragmentAdapter listAdapter = new EventsFragmentAdapter(listener);
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }
}
