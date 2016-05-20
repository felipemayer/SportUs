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
import com.sportus.sportus.data.DbHelper;
import com.sportus.sportus.data.EventData;
import com.sportus.sportus.data.Events;

public class EventDetailsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = EventDetailsFragment.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    EventData mEvenData;
    DbHelper dbHelper;

    int mEventId;
    String mEventName;
    String mEventLevel;
    String mEventAddress;
    String mEventDate;
    String mEventHour;
    boolean mEventPayMethod;
    String mEventCost;
    int mEventIcon;

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
        LatLng position = new LatLng(Events.eventLatitude[0], Events.eventLongitude[0]);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(Events.eventNames[0])
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_home)));
        marker.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position).zoom(16).bearing(0)
                .tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        dbHelper = DbHelper.getInstance(getActivity().getApplicationContext());
        mEvenData = dbHelper.getEventById(index);

        /*mEventId = Events.eventIds[index];
        mEventName = Events.eventNames[index];
        mEventAddress = Events.eventAddress[index];
        mEventLevel = Events.eventLevels[index];
        mEventDate = Events.eventDate[index];
        mEventHour = Events.eventTime[index];
        mEventPayMethod = Events.eventPayMethod[index];
        mEventCost = Events.eventCost[index];
        mEventIcon = Events.eventIcon[index];*/

        TextView  eventName = (TextView) view.findViewById(R.id.eventName);
        eventName.setText(mEvenData.title);
        /*TextView eventAddress = (TextView) view.findViewById(R.id.eventAddress);
        eventAddress.setText("Local: " + mEventAddress);
        ImageView eventIcon = (ImageView) view.findViewById(R.id.eventIcon);
        eventIcon.setImageResource(mEventIcon);
        TextView eventDate = (TextView) view.findViewById(R.id.eventDate);
        eventDate.setText("Data: " + mEventDate);
        TextView eventHour = (TextView) view.findViewById(R.id.eventHour);
        eventHour.setText("Horário: " + mEventHour);
        TextView eventCost = (TextView) view.findViewById(R.id.eventCost);
        if(mEventPayMethod){
            eventCost.setText("Preço: " + mEventCost);
        } else{
            eventCost.setVisibility(View.GONE);
        }*/
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
