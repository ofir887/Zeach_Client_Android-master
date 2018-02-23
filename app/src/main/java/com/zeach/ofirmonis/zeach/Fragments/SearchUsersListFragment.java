package com.zeach.ofirmonis.zeach.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
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
import com.zeach.ofirmonis.zeach.Adapters.UserListAdapter;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Objects.User;

import java.util.ArrayList;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class SearchUsersListFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = SearchUsersListFragment.class.getSimpleName();
    private View rootView;
    private User ZeachUser;
    private ArrayList<User> users = new ArrayList();
    private UserListAdapter userListAdapter;
    private ListView UsersListView;
    private DatabaseReference data;
    private SearchView searchView;
    private static final String ACTION_USER = "User";
    private static final String ACTION_REQUEST_USER = "request_user";
    private BroadcastReceiver mUserReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_USER: {
                    User user = (User) intent.getSerializableExtra("User");
                    Log.d(TAG, String.format("User received:[%s]", user.toString()));
                    ZeachUser = user;
                    getUsersFromServer("");
                    break;
                }
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_users_list, container, false);
        this.UsersListView = (ListView) rootView.findViewById(R.id.users_list);
        this.searchView = (SearchView) rootView.findViewById(R.id.users_search_widget);
        this.searchView.setOnQueryTextListener(this);
        return this.rootView;
    }

    public void getUsersFromServer(final String str) {
        userListAdapter = new UserListAdapter(getContext(), this.users, this.ZeachUser.getFriendsList());

        this.data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userListAdapter.clear();

                userListAdapter.notifyDataSetChanged();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if (!user.getKey().equals(ZeachUser.getUID()))
                        if (user.getValue(User.class).getName().toLowerCase().contains(str))
                            users.add(user.getValue(User.class));
                }


                UsersListView.setAdapter(userListAdapter);
                userListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.data = FirebaseDatabase.getInstance().getReference("Users/");
        ZeachUser = new User();
        if (mUserReciever != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_USER);
            intentFilter.addAction(ACTION_REQUEST_USER);
            getActivity().registerReceiver(mUserReciever, intentFilter);
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_REQUEST_USER);
        getContext().sendBroadcast(intent);
    }

    @Override
    public void onPause() {
        getContext().unregisterReceiver(mUserReciever);
        super.onPause();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
        Log.d(TAG, "Detach");

        super.onDetach();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getUsersFromServer(newText);
        return false;
    }
}
