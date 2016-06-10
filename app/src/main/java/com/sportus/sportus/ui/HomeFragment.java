package com.sportus.sportus.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sportus.sportus.BaseActivity;
import com.sportus.sportus.MainActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.SignInActivity;
import com.sportus.sportus.data.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String LIST_FRAGMENT_EVENTS = "list_fragment_events";
    private static final String EVENT_FRAGMENT = "event_fragment";

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    MapView mMapView;
    private GoogleMap googleMap;
    private Map<Marker, Event> allMarkersMap = new HashMap<>();
    private Map<Marker, String> allMarkersMapEventKey = new HashMap<>();

    private FragmentActivity context;

    FirebaseUser mUser;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        final View view = inflater.inflate(R.layout.home_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapHome);
        mMapView.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        BaseActivity activity = (BaseActivity) getActivity();
        activity.setMainToolBar();

        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();

        Button buttonHome = (Button) view.findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.openFragment(new EventsFragment());
            }
        });
        return view;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            googleMap.clear();
            handleNewLocation(location);
        }
        ;
    }

    private void handleNewLocation(Location location) {
        // Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        Marker userMarker;
        LatLng myPosition = new LatLng(currentLatitude, currentLongitude);

        final ArrayList<Event> events = new ArrayList<>();
        final ArrayList<String> eventsKey = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("events");
        Log.d("EventViewHolder", "ref: " + ref);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    events.add(snapshot.getValue(Event.class));
                    eventsKey.add(snapshot.getKey());
                    for (int i = 0; i < events.size(); i++) {
                        final Event currentEvent = events.get(i);
                        String eventKey = eventsKey.get(i);
                        LatLng position = new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude());
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(currentEvent.getTitle())
                                .snippet("Dia: " + currentEvent.getDate())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_home)));
                        allMarkersMap.put(marker, currentEvent);
                        allMarkersMapEventKey.put(marker, eventKey);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Event currentEvent = allMarkersMap.get(marker);
                String eventKey = allMarkersMapEventKey.get(marker);
                if (currentEvent != null) {
                    Log.d(TAG, "The currentEvent is: " + eventKey);
                    String author = currentEvent.getAuthor();
                    String authorId = currentEvent.getAuthorId();
                    String title = currentEvent.getTitle();
                    String type = currentEvent.getType();
                    String address = currentEvent.getAddress();
                    String date = currentEvent.getDate();
                    String time = currentEvent.getTime();
                    String cost = currentEvent.getCost();
                    boolean payMethod = currentEvent.isPayMethod();
                    String createdAt = currentEvent.getCreatedAt();
                    Double latitude = currentEvent.getLatitude();
                    Double longitude = currentEvent.getLongitude();

                    // Toast.makeText(getActivity(), "keyEvent: " +  currentEvent.getTitle(), Toast.LENGTH_SHORT).show();
                    MainActivity activity = ((MainActivity) getActivity());
                    activity.openEventFragment(new EventDetailsFragment(),
                            new Event(author, authorId, title, type, address, date, time, cost,
                                    payMethod, createdAt, latitude, longitude), eventKey);
                }
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String mUserName;
        if (user != null) {
            mUserName = user.getDisplayName();
        } else {
            mUserName = "";
        }

        userMarker = googleMap.addMarker(new MarkerOptions()
                .position(myPosition)
                .title(mUserName)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.profile_maps)));
        allMarkersMap.put(userMarker, null);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(currentLatitude, currentLongitude)).zoom(16).bearing(0)
                .tilt(45).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    public void onAttach(Activity activity) {
        context = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem login = menu.findItem(R.id.loginMenu);
        MenuItem logout = menu.findItem(R.id.logoutMenu);

        if (isLoggedIn(mUser)){
            login.setVisible(false);
            logout.setVisible(true);
        } else {
            login.setVisible(true);
            logout.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.loginMenu) {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            Log.d("Entrando", " aqui!");
            startActivity(intent);
            return true;
        } else if (id == R.id.logoutMenu) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            Log.d("Entrando", " mentira, saindo!");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
