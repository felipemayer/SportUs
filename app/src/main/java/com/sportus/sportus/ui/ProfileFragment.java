package com.sportus.sportus.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.sportus.sportus.R;

public class ProfileFragment extends BaseFragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        changeToolbar("Perfil");
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        Button myProfile = (Button) view.findViewById(R.id.editProfile);

        myProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new EditProfileFragment());
            }
        });



        return view;
    }
}
