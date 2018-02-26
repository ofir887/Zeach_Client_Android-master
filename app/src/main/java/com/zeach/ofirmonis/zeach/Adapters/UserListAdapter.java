package com.zeach.ofirmonis.zeach.Adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import com.zeach.ofirmonis.zeach.Constants.IntentExtras;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Objects.User;

import java.util.ArrayList;
import java.util.Map;

import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_ADD_FRIEND_REQUEST;

/**
 * Created by ofirmonis on 18/07/2017.
 */

public class UserListAdapter extends ArrayAdapter<User> {

    private static final String TAG = UserListAdapter.class.getSimpleName();

    private ArrayList<User> users = new ArrayList<>();
    private Map<String, Friend> mUserFriends;
    private static FirebaseStorage mStorage;
    private static StorageReference mStorageRef;
    private BroadcastReceiver mFriendRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_ADD_FRIEND_REQUEST:
                    break;
            }
        }
    };

    public UserListAdapter(Context context, ArrayList<User> users, Map<String, Friend> aUserFriends) {
        super(context, 0, users);
        this.users = users;
        this.mUserFriends = aUserFriends;
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_row, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            holder.userPhoto = (ImageView) convertView.findViewById(R.id.User_circle_photo);
            holder.AddAsFriend = (Button) convertView.findViewById(R.id.add_user_as_friend);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (users.get(position).getName() != null)
            holder.userName.setText(users.get(position).getName());
        if (users.get(position).getProfilePictureUri() != null) {
            mStorageRef = mStorage.getReference(users.get(position).getProfilePictureUri());
            Glide.with(getContext()).using(new FirebaseImageLoader()).load(mStorageRef).into(holder.userPhoto);
        }
        if (mUserFriends.containsKey(users.get(position).getUID())) {
            holder.AddAsFriend.setText("Already Friend");
            holder.AddAsFriend.setClickable(false);

        } else {
            if (1 == 5) {
            } else {
                holder.AddAsFriend.setText("Add");
                holder.AddAsFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("clicked", users.get(position).getName());
                        Friend friend = new Friend(users.get(position).getName(), users.get(position).getUID(), users.get(position).getProfilePictureUri());
                        //  AppController.getInstance().AddFriendRequest(friend);
                        Log.i(TAG, String.format("Sending friend request. Friend to add:[%s]", friend));
                        Intent intent = new Intent();
                        intent.setAction(ACTION_ADD_FRIEND_REQUEST);
                        intent.putExtra(IntentExtras.FRIEND, friend);
                        getContext().sendBroadcast(intent);

                    }
                });
            }
        }

        return convertView;
    }


    static class ViewHolder {
        ImageView userPhoto;
        TextView userName;
        Button AddAsFriend;

    }


}
