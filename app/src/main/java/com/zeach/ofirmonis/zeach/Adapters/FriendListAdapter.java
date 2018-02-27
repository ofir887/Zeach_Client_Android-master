package com.zeach.ofirmonis.zeach.Adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zeach.ofirmonis.zeach.Constants.IntentExtras;
import com.zeach.ofirmonis.zeach.Fragments.MapFragment;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.Singletons.AppController;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Singletons.MapSingleton;
import com.zeach.ofirmonis.zeach.interfaces.FriendsListener;

import java.lang.reflect.Type;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_BEACHES;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_DELETE_FRIEND;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_USER;

/**
 * Created by ofirmonis on 18/07/2017.
 */

public class FriendListAdapter extends ArrayAdapter<Friend> {
    private static final String TAG = FriendListAdapter.class.getSimpleName();
    private ArrayList<Friend> friends = new ArrayList<>();

    private static FirebaseStorage mStorage;
    private static StorageReference mStorageRef;
    private ViewHolder holder;
    private FriendsListener mFriendsListener;

    public FriendListAdapter(Context context, ArrayList<Friend> friends, FriendsListener aFriendsListener) {
        super(context, 0, friends);
        this.friends = friends;
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mFriendsListener = aFriendsListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_row, parent, false);
            holder.friendName = convertView.findViewById(R.id.friend_name);
            holder.friendPhoto = (CircleImageView) convertView.findViewById(R.id.circle_photo);
            holder.AddFriendUnfriend = convertView.findViewById(R.id.add_friend_unfriend);
            holder.CurrentBeach = convertView.findViewById(R.id.friend_current_beach);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.friendName.setText(friends.get(position).getName());
        mStorageRef = mStorage.getReference(friends.get(position).getPhotoUrl());
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(mStorageRef).into(holder.friendPhoto);
        holder.AddFriendUnfriend.setText("Unfriend");
        if (friends.get(position).getCurrentBeach() != null)
            holder.CurrentBeach.setText(friends.get(position).getCurrentBeach().getmBeachName());
        holder.AddFriendUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked", friends.get(position).getName());
                mFriendsListener.onFriendRemoved(friends.get(position).getUID());
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView friendPhoto;
        TextView friendName;
        Button AddFriendUnfriend;
        TextView CurrentBeach;
    }


}
