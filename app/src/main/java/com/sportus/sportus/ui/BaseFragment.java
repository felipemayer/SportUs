package com.sportus.sportus.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

    public void openProfileFragment(final Fragment fragment, String index) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .addToBackStack(null)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putString(ProfileFragment.PROFILE_INDEX, index);
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
        fragment.setArguments(bundle);
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
}
