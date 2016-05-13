package com.sportus.sportus.ui;

import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.sportus.sportus.R;
import com.sportus.sportus.data.Events;

public class EventDetailsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = EventDetailsFragment.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    int mEventId;
    String mEventName;
    String mEventLevel;
    String mEventAddress;
    String mEventDate;
    String mEventHour;

    private GoogleApiClient mGoogleApiClient;
    MapView mMapView;
    private GoogleMap googleMap;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int index = getArguments().getInt(EventsFragment.KEY_EVENT_INDEX);
        View view = inflater.inflate(R.layout.event_details_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapEventDetail);
        mMapView.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        LatLng position = new LatLng(Events.eventLatitude[index], Events.eventLongitude[index]);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(Events.eventNames[index])
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_home)));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position).zoom(16).bearing(0)
                .tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mEventId = Events.eventIds[index];
        mEventName = Events.eventNames[index];
        mEventAddress = Events.eventAddress[index];
        mEventLevel = Events.eventLevels[index];
        mEventDate = Events.eventDate[index];
        mEventHour = Events.eventTime[index];

        TextView eventName = (TextView) view.findViewById(R.id.eventName);
        eventName.setText(mEventName);
        TextView eventAddress = (TextView) view.findViewById(R.id.eventAddress);
        eventAddress.setText(mEventAddress);
        TextView eventLevel = (TextView) view.findViewById(R.id.eventLevel);
        eventLevel.setText("Nível: " + mEventLevel);
        TextView eventDate = (TextView) view.findViewById(R.id.eventDate);
        eventDate.setText(mEventDate);
        TextView eventHour = (TextView) view.findViewById(R.id.eventHour);
        eventHour.setText(mEventHour);
        return view;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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
            mGoogleApiClient.disconnect();
        }
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
    public void onMapReady(GoogleMap googleMap) {

    }
}
