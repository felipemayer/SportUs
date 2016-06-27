package com.sportus.sportus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sportus.sportus.data.User;
import com.sportus.sportus.ui.CreateEventFragment;
import com.sportus.sportus.ui.EventDetailsFragment;
import com.sportus.sportus.ui.EventsFragment;
import com.sportus.sportus.ui.HomeFragment;
import com.sportus.sportus.ui.ProfileFragment;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

abstract public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private DatabaseReference mDatabase;

    TextView nameMenu;
    ImageView photoMenu;
    TextView emailMenu;

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    String userId;
    User user;

    ProgressDialog dialog;

    Toolbar toolbar;
    DrawerLayout drawerLayout;

    DatabaseReference readUserRef;
    NavigationView navigationView;
    private String currentUserName;
    private String currentUserEmail;
    private String currentUserPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            userId = mUser.getUid();
            readUserRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        }

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                currentUserName = user.getName();
                currentUserEmail = user.getEmail();
                currentUserPhoto = user.getPhoto();

                View header = navigationView.getHeaderView(0);
                nameMenu = (TextView) header.findViewById(R.id.nameHeaderMenu);
                emailMenu = (TextView) header.findViewById(R.id.emailHeaderMenu);
                photoMenu = (ImageView) header.findViewById(R.id.photoHeaderMenu);

                nameMenu.setText(currentUserName);
                emailMenu.setText(currentUserEmail);
                try {
                    setUserImage(currentUserPhoto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        if (mUser != null) {
            readUserRef.addValueEventListener(userListener);
        }

    }


    public boolean isLoggedIn(FirebaseUser user) {
        return user != null;
    }

    public void openFragment(final Fragment fragment) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.placeholder, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }, 400);

    }

    public void openProfileFragment(final Fragment fragment, String index) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putString(ProfileFragment.PROFILE_INDEX, index);
        fragment.setArguments(bundle);
    }


    public void openEventFragment(final Fragment fragment, String eventIndex) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putString(EventDetailsFragment.EVENT_INDEX, eventIndex);
        fragment.setArguments(bundle);
    }

    public void onRadioButtonClicked(View view) {
        CreateEventFragment.onRadioButtonClicked(view);
    }


    public void openDialogLogin() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_to_login);
        dialog.setTitle("Conte mais sobre você");

        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogCancelLogin);
        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogDoLogin);

        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignInActivity.class);
                startActivity(intent);
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    public void createUser(String userId, String name, String email, String photo) {
        mDatabase.child("users").push();
        User user = new User(name, email, photo);
        Map<String, Object> userValue = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + userId, userValue);

        mDatabase.updateChildren(childUpdates);
    }

    public void createNavigationDrawer(Toolbar toolbar) {

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.homeMenu:
                        openFragment(new HomeFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.eventsMenu:
                        openFragment(new EventsFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.createEventMenu:
                        if (!isLoggedIn(mUser)) {
                            openDialogLogin();
                            drawerLayout.closeDrawers();
                        } else {
                            openFragment(new CreateEventFragment());
                            drawerLayout.closeDrawers();
                            break;
                        }
                    case R.id.profileMenu:
                        if (!isLoggedIn(mUser)) {
                            openDialogLogin();
                            drawerLayout.closeDrawers();
                        } else {
                            openProfileFragment(new ProfileFragment(), mUser.getUid());
                            drawerLayout.closeDrawers();
                            break;
                        }
                    default:

                }
                return true;
            }
        });


        drawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public void setMainToolBar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ImageView toolbarImage = (ImageView) (findViewById(R.id.logo_toolbar));
        toolbarImage.setVisibility(View.VISIBLE);

        createNavigationDrawer(toolbar);
    }

    public void showDialog(String message) {
        dialog = ProgressDialog.show(BaseActivity.this, null, message, false, true);
        dialog.setCancelable(false);
    }

    public void closeDialog() {
        dialog.dismiss();
    }

    private void setUserImage(String photo) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL(photo);
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        photoMenu.setImageBitmap(bmp);
    }
}
