package com.zeach.ofirmonis.zeach;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null){

            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_row, parent, false);
            holder.friendName = (TextView)convertView.findViewById(R.id.friend_name);
            holder.friendPhoto = (ImageView) convertView.findViewById(R.id.circle_photo);
            holder.AddFriendUnfriend = (Button)convertView.findViewById(R.id.add_friend_unfriend);
            holder.CurrentBeach = (TextView)convertView.findViewById(R.id.friend_current_beach);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.friendName.setText(friends.get(position).getName());
        new AppSavedObjects.DownloadImageTask(holder.friendPhoto).execute(friends.get(position).getPhotoUrl().toString());
        holder.AddFriendUnfriend.setText("Unfriend");
        holder.CurrentBeach.setText(friends.get(position).getCurrentBeach());
        holder.AddFriendUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked",friends.get(position).getName());

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
