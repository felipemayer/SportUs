package com.sportus.sportus.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sportus.sportus.R;
import com.sportus.sportus.data.User;

import java.util.ArrayList;
import java.util.List;

public class EditProfileFragment extends BaseFragment {
    private static final String TAG = EditProfileFragment.class.getSimpleName();

    List<String> checkeds;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        changeToolbar("Editar Perfil");
        final View view = inflater.inflate(R.layout.edit_profile_fragment, container, false);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();
        DatabaseReference myRef = database.getReference("users").child(currentUserId);

        final String checkboxs[] = getResources().getStringArray(R.array.event_types);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "Value is: " + user.getName());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        CheckBox checkBox;
        checkeds = new ArrayList<String>();

        for (int i = 0; i < checkboxs.length; i++) {
            final int index = i;
            GridLayout parentLayout = (GridLayout) view.findViewById(R.id.gridInterests);
            checkBox = (CheckBox) inflater.inflate(R.layout.template_checkbox_interests, null);
            checkBox.setText(checkboxs[i]);
            checkBox.setId(i);

            parentLayout.addView(checkBox);

            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean checked = ((CheckBox) v).isChecked();
                    if (checked) {
//                        Toast.makeText(getActivity(), "-- " + checkboxs[index], Toast.LENGTH_LONG).show();
                        checkeds.add(checkboxs[index]);
                    }
                }
            });
        }

        Button changeProfilePhoto = (Button) view.findViewById(R.id.changeProfilePhoto);
        changeProfilePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, String.valueOf(checkeds));
            }
        });

        Button cancelEditProfile = (Button) view.findViewById(R.id.cancelEditProfile);
        cancelEditProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new ProfileFragment());
            }
        });

        Button saveEditProfile = (Button) view.findViewById(R.id.saveEditProfile);
        saveEditProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}
