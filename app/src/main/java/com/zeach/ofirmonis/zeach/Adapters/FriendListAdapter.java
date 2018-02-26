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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zeach.ofirmonis.zeach.Constants.IntentExtras;
import com.zeach.ofirmonis.zeach.Singletons.AppController;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Singletons.MapSingleton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_DELETE_FRIEND;

/**
 * Created by ofirmonis on 18/07/2017.
 */

public class FriendListAdapter extends ArrayAdapter<Friend> {
    private static final String TAG = FriendListAdapter.class.getSimpleName();
    private ArrayList<Friend> friends = new ArrayList<>();

    private static FirebaseStorage mStorage;
    private static StorageReference mStorageRef;
    private ViewHolder holder;

    private BroadcastReceiver adapterReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public FriendListAdapter(Context context, ArrayList<Friend> friends) {
        super(context, 0, friends);
        this.friends = friends;
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        if (adapterReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_DELETE_FRIEND);
            context.registerReceiver(adapterReceiver, intentFilter);
        }
        //
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
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "refreshing");
                mStorageRef.getBytes(512 * 512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bitmap = AppController.SetCircleMarkerIcon(bitmap);
                        bitmap = AppController.addBorderToCircularBitmap(bitmap, 5, Color.BLACK);
                        bitmap = AppController.addShadowToCircularBitmap(bitmap, 4, Color.LTGRAY);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                        holder.friendPhoto.setImageBitmap(smallMarker);
                    }
                });
                handler.postDelayed(this, 1000 * 60);
            }
        });
        holder.AddFriendUnfriend.setText("Unfriend");
        if (friends.get(position).getCurrentBeach() != null)
            holder.CurrentBeach.setText(friends.get(position).getCurrentBeach().getmBeachName());
        holder.AddFriendUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked", friends.get(position).getName());
                Intent intent = new Intent();
                intent.setAction(ACTION_DELETE_FRIEND);
                intent.putExtra(IntentExtras.UID, friends.get(position).getUID());
                getContext().sendBroadcast(intent);
                MapSingleton.getInstance().getmUser().getFriendsList().remove(friends.get(position).getUID());
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
