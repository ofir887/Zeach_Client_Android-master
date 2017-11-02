package com.zeach.ofirmonis.zeach.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeach.ofirmonis.zeach.Adapters.FriendListAdapter;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.ZeachUser;
import com.zeach.ofirmonis.zeach.R;

import java.util.ArrayList;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class FriendsListFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private com.zeach.ofirmonis.zeach.Objects.ZeachUser ZeachUser;
    private ArrayList friends = new ArrayList();
    private FriendListAdapter friendListAdapter;
    private ListView friendsListView;
    private DatabaseReference data;
    //
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_USER = "User";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    private BroadcastReceiver mFriendsReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_USER: {
                    Log.d(MapFragment.class.getSimpleName(), "lets see new");
                    ZeachUser user = (ZeachUser) intent.getSerializableExtra("User");
                    Log.d(MapFragment.class.getSimpleName(), user.toString());
                    break;
                }
            }
        }
    };

    private void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_SERVICE);
        getActivity().sendBroadcast(new_intent);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.friends_list_fragment, container, false);
        this.friendsListView = (ListView) rootView.findViewById(R.id.friends_list);
        this.ZeachUser = AppController.getInstance().getUser();
        this.data = FirebaseDatabase.getInstance().getReference("Users/" + this.ZeachUser.getUID() + "/friendsList/");
        String s = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("user", "defaultStringIfNothingFound");
        Log.d("listfragment", s);
        //
        /*if (mFriendsReciever!= null) {
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            intentFilter.addAction(ACTION_USER);
            //Map the intent filter to the receiver
            getActivity().registerReceiver(mFriendsReciever, intentFilter);
        }*/
        //

        getFriendsFromServer();


        return this.rootView;
    }

    public void getFriendsFromServer() {
        friendListAdapter = new FriendListAdapter(getContext(), friends, getActivity());
        this.data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendListAdapter.clear();

                friendListAdapter.notifyDataSetChanged();
                for (DataSnapshot friend : dataSnapshot.getChildren()) {
                    final Friend friend1 = friend.getValue(Friend.class);

                    friends.add(friend1);
                    //  Log.d("fgf",friend.toString());
                }


                friendsListView.setAdapter(friendListAdapter);
                friendListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible())
            if (!isVisibleToUser) {
                Log.d("not", "visible anymore");
            }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onClick(View v) {


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDetach() {
        Log.d("nir", "nir1222");

        super.onDetach();
    }
}
