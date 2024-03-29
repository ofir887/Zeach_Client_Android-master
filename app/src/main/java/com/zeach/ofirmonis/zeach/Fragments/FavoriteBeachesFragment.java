package com.zeach.ofirmonis.zeach.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeach.ofirmonis.zeach.Adapters.FavoriteBeachesAdapter;
import com.zeach.ofirmonis.zeach.Constants.IntentExtras;
import com.zeach.ofirmonis.zeach.Singletons.AppController;
import com.zeach.ofirmonis.zeach.Objects.FavoriteBeach;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Singletons.MapSingleton;
import com.zeach.ofirmonis.zeach.interfaces.BeachListener;

import java.util.ArrayList;

import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_REMOVE_FAVORITE_BEACH;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.FAVORITE_BEACHES;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.USERS;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class FavoriteBeachesFragment extends Fragment implements BeachListener {
    private FavoriteBeachesAdapter favoriteBeachesAdapter;
    private static final String TAG = FavoriteBeachesFragment.class.getSimpleName();
    private ArrayList<FavoriteBeach> mFavoriteBeaches = new ArrayList<>();
    private ListView beachListView;
    private DatabaseReference data;
    private View rootView;
    private User mUser;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.favorite_beaches_fragment, container, false);
        this.beachListView = (ListView) rootView.findViewById(R.id.favorite_beach_list);
        mUser = AppController.getInstance().getUser();
        data = FirebaseDatabase.getInstance().getReference(String.format("%s/%s/%s", USERS, mUser.getUID(), FAVORITE_BEACHES));
        setFavoriteBeaches();
        return this.rootView;
    }


    public void setFavoriteBeaches() {
        favoriteBeachesAdapter = new FavoriteBeachesAdapter(getContext(), mFavoriteBeaches, this);
        this.data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favoriteBeachesAdapter.clear();
                favoriteBeachesAdapter.notifyDataSetChanged();
                for (DataSnapshot beach : dataSnapshot.getChildren()) {
                    final FavoriteBeach favoriteBeach = beach.getValue(FavoriteBeach.class);
                    mFavoriteBeaches.add(favoriteBeach);
                }
                beachListView.setAdapter(favoriteBeachesAdapter);
                favoriteBeachesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_REMOVE_FAVORITE_BEACH);
            getContext().registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    public void onBeachRemoved(String aBeachID) {
        Intent intent = new Intent();
        intent.setAction(ACTION_REMOVE_FAVORITE_BEACH);
        intent.putExtra(IntentExtras.FAVORITE_BEACH, aBeachID);
        getContext().sendBroadcast(intent);
        MapSingleton.getInstance().getmUser().getFavoriteBeaches().remove(aBeachID);
    }

    @Override
    public void onDetach() {
        getContext().unregisterReceiver(mReceiver);
        super.onDetach();
    }
}
