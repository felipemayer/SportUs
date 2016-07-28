package com.sportus.sportus;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    ImageView logo;
    View bg_splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = (ImageView) findViewById(R.id.logoSplashScreen);
        bg_splash = findViewById(R.id.bg_splash);

        Handler handler = new Handler();
        boolean b = handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    Pair<View, String> p1 = Pair.create((View)logo, "logo");
                    Pair<View, String> p2 = Pair.create(bg_splash, "redView");
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(SplashScreen.this, p1, p2);
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);

    }
}
