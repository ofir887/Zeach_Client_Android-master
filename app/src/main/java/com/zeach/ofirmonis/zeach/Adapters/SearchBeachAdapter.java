package com.zeach.ofirmonis.zeach.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.interfaces.OnMapActions;
import com.zeach.ofirmonis.zeach.R;

import java.util.ArrayList;


/**
 * Created by ofirmonis on 17/02/2018.
 */

public class SearchBeachAdapter extends ArrayAdapter<Beach> {

    private static final String TAG = SearchBeachAdapter.class.getSimpleName();
    private ArrayList<Beach> mBeaches = new ArrayList<>();
    private OnMapActions mMapActions;

    public SearchBeachAdapter(Context context) {
        super(context, 0);
    }

    public SearchBeachAdapter(Context context, ArrayList<Beach> aBeaches, OnMapActions aOnMapActions) {
        super(context, 0, aBeaches);
        mBeaches = aBeaches;
        mMapActions = aOnMapActions;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_beach_row, parent, false);
            holder.beachName = convertView.findViewById(R.id.beach_name);
            holder.beachCapacity = convertView.findViewById(R.id.beach_capacity);
            holder.moveToBeach = convertView.findViewById(R.id.beachButton);
            holder.beachCountry = convertView.findViewById(R.id.beach_country);
            //holder.beachCapacity = convertView.findVi
            convertView.setTag(holder);
            holder.beachName.setText(mBeaches.get(position).getBeachName());
            holder.beachCountry.setText(mBeaches.get(position).getCountry());
            holder.beachCapacity.setText(String.format("%s Capacity", mBeaches.get(position).getTraffic()));
            holder.moveToBeach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, String.format("Beach %s Clicked. focusing on beach", mBeaches.get(position).getBeachName()));
                    mMapActions.onMapLocationChange(mBeaches.get(position));
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }


    static class ViewHolder {
        TextView beachCapacity;
        TextView beachName;
        TextView beachCountry;
        Button moveToBeach;
    }


}
