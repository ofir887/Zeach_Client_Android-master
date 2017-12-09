package com.zeach.ofirmonis.zeach.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Constants.BeachConstants;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Services.BackgroundService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by ofirmonis on 31/05/2017.
 */

public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = MapFragment.class.getSimpleName();
    private View rootView;
    private static GoogleMap mGoogleMap;
    private MapView mMapView;
    private Button SearchButton;
    private AutoCompleteTextView autoCompleteSearch;
    private LatLng userLocation;
    private LatLng CurrentUserLocation;
    private ArrayList<Beach> mBeaches = new ArrayList<>();
    private ArrayList<Marker> mFriendsMarkers = new ArrayList<>();
    private ArrayList<Polygon> mPolygons = new ArrayList<>();
    private Marker mUserMarker;
    private User mUser;
    ///
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    private static final String ACTION_BEACHES = "Beaches";
    private static final String ACTION_USER = "User";
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_USER: {
                    Log.d(MapFragment.class.getSimpleName(), "lets see");
                    User user = (User) intent.getSerializableExtra("User");
                    mUser = user;
                    Log.d(MapFragment.class.getSimpleName(), user.toString());
                    break;
                }
                case ACTION_STRING_ACTIVITY: {
                    Bundle b = intent.getExtras();
                    LatLng latLng = new LatLng(b.getDouble("lat"), b.getDouble("lng"));
                    Log.d(MapFragment.class.getSimpleName(), latLng.toString());
                    userLocation = latLng;
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
                    Log.d(MapFragment.class.getSimpleName(), beaches.toString());
                    addBeachesAsPolygons();
                    for (int i = 0; i < mFriendsMarkers.size(); i++) {
                        mFriendsMarkers.get(i).remove();
                    }
                    showFriendsOnMap();
/*                    if (userLocation != null)
                        setUserLocationOnMap();*/
                    break;
                }
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
                        Bitmap bmp = null;
                        try {

                            bmp = Ion.with(getApplicationContext()).load(friend.getPhotoUrl()).asBitmap().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        bmp = AppController.SetCircleMarkerIcon(bmp);
                        bmp = AppController.addBorderToCircularBitmap(bmp, 5, Color.WHITE);
                        bmp = AppController.addShadowToCircularBitmap(bmp, 4, Color.LTGRAY);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(bmp, 150, 150, true);
                        MarkerOptions marker = new MarkerOptions().position(friendLocation).
                                title(friend.getName()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        mFriendsMarkers.add(mGoogleMap.addMarker(marker));
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
            final Polygon mPolygon = mGoogleMap.addPolygon(new PolygonOptions().clickable(true).
                    addAll(mBeaches.get(i).getBeachCoordinates()).
                    fillColor(color).
                    strokeColor(color).strokeWidth(0));
            mPolygons.add(mPolygon);
            mPolygon.getId();
            final int finalI = i;
            mGoogleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                @Override
                public void onPolygonClick(Polygon polygon) {
                    if (mPolygon.getId().equals(polygon.getId())) {
                        Log.d(TAG, mBeaches.get(finalI).getBeachName() + " beach pressed");
                    }
                }
            });
        }
    }

    private void readDataFromPref() {
        String markers = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("markers", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Marker>>() {
        }.getType();
        if (!markers.isEmpty()) {
            mFriendsMarkers = new ArrayList<>();
            mFriendsMarkers = gson.fromJson(markers, type);
        } else
            mFriendsMarkers = new ArrayList<>();

    }

    private void saveDataInPref() {
        Gson gson = new Gson();
        String markers = gson.toJson(mFriendsMarkers);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("markers", markers);
        String userMarker = gson.toJson(mUserMarker);
        String polygons = gson.toJson(mPolygons);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.map_fragment, container, false);
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("isActive", true);
        readDataFromPref();
        //  this.SearchButton = (Button) rootView.findViewById(R.id.beach_search_button);
        this.autoCompleteSearch = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteSearchTextView);



        if (activityReceiver != null) {
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            intentFilter.addAction(ACTION_BEACHES);
            intentFilter.addAction(ACTION_USER);
            //Map the intent filter to the receiver
            getActivity().registerReceiver(activityReceiver, intentFilter);
        }

        //

        checkPermissions();


        return this.rootView;
    }

    private void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_SERVICE);
        getActivity().sendBroadcast(new_intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        /*
        if (broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    CurrentUserLocation = (LatLng) intent.getExtras().get("coordinates");
                    Log.d("gps ofir",CurrentUserLocation.toString());
                }
            };
        }
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter("location_update"));*/
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

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        checkPermissions();

    }

    public void setMapLocation(LatLng location) {
        this.userLocation = location;
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(this.userLocation).zoom(16).bearing(0).tilt(45).build()));
    }

    public void setUserLocationOnMap() {
        if (mUser != null) {
            try {
                Bitmap bmp = Ion.with(getApplicationContext()).load(mUser.getProfilePictureUri().toString()).asBitmap().get();
                bmp = AppController.SetCircleMarkerIcon(bmp);
                bmp = AppController.addBorderToCircularBitmap(bmp, 5, Color.WHITE);
                bmp = AppController.addShadowToCircularBitmap(bmp, 4, Color.LTGRAY);
                Bitmap smallMarker = Bitmap.createScaledBitmap(bmp, 150, 150, true);
                if (mUserMarker != null) {
                    mUserMarker.remove();
                } else {
                    setMapLocation(userLocation);
                }
                MarkerOptions marker = new MarkerOptions().position(userLocation).
                        title(mUser.getName()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                mUserMarker = mGoogleMap.addMarker(marker);
            /*mGoogleMap.addMarker((new MarkerOptions().position(this.userLocation).
                    title(AppController.getInstance().getUser().getName()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))));*/
                setMapLocation(this.userLocation);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == autoCompleteSearch) {
            this.autoCompleteSearch.setText("");
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
    public void onDetach() {
        super.onDetach();
    }
}