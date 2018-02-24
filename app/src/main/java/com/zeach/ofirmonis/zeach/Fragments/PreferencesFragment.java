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

import com.zeach.ofirmonis.zeach.Singletons.AppController;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Singletons.MapSingleton;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class PreferencesFragment extends Fragment implements View.OnClickListener {
    private Button mReturnButton;
    private static final String TAG = PreferencesFragment.class.getSimpleName();
    private CheckBox importFacebookFriendsCheckbox;
    private CheckBox isUserPrivate;
    private User mUser;
    private Button mSaveButton;
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
        mUser = AppController.getInstance().getUser();
        mReturnButton = rootView.findViewById(R.id.return_button);
        mSaveButton = rootView.findViewById(R.id.save_button);
        mReturnButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        importFacebookFriendsCheckbox = rootView.findViewById(R.id.importFriendsFromFacebookCheckbox);
        isUserPrivate = rootView.findViewById(R.id.HideFromFriends);
        if (mUser.getFacebookUID() == null || mUser.getFacebookUID().isEmpty()) {
            Log.d(TAG, "false");
            importFacebookFriendsCheckbox.setClickable(false);
            importFacebookFriendsCheckbox.setText(this.importFacebookFriendsCheckbox.getText() + " (Unavilable)");
        } else {
            Log.d(TAG, "true");
            importFacebookFriendsCheckbox.setChecked(this.mUser.isImportFacebookFriends());
            importFacebookFriendsCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUser.setImportFacebookFriends(!mUser.isImportFacebookFriends());
                }
            });
        }

        this.isUserPrivate.setChecked(this.mUser.isProfilePrivate());

        this.isUserPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.setProfilePrivate(!mUser.isProfilePrivate());


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

    @Override
    public void onClick(View v) {
        if (v == mReturnButton) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
        }
        if (v == this.mSaveButton) {
            Log.i(TAG, "clicked");
            AppController.getInstance().setUser(this.mUser);
            if (AppController.getInstance().getUser().isImportFacebookFriends()) {
                AppController.getInstance().getFacebookFriends();

            }
            MapSingleton.getInstance().updateUser(mUser);
            sendBroadcast();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
            onDestroy();
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


    @Override
    public void onDetach() {
        Log.d(TAG, "Screen detach");
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
