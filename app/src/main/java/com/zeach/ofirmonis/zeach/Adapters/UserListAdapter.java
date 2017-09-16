package com.zeach.ofirmonis.zeach.Adapters;

import android.content.Context;
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

import com.zeach.ofirmonis.zeach.AppSavedObjects;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Objects.ZeachUser;

import java.util.ArrayList;

/**
 * Created by ofirmonis on 18/07/2017.
 */

public class UserListAdapter extends ArrayAdapter <ZeachUser>{

    private ArrayList<ZeachUser> users = new ArrayList<>();
    private String UserId;

    public UserListAdapter(Context context, ArrayList<ZeachUser> users,String userId, FragmentActivity activity) {
        super(context,0, users);
        this.users = users;
        this.UserId = userId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null){

            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_row, parent, false);
            holder.userName = (TextView)convertView.findViewById(R.id.user_name);
            holder.userPhoto = (ImageView) convertView.findViewById(R.id.user_circle_photo);
            holder.AddAsFriend = (Button)convertView.findViewById(R.id.add_user_as_friend);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (users.get(position).getName() !=null)
            holder.userName.setText(users.get(position).getName());
        if (users.get(position).getProfilePictureUri() !=null)
            holder.userPhoto.setImageURI(Uri.parse(users.get(position).getProfilePictureUri()));
        if ((users.get(position).getFriendsList().containsKey(this.UserId))) {
            holder.AddAsFriend.setText("Already Friend");
            holder.AddAsFriend.setClickable(false);

        }
        else {
            holder.AddAsFriend.setText("Add");
            holder.AddAsFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("clicked",users.get(position).getName());
                    Friend friend = new Friend(users.get(position).getName(),users.get(position).getUID(),users.get(position).getProfilePictureUri());
                    AppSavedObjects.getInstance().AddFriendRequest(UserId,friend);

                }
            });
        }

        return convertView;
    }


    static class  ViewHolder{
        ImageView userPhoto;
        TextView userName;
        Button AddAsFriend;

    }


}
