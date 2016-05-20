package com.sportus.sportus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sportus.sportus.Adapters.DrawerNavigationAdapter;
import com.sportus.sportus.ui.AboutFragment;
import com.sportus.sportus.ui.AgendaInvitesPagerFragment;
import com.sportus.sportus.ui.CreateEventFragment;
import com.sportus.sportus.ui.EventDetailsFragment;
import com.sportus.sportus.ui.EventsFragment;
import com.sportus.sportus.ui.FriendsFragment;
import com.sportus.sportus.ui.HomeFragment;
import com.sportus.sportus.ui.ProfileFragment;


public class MainActivity extends AppCompatActivity implements AppCompatCallback, EventsFragment.OnEventSelectedInterface {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String LIST_FRAGMENT_EVENTS = "list_fragment_events";
    public static final String VIEWPAGER_FRAGMENT = "viewpager_events";
    public static final String AGENDA_FRAGMENT = "agenda_fragment";
    public static final String HOME_FRAGMENT = "home_fragment";

    String TITLES[] = {"Home","Eventos", "Criar Eventos", "Perfil", "Amigos", "Agenda", "Sobre"};
    int ICONS[] = {R.drawable.ic_ball,R.drawable.ic_ball, R.drawable.ic_ball,
            R.drawable.ic_ball, R.drawable.ic_ball,
            R.drawable.ic_ball, R.drawable.ic_ball};
    public static String NAME = "Maria Joaquina";
    String EMAIL = "maria@sportus.com";
    int PROFILE = R.drawable.profile;

    private Toolbar toolbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    protected DrawerLayout Drawer;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeFragment savedFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_EVENTS);
        if (savedFragment == null) {
            openFragment(new HomeFragment());
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerNavigationAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE, this);
        mRecyclerView.setAdapter(mAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

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
                openFragment(new CreateEventFragment());
                break;
            case 4:
                openFragment(new ProfileFragment());
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

    @Override
    public void onListEventSelected(int index) {
        openFragment(new EventDetailsFragment(), index);
    }

    public void onRadioButtonClicked(View view) {
        CreateEventFragment.onRadioButtonClicked(view);
    }
}