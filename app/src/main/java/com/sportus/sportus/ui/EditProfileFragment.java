package com.sportus.sportus.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.Toast;

import com.sportus.sportus.R;

import java.util.ArrayList;
import java.util.List;

public class EditProfileFragment extends BaseFragment {
    private static final String TAG = EditProfileFragment.class.getSimpleName();

    String checkboxs[] = {"Home", "Eventos", "Criar Eventos", "Perfil", "Amigos", "Agenda", "Sobre"};
    List<String> checkeds;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        changeToolbar("Editar Perfil");
        final View view = inflater.inflate(R.layout.edit_profile_fragment, container, false);

        CheckBox checkBox;
        checkeds = new ArrayList<String>();

        for (int i = 0; i < checkboxs.length; i++){
            final int index = i;
            GridLayout parentLayout = (GridLayout) view.findViewById(R.id.gridInterests);
            checkBox = (CheckBox) inflater.inflate(R.layout.template_checkbox_interests, null);
            checkBox.setText(checkboxs[i]);
            checkBox.setId(i);

            parentLayout.addView(checkBox);

            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean checked = ((CheckBox) v).isChecked();
                    if (checked) {
                        Toast.makeText(getActivity(), "-- " + checkboxs[index], Toast.LENGTH_LONG).show();
                        checkeds.add(checkboxs[index]);
                    }
                }
            });



        }

        Button changeProfilePhoto = (Button) view.findViewById(R.id.changeProfilePhoto);
        changeProfilePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(checkeds));
            }
        });



        return view;
    }
}
