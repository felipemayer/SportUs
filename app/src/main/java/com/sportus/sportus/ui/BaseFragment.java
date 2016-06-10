package com.sportus.sportus.ui;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;
import com.sportus.sportus.R;

public class BaseFragment extends Fragment {
    ProgressDialog dialog;

    protected void changeToolbar(String title){
        ImageView toolbarImage  = (ImageView) (getActivity()).findViewById(R.id.logo_toolbar);
        toolbarImage.setVisibility(View.GONE);
        Button buttonLogin  = (Button) (getActivity()).findViewById(R.id.buttonLogin);
        buttonLogin.setVisibility(View.GONE);
        Button buttonLogout  = (Button) (getActivity()).findViewById(R.id.buttonLogout);
        buttonLogout.setVisibility(View.GONE);
        Toolbar toolbar  = (Toolbar) (getActivity()).findViewById(R.id.tool_bar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
    }

    public void showDialog(String message) {
        dialog = ProgressDialog.show(getActivity(), null, message, false, true);
        dialog.setCancelable(false);
    }

    public void closeDialog() {
        dialog.dismiss();
    }

    public boolean isLoggedIn(FirebaseUser user) {
        return user != null;
    }
}
