package com.sportus.sportus.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;
import com.sportus.sportus.R;
import com.sportus.sportus.SignInActivity;
import com.sportus.sportus.data.Event;

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

    public void openDialogLogin() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_to_login);
        dialog.setTitle("Conte mais sobre você");

        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogCancelLogin);
        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogDoLogin);

        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(getActivity(), SignInActivity.class);
                startActivity(i);*/
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    public void openFragment(final Fragment fragment) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.placeholder, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }, 400);

    }

    public void openFragment(final Fragment fragment, String tag) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    public void openFragment(final Fragment fragment, int index) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putInt(EventsFragment.KEY_EVENT_INDEX, index);
        fragment.setArguments(bundle);
    }

    public void openEventFragment(final Fragment fragment, Event event, String eventIndex) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putString(EventDetailsFragment.EVENT_INDEX, eventIndex);
        bundle.putParcelable(EventDetailsFragment.EVENT_OBJECT, event);
        fragment.setArguments(bundle);
    }

    public void openEventFragment(final Fragment fragment, Event event) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EventDetailsFragment.EVENT_OBJECT, event);
        fragment.setArguments(bundle);
    }
}
