package com.zeach.ofirmonis.zeach;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.GraphRequest.TAG;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class PreferencesFragment extends Fragment implements View.OnClickListener{
    Button btn;
    private CheckBox importFacebookFriendsCheckbox;
    private CheckBox isUserPrivate;
    private UserNew ZeachUser;
    private Button SaveButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView  =inflater.inflate(R.layout.fragment_preferences,container,false);
        this.ZeachUser = AppSavedObjects.getInstance().getUser();
        this.btn = (Button)rootView.findViewById(R.id.button3s);
        this.SaveButton = (Button)rootView.findViewById(R.id.save_button);
        this.btn.setOnClickListener(this);
        this.SaveButton.setOnClickListener(this);
        this.importFacebookFriendsCheckbox = (CheckBox)rootView.findViewById(R.id.importFriendsFromFacebookCheckbox);
        this.isUserPrivate = (CheckBox)rootView.findViewById(R.id.HideFromFriends);
        if (this.ZeachUser.getFacebookUID()==null) {
            Log.d("is clickable","false");
            this.importFacebookFriendsCheckbox.setClickable(false);
            this.importFacebookFriendsCheckbox.setText(this.importFacebookFriendsCheckbox.getText() + " (Unavilable)");
        }
        else{
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
    public int checkOnWhatActivityUserIs(){
        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
           return 1;
        }
        else
            return 0;


    }

    @Override
    public void onClick(View v) {
        if(v == btn){
            if (checkOnWhatActivityUserIs() ==1){
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new ProfileFragment()).commit();
            }
            else if (checkOnWhatActivityUserIs() ==0)
                ((ProfileActivity)getActivity()).setCurrentItem(0);
        }
        if (v == this.SaveButton){
            AppSavedObjects.getInstance().setUser(this.ZeachUser);
            if (AppSavedObjects.getInstance().getUser().isImportFacebookFriends()) {
                AppSavedObjects.getInstance().getFacebookFriends();
            }
            AppSavedObjects.getInstance().UpdateUserInfo();
            if (checkOnWhatActivityUserIs()==1){
                /*
                AppSavedObjects.getInstance().setUser(this.ZeachUser);
                if (AppSavedObjects.getInstance().getUser().isImportFacebookFriends()) {
                    AppSavedObjects.getInstance().getFacebookFriends();
                }
                AppSavedObjects.getInstance().UpdateUserInfo();*/
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new MapFragment()).commit();

            }
            else{
                /*
                AppSavedObjects.getInstance().setUser(this.ZeachUser);
                if (AppSavedObjects.getInstance().getUser().isImportFacebookFriends()) {
                    AppSavedObjects.getInstance().getFacebookFriends();
                }
                AppSavedObjects.getInstance().UpdateUserInfo();
                */
                Intent mainActivity = new Intent(getActivity(), MainActivity.class);
                startActivity(mainActivity);
                getActivity().finish();
            }
        }


    }
    @Override
    public void onStart() {
        super.onStart();
    }


    //Update User datails when leaving fragment
    @Override
    public void onDetach() {
        Log.d("nir","nir1222");
        //check if needed because there is a save button
        super.onDetach();
    }
}
