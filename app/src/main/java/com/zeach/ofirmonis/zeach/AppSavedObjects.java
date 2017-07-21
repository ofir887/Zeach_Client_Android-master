package com.zeach.ofirmonis.zeach;

import android.util.Log;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ofirmonis on 18/07/2017.
 */

public class AppSavedObjects {
    private static AppSavedObjects mInstance= null;

    public UserNew User;

    protected AppSavedObjects(){}

    public UserNew getUser() {
        return User;
    }

    public void setUser(UserNew user) {
        User = user;
    }

    public static synchronized AppSavedObjects getInstance(){
        if(null == mInstance){
            mInstance = new AppSavedObjects();
        }
        return mInstance;
    }
    public void UpdateUserInfo(){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference();
      //  AppSavedObjects.getInstance().setUser(this.ZeachUser);
       // Log.d("singleton",AppSavedObjects.getInstance().getUser().toString());
        Map<String,UserNew> user = new HashMap<String,UserNew>();
        user.put(this.User.getUID(),this.User);
        data.child("Users").child(this.User.getUID()).setValue(this.User);
        //Add seperate parent ! need to check if this is good or can out this on Users in nested map
        //data.child("Users").child(this.User.getUID()).child("Friends").push().child("ofir");
        /*
        Intent profileActivity = new Intent(getActivity(),ProfileActivity.class);
        profileActivity.putExtra("User",User);
        getActivity().finish();
        startActivity(profileActivity);*/
    }
    //add friend to awaiting aproval.
    public void AddFriendRequest(String userId,Friend friend){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        //create awaiting confirmation on current user
        data.child("Users").child(this.User.getUID()).child("AwaitngConfirmation").child(friend.getUID()).setValue(friend);
        //create awaiting confirmation on current user
        Friend destinationFriend = new Friend(this.User.getName(),this.User.getUID(),this.User.getProfilePictureUri());
        data.child("Users").child(friend.getUID()).child("FriendsRequset").child(this.User.getUID()).setValue(destinationFriend);
    }
    public void getFacebookFriends(){
        //get friends list
        final DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+ this.User.getFacebookUID()+"/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject json = response.getJSONObject();
                        try {
                            final JSONArray data1 = json.getJSONArray("data");
                            final String[] uid = new String[1];
                            for (int i=0; i < data1.length(); i++){
                                DatabaseReference searchUserId = data.getDatabase().getReference();
                                Query UserId = searchUserId.child("Users").orderByChild("facebookUID").equalTo(data1.getJSONObject(i).getString("id"));
                                final int finalI = i;
                                UserId.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        Log.d("found",dataSnapshot.toString());
                                        UserNew desired = dataSnapshot.getValue(UserNew.class);
                                     //   try {
                                            User.AddFriendToList(desired.getUID(),desired.getName(),desired.getProfilePictureUri(),desired.getCurrentBeach());
                                      //  } catch (JSONException e) {
                                        //    e.printStackTrace();
                                     //   }


                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            //    ZeachUser.AddFriendToList(String.valueOf(uid[0]),data1.getJSONObject(i).getString("name"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //  Log.d("friends app",response.toString());

            /* handle the result */
                    }
                }
        ).executeAsync();
        //
    }
}
