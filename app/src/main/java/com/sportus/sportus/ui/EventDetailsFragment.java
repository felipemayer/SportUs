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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sportus.sportus.R;
import com.sportus.sportus.data.DbHelper;
import com.sportus.sportus.data.Event;
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
    String mEventAddressString;
    String mEventDate;
    String mEventHour;
    boolean mEventPayMethod;
    String mEventCost;
    int mEventIcon;

    private TextView mEventTitle;
    private TextView mEventAddress;

    private Double mLatitude;
    private Double mLongitude;

    private GoogleApiClient mGoogleApiClient;
    MapView mMapView;
    private GoogleMap googleMap;

    FirebaseDatabase mDatabase;
    DatabaseReference eventRef;
    private DatabaseReference mUserRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String index = getArguments().getString(EventsFragment.KEY_EVENT_INDEX);
        View view = inflater.inflate(R.layout.event_details_fragment, container, false);

        mEventTitle = (TextView) view.findViewById(R.id.eventName);
        mEventAddress = (TextView) view.findViewById(R.id.eventAddress);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = mDatabase.getReference("events").child(index);
        Log.d(TAG, String.valueOf(eventRef));

        // Read from the database
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Event event = dataSnapshot.getValue(Event.class);

                mEventTitle.setText(event.title);
                mLatitude = event.latitude;
                mLongitude = event.longitude;
                mEventAddress.setText(event.address);

                Log.d(TAG, "mLatitude: " + mLatitude);
                Log.d(TAG, "mLongitude: " + mLongitude);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

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
        Log.d(TAG, "mLatitude 2 : " + mLatitude);
        Log.d(TAG, "mLongitude 2 : " + mLongitude);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(Events.eventNames[0])
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_home)));
        marker.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position).zoom(16).bearing(0)
                .tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        final TextView eventAddress = (TextView) view.findViewById(R.id.eventAddress);

        return view;
    }

        /*dbHelper = DbHelper.getInstance(getActivity().getApplicationContext());
        mEvenData = dbHelper.getEventById(index);*/

        /*mEventId = Events.eventIds[index];
        mEventName = Events.eventNames[index];
        mEventAddress = Events.eventAddress[index];
        mEventLevel = Events.eventLevels[index];
        mEventDate = Events.eventDate[index];
        mEventHour = Events.eventTime[index];
        mEventPayMethod = Events.eventPayMethod[index];
        mEventCost = Events.eventCost[index];
        mEventIcon = Events.eventIcon[index];*/

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
