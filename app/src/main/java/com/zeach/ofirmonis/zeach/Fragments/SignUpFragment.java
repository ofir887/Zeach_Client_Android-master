package com.zeach.ofirmonis.zeach.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class SignUpFragment extends Fragment implements View.OnClickListener {
    private CallbackManager callbackManager;
    private DatabaseReference data;
    private FirebaseAuth mAuth;
    private View rootView;
    private Button SignUpButton;
    private TextView EmailTextView;
    private TextView PasswordTextView;

    private com.zeach.ofirmonis.zeach.Objects.User User;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.signup_fragment, container, false);

        SignUpButton = (Button) this.rootView.findViewById(R.id.signup_button);
        EmailTextView = (TextView) this.rootView.findViewById(R.id.email_textfield);
        PasswordTextView = (TextView) this.rootView.findViewById(R.id.password_textfield);
        this.data = FirebaseDatabase.getInstance().getReference();
        SignUpButton.setOnClickListener(this);


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


    @Override
    public void onClick(View v) {
        if (v == SignUpButton) {
            String email = this.EmailTextView.getText().toString();
            String password = this.PasswordTextView.getText().toString();
            createNewFirebaseUser(email, password);
        }
    }

    public void SendUserAndMoveToProfileFragment() {
        this.data = FirebaseDatabase.getInstance().getReference();
        Map<String, com.zeach.ofirmonis.zeach.Objects.User> user = new HashMap<String, com.zeach.ofirmonis.zeach.Objects.User>();
        User.setProfilePictureUri("/PersonIcon.png");
        data.child(FirebaseConstants.USERS).child(this.User.getUID()).setValue(this.User);
        AppController.getInstance().setUser(this.User); // save user in singleton
        Intent profileActivity = new Intent(getActivity(), MainActivity.class);
        profileActivity.putExtra("map", false);
        getActivity().finish();
        startActivity(profileActivity);
    }

    public void createNewFirebaseUser(String email, String password) {
        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        AlertDialog.Builder signUpDialog = new AlertDialog.Builder(getContext());
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            User = new User(user.getEmail(), user.getUid(), user.getProviderId());
                            signUpDialog.setTitle("SignIn Completed");
                            signUpDialog.setMessage("Success !");
                            signUpDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    SendUserAndMoveToProfileFragment();
                                }
                            });
                        } else {
                            signUpDialog.setTitle("SignIn Failed");
                            signUpDialog.setMessage("Failed. Try again !");
                            signUpDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                        }
                        signUpDialog.show();
                    }
                });
    }

}
