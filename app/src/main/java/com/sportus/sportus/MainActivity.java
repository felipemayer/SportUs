package com.sportus.sportus;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sportus.sportus.Adapters.DrawerNavigationAdapter;
import com.sportus.sportus.data.Event;
import com.sportus.sportus.ui.AboutFragment;
import com.sportus.sportus.ui.AgendaInvitesPagerFragment;
import com.sportus.sportus.ui.CreateEventFragment;
import com.sportus.sportus.ui.EventDetailsFragment;
import com.sportus.sportus.ui.EventsFragment;
import com.sportus.sportus.ui.FriendsFragment;
import com.sportus.sportus.ui.HomeFragment;


public class MainActivity extends AppCompatActivity implements AppCompatCallback, EventsFragment.OnEventSelectedInterface {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String LIST_FRAGMENT_EVENTS = "list_fragment_events";
    public static final String VIEWPAGER_FRAGMENT = "viewpager_events";
    public static final String AGENDA_FRAGMENT = "agenda_fragment";
    public static final String HOME_FRAGMENT = "home_fragment";

    String TITLES[] = {"Home", "Eventos", "Criar Eventos", "Perfil", "Amigos", "Agenda", "Sobre"};
    int ICONS[] = {R.drawable.ic_ball, R.drawable.ic_ball, R.drawable.ic_ball,
            R.drawable.ic_ball, R.drawable.ic_ball,
            R.drawable.ic_ball, R.drawable.ic_ball};

    public static String mUserName;
    String mUserEmail;
    Uri mUserPhoto;

    private FirebaseAuth mAuth;
    FirebaseUser mUser;

    Toolbar toolbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    protected DrawerLayout Drawer;
    ActionBarDrawerToggle mDrawerToggle;

    Button buttonLogin;
    Button buttonLogout;

    FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        HomeFragment savedFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_EVENTS);
        if (savedFragment == null) {
            openFragment(new HomeFragment());
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Initialize Database
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize Views
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        buttonLogout.setVisibility(isLoggedIn() ? View.VISIBLE : View.GONE);
        buttonLogin.setVisibility(isLoggedIn() ? View.GONE : View.VISIBLE);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        // Allow the user to sign out
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            mUserName = user.getDisplayName();
            mUserEmail = user.getEmail();
            mUserPhoto = user.getPhotoUrl();
            Log.d(TAG, String.valueOf(mUserPhoto));

        } else {
            // No user is signed in
            mUserName = "";
            mUserEmail = "";
            mUserPhoto = null;
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerNavigationAdapter(TITLES, ICONS, mUserName, mUserEmail, mUserPhoto, this);
        mRecyclerView.setAdapter(mAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Drawer.closeDrawers();
                    onTouchDrawer(recyclerView.getChildAdapterPosition(child));
                    // Toast.makeText(MainActivity.this, "The Item Clicked is: " + recyclerView.getChildAdapterPosition(child), Toast.LENGTH_SHORT).show();

                    return true;
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        Drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    public void onTouchDrawer(final int position) {
        switch (position) {
            case 1:
                openFragment(new HomeFragment());
                break;
            case 2:
                openFragment(new EventsFragment());
                break;
            case 3:
                if (mUser == null) {
                    openDialogLogin();
                } else {
                    openFragment(new CreateEventFragment());
                    break;
                }
            case 4:

                break;
            case 5:
                openFragment(new FriendsFragment());
                break;
            case 6:
                openFragment(new AgendaInvitesPagerFragment(), VIEWPAGER_FRAGMENT);
                break;
            case 7:
                openFragment(new AboutFragment());
                break;
            default:
                return;
        }
    }

    private boolean isLoggedIn() {
        return mCurrentUser != null;
    }



    public void openFragment(final Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void openFragment(final Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    public void openFragment(final Fragment fragment, int index) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putInt(EventsFragment.KEY_EVENT_INDEX, index);
        fragment.setArguments(bundle);
    }

    public void openEventFragment(final Fragment fragment, Event event, String eventIndex) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putString(EventDetailsFragment.EVENT_INDEX, eventIndex);
        bundle.putParcelable(EventDetailsFragment.EVENT_OBJECT, event);
        fragment.setArguments(bundle);
    }

    public void openEventFragment(final Fragment fragment, Event event) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EventDetailsFragment.EVENT_OBJECT, event);
        fragment.setArguments(bundle);
    }

    @Override
    public void onListEventSelected(String index, Event currentEvent) {

    }

    public void onRadioButtonClicked(View view) {
        CreateEventFragment.onRadioButtonClicked(view);
    }


    public void openDialogLogin(){
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
                /*Intent i = new Intent(getActivity(), SignInActivity.class);
                startActivity(i);*/
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                dialog.dismiss();

            }
        });

        dialog.show();
    }
}