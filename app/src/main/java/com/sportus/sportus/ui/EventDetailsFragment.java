package com.sportus.sportus.ui;

import android.app.Dialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sportus.sportus.MainActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.data.Event;
import com.sportus.sportus.data.Participants;
import com.sportus.sportus.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventDetailsFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = EventDetailsFragment.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final String EVENT_INDEX = "index_event";

    TextView mEventTitle;
    TextView mEventAddress;
    TextView mEventDate;
    TextView mEventTime;
    TextView mEventCost;
    TextView mEventAuthor;

    double mEventLatitude;
    double mEventLongitude;
    String eventTitle;

    MenuItem deleteEvent;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference eventRef;
    private DatabaseReference participantsRef;
    FirebaseUser currentUser;
    String currentUserId;

    LinearLayout eventParticipants;
    LinearLayout joinEvent;
    LinearLayout exitEvent;

    String userName;
    String userPhoto;

    ArrayList<String> allParticipants;

    Event event;
    String mEventKey;
    String participantsRefKey;

    private GoogleApiClient mGoogleApiClient;
    MapView mMapView;
    private String eventAuthor;
    private ImageView mEventIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mEventKey = getArguments().getString(EventDetailsFragment.EVENT_INDEX);
        View view = inflater.inflate(R.layout.event_details_fragment, container, false);
        String message = "Carregando...";
        showDialog(message);
        setHasOptionsMenu(true);

        changeToolbar("Evento");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        eventRef = FirebaseDatabase.getInstance().getReference("events").child(mEventKey);
        participantsRef = FirebaseDatabase.getInstance().getReference("participants").child(mEventKey);

        mEventTitle = (TextView) view.findViewById(R.id.eventTitle);
        mEventAuthor = (TextView) view.findViewById(R.id.eventAuthor);
        mEventAddress = (TextView) view.findViewById(R.id.eventAddress);
        mEventDate = (TextView) view.findViewById(R.id.eventDate);
        mEventTime = (TextView) view.findViewById(R.id.eventTime);
        mEventCost = (TextView) view.findViewById(R.id.eventCost);
        mEventIcon = (ImageView) view.findViewById(R.id.eventIcon);

        eventRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        event = dataSnapshot.getValue(Event.class);
                        populateEvent(event);
                        initializeMap();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        allParticipants = new ArrayList<>();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    participantsRefKey = snapshot.getKey();
                    allParticipants.add(String.valueOf(snapshot.child("userId").getValue()));
                }
                if (Objects.equals(currentUserId, eventAuthor)) {
                    exitEvent.setVisibility(View.GONE);
                    joinEvent.setVisibility(View.GONE);
                } else if (allParticipants.contains(currentUserId)) {
                    exitEvent.setVisibility(View.VISIBLE);
                    joinEvent.setVisibility(View.GONE);
                } else {
                    exitEvent.setVisibility(View.GONE);
                    joinEvent.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        participantsRef.addValueEventListener(listener);

        if (currentUserId != null) {
            ValueEventListener listenerUser = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    userName = user.getName();
                    userPhoto = user.getPhoto();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            mDatabaseReference.child("users").child(currentUserId).addValueEventListener(listenerUser);
        }

        mMapView = (MapView) view.findViewById(R.id.mapEventDetail);
        mMapView.onCreate(savedInstanceState);

        joinEvent = (LinearLayout) view.findViewById(R.id.joinButton);
        eventParticipants = (LinearLayout) view.findViewById(R.id.eventParticipants);
        exitEvent = (LinearLayout) view.findViewById(R.id.exitEvent);

        joinEvent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.openDialogLogin();
                } else {
                    if (!allParticipants.contains(currentUserId)) {
                        createNewParticipant(mEventKey);
                        openDialogJoinEvent();
                    } else {
                        Toast.makeText(getActivity(), "Você já confirmou esse evento!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        eventParticipants.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openParticipantsFragment(new ParticipantsFragment(), mEventKey);
            }
        });

        exitEvent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogOutEvent();
            }
        });

        return view;
    }

    private void initializeMap() {
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

        GoogleMap googleMap = mMapView.getMap();
        LatLng position = new LatLng(mEventLatitude, mEventLongitude);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(eventTitle)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_home)));
        marker.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position).zoom(16).bearing(0)
                .tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void populateEvent(Event event) {
        mEventTitle.setText(event.getTitle());
        mEventAuthor.setText("Organizador(a): " + event.getAuthor());
        mEventIcon.setImageResource(setTypeIcon(event.getType()));
        mEventAddress.setText(event.getAddress());
        mEventDate.setText(event.getDate());
        mEventTime.setText(event.getTime());
        if (event.isPayMethod()) {
            mEventCost.setText(event.getCost());
        } else {
            mEventCost.setText("Evento Gratuito");
        }
        mEventLatitude = event.getLatitude();
        mEventLongitude = event.getLongitude();
        eventTitle = event.getTitle();
        eventAuthor = event.getAuthorId();
        if (currentUser != null) {
            if (currentUserId.contains(eventAuthor)) {
                joinEvent.setVisibility(View.GONE);
                deleteEvent.setVisible(true);
            } else {
                joinEvent.setVisibility(View.VISIBLE);
                deleteEvent.setVisible(false);
            }
        } else {
            joinEvent.setVisibility(View.VISIBLE);
            deleteEvent.setVisible(false);
        }
        closeDialog();
    }

    private void createNewParticipant(String eventId) {
        String key = mDatabaseReference.child("participants").child(eventId).push().getKey();
        Participants newParticipant = new Participants(currentUserId, userName, userPhoto);
        Map<String, Object> value = newParticipant.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/participants/" + eventId + "/" + key, value);

        mDatabaseReference.updateChildren(childUpdates);
    }


    @Override
    public void onConnected(Bundle bundle) {

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
        // mGoogleApiClient.connect();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_event_details, menu);
        super.onCreateOptionsMenu(menu, inflater);

        deleteEvent = menu.findItem(R.id.deleteEvent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.deleteEvent) {
            Log.d(TAG, " Delete seu evento!");
            openDialogDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDialogDelete() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_to_delete_event);
        dialog.setTitle("Quer deletar seu evento?");

        Button dialogCancelDelete = (Button) dialog.findViewById(R.id.dialogCancelDelete);
        Button dialogToDelete = (Button) dialog.findViewById(R.id.dialogToDelete);

        dialogCancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogToDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventRef.removeValue();
                dialog.dismiss();
                openFragment(new EventsFragment());
            }
        });
        dialog.show();
    }

    public void openDialogJoinEvent() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_to_join_event);
        dialog.setTitle("Uhuuu, let's play!");

        Button dialogHideDialog = (Button) dialog.findViewById(R.id.dialogHideDialog);
        Button dialogToSeeEvents = (Button) dialog.findViewById(R.id.dialogToSeeEvents);

        dialogHideDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogToSeeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openFragment(new EventsFragment());
            }
        });
        dialog.show();
    }

    public void openDialogOutEvent() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_to_exit_event);
        dialog.setTitle("Mas já vai?");

        Button dialogHideDialog = (Button) dialog.findViewById(R.id.dialogHideDialog);
        Button dialogToGoOutEvents = (Button) dialog.findViewById(R.id.dialogToGoOutEvents);

        dialogHideDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogToGoOutEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantsRef.child(participantsRefKey).removeValue();
                dialog.dismiss();
                openFragment(new EventsFragment());
            }
        });
        dialog.show();
    }
}