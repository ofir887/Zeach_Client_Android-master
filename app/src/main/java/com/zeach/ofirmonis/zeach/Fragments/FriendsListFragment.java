package com.zeach.ofirmonis.zeach.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.zeach.ofirmonis.zeach.Constants.IntentExtras;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Singletons.MapSingleton;
import com.zeach.ofirmonis.zeach.interfaces.FriendsListener;

import java.util.ArrayList;

import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_DELETE_FRIEND;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_REQUEST_USER;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_USER;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.FRIENDS_LIST;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.USERS;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class FriendsListFragment extends Fragment implements View.OnClickListener, FriendsListener {
    private static final String TAG = FriendsListFragment.class.getSimpleName();
    private View rootView;
    private User ZeachUser = new User();
    private ArrayList friends = new ArrayList();
    private FriendListAdapter friendListAdapter;
    private ListView friendsListView;
    private DatabaseReference data;

    private BroadcastReceiver mFriendsReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_USER: {
                    User user = (User) intent.getSerializableExtra(IntentExtras.USER);
                    Log.d(TAG, String.format("User received:[%s]", user.toString()));
                    ZeachUser = user;
                    data = FirebaseDatabase.getInstance().getReference(String.format("%s/%s/%s", USERS, ZeachUser.getUID(), FRIENDS_LIST));
                    getFriendsFromServer();
                    MapSingleton.getInstance().updateUser(user, true);
                    break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.friends_list_fragment, container, false);
        this.friendsListView = (ListView) rootView.findViewById(R.id.friends_list);
        return this.rootView;
    }

    public void getFriendsFromServer() {
        friendListAdapter = new FriendListAdapter(getContext(), friends, this);
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
    public void onResume() {
        super.onResume();
        this.data = FirebaseDatabase.getInstance().getReference(String.format("%s/%s/%s", USERS, ZeachUser.getUID(), FRIENDS_LIST));
        ZeachUser = new User();
        if (mFriendsReciever != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_USER);
            intentFilter.addAction(ACTION_REQUEST_USER);
            intentFilter.addAction(ACTION_DELETE_FRIEND);
            getActivity().registerReceiver(mFriendsReciever, intentFilter);
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_REQUEST_USER);
        getContext().sendBroadcast(intent);
    }

    @Override
    public void onPause() {
        getContext().unregisterReceiver(mFriendsReciever);
        super.onPause();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "Detach");
        super.onDetach();
    }

    @Override
    public void onFriendRemoved(String aFriendUid) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DELETE_FRIEND);
        intent.putExtra(IntentExtras.UID, aFriendUid);
        getContext().sendBroadcast(intent);
        MapSingleton.getInstance().getmUser().getFriendsList().remove(aFriendUid);
    }
}
