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
import com.sportus.sportus.data.Event;

public class EventDetailsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = EventDetailsFragment.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final String EVENT_OBJECT = "current_event";
    public static final String EVENT_INDEX = "index_event";

    TextView mEventTitle;
    TextView mEventAddress;
    TextView mEventDate;
    TextView mEventTime;
    TextView mEventCost;
    TextView mEventAuthor;

    private GoogleApiClient mGoogleApiClient;
    MapView mMapView;
    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Event event = getArguments().getParcelable(EventDetailsFragment.EVENT_OBJECT);
        String eventIndex = getArguments().getString(EventDetailsFragment.EVENT_INDEX);
        View view = inflater.inflate(R.layout.event_details_fragment, container, false);
        Log.d(TAG, eventIndex);

        mEventTitle = (TextView) view.findViewById(R.id.eventTitle);
        mEventAuthor = (TextView) view.findViewById(R.id.eventAuthor);
        mEventAddress = (TextView) view.findViewById(R.id.eventAddress);
        mEventDate = (TextView) view.findViewById(R.id.eventDate);
        mEventTime = (TextView) view.findViewById(R.id.eventTime);
        mEventCost = (TextView) view.findViewById(R.id.eventCost);

        mEventTitle.setText(event.getTitle());
        mEventAuthor.setText("Organizadora: " + event.getAuthor());
        mEventAddress.setText("Local: " + event.getAddress());
        mEventDate.setText("Data: " + event.getDate());
        mEventTime.setText("Horário: " + event.getTime());
        if (event.isPayMethod()) {
            mEventCost.setText("Preço: " + event.getCost());
        } else {
            mEventCost.setText("Evento Gratuito");
        }

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
        LatLng position = new LatLng(event.getLatitude(), event.getLongitude());
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(event.getTitle())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_home)));
        marker.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position).zoom(16).bearing(0)
                .tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
