package com.sportus.sportus.ui;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.sportus.sportus.MainActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.data.Events;

import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment implements OnMapReadyCallback,
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
    private Map<Marker, String> allMarkersMap = new HashMap<Marker, String>();

    private FragmentActivity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        final View view = inflater.inflate(R.layout.home_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapHome);
        mMapView.onCreate(savedInstanceState);

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
        }
        else {
            googleMap.clear();
            handleNewLocation(location);
        };
    }

    private void handleNewLocation(Location location) {
        // Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        Marker userMarker;
        LatLng myPosition = new LatLng(currentLatitude, currentLongitude);

        for (int i = 0; i < Events.eventNames.length; i++){
            LatLng position = new LatLng(Events.eventLatitude[i], Events.eventLongitude[i]);
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(Events.eventNames[i])
                    .snippet(Events.eventAddress[i])
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_home)));
            allMarkersMap.put(marker, "1");
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int nameMarker = Integer.parseInt(allMarkersMap.get(marker));
                if (nameMarker != 0) {
                    String stringIndex = marker.getId().substring(1, 2);
                    int index = Integer.parseInt(stringIndex);
                    // Log.d(TAG, "The index is: " + index);
                    MainActivity activity = (MainActivity) getActivity();
                    activity.openFragment(new EventDetailsFragment(), index);
                }
            }
        });

        userMarker = googleMap.addMarker(new MarkerOptions()
                .position(myPosition)
                .title(MainActivity.mUserName)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.profile_maps)));
        allMarkersMap.put(userMarker, "0");

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

}
