package com.sportus.sportus.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sportus.sportus.R;
import com.sportus.sportus.data.Events;

public class EventDetailsFragment extends Fragment {
    int mEventId;
    String mEventName;
    String mEventLevel;
    String mEventAddress;
    String mEventDate;
    String mEventHour;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int index = getArguments().getInt(EventsFragment.KEY_EVENT_INDEX);
        View view = inflater.inflate(R.layout.event_details_fragment, container, false);
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
}
