package com.sportus.sportus.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sportus.sportus.Adapters.ParticipantsViewHolder;
import com.sportus.sportus.R;

import java.util.ArrayList;

public class ParticipantsFragment extends BaseFragment {
    public static final String TAG = ParticipantsFragment.class.getSimpleName();
    public static final String KEY_USER_INDEX = "user_index";

    private DatabaseReference mUserRef;
    private DatabaseReference mUserReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    RecyclerView mRecylerView;

    private DatabaseReference mEventRef;
    private DatabaseReference mParticipantsRef;

    private FirebaseAuth mAuth;
    FirebaseUser mUser;

    ArrayList<String> userParticipants;

    String eventKey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.participants_list, container, false);
        String message = "Carregando os Participantes...";
        showDialog(message);
        changeToolbar("Participantes");

        eventKey = "-KK-Qt_GJBAEScAbQjAk";
        final TextView eventTitle = (TextView) view.findViewById(R.id.eventTitleParticipants);

        mEventRef = FirebaseDatabase.getInstance().getReference("events").child(eventKey);

       /* mEventRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event event = dataSnapshot.getValue(Event.class);
                        eventTitle.setText(event.getTitle());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });*/
        mParticipantsRef = FirebaseDatabase.getInstance().getReference().child("participants").child(eventKey);
        mParticipantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userParticipants = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userKey = snapshot.getKey();
                    userParticipants.add(userKey);
                    mUserReference = FirebaseDatabase.getInstance().getReference(userKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecylerView = (RecyclerView) view.findViewById(R.id.listParticipants);
        mUserRef = FirebaseDatabase.getInstance().getReference();
        setUpFirebaseAdapter();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        return view;
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, ParticipantsViewHolder>
                (String.class, R.layout.list_item_participants, ParticipantsViewHolder.class, mParticipantsRef) {

            @Override
            protected void populateViewHolder(ParticipantsViewHolder viewHolder, String model, int position) {
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
}
