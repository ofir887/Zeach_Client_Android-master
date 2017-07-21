package com.zeach.ofirmonis.zeach;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class SearchUsersListFragment extends Fragment implements View.OnClickListener,SearchView.OnQueryTextListener{

    private View rootView;
    private ZeachUser ZeachUser;
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

        this.ZeachUser = AppSavedObjects.getInstance().getUser();
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
