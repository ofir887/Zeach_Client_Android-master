package com.zeach.ofirmonis.zeach.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.zeach.ofirmonis.zeach.Objects.FavoriteBeach;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.interfaces.BeachListener;

import java.util.ArrayList;


/**
 * Created by ofirmonis on 21/10/2017.
 */

public class FavoriteBeachesAdapter extends ArrayAdapter<FavoriteBeach> {

    private static final String TAG = FavoriteBeachesAdapter.class.getSimpleName();
    private ArrayList<FavoriteBeach> mBeach;
    private BeachListener mBeachListener;


    public FavoriteBeachesAdapter(Context context, ArrayList<FavoriteBeach> beaches, BeachListener aBeachListener) {
        super(context, 0, beaches);
        mBeach = beaches;
        mBeachListener = aBeachListener;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.beach_row, parent, false);
            holder.beachName = (TextView) convertView.findViewById(R.id.beach_name);
            holder.button = (Button) convertView.findViewById(R.id.beachButton);
            holder.beachCountry = (TextView) convertView.findViewById(R.id.beach_country);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.beachName.setText(mBeach.get(position).getmBeachName());
        holder.beachCountry.setText(mBeach.get(position).getmBeachCountry());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, String.format("Remove button clicked. removing favorite beach: %s", mBeach.get(position).getmBeachName()));
                mBeachListener.onBeachRemoved(mBeach.get(position).getmBeachKey());
            }
        });
        return convertView;


    }

    static class ViewHolder {
        TextView beachName;
        Button button;
        TextView beachCountry;
    }
}
