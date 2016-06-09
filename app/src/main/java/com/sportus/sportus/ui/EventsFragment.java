package com.sportus.sportus.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sportus.sportus.Adapters.EventViewHolder;
import com.sportus.sportus.MainActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.data.Event;

public class EventsFragment extends Fragment {
    public static final String TAG = EventsFragment.class.getSimpleName();

    public static final String KEY_EVENT_INDEX = "event_index";

    private DatabaseReference mEventsRef;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    RecyclerView mRecylerView;

    private ProgressDialog dialog;

    public interface OnEventSelectedInterface{
        void onListEventSelected(String index, Event event);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final OnEventSelectedInterface listener = (OnEventSelectedInterface) getActivity();
        final View view = inflater.inflate(R.layout.events_list_fragment, container, false);
        showDialog();

        ImageView toolbarImage  = (ImageView) (getActivity()).findViewById(R.id.logo_toolbar);
        toolbarImage.setVisibility(View.GONE);
        Button buttonLogin  = (Button) (getActivity()).findViewById(R.id.buttonLogin);
        buttonLogin.setVisibility(View.GONE);
        Button buttonLogout  = (Button) (getActivity()).findViewById(R.id.buttonLogout);
        buttonLogout.setVisibility(View.GONE);
        Toolbar toolbar  = (Toolbar) (getActivity()).findViewById(R.id.tool_bar);
        toolbar.setTitle("Eventos");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setHasOptionsMenu(true);

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
                dialog.dismiss();
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

    public void showDialog() {
        dialog = ProgressDialog.show(getActivity(), null, "Carregando os eventos...", false, true);
        dialog.setCancelable(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_event, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.createEvent) {
            MainActivity activity = ((MainActivity) getActivity());
            activity.openFragment(new CreateEventFragment());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
