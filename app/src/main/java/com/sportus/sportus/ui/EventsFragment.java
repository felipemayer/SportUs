package com.sportus.sportus.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sportus.sportus.Adapters.EventViewHolder;
import com.sportus.sportus.R;
import com.sportus.sportus.data.Event;

public class EventsFragment extends Fragment {
    public static final String TAG = EventsFragment.class.getSimpleName();

    public static final String KEY_EVENT_INDEX = "event_index";

    private DatabaseReference mEventsRef;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    RecyclerView mRecylerView;

    public interface OnEventSelectedInterface{
        void onListEventSelected(String index, Event event);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final OnEventSelectedInterface listener = (OnEventSelectedInterface) getActivity();
        final View view = inflater.inflate(R.layout.events_list_fragment, container, false);
        mRecylerView = (RecyclerView) view.findViewById(R.id.listEvents);

        mEventsRef = FirebaseDatabase.getInstance().getReference("events");
        setUpFirebaseAdapter();

        return view;
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>
                (Event.class, R.layout.list_item_events, EventViewHolder.class, mEventsRef) {

            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
                viewHolder.bindEvent(model);
            }
        };
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecylerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }
}
