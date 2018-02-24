package com.zeach.ofirmonis.zeach.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.database.ValueEventListener;
import com.zeach.ofirmonis.zeach.Activities.MainActivity;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Constants.FirebaseConstants;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;

import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = LoginFragment.class.getSimpleName();

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private FirebaseAuth mAuth;
    private DatabaseReference data;
    private View rootView;
    private Button FirebaseLoginButton;
    private EditText mEmailTextField;
    private EditText mPasswordTextField;
    private User mZeachUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.login_fragment, container, false);
        loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        FirebaseLoginButton = (Button) this.rootView.findViewById(R.id.firebase_login_button);
        data = FirebaseDatabase.getInstance().getReference();
        mEmailTextField = this.rootView.findViewById(R.id.email_login_textfield);
        mPasswordTextField = this.rootView.findViewById(R.id.password_login_textfield);
        FirebaseLoginButton.setOnClickListener(this);
        loginButton.setReadPermissions("email", "public_profile", "user_birthday", "user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "yesss" + loginResult.getAccessToken().getToken().toString());
                Profile profile = Profile.getCurrentProfile();
                mZeachUser = new User(profile.getName(), null, null, null, profile.getProfilePictureUri(350, 350).toString(), profile.getId());
                Log.i(TAG, profile.getName() + " " + profile.getId());

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "OnError");
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
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = mAuth.getCurrentUser();
                            data = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference searchId = data.getDatabase().getReference();
                            searchId.child(FirebaseConstants.USERS).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Log.i(TAG, "User exists. Sending to Map fragment" + dataSnapshot.getValue());
                                        mZeachUser = dataSnapshot.getValue(User.class);
                                        SendUserAndMoveToMap();
                                    } else {
                                        Log.i(TAG, "User not exists. Sending to profile fragment");
                                        mZeachUser.setEmail(user.getEmail());
                                        mZeachUser.setUID(user.getUid());
                                        mZeachUser.setProvider(user.getProviderData().get(0).toString());
                                        SendUserAndMoveToProfileFragment();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            Log.i(TAG, "Task Failed");
                        }
                    }
                });
    }

    public void SendUserAndMoveToProfileFragment() {
        this.data = FirebaseDatabase.getInstance().getReference();
        data.child(FirebaseConstants.USERS).child(this.mZeachUser.getUID()).setValue(this.mZeachUser);
        startMainActivity(false);
    }

    public void SendUserAndMoveToMap() {
        startMainActivity(true);
    }

    @Override
    public void onClick(View v) {
        if (v == FirebaseLoginButton) {
            String email = this.mEmailTextField.getText().toString();
            String password = this.mPasswordTextField.getText().toString();
            loginWithFireBaseAccount(email, password);
        }
    }

    public void loginWithFireBaseAccount(String email, final String password) {
        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            DatabaseReference ref = data.getDatabase().getReference("Users/" + mAuth.getCurrentUser().getUid());
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.i(TAG, "Success " + user.getEmail().toString());
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    mZeachUser = dataSnapshot.getValue(User.class);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            startMainActivity(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i(TAG, "Failed to login", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder failedLoginDialog = new AlertDialog.Builder(getContext());
                            failedLoginDialog.setTitle("Login Failed !");
                            failedLoginDialog.setMessage("Try Again ");
                            failedLoginDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            failedLoginDialog.show();
                        }
                    }
                });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference ref = data.getDatabase().getReference("Users/" + mAuth.getCurrentUser().getUid());
            Log.i(TAG, "User is defined in this device. login..");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mZeachUser = dataSnapshot.getValue(User.class);
                    AppController.getInstance().setUser(mZeachUser);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            startMainActivity(true);

        }
    }

    private void startMainActivity(boolean aOpenMap) {
        AppController.getInstance().setUser(this.mZeachUser);
        Intent mainActivity = new Intent(getActivity(), MainActivity.class);
        mainActivity.putExtra("map", aOpenMap);
        startActivity(mainActivity);
        getActivity().finish();
    }

}
