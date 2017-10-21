package com.zeach.ofirmonis.zeach.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeach.ofirmonis.zeach.AppSavedObjects;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ofirmonis on 21/10/2017.
 */

public class FavoriteBeachesAdapter extends ArrayAdapter<Beach> {

    private ArrayList<Beach> mBeach;

    public FavoriteBeachesAdapter(Context context, ArrayList<Beach> beaches) {
        super(context, 0, beaches);
        mBeach = beaches;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.beach_row, parent, false);
            holder.beachName = (TextView) convertView.findViewById(R.id.beach_name);
            holder.beachImage = (CircleImageView) convertView.findViewById(R.id.circle_beach_image);
            holder.button = (Button) convertView.findViewById(R.id.beachButton);
            holder.omes = (TextView) convertView.findViewById(R.id.beach_omes);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.beachName.setText(mBeach.get(position).getBeachName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView beachImage;
        TextView beachName;
        Button button;
        TextView omes;
    }
}
