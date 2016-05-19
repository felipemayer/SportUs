package com.sportus.sportus.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sportus.sportus.R;
import com.sportus.sportus.data.DbHelper;
import com.sportus.sportus.data.EventData;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName() ;
    EventData mEvenData;
    DbHelper dbHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int index = getArguments().getInt(EventsFragment.KEY_EVENT_INDEX);
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        dbHelper = DbHelper.getInstance(getActivity().getApplicationContext());
        mEvenData = dbHelper.getEventById(index);
        TextView mEventName = (TextView) view.findViewById(R.id.eventNameProfileTemporario);
        mEventName.setText(mEvenData.title);
        TextView mEventType = (TextView) view.findViewById(R.id.eventTypeProfileTemporario);
        mEventType.setText(mEvenData.type);
        TextView mEventDate = (TextView) view.findViewById(R.id.eventTimeProfileTemporario);
        mEventDate.setText(mEvenData.date);
        TextView mEventTime = (TextView) view.findViewById(R.id.eventDateProfileTemporario);
        mEventTime.setText(mEvenData.time);
        TextView mEventCost = (TextView) view.findViewById(R.id.eventCostProfileTemporario);
        mEventCost.setText(mEvenData.cost);
        Log.d(TAG, String.valueOf(mEvenData.cost));


        return view;
    }
}
