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

public class FriendsListFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private ZeachUser ZeachUser;
    private ArrayList friends = new ArrayList();
    private FriendListAdapter friendListAdapter;
    private ListView friendsListView;
    private DatabaseReference data;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView  =inflater.inflate(R.layout.friends_list_fragment,container,false);
        this.friendsListView = (ListView)rootView.findViewById(R.id.friends_list);
        this.ZeachUser = AppSavedObjects.getInstance().getUser();
        this.data = FirebaseDatabase.getInstance().getReference("Users/" + this.ZeachUser.getUID()+"/friendsList/");;
        getFriendsFromServer();
        return this.rootView;
    }
    public void getFriendsFromServer(){
        friendListAdapter = new FriendListAdapter(getContext(),friends,getActivity());
        this.data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendListAdapter.clear();

                friendListAdapter.notifyDataSetChanged();
                for(DataSnapshot friend: dataSnapshot.getChildren()){
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
}
