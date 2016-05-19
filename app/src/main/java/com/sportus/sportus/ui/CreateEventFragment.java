package com.sportus.sportus.ui;


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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sportus.sportus.MainActivity;
import com.sportus.sportus.R;
import com.sportus.sportus.data.DbHelper;
import com.sportus.sportus.data.EventData;

import java.util.Calendar;

public class CreateEventFragment extends Fragment {
    public static final String TAG = CreateEventFragment.class.getSimpleName();

    DbHelper dbHelper;
    EditText mEventTitle;
    Spinner mSpinnerType;
    static EditText mEventDate;
    static EditText mEventTime;
    static boolean mPayMethod;
    static EditText mEventCost;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.create_events_fragment, container, false);

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

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.event_types, android.R.layout.simple_dropdown_item_1line);
        mSpinnerType.setAdapter(adapter);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventData eventData = new EventData();
                eventData.title = mEventTitle.getText().toString();
                eventData.type = mSpinnerType.getSelectedItem().toString();
                eventData.date = mEventDate.getText().toString();
                eventData.time = mEventTime.getText().toString();
                String costFormated = mEventCost.getText().toString();
                eventData.cost = "R$ "+ costFormated;

                dbHelper.insertEvent(eventData);
                int index = dbHelper.getLastID();

                MainActivity activity = (MainActivity) getActivity();
                activity.openFragment(new ProfileFragment(), index);
                Toast.makeText(getActivity(), "LastID: " + eventData.cost, Toast.LENGTH_SHORT).show();

                // Toast.makeText(getActivity(),"Evento " + eventData.time + " criado com sucesso",Toast.LENGTH_SHORT).show();

                //Log.d(TAG, userData  + " " + EventData.title + " " + EventData.type);
            }
        });

        return view;
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
            return new DatePickerDialog(getActivity(), this, year, month, day);
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
            return new TimePickerDialog(getActivity(), this, hour, minute,
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
}
