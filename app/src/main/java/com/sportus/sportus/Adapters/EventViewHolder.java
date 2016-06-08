package com.sportus.sportus.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sportus.sportus.MainActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.data.Event;
import com.sportus.sportus.ui.EventDetailsFragment;
import com.sportus.sportus.ui.EventsFragment;

import java.util.ArrayList;


public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = EventViewHolder.class.getSimpleName();
    private EventsFragment.OnEventSelectedInterface mListener;

    View mView;
    Context mContext;

    public EventViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindEvent(Event event) {
        ImageView iconEvent = (ImageView) mView.findViewById(R.id.itemImageEventIcon);
        TextView nameEvent = (TextView) mView.findViewById(R.id.itemTextEvent);
        TextView authorEvent = (TextView) mView.findViewById(R.id.itemAuthor);
        TextView addressEvent = (TextView) mView.findViewById(R.id.itemAddress);
        TextView dateEvent = (TextView) mView.findViewById(R.id.itemDate);
        TextView timeEvent = (TextView) mView.findViewById(R.id.itemTime);

        iconEvent.setImageResource(R.drawable.ic_ball);
        nameEvent.setText(event.getTitle());
        authorEvent.setText("Autor: " + event.getAuthor());
        addressEvent.setText("Local: " + event.getAddress());
        dateEvent.setText("Data: " + event.getDate());
        timeEvent.setText("Horário: " + event.getTime());

    }

    @Override
    public void onClick(View v) {
        final ArrayList<Event> events = new ArrayList<>();
        final ArrayList<String> eventsKey = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("events");
        // Log.d("EventViewHolder", "ref: " + ref );
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    events.add(snapshot.getValue(Event.class));
                    eventsKey.add(snapshot.getKey());
                }
                int itemPosition = getLayoutPosition();
                Event currentEvent = events.get(itemPosition);
                String eventKey = eventsKey.get(itemPosition);

                String author = currentEvent.getAuthor();
                String authorId = currentEvent.getAuthorId();
                String title = currentEvent.getTitle();
                String type = currentEvent.getType();
                String address = currentEvent.getAddress();
                String date = currentEvent.getDate();
                String time = currentEvent.getTime();
                String cost = currentEvent.getCost();
                boolean payMethod = currentEvent.isPayMethod();
                String createdAt = currentEvent.getCreatedAt();
                Double latitude = currentEvent.getLatitude();
                Double longitude = currentEvent.getLongitude();

                // Toast.makeText(mContext, "keyEvent: " +  currentEvent.getTitle(), Toast.LENGTH_SHORT).show();
                // Log.d("EventViewHolder", "itemPosition: " + itemPosition );

                MainActivity activity = ((MainActivity) mContext);
                activity.openEventFragment(new EventDetailsFragment(),
                        new Event(author, authorId, title, type, address, date, time, cost,
                                payMethod, createdAt, latitude, longitude), eventKey);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}