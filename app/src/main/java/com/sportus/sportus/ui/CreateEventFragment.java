package com.sportus.sportus.ui;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sportus.sportus.Adapters.PlaceAutocompleteAdapter;
import com.sportus.sportus.MainActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.data.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateEventFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = CreateEventFragment.class.getSimpleName();

    EditText mEventTitle;
    EditText mEventAddress;
    Spinner mSpinnerType;
    static EditText mEventDate;
    static EditText mEventTime;
    static boolean mPayMethod;
    static EditText mEventCost;
    String mCreateAt;
    LatLng mEventLatLng;
    Double mLatitude;
    Double mLongitude;

    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;

    private static final LatLngBounds BOUNDS_SAO_PAULO = new LatLngBounds(
            new LatLng(-23.5835221,-46.6636087), new LatLng(-23.5643021,-46.6545937));

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.create_events_fragment, container, false);
        setupUI(view);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                view.findViewById(R.id.eventAddressInput);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, BOUNDS_SAO_PAULO,
                null);
        mAutocompleteView.setAdapter(mAdapter);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference();

        Button insertButton = (Button) view.findViewById(R.id.buttonEventInput);
        mEventTitle = (EditText) view.findViewById(R.id.eventNameInput);
        mEventAddress = (EditText) view.findViewById(R.id.eventAddressInput);
        mSpinnerType = (Spinner) view.findViewById(R.id.eventTypeInput);
        mEventCost = (EditText) view.findViewById(R.id.eventCostInput);
        mEventDate = (EditText) view.findViewById(R.id.eventDateInput);
        mEventDate.setInputType(InputType.TYPE_NULL);
        mEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        mEventTime = (EditText) view.findViewById(R.id.eventHourInput);
        mEventTime.setInputType(InputType.TYPE_NULL);
        mEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
        mCreateAt = getCreateAt();

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.event_types, android.R.layout.simple_dropdown_item_1line);
        mSpinnerType.setAdapter(adapter);

        insertButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (mEventTitle.getText().toString().length() == 0) {
                                                    Toast.makeText(getActivity(), "Cadê o nome do evento? ", Toast.LENGTH_LONG).show();
                                                /*} else if (mSpinnerType.getSelectedItem().toString().length() == 0) {
                                                    Toast.makeText(getActivity(), "Cadê o tipo do Evento? ", Toast.LENGTH_LONG).show();
                                                } else if (mEventDate.getText().toString().length() == 0) {
                                                    Toast.makeText(getActivity(), "Cadê a data do evento? ", Toast.LENGTH_LONG).show();
                                                } else if (mEventTime.getText().toString().length() == 0) {
                                                    Toast.makeText(getActivity(), "Cadê o horário do evento? ", Toast.LENGTH_LONG).show();*/
                                                } else if (mEventCost.getText().toString().length() == 0 && mPayMethod == true) {
                                                    Toast.makeText(getActivity(), "Qual o preço do evento? ", Toast.LENGTH_LONG).show();
                                                } else {
                                                    String author = getEventAuthor();
                                                    String authorId = getEventAuthorId();
                                                    String title = mEventTitle.getText().toString();
                                                    String type = mSpinnerType.getSelectedItem().toString();
                                                    String address = mEventAddress.getText().toString();
                                                    String date = mEventDate.getText().toString();
                                                    String time = mEventTime.getText().toString();
                                                    String cost = mEventCost.getText().toString();
                                                    boolean payMethod = mPayMethod;
                                                    String createdAt = mCreateAt;
                                                    Double latitude = mLatitude;
                                                    Double longitude = mLongitude;

                                                    String keyEvent = createEvent(author, authorId, title, type, address, date, time, cost,
                                                            payMethod, createdAt, latitude, longitude );

                                                    MainActivity activity = ((MainActivity) getActivity());
                                                    activity.openEventFragment(new EventDetailsFragment(),
                                                            new Event(author, authorId, title, type, address, date, time, cost,
                                                                    payMethod, createdAt, latitude, longitude), keyEvent);
                                                }
                                            }
                                        }

        );

        return view;
    }
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private String getEventAuthorId() {
        String authorId = user.getUid();
        return authorId;
    }

    private String getEventAuthor() {
        String authorName = user.getDisplayName();
        return authorName;
    }

    public String getCreateAt() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy_HH:mm:ss");
        String timeNow = sdf.format(new Date());
        return timeNow;
    }

    public static void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioFreeInput:
                if (checked)
                    mPayMethod = false;
                mEventCost.setVisibility(View.INVISIBLE);
                break;
            case R.id.radioPayedInput:
                if (checked)
                    mPayMethod = true;
                mEventCost.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), R.style.DialogTheme, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            mEventDate.setText(day + "/" + month + "/" + year);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), R.style.DialogTheme, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            String curTime = String.format("%02d:%02d", hourOfDay, minute);
            mEventTime.setText(curTime);
        }

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private String createEvent(String author, String authorId, String title, String type, String address, String date, String time, String cost,
                               boolean payMethod, String createdAt, Double latitude, Double longitude) {
        String key = mUserRef.child("events").push().getKey();
        Event event = new Event(author, authorId, title, type, address, date, time, cost, payMethod, createdAt, latitude, longitude);
        Map<String, Object> eventValue = event.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + key, eventValue);

        mUserRef.updateChildren(childUpdates);

        return key;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            mEventLatLng = place.getLatLng();
            mLatitude = mEventLatLng.latitude;
            mLongitude = mEventLatLng.longitude;

            places.release();
        }
    };

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
}
