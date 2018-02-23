package com.zeach.ofirmonis.zeach.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.firebase.database.FirebaseDatabase;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Activities.MainActivity;
import com.zeach.ofirmonis.zeach.Activities.ProfileActivity;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class PreferencesFragment extends Fragment implements View.OnClickListener {
    Button btn;
    private CheckBox importFacebookFriendsCheckbox;
    private CheckBox isUserPrivate;
    private User ZeachUser;
    private Button SaveButton;
    private static final String ACTION_UPDATE_USER_PREFERENCES = "update_user_preferences";

    private BroadcastReceiver mPreferencesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);
        this.ZeachUser = AppController.getInstance().getUser();
        this.btn = (Button) rootView.findViewById(R.id.button3s);
        this.SaveButton = (Button) rootView.findViewById(R.id.save_button);
        this.btn.setOnClickListener(this);
        this.SaveButton.setOnClickListener(this);
        this.importFacebookFriendsCheckbox = (CheckBox) rootView.findViewById(R.id.importFriendsFromFacebookCheckbox);
        this.isUserPrivate = (CheckBox) rootView.findViewById(R.id.HideFromFriends);
        if (this.ZeachUser.getFacebookUID() == null) {
            Log.d("is clickable", "false");
            this.importFacebookFriendsCheckbox.setClickable(false);
            this.importFacebookFriendsCheckbox.setText(this.importFacebookFriendsCheckbox.getText() + " (Unavilable)");
        } else {
            this.importFacebookFriendsCheckbox.setChecked(this.ZeachUser.isImportFacebookFriends());
        }

        this.isUserPrivate.setChecked(this.ZeachUser.isProfilePrivate());

        this.isUserPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZeachUser.setProfilePrivate(!ZeachUser.isProfilePrivate());


            }
        });
        this.importFacebookFriendsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZeachUser.setImportFacebookFriends(!ZeachUser.isImportFacebookFriends());
            }
        });
        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public int checkOnWhatActivityUserIs() {
        if (getActivity().getClass().getSimpleName().equals("MainActivity")) {
            return 1;
        } else
            return 0;


    }

    @Override
    public void onClick(View v) {
        if (v == btn) {
            if (checkOnWhatActivityUserIs() == 1) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
            } else if (checkOnWhatActivityUserIs() == 0)
                ((ProfileActivity) getActivity()).setCurrentItem(0);
        }
        if (v == this.SaveButton) {
            AppController.getInstance().setUser(this.ZeachUser);
            if (AppController.getInstance().getUser().isImportFacebookFriends()) {
                AppController.getInstance().getFacebookFriends();

            }
            //TODO - send to receiver
            //  AppController.getInstance().UpdateUserInfo();
            sendBroadcast();
            if (checkOnWhatActivityUserIs() == 1) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();

            } else {
                Intent mainActivity = new Intent(getActivity(), MainActivity.class);
                startActivity(mainActivity);
                getActivity().finish();
            }
        }


    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_USER_PREFERENCES);
        intent.putExtra("add_facebook_friends", importFacebookFriendsCheckbox.isChecked());
        intent.putExtra("private_profile", isUserPrivate.isChecked());
        getContext().sendBroadcast(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    //Update User datails when leaving fragment
    @Override
    public void onDetach() {
        Log.d("nir", "nir1222");
        //check if needed because there is a save button
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPreferencesReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_USER_PREFERENCES);
            getContext().registerReceiver(mPreferencesReceiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        getContext().unregisterReceiver(mPreferencesReceiver);
        super.onPause();
    }
}
