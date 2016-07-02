package com.sportus.sportus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.CallbackManager.Factory;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;

public class SignUpActivity extends BaseActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private AuthStateListener mAuthListener;

    private ProgressDialog dialog;

    EditText emailUser;
    EditText passwordUser;
    EditText nameUser;

    private String email;
    private String password;
    private String name;

    private CallbackManager mCallbackManager;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        View view = getWindow().getDecorView().getRootView();
        setupUI(view);

        mAuth = FirebaseAuth.getInstance();

        nameUser = (EditText) findViewById(R.id.inputNameSign);
        emailUser = (EditText) findViewById(R.id.inputEmailSign);
        passwordUser = (EditText) findViewById(R.id.inputPasswordSign);

        Button mSignin = (Button) findViewById(R.id.loginButton);
        assert mSignin != null;
        mSignin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mAuthListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    String userId = user.getUid();
                    String userName = (user.getDisplayName() != null) ? user.getDisplayName() : nameUser.getText().toString();
                    String userEmail = user.getEmail();
                    String userPhoto;
                    if (user.getPhotoUrl() != null) {
                        userPhoto = String.valueOf(user.getPhotoUrl());
                        Log.d(TAG, "Photo do login: " + userPhoto);
                    } else {
                        userPhoto = null;
                        Log.d(TAG, "Photo do login: NULL");
                    }

                    createUser(userId, userName, userEmail, userPhoto);
                    callMainActivity();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }

        };

        mCallbackManager = Factory.create();
        Button loginButton = (Button) findViewById(R.id.login_button_facebook);
        assert loginButton != null;
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
                LoginManager.getInstance().registerCallback(mCallbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                                handleFacebookAccessToken(loginResult.getAccessToken());
                                showDialog();
                            }

                            @Override
                            public void onCancel() {
                                Log.d(TAG, "facebook:onCancel");
                            }

                            @Override
                            public void onError(FacebookException error) {
                                Log.d(TAG, "facebook:onError", error);
                            }
                        });
            }
        });


        Button createAccount = (Button) findViewById(R.id.createAccount);
        assert createAccount != null;
        createAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameUser.getText().toString();
                email = emailUser.getText().toString();
                password = passwordUser.getText().toString();

                email = email.trim();
                password = password.trim();
                if (name.matches("")) {
                    Toast.makeText(SignUpActivity.this, "Qual é o seu NOME?", Toast.LENGTH_LONG).show();
                } else if (email.matches("")) {
                    Toast.makeText(SignUpActivity.this, "Ops, esqueceu do E-MAIL", Toast.LENGTH_LONG).show();
                } else if (password.matches("")) {
                    Toast.makeText(SignUpActivity.this, "Opa, esqueceu a SENHA", Toast.LENGTH_LONG).show();
                } else {
                    showDialog();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final FirebaseUser firebaseUser = task.getResult().getUser();
                                        Task<Void> updateTask = firebaseUser.updateProfile(
                                                new UserProfileChangeRequest
                                                        .Builder()
                                                        .setDisplayName(name).build());
                                        updateTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });
                                    }
                                    if (!task.isSuccessful()) {
                                        dialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Hmm.. Algo está errado",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        mAuth.addAuthStateListener(mAuthListener);
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Signin Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.sportus.sportus/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Signin Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.sportus.sportus/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        client.disconnect();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void callMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        dialog.dismiss();
        startActivity(intent);
        finish();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(SignUpActivity.this);
                    return false;
                }

            });
        }

        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    public void showDialog() {
        dialog = ProgressDialog.show(SignUpActivity.this, null, "Criando a sua conta...", false, true);
        dialog.setCancelable(false);
    }
}
