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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Constants.BeachConstants;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.ZeachUser;
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

public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private static final String TAG = MapFragment.class.getSimpleName();
    private View rootView;
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private Button SearchButton;
    private AutoCompleteTextView autoCompleteSearch;
    private LocationManager locationManager;
    private LatLng userLocation;
    private BroadcastReceiver broadcastReceiver;
    private LatLng CurrentUserLocation;
    private ArrayList<Beach> mBeaches = new ArrayList<>();
    private ArrayList<Marker> mFriendsMarkers = new ArrayList<>();
    ///
    private LatLng currentLocation;
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
                    ZeachUser user = (ZeachUser) intent.getSerializableExtra("User");
                    Log.d(MapFragment.class.getSimpleName(), user.toString());
                    break;
                }
                case ACTION_STRING_ACTIVITY: {
                    Bundle b = intent.getExtras();
                    LatLng latLng = new LatLng(b.getDouble("lat"), b.getDouble("lng"));
                    Log.d(MapFragment.class.getSimpleName(), latLng.toString());
                    userLocation = latLng;
                    setUserLocationOnMap();
                    //  setMapLocation(latLng);
                    break;
                }
                case ACTION_BEACHES: {
                    mGoogleMap.clear();
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
                    if (userLocation != null)
                        setUserLocationOnMap();
                    break;
                }
            }
        }
    };

    public void showFriendsOnMap() {
        for (int i = 0; i < mBeaches.size(); i++) {
            for (int j = 0; j < mBeaches.get(i).getFriends().size(); j++) {

                final Friend friend = mBeaches.get(i).getFriends().get(j);
                final LatLng friendLocation = new LatLng(AppController.getInstance().getUser().getFriendsList().get(friend.getUID()).getCurrentBeach().getLatitude(),
                        AppController.getInstance().getUser().getFriendsList().get(friend.getUID()).getCurrentBeach().getLongitude());
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
                    /*Bitmap bmp = Ion.with(getApplicationContext()).load(friend.getPhotoUrl()).asBitmap().get();
                    bmp = AppController.SetCircleMarkerIcon(bmp);
                    bmp = AppController.addBorderToCircularBitmap(bmp, 5, Color.WHITE);
                    bmp = AppController.addShadowToCircularBitmap(bmp, 4, Color.LTGRAY);*/
                    /*Bitmap smallMarker = Bitmap.createScaledBitmap(bmp[0], 150, 150, true);
                    MarkerOptions marker = new MarkerOptions().position(friendLocation).
                            title(friend.getName()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    mFriendsMarkers.add(mGoogleMap.addMarker(marker));*/
                //   mGoogleMap.addMarker((new MarkerOptions().position(friendLocation).title(friend.getName()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))));

            }
        }
    }

    public void addBeachesAsPolygons() {
        for (int i = 0; i < mBeaches.size(); i++) {
            final Polygon mPolygon = mGoogleMap.addPolygon(new PolygonOptions().clickable(true).
                    addAll(mBeaches.get(i).getBeachCoordinates()).
                    fillColor(BeachConstants.getTrafficColorByString(mBeaches.get(i).getTraffic())));
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.map_fragment, container, false);
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("isActive", true);
        //  this.SearchButton = (Button) rootView.findViewById(R.id.beach_search_button);
        this.autoCompleteSearch = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteSearchTextView);
        //this.autoCompleteSearch = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteSearchTextView);
//        this.autoCompleteSearch.setOnClickListener(this);

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        //


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
        //  Intent intent = new Intent(getActivity(),StartService.class);
        //  getActivity().startService(intent);
        //Intent intent = new Intent(getActivity(), BackgroundService.class);
        // getActivity().startService(intent);

        Intent intent1 = new Intent(getActivity(), BackgroundService.class);
        //  getActivity().startService(intent1);


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
        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("isActive", false);
            //   getActivity().stopService(new Intent(getActivity(), BackgroundService.class));
        }
        //getActivity().stopService(new Intent(getActivity(), BackgroundService.class));

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
        try {
            Bitmap bmp = Ion.with(getApplicationContext()).load(AppController.getInstance().getUser().getProfilePictureUri().toString()).asBitmap().get();
            bmp = AppController.SetCircleMarkerIcon(bmp);
            bmp = AppController.addBorderToCircularBitmap(bmp, 5, Color.WHITE);
            bmp = AppController.addShadowToCircularBitmap(bmp, 4, Color.LTGRAY);
            Bitmap smallMarker = Bitmap.createScaledBitmap(bmp, 150, 150, true);
            mGoogleMap.addMarker((new MarkerOptions().position(this.userLocation).title(AppController.getInstance().getUser().getName()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))));
            setMapLocation(this.userLocation);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("location", location.toString());
        this.userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(this.userLocation).zoom(16).bearing(0).tilt(45).build()));


        try {
            Bitmap bmp = Ion.with(getApplicationContext()).load(AppController.getInstance().getUser().getProfilePictureUri().toString()).asBitmap().get();
            bmp = AppController.SetCircleMarkerIcon(bmp);
            bmp = AppController.addBorderToCircularBitmap(bmp, 5, Color.WHITE);
            bmp = AppController.addShadowToCircularBitmap(bmp, 4, Color.LTGRAY);
            mGoogleMap.addMarker((new MarkerOptions().position(this.userLocation).title(AppController.getInstance().getUser().getName()).icon(BitmapDescriptorFactory.fromBitmap(bmp))));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //mGoogleMap.addMarker((new MarkerOptions().position(this.userLocation).title("my location")));
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(i);
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

}