package com.zeach.ofirmonis.zeach.Adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.FavoriteBeach;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Singletons.MapSingleton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ofirmonis on 21/10/2017.
 */

public class FavoriteBeachesAdapter extends ArrayAdapter<FavoriteBeach> {

    private static final String TAG = FavoriteBeachesAdapter.class.getSimpleName();
    private ArrayList<FavoriteBeach> mBeach;
    private static final String ACTION_REMOVE_FAVORITE_BEACH = "remove_favorite_beach";
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public FavoriteBeachesAdapter(Context context, ArrayList<FavoriteBeach> beaches) {
        super(context, 0, beaches);
        mBeach = beaches;
        if (mReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_REMOVE_FAVORITE_BEACH);
            context.registerReceiver(mReceiver, intentFilter);
        }
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
                Intent intent = new Intent();
                intent.setAction(ACTION_REMOVE_FAVORITE_BEACH);
                intent.putExtra("favorite_beach", mBeach.get(position).getmBeachKey());
                getContext().sendBroadcast(intent);
                MapSingleton.getInstance().getmUser().getFavoriteBeaches().remove(mBeach.get(position).getmBeachKey());
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
