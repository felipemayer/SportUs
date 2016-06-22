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
import com.sportus.sportus.BaseActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.data.Event;
import com.sportus.sportus.ui.BaseFragment;
import com.sportus.sportus.ui.EventDetailsFragment;

import java.util.ArrayList;


public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = EventViewHolder.class.getSimpleName();

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
        ImageView itemImageEventPayment = (ImageView) mView.findViewById(R.id.itemImageEventPayment);

        BaseFragment fragment = new BaseFragment();
        iconEvent.setImageResource(fragment.setTypeIcon(event.getType()));
        nameEvent.setText(event.getTitle());
        authorEvent.setText(event.getAuthor());
        addressEvent.setText(event.getAddress());
        dateEvent.setText(event.getDate());
        timeEvent.setText(event.getTime());
        if(event.isPayMethod()){
            itemImageEventPayment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        final ArrayList<Event> events = new ArrayList<>();
        final ArrayList<String> eventsKey = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("events");
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
                // Toast.makeText(mContext, "keyEvent: " +  currentEvent.getTitle(), Toast.LENGTH_SHORT).show();
                // Log.d("EventViewHolder", "itemPosition: " + itemPosition );

                BaseActivity activity = ((BaseActivity) mContext);
                activity.openEventFragment(new EventDetailsFragment(), eventKey);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}