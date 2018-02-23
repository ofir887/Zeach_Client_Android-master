package com.zeach.ofirmonis.zeach.Fragments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zeach.ofirmonis.zeach.Activities.MainActivity;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Constants.FirebaseConstants;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Objects.User;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class SignInFragment extends Fragment implements View.OnClickListener{
    private CallbackManager callbackManager;
    private DatabaseReference data;
    private FirebaseAuth mAuth;
    private View rootView;
    private Button SignInButton;
    private TextView EmailTextView;
    private TextView PasswordTextView;

    private com.zeach.ofirmonis.zeach.Objects.User User;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView  =inflater.inflate(R.layout.signin_fragment,container,false);

        SignInButton = (Button)this.rootView.findViewById(R.id.signin_button);
        EmailTextView = (TextView)this.rootView.findViewById(R.id.email_textfield);
        PasswordTextView = (TextView)this.rootView.findViewById(R.id.password_textfield);
        this.data = FirebaseDatabase.getInstance().getReference();
        SignInButton.setOnClickListener(this);



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
        callbackManager.onActivityResult(requestCode,resultCode,data);
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
                            User.setEmail(user.getEmail());
                            User.setUID(user.getUid());
                            User.setProvider(user.getProviderData().get(0).toString());
                            SendUserAndMoveToProfileActivity();
                        } else {

                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == SignInButton){
            String email = this.EmailTextView.getText().toString();
            String password = this.PasswordTextView.getText().toString();
            createNewFirebaseUser(email,password);


        }
    }
    public void SendUserAndMoveToProfileActivity(){
        this.data = FirebaseDatabase.getInstance().getReference();
      //  User.AddFriendToList("hgg","ofir");
       // User.AddFriendToList("hggfdf","ofihr");
        Map<String, com.zeach.ofirmonis.zeach.Objects.User> user = new HashMap<String, com.zeach.ofirmonis.zeach.Objects.User>();
       // user.put(this.User.getUID(),this.User);
        User.setProfilePictureUri("/PersonIcon.png");
        data.child(FirebaseConstants.USERS).child(this.User.getUID()).setValue(this.User);
        AppController.getInstance().setUser(this.User); // save user in singleton
        //Add seperate parent ! need to check if this is good or can out this on Users in nested map
        //data.child("Users").child(this.User.getUID()).child("Friends").push().child("ofir");
        Intent profileActivity = new Intent(getActivity(), MainActivity.class);
        profileActivity.putExtra("map", false);
        getActivity().finish();
        startActivity(profileActivity);
    }

    public void createNewFirebaseUser(String email,String password){
        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            User = new User(user.getEmail(), user.getUid(), user.getProviderId());
                            SendUserAndMoveToProfileActivity();
                        } else {

                        }

                        // ...
                    }
                });
    }

}
