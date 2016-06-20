package com.sportus.sportus.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sportus.sportus.R;
import com.sportus.sportus.data.User;

import java.util.ArrayList;


public class ParticipantsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = ParticipantsViewHolder.class.getSimpleName();

    View mView;
    Context mContext;

    DatabaseReference mUserRef;

    public ParticipantsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindEvent(String participantKey) {
        final ImageView profilePicture = (ImageView) mView.findViewById(R.id.profilePictureParticipants);
        final TextView userName = (TextView) mView.findViewById(R.id.profileNameParticipants);

        mUserRef = FirebaseDatabase.getInstance().getReference("users").child(participantKey);
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                profilePicture.setImageResource(R.drawable.ic_ball);
                userName.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        final ArrayList<User> users = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    users.add(snapshot.getValue(User.class));
                }
                int itemPosition = getLayoutPosition();
                User currentUser = users.get(itemPosition);

                Toast.makeText(mContext, "keyEvent: " + currentUser.getName(), Toast.LENGTH_SHORT).show();
                // Log.d("EventViewHolder", "itemPosition: " + itemPosition );

                /*MainActivity activity = ((MainActivity) mContext);
                activity.openEventFragment(new EventDetailsFragment(),
                        new Event(author, authorId, title, type, address, date, time, cost,
                                payMethod, createdAt, latitude, longitude), eventKey);*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}