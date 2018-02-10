package com.zeach.ofirmonis.zeach.Adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.R;

import java.util.ArrayList;


public class FriendsRequestsListAdapter extends ArrayAdapter<Friend> {

    private ArrayList<Friend> friends = new ArrayList<>();
    private String UserId;
    private static final String ACTION_CONFIRM_FRIEND = "confirmFriend";
    private static FirebaseStorage mStorage;
    private static StorageReference mStorageRef;
    private BroadcastReceiver adapterReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public FriendsRequestsListAdapter(Context context, ArrayList<Friend> friends, String userId, FragmentActivity activity) {
        super(context, 0, friends);
        this.friends = friends;
        this.UserId = userId;
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        if (adapterReceiver != null) {
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
            IntentFilter intentFilter = new IntentFilter(ACTION_CONFIRM_FRIEND);
            //  intentFilter.addAction(ACTION_DELETE_FRIEND);
            //Map the intent filter to the receiver
            context.registerReceiver(adapterReceiver, intentFilter);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_row, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            holder.userPhoto = (ImageView) convertView.findViewById(R.id.User_circle_photo);
            holder.Confirm = (Button) convertView.findViewById(R.id.add_user_as_friend);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (friends.get(position).getName() != null)
            holder.userName.setText(friends.get(position).getName());
        if (friends.get(position).getPhotoUrl() != null) {
            holder.userPhoto.setImageURI(Uri.parse(friends.get(position).getPhotoUrl()));
            //   mStorageRef = mStorage.getReference(friends.get(position).getPhotoUrl());
            //   Glide.with(getContext()).using(new FirebaseImageLoader()).load(mStorageRef).into(holder.userPhoto);
        }
        holder.Confirm.setText("Confirm");
        holder.Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked", friends.get(position).getName());
                Friend friend = new Friend(friends.get(position).getName(), friends.get(position).getUID(), friends.get(position).getPhotoUrl());
                //  AppController.getInstance().AddFriendRequest(UserId,friend);
                Intent intent = new Intent();
                intent.setAction(ACTION_CONFIRM_FRIEND);
                Gson gson = new Gson();
                String friendJson = gson.toJson(friend);
                intent.putExtra("Friend", friendJson);
                getContext().sendBroadcast(intent);

            }
        });


        return convertView;
    }


    static class ViewHolder {
        ImageView userPhoto;
        TextView userName;
        Button Confirm;
        Button Remove;

    }


}
