package com.zeach.ofirmonis.zeach.Fragments;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zeach.ofirmonis.zeach.Activities.MainActivity;
import com.zeach.ofirmonis.zeach.AppSavedObjects;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.ZeachUser;
import com.zeach.ofirmonis.zeach.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.GraphRequest.TAG;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private FirebaseAuth mAuth;
    private DatabaseReference data;
    private View rootView;
    private Button FirebaseLoginButton;
    private TextView EmailTextView;
    private TextView PasswordTextView;
    private com.zeach.ofirmonis.zeach.Objects.ZeachUser ZeachUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.login_fragment, container, false);
        loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        FirebaseLoginButton = (Button) this.rootView.findViewById(R.id.firebase_login_button);
        this.data = FirebaseDatabase.getInstance().getReference();
        EmailTextView = (TextView) this.rootView.findViewById(R.id.email_login_textfield);
        PasswordTextView = (TextView) this.rootView.findViewById(R.id.password_login_textfield);
        FirebaseLoginButton.setOnClickListener(this);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("ok", "yesss" + loginResult.getAccessToken().getToken().toString());
                Profile profile = Profile.getCurrentProfile();
                //image = Profile.getCurrentProfile().getProfilePictureUri(200,200);
                Log.d("Profile", profile.getName() + " " + profile.getId());
                //need to check if user exsits if no create one !!!
                //handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("ok", "no");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("ok", "no");
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance(); //sign in

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        // Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //data.child("Users").child(user.getUid()).
                            //  updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());

                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == FirebaseLoginButton) {
            String email = this.EmailTextView.getText().toString();
            String password = this.PasswordTextView.getText().toString();
            loginWithFireBaseAccount(email, password);
        }
    }

    public void loginWithFireBaseAccount(String email, String password) {
        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            DatabaseReference ref = data.getDatabase().getReference("Users/" + mAuth.getCurrentUser().getUid());
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "signInWithEmail:success " + user.getEmail().toString());
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(mainActivity);
                            getActivity().finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    /*public void getBeachesFromFirebase() {
        DatabaseReference ref = data.getDatabase().getReference("Beaches/Country/Israel");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot beach : dataSnapshot.getChildren()) {
                    String mBeachKey = (String) beach.child("BeachID").getValue();
                    String mBeachName = (String) beach.child("BeachName").getValue();
                    String mBeachListenerID = (String) beach.child("BeachListenerID").getValue();
                    long currentOccupationEstimation = (long) beach.child("Result").getValue();
                    // int beachMaxCapacity = (int)beach.child("Capacity").getValue();

                    HashMap<String, HashMap<String, Double>> mBeachCoords = (HashMap<String, HashMap<String, Double>>)
                            beach.child("Coords").getValue();
                    ArrayList<LatLng> beachCoords = new ArrayList<LatLng>();
                    for (Map.Entry<String, HashMap<String, Double>> entry : mBeachCoords.entrySet()) {
                        HashMap<String, Double> coords = entry.getValue();
                        LatLng latlng = new LatLng(coords.get("lat"), coords.get("lng"));
                        beachCoords.add(latlng);
                        Log.d("Beach1", latlng.toString());
                    }
                    final Beach beach1 = new Beach(mBeachKey, mBeachListenerID, currentOccupationEstimation, beachCoords, mBeachName);
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Beach1", beach1.toString());
                        }
                    };
                    handler.postDelayed(runnable, 1000);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference ref = data.getDatabase().getReference("Users/" + mAuth.getCurrentUser().getUid());
            Log.d("ofofo", "ggg");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ZeachUser = dataSnapshot.getValue(ZeachUser.class);
                    AppSavedObjects.getInstance().setUser(ZeachUser);
//                                              Log.d("ofofo",ZeachUser.toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            //this.ZeachUser = new User()

            //getBeachesFromFirebase();
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
            getActivity().finish();
/*
            Intent profileActivity = new Intent(getActivity(),ProfileActivity.class);
            getActivity().finish();
            startActivity(profileActivity);
            getActivity().finish();*/

        }
    }

}
