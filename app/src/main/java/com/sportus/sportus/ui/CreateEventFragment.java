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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sportus.sportus.MainActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.data.DbHelper;
import com.sportus.sportus.data.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateEventFragment extends Fragment {
    public static final String TAG = CreateEventFragment.class.getSimpleName();

    DbHelper dbHelper;
    EditText mEventTitle;
    Spinner mSpinnerType;
    static EditText mEventDate;
    static EditText mEventTime;
    static boolean mPayMethod;
    static EditText mEventCost;
    String mCreateAt;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.create_events_fragment, container, false);
        setupUI(view);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        dbHelper = DbHelper.getInstance(getActivity().getApplicationContext());

        Button insertButton = (Button) view.findViewById(R.id.buttonEventInput);
        mEventTitle = (EditText) view.findViewById(R.id.eventNameInput);
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
                                               /* EventData eventData = new EventData();
                                                if (mEventTitle.getText().toString().length() == 0) {
                                                    Toast.makeText(getActivity(), "Cadê o nome do evento? ", Toast.LENGTH_LONG).show();
                                                } else if (mSpinnerType.getSelectedItem().toString().length() == 0) {
                                                    Toast.makeText(getActivity(), "Cadê o tipo do Evento? ", Toast.LENGTH_LONG).show();
                                                } else if (mEventDate.getText().toString().length() == 0) {
                                                    Toast.makeText(getActivity(), "Cadê a data do evento? ", Toast.LENGTH_LONG).show();
                                                } else {
                                                    eventData.title = mEventTitle.getText().toString();
                                                    eventData.type = mSpinnerType.getSelectedItem().toString();
                                                    eventData.date = mEventDate.getText().toString();
                                                    eventData.time = mEventTime.getText().toString();
                                                    eventData.cost = mEventCost.getText().toString();
                                                    eventData.created_at = mCreateAt;

                                                    long index = dbHelper.insertEvent(eventData);*/

                                                String title = mEventTitle.getText().toString();
                                                String type = mSpinnerType.getSelectedItem().toString();
                                                String address = "Rua";
                                                String date = mEventDate.getText().toString();
                                                String time = mEventTime.getText().toString();
                                                String cost = mEventCost.getText().toString();
                                                boolean payMethod = true;
                                                String keyEvent = createEvent(title, type, address, date, time, cost, payMethod);
                                                MainActivity activity = ((MainActivity) getActivity());
                                                activity.openEventFragment(new EventDetailsFragment(), keyEvent);
                                                Toast.makeText(getActivity(), "keyEvent: " + keyEvent, Toast.LENGTH_SHORT).show();
                                            }
                                        }

        );

        return view;
    }

    public String getCreateAt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
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

    private String createEvent(String title, String type, String address, String date, String time, String cost, boolean payMethod) {
        String key = mDatabase.child("events").push().getKey();
        Event event = new Event(title, type, address, date, time, cost, payMethod);
        Map<String, Object> eventValue = event.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + key, eventValue);

        mDatabase.updateChildren(childUpdates);

        return key;

    }
}
