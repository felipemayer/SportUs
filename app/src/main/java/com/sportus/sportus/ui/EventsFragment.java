package com.sportus.sportus.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sportus.sportus.Adapters.EventViewHolder;
import com.sportus.sportus.R;
import com.sportus.sportus.data.Event;

public class EventsFragment extends BaseFragment {
    public static final String TAG = EventsFragment.class.getSimpleName();

    public static final String KEY_EVENT_INDEX = "event_index";

    private DatabaseReference mEventsRef;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    RecyclerView mRecylerView;

    private FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.events_list_fragment, container, false);
        String message = "Carregando os Eventos...";
        showDialog(message);

        changeToolbar("Eventos");
        setHasOptionsMenu(true);

        mRecylerView = (RecyclerView) view.findViewById(R.id.listEvents);

        mEventsRef = FirebaseDatabase.getInstance().getReference("events");
        setUpFirebaseAdapter();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        return view;
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>
                (Event.class, R.layout.list_item_events, EventViewHolder.class, mEventsRef) {

            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
                viewHolder.bindEvent(model);
                closeDialog();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_event, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.createEvent) {
            if (!isLoggedIn(mUser)) {
                openDialogLogin();
            } else {
                openFragment(new CreateEventFragment());

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
