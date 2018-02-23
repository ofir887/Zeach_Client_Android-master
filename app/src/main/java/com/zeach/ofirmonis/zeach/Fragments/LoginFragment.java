package com.zeach.ofirmonis.zeach.Fragments;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zeach.ofirmonis.zeach.Activities.MainActivity;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Constants.FirebaseConstants;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;

import org.json.JSONObject;

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
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private View rootView;
    private Button FirebaseLoginButton;
    private TextView EmailTextView;
    private TextView PasswordTextView;
    private User ZeachUser;

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
        loginButton.setReadPermissions("email", "public_profile", "user_birthday", "user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("ok", "yesss" + loginResult.getAccessToken().getToken().toString());
                Profile profile = Profile.getCurrentProfile();
                ZeachUser = new User(profile.getName(), null, null, null, profile.getProfilePictureUri(350, 350).toString(), profile.getId());
                Log.d("Profile", profile.getName() + " " + profile.getId());

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
                //

                handleFacebookAccessToken(loginResult.getAccessToken());
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
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mStorage = FirebaseStorage.getInstance();
                            mStorageRef = mStorage.getReference();
                            ZeachUser.setEmail(user.getEmail());
                            ZeachUser.setUID(user.getUid());
                            ZeachUser.setProvider(user.getProviderData().get(0).toString());
                            SendUserAndMoveToProfileActivity();
                        } else {

                        }
                    }
                });
    }

    public void SendUserAndMoveToProfileActivity() {
        this.data = FirebaseDatabase.getInstance().getReference();
        //  User.AddFriendToList("hgg","ofir");
        // User.AddFriendToList("hggfdf","ofihr");
        Map<String, User> user = new HashMap<String, User>();
        // user.put(this.User.getUID(),this.User);
        data.child(FirebaseConstants.USERS).child(this.ZeachUser.getUID()).setValue(this.ZeachUser);
        AppController.getInstance().setUser(this.ZeachUser); // save user in singleton
        //Add seperate parent ! need to check if this is good or can out this on Users in nested map
        //data.child("Users").child(this.User.getUID()).child("Friends").push().child("ofir");
        Intent profileActivity = new Intent(getActivity(), MainActivity.class);
        profileActivity.putExtra("map", false);
        //  profileActivity.putExtra("User",User);
        getActivity().finish();
        startActivity(profileActivity);
    }

    public void SendUserAndMoveToMap() {
        Intent profileActivity = new Intent(getActivity(), MainActivity.class);
        //  profileActivity.putExtra("User",User);
        getActivity().finish();
        startActivity(profileActivity);
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
                            mainActivity.putExtra("map", true);
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
                    ZeachUser = dataSnapshot.getValue(User.class);
                    //new AppController.DownloadImageTask2().execute("https://graph.facebook.com/10209101466959698/picture?height=200&width=200&migration_overrides=%7Boctober_2012%3Atrue%7D");
                    AppController.getInstance().setUser(ZeachUser);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            //this.ZeachUser = new User()

            //getBeachesFromFirebase();
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            mainActivity.putExtra("map", true);
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
