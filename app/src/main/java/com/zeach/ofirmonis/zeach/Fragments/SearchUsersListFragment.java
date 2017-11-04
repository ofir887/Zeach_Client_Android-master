package com.zeach.ofirmonis.zeach.Fragments;

import android.content.Intent;
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
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Objects.ZeachUser;

import java.util.ArrayList;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class SearchUsersListFragment extends Fragment implements View.OnClickListener,SearchView.OnQueryTextListener{

    private View rootView;
    private com.zeach.ofirmonis.zeach.Objects.ZeachUser ZeachUser;
    private ArrayList<ZeachUser> users = new ArrayList();
    private UserListAdapter userListAdapter;
    private ListView UsersListView;
    private DatabaseReference data;
    private SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView  =inflater.inflate(R.layout.fragment_users_list,container,false);
        this.UsersListView = (ListView)rootView.findViewById(R.id.users_list);
        this.searchView = (SearchView)rootView.findViewById(R.id.users_search_widget);

        this.ZeachUser = AppController.getInstance().getUser();
        this.data = FirebaseDatabase.getInstance().getReference("Users/");
        getUsersFromServer("");
        this.searchView.setOnQueryTextListener(this);
        return this.rootView;
    }

    public void getUsersFromServer(final String str){
        userListAdapter = new UserListAdapter(getContext(),this.users,this.ZeachUser.getUID(),getActivity());

        this.data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userListAdapter.clear();

                userListAdapter.notifyDataSetChanged();
                for(DataSnapshot user: dataSnapshot.getChildren()){
                    if (!user.getKey().equals(ZeachUser.getUID()))
                        if (user.getValue(ZeachUser.class).getName().toLowerCase().contains(str))
                            users.add(user.getValue(ZeachUser.class));
                    //  Log.d("fgf",friend.toString());
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible())
            if (!isVisibleToUser){
                Log.d("not","visible anymore");
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
        Log.d("nir","nir1222");

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
