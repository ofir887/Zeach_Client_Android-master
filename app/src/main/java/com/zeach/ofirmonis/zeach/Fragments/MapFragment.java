package com.zeach.ofirmonis.zeach.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zeach.ofirmonis.zeach.Adapters.SearchBeachAdapter;
import com.zeach.ofirmonis.zeach.Singletons.AppController;
import com.zeach.ofirmonis.zeach.Constants.BeachConstants;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.FavoriteBeach;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.Singletons.MapSingleton;
import com.zeach.ofirmonis.zeach.interfaces.OnMapActions;
import com.zeach.ofirmonis.zeach.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by ofirmonis on 31/05/2017.
 */

public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, View.OnClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, OnMapActions {

    private static final String TAG = MapFragment.class.getSimpleName();
    private View rootView;
    private static GoogleMap mGoogleMap;
    private MapView mMapView;
    private SearchView autoCompleteSearch;
    private ListView mSearchBeachListView;
    private SearchBeachAdapter mSearchBeachAdapter;
    private LatLng userLocation;
    private ArrayList<Beach> mBeaches = new ArrayList<>();
    private ArrayList<Marker> mFriendsMarkers = new ArrayList<>();
    private ArrayList<Polygon> mPolygons = new ArrayList<>();
    private HashMap<String, Polygon> mPolygons2 = new HashMap<>();
    private Marker mUserMarker;
    private User mUser;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private boolean mNearestBeachDialogShowed = false;
    private boolean mUserDetailesReceived;
    private boolean mBeachesDetailsReceived;
    private boolean mUserLocationRecieved;
    ///
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    private static final String ACTION_BEACHES = "Beaches";
    private static final String ACTION_USER = "User";
    private static final String ACTION_ADD_FAVORITE_BEACH = "add_favorite_beach";
    private static final String ACTION_NEAREST_BEACH = "nearest_beach";
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_USER: {
                    Log.d(MapFragment.class.getSimpleName(), "lets see");
                    User user = (User) intent.getSerializableExtra("User");
                    mUser = user;
                    Log.d(MapFragment.class.getSimpleName(), user.toString());
                    mUserDetailesReceived = true;
                    MapSingleton.getInstance().updateUser(mUser, mUserDetailesReceived);
                    setUserLocationOnMap();
                    break;
                }
                case ACTION_STRING_ACTIVITY: {
                    Bundle b = intent.getExtras();
                    LatLng latLng = new LatLng(b.getDouble("lat"), b.getDouble("lng"));
                    Log.d(MapFragment.class.getSimpleName(), latLng.toString());
                    userLocation = latLng;
                    mUserLocationRecieved = true;
                    MapSingleton.getInstance().updateUserLocation(userLocation, mUserLocationRecieved);
                    setUserLocationOnMap();
                    break;
                }
                case ACTION_BEACHES: {
                    Gson gson = new Gson();
                    String str = intent.getStringExtra("beaches");
                    Type type = new TypeToken<ArrayList<Beach>>() {
                    }.getType();
                    ArrayList<Beach> beaches = gson.fromJson(str, type);
                    mBeaches = beaches;
                    mBeachesDetailsReceived = true;
                    MapSingleton.getInstance().updateBeaches(mBeaches, mBeachesDetailsReceived);
                    Log.i(TAG, beaches.toString());
                    removePolygon();
                    addBeachesAsPolygons();
                    for (int i = 0; i < mFriendsMarkers.size(); i++) {
                        mFriendsMarkers.get(i).remove();
                    }
                    showFriendsOnMap();
                    setUserLocationOnMap();
                    break;
                }
                case ACTION_NEAREST_BEACH:
                    final String nearestBeach = intent.getStringExtra("nearest_beach");
                    Log.i(TAG, String.format("Received nearest beach id:[%s]", nearestBeach));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            popupNearestBeach(nearestBeach);
                        }
                    }, 1000 * 10);
            }
        }
    };

    public void showFriendsOnMap() {
        for (int i = 0; i < mBeaches.size(); i++) {
            for (int j = 0; j < mBeaches.get(i).getFriends().size(); j++) {
                final Friend friend = mBeaches.get(i).getFriends().get(j);
                final LatLng friendLocation = new LatLng(mBeaches.get(i).getFriends().get(j).
                        getCurrentBeach().getLatitude(), mBeaches.get(i).getFriends().get(j).
                        getCurrentBeach().getLongitude());
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mStorageRef = mStorage.getReference(friend.getPhotoUrl());
                        mStorageRef.getBytes(512 * 512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                bitmap = AppController.SetCircleMarkerIcon(bitmap);
                                bitmap = AppController.addBorderToCircularBitmap(bitmap, 5, Color.BLACK);
                                bitmap = AppController.addShadowToCircularBitmap(bitmap, 4, Color.LTGRAY);
                                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                                MarkerOptions marker = new MarkerOptions().position(friendLocation).
                                        title(friend.getName()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                mFriendsMarkers.add(mGoogleMap.addMarker(marker));
                            }
                        });
                    }
                });
            }
        }
    }

    public void removePolygon() {
        for (int i = 0; i < mPolygons.size(); i++) {
            mPolygons.get(i).remove();
        }
    }

    public void addBeachesAsPolygons() {
        removePolygon();
        mPolygons.clear();
        for (int i = 0; i < mBeaches.size(); i++) {
            int color = BeachConstants.getTrafficColorByString(mBeaches.get(i).getTraffic());
            final Polygon aPolygon = mGoogleMap.addPolygon(new PolygonOptions().clickable(true).
                    addAll(mBeaches.get(i).getBeachCoordinates()).
                    fillColor(color).
                    strokeColor(color).strokeWidth(0));
            mPolygons.add(aPolygon);
            mPolygons2.put(mBeaches.get(i).getBeachKey(), aPolygon);
            final int finalI = i;
            mGoogleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                @Override
                public void onPolygonClick(Polygon polygon) {
                    if (aPolygon.getId().equals(polygon.getId())) {
                        final Beach beach = mBeaches.get(finalI);
                        Log.d(TAG, mBeaches.get(finalI).getBeachName() + " beach pressed");
                        AlertDialog.Builder beachAlert = new AlertDialog.Builder(getContext());
                        beachAlert.setTitle(beach.getBeachName());
                        beachAlert.setMessage(String.format("Beach occupation Status: %s, Number Of Friends in the beach: %s",
                                beach.getTraffic(), beach.getFriends().size()));
                        if (!mUser.getFavoriteBeaches().containsKey(beach.getBeachKey())) {
                            beachAlert.setPositiveButton("Add To Favorite", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "Adding beach to favorite beach list");
                                    FavoriteBeach favoriteBeach = new FavoriteBeach(beach.getBeachKey(), beach.getBeachName(), beach.getCountry());
                                    Intent intent = new Intent();
                                    intent.setAction(ACTION_ADD_FAVORITE_BEACH);
                                    Gson gson = new Gson();
                                    String favorite = gson.toJson(favoriteBeach);
                                    intent.putExtra("favorite_beach", favorite);
                                    getContext().sendBroadcast(intent);
                                }
                            });
                        }
                        beachAlert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        beachAlert.show();
                    }
                }
            });
        }
    }

    public void popupNearestBeach(String aBeachId) {
        if (!mNearestBeachDialogShowed) {
            Beach nearestBeach = null;
            for (int i = 0; i < mBeaches.size(); i++) {
                if (mBeaches.get(i).getBeachKey().equals(aBeachId)) {
                    nearestBeach = mBeaches.get(i);
                    break;
                }
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Found nearest beach!");
            builder.setMessage(String.format("Nearset beach found:[%s]\nCapacity:[%s]\nNumber of friends in beach:[%d]", nearestBeach.getBeachName(), nearestBeach.getTraffic(), nearestBeach.getFriends().size()));
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            final Beach finalNearestBeach = nearestBeach;
            builder.setNeutralButton("See Beach", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setMapLocation(computeCentreBeach(finalNearestBeach.getBeachCoordinates()));
                }
            });
            AlertDialog nearestBeachDialog = builder.create();
            nearestBeachDialog.show();
            mNearestBeachDialogShowed = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //saveDataInPref();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.map_fragment, container, false);
        mUser = new User();
        this.autoCompleteSearch = rootView.findViewById(R.id.beach_search_widget);
        mSearchBeachListView = rootView.findViewById(R.id.map_beach_list);
        this.autoCompleteSearch.setOnQueryTextListener(this);
        autoCompleteSearch.setOnSearchClickListener(this);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        checkPermissions();


        return this.rootView;
    }


    @Override
    public void onResume() {

        super.onResume();
        if (MapSingleton.getInstance() == null) {
            MapSingleton.createInstance();
        } else {
            mBeaches = MapSingleton.getInstance().getmBeaches();
            mUser = MapSingleton.getInstance().getmUser();
            userLocation = MapSingleton.getInstance().getmUserLocation();
            mUserDetailesReceived = MapSingleton.getInstance().ismUserDetailesReceived();
            mBeachesDetailsReceived = MapSingleton.getInstance().ismBeachesDetailsReceived();
            mUserLocationRecieved = MapSingleton.getInstance().ismUserLocationRecieved();
            setUserLocationOnMap();
        }

        addBeachesAsPolygons();
        if (activityReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            intentFilter.addAction(ACTION_BEACHES);
            intentFilter.addAction(ACTION_USER);
            intentFilter.addAction(ACTION_NEAREST_BEACH);
            getActivity().registerReceiver(activityReceiver, intentFilter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) rootView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        mGoogleMap = googleMap;
        if (MapSingleton.getInstance() != null) {
            setUserLocationOnMap();
        }
        if (mBeaches != null) {
            showFriendsOnMap();
            addBeachesAsPolygons();
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        checkPermissions();

    }

    public void setMapLocation(LatLng location) {
        this.userLocation = location;
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(this.userLocation).zoom(16).bearing(0).tilt(45).build()));
    }

    public void setUserLocationOnMap() {
        if (mBeachesDetailsReceived && mUserLocationRecieved && mUserDetailesReceived) {
            if (mUser != null) {
                //  setMapLocation(userLocation);
                mStorageRef = mStorage.getReference(mUser.getProfilePictureUri());
                mStorageRef.getBytes(512 * 512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bitmap = AppController.SetCircleMarkerIcon(bitmap);
                        bitmap = AppController.addBorderToCircularBitmap(bitmap, 5, Color.WHITE);
                        bitmap = AppController.addShadowToCircularBitmap(bitmap, 4, Color.LTGRAY);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                        if (mUserMarker != null) {
                            mUserMarker.remove();
                        } else {
                            setMapLocation(userLocation);
                        }
                        MarkerOptions marker = new MarkerOptions().position(userLocation).
                                title(mUser.getName()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        mUserMarker = mGoogleMap.addMarker(marker);
                        setMapLocation(userLocation);
                    }
                });

            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == autoCompleteSearch) {
            Log.i(TAG, "Search button clicked. changing list view layout");
            autoCompleteSearch.setIconifiedByDefault(true);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSearchBeachListView.getLayoutParams();
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.addRule(RelativeLayout.BELOW, R.id.beach_search_widget);
            mSearchBeachListView.setLayoutParams(params);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                checkPermissions();
                break;
            default:
                break;
        }
    }

    public void checkPermissions() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onPause() {
        //    saveDataInPref();
        MapSingleton.getInstance().updateBeaches(mBeaches, mBeachesDetailsReceived);
        MapSingleton.getInstance().updateUserLocation(userLocation, mUserLocationRecieved);
        MapSingleton.getInstance().updateUser(mUser, mUserDetailesReceived);
        removePolygon();
        //    mBeaches.clear();
        getActivity().unregisterReceiver(activityReceiver);
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapLocationChange(Beach aBeach) {
        Log.i(TAG, "changing map focus");
        setMapLocation(computeCentreBeach(aBeach.getBeachCoordinates()));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSearchBeachListView.getLayoutParams();
        params.height = 0;
        params.width = 0;
        params.addRule(RelativeLayout.BELOW, R.id.beach_search_widget);
        mSearchBeachListView.setLayoutParams(params);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSearchBeachListView.getLayoutParams();
        params.height = 0;
        params.width = 0;
        params.addRule(RelativeLayout.BELOW, R.id.beach_search_widget);
        mSearchBeachListView.setLayoutParams(params);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<Beach> foundedBeaches = new ArrayList<>();
        mSearchBeachAdapter = new SearchBeachAdapter(getContext(), foundedBeaches, this);
        mSearchBeachAdapter.clear();
        mSearchBeachAdapter.notifyDataSetChanged();
        if (newText.isEmpty()) {
            Log.i(TAG, "empty string. clearing");
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSearchBeachListView.getLayoutParams();
            params.height = 0;
            params.width = 0;
            params.addRule(RelativeLayout.BELOW, R.id.beach_search_widget);
            mSearchBeachListView.setLayoutParams(params);
            mSearchBeachAdapter.clear();
            mSearchBeachAdapter.notifyDataSetChanged();
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSearchBeachListView.getLayoutParams();
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.addRule(RelativeLayout.BELOW, R.id.beach_search_widget);
            mSearchBeachListView.setLayoutParams(params);
            for (int i = 0; i < mBeaches.size(); i++) {
                if (mBeaches.get(i).getBeachName().toLowerCase().contains(newText)) {
                    foundedBeaches.add(mBeaches.get(i));
                }
            }
        }

        mSearchBeachListView.setAdapter(mSearchBeachAdapter);
        mSearchBeachAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onClose() {
        Log.i(TAG, "closed button pressed");
        return false;
    }

    private LatLng computeCentreBeach(ArrayList<LatLng> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }
        return new LatLng(latitude / n, longitude / n);
    }
}