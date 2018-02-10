package com.zeach.ofirmonis.zeach.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zeach.ofirmonis.zeach.Adapters.FavoriteBeachesAdapter;
import com.zeach.ofirmonis.zeach.Adapters.FriendListAdapter;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.FavoriteBeach;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.GraphRequest.TAG;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class FavoriteBeachesFragment extends Fragment {
    private FavoriteBeachesAdapter favoriteBeachesAdapter;
    private static final String TAG = FavoriteBeachesFragment.class.getSimpleName();
    ArrayList mBeaches = new ArrayList();
    private ArrayList<FavoriteBeach> mFavoriteBeaches = new ArrayList<>();
    private ListView beachListView;
    private DatabaseReference data;
    private View rootView;
    private User mUser;
    private static final String ACTION_REQUEST_FAVORITE_BEACHES = "request_favorite_beaches";
    private static final String ACTION_RECEIVE_FAVORITE_BEACHES = "receive_favorite";
    private BroadcastReceiver mFavoriteBeachesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "received");
            switch (intent.getAction()) {
                case ACTION_RECEIVE_FAVORITE_BEACHES: {
                    Log.i(TAG, "Received user favorite beaches");
                    Gson gson = new Gson();
                    String favoriteBeachString = intent.getStringExtra("favorite_beaches");
                    Type type = new TypeToken<ArrayList<FavoriteBeach>>() {
                    }.getType();
                    //   mFavoriteBeaches = gson.fromJson(favoriteBeachString, type);
                    Log.i(TAG, "Favorite beach to add was send " + mFavoriteBeaches);
                    if (mFavoriteBeaches != null) {
                        // setFavoriteBeaches2();
                    }
                    break;
                }

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.favorite_beaches_fragment, container, false);
        this.beachListView = (ListView) rootView.findViewById(R.id.favorite_beach_list);
        mUser = AppController.getInstance().getUser();
        data = FirebaseDatabase.getInstance().getReference("Users/" + this.mUser.getUID() + "/favoriteBeaches/");
        setFavoriteBeaches();
        /*if (mFavoriteBeachesReceiver == null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_RECEIVE_FAVORITE_BEACHES);
            getActivity().registerReceiver(mFavoriteBeachesReceiver, intentFilter);
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_REQUEST_FAVORITE_BEACHES);
        Log.i(TAG, "Requesting favorite user beaches");
        getContext().sendBroadcast(intent);*/
        //  this.data = FirebaseDatabase.getInstance().getReference("Beaches/Country/Israel/");
        //   setFavoriteBeaches();
        return this.rootView;
    }

    public void setFavoriteBeaches2() {
        favoriteBeachesAdapter = new FavoriteBeachesAdapter(getContext(), mFavoriteBeaches);
        beachListView.setAdapter(favoriteBeachesAdapter);
        favoriteBeachesAdapter.notifyDataSetChanged();
    }

    public void setFavoriteBeaches() {
        favoriteBeachesAdapter = new FavoriteBeachesAdapter(getContext(), mFavoriteBeaches);
        this.data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favoriteBeachesAdapter.clear();
                favoriteBeachesAdapter.notifyDataSetChanged();
                for (DataSnapshot beach : dataSnapshot.getChildren()) {
                    //final Friend friend1 = friend.getValue(Friend.class);
                    String mBeachKey = (String) beach.child("mBeachKey").getValue();
                    String mBeachName = (String) beach.child("mBeachName").getValue();
                    FavoriteBeach beachObj = new FavoriteBeach(mBeachName, mBeachKey, "hh");
                    mFavoriteBeaches.add(beachObj);
                    //  Log.d("fgf",friend.toString());
                }


                beachListView.setAdapter(favoriteBeachesAdapter);
                favoriteBeachesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        /*if (mFavoriteBeachesReceiver != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_RECEIVE_FAVORITE_BEACHES);
            getActivity().registerReceiver(mFavoriteBeachesReceiver, intentFilter);
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_REQUEST_FAVORITE_BEACHES);
        Log.i(TAG, "Requesting favorite user beaches");
        getContext().sendBroadcast(intent);*/
        super.onResume();
    }
}
