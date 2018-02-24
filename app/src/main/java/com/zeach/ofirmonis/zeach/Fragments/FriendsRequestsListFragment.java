package com.zeach.ofirmonis.zeach.Fragments;

import android.content.Intent;
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
import com.zeach.ofirmonis.zeach.Adapters.FriendsRequestsListAdapter;
import com.zeach.ofirmonis.zeach.Singletons.AppController;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;

import java.util.ArrayList;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class FriendsRequestsListFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private User ZeachUser;
    private ArrayList<Friend> friendsRequests = new ArrayList();
    private FriendsRequestsListAdapter friendsRequestsListAdapter;
    private ListView UsersListView;
    private DatabaseReference data;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_friends_requests, container, false);
        this.UsersListView = (ListView) rootView.findViewById(R.id.freinds_requests_list);


        this.ZeachUser = AppController.getInstance().getUser();
        this.data = FirebaseDatabase.getInstance().getReference("Users/" + this.ZeachUser.getUID() + "/FriendsRequest/");
        getFriendsRequestsFromServer();
        return this.rootView;
    }

    public void getFriendsRequestsFromServer() {
        this.friendsRequestsListAdapter = new FriendsRequestsListAdapter(getContext(), this.friendsRequests, this.ZeachUser.getUID(), getActivity());
        this.data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsRequests.clear();

                friendsRequestsListAdapter.notifyDataSetChanged();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    friendsRequests.add(user.getValue(Friend.class));
                }


                UsersListView.setAdapter(friendsRequestsListAdapter);
                friendsRequestsListAdapter.notifyDataSetChanged();
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
