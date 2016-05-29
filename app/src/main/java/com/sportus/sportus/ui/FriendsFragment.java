package com.sportus.sportus.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sportus.sportus.R;
import com.sportus.sportus.data.User;

public class FriendsFragment extends Fragment {
    public static final String TAG = FriendsFragment.class.getSimpleName();

    private DatabaseReference mUserRef;
    private String mUserId;
    private ValueEventListener mUserListener;

    private TextView mUserName;

    FirebaseUser mCurrentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_fragment,container, false );

        // Initialize Database
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = mCurrentUser.getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mUserId);
        Log.d(TAG, String.valueOf(mUserRef));

        mUserName = (TextView) view.findViewById(R.id.friend_name);

        FirebaseDatabase.getInstance().getReference().child("users").child(mUserId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        Log.d(TAG, "funciona, porra!");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        return view;
    }



}
