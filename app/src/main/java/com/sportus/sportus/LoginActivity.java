package com.sportus.sportus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.sportus.sportus.ui.LoginFragment;

public class LoginActivity extends AppCompatActivity {
    public static final String LOGIN_FRAGMENT = "login_fragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginFragment savedFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT);

        if (savedFragment == null) {
            openFragment(new LoginFragment());
        }

    }

    public void openFragment(final Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
    }
}
