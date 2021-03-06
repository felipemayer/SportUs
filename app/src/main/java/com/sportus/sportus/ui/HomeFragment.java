package com.sportus.sportus.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
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
import com.sportus.sportus.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final String TAG = HomeFragment.class.getSimpleName();

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Location location;

    MapView mMapView;
    private GoogleMap googleMap;
    private Map<Marker, Event> allMarkersMap = new HashMap<>();
    private Map<Marker, String> allMarkersMapEventKey = new HashMap<>();

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    private User user;
    private DatabaseReference readUserRef;

    private ProgressBar mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapHome);
        mMapView.onCreate(savedInstanceState);

        mProgress = (ProgressBar) view.findViewById(R.id.progressBarHome);
        mProgress.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        MainActivity activity = (MainActivity) getActivity();
        activity.setMainToolBar();

        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1000);

        mMapView.onResume();

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

        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            String userId = mUser.getUid();
            readUserRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        }

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                BaseActivity activity = (BaseActivity) getActivity();
                if (user != null) {
                    activity.fillHeaderNavigation(user);
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
        return view;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getContext(), getActivity())) {
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        handleNewLocation(likelyPlaces.get(0));
                    }
                    likelyPlaces.release();
                }
            });


        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getContext(), getActivity());
        }

        /*if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "sem permissão");
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d(TAG, "location null");
        } else {
            googleMap.clear();
            handleNewLocation(location);
            Log.d(TAG, String.valueOf(googleMap));
        }*/
    }

    private void handleNewLocation(PlaceLikelihood placeLikelihood) {
        String latLng = placeLikelihood.getPlace().getLatLng().toString();
        Log.d(TAG, "latLng: " + latLng );
    }

    public void requestPermission(String strPermission, int perCode, Context _c, Activity _a) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a, strPermission)) {
            Toast.makeText(getActivity(), "Favor, permitir a localização por gps", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
        }
    }

    public static boolean checkPermission(String strPermission, Context _c, Activity _a) {
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    handleNewLocation();

                } else {
                    Toast.makeText(getActivity(), "Permissão negada.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    private void handleNewLocation() {

        double currentLatitude = 1111;
        double currentLongitude = 1111;

        Marker userMarker;
        LatLng myPosition = new LatLng(currentLatitude, currentLongitude);

        final ArrayList<Event> events = new ArrayList<>();
        final ArrayList<String> eventsKey = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("events");

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
                mProgress.setVisibility(View.GONE);
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
                    openEventFragment(new EventDetailsFragment(), eventKey);
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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pinuser)));
        allMarkersMap.put(userMarker, null);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(currentLatitude, currentLongitude)).zoom(16).bearing(0)
                .tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    public void onAttach(Activity activity) {
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
        handleNewLocation();
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

        if (isLoggedIn(mUser)) {
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
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());
            startActivity(intent, options.toBundle());
            return true;
        } else if (id == R.id.logoutMenu) {
            openDialogLogOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}