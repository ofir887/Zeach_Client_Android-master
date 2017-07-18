package com.zeach.ofirmonis.zeach;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ofirmonis on 18/07/2017.
 */

public class FriendListAdapter extends ArrayAdapter <Friend>{

    private ArrayList<Friend> friends = new ArrayList<>();

    public FriendListAdapter(Context context, ArrayList<Friend> friends, FragmentActivity activity) {
        super(context,0, friends);
        this.friends = friends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){

            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_row, parent, false);
            holder.friendName = (TextView)convertView.findViewById(R.id.friend_name);
            holder.friendPhoto = (ImageView) convertView.findViewById(R.id.circle_photo);
            holder.AddFriendUnfriend = (Button)convertView.findViewById(R.id.add_friend_unfriend);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.friendName.setText(friends.get(position).getName());
        holder.friendPhoto.setImageURI(Uri.parse(friends.get(position).getPhotoUrl()));
        holder.AddFriendUnfriend.setText("Unfriend");
        return convertView;
    }


    static class  ViewHolder{
        ImageView friendPhoto;
        TextView friendName;
        Button AddFriendUnfriend;
    }


}
