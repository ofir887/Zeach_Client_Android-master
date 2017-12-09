package com.zeach.ofirmonis.zeach.Adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ofirmonis on 18/07/2017.
 */

public class FriendListAdapter extends ArrayAdapter <Friend>{

    private ArrayList<Friend> friends = new ArrayList<>();

    private static final String ACTION_DELETE_FRIEND = "deleteFriend";
    private static final String ACTION_STRING_FRIENDS_LIST_ADAPTER = "ToFriendsListAdapter";
    private BroadcastReceiver adapterReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*switch (intent.getAction()) {
                case DELETE_FRIEND: {
                    Log.d(TAG, "lets see");
                    User user = (User) intent.getSerializableExtra("User");
                    zeachUser = user;
                    Log.d(TAG, user.toString());
                    setDrawerProfileInforamtion();
                    break;
                }

            }*/
        }
    };

    public FriendListAdapter(Context context, ArrayList<Friend> friends, FragmentActivity activity) {
        super(context,0, friends);
        this.friends = friends;
        if (adapterReceiver != null) {
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
            IntentFilter intentFilter = new IntentFilter(ACTION_DELETE_FRIEND);
            //  intentFilter.addAction(ACTION_DELETE_FRIEND);
            //Map the intent filter to the receiver
            context.registerReceiver(adapterReceiver, intentFilter);
        }
        //
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null){

            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_row, parent, false);
            holder.friendName = (TextView)convertView.findViewById(R.id.friend_name);
            holder.friendPhoto = (CircleImageView) convertView.findViewById(R.id.circle_photo);
            holder.AddFriendUnfriend = (Button)convertView.findViewById(R.id.add_friend_unfriend);
            holder.CurrentBeach = (TextView)convertView.findViewById(R.id.friend_current_beach);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.friendName.setText(friends.get(position).getName());
        new AppController.DownloadImageTask(holder.friendPhoto).execute(friends.get(position).getPhotoUrl().toString());
        holder.AddFriendUnfriend.setText("Unfriend");
        if (friends.get(position).getCurrentBeach() != null)
            holder.CurrentBeach.setText(friends.get(position).getCurrentBeach().getmBeachName());
        holder.AddFriendUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked",friends.get(position).getName());
                Intent intent = new Intent();
                intent.setAction(ACTION_DELETE_FRIEND);
                intent.putExtra("UID", friends.get(position).getUID());
                getContext().sendBroadcast(intent);
            }
        });
        return convertView;
    }


    static class  ViewHolder{
        ImageView friendPhoto;
        TextView friendName;
        Button AddFriendUnfriend;
        TextView CurrentBeach;
    }


}
