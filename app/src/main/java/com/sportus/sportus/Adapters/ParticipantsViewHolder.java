package com.sportus.sportus.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.sportus.sportus.data.Participants;
import com.sportus.sportus.data.User;
import com.sportus.sportus.ui.ProfileFragment;

import java.io.IOException;
import java.net.URL;


public class ParticipantsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = ParticipantsViewHolder.class.getSimpleName();

    View mView;
    Context mContext;
    DatabaseReference mUserRef;
    ProgressDialog dialog;

    ImageView profilePicture;
    TextView userName;

    Participants mParticipants;

    public ParticipantsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindEvent(Participants participant) {
        profilePicture = (ImageView) mView.findViewById(R.id.profilePictureParticipants);
        userName = (TextView) mView.findViewById(R.id.profileNameParticipants);
        mUserRef = FirebaseDatabase.getInstance().getReference("users").child(participant.getUserId());
        mUserRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            userName.setText(user.getName());
                            String userPic = user.getPhoto();
                            try {
                                profilePicture.setImageBitmap(setUserImage(user.getPhoto()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            userName.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        mParticipants = participant;
    }

    @Override
    public void onClick(View v) {
        BaseActivity activity = ((BaseActivity) mContext);
        activity.openProfileFragment(new ProfileFragment(), mParticipants.getUserId());
    }

    private Bitmap setUserImage(String photo) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL(photo);
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        return bmp;
    }

}