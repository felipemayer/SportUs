package com.sportus.sportus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sportus.sportus.LoginActivity;
import com.sportus.sportus.MainActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.data.User;

import java.util.HashMap;
import java.util.Map;

public class SigninFragment extends Fragment {
    private static final String TAG = SigninFragment.class.getSimpleName();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText emailUser;
    EditText passwordUser;
    EditText nameUser;

    private String email;
    private String password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signin_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nameUser = (EditText) view.findViewById(R.id.inputNameSign);
        emailUser = (EditText) view.findViewById(R.id.inputEmailSign);
        passwordUser = (EditText) view.findViewById(R.id.inputPasswordSign);

        Button mSignin = (Button) view.findViewById(R.id.loginButton);
        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity activity = (LoginActivity) getActivity();
                activity.openFragment(new LoginFragment());
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    String userId = user.getUid();
                    String userName = nameUser.getText().toString();
                    String userEmail = emailUser.getText().toString();

                    createUser(userId, userName, userEmail);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }

        };

        Button createAccount = (Button) view.findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Reterives user inputs
                email = emailUser.getText().toString();
                password = passwordUser.getText().toString();

                // trims the input
                email = email.trim();
                password = password.trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final FirebaseUser firebaseUser = task.getResult().getUser();
                                    String userNameFirebase = nameUser.getText().toString();
                                    Task<Void> updateTask = firebaseUser.updateProfile(
                                            new UserProfileChangeRequest
                                                    .Builder()
                                                    .setDisplayName(userNameFirebase).build());
                                    Log.d(TAG, "USER NAME: " + userNameFirebase);
                                    updateTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                                }
                            }
                        });
            }
        });
        return view;
    }

    private void createUser(String userId, String name, String email) {
        mDatabase.child("users").push();
        User user = new User(name, email);
        Map<String, Object> userValue = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + userId, userValue);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
