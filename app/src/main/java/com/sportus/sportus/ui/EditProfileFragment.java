package com.sportus.sportus.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileFragment extends BaseFragment {
    private static final String TAG = EditProfileFragment.class.getSimpleName();

    DatabaseReference readUserRef;
    DatabaseReference updateUserRef;
    List<String> checkeds;

    EditText inputNameMyProfile;
    EditText inputEmailMyProfile;
    EditText inputLocalMyProfile;
    EditText inputAgeMyProfile;

    String checkboxs[];
    CheckBox checkBox;

    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        changeToolbar("Editar Perfil");
        final View view = inflater.inflate(R.layout.edit_profile_fragment, container, false);
        setupUI(view);

        inputNameMyProfile = (EditText) view.findViewById(R.id.inputNameMyProfile);
        inputEmailMyProfile = (EditText) view.findViewById(R.id.inputEmailMyProfile);
        inputLocalMyProfile = (EditText) view.findViewById(R.id.inputLocalMyProfile);
        inputAgeMyProfile = (EditText) view.findViewById(R.id.inputAgeMyProfile);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUserId = currentUser.getUid();
        readUserRef = database.getReference("users").child(currentUserId);
        updateUserRef = database.getReference();

        checkboxs = getResources().getStringArray(R.array.event_types);

        // Read from the database
        readUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                user = dataSnapshot.getValue(User.class);
                fillInputs(user);
                Log.d(TAG, "Value is: " + user.getInterests());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

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
                Log.d(TAG, "Apertou o botão: " + user.getName());
            }
        });

        Button cancelEditProfile = (Button) view.findViewById(R.id.cancelEditProfile);
        cancelEditProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileFragment(new ProfileFragment(), currentUserId);
            }
        });

        Button saveEditProfile = (Button) view.findViewById(R.id.saveEditProfile);
        saveEditProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();
                String name = inputNameMyProfile.getText().toString();
                String email = inputEmailMyProfile.getText().toString();
                String local = inputLocalMyProfile.getText().toString();
                String age = inputAgeMyProfile.getText().toString();
                List<String> interests = checkeds;
                showDialog("Salvando os dados");

                updateUser(userId, name, email, null, local, age, interests);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeDialog();
                        openProfileFragment(new ProfileFragment(), currentUserId);
                    }
                }, 600);
            }
        });

        return view;
    }

    private void fillInputs(User user) {
        inputNameMyProfile.setText((user.getName() == null) ? "" : user.getName());
        inputEmailMyProfile.setText((user.getEmail() == null) ? "" : user.getEmail());
        inputLocalMyProfile.setText((user.getLocal() == null) ? "" : user.getLocal());
        inputAgeMyProfile.setText((user.getAge() == null) ? "" : user.getAge());
    }

    private void updateUser(String userId, String name, String email, Uri photo, String local, String age, List<String> interests) {
        User user = new User(name, email, photo, local, age, interests);
        Map<String, Object> userValue = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + userId, userValue);

        updateUserRef.updateChildren(childUpdates);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
