package com.sportus.sportus.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.sportus.sportus.BaseActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.data.Participants;
import com.sportus.sportus.ui.ProfileFragment;

import java.io.IOException;
import java.net.URL;


public class ParticipantsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = ParticipantsViewHolder.class.getSimpleName();

    View mView;
    Context mContext;
    DatabaseReference mUserRef;

    Participants mParticipants;

    public ParticipantsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void setParticipants(Participants model) {
        mParticipants = model;
    }

    public void bindEvent(Participants participant) throws IOException {
        final ImageView profilePicture = (ImageView) mView.findViewById(R.id.profilePictureParticipants);
        final TextView userName = (TextView) mView.findViewById(R.id.profileNameParticipants);

        profilePicture.setImageBitmap(setUserImage(participant.getUserPhoto()));
        userName.setText(participant.getUserName());
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