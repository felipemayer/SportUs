package com.sportus.sportus.ui;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sportus.sportus.data.Event;
import com.sportus.sportus.data.Participants;
import com.sportus.sportus.data.User;

import java.io.IOException;
import java.net.URL;

public class ParticipantsFragment extends BaseFragment {
    public static final String TAG = ParticipantsFragment.class.getSimpleName();
    public static final String KEY_USER_INDEX = "user_index";

    private FirebaseRecyclerAdapter mFirebaseAdapter;
    RecyclerView mRecylerView;

    private DatabaseReference mParticipantsRef;

    private FirebaseAuth mAuth;
    FirebaseUser mUser;

    String eventKey;
    String eventAuthorId;
    TextView eventAuthor;
    ImageView profilePictureAuthorParticipants;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.participants_list, container, false);
        eventKey = getArguments().getString(ParticipantsFragment.KEY_USER_INDEX);
        showDialog(getString(R.string.loading_participants));

        eventAuthor = (TextView) view.findViewById(R.id.eventTitleParticipants);
        profilePictureAuthorParticipants = (ImageView) view.findViewById(R.id.profilePictureAuthorParticipants);

        DatabaseReference mEventRef = FirebaseDatabase.getInstance().getReference("events").child(eventKey);
        mEventRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event event = dataSnapshot.getValue(Event.class);
                        changeToolbar(event.getTitle());
                        eventAuthorId = event.authorId;
                        getAuthor(eventAuthorId);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        mParticipantsRef = FirebaseDatabase.getInstance().getReference().child("participants").child(eventKey);

        mRecylerView = (RecyclerView) view.findViewById(R.id.listParticipants);

        setUpFirebaseAdapter();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        LinearLayout profileAuthorParticipants = (LinearLayout) view.findViewById(R.id.profileAuthorParticipants);
        profileAuthorParticipants.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileFragment(new ProfileFragment(), eventAuthorId);
            }
        });

        return view;
    }

    private void getAuthor(String eventAuthorId) {
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("users").child(eventAuthorId);
        mUserRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            eventAuthor.setText("Organizador: " + user.getName());
                            String userPic = user.getPhoto();
                            try {
                                setUserImage(userPic);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            eventAuthor.setText("Organizador não encontrado :(");
                            profilePictureAuthorParticipants.setImageDrawable(getResources().getDrawable(R.drawable.profile));
                        }
                        closeDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Participants, ParticipantsViewHolder>
                (Participants.class, R.layout.list_item_participants, ParticipantsViewHolder.class, mParticipantsRef) {

            @Override
            protected void populateViewHolder(ParticipantsViewHolder viewHolder, Participants model, int position) {
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

    private void setUserImage(String photo) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL(photo);
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        profilePictureAuthorParticipants.setImageBitmap(bmp);
    }
}