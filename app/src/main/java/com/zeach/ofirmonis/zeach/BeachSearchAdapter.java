package com.zeach.ofirmonis.zeach;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ofirmonis on 08/09/2017.
 */

public class BeachSearchAdapter extends ArrayAdapter<Beach> {

    public BeachSearchAdapter(Context context) {
        super(context,0);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        return null;
    }
}
