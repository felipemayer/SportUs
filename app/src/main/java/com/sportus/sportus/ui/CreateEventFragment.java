package com.sportus.sportus.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sportus.sportus.R;
import com.sportus.sportus.data.DbHelper;
import com.sportus.sportus.data.EventData;

public class CreateEventFragment extends Fragment{
    public static final String TAG = CreateEventFragment.class.getSimpleName();

    DbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.create_events_fragment, container, false);

        dbHelper = DbHelper.getInstance(getActivity().getApplicationContext());

        Button insertButton = (Button) view.findViewById(R.id.buttonEventInput);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventData eventData = new EventData();

                EditText eventTitle = (EditText)view.findViewById(R.id.eventNameInput);
                EditText eventType = (EditText)view.findViewById(R.id.eventTypeInput);

                eventData.title = eventTitle.getText().toString();
                eventData.type = eventType.getText().toString();

                dbHelper.insertEvent(eventData);
                Toast.makeText(getActivity(),"Evento " + eventData.title + " criado com sucesso",Toast.LENGTH_SHORT).show();

                //Log.d(TAG, userData  + " " + EventData.title + " " + EventData.type);
            }
        });

        return view;
    }
}
