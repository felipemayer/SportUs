package com.sportus.sportus.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sportus.sportus.R;
import com.sportus.sportus.data.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileFragment extends BaseFragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();

    public static final String PROFILE_INDEX = "index_profile";

    DatabaseReference readUserRef;
    DatabaseReference readUserRefInterests;
    FirebaseUser currentUser;

    String mProfileId;

    ImageView profilePicture;
    TextView profileName;
    TextView profileEmail;
    TextView profilePlace;
    TextView profileAge;

    List<String> profileInterests;
    LinearLayout profileInterestsLayout;
    TextView interest;

    User user;

    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference profileImageRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mProfileId = getArguments().getString(ProfileFragment.PROFILE_INDEX);
        changeToolbar("Perfil");
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();
        readUserRef = database.getReference("users").child(mProfileId);
        readUserRefInterests = database.getReference("users").child(mProfileId).child("interests");

        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://sport-us.appspot.com");
        profileImageRef = storageRef.child("profile-images");

        Button myProfile = (Button) view.findViewById(R.id.editProfile);

        profileInterestsLayout = (LinearLayout) view.findViewById(R.id.profileInterests);

        profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
        profileName = (TextView) view.findViewById(R.id.profileName);
        profileEmail = (TextView) view.findViewById(R.id.profileEmail);
        profilePlace = (TextView) view.findViewById(R.id.profilePlace);
        profileAge = (TextView) view.findViewById(R.id.profileAge);

        // Read from the database
        readUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                user = dataSnapshot.getValue(User.class);
                fillInputs(user);

                String profPic = user.getPhoto();

                setUserImage(profPic);

                if (user.getInterests() != null) {
                    profileInterests = user.getInterests();
                    String[] profileInterestsArray = profileInterests.toArray(new String[profileInterests.size()]);
                    createInterestsList(profileInterestsArray);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        myProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new EditProfileFragment());
            }
        });


        return view;
    }

    private void setUserImage(String photo) {
        Picasso.with(getActivity())
                .load(photo)
                .placeholder(R.drawable.profile)
                .into(profilePicture);
    }

    private void fillInputs(User user) {
        profileName.setText((user.getName() == null) ? "" : user.getName());
        profileEmail.setText((user.getEmail() == null) ? "" : user.getEmail());
        profilePlace.setText((user.getLocal() == null) ? "Local: " : "Local: " + user.getLocal());
        profileAge.setText((user.getAge() == null) ? "Idade: " : "Idade: " + user.getAge());
        ;
    }

    public void createInterestsList(String[] profileInterestsArray) {
        profileInterestsLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < profileInterestsArray.length; i++) {
            TextView textView = new TextView(getActivity());
            textView.setText(" - " + profileInterestsArray[i]);
            textView.setTextSize(18);
            profileInterestsLayout.addView(textView);
        }
    }
}
