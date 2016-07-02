package com.sportus.sportus;

import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sportus.sportus.ui.HomeFragment;


public class MainActivity extends BaseActivity implements AppCompatCallback {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String LIST_FRAGMENT_EVENTS = "list_fragment_events";

    FirebaseUser mUser;

    FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        HomeFragment savedFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_EVENTS);
        if (savedFragment == null) {
            openFragment(new HomeFragment());
        }
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}